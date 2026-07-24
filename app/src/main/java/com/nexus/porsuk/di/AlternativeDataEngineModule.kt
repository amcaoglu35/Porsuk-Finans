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
object AlternativeDataEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideAlternativeDataRepository(impl: AlternativeDataRepositoryImpl): AlternativeDataRepository = impl

    @Provides
    @Singleton
    fun provideSatelliteRepository(impl: SatelliteRepositoryImpl): SatelliteRepository = impl

    @Provides
    @Singleton
    fun provideShippingRepository(impl: ShippingRepositoryImpl): ShippingRepository = impl

    @Provides
    @Singleton
    fun provideAviationRepository(impl: AviationRepositoryImpl): AviationRepository = impl

    @Provides
    @Singleton
    fun provideRetailRepository(impl: RetailRepositoryImpl): RetailRepository = impl

    @Provides
    @Singleton
    fun provideEnergyRepository(impl: EnergyRepositoryImpl): EnergyRepository = impl
}
