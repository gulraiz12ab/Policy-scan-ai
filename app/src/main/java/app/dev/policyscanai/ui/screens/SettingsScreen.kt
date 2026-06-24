package app.dev.policyscanai.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.R
import app.dev.policyscanai.data.local.ScanHistoryManager
import app.dev.policyscanai.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("policy_scan_settings", Context.MODE_PRIVATE)
    }
    var riskNotify by remember {
        mutableStateOf(prefs.getBoolean("risk_notify", true))
    }
    var autoSaveScans by remember {
        mutableStateOf(prefs.getBoolean("auto_save", true))
    }
    var showClearDialog by remember { mutableStateOf(false) }
    var showAboutSheet by remember { mutableStateOf(false) }

    val historyManager = remember { ScanHistoryManager(context) }

    BackHandler { onBack() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar("settings", onNavigate) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 52.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Settings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "App preferences & information",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp)
        ) {
            // SECTION — AI STATUS CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4DFF).copy(0.08f)),
                    border = BorderStroke(1.dp, Color(0xFF0A4DFF).copy(0.25f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF0A4DFF), Color(0xFF7B2EFF))
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "AI Engine",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0A4DFF)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = Color(0xFF10B981).copy(0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(Color(0xFF10B981), CircleShape)
                                            )
                                            Text(
                                                text = "Connected",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Policy Scan AI Cloud",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "High-precision analysis active",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // SECTION — PREFERENCES
            item {
                SectionLabel("Preferences")
                Spacer(modifier = Modifier.height(10.dp))

                SettingsCard {
                    SettingsToggle(
                        icon = Icons.Outlined.DarkMode,
                        iconColor = Color(0xFF7B2EFF),
                        title = "Dark Mode",
                        subtitle = "Switch between dark and light theme",
                        checked = isDarkMode,
                        onToggle = onToggleDarkMode
                    )

                    SettingsDivider()

                    SettingsToggle(
                        icon = Icons.Outlined.Save,
                        iconColor = Color(0xFF0A4DFF),
                        title = "Auto-Save Scans",
                        subtitle = "Automatically save scan results to history",
                        checked = autoSaveScans,
                        onToggle = {
                            autoSaveScans = it
                            prefs.edit().putBoolean("auto_save", it).apply()
                        }
                    )

                    SettingsDivider()

                    SettingsToggle(
                        icon = Icons.Outlined.Notifications,
                        iconColor = Color(0xFF10B981),
                        title = "Risk Alerts",
                        subtitle = "Show notification when critical issues found",
                        checked = riskNotify,
                        onToggle = {
                            riskNotify = it
                            prefs.edit().putBoolean("risk_notify", it).apply()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SECTION — SCAN STATS
            item {
                SectionLabel("Your Stats")
                Spacer(modifier = Modifier.height(10.dp))

                val allScans = remember { historyManager.getAllScans() }
                val totalIssues = allScans.sumOf { it.findingsCount }
                val critical = allScans.sumOf { it.redCount }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatBox("${allScans.size}", "Scans", Color(0xFF0A4DFF))
                        StatDivider()
                        StatBox("$totalIssues", "Issues", Color(0xFFF59E0B))
                        StatDivider()
                        StatBox("$critical", "Critical", Color(0xFFEF4444))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SECTION — DATA & PRIVACY
            item {
                SectionLabel("Data & Privacy")
                Spacer(modifier = Modifier.height(10.dp))

                SettingsCard {
                    SettingsItem(
                        icon = Icons.Outlined.Lock,
                        iconColor = Color(0xFF10B981),
                        title = "Data stays on device",
                        subtitle = "Documents never uploaded to any server",
                        showArrow = false,
                        badge = "Private"
                    )

                    SettingsDivider()

                    SettingsItem(
                        icon = Icons.Outlined.WifiOff,
                        iconColor = Color(0xFF0A4DFF),
                        title = "Works offline",
                        subtitle = "Laws database & analysis history fully offline",
                        showArrow = false,
                        badge = "Offline"
                    )

                    SettingsDivider()

                    SettingsItem(
                        icon = Icons.Outlined.DeleteSweep,
                        iconColor = Color(0xFFEF4444),
                        title = "Clear All History",
                        subtitle = "Delete all saved scan results",
                        showArrow = true,
                        onClick = { showClearDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SECTION — ABOUT
            item {
                SectionLabel("About")
                Spacer(modifier = Modifier.height(10.dp))

                SettingsCard {
                    SettingsItem(
                        icon = Icons.Outlined.Info,
                        iconColor = Color(0xFF7B2EFF),
                        title = "About Policy Scan AI",
                        subtitle = "Version 1.0.0",
                        showArrow = true,
                        onClick = { showAboutSheet = true }
                    )

                    SettingsDivider()

                    SettingsItem(
                        icon = Icons.Outlined.Gavel,
                        iconColor = Color(0xFF0A4DFF),
                        title = "Laws Reference",
                        subtitle = "View all supported laws & regulations",
                        showArrow = true,
                        onClick = { onNavigate("laws") }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFFEF4444)
                )
            },
            title = {
                Text(
                    text = "Clear All History?",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "All saved scans will be permanently deleted. This cannot be undone.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        historyManager.clearAll()
                        showClearDialog = false
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color(0xFF0A4DFF))
                }
            }
        )
    }

    if (showAboutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAboutSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                )
                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Policy Scan AI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Policy Scan AI aapke documents ko analyze kar ke dangerous clauses dhundta hai. Sab kuch aapke device par hota hai — koi data bahar nahi jata.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AboutChip(Icons.Outlined.Lock, "Private")
                    AboutChip(Icons.Outlined.WifiOff, "Offline")
                    AboutChip(Icons.Outlined.Android, "Android")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF7B2EFF)
                        )
                        Text(
                            text = "Powered by Policy Scan AI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "🛡️", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AIModelChip(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0A4DFF).copy(0.08f),
        border = BorderStroke(0.5.dp, Color(0xFF0A4DFF).copy(0.2f))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color(0xFF0A4DFF)
        )
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        color = Color(0xFF0A4DFF)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp)
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconColor)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0A4DFF),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    showArrow: Boolean = true,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showArrow || onClick != {}) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconColor)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (badge != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF10B981).copy(0.12f)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    color = Color(0xFF10B981)
                )
            }
        }
        if (showArrow && badge == null) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    )
}

@Composable
private fun AboutChip(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFF0A4DFF).copy(0.08f),
        border = BorderStroke(0.5.dp, Color(0xFF0A4DFF).copy(0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0A4DFF))
            Text(text = label, fontSize = 11.sp, color = Color(0xFF0A4DFF))
        }
    }
}
