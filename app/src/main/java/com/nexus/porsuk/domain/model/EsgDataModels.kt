package com.nexus.porsuk.domain.model

/**
 * ESG Veri Sağlayıcısı Türü (EsgProviderType)
 */
enum class EsgProviderType(val displayName: String, val iconEmoji: String) {
    MSCI_ESG("MSCI ESG Research", "🌱"),
    SUSTAINALYTICS("Sustainalytics ESG", "⚖️"),
    REFINITIV_ESG("Refinitiv ESG Ratings", "📊"),
    BLOOMBERG_ESG("Bloomberg ESG Data", "⚡"),
    CDP_CARBON("CDP Climate Disclosure", "🌍");
}

/**
 * Genel ESG Skoru ve Derecesi (EsgScoreData)
 */
data class EsgScoreData(
    val companySymbol: String = "THYAO.IS",
    val companyName: String = "Türk Hava Yolları",
    val overallScore: Int = 84, // 0-100
    val ratingGrade: String = "AA (Leader)",
    val environmentalScore: Int = 82,
    val socialScore: Int = 86,
    val governanceScore: Int = 85,
    val industryRankText: String = "#3 / 48 (Top %5)",
    val carbonIntensityMt: Double = 142.5, // Ton CO2e / M$ Gelir
    val netZeroTargetYear: Int = 2035
)

/**
 * Çevresel Sütun Detayları (EnvironmentalPillar)
 */
data class EnvironmentalPillar(
    val carbonEmissionsScope1: Double = 450000.0, // Ton CO2e
    val carbonEmissionsScope2: Double = 120000.0,
    val carbonEmissionsScope3: Double = 850000.0,
    val renewableEnergyUsagePct: Double = 64.5,
    val waterConsumptionM3: Double = 185000.0,
    val wasteRecyclingRatePct: Double = 88.0,
    val climateRiskLevel: String = "Düşük (Low Physical Risk)"
)

/**
 * Sosyal Sütun Detayları (SocialPillar)
 */
data class SocialPillar(
    val employeeTurnoverPct: Double = 6.2,
    val genderDiversityPct: Double = 42.5,
    val healthSafetyIncidentRate: Double = 0.02,
    val humanRightsPolicyActive: Boolean = true,
    val customerSatisfactionScore: Int = 92
)

/**
 * Kurumsal Yönetişim Sütun Detayları (GovernancePillar)
 */
data class GovernancePillar(
    val independentDirectorsPct: Double = 68.0,
    val boardGenderDiversityPct: Double = 35.0,
    val auditQualityScore: Int = 95,
    val executiveCompAlignment: Boolean = true,
    val antiCorruptionPolicyActive: Boolean = true
)

/**
 * ESG Tartışma & Uyarı (EsgControversyAlert)
 */
data class EsgControversyAlert(
    val alertId: String = "alert_${System.currentTimeMillis()}",
    val title: String = "Karbon Emisyon Hedefi Açıklandı",
    val severity: String = "Düşük (Low Impact)",
    val category: String = "Çevre & İklim",
    val publishedDate: String = "24 Temmuz 2026"
)

/**
 * Geleceğe Hazır ESG Stub Modeli (EsgFutureStubs)
 */
data class EsgFutureStubs(
    val isAiEsgRatingReady: Boolean = true,
    val isSatelliteClimateDataActive: Boolean = true,
    val isScope123AnalysisReady: Boolean = true,
    val isRealTimeEsgMonitoringActive: Boolean = true,
    val isGreenwashingDetectionSupported: Boolean = false
)
