package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.SmartAlertDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmartAlertDaoModule {

    @Provides
    fun provideSmartAlertDao(db: PorsukDatabase): SmartAlertDao {
        return db.smartAlertDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SmartAlertRepositoriesModule {

    @Provides
    @Singleton
    fun provideSmartAlertRepository(impl: SmartAlertRepositoryImpl): SmartAlertRepository = impl

    @Provides
    @Singleton
    fun provideAppNotificationRepository(impl: AppNotificationRepositoryImpl): AppNotificationRepository = impl

    @Provides
    @Singleton
    fun provideSystemAlarmRepository(impl: SystemAlarmRepositoryImpl): SystemAlarmRepository = impl
}
