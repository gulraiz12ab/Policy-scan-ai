package app.dev.policyscanai.data.local

data class Law(
    val id: String,
    val shortName: String,
    val fullName: String,
    val region: String,
    val flag: String,
    val category: String,
    val status: String,
    val year: Int,
    val summary: String,
    val userRights: List<String>,
    val articles: List<LawArticle>,
    val appliesTo: List<String>,
    val enforcedBy: String,
    val penalty: String
)

data class LawArticle(
    val number: String,
    val title: String,
    val description: String,
    val protectsUser: Boolean
)

object LawsDatabase {

    val ALL = listOf(

        Law(
            id = "gdpr",
            shortName = "GDPR",
            fullName = "General Data Protection Regulation",
            region = "European Union",
            flag = "🇪🇺",
            category = "Data Privacy",
            status = "Enacted",
            year = 2018,
            summary = "World's strongest data protection law. Gives EU citizens full control over their personal data and forces companies to be transparent.",
            userRights = listOf(
                "Right to access your personal data",
                "Right to erase your data (Right to be Forgotten)",
                "Right to data portability",
                "Right to object to data processing",
                "Right to be informed about data breaches",
                "Right to withdraw consent at any time"
            ),
            articles = listOf(
                LawArticle("Article 6", "Lawful Processing",
                    "Data can only be processed with valid legal basis such as explicit consent.", true),
                LawArticle("Article 7", "Consent Conditions",
                    "Consent must be freely given, specific, informed and unambiguous. Pre-ticked boxes are invalid.", true),
                LawArticle("Article 17", "Right to Erasure",
                    "Users can demand complete deletion of their data without undue delay.", true),
                LawArticle("Article 20", "Data Portability",
                    "Users can request data in machine-readable format to transfer elsewhere.", true),
                LawArticle("Article 83", "Fines",
                    "Up to €20 million or 4% of global annual turnover.", false)
            ),
            appliesTo = listOf("Privacy Policy","Terms of Service","Cookie Policy"),
            enforcedBy = "Data Protection Authorities (DPA) in each EU member state",
            penalty = "Up to €20 million or 4% global revenue"
        ),

        Law(
            id = "pdpa_pk",
            shortName = "PDPA 2023",
            fullName = "Pakistan Personal Data Protection Act 2023",
            region = "Pakistan",
            flag = "🇵🇰",
            category = "Data Privacy",
            status = "Draft",
            year = 2023,
            summary = "Pakistan's first dedicated data protection law. Currently in draft stage. Modeled after GDPR to give Pakistani citizens rights over their personal data.",
            userRights = listOf(
                "Right to know what data is collected",
                "Right to access personal data held by companies",
                "Right to correct inaccurate data",
                "Right to request data deletion",
                "Right to object to automated decisions",
                "Right to data portability"
            ),
            articles = listOf(
                LawArticle("Section 3", "Scope",
                    "Applies to all processors handling Pakistani citizens data including foreign companies.", false),
                LawArticle("Section 4", "Lawful Processing",
                    "Personal data can only be processed with user consent or lawful basis.", true),
                LawArticle("Section 9", "User Rights",
                    "Citizens have right to access, correct and erase their personal data.", true),
                LawArticle("Section 14", "Data Localization",
                    "Sensitive data of Pakistani citizens must be stored on servers within Pakistan.", false),
                LawArticle("Section 22", "Penalties",
                    "Fines up to PKR 25 million for serious violations (once enacted).", false)
            ),
            appliesTo = listOf("Privacy Policy","Terms of Service","Loan Agreement"),
            enforcedBy = "NCPDP — National Commission for Personal Data Protection (proposed)",
            penalty = "Up to PKR 25 million (once enacted)"
        ),

        Law(
            id = "peca",
            shortName = "PECA 2016",
            fullName = "Prevention of Electronic Crimes Act 2016",
            region = "Pakistan",
            flag = "🇵🇰",
            category = "Cybercrime & Digital Rights",
            status = "Enacted",
            year = 2016,
            summary = "Pakistan's primary cybercrime law. Includes provisions for data privacy and unauthorized data access that apply to digital policy violations.",
            userRights = listOf(
                "Protection against unauthorized access to your data",
                "Protection against data interception without consent",
                "Right to report unauthorized data collection",
                "Protection against online harassment",
                "Protection against identity theft"
            ),
            articles = listOf(
                LawArticle("Section 3", "Unauthorized Access",
                    "Criminal offense to access data systems without authorization.", true),
                LawArticle("Section 9", "Data Damage",
                    "Offense to damage, alter or suppress data without authorization.", true),
                LawArticle("Section 38", "Privacy of Data",
                    "Service providers must protect users private data and cannot share without lawful authority.", true),
                LawArticle("Section 43", "Jurisdiction",
                    "Applies to offenses anywhere if data of Pakistani citizens is involved.", false)
            ),
            appliesTo = listOf("Privacy Policy","Terms of Service","Mobile App Permissions"),
            enforcedBy = "FIA — Federal Investigation Agency, Cybercrime Wing",
            penalty = "3 months to 7 years imprisonment"
        ),

        Law(
            id = "cpa",
            shortName = "CPA 2019",
            fullName = "Consumer Protection Act 2019",
            region = "Pakistan",
            flag = "🇵🇰",
            category = "Consumer Rights",
            status = "Enacted",
            year = 2019,
            summary = "Protects Pakistani consumers from unfair trade practices, misleading terms and deceptive business conduct including digital services.",
            userRights = listOf(
                "Right to accurate and truthful information",
                "Right to fair contract terms",
                "Right to refund for defective services",
                "Protection against misleading advertisements",
                "Right to file complaint with Consumer Court",
                "Protection against unfair automatic renewals"
            ),
            articles = listOf(
                LawArticle("Section 8", "Unfair Trade Practices",
                    "Prohibits deceptive terms, hidden charges and misleading policy language.", true),
                LawArticle("Section 12", "Unfair Contract Terms",
                    "Terms that create imbalance between consumer and company rights are unenforceable.", true),
                LawArticle("Section 15", "Right to Redress",
                    "Consumers can seek refund, replacement or compensation for violations.", true),
                LawArticle("Section 26", "Penalties",
                    "Fines and imprisonment for companies violating consumer rights.", false)
            ),
            appliesTo = listOf("Terms of Service","Loan Agreement","Insurance","Rental Contract"),
            enforcedBy = "Provincial Consumer Protection Councils",
            penalty = "PKR 1 million to 5 million + imprisonment"
        ),

        Law(
            id = "ccpa",
            shortName = "CCPA",
            fullName = "California Consumer Privacy Act",
            region = "United States",
            flag = "🇺🇸",
            category = "Data Privacy",
            status = "Enacted",
            year = 2020,
            summary = "Gives California residents control over personal data. Applies globally to any company serving California users — including apps used in Pakistan.",
            userRights = listOf(
                "Right to know what personal data is collected",
                "Right to delete personal data",
                "Right to opt-out of data sale",
                "Right to non-discrimination",
                "Right to correct inaccurate data",
                "Right to limit use of sensitive information"
            ),
            articles = listOf(
                LawArticle("Section 1798.100", "Right to Know",
                    "Consumers can request disclosure of personal data collected in past 12 months.", true),
                LawArticle("Section 1798.105", "Right to Delete",
                    "Consumers can request deletion of personal data held by businesses.", true),
                LawArticle("Section 1798.120", "Right to Opt-Out",
                    "Consumers can opt-out of sale of their personal information.", true),
                LawArticle("Section 1798.150", "Private Right of Action",
                    "Consumers can sue companies directly for data breaches up to $750 per incident.", false)
            ),
            appliesTo = listOf("Privacy Policy","Terms of Service","Cookie Policy"),
            enforcedBy = "California Privacy Protection Agency (CPPA)",
            penalty = "Up to $7,500 per intentional violation"
        ),

        Law(
            id = "sbp",
            shortName = "SBP Regs",
            fullName = "Payment Systems & Electronic Funds Transfer Act 2007",
            region = "Pakistan",
            flag = "🇵🇰",
            category = "Financial",
            status = "Enacted",
            year = 2007,
            summary = "Regulates digital payment systems in Pakistan. Governs how fintech apps, banks and loan platforms must handle financial data and disclose charges.",
            userRights = listOf(
                "Right to transparent fee disclosure",
                "Right to dispute unauthorized transactions",
                "Right to clear loan repayment terms",
                "Protection against hidden financial charges",
                "Right to payment transaction records"
            ),
            articles = listOf(
                LawArticle("Section 5", "Authorization",
                    "Payment systems must be authorized by State Bank of Pakistan.", false),
                LawArticle("Section 14", "Consumer Disclosure",
                    "All charges, fees and interest rates must be clearly disclosed before transaction.", true),
                LawArticle("Section 21", "Dispute Resolution",
                    "Consumers have right to dispute unauthorized or erroneous transactions.", true)
            ),
            appliesTo = listOf("Loan Agreement","Insurance","Terms of Service"),
            enforcedBy = "State Bank of Pakistan (SBP)",
            penalty = "SBP can revoke license and impose fines"
        )
    )

    fun search(query: String) = ALL.filter {
        it.shortName.contains(query, true) ||
        it.fullName.contains(query, true) ||
        it.region.contains(query, true) ||
        it.summary.contains(query, true)
    }

    val CATEGORIES = listOf(
        "All","Data Privacy",
        "Consumer Rights",
        "Cybercrime & Digital Rights",
        "Financial"
    )

    val REGIONS = listOf(
        "All","Pakistan","European Union","United States"
    )
}
