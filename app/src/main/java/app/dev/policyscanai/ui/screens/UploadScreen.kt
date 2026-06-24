package app.dev.policyscanai.ui.screens

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.ui.components.DocumentPreviewCard
import app.dev.policyscanai.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

sealed class UploadState {
    data object Idle : UploadState()
    data object Processing : UploadState()
    data class Preview(
        val fileName: String,
        val fileType: String,
        val pageCount: Int,
        val fileSizeKb: Long,
        val thumbnailUri: Uri?
    ) : UploadState()
    data class Error(val message: String) : UploadState()
}

@Composable
fun UploadScreen(
    uploadState: UploadState = UploadState.Idle,
    initialUri: Uri? = null,
    onBack: () -> Unit,
    onStartScan: (policyType: String) -> Unit
) {
    var selectedPolicyType by remember { mutableStateOf("Privacy Policy") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 48.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Document Analysis",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                Box(modifier = Modifier.width(48.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (val state = uploadState) {
                    is UploadState.Idle -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Waiting for document selection...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    is UploadState.Processing -> ProcessingStateContent()
                    is UploadState.Preview -> PreviewStateContent(
                        state = state,
                        selectedType = selectedPolicyType,
                        onTypeSelected = { selectedPolicyType = it },
                        onRemove = onBack
                    )
                    is UploadState.Error -> ErrorStateContent(
                        message = state.message,
                        onRetry = onBack
                    )
                }
            }

            // Bottom Section: Start Scan Button
            if (uploadState is UploadState.Preview) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onStartScan(selectedPolicyType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DocumentScanner,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "Start AI Analysis",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Secure local scan enabled",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessingStateContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val infiniteTransition = rememberInfiniteTransition(label = "Processing")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "Rotation"
            )

            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation),
                    color = PrimaryBlue,
                    trackColor = PrimaryBlue.copy(alpha = 0.12f),
                    strokeWidth = 4.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF7B2EFF)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Processing Policy...",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Extracting critical clauses using AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PreviewStateContent(
    state: UploadState.Preview,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    onRemove: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        DocumentPreviewCard(
            fileName = state.fileName,
            fileType = state.fileType,
            pageCount = state.pageCount,
            fileSizeKb = state.fileSizeKb,
            thumbnailUri = state.thumbnailUri,
            onRemove = onRemove
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "POLICY TYPE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                val types = listOf(
                    "Privacy Policy", "Terms of Service",
                    "Insurance", "Loan Agreement",
                    "Rental Contract", "Employment", "Other"
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(types) { type ->
                        FilterChip(
                            selected = (selectedType == type),
                            onClick = { onTypeSelected(type) },
                            label = { Text(type, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.background,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorStateContent(message: String, onRetry: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF2D1515) else Color(0xFFFFEEEE)
    val textColor = if (isDark) Color(0xFFEF4444) else Color(0xFFB91C1C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = textColor
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Analysis Error",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Color(0xFFFF9999) else textColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onRetry) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Retry", tint = textColor)
            }
        }
    }
}
