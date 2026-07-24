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
object DerivativesEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideDerivativesRepository(impl: DerivativesRepositoryImpl): DerivativesRepository = impl

    @Provides
    @Singleton
    fun provideOptionsRepository(impl: OptionsRepositoryImpl): OptionsRepository = impl

    @Provides
    @Singleton
    fun provideFuturesRepository(impl: FuturesRepositoryImpl): FuturesRepository = impl

    @Provides
    @Singleton
    fun provideGreeksRepository(impl: GreeksRepositoryImpl): GreeksRepository = impl

    @Provides
    @Singleton
    fun provideOptionStrategyRepository(impl: OptionStrategyRepositoryImpl): OptionStrategyRepository = impl
}
