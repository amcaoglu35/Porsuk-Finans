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
object EnterpriseApiEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideEnterpriseApiRepository(impl: EnterpriseApiRepositoryImpl): EnterpriseApiRepository = impl

    @Provides
    @Singleton
    fun provideEnterpriseAuthenticationRepository(impl: EnterpriseAuthenticationRepositoryImpl): EnterpriseAuthenticationRepository = impl

    @Provides
    @Singleton
    fun provideEnterpriseWebhookRepository(impl: EnterpriseWebhookRepositoryImpl): EnterpriseWebhookRepository = impl

    @Provides
    @Singleton
    fun provideEnterpriseAutomationRepository(impl: EnterpriseAutomationRepositoryImpl): EnterpriseAutomationRepository = impl

    @Provides
    @Singleton
    fun provideEnterpriseUsageRepository(impl: EnterpriseUsageRepositoryImpl): EnterpriseUsageRepository = impl
}
