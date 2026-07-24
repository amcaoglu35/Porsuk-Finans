package com.nexus.porsuk.feature.masterscore

import com.nexus.porsuk.domain.model.MasterScoreHistoryItem
import com.nexus.porsuk.domain.model.MasterScoreResult

/**
 * Porsuk Master Score Engine — UI Ekran Durumu (MasterScoreUiState)
 */
data class MasterScoreUiState(
    val symbol: String = "THYAO.IS",
    val scoreResult: MasterScoreResult? = null,
    val scoreHistory: List<MasterScoreHistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
