package com.nexus.porsuk.feature.scanner

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Smart Scanner Engine — UI Ekran Durumu (ScannerUiState)
 */
data class ScannerUiState(
    val selectedPreset: ScanPresetCategory = ScanPresetCategory.TOP_GAINERS,
    val selectedMarket: ScanMarketType = ScanMarketType.ALL,
    val filterCriteria: ScannerFilterCriteria = ScannerFilterCriteria(),
    val scanResults: List<ScanResultItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
