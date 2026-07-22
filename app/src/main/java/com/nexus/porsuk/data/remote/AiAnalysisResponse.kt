package com.nexus.porsuk.data.remote

import org.json.JSONObject

/**
 * Structured Data Class for Gemini Analysis Responses.
 * Includes Consensus Engine Score (0-100), dragging factors, and Data Quality.
 */
data class AiAnalysisResponse(
    val positives: List<String> = emptyList(),
    val negatives: List<String> = emptyList(),
    val risks: List<String> = emptyList(),
    val uncertainties: List<String> = emptyList(),
    val watchLevels: List<String> = emptyList(),
    val explainableReasons: List<String> = emptyList(),
    val consensusScore: Int = 82,
    val scoreDraggingFactors: List<String> = emptyList(),
    val dataQualityScore: Int = 85,
    val dataQualityLevel: String = "Yüksek",
    val missingDataWarnings: List<String> = emptyList(),
    val summary: String = "",
    val confidence: Int = 0,
    val confidenceReason: String = "",
    val rawText: String = ""
) {
    /**
     * Converts structured JSON into clean, concise Markdown including Consensus Score (0-100) and dragging factors when <65.
     */
    fun toFormattedMarkdown(): String {
        if (positives.isEmpty() && negatives.isEmpty() && risks.isEmpty() && summary.isBlank() && rawText.isNotBlank()) {
            return rawText
        }

        val sb = StringBuilder()

        if (consensusScore < 65 || scoreDraggingFactors.isNotEmpty()) {
            sb.append("⚠️ **DÜŞÜK UZLAŞI (KONSENSÜS) UYARISI ($consensusScore/100):**\n")
            if (scoreDraggingFactors.isNotEmpty()) {
                sb.append("• **Skoru Düşüren Faktörler:** ").append(scoreDraggingFactors.joinToString(", ")).append("\n")
            } else {
                sb.append("• Analiz kategorileri arasında yüksek görüş ayrılığı saptandı. Analizi temkinli değerlendiriniz.\n")
            }
            sb.append("\n")
        }

        if (missingDataWarnings.isNotEmpty()) {
            sb.append("⚠️ **Eksik Veri Uyarıları:**\n")
            missingDataWarnings.forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (positives.isNotEmpty()) {
            sb.append("### 🟢 Olumlu Faktörler\n")
            positives.take(2).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (negatives.isNotEmpty()) {
            sb.append("### 🔴 Olumsuz Faktörler\n")
            negatives.take(2).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (risks.isNotEmpty()) {
            sb.append("### ⚠️ Riskler\n")
            risks.take(2).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (uncertainties.isNotEmpty()) {
            sb.append("### ❓ Belirsizlikler\n")
            uncertainties.take(2).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (watchLevels.isNotEmpty()) {
            sb.append("### 📌 İzlenmesi Gereken Seviyeler\n")
            watchLevels.take(2).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        if (summary.isNotBlank()) {
            sb.append("### 🎯 Sonuç\n").append(summary).append("\n\n")
        }

        // Explainable AI Layer (Neden bu sonuca ulaşıldı? - Max 5 Items)
        if (explainableReasons.isNotEmpty()) {
            sb.append("### 🔍 Neden bu sonuca ulaşıldı?\n")
            explainableReasons.take(5).forEach { sb.append("• ").append(it).append("\n") }
            sb.append("\n")
        }

        sb.append("🤝 **BÜTÜNLEŞİK KONSENSÜS SKORU: ").append(consensusScore).append(" / 100**\n")
        sb.append("📶 **Veri Kalitesi Skoru: ").append(dataQualityScore).append(" / 100** (Seviye: ").append(dataQualityLevel.ifBlank { "Yüksek" }).append(")\n")

        if (confidence in 1..100) {
            sb.append("\n---\n")
            sb.append("📊 **ANALİZ GÜVEN SKORU: $confidence / 100**\n")
            if (confidenceReason.isNotBlank()) {
                sb.append("💡 *Güven Gerekçesi:* ").append(confidenceReason).append("\n")
            }
        }

        return sb.toString().trim()
    }

    companion object {
        /**
         * Safely parse JSON response from Gemini or fallback to rawText if invalid JSON.
         */
        fun parseFromJsonOrRaw(rawResponse: String): AiAnalysisResponse {
            if (rawResponse.isBlank()) return AiAnalysisResponse(rawText = rawResponse)

            return try {
                val cleaned = rawResponse.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val json = JSONObject(cleaned)
                val summary = json.optString("summary", "")
                val confidence = json.optInt("confidence", 0)
                val confidenceReason = json.optString("confidenceReason", "")
                val consensusScore = json.optInt("consensusScore", 82)
                val dataQualityScore = json.optInt("dataQualityScore", 85)
                val dataQualityLevel = json.optString("dataQualityLevel", "Yüksek")

                val positives = mutableListOf<String>()
                json.optJSONArray("positives")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) positives.add(item)
                    }
                }

                val negatives = mutableListOf<String>()
                json.optJSONArray("negatives")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) negatives.add(item)
                    }
                }

                val risks = mutableListOf<String>()
                json.optJSONArray("risks")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) risks.add(item)
                    }
                }

                val uncertainties = mutableListOf<String>()
                json.optJSONArray("uncertainties")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) uncertainties.add(item)
                    }
                }

                val watchLevels = mutableListOf<String>()
                json.optJSONArray("watchLevels")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) watchLevels.add(item)
                    }
                }

                val explainableReasons = mutableListOf<String>()
                json.optJSONArray("explainableReasons")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) explainableReasons.add(item)
                    }
                }

                val scoreDraggingFactors = mutableListOf<String>()
                json.optJSONArray("scoreDraggingFactors")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) scoreDraggingFactors.add(item)
                    }
                }

                val missingDataWarnings = mutableListOf<String>()
                json.optJSONArray("missingDataWarnings")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val item = arr.optString(i)
                        if (item.isNotBlank()) missingDataWarnings.add(item)
                    }
                }

                if (positives.isEmpty() && negatives.isEmpty() && risks.isEmpty() && summary.isBlank()) {
                    AiAnalysisResponse(rawText = rawResponse)
                } else {
                    AiAnalysisResponse(
                        positives = positives,
                        negatives = negatives,
                        risks = risks,
                        uncertainties = uncertainties,
                        watchLevels = watchLevels,
                        explainableReasons = explainableReasons,
                        consensusScore = consensusScore,
                        scoreDraggingFactors = scoreDraggingFactors,
                        dataQualityScore = dataQualityScore,
                        dataQualityLevel = dataQualityLevel,
                        missingDataWarnings = missingDataWarnings,
                        summary = summary,
                        confidence = confidence,
                        confidenceReason = confidenceReason,
                        rawText = rawResponse
                    )
                }
            } catch (_: Exception) {
                AiAnalysisResponse(rawText = rawResponse)
            }
        }
    }
}
