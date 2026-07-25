package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Kurumsal Yatırımcılar & Fonlar Deposu Sözleşmesi (InstitutionRepository)
 */
interface InstitutionRepository {
    fun getTopInstitutionalInvestors(): Flow<List<InstitutionalInvestor>>
    fun getFundHoldings(companySymbol: String): Flow<List<InstitutionalHoldingItem>>
    fun getHoldingChanges(companySymbol: String): Flow<List<InstitutionalHoldingItem>>
    suspend fun getTopBuyers(companySymbol: String): List<InstitutionalHoldingItem>
    suspend fun getTopSellers(companySymbol: String): List<InstitutionalHoldingItem>
}

/**
 * 2. İçeriden Öğrenenler İşlem Deposu Sözleşmesi (InsiderRepository)
 */
interface InsiderRepository {
    fun getRecentInsiderTrades(companySymbol: String): Flow<List<InsiderTradeRecord>>
    fun getTradesByRole(companySymbol: String, role: InsiderRoleType): Flow<List<InsiderTradeRecord>>
    suspend fun getNetInsiderActivity(companySymbol: String): NetInsiderActivity
}

/**
 * 3. Şirket Sahiplik Yapısı Deposu Sözleşmesi (OwnershipRepository)
 */
interface OwnershipRepository {
    fun getOwnershipBreakdown(companySymbol: String): Flow<OwnershipBreakdown>
    fun getOwnershipHistory(companySymbol: String): Flow<List<OwnershipHistoryPoint>>
    suspend fun calculateOwnershipConcentration(companySymbol: String): Double
}

/**
 * 4. Fon Akışı & Balina Takip Deposu Sözleşmesi (FundFlowRepository)
 */
interface FundFlowRepository {
    fun getWhaleAlerts(): Flow<List<WhaleAlert>>
    fun getSmartMoneyFlow(companySymbol: String): Flow<SmartMoneyFlowSummary>
    suspend fun getSmartMoneyAiCommentary(companySymbol: String): SmartMoneyAiCommentary
    fun getFutureStubs(): Flow<InstitutionalFutureStubs>
}
