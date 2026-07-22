package com.nexus.porsuk.data.model

data class StockDetails(
    val symbol: String,
    val name: String,
    val price: Double,
    val currency: String,
    val changeAmount: Double,
    val changePercentage: Double,
    val description: String = "",
    val peRatio: String = "-",
    val marketCap: String = "-",
    val yearHigh: String = "-",
    val yearLow: String = "-",
    val dividendYield: String = "-",
    val news: List<NewsItem> = emptyList(),
    val historicalPrices: List<Float> = emptyList()
)

data class NewsItem(
    val title: String,
    val source: String,
    val time: String,
    val url: String
)
