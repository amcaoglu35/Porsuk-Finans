package com.nexus.porsuk.feature.strategybuilder

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Strategy Builder Pro — UI Ekran Durumu (StrategyBuilderUiState)
 */
data class StrategyBuilderUiState(
    val selectedType: StrategyType = StrategyType.VALUE_INVESTING,
    val templates: List<StrategyModel> = emptyList(),
    val currentStrategy: StrategyModel = StrategyModel(
        name = "Yeni Değer Yatırımı Stratejisi",
        type = StrategyType.VALUE_INVESTING,
        description = "Düşük F/K ve yüksek ROE filtreli kural seti",
        blocks = emptyList()
    ),
    val validationResult: StrategyValidationResult = StrategyValidationResult(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
