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
object ChartCenterRepositoriesModule {

    @Provides
    @Singleton
    fun provideChartRepository(impl: ChartRepositoryImpl): ChartRepository = impl

    @Provides
    @Singleton
    fun provideDrawingRepository(impl: DrawingRepositoryImpl): DrawingRepository = impl

    @Provides
    @Singleton
    fun provideChartIndicatorRepository(impl: ChartIndicatorRepositoryImpl): ChartIndicatorRepository = impl
}
