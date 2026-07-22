package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.DecisionJournalEntry
import java.util.Locale

/**
 * Calculated Decision Journal Statistics.
 */
data class JournalStatistics(
    val totalEntries: Int,
    val successRatePercent: Double,
    val successfulCount: Int,
    val failedCount: Int,
    val mostSuccessfulSector: String,
    val leastSuccessfulSector: String,
    val mostSuccessfulStyle: String,
    val styleSuccessRates: Map<String, Double>,
    val overallSummaryMarkdown: String
)

object DecisionJournalManager {

    /**
     * Evaluates a decision entry against the current price.
     * Produces tailored AI evaluation feedback.
     */
    fun evaluateEntry(entry: DecisionJournalEntry, currentPrice: Double): String {
        if (entry.buyPrice <= 0 || currentPrice <= 0) {
            return "Fiyat verisi henüz güncellenmedi."
        }

        val changePct = ((currentPrice - entry.buyPrice) / entry.buyPrice) * 100.0
        val isSuccessful = changePct > 0.0

        val sb = StringBuilder()
        if (isSuccessful) {
            sb.append("🎯 **Başarılı Karar!** Bu karardan sonra ${entry.symbol} %${String.format(Locale.US, "%.1f", changePct)} yükseldi. ")
            sb.append("İlk analizin doğru çıktı! Hedef süren: ${entry.targetHorizon}. Gerekçen: \"${entry.reason}\".")
        } else {
            sb.append("⚠️ **Risk Değerlendirmesi:** Bu karardan sonra ${entry.symbol} %${String.format(Locale.US, "%.1f", kotlin.math.abs(changePct))} geriledi. ")
            sb.append("Hedef süren (${entry.targetHorizon}) ve \"${entry.reason}\" gerekçen doğrultusunda sabırlı kalabilir veya stop seviyeni gözden geçirebilirsin.")
        }
        return sb.toString()
    }

    /**
     * Calculates comprehensive private Decision Journal Statistics locally on device.
     */
    fun calculateStatistics(
        entries: List<DecisionJournalEntry>,
        companyMap: Map<String, Company>
    ): JournalStatistics {
        if (entries.isEmpty()) {
            return JournalStatistics(
                totalEntries = 0,
                successRatePercent = 0.0,
                successfulCount = 0,
                failedCount = 0,
                mostSuccessfulSector = "Veri Yok",
                leastSuccessfulSector = "Veri Yok",
                mostSuccessfulStyle = "Veri Yok",
                styleSuccessRates = emptyMap(),
                overallSummaryMarkdown = "Henüz kayıtlı bir yatırım kararı notunuz bulunmuyor. Alım-satım kararlarınızı kaydederek başarım istatistiklerinizi takip edin."
            )
        }

        var successCount = 0
        var failCount = 0

        val sectorPnLMap = mutableMapOf<String, MutableList<Double>>()
        val styleWinMap = mutableMapOf<String, Pair<Int, Int>>() // Style -> (Wins, Total)

        entries.forEach { entry ->
            val comp = companyMap[entry.symbol]
            val currentPrice = comp?.currentPrice ?: entry.buyPrice
            val changePct = if (entry.buyPrice > 0) ((currentPrice - entry.buyPrice) / entry.buyPrice) * 100.0 else 0.0
            val isWin = changePct > 0.0

            if (isWin) successCount++ else failCount++

            val sector = comp?.sector ?: entry.sector
            if (!sectorPnLMap.containsKey(sector)) sectorPnLMap[sector] = mutableListOf()
            sectorPnLMap[sector]?.add(changePct)

            val style = entry.investmentStyle.ifBlank { "Değer Yatırımı" }
            val currentStyle = styleWinMap[style] ?: Pair(0, 0)
            val newWins = currentStyle.first + (if (isWin) 1 else 0)
            val newTotal = currentStyle.second + 1
            styleWinMap[style] = Pair(newWins, newTotal)
        }

        val total = entries.size
        val successRate = (successCount.toDouble() / total) * 100.0

        val sectorAvgMap = sectorPnLMap.mapValues { it.value.average() }
        val bestSector = sectorAvgMap.maxByOrNull { it.value }?.key ?: "Genel"
        val worstSector = sectorAvgMap.minByOrNull { it.value }?.key ?: "Genel"

        val styleRates = styleWinMap.mapValues { (it.value.first.toDouble() / it.value.second) * 100.0 }
        val bestStyle = styleRates.maxByOrNull { it.value }?.key ?: "Değer Yatırımı"

        val sb = StringBuilder()
        sb.append("### 📓 YATIRIM KARAR GÜNLÜĞÜ İSTATİSTİKLERİ\n\n")
        sb.append("• **Toplam Alınan Karar:** ").append(total).append("\n")
        sb.append("• **Başarı Oranı:** %").append(String.format(Locale.US, "%.1f", successRate)).append(" (").append(successCount).append(" Doğru / ").append(failCount).append(" Hatalı)\n")
        sb.append("• **En Başarılı Sektör:** ").append(bestSector).append("\n")
        sb.append("• **En Başarısız Sektör:** ").append(worstSector).append("\n")
        sb.append("• **En Başarılı Yatırım Tarzı:** ").append(bestStyle).append(" (%").append(String.format(Locale.US, "%.1f", styleRates[bestStyle] ?: 0.0)).append(" Başarı)\n\n")

        sb.append("*Tüm karar notlarınız ve istatistikleriniz cihazınızda %100 gizli ve size özel tutulmaktadır.*")

        return JournalStatistics(
            totalEntries = total,
            successRatePercent = successRate,
            successfulCount = successCount,
            failedCount = failCount,
            mostSuccessfulSector = bestSector,
            leastSuccessfulSector = worstSector,
            mostSuccessfulStyle = bestStyle,
            styleSuccessRates = styleRates,
            overallSummaryMarkdown = sb.toString()
        )
    }
}
