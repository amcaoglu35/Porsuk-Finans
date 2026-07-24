package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Tarama Deposu Sözleşmesi (ScreenerRepository)
 */
interface ScreenerRepository {
    fun executeScan(criteria: ScreenerUltimateCriteria): Flow<List<ScreenerResultItem>>
}

/**
 * 2. Filtre Deposu Sözleşmesi (FilterRepository)
 */
interface FilterRepository {
    fun getSavedFilterPresets(): Flow<List<ScreenerUltimateCriteria>>
    suspend fun saveFilterPreset(name: String, criteria: ScreenerUltimateCriteria)
}

/**
 * 3. Akıllı Paket Deposu Sözleşmesi (PresetRepository)
 */
interface PresetRepository {
    fun getPresetBundles(): Flow<List<SmartFilterPresetCategory>>
}

/**
 * 4. Sonuç Önbellek Deposu Sözleşmesi (ScanResultRepository)
 */
interface ScanResultRepository {
    fun getCachedResults(): Flow<List<ScreenerResultItem>>
}
