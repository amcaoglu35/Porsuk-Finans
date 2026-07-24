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
object PremiumEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository = impl

    @Provides
    @Singleton
    fun provideBillingRepository(impl: BillingRepositoryImpl): BillingRepository = impl

    @Provides
    @Singleton
    fun provideEntitlementRepository(impl: EntitlementRepositoryImpl): EntitlementRepository = impl

    @Provides
    @Singleton
    fun provideMembershipRepository(impl: MembershipRepositoryImpl): MembershipRepository = impl
}
