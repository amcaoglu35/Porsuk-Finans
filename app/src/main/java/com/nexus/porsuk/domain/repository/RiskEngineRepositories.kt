package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Risk Analiz Deposu Sözleşmesi (RiskRepository)
 */
interface RiskRepository {
    fun getRiskReport(symbol: String): Flow<RiskIntelligenceReport>
}

/**
 * 2. Portföy Riski Deposu Sözleşmesi (PortfolioRiskRepository)
 */
interface PortfolioRiskRepository {
    fun getPortfolioRisk(symbol: String): Flow<PortfolioRiskData>
}

/**
 * 3. Piyasa Riski Deposu Sözleşmesi (MarketRiskRepository)
 */
interface MarketRiskRepository {
    fun getMarketRisk(symbol: String): Flow<MarketRiskData>
}

/**
 * 4. Finansal Risk Deposu Sözleşmesi (FinancialRiskRepository)
 */
interface FinancialRiskRepository {
    fun getFinancialRisk(symbol: String): Flow<FinancialRiskData>
}
