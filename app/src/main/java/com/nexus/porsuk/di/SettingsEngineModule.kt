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
object SettingsRepositoriesModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository = impl

    @Provides
    @Singleton
    fun providePreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository = impl

    @Provides
    @Singleton
    fun provideThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository = impl

    @Provides
    @Singleton
    fun provideRegionRepository(impl: RegionRepositoryImpl): RegionRepository = impl

    @Provides
    @Singleton
    fun provideAccessibilityRepository(impl: AccessibilityRepositoryImpl): AccessibilityRepository = impl
}
