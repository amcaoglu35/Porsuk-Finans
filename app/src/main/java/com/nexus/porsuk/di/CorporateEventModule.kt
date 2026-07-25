package com.nexus.porsuk.di

import com.nexus.porsuk.data.ma.CorporateEventEngine
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CorporateEventModule {

    @Provides
    @Singleton
    fun provideCorporateEventRepository(impl: CorporateEventRepositoryImpl): CorporateEventRepository = impl

    @Provides
    @Singleton
    fun provideMergerRepository(impl: MergerRepositoryImpl): MergerRepository = impl

    @Provides
    @Singleton
    fun provideAcquisitionRepository(impl: AcquisitionRepositoryImpl): AcquisitionRepository = impl

    @Provides
    @Singleton
    fun provideDealAnalyticsRepository(impl: DealAnalyticsRepositoryImpl): DealAnalyticsRepository = impl
}
