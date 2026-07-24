package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.DividendIntelligenceDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DividendIntelligenceDaoModule {

    @Provides
    fun provideDividendIntelligenceDao(db: PorsukDatabase): DividendIntelligenceDao {
        return db.dividendIntelligenceDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DividendIntelligenceRepositoriesModule {

    @Provides
    @Singleton
    fun provideDividendRepository(impl: DividendRepositoryImpl): DividendRepository = impl

    @Provides
    @Singleton
    fun provideDividendIntelligenceCalendarRepository(impl: DividendIntelligenceCalendarRepositoryImpl): DividendIntelligenceCalendarRepository = impl

    @Provides
    @Singleton
    fun provideDividendAnalyticsRepository(impl: DividendAnalyticsRepositoryImpl): DividendAnalyticsRepository = impl

    @Provides
    @Singleton
    fun provideDividendPortfolioRepository(impl: DividendPortfolioRepositoryImpl): DividendPortfolioRepository = impl
}
