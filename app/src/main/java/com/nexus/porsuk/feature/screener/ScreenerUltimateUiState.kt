package com.nexus.porsuk.feature.screener

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Screener Pro Ultimate — UI Ekran Durumu (ScreenerUltimateUiState)
 */
data class ScreenerUltimateUiState(
    val selectedPreset: SmartFilterPresetCategory = SmartFilterPresetCategory.VALUE_INVESTORS,
    val filterCriteria: ScreenerUltimateCriteria = ScreenerUltimateCriteria(),
    val results: List<ScreenerResultItem> = emptyList(),
    val presetsList: List<SmartFilterPresetCategory> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
