package com.nexus.porsuk.core.domain.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.TechnicalSignal

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey val symbol: String,
    val id: String,
    val name: String,
    val price: Double,
    val changePercentage: Double,
    val volume: Double,
    val peRatio: Double,
    val pbRatio: Double,
    val rsi: Double,
    val technicalSignal: String,
    val sector: String,
    val marketCap: String,
    val high52w: Double,
    val low52w: Double,
    val supportPrice: Double,
    val resistancePrice: Double,
    val aiRatingScore: Int,
    val aiSummary: String,
    val roe: Double,
    val isFavorite: Boolean
) {
    fun toDomain(): CompanyStock = CompanyStock(
        id = id,
        symbol = symbol,
        name = name,
        price = price,
        changePercentage = changePercentage,
        volume = volume,
        peRatio = peRatio,
        pbRatio = pbRatio,
        rsi = rsi,
        technicalSignal = runCatching { TechnicalSignal.valueOf(technicalSignal) }.getOrDefault(TechnicalSignal.NEUTRAL),
        sector = sector,
        marketCap = marketCap,
        high52w = high52w,
        low52w = low52w,
        supportPrice = supportPrice,
        resistancePrice = resistancePrice,
        aiRatingScore = aiRatingScore,
        aiSummary = aiSummary,
        roe = roe
    )
}

fun CompanyStock.toEntity(isFavorite: Boolean = false): StockEntity = StockEntity(
    symbol = symbol,
    id = id,
    name = name,
    price = price,
    changePercentage = changePercentage,
    volume = volume,
    peRatio = peRatio,
    pbRatio = pbRatio,
    rsi = rsi,
    technicalSignal = technicalSignal.name,
    sector = sector,
    marketCap = marketCap,
    high52w = high52w,
    low52w = low52w,
    supportPrice = supportPrice,
    resistancePrice = resistancePrice,
    aiRatingScore = aiRatingScore,
    aiSummary = aiSummary,
    roe = roe,
    isFavorite = isFavorite
)

@Entity(tableName = "portfolio_baskets")
data class PortfolioBasketEntity(
    @PrimaryKey val id: String,
    val basketName: String,
    val symbolsJson: String,
    val createdTimestamp: Long
)

@Entity(tableName = "kap_notices")
data class KapNoticeEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val companyName: String,
    val title: String,
    val summary: String,
    val date: String,
    val category: String,
    val isImportant: Boolean
)
