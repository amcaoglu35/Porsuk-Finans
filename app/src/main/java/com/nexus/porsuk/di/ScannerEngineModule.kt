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
object ScannerEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideScannerRepository(impl: ScannerRepositoryImpl): ScannerRepository = impl

    @Provides
    @Singleton
    fun provideScannerFilterRepository(impl: ScannerFilterRepositoryImpl): ScannerFilterRepository = impl

    @Provides
    @Singleton
    fun provideScanHistoryRepository(impl: ScanHistoryRepositoryImpl): ScanHistoryRepository = impl
}
