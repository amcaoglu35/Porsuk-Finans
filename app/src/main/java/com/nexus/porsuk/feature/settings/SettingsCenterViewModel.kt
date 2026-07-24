package com.nexus.porsuk.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Settings Center — ViewModel
 *
 * Görünüm (Tema), Dil/Bölge ve Piyasa/AI tercihlerini gerçek zamanlı yönetir.
 */
@HiltViewModel
class SettingsCenterViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val themeRepository: ThemeRepository,
    private val regionRepository: RegionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsCenterUiState(appVersion = settingsRepository.getAppVersion()))
    val uiState: StateFlow<SettingsCenterUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            themeRepository.setThemeMode(mode)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            regionRepository.setLanguage(language)
        }
    }

    fun setCurrency(currency: DefaultCurrency) {
        viewModelScope.launch {
            regionRepository.setCurrency(currency)
        }
    }

    fun toggleHidePortfolioValue(hidden: Boolean) {
        viewModelScope.launch {
            preferencesRepository.toggleHidePortfolioValue(hidden)
            _uiState.update { current ->
                current.copy(marketAiPreferences = current.marketAiPreferences.copy(isPortfolioValueHidden = hidden))
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                themeRepository.getThemeSettings().collect { theme ->
                    _uiState.update { it.copy(themeSettings = theme, isLoading = false) }
                }
            }

            launch {
                regionRepository.getRegionPreferences().collect { region ->
                    _uiState.update { it.copy(regionPreferences = region) }
                }
            }

            launch {
                preferencesRepository.getMarketAiPreferences().collect { pref ->
                    _uiState.update { it.copy(marketAiPreferences = pref) }
                }
            }
        }
    }
}
