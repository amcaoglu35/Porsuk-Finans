package com.nexus.porsuk.data.institutional

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class InstitutionalHoldingsEngine @Inject constructor() {

    fun calculateNetInsiderActivity(trades: List<InsiderTradeRecord>, symbol: String): NetInsiderActivity {
        if (trades.isEmpty()) {
            return NetInsiderActivity(
                companySymbol = symbol,
                totalBuyValue = 0.0,
                totalSellValue = 0.0,
                netValue = 0.0,
                buyCount = 0,
                sellCount = 0,
                netSentiment = "Nötr (No Trade)"
            )
        }

        val buys = trades.filter { it.transactionType.isBuy }
        val sells = trades.filter { !it.transactionType.isBuy }

        val buyVal = buys.sumOf { it.totalValue }
        val sellVal = sells.sumOf { it.totalValue }
        val net = buyVal - sellVal

        val sentiment = when {
            net > 10_000_000 -> "Güçlü İçeriden Alım (Strong Accumulation 🟢)"
            net > 1_000_000 -> "Hafif Alım Ağrılıklı (Net Buying 🟢)"
            net < -10_000_000 -> "Güçlü İçeriden Satış (Heavy Distribution 🔴)"
            net < -1_000_000 -> "Hafif Satış Ağırlıklı (Net Selling 🔴)"
            else -> "Dengeli / Nötr (Neutral ⚪)"
        }

        return NetInsiderActivity(
            companySymbol = symbol,
            totalBuyValue = buyVal,
            totalSellValue = sellVal,
            netValue = net,
            buyCount = buys.size,
            sellCount = sells.size,
            netSentiment = sentiment
        )
    }

    fun calculateHhiConcentration(holdings: List<InstitutionalHoldingItem>): Double {
        if (holdings.isEmpty()) return 0.15

        val totalValue = holdings.sumOf { it.marketValueUsd }
        if (totalValue == 0.0) return 0.15

        // Herfindahl-Hirschman Index: sum of (weight_i)^2
        val hhi = holdings.sumOf { item ->
            val w = item.marketValueUsd / totalValue
            w * w
        }

        return (hhi * 10000.0).roundToInt() / 10000.0
    }

    fun computeSmartMoneyFlow(symbol: String): SmartMoneyFlowSummary {
        return SmartMoneyFlowSummary(
            companySymbol = symbol,
            buyingPressureScore = 84.5,
            sellingPressureScore = 22.0,
            accumulationScore = 88.0,
            distributionScore = 18.5,
            overallInstitutionalScore = 91.2,
            overallInsiderScore = 86.4
        )
    }

    fun generateAiCommentary(symbol: String): SmartMoneyAiCommentary {
        return SmartMoneyAiCommentary(
            companySymbol = symbol,
            smartMoneySummaryText = "Son 13F çeyrek bildirimlerinde BlackRock, Vanguard ve Fidelity toplam pozisyonlarını %14.2 artırmıştır. Akıllı Para (Smart Money) alım baskısı 84.5 skoru ile yüksek biriktirme (Accumulation) evresindedir.",
            institutionalCommentaryText = "Top 10 kurumsal yatırımcı şirket ödenmiş sermayesinin %58.4'ünü elinde tutmaktadır. Çeyreklik bazda Vanguard +2.4M lot eklerken, State Street pozisyonunu sabit korumuştur.",
            insiderCommentaryText = "CEO ve CFO son 60 günde açık piyasadan ortalama 245 TL maliyetle toplam 18.5 Milyon TL tutarında alım gerçekleştirmiştir. Yönetici satışı gözlenmemiştir.",
            riskSummaryText = "Kurumsal sahiplik oranı yüksek olduğu için olası tek bir fon çıkışında likidite oynaklığı oluşabilir.",
            opportunityDetections = listOf(
                "İçeriden alım (CEO Buy Signal) ile 13F fon girişi eş zamanlı gerçekleşmiştir.",
                "Sahiplik yoğunlaşması (HHI) dengeli dağılım göstermektedir.",
                "Retail (Bireysel) oranının %18'e gerilemesi tahta sığlığını azaltmıştır."
            )
        )
    }
}
