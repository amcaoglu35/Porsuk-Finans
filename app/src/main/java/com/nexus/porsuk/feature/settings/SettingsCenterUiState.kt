package com.nexus.porsuk.feature.settings

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Settings Center — UI Ekran Durumu (SettingsCenterUiState)
 */
data class SettingsCenterUiState(
    val themeSettings: AppThemeSettings = AppThemeSettings(),
    val regionPreferences: RegionPreferences = RegionPreferences(),
    val marketAiPreferences: MarketAiPreferences = MarketAiPreferences(),
    val appVersion: String = "v3.8.4",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
