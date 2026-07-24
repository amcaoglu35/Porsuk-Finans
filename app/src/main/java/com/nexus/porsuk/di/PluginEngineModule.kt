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
object PluginEngineRepositoriesModule {

    @Provides
    @Singleton
    fun providePluginRepository(impl: PluginRepositoryImpl): PluginRepository = impl

    @Provides
    @Singleton
    fun provideSDKRepository(impl: SDKRepositoryImpl): SDKRepository = impl

    @Provides
    @Singleton
    fun providePermissionRepository(impl: PermissionRepositoryImpl): PermissionRepository = impl

    @Provides
    @Singleton
    fun provideManifestRepository(impl: ManifestRepositoryImpl): ManifestRepository = impl
}
