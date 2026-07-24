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
object OrakulCoreRepositoriesModule {

    @Provides
    @Singleton
    fun provideAnalysisRepository(impl: AnalysisRepositoryImpl): AnalysisRepository = impl

    @Provides
    @Singleton
    fun provideFinancialAnalysisRepository(impl: FinancialAnalysisRepositoryImpl): FinancialAnalysisRepository = impl

    @Provides
    @Singleton
    fun provideTechnicalAnalysisRepository(impl: TechnicalAnalysisRepositoryImpl): TechnicalAnalysisRepository = impl

    @Provides
    @Singleton
    fun provideOrakulRiskRepository(impl: OrakulRiskRepositoryImpl): OrakulRiskRepository = impl
}
