package com.nexus.porsuk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nexus.porsuk.data.local.dao.*
import com.nexus.porsuk.data.local.entity.*

/**
 * Porsuk Finans — Ana Room Veritabanı (PorsukDatabase - Version 39)
 *
 * Security & Privacy Center `SecurityAuditEntity`, `SecuritySessionEntity` ve `SecurityAuditDao` güncellemelerini içerir.
 */
@Database(
    entities = [
        SecurityAuditEntity::class,
        SecuritySessionEntity::class,
        CloudSyncQueueEntity::class,
        UserDeviceEntity::class,
        AutomationRuleEntity::class,
        NotificationCenterEntity::class,
        BrokerAccountEntity::class,
        SubscriptionEntity::class,
        AiWorkspaceEntity::class,
        CalculationHistoryEntity::class,
        DividendIntelligenceEntity::class,
        StrategyEntity::class,
        BacktestReportEntity::class,
        ScreenerFilterPresetEntity::class,
        MasterScoreHistoryEntity::class,
        EconomicEventEntity::class,
        EarningsCalendarEntity::class,
        DividendCalendarProEntity::class,
        NewsArticleEntity::class,
        NewsCategoryEntity::class,
        NewsSourceEntity::class,
        SmartAlertEntity::class,
        NotificationHistoryEntity::class,
        WatchlistGroupEntity::class,
        WatchlistItemProEntity::class,
        WatchlistAlertStubEntity::class,
        PortfolioEngineEntity::class,
        PortfolioAssetEntity::class,
        PortfolioTransactionEntity::class,
        TefasFundEntity::class,
        CompanyEntity::class,
        FundEntity::class,
        PortfolioHoldingEntity::class,
        WatchlistItemEntity::class,
        AlarmEntity::class,
        NewsEntity::class,
        DividendEntity::class,
        EarningsEntity::class,
        AIHistoryEntity::class,
        AppSettingsEntity::class,
        // Legacy/Existing Entities
        Company::class,
        Basket::class,
        BasketItem::class,
        WatchlistItem::class,
        PriceSnapshot::class,
        CachedCompanyInfo::class,
        NewsItemEntity::class,
        PriceAlert::class,
        PortfolioHistoryEntry::class,
        PortfolioTransaction::class,
        StockHistoryEntry::class,
        KaziRun::class,
        KaziCandidate::class,
        KaziBasket::class,
        KaziBasketItem::class,
        KaziWatch::class,
        DividendCalendarEntry::class,
        IpoCalendarEntry::class,
        EconomicEventEntry::class,
        DecisionJournalEntry::class,
        AiAnalysisAuditEntry::class,
        PorsukBrainMemory::class,
        AiInsightEntry::class,
        MarketQuoteEntity::class,
        SyncLogEntity::class
    ],
    version = 39,
    exportSchema = false
)
abstract class PorsukDatabase : RoomDatabase() {

    abstract fun securityAuditDao(): SecurityAuditDao
    abstract fun cloudSyncDao(): CloudSyncDao
    abstract fun notificationAutomationDao(): NotificationAutomationDao
    abstract fun brokerAccountDao(): BrokerAccountDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun aiWorkspaceDao(): AiWorkspaceDao
    abstract fun calculationHistoryDao(): CalculationHistoryDao
    abstract fun dividendIntelligenceDao(): DividendIntelligenceDao
    abstract fun strategyDao(): StrategyDao
    abstract fun backtestReportDao(): BacktestReportDao
    abstract fun screenerFilterDao(): ScreenerFilterDao
    abstract fun masterScoreDao(): MasterScoreDao
    abstract fun calendarDao(): CalendarDao
    abstract fun newsIntelligenceDao(): NewsIntelligenceDao
    abstract fun smartAlertDao(): SmartAlertDao
    abstract fun watchlistProDao(): WatchlistProDao
    abstract fun portfolioEngineDao(): PortfolioEngineDao
    abstract fun tefasFundDao(): TefasFundDao
    abstract fun companyDao(): CompanyDao
    abstract fun fundDao(): FundDao
    abstract fun portfolioHoldingDao(): PortfolioHoldingDao
    abstract fun watchlistDao(): WatchlistItemDao
    abstract fun alarmDao(): AlarmDao
    abstract fun newsDao(): NewsDao
    abstract fun dividendDao(): DividendDao
    abstract fun earningsDao(): EarningsDao
    abstract fun aiHistoryDao(): AIHistoryDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun assetDao(): AssetDao
    abstract fun marketQuoteDao(): MarketQuoteDao
    abstract fun syncLogDao(): SyncLogDao

    companion object {
        @Volatile
        private var INSTANCE: PorsukDatabase? = null

        fun getDatabase(context: Context): PorsukDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PorsukDatabase::class.java,
                    "porsuk_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
