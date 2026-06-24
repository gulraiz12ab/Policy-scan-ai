package app.dev.policyscanai.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class ScanFinding(
    val id: String,
    val title: String,
    val description: String,
    val riskLevel: String, // "RED" | "ORANGE" | "YELLOW"
    val category: String,  // e.g., "Data Privacy", "User Rights"
    val pageNumber: Int,
    val textSnippet: String? = null
)
