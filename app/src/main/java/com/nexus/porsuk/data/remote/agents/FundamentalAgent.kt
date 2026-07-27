package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company
import java.util.Locale

import com.nexus.porsuk.domain.model.AiAgentType

/**
 * Fundamental Analysis Agent for Porsuk Finans.
 * Evaluates company balance sheet, valuation (F/K), 52-week high/low, dividend yield, and sector positioning.
 */
class FundamentalAgent : BaseAgent() {
    override val agentName: String = "Fundamental Agent"
    override val agentType: AiAgentType = AiAgentType.FUNDAMENTAL

    override suspend fun runAnalysis(request: AgentRequest): String {
        val symbol = request.symbol
        val company = request.companies.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
        val info = request.companyInfos.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }

        if (company == null && info == null) {
            return "Temel Analiz: Detaylı bilanço verisi bulunamadı. Genel finansal durum Nötr."
        }

        val pe = info?.peRatio ?: 15.0
        val divYield = info?.dividendYield ?: 0.0
        val currentPrice = company?.currentPrice ?: 0.0
        val low52 = info?.week52Low ?: 0.0
        val high52 = info?.week52High ?: 0.0

        var score = 50.0
        val details = mutableListOf<String>()

        // 1. Valuation (F/K)
        if (pe > 0 && pe < 12) {
            score += 15
            details.add("F/K Oranı cazip seviyede (${String.format(Locale.US, "%.1f", pe)})")
        } else if (pe >= 25) {
            score -= 15
            details.add("F/K Oranı yüksek/primli (${String.format(Locale.US, "%.1f", pe)})")
        }

        // 2. Dividend Yield (Temettü Verimi)
        if (divYield > 5.0) {
            score += 10
            details.add("Temettü Verimi güçlü (%${String.format(Locale.US, "%.1f", divYield)})")
        }

        // 3. 52-Week Range Positioning
        if (high52 > low52 && currentPrice > 0) {
            val rangePosPct = ((currentPrice - low52) / (high52 - low52)) * 100.0
            if (rangePosPct < 30.0) {
                score += 10
                details.add("52 haftalık dip seviyelerine yakın (%${String.format(Locale.US, "%.0f", rangePosPct)} bandında)")
            } else if (rangePosPct > 85.0) {
                score -= 5
                details.add("52 haftalık zirve seviyelerinde (%${String.format(Locale.US, "%.0f", rangePosPct)} bandında)")
            }
        }

        val finalScore = score.coerceIn(0.0, 100.0).toInt()
        val detailsStr = if (details.isNotEmpty()) details.joinToString(", ") else "Finansal rasyolar ortalama seviyelerde"

        return "Temel Analiz Puanı: $finalScore/100 | Sektörel Değerleme & Temettü: $detailsStr."
    }
}
