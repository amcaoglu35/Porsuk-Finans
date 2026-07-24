package com.nexus.porsuk.domain.model

/**
 * Optimizasyon Stratejisi Türü (OptimizationStrategyType)
 */
enum class OptimizationStrategyType(val displayName: String, val iconEmoji: String) {
    MEAN_VARIANCE("Markowitz Mean-Variance", "📊"),
    MINIMUM_VARIANCE("Minimum Varyans (Min Risk)", "🛡️"),
    MAXIMUM_SHARPE("Maksimum Sharpe Oranı (Optimal)", "🎯"),
    MAXIMUM_RETURN("Maksimum Getiri Odaklı", "🚀"),
    RISK_PARITY("Risk Paritesi (Risk Parity)", "⚖️"),
    EQUAL_WEIGHT("Eşit Ağırlıklı (1/N)", "🧩"),
    EQUAL_RISK_CONTRIBUTION("Eşit Risk Katkısı (ERC)", "⚖️"),
    BLACK_LITTERMAN("Black-Litterman Model", "🔮");
}

/**
 * Varlık Sınıfı Türü (AssetClassType)
 */
enum class AssetClassType(val displayName: String, val iconEmoji: String) {
    STOCKS("Hisse Senetleri", "📈"),
    ETFS("Borsa Yatırım Fonları (ETF)", "🧺"),
    MUTUAL_FUNDS("Yatırım Fonları", "🏛️"),
    BONDS("Devlet / Özel Sektör Tahvilleri", "📜"),
    COMMODITIES("Emtia & Enerji", "🛢️"),
    GOLD("Altın & Kıymetli Madenler", "🪙"),
    CRYPTO("Kripto Varlıklar", "₿"),
    CASH("Nakit & Likit Fonlar", "💵");
}

/**
 * Varlık Dağılım Övesi (AssetAllocationItem)
 */
data class AssetAllocationItem(
    val symbol: String = "THYAO.IS",
    val name: String = "Türk Hava Yolları",
    val assetClass: AssetClassType = AssetClassType.STOCKS,
    val currentWeightPct: Double = 35.0,
    val targetOptimizedWeightPct: Double = 22.5,
    val expectedReturnPct: Double = 28.5,
    val volatilityPct: Double = 18.2
)

/**
 * Portföy Optimizasyon Risk Metrikleri (PortfolioRiskMetrics)
 */
data class PortfolioRiskMetrics(
    val standardDeviationPct: Double = 14.8,
    val beta: Double = 1.05,
    val alpha: Double = 0.042,
    val sharpeRatio: Double = 1.94,
    val sortinoRatio: Double = 2.45,
    val treynorRatio: Double = 0.18,
    val maxDrawdownPct: Double = 11.2,
    val valueAtRiskVaR: Double = 4.25,  // %95 Güvenle 1 Günlük VaR
    val conditionalVaR: Double = 6.10,  // CVaR / Expected Shortfall
    val diversificationScore: Int = 86
)

/**
 * Etkin Sınır Noktası (EfficientFrontierPoint)
 */
data class EfficientFrontierPoint(
    val pointId: String = "pt_${System.currentTimeMillis()}",
    val expectedReturnPct: Double,
    val volatilityPct: Double,
    val isOptimalTangencyPoint: Boolean = false
)

/**
 * Yeniden Dengeleme Önerisi (RebalanceSuggestion)
 */
data class RebalanceSuggestion(
    val symbol: String = "THYAO.IS",
    val actionText: String = "Ağırlık %12.5 azaltılsın",
    val currentWeightPct: Double = 35.0,
    val targetWeightPct: Double = 22.5,
    val driftPct: Double = 12.5
)

/**
 * Stres Testi Senaryosu (StressTestScenario)
 */
data class StressTestScenario(
    val scenarioId: String = "sc_1",
    val name: String = "2008 Küresel Finansal Kriz Sıçraması",
    val category: String = "Tarihsel Şok",
    val expectedPortfolioChangePct: Double = -14.2
)

/**
 * Geleceğe Hazır Portföy Optimizasyon Stub Modeli (PortfolioOptimizationFutureStubs)
 */
data class PortfolioOptimizationFutureStubs(
    val isMultiObjectiveOptimizationReady: Boolean = true,
    val isEsgOptimizationActive: Boolean = true,
    val isFactorInvestingReady: Boolean = true,
    val isAiPortfolioOptimizerActive: Boolean = true,
    val isGoalBasedInvestingSupported: Boolean = false
)
