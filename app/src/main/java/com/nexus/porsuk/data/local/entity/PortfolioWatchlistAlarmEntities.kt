package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Finans — Portföy Varlık Kalemi (Portfolio Holding Entity)
 */
@Entity(
    tableName = "db_portfolio_holdings",
    indices = [
        Index(value = ["symbol"])
    ]
)
data class PortfolioHoldingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String, // Hisse veya Fon Kodu

    @ColumnInfo(name = "quantity")
    val quantity: Double, // Adet

    @ColumnInfo(name = "average_cost")
    val averageCost: Double, // Ortalama Maliyet

    @ColumnInfo(name = "current_price")
    val currentPrice: Double, // Anlık Fiyat

    @ColumnInfo(name = "total_value")
    val totalValue: Double, // Toplam Tutar (quantity * currentPrice)

    @ColumnInfo(name = "profit_loss")
    val profitLoss: Double, // Kar/Zarar Tutarı

    @ColumnInfo(name = "profit_percent")
    val profitPercent: Double, // Kar/Zarar Oranı (%)

    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Long = System.currentTimeMillis()
)

/**
 * Porsuk Finans — Takip Listesi Kalemi (Watchlist Item Entity)
 */
@Entity(
    tableName = "db_watchlist_items",
    indices = [
        Index(value = ["symbol"], unique = true)
    ]
)
data class WatchlistItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "notes")
    val notes: String? = null
)

/**
 * Porsuk Finans — Fiyat Alarmı Varlık Tanımı (Alarm Entity)
 */
@Entity(
    tableName = "db_alarms",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["is_enabled"])
    ]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "type")
    val type: String, // ABOVE, BELOW, PCT_CHANGE

    @ColumnInfo(name = "target_value")
    val targetValue: Double,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis()
)
