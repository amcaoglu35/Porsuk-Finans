package com.nexus.porsuk.di

import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalMarketsRepositoriesModule {

    @Provides
    @Singleton
    fun provideGlobalMarketRepository(impl: GlobalMarketRepositoryImpl): GlobalMarketRepository = impl

    @Provides
    @Singleton
    fun provideExchangeRepository(impl: ExchangeRepositoryImpl): ExchangeRepository = impl

    @Provides
    @Singleton
    fun provideSectorRepository(impl: SectorRepositoryImpl): SectorRepository = impl

    @Provides
    @Singleton
    fun provideGlobalIndexRepository(impl: GlobalIndexRepositoryImpl): GlobalIndexRepository = impl
}
