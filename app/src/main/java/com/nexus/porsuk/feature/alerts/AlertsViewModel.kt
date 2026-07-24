package com.nexus.porsuk.feature.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.AlertCategory
import com.nexus.porsuk.domain.model.AlertCondition
import com.nexus.porsuk.domain.model.SmartAlert
import com.nexus.porsuk.domain.repository.AppNotificationRepository
import com.nexus.porsuk.domain.repository.SmartAlertRepository
import com.nexus.porsuk.domain.repository.SystemAlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Smart Alert Engine — ViewModel (AlertsViewModel)
 *
 * 9 Farklı alarm türünün oluşturulmasını, duraklatılmasını, silinmesini ve geçmiş bildirim günlüğünü yönetir.
 */
@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: SmartAlertRepository,
    private val notificationRepository: AppNotificationRepository,
    private val systemAlarmRepository: SystemAlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadSmartAlerts()
        loadNotificationHistory()
        scheduleBackgroundWorker()
    }

    private fun scheduleBackgroundWorker() {
        viewModelScope.launch {
            systemAlarmRepository.schedulePeriodicAlertWorker()
        }
    }

    private fun loadSmartAlerts() {
        viewModelScope.launch {
            alertRepository.getAllSmartAlerts().collect { alerts ->
                if (alerts.isEmpty()) {
                    // Varsayılan temsil alarmları oluştur
                    createSampleAlerts()
                } else {
                    _uiState.update { it.copy(alertsList = alerts, isLoading = false) }
                    applyFilters()
                }
            }
        }
    }

    private fun loadNotificationHistory() {
        viewModelScope.launch {
            notificationRepository.getAllNotificationHistory().collect { history ->
                _uiState.update { it.copy(notificationHistory = history) }
            }
        }
    }

    private suspend fun createSampleAlerts() {
        alertRepository.createSmartAlert("THYAO.IS", AlertCategory.PRICE, AlertCondition.ABOVE, 300.0, "300 TL Direnç Kırılımı")
        alertRepository.createSmartAlert("GARAN.IS", AlertCategory.PERCENT_CHANGE, AlertCondition.PERCENT_INCREASE, 5.0, "Günlük %5 Yükseliş")
        alertRepository.createSmartAlert("BTC-USD", AlertCategory.VOLUME, AlertCondition.VOLUME_SPIKE, 50000.0, "Sıradışı Hacim İvmesi")
        alertRepository.createSmartAlert("AAPL", AlertCategory.DIVIDEND, AlertCondition.EQUAL, 0.0, "Temettü Hak Kullanımı")
        alertRepository.createSmartAlert("THYAO.IS", AlertCategory.AI_ORAKUL_STUB, AlertCondition.ABOVE, 90.0, "Orakul AI Master Score 90+")
    }

    fun selectCategoryFilter(category: AlertCategory?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
        applyFilters()
    }

    fun toggleTab(isHistory: Boolean) {
        _uiState.update { it.copy(isShowingHistoryTab = isHistory) }
    }

    fun createNewAlert(
        symbol: String,
        category: AlertCategory,
        condition: AlertCondition,
        targetValue: Double,
        note: String?
    ) {
        viewModelScope.launch {
            alertRepository.createSmartAlert(symbol, category, condition, targetValue, note)
        }
    }

    fun toggleAlertEnabled(alertId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            alertRepository.toggleAlertStatus(alertId, isEnabled)
        }
    }

    fun toggleAlertMuted(alertId: String, isMuted: Boolean) {
        viewModelScope.launch {
            alertRepository.toggleAlertMute(alertId, isMuted)
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            alertRepository.deleteSmartAlert(alertId)
        }
    }

    fun clearNotificationHistory() {
        viewModelScope.launch {
            notificationRepository.clearAllNotifications()
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var list = state.alertsList

        if (state.selectedCategoryFilter != null) {
            list = list.filter { it.category == state.selectedCategoryFilter }
        }

        _uiState.update { it.copy(filteredAlertsList = list) }
    }
}
