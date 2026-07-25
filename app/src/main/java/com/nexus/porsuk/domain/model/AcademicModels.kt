package com.nexus.porsuk.domain.model

/**
 * Akademik Faktör Model Türleri
 */
enum class AcademicModelType(val code: String, val title: String, val formulaDesc: String) {
    CAPM("CAPM", "Capital Asset Pricing Model", "R_i - R_f = β_i * (R_m - R_f) + α"),
    APT("APT", "Arbitrage Pricing Theory", "R_i = E(R_i) + β_1*F_1 + β_2*F_2 + ... + ε"),
    FAMA_FRENCH_3("FF3", "Fama-French 3-Factor Model", "R_i - R_f = α + β_m*(R_m-R_f) + s_i*SMB + h_i*HML"),
    FAMA_FRENCH_5("FF5", "Fama-French 5-Factor Model", "R_i - R_f = α + β_m*(R_m-R_f) + s*SMB + h*HML + r*RMW + c*CMA"),
    CARHART_4("CH4", "Carhart 4-Factor Model", "R_i - R_f = α + β_m*(R_m-R_f) + s*SMB + h*HML + m*MOM"),
    CUSTOM_FUTURE("FUTURE", "Extensible Academic Model Interface", "Dynamic User-Defined Factors");
}

/**
 * Akademik Model Analiz Regresyon Sonucu
 */
data class AcademicModelResult(
    val assetSymbol: String,
    val modelType: AcademicModelType,
    val alphaPct: Double,
    val alphaPValue: Double,
    val isAlphaStatisticallySignificant: Boolean,
    val rSquared: Double,
    val adjustedRSquared: Double,
    val residualVolatility: Double,
    val factorBetas: List<FactorBetaDetail>,
    val fittedReturnPct: Double,
    val actualReturnPct: Double
)

/**
 * Faktör Beta ve İstatistiksel Anlamlılık Metriği
 */
data class FactorBetaDetail(
    val factorName: String,
    val betaValue: Double,
    val tStatistic: Double,
    val pValue: Double,
    val standardError: Double
)
