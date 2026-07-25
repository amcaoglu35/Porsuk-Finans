package com.nexus.porsuk.di

import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.data.transcript.TranscriptEngine
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TranscriptModule {

    @Provides
    @Singleton
    fun provideTranscriptRepository(impl: TranscriptRepositoryImpl): TranscriptRepository = impl

    @Provides
    @Singleton
    fun provideEarningsCallRepository(impl: EarningsCallRepositoryImpl): EarningsCallRepository = impl

    @Provides
    @Singleton
    fun provideSpeakerRepository(impl: SpeakerRepositoryImpl): SpeakerRepository = impl

    @Provides
    @Singleton
    fun provideTranscriptSearchRepository(impl: TranscriptSearchRepositoryImpl): TranscriptSearchRepository = impl
}
