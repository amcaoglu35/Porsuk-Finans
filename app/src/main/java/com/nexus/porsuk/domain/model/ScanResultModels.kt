package com.nexus.porsuk.domain.model

/**
 * Porsuk Smart Scanner Engine — Tarama Sonuç Öge Modeli (ScanResultItem)
 */
data class ScanResultItem(
    val symbol: String,
    val companyName: String,
    val logoUrl: String? = null,
    val marketType: ScanMarketType,
    val lastPrice: Double,
    val dailyChangePct: Double,
    val volumeText: String,
    val masterScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val matchedPreset: ScanPresetCategory = ScanPresetCategory.TOP_GAINERS
)

/**
 * Gelişmiş Çoklu Kriter Filtreleme Veri Modeli (ScannerFilterCriteria)
 */
data class ScannerFilterCriteria(
    val marketType: ScanMarketType = ScanMarketType.ALL,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minMarketCapUsd: Double? = null,
    val maxPeRatio: Double? = null,
    val minRsi: Double? = null,
    val maxRsi: Double? = null,
    val minMasterScore: Int? = null,
    val riskLevelFilter: RiskLevel? = null,
    val sectorName: String? = null
)

/**
 * Geleceğe Hazır AI Smart Scanner Stub Modeli
 */
data class AiOpportunityScannerStub(
    val symbol: String,
    val opportunityScore: Int = 94,
    val aiReasoningText: String = "Orakul AI: Direnç kırılımı sonrası yüksek işlem hacmi ile yükseliş ivmesi bekleniyor."
)
