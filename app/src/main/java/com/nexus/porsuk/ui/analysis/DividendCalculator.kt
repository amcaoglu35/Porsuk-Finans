package com.nexus.porsuk.ui.analysis

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.DividendCalendarEntry
import java.util.Locale

data class DividendContributor(
    val symbol: String,
    val quantity: Double,
    val currentPrice: Double,
    val currentValue: Double,
    val yieldPct: Double,
    val annualIncome: Double
)

data class DividendSummary(
    val totalPortfolioValue: Double,
    val annualExpectedIncome: Double,
    val monthlyAverageIncome: Double,
    val portfolioYieldPct: Double,
    val contributors: List<DividendContributor>,
    val upcomingEvents: List<DividendCalendarEntry>
)

object DividendCalculator {

    fun calculateSummary(
        basketItems: List<BasketItem>,
        companies: List<Company>,
        cachedInfoMap: Map<String, CachedCompanyInfo>,
        allDividendEvents: List<DividendCalendarEntry> = emptyList()
    ): DividendSummary {
        val companyMap = companies.associateBy { it.symbol }
        
        var totalValue = 0.0
        var totalAnnualIncome = 0.0
        val contributorsList = mutableListOf<DividendContributor>()

        basketItems.forEach { item ->
            val comp = companyMap[item.symbol]
            val info = cachedInfoMap[item.symbol]
            val price = comp?.currentPrice ?: item.buyPrice
            val itemValue = item.quantity * price
            totalValue += itemValue

            val yieldPct = info?.dividendYield ?: 0.0
            val annualIncome = itemValue * (yieldPct / 100.0)
            totalAnnualIncome += annualIncome

            if (itemValue > 0) {
                contributorsList.add(
                    DividendContributor(
                        symbol = item.symbol,
                        quantity = item.quantity,
                        currentPrice = price,
                        currentValue = itemValue,
                        yieldPct = yieldPct,
                        annualIncome = annualIncome
                    )
                )
            }
        }

        val monthlyAvg = if (totalAnnualIncome > 0) totalAnnualIncome / 12.0 else 0.0
        val overallYield = if (totalValue > 0) (totalAnnualIncome / totalValue) * 100.0 else 0.0

        val sortedContributors = contributorsList.sortedByDescending { it.annualIncome }
        val portfolioSymbols = basketItems.map { it.symbol }.toSet()
        val relevantEvents = allDividendEvents.filter { portfolioSymbols.contains(it.symbol) }

        return DividendSummary(
            totalPortfolioValue = totalValue,
            annualExpectedIncome = totalAnnualIncome,
            monthlyAverageIncome = monthlyAvg,
            portfolioYieldPct = overallYield,
            contributors = sortedContributors,
            upcomingEvents = relevantEvents
        )
    }
}
