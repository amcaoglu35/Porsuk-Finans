package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Piyasa Tarama Deposu Sözleşmesi (ScannerRepository)
 */
interface ScannerRepository {
    fun executeScan(preset: ScanPresetCategory, market: ScanMarketType, criteria: ScannerFilterCriteria? = null): Flow<List<ScanResultItem>>
}

/**
 * 2. Filtre Yönetim Deposu Sözleşmesi (ScannerFilterRepository)
 */
interface ScannerFilterRepository {
    fun getSavedFilterCriteria(): Flow<ScannerFilterCriteria>
    suspend fun saveFilterCriteria(criteria: ScannerFilterCriteria)
}

/**
 * 3. Tarama Geçmişi Deposu Sözleşmesi (ScanHistoryRepository)
 */
interface ScanHistoryRepository {
    fun getRecentScans(): Flow<List<ScanPresetCategory>>
    suspend fun recordScanPreset(preset: ScanPresetCategory)
}
