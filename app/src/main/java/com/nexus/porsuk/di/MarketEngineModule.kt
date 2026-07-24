package com.nexus.porsuk.di

import com.nexus.porsuk.data.provider.*
import com.nexus.porsuk.data.repository.MarketEngineRepositoryImpl
import com.nexus.porsuk.domain.repository.MarketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MarketEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideMarketRepository(impl: MarketEngineRepositoryImpl): MarketRepository = impl

    @Provides
    @IntoSet
    fun provideFinnhubDataProvider(impl: FinnhubDataProviderImpl): MarketDataProvider = impl

    @Provides
    @IntoSet
    fun provideAlphaVantageDataProvider(impl: AlphaVantageDataProviderImpl): MarketDataProvider = impl

    @Provides
    @IntoSet
    fun providePolygonDataProvider(impl: PolygonDataProviderImpl): MarketDataProvider = impl

    @Provides
    @IntoSet
    fun provideTwelveDataDataProvider(impl: TwelveDataDataProviderImpl): MarketDataProvider = impl
}
