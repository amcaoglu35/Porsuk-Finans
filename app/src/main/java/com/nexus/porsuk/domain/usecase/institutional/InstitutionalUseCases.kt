package com.nexus.porsuk.domain.usecase.institutional

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 1. Kurumsal Pozisyonlar & Fonlar UseCase
 */
class GetInstitutionalHoldingsUseCase @Inject constructor(
    private val institutionRepository: InstitutionRepository
) {
    fun getHoldings(symbol: String): Flow<List<InstitutionalHoldingItem>> {
        return institutionRepository.getFundHoldings(symbol)
    }

    suspend fun getTopBuyersAndSellers(symbol: String): Pair<List<InstitutionalHoldingItem>, List<InstitutionalHoldingItem>> {
        val buyers = institutionRepository.getTopBuyers(symbol)
        val sellers = institutionRepository.getTopSellers(symbol)
        return Pair(buyers, sellers)
    }
}

/**
 * 2. Insider İşlemler UseCase
 */
class GetInsiderTradesUseCase @Inject constructor(
    private val insiderRepository: InsiderRepository
) {
    fun getTrades(symbol: String): Flow<List<InsiderTradeRecord>> {
        return insiderRepository.getRecentInsiderTrades(symbol)
    }

    suspend fun getNetActivity(symbol: String): NetInsiderActivity {
        return insiderRepository.getNetInsiderActivity(symbol)
    }
}

/**
 * 3. Sahiplik Yapısı UseCase
 */
class GetOwnershipBreakdownUseCase @Inject constructor(
    private val ownershipRepository: OwnershipRepository
) {
    fun execute(symbol: String): Flow<OwnershipBreakdown> {
        return ownershipRepository.getOwnershipBreakdown(symbol)
    }

    fun history(symbol: String): Flow<List<OwnershipHistoryPoint>> {
        return ownershipRepository.getOwnershipHistory(symbol)
    }
}

/**
 * 4. Balina Takibi & Smart Money Akışı UseCase
 */
class TrackWhaleActivityUseCase @Inject constructor(
    private val fundFlowRepository: FundFlowRepository
) {
    fun getWhaleAlerts(): Flow<List<WhaleAlert>> {
        return fundFlowRepository.getWhaleAlerts()
    }

    fun getFlow(symbol: String): Flow<SmartMoneyFlowSummary> {
        return fundFlowRepository.getSmartMoneyFlow(symbol)
    }
}

/**
 * 5. AI Intelligence UseCase
 */
class GenerateInstitutionalAiIntelligenceUseCase @Inject constructor(
    private val fundFlowRepository: FundFlowRepository
) {
    suspend fun execute(symbol: String): SmartMoneyAiCommentary {
        return fundFlowRepository.getSmartMoneyAiCommentary(symbol)
    }
}
