package com.nexus.porsuk.ui.common

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.delay

object GeminiModels {
    val fallbackList = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash",
        "gemini-1.5-flash"
    )

    suspend fun generateContentWithFallback(
        apiKey: String,
        prompt: String,
        systemInstruction: Content? = null
    ): String {
        val exceptions = mutableListOf<Pair<String, Exception>>()
        
        for (modelName in fallbackList) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = systemInstruction
                )
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: Exception) {
                exceptions.add(modelName to e)
                
                val errorMsg = e.message ?: ""
                val fullText = "$errorMsg ${e.localizedMessage ?: ""}"
                
                // 404 (Model Bulunamadı) veya 429 (Kota/Rate Limit) durumunda DİĞER MODELİ DENE!
                if (fullText.contains("404") ||
                    fullText.contains("NOT_FOUND", ignoreCase = true) ||
                    fullText.contains("is not found for API version", ignoreCase = true)
                ) {
                    continue
                }

                if (fullText.contains("429") ||
                    fullText.contains("quota", ignoreCase = true) ||
                    fullText.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                    fullText.contains("rate limit", ignoreCase = true)
                ) {
                    // Kota/Rate limit aşıldığında kısa bir gecikme ekleyip alternatif modeli dene
                    delay(1200L)
                    continue
                }

                // Sadece kritik yetkilendirme / ağ hatalarında erken dur
                if (fullText.contains("API key not valid", ignoreCase = true) ||
                    fullText.contains("API_KEY_INVALID", ignoreCase = true) ||
                    fullText.contains("401") ||
                    fullText.contains("403") ||
                    fullText.contains("PERMISSION_DENIED", ignoreCase = true) ||
                    fullText.contains("Unable to resolve host", ignoreCase = true)
                ) {
                    throw e
                }
            }
        }
        
        // Tüm modeller denendiyse en son hatayı fırlat
        if (exceptions.isNotEmpty()) {
            throw exceptions.last().second
        }
        throw Exception("Modellerin hiçbirinden yanıt alınamadı.")
    }
}
