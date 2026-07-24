package com.nexus.porsuk.domain.model

/**
 * Tema ve Görünüm Ayarları (AppThemeSettings)
 */
data class AppThemeSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    val fontScaling: Float = 1.0f,
    val useHighContrast: Boolean = false
)

/**
 * Dil ve Bölge Tercihleri (RegionPreferences)
 */
data class RegionPreferences(
    val language: AppLanguage = AppLanguage.TURKISH,
    val currency: DefaultCurrency = DefaultCurrency.TRY,
    val dateFormat: String = "dd.MM.yyyy",
    val numberFormat: String = "1.234,56 ₺"
)

/**
 * Piyasa ve AI Tercihleri (MarketAiPreferences)
 */
data class MarketAiPreferences(
    val defaultExchange: String = "BIST",
    val defaultTimeframe: String = "1D",
    val defaultAiModel: String = "Orakul Ultra 2.0",
    val isPortfolioValueHidden: Boolean = false,
    val isBiometricOnLaunch: Boolean = true
)
