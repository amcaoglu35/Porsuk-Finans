package com.nexus.porsuk.data.remote

import com.nexus.porsuk.domain.model.AiEventImpact
import com.nexus.porsuk.domain.model.EconomicEvent
import com.nexus.porsuk.ui.common.GeminiModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Engine for analyzing financial events and their potential market impact.
 */
@Singleton
class CalendarAiEngine @Inject constructor(
    private val settingsManager: com.nexus.porsuk.data.local.SettingsManager
) {

    suspend fun analyzeEventImpact(event: EconomicEvent): AiEventImpact? = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext null
        val prompt = """
            Aşağıdaki finansal etkinliğin piyasa üzerindeki olası etkisini analiz et:
            Etkinlik: ${event.title}
            Ülke: ${event.country}
            Kategori: ${event.category.displayName}
            Önem: ${event.impactLevel.displayName}
            
            Lütfen aşağıdaki JSON formatında bir yanıt ver:
            {
              "eventId": "${event.eventId}",
              "expectedImpact": "OLUMLU/OLUMSUZ/NÖTR",
              "affectedSectors": ["Sektör1", "Sektör2"],
              "riskLevel": 1-10 arası sayı,
              "opportunityLevel": 1-10 arası sayı,
              "aiCommentary": "Analiz özeti ve tavsiye"
            }
        """.trimIndent()

        try {
            val response = GeminiModels.generateContentWithFallback(apiKey, prompt)
            val jsonStr = response.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$jsonStr}")
            
            val sectors = mutableListOf<String>()
            val sectorsArr = json.getJSONArray("affectedSectors")
            for (i in 0 until sectorsArr.length()) {
                sectors.add(sectorsArr.getString(i))
            }

            AiEventImpact(
                eventId = json.getString("eventId"),
                expectedImpact = json.getString("expectedImpact"),
                affectedSectors = sectors,
                riskLevel = json.getInt("riskLevel"),
                opportunityLevel = json.getInt("opportunityLevel"),
                aiCommentary = json.getString("aiCommentary")
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateDailySummary(events: List<EconomicEvent>): String = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext "AI özeti hazırlanamadı."
        if (events.isEmpty()) return@withContext "Bugün için kritik bir ekonomik veri akışı bulunmuyor."

        val eventListText = events.joinToString("\n") { "• ${it.title} (${it.country})" }
        val prompt = "Bugünkü şu finansal olayları kısaca özetle ve hangi sektörlerde hareketlilik beklendiğini belirt:\n$eventListText"

        try {
            GeminiModels.generateContentWithFallback(apiKey, prompt)
        } catch (e: Exception) {
            "Bugün piyasalarda önemli veriler açıklanacak. Hareketliliğe hazırlıklı olun."
        }
    }
}
