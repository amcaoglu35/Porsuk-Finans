package com.nexus.porsuk.di

import com.nexus.porsuk.data.provider.FundIntelligenceProvider
import com.nexus.porsuk.data.provider.TefasFundProvider
import com.nexus.porsuk.data.repository.FundIntelligenceRepositoryImpl
import com.nexus.porsuk.domain.repository.FundIntelligenceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FundIntelligenceModule {

    @Binds
    @Singleton
    abstract fun bindFundIntelligenceRepository(
        impl: FundIntelligenceRepositoryImpl
    ): FundIntelligenceRepository

    @Binds
    @IntoSet
    abstract fun bindTefasFundProvider(
        impl: TefasFundProvider
    ): FundIntelligenceProvider

    companion object {
        @Provides
        @Singleton
        fun provideFundIntelligenceProviders(
            providers: Set<@JvmSuppressWildcards FundIntelligenceProvider>
        ): List<FundIntelligenceProvider> = providers.toList()
    }
}
