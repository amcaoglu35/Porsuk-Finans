package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.AiInsightEntry
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.local.entity.PorsukBrainMemory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AI Insight Engine for Porsuk Finans.
 * Proactively generates high-impact, cause-and-effect insights by analyzing user behavior patterns,
 * portfolio shifts, frequent stock analyses, and Porsuk Brain context.
 * 
 * Rules:
 * - Max 3 insights generated per day.
 * - Deduplication via dedupeKey (no duplicate insights).
 * - Powered by Porsuk Brain memory.
 * - Concise, explanatory cause-and-effect structure without financial advice (YTD).
 */
object AiInsightEngine {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun getTodayDateString(): String = dateFormat.format(Date())

    fun generateProactiveInsights(
        holdings: List<BasketItem>,
        companies: List<Company>,
        brainMemory: PorsukBrainMemory?,
        recentNews: List<NewsItemEntity> = emptyList(),
        existingTodayKeys: Set<String> = emptySet()
    ): List<AiInsightEntry> {
        val todayStr = getTodayDateString()
        val generated = mutableListOf<AiInsightEntry>()

        if (holdings.isEmpty() && brainMemory == null) return emptyList()

        val companyMap = companies.associateBy { it.symbol }

        // 1. INSIGHT 1: Sektörel Yoğunlaşma & Risk Dengesi (Cause & Effect)
        if (holdings.size >= 2) {
            var totalVal = 0.0
            val sectorVal = mutableMapOf<String, Double>()
            holdings.forEach { item ->
                val price = companyMap[item.symbol]?.currentPrice ?: item.buyPrice
                val itemVal = item.quantity * price
                totalVal += itemVal
                val sector = companyMap[item.symbol]?.sector ?: "Diğer"
                sectorVal[sector] = (sectorVal[sector] ?: 0.0) + itemVal
            }

            if (totalVal > 0) {
                val topSectorEntry = sectorVal.maxByOrNull { it.value }
                if (topSectorEntry != null) {
                    val topPct = (topSectorEntry.value / totalVal) * 100.0
                    if (topPct >= 50.0) {
                        val key = "INSIGHT_SECTOR_CONCENTRATION_$todayStr"
                        if (!existingTodayKeys.contains(key)) {
                            generated.add(
                                AiInsightEntry(
                                    title = "🛡️ Portföy Yoğunlaşma Analizi",
                                    message = "Portföy değerinin %${String.format(Locale.US, "%.0f", topPct)}'i ${topSectorEntry.key} sektöründe yoğunlaştı. Sektörel dalgalanmalardan korunmak adına varlık dağılımını gözden geçirmek isteyebilirsin.",
                                    category = "SECTOR_CONCENTRATION",
                                    dedupeKey = key
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. INSIGHT 2: Odak Hisse & Analiz Sıklığı (Porsuk Brain Context)
        val topSymbols = brainMemory?.topInterestedSymbols ?: holdings.firstOrNull()?.symbol ?: ""
        if (topSymbols.isNotBlank()) {
            val primarySymbol = topSymbols.split(",").firstOrNull()?.trim() ?: "ASELSAN"
            val comp = companyMap[primarySymbol]
            if (comp != null) {
                val key = "INSIGHT_FOCUS_STOCK_${primarySymbol}_$todayStr"
                if (!existingTodayKeys.contains(key)) {
                    val directionText = if (comp.changePercent >= 0) "primlenerek %${String.format(Locale.US, "%.1f", comp.changePercent)} yükseldi." else "göstergelerinde %${String.format(Locale.US, "%.1f", kotlin.math.abs(comp.changePercent))} geri çekilme yaşandı."
                    generated.add(
                        AiInsightEntry(
                            title = "💡 $primarySymbol Alışkanlık Analizi",
                            message = "Son dönemde sık takip ettiğin $primarySymbol hissesi bugün $directionText Değer yatırımı hedeflerin doğrultusunda teknik görünümünü inceleyebilirsin.",
                            category = "WATCHLIST_ACTIVITY",
                            relatedSymbol = primarySymbol,
                            dedupeKey = key
                        )
                    )
                }
            }
        }

        // 3. INSIGHT 3: Haber - Piyasa Nabzı Duyarlılığı
        val topNews = recentNews.firstOrNull { it.sentiment != null && it.sentiment != "NEUTRAL" }
        if (topNews != null) {
            val key = "INSIGHT_NEWS_IMPACT_${topNews.symbol}_$todayStr"
            if (!existingTodayKeys.contains(key)) {
                val newsSentimentText = if (topNews.sentiment == "POSITIVE") "pozitif duyarlılık taşıyor." else "piyasa tarafından temkinli karşılandı."
                generated.add(
                    AiInsightEntry(
                        title = "📰 ${topNews.symbol} Haber Nabzı",
                        message = "${topNews.symbol} için son yayınlanan haber (\"${topNews.title.take(40)}...\") $newsSentimentText Bu gelişmenin fiyatlamaya etkisini Orakul ile analiz edebilirsin.",
                        category = "NEWS_IMPACT",
                        relatedSymbol = topNews.symbol,
                        dedupeKey = key
                    )
                )
            }
        }

        return generated.take(3) // Strict Cap: Max 3 insights per day
    }
}
