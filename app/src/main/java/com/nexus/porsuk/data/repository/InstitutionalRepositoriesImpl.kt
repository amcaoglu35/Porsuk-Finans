package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.institutional.InstitutionalHoldingsEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstitutionRepositoryImpl @Inject constructor(
    private val engine: InstitutionalHoldingsEngine
) : InstitutionRepository {

    private val topInvestorsState = MutableStateFlow(
        listOf(
            InstitutionalInvestor("inv_1", "Vanguard Group Inc", 4500000000000.0, "THYAO.IS", 4200, "2026-Q2 (13F)", 4.2, "Tim Buckley"),
            InstitutionalInvestor("inv_2", "BlackRock Inc", 5200000000000.0, "GARAN.IS", 5100, "2026-Q2 (13F)", 3.8, "Larry Fink"),
            InstitutionalInvestor("inv_3", "Fidelity Management & Research", 2800000000000.0, "TUPRS.IS", 3100, "2026-Q2 (13F)", 6.1, "Abigail Johnson"),
            InstitutionalInvestor("inv_4", "Norges Bank Investment Management (NBIM)", 1400000000000.0, "KCHOL.IS", 1200, "2026-Q2 (13F)", 2.5, "Nicolai Tangen")
        )
    )

    private val sampleHoldings = listOf(
        InstitutionalHoldingItem("Vanguard Group Inc", "THYAO.IS", 48500000L, 1455000000.0, 4.8, 2400000L, 5.2, HoldingChangeType.ADDED, "2026-Q2"),
        InstitutionalHoldingItem("BlackRock Inc", "THYAO.IS", 42000000L, 1260000000.0, 4.2, 1800000L, 4.5, HoldingChangeType.ADDED, "2026-Q2"),
        InstitutionalHoldingItem("Fidelity Management", "THYAO.IS", 22000000L, 660000000.0, 2.2, 22000000L, 100.0, HoldingChangeType.NEW_POSITION, "2026-Q2"),
        InstitutionalHoldingItem("State Street Corp", "THYAO.IS", 18500000L, 555000000.0, 1.85, -1200000L, -6.1, HoldingChangeType.REDUCED, "2026-Q2"),
        InstitutionalHoldingItem("Norges Bank", "THYAO.IS", 15000000L, 450000000.0, 1.5, 0L, 0.0, HoldingChangeType.UNCHANGED, "2026-Q2")
    )

    override fun getTopInstitutionalInvestors(): Flow<List<InstitutionalInvestor>> = topInvestorsState.asStateFlow()

    override fun getFundHoldings(companySymbol: String): Flow<List<InstitutionalHoldingItem>> {
        return MutableStateFlow(sampleHoldings).asStateFlow()
    }

    override fun getHoldingChanges(companySymbol: String): Flow<List<InstitutionalHoldingItem>> {
        return MutableStateFlow(sampleHoldings.filter { it.changeType != HoldingChangeType.UNCHANGED }).asStateFlow()
    }

    override suspend fun getTopBuyers(companySymbol: String): List<InstitutionalHoldingItem> {
        return sampleHoldings.filter { it.sharesChange > 0 }
    }

    override suspend fun getTopSellers(companySymbol: String): List<InstitutionalHoldingItem> {
        return sampleHoldings.filter { it.sharesChange < 0 }
    }
}

@Singleton
class InsiderRepositoryImpl @Inject constructor(
    private val engine: InstitutionalHoldingsEngine
) : InsiderRepository {

    private val sampleTrades = listOf(
        InsiderTradeRecord("t_101", "THYAO.IS", "Türk Hava Yolları", "Ahmet Bolat", InsiderRoleType.CEO, InsiderTransactionType.BUY, 50000L, 310.5, 15525000.0, 240000L, "22 Temmuz 2026", "23 Temmuz 2026"),
        InsiderTradeRecord("t_102", "THYAO.IS", "Türk Hava Yolları", "Murat Şeker", InsiderRoleType.CFO, InsiderTransactionType.BUY, 25000L, 308.0, 7700000.0, 110000L, "20 Temmuz 2026", "21 Temmuz 2026"),
        InsiderTradeRecord("t_103", "THYAO.IS", "Türk Hava Yolları", "Şekib Avdagiç", InsiderRoleType.DIRECTOR, InsiderTransactionType.BUY, 15000L, 309.2, 4638000.0, 85000L, "18 Temmuz 2026", "19 Temmuz 2026")
    )

    override fun getRecentInsiderTrades(companySymbol: String): Flow<List<InsiderTradeRecord>> {
        return MutableStateFlow(sampleTrades).asStateFlow()
    }

    override fun getTradesByRole(companySymbol: String, role: InsiderRoleType): Flow<List<InsiderTradeRecord>> {
        return MutableStateFlow(sampleTrades.filter { it.role == role }).asStateFlow()
    }

    override suspend fun getNetInsiderActivity(companySymbol: String): NetInsiderActivity {
        return engine.calculateNetInsiderActivity(sampleTrades, companySymbol)
    }
}

@Singleton
class OwnershipRepositoryImpl @Inject constructor(
    private val engine: InstitutionalHoldingsEngine
) : OwnershipRepository {

    private val sampleBreakdown = OwnershipBreakdown(
        companySymbol = "THYAO.IS",
        institutionalOwnershipPct = 54.8,
        insiderOwnershipPct = 22.4,
        retailOwnershipPct = 22.8,
        governmentOwnershipPct = 0.0,
        floatShares = 1380000000L,
        totalSharesOutstanding = 1380000000L,
        hhiConcentrationIndex = 0.084
    )

    private val sampleHistory = listOf(
        OwnershipHistoryPoint("2025-Q3", 48.2, 22.0, 29.8),
        OwnershipHistoryPoint("2025-Q4", 50.5, 22.1, 27.4),
        OwnershipHistoryPoint("2026-Q1", 52.8, 22.2, 25.0),
        OwnershipHistoryPoint("2026-Q2", 54.8, 22.4, 22.8)
    )

    override fun getOwnershipBreakdown(companySymbol: String): Flow<OwnershipBreakdown> {
        return MutableStateFlow(sampleBreakdown).asStateFlow()
    }

    override fun getOwnershipHistory(companySymbol: String): Flow<List<OwnershipHistoryPoint>> {
        return MutableStateFlow(sampleHistory).asStateFlow()
    }

    override suspend fun calculateOwnershipConcentration(companySymbol: String): Double {
        return sampleBreakdown.hhiConcentrationIndex
    }
}

@Singleton
class FundFlowRepositoryImpl @Inject constructor(
    private val engine: InstitutionalHoldingsEngine
) : FundFlowRepository {

    private val whaleAlertsState = MutableStateFlow(
        listOf(
            WhaleAlert("w_1", "THYAO.IS", "Vanguard Group", "THYAO pozisyonunu %5.2 oranında artırdı (+2.4M Lot).", 24500000.0, "HIGH", "24 Temmuz 2026"),
            WhaleAlert("w_2", "GARAN.IS", "BlackRock Inc", "GARAN hisselerinde $18M tutarında yeni alım bildirimi.", 18000000.0, "HIGH", "23 Temmuz 2026"),
            WhaleAlert("w_3", "TUPRS.IS", "Fidelity Management", "TUPRS'ta 2.2M Lot yeni pozisyon açıldı.", 14200000.0, "MEDIUM", "21 Temmuz 2026")
        )
    )

    override fun getWhaleAlerts(): Flow<List<WhaleAlert>> = whaleAlertsState.asStateFlow()

    override fun getSmartMoneyFlow(companySymbol: String): Flow<SmartMoneyFlowSummary> {
        return MutableStateFlow(engine.computeSmartMoneyFlow(companySymbol)).asStateFlow()
    }

    override suspend fun getSmartMoneyAiCommentary(companySymbol: String): SmartMoneyAiCommentary {
        return engine.generateAiCommentary(companySymbol)
    }

    override fun getFutureStubs(): Flow<InstitutionalFutureStubs> {
        return MutableStateFlow(InstitutionalFutureStubs()).asStateFlow()
    }
}
