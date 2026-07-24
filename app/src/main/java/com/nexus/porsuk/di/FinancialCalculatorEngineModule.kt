package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.CalculationHistoryDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalculationHistoryDaoModule {

    @Provides
    fun provideCalculationHistoryDao(db: PorsukDatabase): CalculationHistoryDao {
        return db.calculationHistoryDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object FinancialCalculatorRepositoriesModule {

    @Provides
    @Singleton
    fun provideCalculatorRepository(impl: CalculatorRepositoryImpl): CalculatorRepository = impl

    @Provides
    @Singleton
    fun provideScenarioRepository(impl: ScenarioRepositoryImpl): ScenarioRepository = impl

    @Provides
    @Singleton
    fun provideValuationRepository(impl: ValuationRepositoryImpl): ValuationRepository = impl

    @Provides
    @Singleton
    fun provideHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository = impl
}
