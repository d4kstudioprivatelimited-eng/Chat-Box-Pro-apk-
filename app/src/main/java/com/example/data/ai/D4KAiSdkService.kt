package com.example.data.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import java.util.concurrent.TimeUnit

class D4KAiSdkService private constructor() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val endpointUrl = "https://mr4k09.alwaysdata.net/index.php"
    private val bearerToken = "Bearer D4K-AIU6WD4D1FEJ"

    companion object {
        @Volatile
        private var instance: D4KAiSdkService? = null

        fun getInstance(): D4KAiSdkService {
            return instance ?: synchronized(this) {
                instance ?: D4KAiSdkService().also { instance = it }
            }
        }
    }

    suspend fun generateAutoReply(
        userPrompt: String,
        isImageRequest: Boolean = false,
        imageBase64: String? = null
    ): String = withContext(Dispatchers.IO) {
        val jsonPayload = JSONObject().apply {
            put("prompt", userPrompt)
            put("isImageRequest", isImageRequest)
            put("imageBase64", imageBase64 ?: JSONObject.NULL)
        }.toString()

        val mediaType = "application/json".toMediaType()
        val body = jsonPayload.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(endpointUrl)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", bearerToken)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val rawString = response.body?.string() ?: ""
                Log.d("D4KAiSdkService", "D4K AI response code: ${response.code}, raw: $rawString")

                if (response.isSuccessful && rawString.isNotBlank()) {
                    try {
                        val json = JSONObject(rawString)
                        when {
                            json.has("reply") -> json.getString("reply")
                            json.has("response") -> json.getString("response")
                            json.has("message") -> json.getString("message")
                            json.has("text") -> json.getString("text")
                            else -> rawString.trim()
                        }
                    } catch (_: Exception) {
                        rawString.trim()
                    }
                } else {
                    generateGeminiFallback(userPrompt)
                }
            }
        } catch (e: Exception) {
            Log.e("D4KAiSdkService", "Error calling D4K AI SDK: ${e.message}, invoking Gemini AI fallback...")
            generateGeminiFallback(userPrompt)
        }
    }

    private suspend fun generateGeminiFallback(userPrompt: String): String {
        return try {
            val model = com.google.firebase.Firebase.ai.generativeModel("gemini-3.8-flash")
            val response = kotlinx.coroutines.withTimeoutOrNull(10000L) {
                model.generateContent("You are Chat Box Pro Auto AI Assistant. Write a friendly, helpful, short chat response (1-2 sentences max) replying to:\n$userPrompt")
            }
            response?.text?.trim()?.ifBlank { null }
                ?: "Thanks for reaching out! Auto AI Reply is active and I will respond soon."
        } catch (e: Exception) {
            Log.w("D4KAiSdkService", "Gemini AI fallback exception: ${e.message}")
            "Thanks for reaching out! Auto AI Reply is active."
        }
    }

    suspend fun summarizeMessage(text: String): String {
        val prompt = "Summarize the following message in 1-2 concise bullet points:\n$text"
        return generateAutoReply(prompt, isImageRequest = false)
    }

    suspend fun generateAvatar(prompt: String): String {
        val fullPrompt = "Generate a stylized HD user avatar image for profile: $prompt"
        return generateAutoReply(fullPrompt, isImageRequest = true)
    }

    suspend fun translateMessage(text: String, targetLanguage: String): String {
        val prompt = "Translate the following chat message to $targetLanguage keeping tone intact:\n$text"
        return generateAutoReply(prompt, isImageRequest = false)
    }
}

