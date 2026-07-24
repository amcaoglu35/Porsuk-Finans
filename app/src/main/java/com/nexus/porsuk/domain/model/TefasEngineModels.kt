package com.nexus.porsuk.domain.model

/**
 * TEFAS Fon Temel Domain Modeli
 */
data class TefasFundModel(
    val code: String,
    val name: String,
    val founder: String,
    val manager: String,
    val umbrellaFund: String,
    val fundType: String,
    val riskLevel: Int,
    val currency: String,
    val price: Double,
    val totalAssets: Double,
    val investorCount: Long,
    val managementFee: Double,
    val lastUpdated: Long,
    val isActive: Boolean
)

/**
 * Geleceğe Hazır — TEFAS Fon Getiri ve Risk Performans Metrikleri Modeli
 *
 * İleride eklenecek günlük/haftalık/aylık/yıllık/3 yıllık/5 yıllık getiri, Sharpe Oranı,
 * Standart Sapma ve AI Fund Score sistemleri için genişletilebilir mimari.
 */
data class TefasPerformanceMetrics(
    val fundCode: String,
    val dailyReturnPct: Double = 0.0,
    val weeklyReturnPct: Double = 0.0,
    val monthlyReturnPct: Double = 0.0,
    val yearlyReturnPct: Double = 0.0,
    val threeYearReturnPct: Double = 0.0,
    val fiveYearReturnPct: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val standardDeviation: Double = 0.0,
    val aiFundScore: Double = 0.0 // 0.0 - 100.0 Orakul AI Fon Skoru
)

/**
 * Geleceğe Hazır — TEFAS Fon Varlık Dağılım Modeli
 */
data class TefasAssetAllocation(
    val fundCode: String,
    val stockPct: Double = 0.0, // Hisse Senedi %
    val bondPct: Double = 0.0, // Devlet Tahvili / Bono %
    val eurobondPct: Double = 0.0, // Eurobond %
    val cashPct: Double = 0.0, // Mevduat / Takasbank %
    val foreignSecuritiesPct: Double = 0.0, // Yabancı Menkul Kıymetler %
    val otherPct: Double = 0.0
)
