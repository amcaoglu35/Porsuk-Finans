package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepositoryImpl @Inject constructor(
    private val reportGeneratorEngine: OrakulReportGeneratorEngine
) : AnalysisRepository {
    override fun generateAnalysisReport(symbol: String): Flow<OrakulAnalysisReport> = flow {
        emit(reportGeneratorEngine.generateReport(symbol))
    }
}

@Singleton
class FinancialAnalysisRepositoryImpl @Inject constructor(
    private val financialEngine: FinancialAnalysisEngine
) : FinancialAnalysisRepository {
    override fun getFinancialAnalysis(symbol: String): Flow<FinancialAnalysisData> = flow {
        emit(financialEngine.analyzeFinancials(symbol))
    }
}

@Singleton
class TechnicalAnalysisRepositoryImpl @Inject constructor(
    private val technicalEngine: TechnicalAnalysisEngine
) : TechnicalAnalysisRepository {
    override fun getTechnicalAnalysis(symbol: String): Flow<TechnicalAnalysisData> = flow {
        emit(technicalEngine.analyzeTechnicals(symbol))
    }
}

@Singleton
class OrakulRiskRepositoryImpl @Inject constructor(
    private val riskEngine: RiskAnalysisEngine
) : OrakulRiskRepository {
    override fun getRiskAnalysis(symbol: String): Flow<RiskAnalysisData> = flow {
        emit(riskEngine.analyzeRisk(symbol))
    }
}
