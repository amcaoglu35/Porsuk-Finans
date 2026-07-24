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
object QuantResearchEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideResearchRepository(impl: ResearchRepositoryImpl): ResearchRepository = impl

    @Provides
    @Singleton
    fun provideFactorRepository(impl: FactorRepositoryImpl): FactorRepository = impl

    @Provides
    @Singleton
    fun provideStatisticsRepository(impl: StatisticsRepositoryImpl): StatisticsRepository = impl

    @Provides
    @Singleton
    fun provideDatasetRepository(impl: DatasetRepositoryImpl): DatasetRepository = impl

    @Provides
    @Singleton
    fun provideQuantWorkspaceRepository(impl: QuantWorkspaceRepositoryImpl): QuantWorkspaceRepository = impl
}
