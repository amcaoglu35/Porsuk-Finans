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
object EsgEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideESGRepository(impl: ESGRepositoryImpl): ESGRepository = impl

    @Provides
    @Singleton
    fun provideSustainabilityRepository(impl: SustainabilityRepositoryImpl): SustainabilityRepository = impl

    @Provides
    @Singleton
    fun provideClimateRepository(impl: ClimateRepositoryImpl): ClimateRepository = impl

    @Provides
    @Singleton
    fun provideGovernanceRepository(impl: GovernanceRepositoryImpl): GovernanceRepository = impl

    @Provides
    @Singleton
    fun provideSocialRepository(impl: SocialRepositoryImpl): SocialRepository = impl
}
