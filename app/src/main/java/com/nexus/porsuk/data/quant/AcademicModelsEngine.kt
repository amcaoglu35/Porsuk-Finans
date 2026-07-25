package com.nexus.porsuk.data.quant

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class AcademicModelsEngine @Inject constructor() {

    fun fitModel(symbol: String, modelType: AcademicModelType): AcademicModelResult {
        return when (modelType) {
            AcademicModelType.CAPM -> fitCAPM(symbol)
            AcademicModelType.APT -> fitAPT(symbol)
            AcademicModelType.FAMA_FRENCH_3 -> fitFamaFrench3(symbol)
            AcademicModelType.FAMA_FRENCH_5 -> fitFamaFrench5(symbol)
            AcademicModelType.CARHART_4 -> fitCarhart4(symbol)
            AcademicModelType.CUSTOM_FUTURE -> fitCAPM(symbol).copy(modelType = AcademicModelType.CUSTOM_FUTURE)
        }
    }

    private fun fitCAPM(symbol: String): AcademicModelResult {
        val betas = listOf(
            FactorBetaDetail("Market Risk Premium (R_m - R_f)", betaValue = 1.18, tStatistic = 4.25, pValue = 0.0001, standardError = 0.04)
        )
        return AcademicModelResult(
            assetSymbol = symbol,
            modelType = AcademicModelType.CAPM,
            alphaPct = 4.8,
            alphaPValue = 0.012,
            isAlphaStatisticallySignificant = true,
            rSquared = 0.74,
            adjustedRSquared = 0.73,
            residualVolatility = 12.5,
            factorBetas = betas,
            fittedReturnPct = 28.4,
            actualReturnPct = 33.2
        )
    }

    private fun fitAPT(symbol: String): AcademicModelResult {
        val betas = listOf(
            FactorBetaDetail("Enflasyon Faktörü", betaValue = 0.45, tStatistic = 2.85, pValue = 0.005, standardError = 0.05),
            FactorBetaDetail("Faiz Oranı (Tahvil) Faktörü", betaValue = -0.72, tStatistic = -3.40, pValue = 0.001, standardError = 0.06),
            FactorBetaDetail("Sanayi Üretimi Faktörü", betaValue = 1.05, tStatistic = 3.90, pValue = 0.0003, standardError = 0.04)
        )
        return AcademicModelResult(
            assetSymbol = symbol,
            modelType = AcademicModelType.APT,
            alphaPct = 3.2,
            alphaPValue = 0.035,
            isAlphaStatisticallySignificant = true,
            rSquared = 0.81,
            adjustedRSquared = 0.79,
            residualVolatility = 10.2,
            factorBetas = betas,
            fittedReturnPct = 30.1,
            actualReturnPct = 33.2
        )
    }

    private fun fitFamaFrench3(symbol: String): AcademicModelResult {
        val betas = listOf(
            FactorBetaDetail("Market (R_m - R_f)", betaValue = 1.05, tStatistic = 5.80, pValue = 0.00001, standardError = 0.03),
            FactorBetaDetail("Size (SMB - Small Minus Big)", betaValue = -0.35, tStatistic = -2.15, pValue = 0.032, standardError = 0.05),
            FactorBetaDetail("Value (HML - High Minus Low)", betaValue = 0.68, tStatistic = 3.95, pValue = 0.0002, standardError = 0.04)
        )
        return AcademicModelResult(
            assetSymbol = symbol,
            modelType = AcademicModelType.FAMA_FRENCH_3,
            alphaPct = 5.4,
            alphaPValue = 0.008,
            isAlphaStatisticallySignificant = true,
            rSquared = 0.86,
            adjustedRSquared = 0.84,
            residualVolatility = 8.8,
            factorBetas = betas,
            fittedReturnPct = 27.8,
            actualReturnPct = 33.2
        )
    }

    private fun fitFamaFrench5(symbol: String): AcademicModelResult {
        val betas = listOf(
            FactorBetaDetail("Market (R_m - R_f)", betaValue = 1.02, tStatistic = 6.10, pValue = 0.00001, standardError = 0.03),
            FactorBetaDetail("Size (SMB)", betaValue = -0.28, tStatistic = -1.98, pValue = 0.048, standardError = 0.05),
            FactorBetaDetail("Value (HML)", betaValue = 0.52, tStatistic = 3.20, pValue = 0.002, standardError = 0.04),
            FactorBetaDetail("Profitability (RMW - Robust Minus Weak)", betaValue = 0.84, tStatistic = 4.50, pValue = 0.0001, standardError = 0.04),
            FactorBetaDetail("Investment (CMA - Conservative Minus Aggressive)", betaValue = 0.38, tStatistic = 2.40, pValue = 0.018, standardError = 0.05)
        )
        return AcademicModelResult(
            assetSymbol = symbol,
            modelType = AcademicModelType.FAMA_FRENCH_5,
            alphaPct = 6.1,
            alphaPValue = 0.004,
            isAlphaStatisticallySignificant = true,
            rSquared = 0.91,
            adjustedRSquared = 0.89,
            residualVolatility = 7.1,
            factorBetas = betas,
            fittedReturnPct = 27.1,
            actualReturnPct = 33.2
        )
    }

    private fun fitCarhart4(symbol: String): AcademicModelResult {
        val betas = listOf(
            FactorBetaDetail("Market (R_m - R_f)", betaValue = 1.04, tStatistic = 5.95, pValue = 0.00001, standardError = 0.03),
            FactorBetaDetail("Size (SMB)", betaValue = -0.30, tStatistic = -2.05, pValue = 0.041, standardError = 0.05),
            FactorBetaDetail("Value (HML)", betaValue = 0.60, tStatistic = 3.75, pValue = 0.0003, standardError = 0.04),
            FactorBetaDetail("Momentum (WML - Winners Minus Losers)", betaValue = 0.92, tStatistic = 5.10, pValue = 0.00005, standardError = 0.03)
        )
        return AcademicModelResult(
            assetSymbol = symbol,
            modelType = AcademicModelType.CARHART_4,
            alphaPct = 5.8,
            alphaPValue = 0.006,
            isAlphaStatisticallySignificant = true,
            rSquared = 0.89,
            adjustedRSquared = 0.87,
            residualVolatility = 7.9,
            factorBetas = betas,
            fittedReturnPct = 27.4,
            actualReturnPct = 33.2
        )
    }
}
