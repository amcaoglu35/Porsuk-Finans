package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.CalendarDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarDaoModule {

    @Provides
    fun provideCalendarDao(db: PorsukDatabase): CalendarDao {
        return db.calendarDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object CalendarRepositoriesModule {

    @Provides
    @Singleton
    fun provideCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository = impl

    @Provides
    @Singleton
    fun provideEconomicRepository(impl: EconomicRepositoryImpl): EconomicRepository = impl

    @Provides
    @Singleton
    fun provideEarningsCalendarRepository(impl: EarningsCalendarRepositoryImpl): EarningsCalendarRepository = impl

    @Provides
    @Singleton
    fun provideDividendCalendarRepository(impl: DividendCalendarRepositoryImpl): DividendCalendarRepository = impl
}
