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
object DevOpsEngineRepositoriesModule {

    @Provides
    @Singleton
    fun provideBuildRepository(impl: BuildRepositoryImpl): BuildRepository = impl

    @Provides
    @Singleton
    fun provideReleaseRepository(impl: ReleaseRepositoryImpl): ReleaseRepository = impl

    @Provides
    @Singleton
    fun provideVersionRepository(impl: VersionRepositoryImpl): VersionRepository = impl

    @Provides
    @Singleton
    fun provideQualityRepository(impl: QualityRepositoryImpl): QualityRepository = impl

    @Provides
    @Singleton
    fun providePipelineRepository(impl: PipelineRepositoryImpl): PipelineRepository = impl
}
