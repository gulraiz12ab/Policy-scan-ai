package app.dev.policyscanai.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.policyscanai.data.local.Law
import app.dev.policyscanai.data.local.LawArticle
import app.dev.policyscanai.data.local.LawsDatabase
import app.dev.policyscanai.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawsScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    var region by remember { mutableStateOf("All") }
    var selectedLaw by remember { mutableStateOf<Law?>(null) }

    val filtered = remember(search, category, region) {
        LawsDatabase.ALL
            .let { list ->
                if (search.isNotEmpty())
                    LawsDatabase.search(search)
                else list
            }
            .filter { category == "All" || it.category == category }
            .filter { region == "All" || it.region == region }
            .sortedWith(compareBy(
                { if (it.region == "Pakistan") 0 else 1 },
                { it.shortName }
            ))
    }

    BackHandler {
        if (selectedLaw != null) selectedLaw = null
        else onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar("laws", onNavigate) },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 52.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Laws & Rights",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "100% offline · ${LawsDatabase.ALL.size} laws",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981).copy(0.1f),
                        border = BorderStroke(0.5.dp, Color(0xFF10B981).copy(0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.OfflineBolt,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF10B981)
                            )
                            Text(
                                text = "Offline",
                                fontSize = 11.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Search laws, region, rights...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = { search = "" }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LawsDatabase.REGIONS) { r ->
                        val color = when (r) {
                            "Pakistan" -> Color(0xFF10B981)
                            "European Union" -> Color(0xFF0A4DFF)
                            "United States" -> Color(0xFFF59E0B)
                            else -> Color(0xFF7B2EFF)
                        }
                        val flag = when (r) {
                            "Pakistan" -> "🇵🇰 "
                            "European Union" -> "🇪🇺 "
                            "United States" -> "🇺🇸 "
                            else -> ""
                        }
                        val selected = region == r
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = if (selected) color.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                if (selected) 1.dp else 0.5.dp,
                                if (selected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.clickable { region = r }
                        ) {
                            Text(
                                text = "$flag$r",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LawsDatabase.CATEGORIES) { c ->
                        val selected = category == c
                        val activeColor = Color(0xFF7B2EFF)
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = if (selected) activeColor.copy(0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                if (selected) 1.dp else 0.5.dp,
                                if (selected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.clickable { category = c }
                        ) {
                            Text(
                                text = c,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (selected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PAKISTAN SPOTLIGHT CARD
            if (region == "All" && search.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(0.08f)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "🇵🇰", fontSize = 36.sp)

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Pakistan Laws",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    Surface(
                                        color = Color(0xFFF59E0B).copy(0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "PDPA DRAFT",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "PECA 2016 aur CPA 2019 already enacted hain. PDPA 2023 draft stage mein hai lekin jab pass hoga toh strongest protection milegi.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    StatusPill("PECA 2016", true)
                                    StatusPill("CPA 2019", true)
                                    StatusPill("PDPA 2023", false)
                                }
                            }
                        }
                    }
                }
            }

            // LAW CARDS
            items(filtered, key = { it.id }) { law ->
                LawCard(law = law, onClick = { selectedLaw = law })
            }

            // EMPTY STATE
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.35f)
                            )
                            Text(
                                text = "No laws found",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                              )
                            Text(
                                text = "Try different filter",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = { search = ""; category = "All"; region = "All" }) {
                                Text("Clear All", color = Color(0xFF0A4DFF))
                            }
                        }
                    }
                }
            }
        }
    }

    // LAW DETAIL BOTTOM SHEET
    if (selectedLaw != null) {
        LawDetailSheet(law = selectedLaw!!, onDismiss = { selectedLaw = null })
    }
}

@Composable
fun StatusPill(label: String, enacted: Boolean) {
    val c = if (enacted) Color(0xFF10B981) else Color(0xFFF59E0B)
    Surface(
        shape = CircleShape,
        color = c.copy(0.12f),
        border = BorderStroke(0.5.dp, c.copy(0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(c, CircleShape)
            )
            Text(label, fontSize = 10.sp, color = c)
        }
    }
}

@Composable
fun LawCard(law: Law, onClick: () -> Unit) {
    val rc = when (law.region) {
        "Pakistan" -> Color(0xFF10B981)
        "European Union" -> Color(0xFF0A4DFF)
        "United States" -> Color(0xFFF59E0B)
        else -> Color(0xFF7B2EFF)
    }
    val sc = when (law.status) {
        "Enacted" -> Color(0xFF10B981)
        "Draft" -> Color(0xFFF59E0B)
        else -> Color(0xFF64748B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(44.dp)
                        .background(rc, RoundedCornerShape(50.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = law.flag, fontSize = 16.sp)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = rc.copy(0.12f)
                        ) {
                            Text(
                                text = law.shortName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = rc
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = sc.copy(0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(sc, CircleShape)
                                )
                                Text(law.status, fontSize = 9.sp, color = sc)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = law.fullName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(law.region, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("·", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${law.year}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = law.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(law.appliesTo) { type ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = type,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = rc
                    )
                    Text(
                        text = "${law.userRights.size} user rights",
                        fontSize = 11.sp,
                        color = rc
                    )
                }
                Text(
                    text = "${law.articles.size} key articles",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawDetailSheet(law: Law, onDismiss: () -> Unit) {
    val rc = when (law.region) {
        "Pakistan" -> Color(0xFF10B981)
        "European Union" -> Color(0xFF0A4DFF)
        "United States" -> Color(0xFFF59E0B)
        else -> Color(0xFF7B2EFF)
    }
    val sc = when (law.status) {
        "Enacted" -> Color(0xFF10B981)
        "Draft" -> Color(0xFFF59E0B)
        else -> Color(0xFF64748B)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 40.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = law.flag, fontSize = 36.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = law.shortName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = rc
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = sc.copy(0.12f)
                            ) {
                                Text(
                                    text = law.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = sc
                                )
                            }
                        }
                        Text(text = law.fullName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${law.region} · ${law.year}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = law.summary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = rc
                    )
                    Text(
                        text = "Your Rights Under ${law.shortName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = rc
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                law.userRights.forEachIndexed { i, right ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = rc.copy(0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${i + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rc
                                )
                            }
                        }
                        Text(
                            text = right,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Article,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = rc
                    )
                    Text(
                        text = "Key Articles",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = rc
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(law.articles) { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = if (article.protectsUser) BorderStroke(0.5.dp, rc.copy(0.3f)) else null
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = rc.copy(0.12f)
                            ) {
                                Text(
                                    text = article.number,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = rc
                                )
                            }
                            Text(
                                text = article.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (article.protectsUser) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = rc
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = article.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = rc
                    )
                    Text(
                        text = "Enforcement",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = rc
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = rc.copy(0.07f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Gavel, contentDescription = null, modifier = Modifier.size(16.dp), tint = rc)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Enforced By", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = rc)
                            Text(text = law.enforcedBy, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(0.07f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Penalties", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFFEF4444))
                            Text(text = law.penalty, fontSize = 12.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
