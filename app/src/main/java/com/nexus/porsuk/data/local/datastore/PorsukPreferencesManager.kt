package com.nexus.porsuk.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "porsuk_preferences")

/**
 * Porsuk Data Center — Tercihler ve Senkronizasyon Zaman Damgaları Yöneticisi (Preferences Manager)
 *
 * İlk kurulum tamamlandı mı (isInitialSyncDone), son başarılı senkronizasyon ne zaman gerçekleşti vb.
 * verileri Jetpack DataStore üzerinde tutar.
 */
@Singleton
class PorsukPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_INITIAL_SYNC_DONE = booleanPreferencesKey("initial_sync_done")
        private val KEY_LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        private val KEY_LAST_FUND_SYNC_TIMESTAMP = longPreferencesKey("last_fund_sync_timestamp")
    }

    val isInitialSyncCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_INITIAL_SYNC_DONE] ?: false
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_SYNC_TIMESTAMP] ?: 0L
    }

    suspend fun setInitialSyncCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_INITIAL_SYNC_DONE] = completed
        }
    }

    suspend fun updateLastSyncTimestamp(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    suspend fun updateLastFundSyncTimestamp(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_FUND_SYNC_TIMESTAMP] = timestamp
        }
    }
}
