package com.nexus.porsuk.domain.usecase.search

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.data.remote.ScrapeResult
import com.nexus.porsuk.data.model.StockDetails
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.remote.YahooFinancePublicService
import javax.inject.Inject

class SearchStockUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(symbol: String, market: String): List<StockDetails> {
        val result = repository.refreshPrice(symbol, market)
        return if (result is ScrapeResult.Success) {
            val details = StockDetails(
                symbol = symbol,
                name = symbol,
                price = result.data.price,
                currency = "",
                changeAmount = 0.0,
                changePercentage = result.data.changePercent
            )
            checkAndAddCompany(symbol, market)
            listOf(details)
        } else {
            emptyList()
        }
    }

    private suspend fun checkAndAddCompany(symbol: String, market: String) {
        val existing = repository.getCompany(symbol)
        if (existing == null) {
            val yahooPublic = YahooFinancePublicService()
            val result = yahooPublic.fetchCompanyInfo(symbol, market)
            val companyName = if (result is ScrapeResult.Success) result.data.about else symbol
            
            val cleanName = companyName.replace("[^a-zA-Z\\s]".toRegex(), "").trim()
            val domain = cleanName.split(" ").firstOrNull()?.lowercase()?.filter { it.isLetter() } ?: symbol.lowercase()
            val logoUrl = "https://logo.clearbit.com/$domain.com"
            
            repository.insertCompanies(listOf(
                Company(
                    symbol = symbol,
                    name = companyName, 
                    market = market,
                    logoInitials = symbol.take(3),
                    logoUrl = logoUrl,
                    sector = "Genel"
                )
            ))
        }
    }
}
