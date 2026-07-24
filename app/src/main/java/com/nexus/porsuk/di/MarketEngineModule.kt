package com.nexus.porsuk.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MarketEngineRepositoriesModule {
    // Market data repositories provided via DataCenterModule
}
