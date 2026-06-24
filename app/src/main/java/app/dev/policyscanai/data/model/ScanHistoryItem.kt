package app.dev.policyscanai.data.model

data class ScanHistoryItem(
    val id: String,
    val fileName: String,
    val policyType: String,
    val overallRisk: String,     // "RED"|"ORANGE"|"YELLOW"|"GREEN"
    val findingsCount: Int,
    val redCount: Int,
    val orangeCount: Int,
    val yellowCount: Int,
    val pageCount: Int,
    val fileSizeKb: Long,
    val scannedAtMillis: Long,
    val scannedAtLabel: String,   // "Today 2:30 PM" | "Yesterday" etc
    val findings: List<ScanFinding> = emptyList(),
    val summary: String = ""
) {
    companion object {
        val SAMPLE_LIST = listOf(
            ScanHistoryItem(
                id = "scan_001",
                fileName = "WhatsApp_Privacy_Policy.pdf",
                policyType = "Privacy Policy",
                overallRisk = "RED",
                findingsCount = 5,
                redCount = 2, orangeCount = 2, yellowCount = 1,
                pageCount = 8, fileSizeKb = 420,
                scannedAtMillis = System.currentTimeMillis(),
                scannedAtLabel = "Today, 2:30 PM",
                findings = listOf(
                    ScanFinding("f1", "Data Sharing with Meta", "Your data is shared with Meta for advertising purposes.", "RED", "Privacy", 1),
                    ScanFinding("f2", "Location Tracking", "Continuous background location tracking is active.", "RED", "Privacy", 2)
                ),
                summary = "Critical privacy concerns regarding data sharing and location tracking."
            ),
            ScanHistoryItem(
                id = "scan_002",
                fileName = "Uber_Terms_of_Service.pdf",
                policyType = "Terms of Service",
                overallRisk = "ORANGE",
                findingsCount = 3,
                redCount = 0, orangeCount = 2, yellowCount = 1,
                pageCount = 12, fileSizeKb = 680,
                scannedAtMillis = System.currentTimeMillis() - 86400000,
                scannedAtLabel = "Yesterday, 6:15 PM"
            ),
            ScanHistoryItem(
                id = "scan_003",
                fileName = "House_Rental_Agreement.jpg",
                policyType = "Rental Contract",
                overallRisk = "RED",
                findingsCount = 7,
                redCount = 4, orangeCount = 2, yellowCount = 1,
                pageCount = 3, fileSizeKb = 210,
                scannedAtMillis = System.currentTimeMillis() - 172800000,
                scannedAtLabel = "2 days ago"
            ),
            ScanHistoryItem(
                id = "scan_004",
                fileName = "Netflix_Subscription.pdf",
                policyType = "Terms of Service",
                overallRisk = "YELLOW",
                findingsCount = 2,
                redCount = 0, orangeCount = 0, yellowCount = 2,
                pageCount = 6, fileSizeKb = 310,
                scannedAtMillis = System.currentTimeMillis() - 604800000,
                scannedAtLabel = "Last week"
            ),
            ScanHistoryItem(
                id = "scan_005",
                fileName = "Bank_Loan_Agreement.pdf",
                policyType = "Loan Agreement",
                overallRisk = "RED",
                findingsCount = 9,
                redCount = 5, orangeCount = 3, yellowCount = 1,
                pageCount = 18, fileSizeKb = 890,
                scannedAtMillis = System.currentTimeMillis() - 1209600000,
                scannedAtLabel = "2 weeks ago"
            )
        )
    }
}
