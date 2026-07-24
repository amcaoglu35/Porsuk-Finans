package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.ScreenerFilterDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScreenerFilterDaoModule {

    @Provides
    fun provideScreenerFilterDao(db: PorsukDatabase): ScreenerFilterDao {
        return db.screenerFilterDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ScreenerUltimateRepositoriesModule {

    @Provides
    @Singleton
    fun provideScreenerRepository(impl: ScreenerRepositoryImpl): ScreenerRepository = impl

    @Provides
    @Singleton
    fun provideFilterRepository(impl: FilterRepositoryImpl): FilterRepository = impl

    @Provides
    @Singleton
    fun providePresetRepository(impl: PresetRepositoryImpl): PresetRepository = impl

    @Provides
    @Singleton
    fun provideScanResultRepository(impl: ScanResultRepositoryImpl): ScanResultRepository = impl
}
