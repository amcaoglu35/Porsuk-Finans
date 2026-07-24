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
object TechnicalEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideTechnicalRepository(impl: TechnicalRepositoryImpl): TechnicalRepository = impl

    @Provides
    @Singleton
    fun provideIndicatorRepository(impl: IndicatorRepositoryImpl): IndicatorRepository = impl

    @Provides
    @Singleton
    fun provideSignalRepository(impl: SignalRepositoryImpl): SignalRepository = impl
}
