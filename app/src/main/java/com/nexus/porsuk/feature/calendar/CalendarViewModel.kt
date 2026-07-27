package com.nexus.porsuk.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import com.nexus.porsuk.data.local.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk AI Financial Calendar & Event Hub — ViewModel
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val earningsRepository: EarningsCalendarRepository,
    private val dividendRepository: DividendCalendarRepository,
    private val ipoRepository: IpoRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                calendarRepository.getAllEvents(),
                earningsRepository.getAllEarningsEvents(),
                dividendRepository.getAllDividendEvents(),
                ipoRepository.getAllIpos()
            ) { events, earnings, dividends, ipos ->
                _uiState.update { 
                    it.copy(
                        allEvents = events,
                        earningsEvents = earnings,
                        dividendEvents = dividends,
                        ipoEvents = ipos,
                        isLoading = false
                    )
                }
                applyFilters()
            }.collect()
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun triggerAiAnalysis(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            calendarRepository.getAiImpactAnalysis(eventId)
            _uiState.update { it.copy(isAiLoading = false) }
        }
    }

    fun selectCategory(category: CalendarEventCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun selectCountry(country: String?) {
        _uiState.update { it.copy(selectedCountry = country) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var list = state.allEvents

        if (state.selectedCategory != CalendarEventCategory.ALL) {
            list = list.filter { it.category == state.selectedCategory }
        }
        if (state.selectedCountry != null) {
            list = list.filter { it.country.equals(state.selectedCountry, ignoreCase = true) }
        }
        if (state.selectedImpactLevel != null) {
            list = list.filter { it.impactLevel == state.selectedImpactLevel }
        }

        _uiState.update { it.copy(filteredEvents = list) }
    }
}
