package com.nexus.porsuk.feature.alerts

import com.nexus.porsuk.domain.model.AlertCategory
import com.nexus.porsuk.domain.model.AppNotificationItem
import com.nexus.porsuk.domain.model.SmartAlert

/**
 * Porsuk Smart Alert Engine — UI Ekran Durumu (AlertsUiState)
 */
data class AlertsUiState(
    val alertsList: List<SmartAlert> = emptyList(),
    val filteredAlertsList: List<SmartAlert> = emptyList(),
    val notificationHistory: List<AppNotificationItem> = emptyList(),
    val selectedCategoryFilter: AlertCategory? = null,
    val isShowingHistoryTab: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
