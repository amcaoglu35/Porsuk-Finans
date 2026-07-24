package com.nexus.porsuk.domain.model

/**
 * Türev Ürün Veri Sağlayıcı Türü (DerivativesProviderType)
 */
enum class DerivativesProviderType(val displayName: String, val iconEmoji: String) {
    OCC("OCC Options Clearing Corporation", "🇺🇸"),
    OPRA("OPRA Options Price Reporting Authority", "📊"),
    CBOE("CBOE Chicago Board Options Exchange", "📈"),
    CME("CME Chicago Mercantile Exchange", "🌾"),
    VIOP_TURKEY("Borsa İstanbul VİOP (VOB)", "🇹🇷");
}

/**
 * Türev Varlık Türü (DerivativesAssetType)
 */
enum class DerivativesAssetType(val displayName: String) {
    EQUITY_OPTION("Hisse Opsiyonu (Equity Option)"),
    ETF_OPTION("ETF Opsiyonu"),
    INDEX_OPTION("Endeks Opsiyonu (Index Option)"),
    FUTURES("Vadeli İşlem Sözleşmesi (Futures)"),
    MINI_FUTURES("Mini Vadeli İşlem"),
    VIOP_CONTRACT("VİOP Vadeli / Opsiyon Sözleşmesi");
}

/**
 * Opsiyon Türü (OptionType)
 */
enum class OptionType(val code: String) {
    CALL("Call (Alım)"),
    PUT("Put (Satım)");
}

/**
 * Fiyatlama Modeli (OptionPricingModel)
 */
enum class OptionPricingModel(val displayName: String) {
    BLACK_SCHOLES("Black-Scholes (1973)"),
    BLACK_76("Black-76 Futures Model"),
    BINOMIAL("Binomial Tree (Cox-Ross-Rubinstein)"),
    MONTE_CARLO("Monte Carlo Simulation");
}

/**
 * Opsiyon Sözleşmesi (OptionContract)
 */
data class OptionContract(
    val optionId: String = "opt_${System.currentTimeMillis()}",
    val symbol: String = "THYAO_C_350_202608",
    val underlyingSymbol: String = "THYAO.IS",
    val type: OptionType = OptionType.CALL,
    val strikePrice: Double = 350.0,
    val expirationDate: String = "21 Ağustos 2026",
    val bid: Double = 14.50,
    val ask: Double = 14.80,
    val lastPrice: Double = 14.65,
    val volume: Long = 4850L,
    val openInterest: Long = 18400L,
    val impliedVolatility: Double = 0.325 // %32.5 IV
)

/**
 * Opsiyon Yunanları (OptionGreeks)
 */
data class OptionGreeks(
    val delta: Double = 0.54,    // ∂V/∂S
    val gamma: Double = 0.082,   // ∂²V/∂S²
    val theta: Double = -0.045,  // ∂V/∂t (Günlük zaman erimesi)
    val vega: Double = 0.185,    // ∂V/∂σ (Volatilite duyarlılığı)
    val rho: Double = 0.024,     // ∂V/∂r (Faiz duyarlılığı)
    val vanna: Double = 0.012,   // ∂Δ/∂σ
    val vomma: Double = 0.006    // ∂Vega/∂σ
)

/**
 * Opsiyon Stratejisi Türü (OptionStrategyType)
 */
enum class OptionStrategyType(val displayName: String, val iconEmoji: String) {
    COVERED_CALL("Covered Call", "🛡️"),
    CASH_SECURED_PUT("Cash Secured Put", "💵"),
    PROTECTIVE_PUT("Protective Put", "☔"),
    BULL_CALL_SPREAD("Bull Call Spread", "🐂"),
    BEAR_PUT_SPREAD("Bear Put Spread", "🐻"),
    IRON_CONDOR("Iron Condor", "🦅"),
    IRON_BUTTERFLY("Iron Butterfly", "🦋"),
    LONG_STRADDLE("Long Straddle", "⚡"),
    SHORT_STRANGLE("Short Strangle", "⚖️"),
    CUSTOM_STRATEGY("Özel Strateji (Custom)", "🛠️");
}

/**
 * Opsiyon Stratejisi Risk & Payoff Analizi (OptionStrategyRisk)
 */
data class OptionStrategyRisk(
    val strategyType: OptionStrategyType = OptionStrategyType.COVERED_CALL,
    val maxProfitText: String = "Sınırlı (+1.480 TL)",
    val maxLossText: String = "Sınırsız (Hisse düşüş riski)",
    val breakEvenPoints: List<Double> = listOf(335.35),
    val riskRewardRatio: Double = 1.85,
    val probabilityOfProfitPct: Double = 68.4
)

/**
 * Vadeli İşlem Sözleşmesi (FuturesContract)
 */
data class FuturesContract(
    val contractSymbol: String = "F_THYAO0826",
    val underlyingName: String = "THYAO Pay Vadeli Sözleşmesi",
    val provider: DerivativesProviderType = DerivativesProviderType.VIOP_TURKEY,
    val tickSize: Double = 0.05,
    val contractMultiplier: Double = 100.0,
    val expirationDate: String = "31 Ağustos 2026",
    val lastPrice: Double = 362.50,
    val initialMarginTL: Double = 8400.0,
    val maintenanceMarginTL: Double = 6300.0
)

/**
 * Geleceğe Hazır Türev Ürün Stub Modeli (DerivativesFutureStubs)
 */
data class DerivativesFutureStubs(
    val isMultiLegOrderBuilderReady: Boolean = true,
    val isVolatilitySurfaceActive: Boolean = true,
    val isOptionsBacktestingReady: Boolean = true,
    val isAutomaticHedgeSuggestionsActive: Boolean = false,
    val isLiveOptionsScannerReady: Boolean = true
)
