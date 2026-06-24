package app.dev.policyscanai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.ui.screens.ScanningStep

@Composable
fun ScanProgressCard(
    step: ScanningStep,
    progress: Float,
    extractedPages: Int,
    totalChunks: Int,
    currentChunk: Int,
    elapsedSeconds: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Animated AI Icon Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                val pulseAnim by infiniteTransition.animateFloat(
                    initialValue = 0.92f,
                    targetValue = 1.08f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseScale"
                )

                if (step !is ScanningStep.Complete) {
                    Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                        // Outer pulse ring
                        Box(
                            modifier = Modifier
                                .size(88.dp * pulseAnim)
                                .border(
                                    BorderStroke(1.5.dp, Color(0xFF0A4DFF).copy(alpha = 0.25f)),
                                    CircleShape
                                )
                        )

                        // Inner gradient circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF0A4DFF), Color(0xFF7B2EFF))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getStepLabel(step),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0A4DFF)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(600, easing = LinearOutSlowInEasing),
                label = "Progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(50.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF0A4DFF), Color(0xFF7B2EFF))),
                            RoundedCornerShape(50.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Indicators Row
            StepIndicators(step)

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "Pages", value = "$extractedPages")
                StatDivider()
                StatItem(label = "Chunks", value = "$totalChunks")
                StatDivider()
                StatItem(label = "Analyzed", value = "$currentChunk")
                StatDivider()
                StatItem(label = "Time", value = "${elapsedSeconds}s")
            }
        }
    }
}

@Composable
private fun StepIndicators(currentStep: ScanningStep) {
    val steps = listOf("Extract", "Detect", "Chunk", "Analyze", "Summary")
    val currentStepIndex = when (currentStep) {
        ScanningStep.ExtractingText -> 0
        ScanningStep.DetectingType -> 1
        ScanningStep.ChunkingText -> 2
        is ScanningStep.AnalyzingChunk -> 3
        ScanningStep.BuildingSummary -> 4
        ScanningStep.Complete -> 5
        is ScanningStep.Error -> 0 // Treat error as start or just handle separately
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = when {
                                index < currentStepIndex -> Color(0xFF10B981)
                                index == currentStepIndex -> Color(0xFF0A4DFF)
                                else -> MaterialTheme.colorScheme.background
                            },
                            shape = CircleShape
                        )
                        .then(
                            if (index > currentStepIndex) {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < currentStepIndex) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (index == currentStepIndex) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = if (index <= currentStepIndex) Color(0xFF0A4DFF) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .padding(bottom = 12.dp) // Adjusted for alignment with circle centers
                        .background(if (index < currentStepIndex) Color(0xFF10B981) else MaterialTheme.colorScheme.outline)
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    )
}

private fun getStepLabel(step: ScanningStep): String {
    return when (step) {
        ScanningStep.ExtractingText -> "Extracting Text"
        ScanningStep.DetectingType -> "Detecting Policy Type"
        ScanningStep.ChunkingText -> "Splitting into Chunks"
        is ScanningStep.AnalyzingChunk -> "Analyzing Chunk ${step.current}/${step.total}"
        ScanningStep.BuildingSummary -> "Building Summary"
        ScanningStep.Complete -> "Scan Complete"
        is ScanningStep.Error -> "Scan Failed"
    }
}
