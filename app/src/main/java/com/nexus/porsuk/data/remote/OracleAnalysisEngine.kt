package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.ui.orakul.OrakulDecision
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// 17-Metric Portfolio Report — Oracle 2.0
// ─────────────────────────────────────────────────────────────────────────────

data class OraclePortfolioReport(
    // 1. Beklenen Yıllık Getiri
    val expectedAnnualReturnPct: Double,
    // 2. Beklenen Volatilite
    val expectedVolatilityPct: Double,
    // 3. Risk Seviyesi
    val riskLevel: String,           // "DÜŞÜK", "ORTA", "YÜKSEK"
    // 4. Sharpe Skoru
    val sharpeScore: Double,
    // 5. Maksimum Drawdown Tahmini
    val maxDrawdownPct: Double,
    // 6. Sektör Dağılımı (Sektör → Ağırlık%)
    val sectorAllocation: Map<String, Double>,
    // 7. Portföy Çeşitlilik Puanı (0-100)
    val diversificationScore: Int,
    // 8. Likidite Puanı (0-100)
    val liquidityScore: Int,
    // 9. Temettü Puanı (0-100)
    val dividendScore: Int,
    // 10. Büyüme Puanı (0-100)
    val growthScore: Int,
    // 11. Defansiflik Puanı (0-100)
    val defensivenessScore: Int,
    // 12. Yapay Zekâ Açıklaması
    val aiExplanation: String,
    // 13. En İyi Senaryo
    val bestCaseScenario: String,
    // 14. Normal Senaryo
    val baseScenario: String,
    // 15. Kötü Senaryo
    val worstCaseScenario: String,
    // 16. Risk Uyarıları
    val riskWarnings: List<String>,
    // 17. Portföy Sağlık Skoru (0-100)
    val healthScore: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// OracleAnalysisEngine — Pure Kotlin, 0 AI Token Cost
// ─────────────────────────────────────────────────────────────────────────────

object OracleAnalysisEngine {

    private val DEFENSIVE_SECTORS = setOf(
        "Sağlık", "Ilaç", "Gıda", "Enerji", "Temettü",
        "Healthcare", "Utilities", "Consumer Staples", "Pharma"
    )

    private val GROWTH_SECTORS = setOf(
        "Teknoloji", "Yazılım", "Bilişim", "Technology",
        "Semiconductors", "Cloud", "AI", "E-Ticaret"
    )

    /**
     * Analyzes the generated basket (list of OrakulDecisions) and returns a full 17-metric report.
     * All calculations are done locally in Kotlin — no AI tokens consumed.
     */
    fun analyze(
        decisions: List<OrakulDecision>,
        companies: List<Company>,
        infos: List<CachedCompanyInfo>,
        usdRate: Double = 34.5
    ): OraclePortfolioReport {
        if (decisions.isEmpty()) return emptyReport()

        val companyMap = companies.associateBy { it.symbol.uppercase() }
        val infoMap = infos.associateBy { it.symbol.uppercase() }

        // ── METRIC 1 & 2: Expected Return & Volatility ───────────────────────
        // Based on weighted confidence scores and RSI-based momentum
        val totalWeight = decisions.sumOf { it.weight.coerceAtLeast(1.0) }
        val weightedConfidence = decisions.sumOf {
            val w = it.weight.coerceAtLeast(1.0)
            w * it.confidence
        } / totalWeight

        val avgRsi = decisions.map { it.rsi }.average()

        // RSI momentum factor: RSI 60-70 = strong positive, RSI 30-40 = negative
        val rsiMomentumFactor = when {
            avgRsi >= 65 -> 1.15
            avgRsi >= 55 -> 1.05
            avgRsi >= 45 -> 1.00
            avgRsi >= 35 -> 0.90
            else         -> 0.80
        }

        val baseReturnPct = ((weightedConfidence - 50.0) * 0.7) * rsiMomentumFactor
        val expectedAnnualReturnPct = baseReturnPct.coerceIn(-20.0, 60.0)

        // Golden Cross count boosts return
        val goldenCrossCount = decisions.count { it.crossSignal == "GOLDEN_CROSS" }
        val deathCrossCount = decisions.count { it.crossSignal == "DEATH_CROSS" }

        // Volatility: Based on confidence dispersion + cross signals
        val confidences = decisions.map { it.confidence.toDouble() }
        val confVariance = if (confidences.size > 1) {
            val mean = confidences.average()
            confidences.sumOf { (it - mean) * (it - mean) } / confidences.size
        } else 0.0
        val baseVol = 18.0 + sqrt(confVariance) * 0.4 + deathCrossCount * 2.0 - goldenCrossCount * 1.0
        val expectedVolatilityPct = baseVol.coerceIn(8.0, 55.0)

        // ── METRIC 3: Risk Level ──────────────────────────────────────────────
        val riskLevel = when {
            expectedVolatilityPct >= 32.0 -> "YÜKSEK"
            expectedVolatilityPct >= 20.0 -> "ORTA"
            else                          -> "DÜŞÜK"
        }

        // ── METRIC 4: Sharpe Score ────────────────────────────────────────────
        val riskFreeRate = 14.0 // TCMB faiz (yaklaşık reel getiri baz)
        val sharpeScore = if (expectedVolatilityPct > 0) {
            (expectedAnnualReturnPct - riskFreeRate) / expectedVolatilityPct
        } else 0.0

        // ── METRIC 5: Max Drawdown Estimate ──────────────────────────────────
        val drawdownMultiplier = when (riskLevel) {
            "YÜKSEK" -> 0.65
            "ORTA"   -> 0.45
            else     -> 0.28
        }
        val maxDrawdownPct = (expectedVolatilityPct * drawdownMultiplier).coerceIn(5.0, 50.0)

        // ── METRIC 6: Sector Allocation ───────────────────────────────────────
        val sectorMap = mutableMapOf<String, Double>()
        val equalWeight = 100.0 / decisions.size
        decisions.forEach { decision ->
            val company = companyMap[decision.symbol.uppercase()]
            val sector = company?.sector?.ifBlank { "Diğer" } ?: "Diğer"
            val weight = if (decision.weight > 0) decision.weight else equalWeight
            sectorMap[sector] = (sectorMap[sector] ?: 0.0) + weight
        }
        val sectorAllocation: Map<String, Double> = sectorMap.entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }

        // ── METRIC 7: Diversification Score ──────────────────────────────────
        val uniqueSectors = sectorAllocation.keys.size
        val stockCount = decisions.size
        val rawDiversification = ((uniqueSectors.toDouble() / stockCount.coerceAtLeast(1)) * 100.0)
        val stockCountBonus = when {
            stockCount >= 8 -> 20
            stockCount >= 5 -> 10
            else -> 0
        }
        val diversificationScore = (rawDiversification + stockCountBonus).toInt().coerceIn(0, 100)

        // ── METRIC 8: Liquidity Score ─────────────────────────────────────────
        // Uses marketCap and volume strings as proxy (large cap = high liquidity)
        val liquidityScore = run {
            var score = 50
            decisions.forEach { d ->
                val info = infoMap[d.symbol.uppercase()]
                val capStr = info?.marketCap?.uppercase() ?: ""
                when {
                    capStr.contains("B") || capStr.contains("MİLYAR") -> score += 5
                    capStr.contains("M") || capStr.contains("MİLYON") -> score += 2
                }
            }
            score.coerceIn(0, 100)
        }

        // ── METRIC 9: Dividend Score ──────────────────────────────────────────
        val avgDivYield = decisions.map { d ->
            infoMap[d.symbol.uppercase()]?.dividendYield ?: 0.0
        }.average()
        val dividendScore = when {
            avgDivYield >= 6.0 -> 90
            avgDivYield >= 4.0 -> 75
            avgDivYield >= 2.0 -> 55
            avgDivYield >= 0.5 -> 35
            else -> 15
        }

        // ── METRIC 10: Growth Score ───────────────────────────────────────────
        val growthSectorWeight = sectorAllocation.entries
            .filter { GROWTH_SECTORS.any { gs -> it.key.contains(gs, ignoreCase = true) } }
            .sumOf { it.value }
        val growthMomentum = goldenCrossCount * 8.0 + (avgRsi - 50.0) * 0.6
        val growthScore = (40.0 + growthSectorWeight * 0.4 + growthMomentum).toInt().coerceIn(0, 100)

        // ── METRIC 11: Defensiveness Score ───────────────────────────────────
        val defensiveSectorWeight = sectorAllocation.entries
            .filter { DEFENSIVE_SECTORS.any { ds -> it.key.contains(ds, ignoreCase = true) } }
            .sumOf { it.value }
        val defensivenessScore = (defensiveSectorWeight * 0.8 + dividendScore * 0.2).toInt().coerceIn(0, 100)

        // ── METRIC 17: Health Score (composite) ──────────────────────────────
        val healthScore = ((diversificationScore * 0.20 +
                liquidityScore * 0.15 +
                dividendScore * 0.10 +
                growthScore * 0.15 +
                defensivenessScore * 0.10 +
                weightedConfidence * 0.30).toInt()).coerceIn(0, 100)

        // ── METRIC 12: AI Explanation ─────────────────────────────────────────
        val topSector = sectorAllocation.entries.firstOrNull()?.key ?: "Karma"
        val alCount = decisions.count { it.decision == "AL" }
        val aiExplanation = buildString {
            append("Bu ${stockCount} hisselik Oracle 2.0 sepeti, ")
            append("ağırlıklı olarak ${topSector} sektörüne odaklanmakta ve ")
            append("${alCount}/${stockCount} hisse için AL sinyali üretilmiştir. ")
            if (goldenCrossCount > 0) append("$goldenCrossCount hissede Altın Kesişim tespit edildi — güçlü teknik sinyal. ")
            append("Sharpe oranı ${String.format(Locale.US, "%.2f", sharpeScore)} ile ")
            append(if (sharpeScore >= 1.0) "risk-getiri dengesi oldukça sağlıklı." else "risk-getiri dengesi geliştirilmeye açık.")
            append(" Portföy Sağlık Skoru: $healthScore/100.")
        }

        // ── METRIC 13-15: Scenarios ───────────────────────────────────────────
        val bestCasePct = expectedAnnualReturnPct * 1.6
        val worstCasePct = expectedAnnualReturnPct * 0.3 - maxDrawdownPct * 0.5
        val bestCaseScenario = "Piyasa rüzgarı arkada: +%${String.format(Locale.US, "%.1f", bestCasePct)} yıllık getiri senaryosu. " +
                "Golden Cross sinyalleri ivme kazandırırsa $topSector liderlik eder."
        val baseScenario = "Baz durum: +%${String.format(Locale.US, "%.1f", expectedAnnualReturnPct)} yıllık beklenti. " +
                "Makro ortam stabil kalırsa Sharpe ${String.format(Locale.US, "%.2f", sharpeScore)} korunur."
        val worstCaseScenario = "Stres senaryosu: %${String.format(Locale.US, "%.1f", abs(worstCasePct))} düşüş riski. " +
                "Max Drawdown %${String.format(Locale.US, "%.1f", maxDrawdownPct)} olarak tahmin ediliyor. Nakit rezervi kritik."

        // ── METRIC 16: Risk Warnings ──────────────────────────────────────────
        val riskWarnings = mutableListOf<String>()
        if (deathCrossCount > 0) riskWarnings.add("⚠️ $deathCrossCount hissede Ölüm Kesişimi (DEATH_CROSS) tespit edildi.")
        if (uniqueSectors <= 2) riskWarnings.add("⚠️ Sektör konsantrasyonu yüksek — $uniqueSectors farklı sektör.")
        if (expectedVolatilityPct >= 35.0) riskWarnings.add("⚠️ Yüksek volatilite: %${String.format(Locale.US, "%.1f", expectedVolatilityPct)}.")
        if (sharpeScore < 0.5) riskWarnings.add("⚠️ Sharpe oranı düşük (${String.format(Locale.US, "%.2f", sharpeScore)}) — risk-getiri dengesi zayıf.")
        if (avgDivYield < 1.0 && defensivenessScore < 30) riskWarnings.add("⚠️ Defansif koruma yetersiz — piyasa düzeltmesine karşı kırılgan.")
        if (riskWarnings.isEmpty()) riskWarnings.add("✅ Kritik risk bayrağı tespit edilmedi.")

        return OraclePortfolioReport(
            expectedAnnualReturnPct = expectedAnnualReturnPct,
            expectedVolatilityPct = expectedVolatilityPct,
            riskLevel = riskLevel,
            sharpeScore = sharpeScore,
            maxDrawdownPct = maxDrawdownPct,
            sectorAllocation = sectorAllocation,
            diversificationScore = diversificationScore,
            liquidityScore = liquidityScore,
            dividendScore = dividendScore,
            growthScore = growthScore,
            defensivenessScore = defensivenessScore,
            aiExplanation = aiExplanation,
            bestCaseScenario = bestCaseScenario,
            baseScenario = baseScenario,
            worstCaseScenario = worstCaseScenario,
            riskWarnings = riskWarnings,
            healthScore = healthScore
        )
    }

    private fun emptyReport() = OraclePortfolioReport(
        expectedAnnualReturnPct = 0.0,
        expectedVolatilityPct = 0.0,
        riskLevel = "ORTA",
        sharpeScore = 0.0,
        maxDrawdownPct = 0.0,
        sectorAllocation = emptyMap(),
        diversificationScore = 0,
        liquidityScore = 0,
        dividendScore = 0,
        growthScore = 0,
        defensivenessScore = 0,
        aiExplanation = "Analiz için sepet verisi bekleniyor.",
        bestCaseScenario = "-",
        baseScenario = "-",
        worstCaseScenario = "-",
        riskWarnings = listOf("Veri yetersiz."),
        healthScore = 0
    )
}
