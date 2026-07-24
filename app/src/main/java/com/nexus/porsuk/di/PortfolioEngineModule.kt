package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.PortfolioEngineDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PortfolioEngineDaoModule {

    @Provides
    fun providePortfolioEngineDao(db: PorsukDatabase): PortfolioEngineDao {
        return db.portfolioEngineDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PortfolioEngineRepositoriesModule {

    @Provides
    @Singleton
    fun providePortfolioEngineRepository(impl: PortfolioEngineRepositoryImpl): PortfolioEngineRepository = impl

    @Provides
    @Singleton
    fun providePortfolioTransactionRepository(impl: PortfolioTransactionRepositoryImpl): PortfolioTransactionRepository = impl

    @Provides
    @Singleton
    fun providePortfolioAssetRepository(impl: PortfolioAssetRepositoryImpl): PortfolioAssetRepository = impl

    @Provides
    @Singleton
    fun providePortfolioPerformanceRepository(impl: PortfolioPerformanceRepositoryImpl): PortfolioPerformanceRepository = impl
}
