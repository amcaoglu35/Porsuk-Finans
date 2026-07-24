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
object PortfolioDoctorRepositoriesModule {

    @Provides
    @Singleton
    fun providePortfolioDoctorRepository(impl: PortfolioDoctorRepositoryImpl): PortfolioDoctorRepository = impl

    @Provides
    @Singleton
    fun providePortfolioHealthRepository(impl: PortfolioHealthRepositoryImpl): PortfolioHealthRepository = impl

    @Provides
    @Singleton
    fun provideDiversificationRepository(impl: DiversificationRepositoryImpl): DiversificationRepository = impl

    @Provides
    @Singleton
    fun providePortfolioAnalyticsRepository(impl: PortfolioAnalyticsRepositoryImpl): PortfolioAnalyticsRepository = impl
}
