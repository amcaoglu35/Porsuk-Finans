package com.nexus.porsuk.core.domain.di

import com.nexus.porsuk.core.domain.repository.CorporateEventRepository
import com.nexus.porsuk.core.domain.repository.CorporateEventRepositoryImpl
import com.nexus.porsuk.core.domain.repository.FundComparisonRepository
import com.nexus.porsuk.core.domain.repository.FundComparisonRepositoryImpl
import com.nexus.porsuk.core.domain.repository.InstitutionalAnalyticsRepository
import com.nexus.porsuk.core.domain.repository.InstitutionalAnalyticsRepositoryImpl
import com.nexus.porsuk.core.domain.repository.KapRadarRepository
import com.nexus.porsuk.core.domain.repository.KapRadarRepositoryImpl
import com.nexus.porsuk.core.domain.repository.MarketRepository
import com.nexus.porsuk.core.domain.repository.MarketRepositoryImpl
import com.nexus.porsuk.core.domain.repository.NewsRepository
import com.nexus.porsuk.core.domain.repository.NewsRepositoryImpl
import com.nexus.porsuk.core.domain.repository.StockRepository
import com.nexus.porsuk.core.domain.repository.StockRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        impl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindMarketRepository(
        impl: MarketRepositoryImpl
    ): MarketRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindKapRadarRepository(
        impl: KapRadarRepositoryImpl
    ): KapRadarRepository

    @Binds
    @Singleton
    abstract fun bindCorporateEventRepository(
        impl: CorporateEventRepositoryImpl
    ): CorporateEventRepository

    @Binds
    @Singleton
    abstract fun bindInstitutionalAnalyticsRepository(
        impl: InstitutionalAnalyticsRepositoryImpl
    ): InstitutionalAnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindFundComparisonRepository(
        impl: FundComparisonRepositoryImpl
    ): FundComparisonRepository
}
