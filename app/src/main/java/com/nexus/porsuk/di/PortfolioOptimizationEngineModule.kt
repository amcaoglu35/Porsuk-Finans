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
object PortfolioOptimizationEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideOptimizationRepository(impl: OptimizationRepositoryImpl): OptimizationRepository = impl

    @Provides
    @Singleton
    fun provideAllocationRepository(impl: AllocationRepositoryImpl): AllocationRepository = impl

    @Provides
    @Singleton
    fun provideOptimizationRiskRepository(impl: OptimizationRiskRepositoryImpl): OptimizationRiskRepository = impl

    @Provides
    @Singleton
    fun provideOptimizationScenarioRepository(impl: OptimizationScenarioRepositoryImpl): OptimizationScenarioRepository = impl

    @Provides
    @Singleton
    fun provideRebalancingRepository(impl: RebalancingRepositoryImpl): RebalancingRepository = impl
}
