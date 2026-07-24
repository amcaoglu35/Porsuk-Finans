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
object MacroEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideMacroRepository(impl: MacroRepositoryImpl): MacroRepository = impl

    @Provides
    @Singleton
    fun provideMacroIndicatorRepository(impl: MacroIndicatorRepositoryImpl): MacroIndicatorRepository = impl

    @Provides
    @Singleton
    fun provideCentralBankRepository(impl: CentralBankRepositoryImpl): CentralBankRepository = impl

    @Provides
    @Singleton
    fun provideBondRepository(impl: BondRepositoryImpl): BondRepository = impl

    @Provides
    @Singleton
    fun provideFXRepository(impl: FXRepositoryImpl): FXRepository = impl

    @Provides
    @Singleton
    fun provideMacroCommodityRepository(impl: MacroCommodityRepositoryImpl): MacroCommodityRepository = impl
}
