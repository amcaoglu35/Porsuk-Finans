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
object StreamingEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideStreamingRepository(impl: StreamingRepositoryImpl): StreamingRepository = impl

    @Provides
    @Singleton
    fun provideWebSocketRepository(impl: WebSocketRepositoryImpl): WebSocketRepository = impl

    @Provides
    @Singleton
    fun provideStreamSymbolSubscriptionRepository(impl: StreamSymbolSubscriptionRepositoryImpl): StreamSymbolSubscriptionRepository = impl

    @Provides
    @Singleton
    fun provideTickRepository(impl: TickRepositoryImpl): TickRepository = impl
}
