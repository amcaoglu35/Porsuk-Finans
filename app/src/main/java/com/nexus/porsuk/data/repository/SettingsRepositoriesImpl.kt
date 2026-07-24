package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.datastore.PorsukDataStoreManager
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {
    override fun getAppVersion(): String = "v3.8.4 Enterprise Build"
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor() : PreferencesRepository {
    private var isHidden = false

    override fun getMarketAiPreferences(): Flow<MarketAiPreferences> = flow {
        emit(MarketAiPreferences(isPortfolioValueHidden = isHidden))
    }

    override suspend fun toggleHidePortfolioValue(hidden: Boolean) {
        isHidden = hidden
    }
}

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val dataStoreManager: PorsukDataStoreManager
) : ThemeRepository {

    override fun getThemeSettings(): Flow<AppThemeSettings> = dataStoreManager.themeSettingsFlow

    override suspend fun setThemeMode(mode: AppThemeMode) {
        dataStoreManager.setThemeMode(mode)
    }
}

@Singleton
class RegionRepositoryImpl @Inject constructor(
    private val dataStoreManager: PorsukDataStoreManager
) : RegionRepository {

    override fun getRegionPreferences(): Flow<RegionPreferences> = dataStoreManager.regionPreferencesFlow

    override suspend fun setLanguage(language: AppLanguage) {
        dataStoreManager.setLanguage(language)
    }

    override suspend fun setCurrency(currency: DefaultCurrency) {
        dataStoreManager.setCurrency(currency)
    }
}

@Singleton
class AccessibilityRepositoryImpl @Inject constructor() : AccessibilityRepository {
    override fun getFontScaling(): Flow<Float> = flow {
        emit(1.0f)
    }
}
