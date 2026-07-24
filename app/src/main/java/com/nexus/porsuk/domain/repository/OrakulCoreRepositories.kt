package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Orakul Analiz Deposu Sözleşmesi (AnalysisRepository)
 */
interface AnalysisRepository {
    fun generateAnalysisReport(symbol: String): Flow<OrakulAnalysisReport>
}

/**
 * 2. Finansal Analiz Deposu Sözleşmesi (FinancialAnalysisRepository)
 */
interface FinancialAnalysisRepository {
    fun getFinancialAnalysis(symbol: String): Flow<FinancialAnalysisData>
}

/**
 * 3. Teknik Analiz Deposu Sözleşmesi (TechnicalAnalysisRepository)
 */
interface TechnicalAnalysisRepository {
    fun getTechnicalAnalysis(symbol: String): Flow<TechnicalAnalysisData>
}

/**
 * 4. Risk Analiz Deposu Sözleşmesi (OrakulRiskRepository)
 */
interface OrakulRiskRepository {
    fun getRiskAnalysis(symbol: String): Flow<RiskAnalysisData>
}
