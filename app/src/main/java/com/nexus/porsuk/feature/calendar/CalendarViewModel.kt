package com.nexus.porsuk.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Economic Calendar Engine — ViewModel (CalendarViewModel)
 *
 * Günlük, Haftalık, Aylık ve Liste takvim görünümlerini, etki seviyesi ve ülke filtrelerini yönetir.
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val earningsRepository: EarningsCalendarRepository,
    private val dividendRepository: DividendCalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarEvents()
    }

    private fun loadCalendarEvents() {
        viewModelScope.launch {
            calendarRepository.getAllEvents().collect { list ->
                if (list.isEmpty()) {
                    createSampleCalendarData()
                } else {
                    _uiState.update { it.copy(allEvents = list, isLoading = false) }
                    applyFilters()
                }
            }
        }
    }

    private suspend fun createSampleCalendarData() {
        val sampleList = listOf(
            EconomicEvent(
                eventId = "c1",
                title = "FED Politika Faiz Kararı",
                country = "US",
                category = CalendarEventCategory.MACRO,
                impactLevel = CalendarImpactLevel.HIGH,
                actualValue = "%5.50",
                forecastValue = "%5.50",
                previousValue = "%5.50"
            ),
            EconomicEvent(
                eventId = "c2",
                title = "TCMB Politika Faiz Kararı (PPK)",
                country = "TR",
                category = CalendarEventCategory.MACRO,
                impactLevel = CalendarImpactLevel.HIGH,
                actualValue = "%50.00",
                forecastValue = "%50.00",
                previousValue = "%50.00"
            ),
            EconomicEvent(
                eventId = "c3",
                title = "Türkiye TÜFE Enflasyon Verisi (Aylık)",
                country = "TR",
                category = CalendarEventCategory.ECONOMIC_DATA,
                impactLevel = CalendarImpactLevel.HIGH,
                actualValue = "%2.80",
                forecastValue = "%2.50",
                previousValue = "%3.10"
            ),
            EconomicEvent(
                eventId = "c4",
                title = "ABD Tarım Dışı İstihdam (NFP)",
                country = "US",
                category = CalendarEventCategory.ECONOMIC_DATA,
                impactLevel = CalendarImpactLevel.HIGH,
                actualValue = "216K",
                forecastValue = "170K",
                previousValue = "173K"
            )
        )
        _uiState.update { it.copy(allEvents = sampleList, isLoading = false) }
        applyFilters()
    }

    fun selectViewMode(mode: CalendarViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun selectCategory(category: CalendarEventCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun selectCountry(country: String?) {
        _uiState.update { it.copy(selectedCountry = country) }
        applyFilters()
    }

    fun selectImpactLevel(impact: CalendarImpactLevel?) {
        _uiState.update { it.copy(selectedImpactLevel = impact) }
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
