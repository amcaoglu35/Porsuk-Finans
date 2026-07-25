package com.nexus.porsuk.domain.model

/**
 * Normalizasyon Yöntemleri (Factor Normalization Methods)
 */
enum class FactorNormalizationType(val displayName: String, val description: String) {
    Z_SCORE("Standardization (Z-Score)", "Mean=0, StdDev=1 ölçeklemesi"),
    MIN_MAX("Min-Max Normalization", "[0, 1] aralığına ölçekleme"),
    WINSORIZATION("Winsorization (Outlier Clipping)", "Aşırı uç değerleri %1 - %99 diliminde kırpma"),
    RANK_NORMALIZATION("Rank Uniform Normalization", "Sıralama bazlı homojen dağılım");
}

/**
 * Faktör Kombinasyon Stratejileri (Factor Combination Methods)
 */
enum class FactorCombinationStrategy(val displayName: String) {
    EQUAL_WEIGHT("Eşit Ağırlıklı (Equal Weight)"),
    LINEAR_WEIGHTED("Lineer Ağırlıklı (Linear Weighted)"),
    RISK_PARITY("Risk Paritesi (Volatility Risk Parity)"),
    IC_WEIGHTED("Information Coefficient (IC) Ağırlıklı");
}

/**
 * 10 Multi-Factor Kategorisi
 */
enum class MultiFactorCategory(val code: String, val title: String, val iconEmoji: String) {
    MOMENTUM("MOM", "Momentum", "⚡"),
    VALUE("VAL", "Değer (Value)", "💎"),
    GROWTH("GRO", "Büyüme (Growth)", "🚀"),
    QUALITY("QUA", "Kalite (Quality)", "🏅"),
    SIZE("SIZ", "Büyüklük (Size / Market Cap)", "🏢"),
    LOW_VOLATILITY("VOL", "Düşük Volatolite (Low Vol)", "📉"),
    DIVIDEND("DIV", "Temettü (Dividend Yield)", "💰"),
    PROFITABILITY("PRF", "Kârlılık (Profitability)", "📊"),
    INVESTMENT("INV", "Yatırım Oranı (Investment Asset Growth)", "🏗️"),
    LIQUIDITY("LIQ", "Likidite (Liquidity / Volume)", "💧");
}

/**
 * Alfa Faktörü Tanımı (Alpha Factor Definition)
 */
data class AlphaFactorDefinition(
    val factorId: String,
    val name: String,
    val category: MultiFactorCategory,
    val description: String,
    val formulaExpression: String,
    val isCustom: Boolean = false,
    val defaultWeight: Double = 1.0
)

/**
 * Alfa Faktörü Değeri ve Normalizasyon Sonucu
 */
data class AlphaFactorValue(
    val symbol: String,
    val factorId: String,
    val rawValue: Double,
    val normalizedValue: Double,
    val percentileRank: Double,
    val zScore: Double
)

/**
 * Faktör Sıralama Sonucu (Factor Ranking)
 */
data class FactorRankingResult(
    val factorId: String,
    val symbolRanks: List<SymbolRankItem>,
    val topDecileSymbols: List<String>,
    val bottomDecileSymbols: List<String>
)

data class SymbolRankItem(
    val symbol: String,
    val rank: Int,
    val totalCount: Int,
    val percentile: Double,
    val score: Double
)

/**
 * Faktör Maruz Kalma Sonucu (Factor Exposure)
 */
data class FactorExposureResult(
    val symbol: String,
    val exposures: Map<MultiFactorCategory, Double>,
    val dominantFactor: MultiFactorCategory,
    val netExposureScore: Double
)

/**
 * Faktör Kombinasyon Sonucu (Composite Alpha Score)
 */
data class FactorCombinationResult(
    val symbol: String,
    val compositeAlphaScore: Double,
    val strategyUsed: FactorCombinationStrategy,
    val factorContributions: Map<String, Double>
)

/**
 * Özel Faktör Formülü (Custom Factor Formula)
 */
data class CustomFactorFormula(
    val formulaId: String,
    val title: String,
    val expression: String,
    val author: String = "Quant Research User",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
