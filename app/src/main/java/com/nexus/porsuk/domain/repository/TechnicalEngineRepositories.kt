package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Teknik Analiz Deposu Sözleşmesi (TechnicalRepository)
 */
interface TechnicalRepository {
    fun getTechnicalReport(symbol: String, timeFrame: TimeFrame): Flow<TechnicalAnalysisReport>
}

/**
 * 2. İndikatörler Deposu Sözleşmesi (IndicatorRepository)
 */
interface IndicatorRepository {
    fun getIndicatorsByCategory(symbol: String, timeFrame: TimeFrame, category: IndicatorCategory): Flow<List<IndicatorValue>>
}

/**
 * 3. Sinyaller ve Formasyonlar Deposu Sözleşmesi (SignalRepository)
 */
interface SignalRepository {
    fun getOverallSignal(symbol: String, timeFrame: TimeFrame): Flow<TechnicalSignalType>
    fun getDetectedPatterns(symbol: String, timeFrame: TimeFrame): Flow<List<PatternResult>>
}
