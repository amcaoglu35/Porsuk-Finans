package com.nexus.porsuk.di

import com.nexus.porsuk.data.remote.datasource.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FinnhubEngineModule {

    @Provides
    @Singleton
    fun provideFinnhubRemoteDataSource(impl: FinnhubRemoteDataSourceImpl): FinnhubRemoteDataSource = impl

    @Provides
    @Singleton
    fun provideFinnhubMarketRemoteDataSource(impl: FinnhubMarketRemoteDataSourceImpl): FinnhubMarketRemoteDataSource = impl
}
