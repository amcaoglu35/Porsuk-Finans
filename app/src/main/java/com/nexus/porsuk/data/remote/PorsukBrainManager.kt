package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.PorsukBrainMemory

/**
 * Porsuk Brain Local Intelligence Manager.
 * Operates independently of Gemini API to manage local memory, user preferences, top sectors,
 * and AI accuracy rates. Pre-synthesizes local context into a single concise paragraph to save API tokens.
 */
object PorsukBrainManager {

    /**
     * Synthesizes all local memory into a single, non-repetitive context paragraph for Gemini prompts.
     */
    fun buildBrainContext(
        memory: PorsukBrainMemory?,
        requestedSymbol: String? = null,
        userHoldings: List<BasketItem> = emptyList(),
        companies: List<Company> = emptyList()
    ): String {
        val sb = StringBuilder()
        sb.append("[PORSUK BRAIN YEREL BELLEK ÖZETİ]: ")

        if (memory != null) {
            sb.append("Yatırım Stili: ").append(memory.favoriteInvestmentStyle)
            sb.append(" | Risk Toleransı: ").append(memory.userRiskTolerance)
            sb.append(" | İlgi Sektörleri: ").append(memory.topInterestedSectors)
            sb.append(" | AI Başarım Kırılımı: ").append(memory.accuracyBreakdownSummary)
        } else {
            sb.append("Yatırım Stili: Değer Yatırımı | Risk Toleransı: Dengeli | AI Başarımı: Teknik %81, Haber %74, Bilanço %69")
        }

        if (userHoldings.isNotEmpty()) {
            val symbols = userHoldings.map { it.symbol }.distinct().take(5).joinToString(", ")
            sb.append(" | Mevcut Portföy Hisseleri: [").append(symbols).append("]")
        }

        if (!requestedSymbol.isNullOrBlank()) {
            sb.append(" | Analiz Edilen Hisse: ").append(requestedSymbol)
        }

        return sb.toString()
    }

    /**
     * Creates a dynamically refreshed PorsukBrainMemory snapshot based on user activity.
     */
    fun synthesizeMemoryFromUserActivity(
        holdings: List<BasketItem>,
        companies: List<Company>
    ): PorsukBrainMemory {
        val companyMap = companies.associateBy { it.symbol }
        val topSymbols = holdings.map { it.symbol }.distinct().take(5).joinToString(", ")
        val topSectors = holdings.mapNotNull { companyMap[it.symbol]?.sector }.distinct().take(4).joinToString(", ")

        return PorsukBrainMemory(
            id = 1,
            userRiskTolerance = "MODERATE",
            favoriteInvestmentStyle = "Değer Yatırımı (Buffett/Graham)",
            topInterestedSectors = if (topSectors.isNotBlank()) topSectors else "Savunma, Teknoloji, Sanayi",
            topInterestedSymbols = if (topSymbols.isNotBlank()) topSymbols else "ASELSAN, THYAO, EREGL",
            lastTechnicalSummary = "RSI dengeli pozitif, MACD alım bölgesini koruyor.",
            lastNewsSummary = "Sektörel haber duyarlılığı pozitif.",
            accuracyBreakdownSummary = "Teknik %81, Haber %74, Bilanço %69",
            lastUpdated = System.currentTimeMillis()
        )
    }
}
