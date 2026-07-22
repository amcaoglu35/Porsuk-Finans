package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_assets")
data class StockAsset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val exchange: String,
    val purchasePrice: Double,
    val quantity: Double,
    val purchaseDate: Long,
    val fundId: Int? = null // Null means it's a standalone stock/watchlist item
)

@Entity(tableName = "funds")
data class Fund(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = ""
)
