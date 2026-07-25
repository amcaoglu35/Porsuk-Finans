package com.nexus.porsuk.di

import com.nexus.porsuk.data.institutional.InstitutionalHoldingsEngine
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InstitutionalModule {

    @Provides
    @Singleton
    fun provideInstitutionRepository(impl: InstitutionRepositoryImpl): InstitutionRepository = impl

    @Provides
    @Singleton
    fun provideInsiderRepository(impl: InsiderRepositoryImpl): InsiderRepository = impl

    @Provides
    @Singleton
    fun provideOwnershipRepository(impl: OwnershipRepositoryImpl): OwnershipRepository = impl

    @Provides
    @Singleton
    fun provideFundFlowRepository(impl: FundFlowRepositoryImpl): FundFlowRepository = impl
}
