package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_stocks")
data class MarketStock(
    @PrimaryKey val symbol: String,
    val exchange: String,
    val name: String,
    val type: String // "BIST", "NASDAQ", "FOREX" vb.
)
