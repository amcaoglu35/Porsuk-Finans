package com.nexus.porsuk.data.local.entity

import androidx.room.*

/**
 * Porsuk Portfolio Engine — Portföy Ana Tablosu (PortfolioEngineEntity)
 */
@Entity(
    tableName = "engine_portfolios",
    indices = [
        Index(value = ["portfolio_id"], unique = true),
        Index(value = ["is_active"])
    ]
)
data class PortfolioEngineEntity(
    @PrimaryKey
    @ColumnInfo(name = "portfolio_id")
    val portfolioId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "portfolio_type")
    val portfolioType: String = "REAL", // REAL, VIRTUAL_PAPER_TRADING, FAMILY_SHARED

    @ColumnInfo(name = "currency")
    val currency: String = "TRY",

    @ColumnInfo(name = "total_valuation")
    val totalValuation: Double = 0.0,

    @ColumnInfo(name = "total_cost")
    val totalCost: Double = 0.0,

    @ColumnInfo(name = "daily_profit_loss")
    val dailyProfitLoss: Double = 0.0,

    @ColumnInfo(name = "total_profit_loss")
    val totalProfitLoss: Double = 0.0,

    @ColumnInfo(name = "return_rate_pct")
    val returnRatePct: Double = 0.0,

    @ColumnInfo(name = "total_dividends")
    val totalDividends: Double = 0.0,

    @ColumnInfo(name = "risk_score")
    val riskScore: Int = 1,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Porsuk Portfolio Engine — Portföy Varlık Kalemi Tablosu
 */
@Entity(
    tableName = "engine_portfolio_assets",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEngineEntity::class,
            parentColumns = ["portfolio_id"],
            childColumns = ["portfolio_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["portfolio_id"]),
        Index(value = ["symbol"])
    ]
)
data class PortfolioAssetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "asset_id")
    val assetId: Long = 0,

    @ColumnInfo(name = "portfolio_id")
    val portfolioId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "average_cost")
    val averageCost: Double,

    @ColumnInfo(name = "current_price")
    val currentPrice: Double,

    @ColumnInfo(name = "total_value")
    val totalValue: Double,

    @ColumnInfo(name = "total_cost")
    val totalCost: Double,

    @ColumnInfo(name = "profit_loss")
    val profitLoss: Double,

    @ColumnInfo(name = "profit_percent")
    val profitPercent: Double,

    @ColumnInfo(name = "asset_category")
    val assetCategory: String,

    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Porsuk Portfolio Engine — İşlem Geçmişi Tablosu
 */
@Entity(
    tableName = "engine_portfolio_transactions",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEngineEntity::class,
            parentColumns = ["portfolio_id"],
            childColumns = ["portfolio_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["portfolio_id"]),
        Index(value = ["symbol"]),
        Index(value = ["transaction_type"])
    ]
)
data class PortfolioTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    val transactionId: Long = 0,

    @ColumnInfo(name = "portfolio_id")
    val portfolioId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "transaction_type")
    val transactionType: String, // BUY, SELL, DIVIDEND, RIGHTS_ISSUE_PAID, BONUS_ISSUE_FREE, COMMISSION, TAX, CASH_DEPOSIT, CASH_WITHDRAWAL

    @ColumnInfo(name = "quantity")
    val quantity: Double,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double,

    @ColumnInfo(name = "fee")
    val fee: Double = 0.0,

    @ColumnInfo(name = "tax")
    val tax: Double = 0.0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "notes")
    val notes: String? = null
)
