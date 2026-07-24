package com.nexus.porsuk.domain.model

/**
 * Screener Pro Ultimate — Tarama Sonuç Öge Modeli (ScreenerResultItem)
 */
data class ScreenerResultItem(
    val symbol: String,
    val companyName: String,
    val logoUrl: String? = null,
    val marketType: ScanMarketType,
    val lastPrice: Double,
    val dailyChangePct: Double,
    val volumeText: String,
    val marketCapText: String,
    val masterScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val peRatio: Double = 8.5,
    val pbRatio: Double = 1.4,
    val roePct: Double = 28.5,
    val dividendYieldPct: Double = 4.2,
    val altmanZScore: Double = 3.85,
    val piotroskiFScore: Int = 8 // 0-9 Piotroski Skor Altyapısı
)

/**
 * Gelişmiş Çoklu Filtreleme Kriter Modeli (ScreenerUltimateCriteria)
 */
data class ScreenerUltimateCriteria(
    val presetCategory: SmartFilterPresetCategory = SmartFilterPresetCategory.VALUE_INVESTORS,
    val marketType: ScanMarketType = ScanMarketType.ALL,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val maxPeRatio: Double? = null,
    val maxPbRatio: Double? = null,
    val minRoePct: Double? = null,
    val minDividendYieldPct: Double? = null,
    val minAltmanZScore: Double? = null,
    val minMasterScore: Int? = null,
    val riskLevelFilter: RiskLevel? = null
)

/**
 * Geleceğe Hazır AI Filter Builder & Opportunity Ranking Stub
 */
data class AiFilterBuilderStub(
    val filterName: String = "Orakul AI Warren Buffett Stratejisi",
    val aiScore: Int = 96,
    val description: String = "Orakul AI: Düşük F/K, yüksek ROE ve Altman Z-Score > 3.0 üzeri şirketleri otomatik filtreler."
)
