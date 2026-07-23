package com.nexus.porsuk.ui.common

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object GeminiModels {
    /**
     * Candidate model list including latest Google AI Studio models.
     */
    val defaultModelCandidates = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite",
        "gemini-3.5-flash",
        "gemini-2.0-flash",
        "gemini-1.5-flash",
        "gemini-1.5-flash-latest",
        "gemini-1.5-pro"
    )

    @Volatile
    var activeWorkingModel: String = "gemini-1.5-flash"
        private set

    private const val TAG = "GEMINI_SERVICE"

    /**
     * Queries Google AI ListModels endpoint via HTTP to find all models supported by the user's API key.
     */
    suspend fun discoverAvailableModels(apiKey: String): List<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext emptyList()
        val discovered = mutableListOf<String>()
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseStr = reader.use { it.readText() }
                val json = JSONObject(responseStr)
                val modelsArray = json.optJSONArray("models")
                if (modelsArray != null) {
                    for (i in 0 until modelsArray.length()) {
                        val modelObj = modelsArray.optJSONObject(i) ?: continue
                        val name = modelObj.optString("name", "").removePrefix("models/")
                        val supportedMethods = modelObj.optJSONArray("supportedGenerationMethods")
                        var supportsGenerateContent = false
                        if (supportedMethods != null) {
                            for (j in 0 until supportedMethods.length()) {
                                if (supportedMethods.optString(j) == "generateContent") {
                                    supportsGenerateContent = true
                                    break
                                }
                            }
                        }
                        if (supportsGenerateContent && name.isNotBlank()) {
                            discovered.add(name)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "ListModels discovery failed, falling back to default candidate list: ${e.message}")
        }
        return@withContext discovered
    }

    suspend fun generateContentWithFallback(
        apiKey: String,
        prompt: String,
        systemInstruction: Content? = null
    ): String {
        // First try discovered models, then default candidate list
        val discovered = discoverAvailableModels(apiKey)
        val candidates = (discovered + defaultModelCandidates).distinct()

        var lastException: Exception? = null

        for (modelName in candidates) {
            Log.d(TAG, "Attempting Gemini call -> Model: $modelName | API Key Present: ${apiKey.isNotBlank()}")

            for (apiVersion in listOf("v1", "v1beta")) {
                try {
                    val model = GenerativeModel(
                        modelName = modelName,
                        apiKey = apiKey,
                        systemInstruction = systemInstruction,
                        requestOptions = RequestOptions(apiVersion = apiVersion)
                    )
                    val response = model.generateContent(prompt)
                    val text = response.text
                    if (!text.isNullOrBlank()) {
                        activeWorkingModel = modelName
                        Log.i(TAG, "Gemini SUCCESS! Active working model selected: $modelName (API Version: $apiVersion) | Length: ${text.length}")
                        return text
                    }
                } catch (e: Exception) {
                    lastException = e
                    Log.w(TAG, "Gemini Attempt Failed -> Model: $modelName (Version: $apiVersion) | Error: ${e.message}")
                }
            }
        }

        throw lastException ?: Exception("Gemini servisinden yanıt alınamadı.")
    }

    fun generateContentStreamWithFallback(
        apiKey: String,
        prompt: String,
        systemInstruction: Content? = null
    ): kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.channelFlow {
        var success = false
        var lastException: Exception? = null

        // Try active working model first if available
        val initialCandidates = (listOf(activeWorkingModel) + defaultModelCandidates).distinct()

        for (modelName in initialCandidates) {
            for (apiVersion in listOf("v1", "v1beta")) {
                Log.d(TAG, "Attempting Gemini Stream -> Model: $modelName (Version: $apiVersion) | API Key Present: ${apiKey.isNotBlank()}")

                try {
                    var accumulated = ""
                    val model = GenerativeModel(
                        modelName = modelName,
                        apiKey = apiKey,
                        systemInstruction = systemInstruction,
                        requestOptions = RequestOptions(apiVersion = apiVersion)
                    )
                    val stream = model.generateContentStream(prompt)
                    stream.collect { chunk ->
                        val text = chunk.text ?: ""
                        accumulated += text
                        if (text.isNotEmpty()) {
                            send(text)
                        }
                    }
                    if (accumulated.isNotBlank()) {
                        success = true
                        activeWorkingModel = modelName
                        Log.i(TAG, "Gemini Stream SUCCESS! Active working model: $modelName | Total Length: ${accumulated.length}")
                        break
                    }
                } catch (e: Exception) {
                    lastException = e
                    Log.w(TAG, "Gemini Stream Attempt Failed -> Model: $modelName (Version: $apiVersion) | Error: ${e.message}")
                }
            }
            if (success) break
        }

        if (!success) {
            throw lastException ?: Exception("Gemini servisinden yanıt alınamadı.")
        }
    }
}
