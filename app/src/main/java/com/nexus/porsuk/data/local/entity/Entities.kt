package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey val symbol: String,
    val name: String,
    val market: String, // BIST, NASDAQ, NYSE, FRA, EURONEXT
    val logoUrl: String?, // Clearbit veya diğer CDN servislerinden logo linkleri
    val logoInitials: String, // Logo internetten yüklenemezse yedek harfler
    val sector: String, // Sektörel dağılım analizi için sektör bilgisi
    var currentPrice: Double = 0.0, // Anlık borsa fiyatı
    var changePercent: Double = 0.0, // Günlük yüzde değişim oranı
    var about: String? = null, // Şirket hakkında kısa bilgi
    var lastUpdated: Long = 0L // Son güncellenme zamanı (Timestamp)
)

@Entity(tableName = "baskets")
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val market: String,
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = "Dengeli"
)

@Entity(
    tableName = "basket_items",
    foreignKeys = [
        ForeignKey(
            entity = Basket::class,
            parentColumns = ["id"],
            childColumns = ["basketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["basketId"])]
)
data class BasketItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val basketId: Int,
    val symbol: String,
    val quantity: Double,
    val buyPrice: Double,
    val buyDate: Long
)

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey val symbol: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_snapshots")
data class PriceSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val price: Double,
    val changePercent: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val interval: String // MINUTE, HOUR, DAY, MONTH, YEAR
)

@Entity(tableName = "cached_company_info")
data class CachedCompanyInfo(
    @PrimaryKey val symbol: String,
    val about: String,
    val peRatio: Double?,
    val marketCap: String?,
    val week52High: Double?,
    val week52Low: Double?,
    val dividendYield: Double?,
    val nextDividendDate: Long? = null, // ITEM 3: Temettü tarihi
    val volume: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "news_items")
data class NewsItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val title: String,
    val summary: String? = null,
    val source: String,
    val publishedAt: Long,
    val url: String,
    val imageUrl: String? = null,
    val sentiment: String? = null // POSITIVE, NEGATIVE, NEUTRAL
)

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val market: String,
    val targetPrice: Double? = null,
    val targetChangePct: Double? = null, // ITEM 8: Yüzde değişim hedefi
    val alertType: String = "ABOVE", // ABOVE, BELOW, PERCENT_UP, PERCENT_DOWN, WEEK52_HIGH
    val isAbove: Boolean = true, // for backward compatibility
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_history")
data class PortfolioHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalValue: Double
)

@Entity(tableName = "stock_history")
data class StockHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Basket::class,
            parentColumns = ["id"],
            childColumns = ["basketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["basketId"])]
)
data class PortfolioTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val basketId: Int,
    val symbol: String,
    val quantity: Double,
    val price: Double,
    val isBuy: Boolean, // true = Buy, false = Sell
    val realizedPnL: Double = 0.0, // calculated for Sell transactions
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "kazi_runs")
data class KaziRun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val status: String, // IDLE, RUNNING, COMPLETED, FAILED
    val currentStep: Int = 0, // 0 to 6
    val riskProfile: String, // CONSERVATIVE, BALANCED, AGGRESSIVE
    val horizon: String, // SHORT, MEDIUM, LONG
    val capital: Double? = null,
    val excludedSectors: String, // comma separated
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(
    tableName = "kazi_candidates",
    foreignKeys = [
        ForeignKey(
            entity = KaziRun::class,
            parentColumns = ["id"],
            childColumns = ["runId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["runId"])]
)
data class KaziCandidate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val runId: Int,
    val symbol: String,
    val kScore: Int,
    val aScore: Int,
    val zScore: Int,
    val iScore: Int,
    val compositeScore: Int,
    val bullCase: String,
    val bearCase: String,
    val selected: Boolean,
    val rejectionReason: String? = null
)

@Entity(
    tableName = "kazi_baskets",
    foreignKeys = [
        ForeignKey(
            entity = KaziRun::class,
            parentColumns = ["id"],
            childColumns = ["runId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["runId"])]
)
data class KaziBasket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val runId: Int?,
    val basketName: String,
    val totalWeight: Double,
    val cashBufferPct: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "kazi_basket_items",
    foreignKeys = [
        ForeignKey(
            entity = KaziBasket::class,
            parentColumns = ["id"],
            childColumns = ["basketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["basketId"])]
)
data class KaziBasketItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val basketId: Int,
    val symbol: String,
    val weightPct: Double
)

@Entity(tableName = "kazi_watches")
data class KaziWatch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val basketId: Int,
    val symbol: String,
    val lastScore: Int,
    val scoreHistory: String, // comma separated historical scores
    val notifyThreshold: Int = 15
)

@Entity(tableName = "dividend_calendar")
data class DividendCalendarEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val companyName: String,
    val exDividendDate: Long, // Temettü hak kazanma tarihi
    val paymentDate: Long, // Ödeme tarihi
    val rate: Double, // Hisse başına brüt temettü (TL/USD/EUR)
    val yieldPercentage: Double, // Verim %
    val market: String // BIST, NASDAQ, FRA etc.
)

@Entity(tableName = "ipo_calendar")
data class IpoCalendarEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String, // Yeni halka arz sembolü
    val companyName: String,
    val startDate: Long, // Talep toplama başlangıç tarihi
    val endDate: Long, // Talep toplama bitiş tarihi
    val price: Double, // Halka arz fiyatı (TL/USD/EUR)
    val lotQuantity: Long, // Halka arz lot miktarı (toplam lot)
    val distributionMethod: String, // Eşit Dağıtım, Oransal Dağıtım
    val isCatkatEnabled: Boolean = false, // Katılım endeksine uygun mu?
    val broker: String, // Lider aracı kurum
    val status: String // UPCOMING, ACTIVE, COMPLETED (Tamamlananlar borsada işlem görüyor)
)

@Entity(tableName = "economic_events")
data class EconomicEventEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val country: String, // TR, US, EU etc.
    val date: Long,
    val importance: String, // HIGH, MEDIUM, LOW
    val previousValue: String?,
    val expectedValue: String?,
    val actualValue: String?,
    val comment: String
)

@Entity(tableName = "income_statements", primaryKeys = ["symbol", "date"])
data class IncomeStatementEntity(
    val symbol: String,
    val date: String,
    val revenue: Double,
    val grossProfit: Double,
    val ebitda: Double,
    val netIncome: Double,
    val eps: Double
)

@Entity(tableName = "balance_sheets", primaryKeys = ["symbol", "date"])
data class BalanceSheetEntity(
    val symbol: String,
    val date: String,
    val totalAssets: Double,
    val totalLiabilities: Double,
    val totalEquity: Double,
    val netDebt: Double
)

@Entity(tableName = "cash_flows", primaryKeys = ["symbol", "date"])
data class CashFlowEntity(
    val symbol: String,
    val date: String,
    val operatingCashFlow: Double,
    val freeCashFlow: Double
)

@Entity(tableName = "company_ratios", primaryKeys = ["symbol", "date"])
data class CompanyRatioEntity(
    val symbol: String,
    val date: String,
    val roe: Double,
    val roa: Double,
    val peRatio: Double,
    val pbRatio: Double,
    val currentRatio: Double,
    val debtToEquity: Double
)

@Entity(tableName = "macro_data", primaryKeys = ["seriesId", "date"])
data class MacroDataEntity(
    val seriesId: String,
    val date: String,
    val value: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

