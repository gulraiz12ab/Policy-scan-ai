package app.dev.policyscanai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.domain.ScanEvent
import app.dev.policyscanai.domain.ScanOrchestrator
import app.dev.policyscanai.domain.ScanReport
import app.dev.policyscanai.ui.components.LiveFindingChip
import app.dev.policyscanai.ui.components.ScanProgressCard
import app.dev.policyscanai.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

sealed class ScanningStep {
    data object ExtractingText : ScanningStep()
    data object DetectingType : ScanningStep()
    data object ChunkingText : ScanningStep()
    data class AnalyzingChunk(val current: Int, val total: Int) : ScanningStep()
    data object BuildingSummary : ScanningStep()
    data object Complete : ScanningStep()
    data class Error(val message: String) : ScanningStep()
}

@Immutable
data class LiveFinding(
    val title: String,
    val riskLevel: String, // "RED" | "ORANGE" | "YELLOW"
    val pageNumber: Int,
    val chunkIndex: Int
)

@Composable
fun ScanningScreen(
    fileName: String,
    policyType: String,
    pageTexts: Map<Int, String>,
    onScanComplete: (ScanReport) -> Unit,
    onCancelScan: () -> Unit
) {
    val orchestrator = remember { ScanOrchestrator() }
    
    var currentStep by remember { mutableStateOf<ScanningStep>(ScanningStep.ExtractingText) }
    var progress by remember { mutableFloatStateOf(0f) }
    val liveFindings = remember { mutableStateListOf<LiveFinding>() }
    var totalChunks by remember { mutableIntStateOf(0) }
    var currentChunk by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var canCancel by remember { mutableStateOf(true) }
    var stepLabel by remember { mutableStateOf("Initializing AI...") }

    // Timer Logic
    LaunchedEffect(currentStep) {
        if (currentStep !is ScanningStep.Complete && currentStep !is ScanningStep.Error) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    LaunchedEffect(Unit) {
        orchestrator
            .scan(
                pageTexts  = pageTexts,
                fileName   = fileName,
                policyType = policyType
            )
            .collect { event ->
                when (event) {
                    is ScanEvent.Step -> {
                        stepLabel = event.label
                        progress  = event.progress
                        
                        if (event.label.contains("Preparing chunks", true)) {
                            currentStep = ScanningStep.ChunkingText
                        } else if (event.label.contains("Building final summary", true)) {
                            currentStep = ScanningStep.BuildingSummary
                        } else if (event.label.contains("Finalizing report", true)) {
                            currentStep = ScanningStep.BuildingSummary
                        }
                    }

                    is ScanEvent.ChunkDone -> {
                        currentChunk = event.current
                        totalChunks  = event.total
                        progress     = event.progress
                        currentStep = ScanningStep.AnalyzingChunk(event.current, event.total)
                        
                        event.newFindings.forEach { f ->
                            liveFindings.add(0, LiveFinding(
                                title     = f.title,
                                riskLevel = f.riskLevel,
                                pageNumber= f.page,
                                chunkIndex= f.chunk
                            ))
                        }
                    }

                    is ScanEvent.Complete -> {
                        progress     = 1f
                        canCancel    = false
                        currentStep  = ScanningStep.Complete
                        delay(500) // Small delay for UX
                        onScanComplete(event.report)
                    }

                    is ScanEvent.Error -> {
                        currentStep  = ScanningStep.Error(event.message)
                    }
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 52.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Scanning Document",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = getStepStatus(currentStep, stepLabel),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (canCancel) {
                    TextButton(onClick = onCancelScan) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 13.sp,
                                color = Color(0xFFEF4444)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScanProgressCard(
                step = if (currentStep is ScanningStep.Error) ScanningStep.ExtractingText else currentStep,
                progress = progress,
                extractedPages = pageTexts.size,
                totalChunks = totalChunks,
                currentChunk = currentChunk,
                elapsedSeconds = elapsedSeconds
            )

            if (currentStep is ScanningStep.Error) {
                ErrorCard(message = (currentStep as ScanningStep.Error).message, onBack = onCancelScan)
            }

            if (totalChunks > 0 && currentStep !is ScanningStep.Error) {
                ChunkReportCard(
                    currentChunk = currentChunk,
                    totalChunks = totalChunks,
                    liveFindings = liveFindings,
                    stepLabel = stepLabel
                )
            }

            if (currentStep is ScanningStep.AnalyzingChunk) {
                val step = currentStep as ScanningStep.AnalyzingChunk
                CurrentChunkCard(current = step.current, total = step.total)
            }

            LiveFindingsSection(liveFindings)
        }
    }
}

@Composable
private fun ErrorCard(message: String, onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1515))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scan Failed",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Color(0xFFFF9999)
                )
            }
            TextButton(onClick = onBack) {
                Text("Go Back", color = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun ChunkReportCard(
    currentChunk: Int,
    totalChunks: Int,
    liveFindings: List<LiveFinding>,
    stepLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Chunk Report",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChunkStat("Chunks", "$currentChunk / $totalChunks", Color(0xFF0A4DFF))
                ChunkStat("Critical", "${liveFindings.count { it.riskLevel == "RED" }}", Color(0xFFEF4444))
                ChunkStat("Warning", "${liveFindings.count { it.riskLevel == "ORANGE" }}", Color(0xFFF59E0B))
                ChunkStat("Caution", "${liveFindings.count { it.riskLevel == "YELLOW" }}", Color(0xFFEAB308))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "DotPulse")
                val dotAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
                    label = "DotAlpha"
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color(0xFF0A4DFF).copy(alpha = dotAlpha), CircleShape)
                )
                Text(
                    text = stepLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChunkStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrentChunkCard(current: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "DotPulse")
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
                label = "DotAlpha"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(PrimaryBlue.copy(alpha = dotAlpha), CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Policy Scan AI Analysis",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Analyzing Segment $current of $total",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "AI ACTIVE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = PrimaryBlue
                    )
                )
            }
        }
    }
}

@Composable
private fun LiveFindingsSection(liveFindings: List<LiveFinding>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Live AI Detection",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (liveFindings.isNotEmpty()) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFEF4444).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${liveFindings.size} flagged",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (liveFindings.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "ScanIcon")
                val scanAnim by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(1500)),
                    label = "ScanAlpha"
                )
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = PrimaryBlue.copy(alpha = 0.4f + scanAnim * 0.6f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI is looking for hidden risks...",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(liveFindings, key = { index, finding -> "${finding.chunkIndex}_${index}_${finding.title}" }) { _, finding ->
                LiveFindingChip(finding = finding, isNew = true)
            }
        }
    }
}

private fun getStepStatus(step: ScanningStep, label: String): String {
    return when (step) {
        ScanningStep.ExtractingText -> "Extracting text with ML Kit..."
        ScanningStep.DetectingType -> "Detecting policy type..."
        ScanningStep.ChunkingText -> "Chunking text for AI..."
        is ScanningStep.AnalyzingChunk -> label
        ScanningStep.BuildingSummary -> "Summarizing findings..."
        ScanningStep.Complete -> "Analysis complete!"
        is ScanningStep.Error -> "Error encountered"
    }
}

@Preview(showBackground = true)
@Composable
fun ScanningScreenPreview() {
    ScanningScreen(
        fileName = "Sample_Policy.pdf",
        policyType = "Privacy Policy",
        pageTexts = mapOf(1 to "Sample text"),
        onScanComplete = {},
        onCancelScan = {}
    )
}
