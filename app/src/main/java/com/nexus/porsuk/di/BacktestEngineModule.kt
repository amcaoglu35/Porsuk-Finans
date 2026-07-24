package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.BacktestReportDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BacktestReportDaoModule {

    @Provides
    fun provideBacktestReportDao(db: PorsukDatabase): BacktestReportDao {
        return db.backtestReportDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BacktestEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideBacktestRepository(impl: BacktestRepositoryImpl): BacktestRepository = impl

    @Provides
    @Singleton
    fun provideSimulationRepository(impl: SimulationRepositoryImpl): SimulationRepository = impl

    @Provides
    @Singleton
    fun provideMetricsRepository(impl: MetricsRepositoryImpl): MetricsRepository = impl

    @Provides
    @Singleton
    fun provideBenchmarkRepository(impl: BenchmarkRepositoryImpl): BenchmarkRepository = impl

    @Provides
    @Singleton
    fun provideBacktestDataProvider(impl: com.nexus.porsuk.data.provider.DefaultBacktestDataProvider): com.nexus.porsuk.data.provider.BacktestDataProvider = impl
}
