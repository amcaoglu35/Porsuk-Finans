package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 3. Tarama Önbellekleme ve Rapor Konsolidasyon Motoru (SmartScannerReportEngine)
 */
@Singleton
class SmartScannerReportEngine @Inject constructor(
    private val presetScanEngine: PresetScanEngine,
    private val multiFilterScanEngine: MultiFilterScanEngine
) {

    fun runScan(
        preset: ScanPresetCategory,
        market: ScanMarketType,
        customFilter: ScannerFilterCriteria? = null
    ): List<ScanResultItem> {
        val baseResults = presetScanEngine.executePresetScan(preset, market)
        return if (customFilter != null) {
            multiFilterScanEngine.applyFilters(baseResults, customFilter)
        } else {
            baseResults
        }
    }
}
