package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.SmartScannerReportEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannerRepositoryImpl @Inject constructor(
    private val reportEngine: SmartScannerReportEngine
) : ScannerRepository {

    override fun executeScan(
        preset: ScanPresetCategory,
        market: ScanMarketType,
        criteria: ScannerFilterCriteria?
    ): Flow<List<ScanResultItem>> = flow {
        emit(reportEngine.runScan(preset, market, criteria))
    }
}

@Singleton
class ScannerFilterRepositoryImpl @Inject constructor() : ScannerFilterRepository {

    private val filterState = MutableStateFlow(ScannerFilterCriteria())

    override fun getSavedFilterCriteria(): Flow<ScannerFilterCriteria> = filterState

    override suspend fun saveFilterCriteria(criteria: ScannerFilterCriteria) {
        filterState.value = criteria
    }
}

@Singleton
class ScanHistoryRepositoryImpl @Inject constructor() : ScanHistoryRepository {

    private val recentScans = MutableStateFlow(
        listOf(
            ScanPresetCategory.TOP_GAINERS,
            ScanPresetCategory.HIGH_MOMENTUM,
            ScanPresetCategory.VALUE_STOCKS
        )
    )

    override fun getRecentScans(): Flow<List<ScanPresetCategory>> = recentScans

    override suspend fun recordScanPreset(preset: ScanPresetCategory) {
        val current = recentScans.value.toMutableList()
        current.remove(preset)
        current.add(0, preset)
        recentScans.value = current.take(5)
    }
}
