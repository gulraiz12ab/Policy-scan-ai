package app.dev.policyscanai.domain

import app.dev.policyscanai.data.remote.PolicyAiClient
import app.dev.policyscanai.utils.TextChunker
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

data class AiFinding(
    val id: String        = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val originalText: String,
    val simplified: String,
    val riskLevel: String,   // RED | ORANGE | YELLOW
    val category: String,
    val laws: List<String>,
    val page: Int,
    val chunk: Int
)

data class ScanReport(
    val findings: List<AiFinding>,
    val summaryPoints: List<String>,
    val overallRisk: String,
    val headline: String,
    val totalChunks: Int,
    val modelUsed: String = ""
)

sealed class ScanEvent {
    data class Step(val label: String,
                    val progress: Float)  : ScanEvent()
    data class ChunkDone(
        val current: Int,
        val total: Int,
        val progress: Float,
        val newFindings: List<AiFinding>)  : ScanEvent()
    data class Complete(val report: ScanReport): ScanEvent()
    data class Error(val message: String)  : ScanEvent()
}

class ScanOrchestrator {

    private val ai = PolicyAiClient()
    private val gson = Gson()

    private fun chunkPrompt(
        chunk: TextChunker.Chunk,
        policyType: String
    ) = """
You are a senior policy analyst. Identify unfair, harmful, or restrictive clauses in this document.
Policy Category: $policyType | Document Page: ${chunk.page}

MANDATORY RESPONSE FORMAT (JSON ONLY):
{"findings":[{"title":"Short Title","description":"Detailed explanation","originalText":"Exact quote","simplified":"One-sentence summary","riskLevel":"RED","category":"Data Privacy","laws":["GDPR"]}]}

RISK LEVELS:
- RED: Serious privacy violations or legal traps.
- ORANGE: Concerning terms or lack of clarity.
- YELLOW: Minor issues or standard data collection.

If no issues found, return: {"findings":[]}
Max 3 findings.

DOCUMENT TEXT:
${chunk.text}
""".trimIndent()

    private fun summaryPrompt(
        findings: List<AiFinding>,
        fileName: String,
        policyType: String
    ): String {
        val list = findings.take(12).joinToString("\n") {
            "[${it.riskLevel}] ${it.title}: ${it.description}"
        }
        return """
You are a consumer protection lawyer. Review these findings and provide a final verdict.
Document: $fileName | Type: $policyType
Findings:
$list

MANDATORY RESPONSE FORMAT (JSON ONLY):
{"points":["bullet 1","bullet 2","bullet 3"],"risk":"RED","headline":"One-sentence verdict"}

RISK: RED | ORANGE | YELLOW | GREEN
POINTS: Exactly 3 concise bullets.
""".trimIndent()
    }

    private fun cleanJson(raw: String): String {
        return raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    fun scan(
        pageTexts: Map<Int, String>,
        fileName: String,
        policyType: String
    ): Flow<ScanEvent> = flow {

        emit(ScanEvent.Step("Preparing document analysis...", 0.05f))
        val chunks = TextChunker.split(pageTexts)
        val total  = chunks.size

        if (chunks.isEmpty()) {
            emit(ScanEvent.Error("Could not detect any text. Please ensure the scan is clear."))
            return@flow
        }

        emit(ScanEvent.Step("Analyzing $total document segments...", 0.10f))

        val allFindings = mutableListOf<AiFinding>()

        chunks.forEachIndexed { i, chunk ->
            val progress = 0.10f + (0.75f * ((i + 1).toFloat() / total))
            emit(ScanEvent.Step("Analyzing segment ${i + 1} of $total...", progress))

            val result = ai.ask(chunkPrompt(chunk, policyType))

            result.onSuccess { raw ->
                val cleaned = cleanJson(raw)
                try {
                    val obj = gson.fromJson(cleaned, JsonObject::class.java)
                    val arr = obj.getAsJsonArray("findings") ?: return@onSuccess

                    val chunkFindings = arr.mapNotNull { el ->
                        try {
                            val f = el.asJsonObject
                            AiFinding(
                                title = f["title"]?.asString ?: return@mapNotNull null,
                                description = f["description"]?.asString ?: "",
                                originalText = f["originalText"]?.asString ?: "",
                                simplified = f["simplified"]?.asString ?: "",
                                riskLevel = f["riskLevel"]?.asString?.uppercase() ?: "YELLOW",
                                category = f["category"]?.asString ?: "General",
                                laws = f.getAsJsonArray("laws")?.map { it.asString } ?: emptyList(),
                                page = chunk.page,
                                chunk = chunk.index
                            )
                        } catch (e: Exception) { null }
                    }

                    allFindings.addAll(chunkFindings)
                    emit(ScanEvent.ChunkDone(i + 1, total, progress, chunkFindings))
                } catch (e: Exception) {
                    emit(ScanEvent.ChunkDone(i + 1, total, progress, emptyList()))
                }
            }.onFailure {
                emit(ScanEvent.ChunkDone(i + 1, total, progress, emptyList()))
            }
        }

        emit(ScanEvent.Step("Generating final verdict...", 0.90f))
        val summaryResult = ai.ask(summaryPrompt(allFindings, fileName, policyType))

        val (points, risk, headline) = summaryResult.getOrNull()?.let { raw ->
            val cleaned = cleanJson(raw)
            try {
                val obj = gson.fromJson(cleaned, JsonObject::class.java)
                Triple(
                    obj.getAsJsonArray("points")?.map { it.asString } ?: listOf("Review completed.", "${allFindings.size} findings.", "See details below."),
                    obj["risk"]?.asString?.uppercase() ?: "YELLOW",
                    obj["headline"]?.asString ?: "Scan Complete"
                )
            } catch (e: Exception) {
                Triple(listOf("Analysis finished.", "${allFindings.size} issues flagged.", "Review findings."), "YELLOW", "Scan Complete")
            }
        } ?: Triple(listOf("Document analyzed.", "${allFindings.size} total findings.", "Check details."), "YELLOW", "Scan Complete")

        emit(ScanEvent.Step("Finalizing Report...", 0.98f))
        emit(ScanEvent.Complete(ScanReport(
            findings = allFindings.sortedBy { when (it.riskLevel) { "RED" -> 0; "ORANGE" -> 1; "YELLOW" -> 2; else -> 3 } },
            summaryPoints = points,
            overallRisk = risk,
            headline = headline,
            totalChunks = total
        )))
    }.flowOn(Dispatchers.IO)
}
