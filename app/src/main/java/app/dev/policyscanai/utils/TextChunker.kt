package app.dev.policyscanai.utils

object TextChunker {

    // Keep small — worker models have 1024 token limit
    // 300 words leaves room for prompt + JSON output
    private const val MAX_WORDS = 300

    data class Chunk(
        val index: Int,
        val text: String,
        val page: Int
    )

    fun split(pageTexts: Map<Int, String>): List<Chunk> {
        val out = mutableListOf<Chunk>()
        var idx = 0
        pageTexts.forEach { (page, text) ->
            if (text.isBlank()) return@forEach
            text.trim()
                .split(Regex("\\s+"))
                .chunked(MAX_WORDS)
                .forEach { words ->
                    out.add(Chunk(idx++,
                        words.joinToString(" "), page))
                }
        }
        return out
    }
}
