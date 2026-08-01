package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class MutualFund(
    val code: String,
    val name: String,
    val category: String,
    val yearlyReturn: Double,
    val topHoldings: List<String>,
    val riskLevel: Int
)

data class FundOverlapResult(
    val fundCodeA: String,
    val fundCodeB: String,
    val overlapPercentage: Double,
    val commonHoldings: List<String>,
    val similarityLabel: String
)

interface FundComparisonRepository {
    fun getFunds(): Flow<List<MutualFund>>
    fun calculateOverlap(fundCodeA: String, fundCodeB: String): FundOverlapResult
}

@Singleton
class FundComparisonRepositoryImpl @Inject constructor() : FundComparisonRepository {

    private val fundsList = listOf(
        MutualFund(
            code = "TCD",
            name = "Tacirler Portföy Hisse Senedi Fonu",
            category = "Hisse Senedi Yoğun",
            yearlyReturn = 84.5,
            topHoldings = listOf("THYAO", "GARAN", "ASELS", "ASTOR", "KCHOL"),
            riskLevel = 6
        ),
        MutualFund(
            code = "AFT",
            name = "Ak Portföy Yeni Teknolojiler Yabancı Hisse",
            category = "Teknoloji",
            yearlyReturn = 62.0,
            topHoldings = listOf("NVDA", "AAPL", "MSFT", "ASELS", "TCELL"),
            riskLevel = 6
        ),
        MutualFund(
            code = "MAC",
            name = "Marmara Capital Portföy Hisse Senedi",
            category = "Hisse Senedi Yoğun",
            yearlyReturn = 78.2,
            topHoldings = listOf("THYAO", "GARAN", "BIMAS", "FROTO", "SAHOL"),
            riskLevel = 6
        ),
        MutualFund(
            code = "NNF",
            name = "Hedef Portföy Birinci Hisse Senedi Fonu",
            category = "Hisse Senedi Yoğun",
            yearlyReturn = 91.4,
            topHoldings = listOf("THYAO", "ASELS", "ASTOR", "YKBNK", "EREGL"),
            riskLevel = 7
        )
    )

    private val fundsState = MutableStateFlow(fundsList)

    override fun getFunds(): Flow<List<MutualFund>> = fundsState.asStateFlow()

    override fun calculateOverlap(fundCodeA: String, fundCodeB: String): FundOverlapResult {
        val fundA = fundsList.find { it.code == fundCodeA } ?: fundsList[0]
        val fundB = fundsList.find { it.code == fundCodeB } ?: fundsList[2]

        val setA = fundA.topHoldings.toSet()
        val setB = fundB.topHoldings.toSet()
        val common = setA.intersect(setB).toList()

        val overlapPct = (common.size.toDouble() / (setA.size.coerceAtLeast(1))) * 100.0

        val label = when {
            overlapPct >= 60.0 -> "Yüksek Çakışma - Portföy İkilemesi Riski"
            overlapPct >= 30.0 -> "Orta Çakışma - Makul Çeşitlendirme"
            else -> "Düşük Çakışma - Güçlü Çeşitlendirme"
        }

        return FundOverlapResult(
            fundCodeA = fundA.code,
            fundCodeB = fundB.code,
            overlapPercentage = overlapPct,
            commonHoldings = common,
            similarityLabel = label
        )
    }
}
