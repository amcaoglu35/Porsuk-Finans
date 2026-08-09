package com.nexus.porsuk.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.PriceAlert
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "Yerel Kullanıcı",
    val baseCurrency: String = "TRY",
    val numberFormat: String = "TR",
    val isDarkMode: Boolean = false,
    val isTrueBlack: Boolean = false,
    val priceAlertsEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = false,
    val updateFrequencyMinutes: Int = 2,
    val hasGeminiApiKey: Boolean = false,
    val hasFmpApiKey: Boolean = false,
    val activeAlerts: List<PriceAlert> = emptyList(),
    val isOnboardingCompleted: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val repository: FinanceRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsManager.baseCurrency,
        settingsManager.numberFormat,
        settingsManager.isDarkMode,
        settingsManager.isTrueBlack,
        settingsManager.priceAlerts,
        settingsManager.dailySummary,
        settingsManager.updateFrequency,
        settingsManager.geminiApiKeyFlow,
        settingsManager.fmpApiKeyFlow,
        repository.getAllPriceAlertsFlow(),
        settingsManager.isOnboardingCompleted
    ) { args ->
        val currency = args[0] as String
        val format = args[1] as String
        val darkMode = args[2] as Boolean
        val trueBlack = args[3] as Boolean
        val alerts = args[4] as Boolean
        val summary = args[5] as Boolean
        val freq = args[6] as Int
        val apiKey = args[7] as String?
        val fmpKey = args[8] as String?
        @Suppress("UNCHECKED_CAST")
        val activeAlarms = args[9] as List<PriceAlert>
        val onboarding = args[10] as Boolean

        SettingsUiState(
            baseCurrency = currency,
            numberFormat = format,
            isDarkMode = darkMode,
            isTrueBlack = trueBlack,
            priceAlertsEnabled = alerts,
            dailySummaryEnabled = summary,
            updateFrequencyMinutes = freq,
            hasGeminiApiKey = !apiKey.isNullOrBlank(),
            hasFmpApiKey = !fmpKey.isNullOrBlank(),
            activeAlerts = activeAlarms,
            isOnboardingCompleted = onboarding,
            isLoaded = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setOnboardingCompleted(completed: Boolean) = viewModelScope.launch { settingsManager.setOnboardingCompleted(completed) }

    fun setBaseCurrency(currency: String) = viewModelScope.launch { settingsManager.setBaseCurrency(currency) }
    fun setNumberFormat(format: String) = viewModelScope.launch { settingsManager.setNumberFormat(format) }
    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { settingsManager.setDarkMode(enabled) }
    fun setTrueBlack(enabled: Boolean) = viewModelScope.launch { settingsManager.setTrueBlack(enabled) }
    fun setPriceAlerts(enabled: Boolean) = viewModelScope.launch { settingsManager.setPriceAlerts(enabled) }
    fun setDailySummary(enabled: Boolean) = viewModelScope.launch { settingsManager.setDailySummary(enabled) }
    fun setUpdateFrequency(minutes: Int) = viewModelScope.launch { settingsManager.setUpdateFrequency(minutes) }

    fun saveApiKey(key: String) {
        settingsManager.saveGeminiApiKey(key)
    }

    fun saveFmpApiKey(key: String) {
        settingsManager.saveFmpApiKey(key)
    }

    fun deletePriceAlert(alertId: Int) {
        viewModelScope.launch {
            repository.deletePriceAlert(alertId)
        }
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            onComplete()
        }
    }
}
