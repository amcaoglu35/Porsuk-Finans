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
object IpoCorporateModule {

    @Provides
    @Singleton
    fun provideIpoRepository(impl: IpoRepositoryImpl): IpoRepository = impl

    @Provides
    @Singleton
    fun provideCorporateActionRepository(impl: CorporateActionRepositoryImpl): CorporateActionRepository = impl

    @Provides
    @Singleton
    fun provideDividendRepositoryPro(impl: DividendRepositoryProImpl): DividendRepositoryPro = impl

    @Provides
    @Singleton
    fun provideCorporateCalendarRepository(impl: CorporateCalendarRepositoryImpl): CorporateCalendarRepository = impl
}
