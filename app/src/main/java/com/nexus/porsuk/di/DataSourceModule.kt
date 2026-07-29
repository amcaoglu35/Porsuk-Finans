package com.nexus.porsuk.di

import com.nexus.porsuk.data.remote.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFmpRemoteDataSource(
        impl: FmpRemoteDataSourceImpl
    ): FmpRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindNewsRemoteDataSource(
        impl: NewsRemoteDataSourceImpl
    ): NewsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFredRemoteDataSource(
        impl: FredRemoteDataSourceImpl
    ): FredRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindExchangeRemoteDataSource(
        impl: ExchangeRemoteDataSourceImpl
    ): ExchangeRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFinnhubRemoteDataSource(
        impl: FinnhubRemoteDataSourceImpl
    ): FinnhubRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFinnhubMarketRemoteDataSource(
        impl: FinnhubMarketRemoteDataSourceImpl
    ): FinnhubMarketRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTefasRemoteDataSource(
        impl: TefasRemoteDataSourceImpl
    ): TefasRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTefasEngineRemoteDataSource(
        impl: TefasEngineRemoteDataSourceImpl
    ): TefasEngineRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindEconomicCalendarRemoteDataSource(
        impl: EconomicCalendarRemoteDataSourceImpl
    ): EconomicCalendarRemoteDataSource
}
