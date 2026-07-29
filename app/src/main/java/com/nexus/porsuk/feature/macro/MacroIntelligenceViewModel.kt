package com.nexus.porsuk.feature.macro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.MacroDashboardTab
import com.nexus.porsuk.domain.model.MacroIndicatorCategory
import com.nexus.porsuk.domain.repository.MacroIndicatorRepository
import com.nexus.porsuk.domain.repository.MacroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MacroIntelligenceViewModel @Inject constructor(
    macroRepository: MacroRepository,
    private val indicatorRepository: MacroIndicatorRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _activeTab = MutableStateFlow(MacroDashboardTab.INFLATION)

    val uiState: StateFlow<MacroIntelligenceUiState> = combine(
        indicatorRepository.getEconomicIndicators(),
        macroRepository.getMacroAiOutlook(),
        _activeTab,
        _isLoading
    ) { indicators, outlook, tab, loading ->
        MacroIntelligenceUiState(
            indicators = indicators.filter { it.category == categoryFromTab(tab) },
            aiOutlook = outlook,
            activeTab = tab,
            isLoading = loading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MacroIntelligenceUiState(activeTab = MacroDashboardTab.INFLATION))

    init {
        refreshMacro()
    }

    fun selectTab(tab: MacroDashboardTab) {
        _activeTab.value = tab
    }

    fun refreshMacro() {
        viewModelScope.launch {
            _isLoading.value = true
            indicatorRepository.refreshIndicators()
            _isLoading.value = false
        }
    }

    fun getIndicatorData(seriesId: String): Flow<List<Double>> {
        return indicatorRepository.getIndicatorHistory(seriesId)
    }

    private fun categoryFromTab(tab: MacroDashboardTab): MacroIndicatorCategory {
        return when (tab) {
            MacroDashboardTab.INFLATION -> MacroIndicatorCategory.INFLATION
            MacroDashboardTab.GROWTH -> MacroIndicatorCategory.GROWTH
            MacroDashboardTab.INTEREST_RATES -> MacroIndicatorCategory.INTEREST_RATE
            MacroDashboardTab.BONDS -> MacroIndicatorCategory.BONDS
            MacroDashboardTab.FX_COMMODITIES -> MacroIndicatorCategory.VOLATILITY_FX
            else -> MacroIndicatorCategory.INFLATION
        }
    }
}
