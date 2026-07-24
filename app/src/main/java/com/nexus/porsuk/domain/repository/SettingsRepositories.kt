package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Ayarlar Deposu Sözleşmesi (SettingsRepository)
 */
interface SettingsRepository {
    fun getAppVersion(): String
}

/**
 * 2. Tercihler Deposu Sözleşmesi (PreferencesRepository)
 */
interface PreferencesRepository {
    fun getMarketAiPreferences(): Flow<MarketAiPreferences>
    suspend fun toggleHidePortfolioValue(hidden: Boolean)
}

/**
 * 3. Tema Deposu Sözleşmesi (ThemeRepository)
 */
interface ThemeRepository {
    fun getThemeSettings(): Flow<AppThemeSettings>
    suspend fun setThemeMode(mode: AppThemeMode)
}

/**
 * 4. Bölge ve Dil Deposu Sözleşmesi (RegionRepository)
 */
interface RegionRepository {
    fun getRegionPreferences(): Flow<RegionPreferences>
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setCurrency(currency: DefaultCurrency)
}

/**
 * 5. Erişilebilirlik Deposu Sözleşmesi (AccessibilityRepository)
 */
interface AccessibilityRepository {
    fun getFontScaling(): Flow<Float>
}
