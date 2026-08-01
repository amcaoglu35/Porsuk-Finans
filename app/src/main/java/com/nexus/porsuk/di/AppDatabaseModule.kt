package com.nexus.porsuk.di

import android.content.Context
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.dao.*
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Porsuk Finans — Hilt Veritabanı ve DAO Sağlayıcı Modülü
 */
@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    @Singleton
    fun providePorsukDatabase(
        @ApplicationContext context: Context
    ): PorsukDatabase {
        return PorsukDatabase.getDatabase(context)
    }

    @Provides fun provideCompanyDao(db: PorsukDatabase): CompanyDao = db.companyDao()
    @Provides fun provideFundDao(db: PorsukDatabase): FundDao = db.fundDao()
    @Provides fun providePortfolioHoldingDao(db: PorsukDatabase): PortfolioHoldingDao = db.portfolioHoldingDao()
    @Provides fun provideWatchlistDao(db: PorsukDatabase): WatchlistItemDao = db.watchlistDao()
    @Provides fun provideAlarmDao(db: PorsukDatabase): AlarmDao = db.alarmDao()
    @Provides fun provideNewsDao(db: PorsukDatabase): NewsDao = db.newsDao()
    @Provides fun provideDividendDao(db: PorsukDatabase): DividendDao = db.dividendDao()
    @Provides fun provideEarningsDao(db: PorsukDatabase): EarningsDao = db.earningsDao()
    @Provides fun provideAiHistoryDao(db: PorsukDatabase): AIHistoryDao = db.aiHistoryDao()
    @Provides fun provideAppSettingsDao(db: PorsukDatabase): AppSettingsDao = db.appSettingsDao()
    @Provides fun provideMarketQuoteDao(db: PorsukDatabase): MarketQuoteDao = db.marketQuoteDao()
    @Provides fun provideSyncLogDao(db: PorsukDatabase): SyncLogDao = db.syncLogDao()
    @Provides fun provideSubscriptionDao(db: PorsukDatabase): SubscriptionDao = db.subscriptionDao()
    @Provides fun provideAssetDao(db: PorsukDatabase): AssetDao = db.assetDao()
    @Provides fun provideIpoCorporateDao(db: PorsukDatabase): IpoCorporateDao = db.ipoCorporateDao()
    @Provides fun provideAiEngineDao(db: PorsukDatabase): AiEngineDao = db.aiEngineDao()
    @Provides fun providePluginDao(db: PorsukDatabase): PluginDao = db.pluginDao()
    @Provides fun provideExchangeRateDao(db: PorsukDatabase): ExchangeRateDao = db.exchangeRateDao()

    @Provides
    @Singleton
    fun provideEventBus(): com.nexus.porsuk.core.common.PorsukEventBus = com.nexus.porsuk.core.common.PorsukEventBus()

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(context)
    }

    @Provides
    @Singleton
    fun provideFinanceRepository(
        assetDao: AssetDao,
        settingsManager: SettingsManager,
        eventBus: com.nexus.porsuk.core.common.PorsukEventBus,
        newsApi: com.nexus.porsuk.data.remote.api.NewsApi,
        fredRemoteDataSource: com.nexus.porsuk.data.remote.datasource.FredRemoteDataSource,
        configProvider: com.nexus.porsuk.core.network.ConfigProvider
    ): FinanceRepository {
        return FinanceRepository(
            assetDao,
            com.nexus.porsuk.data.remote.GoogleFinanceScraper(),
            eventBus,
            com.nexus.porsuk.data.remote.FinnhubService(configProvider.getFinnhubKey()),
            com.nexus.porsuk.data.remote.YahooFinanceService(configProvider.getYahooRapidApiKey()),
            settingsManager,
            newsApi,
            fredRemoteDataSource
        )
    }
}

/**
 * Porsuk Finans — Hilt Repository Enjeksiyon Modülü
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCompanyRepository(impl: CompanyRepositoryImpl): CompanyRepository = impl

    @Provides
    @Singleton
    fun provideFundRepository(impl: FundRepositoryImpl): FundRepository = impl

    @Provides
    @Singleton
    fun providePortfolioHoldingRepository(impl: PortfolioHoldingRepositoryImpl): PortfolioHoldingRepository = impl

    @Provides
    @Singleton
    fun provideWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository = impl

    @Provides
    @Singleton
    fun provideAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository = impl

    @Provides
    @Singleton
    fun provideNewsRepository(impl: NewsRepositoryImpl): NewsRepository = impl

    @Provides
    @Singleton
    fun provideEarningsRepository(impl: EarningsRepositoryImpl): EarningsRepository = impl

    @Provides
    @Singleton
    fun provideAIHistoryRepository(impl: AIHistoryRepositoryImpl): AIHistoryRepository = impl

    @Provides
    @Singleton
    fun provideAiEngineRepository(impl: AiEngineRepositoryImpl): AiEngineRepository = impl

    @Provides
    @Singleton
    fun provideAppSettingsRepository(impl: AppSettingsRepositoryImpl): AppSettingsRepository = impl

    @Provides
    @Singleton
    fun provideBasketRepository(impl: com.nexus.porsuk.data.repository.BasketRepositoryImpl): com.nexus.porsuk.domain.repository.BasketRepository = impl
}
