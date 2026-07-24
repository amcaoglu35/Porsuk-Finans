package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.StrategyDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StrategyDaoModule {

    @Provides
    fun provideStrategyDao(db: PorsukDatabase): StrategyDao {
        return db.strategyDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object StrategyBuilderRepositoriesModule {

    @Provides
    @Singleton
    fun provideStrategyRepository(impl: StrategyRepositoryImpl): StrategyRepository = impl

    @Provides
    @Singleton
    fun provideStrategyTemplateRepository(impl: StrategyTemplateRepositoryImpl): StrategyTemplateRepository = impl

    @Provides
    @Singleton
    fun provideStrategyValidationRepository(impl: StrategyValidationRepositoryImpl): StrategyValidationRepository = impl

    @Provides
    @Singleton
    fun provideStrategyExecutionRepository(impl: StrategyExecutionRepositoryImpl): StrategyExecutionRepository = impl
}
