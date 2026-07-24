package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.CloudSyncDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudSyncDaoModule {

    @Provides
    fun provideCloudSyncDao(db: PorsukDatabase): CloudSyncDao {
        return db.cloudSyncDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object CloudSyncRepositoriesModule {

    @Provides
    @Singleton
    fun provideCloudRepository(impl: CloudRepositoryImpl): CloudRepository = impl

    @Provides
    @Singleton
    fun provideSyncRepository(impl: SyncRepositoryImpl): SyncRepository = impl

    @Provides
    @Singleton
    fun provideBackupRepository(impl: BackupRepositoryImpl): BackupRepository = impl

    @Provides
    @Singleton
    fun provideDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository = impl
}
