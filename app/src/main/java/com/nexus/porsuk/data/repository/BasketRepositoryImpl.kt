package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.core.network.BaseRepository
import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.remote.datasource.FinnhubRemoteDataSource
import com.nexus.porsuk.data.remote.datasource.FmpRemoteDataSource
import com.nexus.porsuk.domain.repository.BasketItemHoldingModel
import com.nexus.porsuk.domain.repository.BasketPerformanceDomainModel
import com.nexus.porsuk.domain.repository.BasketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasketRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val finnhubRemoteDataSource: FinnhubRemoteDataSource,
    private val fmpRemoteDataSource: FmpRemoteDataSource
) : BaseRepository(), BasketRepository {

    override fun getAllBaskets(): Flow<List<Basket>> = assetDao.getAllBaskets()

    override fun getBasketById(basketId: Int): Flow<Basket?> = assetDao.getBasketById(basketId)

    override suspend fun createBasket(name: String, market: String): Long {
        return assetDao.insertBasket(
            Basket(
                name = name,
                market = market,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun renameBasket(basketId: Int, newName: String) {
        val basket = assetDao.getBasketById(basketId).first()
        if (basket != null) {
            assetDao.updateBasket(basket.copy(name = newName))
        }
    }

    override suspend fun deleteBasket(basketId: Int) {
        val basket = assetDao.getBasketById(basketId).first()
        if (basket != null) {
            assetDao.deleteBasket(basket)
        }
    }

    override fun getBasketItems(basketId: Int): Flow<List<BasketItem>> {
        return assetDao.getItemsForBasket(basketId)
    }

    override suspend fun addStockToBasket(
        basketId: Int,
        symbol: String,
        quantity: Double,
        buyPrice: Double
    ): Result<Unit> {
        val cleanSymbol = symbol.trim().uppercase()
        val existingItems = assetDao.getItemsForBasket(basketId).first()
        
        val duplicate = existingItems.any { it.symbol.equals(cleanSymbol, ignoreCase = true) }
        if (duplicate) {
            return Result.failure(IllegalArgumentException("Bu hisse senedi zaten bu sepette mevcut."))
        }

        val item = BasketItem(
            basketId = basketId,
            symbol = cleanSymbol,
            quantity = quantity,
            buyPrice = buyPrice,
            buyDate = System.currentTimeMillis()
        )
        assetDao.insertBasketItem(item)
        refreshStockPrice(cleanSymbol)
        return Result.success(Unit)
    }

    override suspend fun removeStockFromBasket(itemId: Int) {
        val allItems = assetDao.getAllBasketItemsDirect()
        val target = allItems.find { it.id == itemId }
        if (target != null) {
            assetDao.deleteBasketItem(target)
        }
    }

    override suspend fun refreshBasketPrices(basketId: Int) {
        val items = assetDao.getItemsForBasket(basketId).first()
        items.forEach { item ->
            refreshStockPrice(item.symbol)
        }
    }

    private suspend fun refreshStockPrice(symbol: String) {
        // Try Finnhub quote first
        when (val finnhubRes = finnhubRemoteDataSource.fetchCompanyQuote(symbol)) {
            is NetworkResult.Success -> {
                val quote = finnhubRes.data
                if (quote.currentPrice != null && quote.currentPrice > 0) {
                    val company = assetDao.getCompany(symbol) ?: Company(
                        symbol = symbol,
                        name = symbol,
                        market = "BIST",
                        logoUrl = null,
                        logoInitials = symbol.take(2),
                        sector = "Genel"
                    )
                    val updated = company.copy(
                        currentPrice = quote.currentPrice,
                        changePercent = quote.changePercent ?: 0.0,
                        lastUpdated = System.currentTimeMillis()
                    )
                    assetDao.insertCompanies(listOf(updated))
                    return
                }
            }
            else -> {}
        }

        // Fallback to FMP quote
        when (val fmpRes = fmpRemoteDataSource.getQuote(symbol)) {
            is NetworkResult.Success -> {
                val quoteList = fmpRes.data
                if (quoteList.isNotEmpty()) {
                    val quote = quoteList.first()
                    if (quote.price != null && quote.price > 0) {
                        val company = assetDao.getCompany(symbol) ?: Company(
                            symbol = symbol,
                            name = quote.name ?: symbol,
                            market = "US",
                            logoUrl = null,
                            logoInitials = symbol.take(2),
                            sector = "Genel"
                        )
                        val updated = company.copy(
                            currentPrice = quote.price,
                            changePercent = quote.changesPercentage ?: 0.0,
                            lastUpdated = System.currentTimeMillis()
                        )
                        assetDao.insertCompanies(listOf(updated))
                    }
                }
            }
            else -> {}
        }
    }

    override fun getBasketPerformance(basketId: Int): Flow<BasketPerformanceDomainModel> {
        return combine(
            assetDao.getBasketById(basketId),
            assetDao.getItemsForBasket(basketId),
            assetDao.getAllCompanies()
        ) { basket, items, companies ->
            val basketName = basket?.name ?: "Sepet #$basketId"
            val market = basket?.market ?: "BIST"
            val companyMap = companies.associateBy { it.symbol.uppercase() }

            var totalCost = 0.0
            var currentValue = 0.0
            var weightedDailyChange = 0.0

            val rawHoldings = items.map { item ->
                val comp = companyMap[item.symbol.uppercase()]
                val currentPrice = comp?.currentPrice?.takeIf { it > 0.0 } ?: item.buyPrice
                val changePct = comp?.changePercent ?: 0.0
                
                val itemValue = item.quantity * currentPrice
                val itemCost = item.quantity * item.buyPrice
                val itemPnl = itemValue - itemCost
                val itemPnlPct = if (itemCost > 0) (itemPnl / itemCost) * 100.0 else 0.0

                totalCost += itemCost
                currentValue += itemValue

                BasketItemHoldingModel(
                    id = item.id,
                    symbol = item.symbol,
                    quantity = item.quantity,
                    buyPrice = item.buyPrice,
                    currentPrice = currentPrice,
                    currentValue = itemValue,
                    profitLossAmount = itemPnl,
                    profitLossPercent = itemPnlPct,
                    dailyChangePercent = changePct,
                    allocationPercent = 0f
                )
            }

            val finalHoldings = rawHoldings.map { holding ->
                val alloc = if (currentValue > 0) (holding.currentValue / currentValue).toFloat() else 0f
                weightedDailyChange += holding.dailyChangePercent * alloc
                holding.copy(allocationPercent = alloc)
            }

            val pnlAmount = currentValue - totalCost
            val pnlPercent = if (totalCost > 0) (pnlAmount / totalCost) * 100.0 else 0.0

            // Generate representative price progression line for chart
            val chartLine = generateChartLine(currentValue, pnlPercent)

            BasketPerformanceDomainModel(
                basketId = basketId,
                basketName = basketName,
                market = market,
                totalCost = totalCost,
                currentValue = currentValue,
                profitLossAmount = pnlAmount,
                profitLossPercent = pnlPercent,
                dailyChangePercent = weightedDailyChange,
                holdings = finalHoldings,
                chartData = chartLine
            )
        }
    }

    private fun generateChartLine(currentValue: Double, profitLossPercent: Double): List<Double> {
        if (currentValue <= 0) return emptyList()
        val steps = 15
        val startVal = currentValue / (1.0 + (profitLossPercent / 100.0))
        val stepDiff = (currentValue - startVal) / steps
        return List(steps) { idx ->
            startVal + (stepDiff * idx)
        }
    }
}
