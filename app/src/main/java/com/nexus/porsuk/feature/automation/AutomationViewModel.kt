package com.nexus.porsuk.feature.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.AlertCategory
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Notification & Automation Center — ViewModel
 *
 * Event-Driven bildirimleri, IF/AND/OR otomasyon kurallarını ve otomatik bülten iş akışlarını yönetir.
 */
@HiltViewModel
class AutomationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val automationRepository: AutomationRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun selectCategory(category: AlertCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                notificationRepository.getNotifications().collect { list ->
                    _uiState.update { it.copy(notifications = list, isLoading = false) }
                }
            }

            launch {
                automationRepository.getAutomationRules().collect { list ->
                    _uiState.update { it.copy(rules = list) }
                }
            }

            launch {
                workflowRepository.getWorkflows().collect { list ->
                    _uiState.update { it.copy(workflows = list) }
                }
            }
        }
    }
}
