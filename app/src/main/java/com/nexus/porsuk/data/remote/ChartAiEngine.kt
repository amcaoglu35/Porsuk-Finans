package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.domain.model.AiChartAnalysis
import com.nexus.porsuk.domain.model.CandleStickItem
import com.nexus.porsuk.domain.model.TechnicalSignalType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Engine for specialized chart pattern recognition and analysis.
 */
@Singleton
class ChartAiEngine @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend fun analyzeChartData(symbol: String, candles: List<CandleStickItem>): AiChartAnalysis? = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext null
        val service = GeminiService(apiKey)

        val candleData = candles.takeLast(50).joinToString("\n") { 
            "${it.timestamp}: O:${it.open} H:${it.high} L:${it.low} C:${it.close} V:${it.volume}"
        }

        val prompt = """
            Aşağıdaki 50 adet mum verisini (OHLCV) profesyonel bir teknik analist gibi analiz et:
            Sembol: $symbol
            Veriler:
            $candleData
            
            Lütfen şu formatta JSON yanıtı ver:
            {
              "trend": "Yükseliş/Düşüş/Yatay ve nedeni",
              "pattern": "Tespit edilen formasyon adı (yoksa null)",
              "supportLevels": [destek1, destek2],
              "resistanceLevels": [direnc1, direnc2],
              "riskScore": 1-10 arası,
              "confidence": 1-100 arası,
              "scenario": "Beklenen olası senaryo özeti",
              "signal": "STRONG_BUY/BUY/NEUTRAL/SELL/STRONG_SELL"
            }
        """.trimIndent()

        try {
            val response = service.chat(prompt)
            val jsonStr = response.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$jsonStr}")
            
            val supports = mutableListOf<Double>()
            val sArr = json.getJSONArray("supportLevels")
            for (i in 0 until sArr.length()) supports.add(sArr.getDouble(i))

            val resistances = mutableListOf<Double>()
            val rArr = json.getJSONArray("resistanceLevels")
            for (i in 0 until rArr.length()) resistances.add(rArr.getDouble(i))

            AiChartAnalysis(
                trend = json.getString("trend"),
                pattern = if (json.isNull("pattern")) null else json.getString("pattern"),
                supportLevels = supports,
                resistanceLevels = resistances,
                riskScore = json.getInt("riskScore"),
                confidence = json.getInt("confidence"),
                scenario = json.getString("scenario"),
                signal = TechnicalSignalType.valueOf(json.getString("signal"))
            )
        } catch (e: Exception) {
            null
        }
    }
}
