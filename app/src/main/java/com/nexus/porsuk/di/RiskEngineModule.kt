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
object RiskEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideRiskRepository(impl: RiskRepositoryImpl): RiskRepository = impl

    @Provides
    @Singleton
    fun providePortfolioRiskRepository(impl: PortfolioRiskRepositoryImpl): PortfolioRiskRepository = impl

    @Provides
    @Singleton
    fun provideMarketRiskRepository(impl: MarketRiskRepositoryImpl): MarketRiskRepository = impl

    @Provides
    @Singleton
    fun provideFinancialRiskRepository(impl: FinancialRiskRepositoryImpl): FinancialRiskRepository = impl
}
