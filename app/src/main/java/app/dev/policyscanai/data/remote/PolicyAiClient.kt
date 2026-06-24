package app.dev.policyscanai.data.remote

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class PolicyAiClient {

    private val URL = "https://ai.creativetaleem.app"
    private val KEY = "ct-ai-2025-khawat-secret"

    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun ask(prompt: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val jsonPrompt = Gson().toJson(prompt)
                val body = "{\"prompt\":$jsonPrompt}".toRequestBody("application/json".toMediaType())

                val req = Request.Builder()
                    .url(URL)
                    .post(body)
                    .addHeader("X-API-Key", KEY)
                    .addHeader("X-Model", "text")
                    .build()

                http.newCall(req).execute().use { res ->
                    val raw = res.body?.string()
                        ?: return@withContext Result.failure(Exception("AI returned an empty response."))

                    if (!res.isSuccessful) {
                        return@withContext Result.failure(Exception("AI Server Error (${res.code}): $raw"))
                    }

                    val map = try {
                        Gson().fromJson(raw, Map::class.java)
                    } catch (e: Exception) {
                        return@withContext Result.failure(Exception("Failed to parse AI response format."))
                    }

                    val answer = map["answer"] as? String
                        ?: return@withContext Result.failure(Exception("AI response is missing the 'answer' field."))

                    if (answer.startsWith("⚠️")) {
                        return@withContext Result.failure(Exception("AI Service Temporarily Unavailable: $answer"))
                    }

                    Result.success(answer)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
