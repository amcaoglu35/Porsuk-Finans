package com.nexus.porsuk.feature.automation

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Notification & Automation Center — UI Ekran Durumu (AutomationUiState)
 */
data class AutomationUiState(
    val notifications: List<NotificationCenterItem> = emptyList(),
    val rules: List<AutomationRuleModel> = emptyList(),
    val workflows: List<AutomationWorkflow> = emptyList(),
    val selectedCategory: AlertCategory = AlertCategory.PRICE,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
