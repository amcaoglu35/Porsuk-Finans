package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.MasterScoreDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MasterScoreDaoModule {

    @Provides
    fun provideMasterScoreDao(db: PorsukDatabase): MasterScoreDao {
        return db.masterScoreDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object MasterScoreRepositoriesModule {

    @Provides
    @Singleton
    fun provideMasterScoreRepository(impl: MasterScoreRepositoryImpl): MasterScoreRepository = impl

    @Provides
    @Singleton
    fun provideScoreHistoryRepository(impl: ScoreHistoryRepositoryImpl): ScoreHistoryRepository = impl

    @Provides
    @Singleton
    fun provideScoreCalculationRepository(impl: ScoreCalculationRepositoryImpl): ScoreCalculationRepository = impl
}
