package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.PatternRecognitionEngine
import com.nexus.porsuk.data.engine.TechnicalSignalEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TechnicalRepositoryImpl @Inject constructor(
    private val signalEngine: TechnicalSignalEngine
) : TechnicalRepository {

    override fun getTechnicalReport(symbol: String, timeFrame: TimeFrame): Flow<TechnicalAnalysisReport> = flow {
        emit(signalEngine.generateTechnicalReport(symbol, timeFrame))
    }
}

@Singleton
class IndicatorRepositoryImpl @Inject constructor(
    private val signalEngine: TechnicalSignalEngine
) : IndicatorRepository {

    override fun getIndicatorsByCategory(symbol: String, timeFrame: TimeFrame, category: IndicatorCategory): Flow<List<IndicatorValue>> = flow {
        val report = signalEngine.generateTechnicalReport(symbol, timeFrame)
        emit(report.indicators.filter { it.category == category })
    }
}

@Singleton
class SignalRepositoryImpl @Inject constructor(
    private val signalEngine: TechnicalSignalEngine,
    private val patternEngine: PatternRecognitionEngine
) : SignalRepository {

    override fun getOverallSignal(symbol: String, timeFrame: TimeFrame): Flow<TechnicalSignalType> = flow {
        val report = signalEngine.generateTechnicalReport(symbol, timeFrame)
        emit(report.overallSignal)
    }

    override fun getDetectedPatterns(symbol: String, timeFrame: TimeFrame): Flow<List<PatternResult>> = flow {
        emit(patternEngine.detectPatterns(symbol, timeFrame))
    }
}
