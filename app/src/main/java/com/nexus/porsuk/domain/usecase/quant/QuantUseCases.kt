package com.nexus.porsuk.domain.usecase.quant

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import javax.inject.Inject

/**
 * 1. Alfa Faktörleri ve Kombinasyonu UseCase
 */
class CalculateAlphaFactorsUseCase @Inject constructor(
    private val factorRepository: FactorRepository
) {
    suspend fun getRankings(factorId: String): FactorRankingResult {
        return factorRepository.calculateFactorRanking(factorId)
    }

    suspend fun getExposure(symbol: String): FactorExposureResult {
        return factorRepository.calculateFactorExposure(symbol)
    }

    suspend fun combine(symbols: List<String>, strategy: FactorCombinationStrategy): List<FactorCombinationResult> {
        return factorRepository.combineFactors(symbols, strategy)
    }
}

/**
 * 2. Akademik Modeller (CAPM, APT, FF3, FF5, CH4) UseCase
 */
class EvaluateAcademicModelUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend fun execute(symbol: String, modelType: AcademicModelType): AcademicModelResult {
        return statisticsRepository.calculateAcademicModel(symbol, modelType)
    }
}

/**
 * 3. Quant Strateji Doğrulama (Walk-Forward, Rolling Window, Bootstrap) UseCase
 */
class RunQuantValidationUseCase @Inject constructor(
    private val validationRepository: ValidationRepository
) {
    suspend fun walkForward(strategyId: String, inSampleMonths: Int, outOfSampleMonths: Int): WalkForwardResult {
        return validationRepository.runWalkForwardAnalysis(strategyId, inSampleMonths, outOfSampleMonths)
    }

    suspend fun rollingWindow(symbol: String, windowDays: Int): RollingWindowResult {
        return validationRepository.runRollingWindowAnalysis(symbol, windowDays)
    }

    suspend fun bootstrap(strategyId: String, simulationsCount: Int): BootstrapResult {
        return validationRepository.runBootstrapSimulation(strategyId, simulationsCount)
    }
}

/**
 * 4. Faktör Analitiği (Factor Decay, Persistence, Correlation, Attribution) UseCase
 */
class GetFactorAnalyticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend fun getDecay(factorId: String): FactorDecayMetrics {
        return statisticsRepository.getFactorDecay(factorId)
    }

    suspend fun getPersistence(factorId: String): FactorPersistenceMetrics {
        return statisticsRepository.getFactorPersistence(factorId)
    }

    suspend fun getCorrelationMatrix(): FactorCorrelationMatrix {
        return statisticsRepository.getFactorCorrelationMatrix()
    }

    suspend fun getAttribution(symbolOrPortfolio: String): PerformanceAttributionResult {
        return statisticsRepository.getPerformanceAttribution(symbolOrPortfolio)
    }
}

/**
 * 5. Feature Engineering & Feature Store UseCase
 */
class FeatureEngineeringUseCase @Inject constructor(
    private val datasetRepository: DatasetRepository
) {
    suspend fun applyTransformation(featureId: String, transformation: FeatureTransformationType): List<Double> {
        return datasetRepository.applyFeatureTransformation(featureId, transformation)
    }
}

/**
 * 6. Deney Takibi & Hipotez Yönetimi UseCase
 */
class ManageQuantExperimentsUseCase @Inject constructor(
    private val experimentRepository: ExperimentRepository
) {
    suspend fun save(title: String, params: Map<String, String>, metrics: Map<String, Double>, notes: String): QuantExperiment {
        return experimentRepository.saveExperiment(title, params, metrics, notes)
    }
}
