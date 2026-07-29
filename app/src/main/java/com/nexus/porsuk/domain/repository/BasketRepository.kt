package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import kotlinx.coroutines.flow.Flow

data class BasketPerformanceDomainModel(
    val basketId: Int,
    val basketName: String,
    val market: String,
    val totalCost: Double,
    val currentValue: Double,
    val profitLossAmount: Double,
    val profitLossPercent: Double,
    val dailyChangePercent: Double,
    val holdings: List<BasketItemHoldingModel>,
    val chartData: List<Double>
)

data class BasketItemHoldingModel(
    val id: Int,
    val symbol: String,
    val quantity: Double,
    val buyPrice: Double,
    val currentPrice: Double,
    val currentValue: Double,
    val profitLossAmount: Double,
    val profitLossPercent: Double,
    val dailyChangePercent: Double,
    val allocationPercent: Float
)

interface BasketRepository {
    fun getAllBaskets(): Flow<List<Basket>>
    fun getBasketById(basketId: Int): Flow<Basket?>
    suspend fun createBasket(name: String, market: String): Long
    suspend fun renameBasket(basketId: Int, newName: String)
    suspend fun deleteBasket(basketId: Int)
    fun getBasketItems(basketId: Int): Flow<List<BasketItem>>
    suspend fun addStockToBasket(basketId: Int, symbol: String, quantity: Double, buyPrice: Double): Result<Unit>
    suspend fun removeStockFromBasket(itemId: Int)
    suspend fun refreshBasketPrices(basketId: Int)
    fun getBasketPerformance(basketId: Int): Flow<BasketPerformanceDomainModel>
}
