package com.nexus.porsuk.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.nexus.porsuk.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "porsuk_user_settings")

/**
 * Porsuk Finans — DataStore Ayarlar Yöneticisi (PorsukDataStoreManager)
 */
@Singleton
class PorsukDataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val LANGUAGE = stringPreferencesKey("app_language")
        val CURRENCY = stringPreferencesKey("app_currency")
        val HIDE_PORTFOLIO = booleanPreferencesKey("hide_portfolio_value")
    }

    val themeSettingsFlow: Flow<AppThemeSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val themeMode = try { AppThemeMode.valueOf(themeStr) } catch (e: Exception) { AppThemeMode.SYSTEM }
        val dynamicColor = prefs[PreferencesKeys.DYNAMIC_COLOR] ?: true
        AppThemeSettings(themeMode = themeMode, useDynamicColor = dynamicColor)
    }

    val regionPreferencesFlow: Flow<RegionPreferences> = context.dataStore.data.map { prefs ->
        val langStr = prefs[PreferencesKeys.LANGUAGE] ?: AppLanguage.TURKISH.name
        val currStr = prefs[PreferencesKeys.CURRENCY] ?: DefaultCurrency.TRY.name
        val lang = try { AppLanguage.valueOf(langStr) } catch (e: Exception) { AppLanguage.TURKISH }
        val curr = try { DefaultCurrency.valueOf(currStr) } catch (e: Exception) { DefaultCurrency.TRY }
        RegionPreferences(language = lang, currency = curr)
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LANGUAGE] = language.name
        }
    }

    suspend fun setCurrency(currency: DefaultCurrency) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.CURRENCY] = currency.name
        }
    }
}
