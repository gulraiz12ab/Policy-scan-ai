package app.dev.policyscanai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.R
import app.dev.policyscanai.ui.components.BottomNavBar
import app.dev.policyscanai.ui.theme.AppColors
import app.dev.policyscanai.ui.theme.PrimaryBlue

data class RecentScanData(
    val fileName: String,
    val policyType: String,
    val riskLevel: String,
    val findingsCount: Int,
    val timeAgo: String
)

private data class PolicyType(val title: String, val icon: ImageVector, val color: Color)

@Composable
fun HomeScreen(
    onPickFile: () -> Unit,
    onOpenCamera: () -> Unit,
    onNavigate: (String) -> Unit,
    recentScans: List<RecentScanData> = emptyList()
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "home", onNavigate = onNavigate)
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern Top Bar: Notifications only on right
            item {
                ModernTopBar()
            }

            // Modern Greeting
            item {
                ModernGreeting()
            }

            // Premium Hero Card
            item {
                ModernHeroCard(onPickFile = onPickFile, onOpenCamera = onOpenCamera)
            }

            // Categories Row
            item {
                ModernCategoriesSection()
            }

            // Recent Scans
            item {
                ModernRecentScansSection(recentScans, onNavigate)
            }
        }
    }
}

@Composable
private fun ModernTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    text = "Policy Scan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "AI CLARITY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        color = PrimaryBlue
                    )
                )
            }
        }

        // Only Notifications here
        Surface(
            onClick = { },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                // Red dot for new notifications
                Surface(
                    color = AppColors.riskRed,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface)
                ) {}
            }
        }
    }
}

@Composable
private fun ModernGreeting() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp
            )
        )
        Text(
            text = "Analyze policies with precision.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun ModernHeroCard(onPickFile: () -> Unit, onOpenCamera: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val iconBrush = AppColors.gradientBrush
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(iconBrush, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DocumentScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "New Scan",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Drop a PDF or capture a photo to begin AI analysis",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPickFile,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Outlined.FileOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Choose", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenCamera,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Scan", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ModernCategoriesSection() {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val categories = listOf(
            PolicyType("Privacy", Icons.Outlined.PrivacyTip, Color(0xFF0A4DFF)),
            PolicyType("Terms", Icons.Outlined.Gavel, Color(0xFF7B2EFF)),
            PolicyType("Health", Icons.Outlined.HealthAndSafety, Color(0xFF10B981)),
            PolicyType("Loan", Icons.Outlined.AccountBalance, Color(0xFFF59E0B)),
            PolicyType("Rental", Icons.Outlined.Apartment, Color(0xFFEF4444)),
            PolicyType("Work", Icons.Outlined.Work, Color(0xFFA855F7))
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(categories) { cat ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(cat.title, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun ModernRecentScansSection(recentScans: List<RecentScanData>, onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { onNavigate("history") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recentScans.isEmpty()) {
            EmptyState()
        } else {
            recentScans.forEach { scan ->
                ModernScanCard(scan)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ModernScanCard(scan: RecentScanData) {
    val statusColor = when (scan.riskLevel.uppercase()) {
        "RED" -> AppColors.riskRed
        "YELLOW" -> AppColors.riskYellow
        "ORANGE" -> AppColors.riskOrange
        "GREEN" -> AppColors.riskGreen
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        onClick = { },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.fileName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${scan.policyType} • ${scan.timeAgo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${scan.findingsCount} Issues",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Inbox, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text("No scans yet", style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(showBackground = true)
@Composable
fun ModernHomePreview() {
    val mockScans = listOf(
        RecentScanData("Privacy_Policy.pdf", "Privacy", "RED", 12, "Just now"),
        RecentScanData("TOS_Agreement.docx", "Terms", "YELLOW", 4, "2h ago")
    )
    HomeScreen(
        onPickFile = {},
        onOpenCamera = {},
        onNavigate = {},
        recentScans = mockScans
    )
}
