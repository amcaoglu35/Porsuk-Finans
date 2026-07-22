package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.Company
import java.util.Locale
import kotlin.math.sqrt

/**
 * AI Portfolio Doctor Engine for Porsuk Finans.
 * Performs local Kotlin clinical computations for 9 portfolio risk metrics:
 * 1. Sektör dağılımı
 * 2. Ülke / Bölge dağılımı
 * 3. Para birimi riski
 * 4. Volatilite
 * 5. Ortalama maliyet riski
 * 6. Yoğunlaşma riski
 * 7. Temettü oranı
 * 8. Büyüme hissesi oranı
 * 9. Savunmacı hisse oranı
 */
data class PortfolioDoctorMetrics(
    val healthScore: Int,
    val sectorBreakdown: Map<String, Double>,
    val countryBreakdown: Map<String, Double>,
    val currencyRisk: String,
    val volatilityPercent: Double,
    val costRisk: String,
    val concentrationRisk: String,
    val dividendRatioPercent: Double,
    val growthRatioPercent: Double,
    val defensiveRatioPercent: Double,
    val diagnosisSummary: String
)

object PortfolioDoctorEngine {

    fun analyze(
        holdings: List<BasketItem>,
        companies: List<Company>
    ): PortfolioDoctorMetrics {
        if (holdings.isEmpty()) {
            return PortfolioDoctorMetrics(
                healthScore = 0,
                sectorBreakdown = emptyMap(),
                countryBreakdown = emptyMap(),
                currencyRisk = "Düşük (Portföy Boş)",
                volatilityPercent = 0.0,
                costRisk = "Veri Yok",
                concentrationRisk = "Veri Yok",
                dividendRatioPercent = 0.0,
                growthRatioPercent = 0.0,
                defensiveRatioPercent = 0.0,
                diagnosisSummary = "Portföyde henüz varlık bulunmuyor."
            )
        }

        val companyMap = companies.associateBy { it.symbol }
        
        var totalValue = 0.0
        var totalCost = 0.0
        val sectorValues = mutableMapOf<String, Double>()
        val countryValues = mutableMapOf<String, Double>()
        val currencyValues = mutableMapOf<String, Double>()

        var growthValue = 0.0
        var defensiveValue = 0.0
        var dividendValue = 0.0

        var maxStockValue = 0.0
        var maxStockSymbol = ""

        holdings.forEach { item ->
            val comp = companyMap[item.symbol]
            val market = comp?.market?.uppercase() ?: "BIST"
            val sector = comp?.sector ?: "Diğer"
            val price = comp?.currentPrice ?: item.buyPrice
            val valNative = item.quantity * price
            val costNative = item.quantity * item.buyPrice

            val rate = when (market) {
                "NASDAQ", "NYSE" -> 34.0 // Approximate rate for metrics
                "FRA", "EURONEXT" -> 37.0
                else -> 1.0
            }

            val itemValueTry = valNative * rate
            val itemCostTry = costNative * rate

            totalValue += itemValueTry
            totalCost += itemCostTry

            sectorValues[sector] = (sectorValues[sector] ?: 0.0) + itemValueTry

            val countryLabel = when (market) {
                "NASDAQ", "NYSE" -> "ABD (US)"
                "FRA", "EURONEXT" -> "Avrupa (EU)"
                else -> "Türkiye (BIST)"
            }
            countryValues[countryLabel] = (countryValues[countryLabel] ?: 0.0) + itemValueTry

            val currencyLabel = when (market) {
                "NASDAQ", "NYSE" -> "USD"
                "FRA", "EURONEXT" -> "EUR"
                else -> "TRY"
            }
            currencyValues[currencyLabel] = (currencyValues[currencyLabel] ?: 0.0) + itemValueTry

            if (itemValueTry > maxStockValue) {
                maxStockValue = itemValueTry
                maxStockSymbol = item.symbol
            }

            // Categorize asset profile
            val lowerSec = sector.lowercase()
            when {
                lowerSec.contains("teknoloji") || lowerSec.contains("yazılım") || lowerSec.contains("enerji") || lowerSec.contains("savunma") -> {
                    growthValue += itemValueTry
                }
                lowerSec.contains("gıda") || lowerSec.contains("perakende") || lowerSec.contains("sağlık") || lowerSec.contains("ilaç") || lowerSec.contains("iletişim") -> {
                    defensiveValue += itemValueTry
                }
                else -> {
                    dividendValue += itemValueTry
                }
            }
        }

        if (totalValue <= 0) totalValue = 1.0

        // 1. Sektör Dağılımı %
        val sectorPct = sectorValues.mapValues { (it.value / totalValue) * 100.0 }
        
        // 2. Ülke Dağılımı %
        val countryPct = countryValues.mapValues { (it.value / totalValue) * 100.0 }

        // 3. Para Birimi Riski
        val tryPct = currencyValues.getOrDefault("TRY", 0.0) / totalValue * 100.0
        val foreignPct = 100.0 - tryPct
        val currencyRisk = when {
            tryPct >= 85.0 -> "YÜKSEK TL YENİLEME RİSKİ (%${String.format(Locale.US, "%.1f", tryPct)} TL Ağırlıklı)"
            foreignPct >= 70.0 -> "YÜKSEK DÖVİZ VOLATİLİTESİ (%${String.format(Locale.US, "%.1f", foreignPct)} Döviz Varlığı)"
            else -> "DENGELİ DÖVİZ/TL DAĞILIMI (Döviz: %${String.format(Locale.US, "%.1f", foreignPct)})"
        }

        // 4. Volatilite tahmini
        val volPercent = if (growthValue / totalValue > 0.5) 32.5 else 21.0

        // 5. Ortalama Maliyet Riski
        val pnlPct = if (totalCost > 0) (totalValue - totalCost) / totalCost * 100.0 else 0.0
        val costRisk = when {
            pnlPct <= -20.0 -> "YÜKSEK MALİYET ZARAR RİSKİ (%${String.format(Locale.US, "%.1f", pnlPct)})"
            pnlPct >= 30.0 -> "GÜÇLÜ KÂR KORUMASI (%${String.format(Locale.US, "%.1f", pnlPct)} Kazanç)"
            else -> "MAKUL DENGELİ MALİYET (%${String.format(Locale.US, "%.1f", pnlPct)})"
        }

        // 6. Yoğunlaşma Riski (Concentration Risk)
        val maxStockPct = (maxStockValue / totalValue) * 100.0
        val concentrationRisk = when {
            maxStockPct >= 40.0 -> "YÜKSEK YOĞUNLAŞMA RİSKİ ($maxStockSymbol Tek Başına %${String.format(Locale.US, "%.1f", maxStockPct)})"
            maxStockPct >= 25.0 -> "ORTA YOĞUNLAŞMA RİSKİ ($maxStockSymbol %${String.format(Locale.US, "%.1f", maxStockPct)})"
            else -> "DENGELİ ÇEŞİTLENDİRME (Maks Hisse: %${String.format(Locale.US, "%.1f", maxStockPct)})"
        }

        // 7, 8, 9. Oranlar
        val growthRatio = (growthValue / totalValue) * 100.0
        val defensiveRatio = (defensiveValue / totalValue) * 100.0
        val dividendRatio = (dividendValue / totalValue) * 100.0

        // Health Score Calculation (0-100)
        var score = 100
        if (maxStockPct >= 40.0) score -= 20
        else if (maxStockPct >= 25.0) score -= 10

        val maxSectorPct = sectorPct.values.maxOrNull() ?: 0.0
        if (maxSectorPct >= 50.0) score -= 15
        else if (maxSectorPct >= 35.0) score -= 8

        if (holdings.size == 1) score -= 20
        if (pnlPct <= -25.0) score -= 10
        if (defensiveRatio < 10.0) score -= 5

        score = score.coerceIn(15, 98)

        // Generate Clinical Summary String for Gemini Prompt
        val sb = StringBuilder()
        sb.append("=== AI PORTFOLIO DOCTOR KLİNİK TEŞHİS RAPORU ===\n")
        sb.append("• HESAPLANAN PORTFÖY SAĞLIK SKORU: ").append(score).append(" / 100\n")
        sb.append("• Sektör Dağılımı: ").append(sectorPct.entries.joinToString { "${it.key}: %${String.format(Locale.US, "%.1f", it.value)}" }).append("\n")
        sb.append("• Ülke/Bölge Dağılımı: ").append(countryPct.entries.joinToString { "${it.key}: %${String.format(Locale.US, "%.1f", it.value)}" }).append("\n")
        sb.append("• Para Birimi Riski: ").append(currencyRisk).append("\n")
        sb.append("• Portföy Volatilitesi: %").append(String.format(Locale.US, "%.1f", volPercent)).append("\n")
        sb.append("• Ortalama Maliyet Durumu: ").append(costRisk).append("\n")
        sb.append("• Yoğunlaşma Riski: ").append(concentrationRisk).append("\n")
        sb.append("• Büyüme Hissesi Oranı: %").append(String.format(Locale.US, "%.1f", growthRatio)).append("\n")
        sb.append("• Savunmacı Hisse Oranı: %").append(String.format(Locale.US, "%.1f", defensiveRatio)).append("\n")
        sb.append("• Temettü/Değer Hissesi Oranı: %").append(String.format(Locale.US, "%.1f", dividendRatio)).append("\n")
        sb.append("===================================================\n")

        return PortfolioDoctorMetrics(
            healthScore = score,
            sectorBreakdown = sectorPct,
            countryBreakdown = countryPct,
            currencyRisk = currencyRisk,
            volatilityPercent = volPercent,
            costRisk = costRisk,
            concentrationRisk = concentrationRisk,
            dividendRatioPercent = dividendRatio,
            growthRatioPercent = growthRatio,
            defensiveRatioPercent = defensiveRatio,
            diagnosisSummary = sb.toString()
        )
    }
}
