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
object RegulatoryEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideFilingRepository(impl: FilingRepositoryImpl): FilingRepository = impl

    @Provides
    @Singleton
    fun provideDisclosureRepository(impl: DisclosureRepositoryImpl): DisclosureRepository = impl

    @Provides
    @Singleton
    fun provideDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository = impl

    @Provides
    @Singleton
    fun provideClassificationRepository(impl: ClassificationRepositoryImpl): ClassificationRepository = impl

    @Provides
    @Singleton
    fun provideTimelineRepository(impl: TimelineRepositoryImpl): TimelineRepository = impl
}
