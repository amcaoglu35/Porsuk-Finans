package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskRepositoryImpl @Inject constructor(
    private val reportGeneratorEngine: RiskReportGeneratorEngine
) : RiskRepository {
    override fun getRiskReport(symbol: String): Flow<RiskIntelligenceReport> = flow {
        emit(reportGeneratorEngine.generateReport(symbol))
    }
}

@Singleton
class PortfolioRiskRepositoryImpl @Inject constructor(
    private val portfolioRiskEngine: PortfolioRiskEngine
) : PortfolioRiskRepository {
    override fun getPortfolioRisk(symbol: String): Flow<PortfolioRiskData> = flow {
        emit(portfolioRiskEngine.calculatePortfolioRisk(symbol))
    }
}

@Singleton
class MarketRiskRepositoryImpl @Inject constructor(
    private val marketRiskEngine: MarketRiskEngine
) : MarketRiskRepository {
    override fun getMarketRisk(symbol: String): Flow<MarketRiskData> = flow {
        emit(marketRiskEngine.calculateMarketRisk(symbol))
    }
}

@Singleton
class FinancialRiskRepositoryImpl @Inject constructor(
    private val financialRiskEngine: FinancialRiskEngine
) : FinancialRiskRepository {
    override fun getFinancialRisk(symbol: String): Flow<FinancialRiskData> = flow {
        emit(financialRiskEngine.calculateFinancialRisk(symbol))
    }
}
