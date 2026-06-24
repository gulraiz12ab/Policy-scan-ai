package app.dev.policyscanai.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.data.local.ScanHistoryManager
import app.dev.policyscanai.data.model.ScanHistoryItem
import app.dev.policyscanai.ui.components.BottomNavBar
import app.dev.policyscanai.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigate: (String) -> Unit,
    onOpenScan: (scanId: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedRiskFilter by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Newest") }
    var scanToDelete by remember { mutableStateOf<ScanHistoryItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val historyManager = remember {
        ScanHistoryManager(context)
    }
    var allScans by remember {
        mutableStateOf(historyManager.getAllScans())
    }

    val filteredScans = remember(
        searchQuery, selectedRiskFilter, sortBy, allScans
    ) {
        allScans
            .filter { scan ->
                (searchQuery.isEmpty() ||
                        scan.fileName.contains(searchQuery, ignoreCase = true) ||
                        scan.policyType.contains(searchQuery, ignoreCase = true))
                        &&
                        (selectedRiskFilter == "All" ||
                                scan.overallRisk == selectedRiskFilter)
            }
            .sortedByDescending {
                if (sortBy == "Newest") it.scannedAtMillis else it.findingsCount.toLong()
            }
    }

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                currentRoute = "history",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // HEADER SECTION
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 52.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Scan History",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${allScans.size} documents scanned",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Sort button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                            onClick = {
                                sortBy = if (sortBy == "Newest") "Riskiest" else "Newest"
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Sort,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = sortBy,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SEARCH BAR
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "Search scan history...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // FILTER CHIPS ROW
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val riskFilters = listOf(
                            "All" to "All",
                            "Critical" to "RED",
                            "Warning" to "ORANGE",
                            "Caution" to "YELLOW"
                        )

                        items(riskFilters) { pair ->
                            val label = pair.first
                            val riskCode = pair.second
                            val isSelected = selectedRiskFilter == riskCode
                            val chipColor = when (riskCode) {
                                "RED"    -> Color(0xFFEF4444)
                                "ORANGE" -> Color(0xFFF59E0B)
                                "YELLOW" -> Color(0xFFEAB308)
                                else     -> PrimaryBlue
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRiskFilter = riskCode },
                                label = { Text(label) },
                                leadingIcon = if (riskCode != "All") {
                                    {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(chipColor, CircleShape)
                                        )
                                    }
                                } else null,
                                shape = RoundedCornerShape(50.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor.copy(alpha = 0.15f),
                                    selectedLabelColor = chipColor,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    selectedBorderColor = chipColor
                                )
                            )
                        }
                    }
                }
            }

            // OVERVIEW CARD
            if (selectedRiskFilter == "All" && searchQuery.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val totalFindings = allScans.sumOf { it.findingsCount }
                                val totalRed = allScans.sumOf { it.redCount }

                                StatBox(value = "${allScans.size}", label = "Total Scans", color = PrimaryBlue)
                                StatBox(value = "$totalFindings", label = "Issues Found", color = Color(0xFFF59E0B))
                                StatBox(value = "$totalRed", label = "Critical", color = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }

            // EMPTY STATE
            if (filteredScans.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FindInPage,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No results found" else "No scans yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (searchQuery.isEmpty()) {
                            Button(onClick = { onNavigate("home") }) {
                                Text("Scan First Document")
                            }
                        }
                    }
                }
            } else {
                // HISTORY ITEMS
                items(filteredScans, key = { it.id }) { scan ->
                    HistoryItemCard(
                        scan = scan,
                        onOpen = { onOpenScan(scan.id) },
                        onDeleteRequest = {
                            scanToDelete = scan
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // DELETE DIALOG
    if (showDeleteDialog && scanToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Delete Scan history?") },
            text = { Text("Are you sure you want to delete \"${scanToDelete?.fileName}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        allScans = historyManager.deleteScan(scanToDelete!!.id)
                        showDeleteDialog = false
                        scanToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    scan: ScanHistoryItem,
    onOpen: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    val riskColor = when (scan.overallRisk) {
        "RED"    -> Color(0xFFEF4444)
        "ORANGE" -> Color(0xFFF59E0B)
        "YELLOW" -> Color(0xFFEAB308)
        else     -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(riskColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (scan.fileName.endsWith(".pdf")) Icons.Outlined.Description else Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = riskColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scan.fileName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${scan.policyType} • ${scan.pageCount} pages",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = riskColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = scan.overallRisk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = riskColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress representation
            val total = (scan.redCount + scan.orangeCount + scan.yellowCount).toFloat().coerceAtLeast(1f)
            Row(
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                if (scan.redCount > 0) Box(Modifier.weight(scan.redCount / total).fillMaxHeight().background(Color(0xFFEF4444)))
                if (scan.orangeCount > 0) Box(Modifier.weight(scan.orangeCount / total).fillMaxHeight().background(Color(0xFFF59E0B)))
                if (scan.yellowCount > 0) Box(Modifier.weight(scan.yellowCount / total).fillMaxHeight().background(Color(0xFFEAB308)))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = scan.scannedAtLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "View Details", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    HistoryScreen(onNavigate = {}, onOpenScan = {}, onBack = {})
}
