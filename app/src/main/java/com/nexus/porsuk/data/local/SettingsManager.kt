@file:Suppress("DEPRECATION")

package com.nexus.porsuk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private val masterKey = try {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    } catch (e: Exception) {
        null
    }

    private val encryptedPrefs = try {
        masterKey?.let {
            EncryptedSharedPreferences.create(
                context,
                "secure_settings",
                it,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    } catch (e: Exception) {
        try {
            context.deleteSharedPreferences("secure_settings")
        } catch (ex: Exception) { }
        null
    }

    private val backupPrefs = context.getSharedPreferences("settings_backup", Context.MODE_PRIVATE)

    companion object {
        val BASE_CURRENCY = stringPreferencesKey("base_currency")
        val NUMBER_FORMAT = stringPreferencesKey("number_format")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val TRUE_BLACK = booleanPreferencesKey("true_black")
        val PRICE_ALERTS = booleanPreferencesKey("price_alerts")
        val DAILY_SUMMARY = booleanPreferencesKey("daily_summary")
        val UPDATE_FREQUENCY = intPreferencesKey("update_frequency")
        val LATEST_ORAKUL_TIP = stringPreferencesKey("latest_orakul_tip")
        val LAST_ORAKUL_RUN_TIME = longPreferencesKey("last_orakul_run_time")
        val IS_SAMPLE_SEEDED = booleanPreferencesKey("is_sample_seeded")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val DAILY_ORAKUL_NOTIF = booleanPreferencesKey("daily_orakul_notif")
        val LAST_MORNING_NOTIF_TIME = longPreferencesKey("last_morning_notif_time") // ITEM 7
        val LAST_EVENING_NOTIF_TIME = longPreferencesKey("last_evening_notif_time")
        val TARGET_ALLOCATION = stringPreferencesKey("target_allocation")
        val ACTIVE_IPO_ALARMS = stringSetPreferencesKey("active_ipo_alarms")
    }

    val baseCurrency: Flow<String> = context.dataStore.data.map { it[BASE_CURRENCY] ?: "TRY" }
    val numberFormat: Flow<String> = context.dataStore.data.map { it[NUMBER_FORMAT] ?: "TR" }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val isTrueBlack: Flow<Boolean> = context.dataStore.data.map { it[TRUE_BLACK] ?: false }
    val priceAlerts: Flow<Boolean> = context.dataStore.data.map { it[PRICE_ALERTS] ?: true }
    val dailySummary: Flow<Boolean> = context.dataStore.data.map { it[DAILY_SUMMARY] ?: false }
    val updateFrequency: Flow<Int> = context.dataStore.data.map { it[UPDATE_FREQUENCY] ?: 2 }
    val latestOrakulTip: Flow<String> = context.dataStore.data.map { it[LATEST_ORAKUL_TIP] ?: "" }
    val lastOrakulRunTime: Flow<Long> = context.dataStore.data.map { it[LAST_ORAKUL_RUN_TIME] ?: 0L }
    val isSampleSeeded: Flow<Boolean> = context.dataStore.data.map { it[IS_SAMPLE_SEEDED] ?: false }
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[IS_ONBOARDING_COMPLETED] ?: false }
    val dailyOrakulNotif: Flow<Boolean> = context.dataStore.data.map { it[DAILY_ORAKUL_NOTIF] ?: false }
    val lastMorningNotifTime: Flow<Long> = context.dataStore.data.map { it[LAST_MORNING_NOTIF_TIME] ?: 0L }
    val lastEveningNotifTime: Flow<Long> = context.dataStore.data.map { it[LAST_EVENING_NOTIF_TIME] ?: 0L }
    val targetAllocationJson: Flow<String> = context.dataStore.data.map { it[TARGET_ALLOCATION] ?: "" }
    val activeIpoAlarms: Flow<Set<String>> = context.dataStore.data.map { it[ACTIVE_IPO_ALARMS] ?: emptySet() }

    suspend fun setLatestOrakulTip(tip: String) {
        context.dataStore.edit { it[LATEST_ORAKUL_TIP] = tip }
    }

    suspend fun setLastOrakulRunTime(time: Long) {
        context.dataStore.edit { it[LAST_ORAKUL_RUN_TIME] = time }
    }

    suspend fun setSampleSeeded(seeded: Boolean) {
        context.dataStore.edit { it[IS_SAMPLE_SEEDED] = seeded }
    }

    suspend fun setBaseCurrency(currency: String) {
        context.dataStore.edit { it[BASE_CURRENCY] = currency }
    }

    suspend fun setNumberFormat(format: String) {
        context.dataStore.edit { it[NUMBER_FORMAT] = format }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun setTrueBlack(enabled: Boolean) {
        context.dataStore.edit { it[TRUE_BLACK] = enabled }
    }

    suspend fun setPriceAlerts(enabled: Boolean) {
        context.dataStore.edit { it[PRICE_ALERTS] = enabled }
    }

    suspend fun setDailySummary(enabled: Boolean) {
        context.dataStore.edit { it[DAILY_SUMMARY] = enabled }
    }

    private val _geminiApiKey = MutableStateFlow<String?>(null)
    val geminiApiKeyFlow: Flow<String?> = _geminiApiKey

    init {
        try {
            val prefs = encryptedPrefs ?: backupPrefs
            _geminiApiKey.value = prefs.getString("gemini_api_key", null)?.trim()
        } catch (e: Exception) {
            _geminiApiKey.value = null
        }
    }

    suspend fun setUpdateFrequency(minutes: Int) {
        context.dataStore.edit { it[UPDATE_FREQUENCY] = minutes }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[IS_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setDailyOrakulNotif(enabled: Boolean) {
        context.dataStore.edit { it[DAILY_ORAKUL_NOTIF] = enabled }
    }

    suspend fun setLastMorningNotifTime(time: Long) {
        context.dataStore.edit { it[LAST_MORNING_NOTIF_TIME] = time }
    }

    suspend fun setLastEveningNotifTime(time: Long) {
        context.dataStore.edit { it[LAST_EVENING_NOTIF_TIME] = time }
    }

    suspend fun setTargetAllocationJson(json: String) {
        context.dataStore.edit { it[TARGET_ALLOCATION] = json }
    }

    suspend fun saveActiveIpoAlarms(alarms: Set<String>) {
        context.dataStore.edit { it[ACTIVE_IPO_ALARMS] = alarms }
    }

    fun saveGeminiApiKey(key: String) {
        try {
            val prefs = encryptedPrefs ?: backupPrefs
            prefs.edit().putString("gemini_api_key", key).apply()
        } catch (e: Exception) {
            backupPrefs.edit().putString("gemini_api_key", key).apply()
        }
        _geminiApiKey.value = key.trim()
    }

    fun getGeminiApiKey(): String? {
        return _geminiApiKey.value
    }


}
