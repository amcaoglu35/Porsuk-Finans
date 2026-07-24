package com.nexus.porsuk.feature.strategybuilder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Strategy Builder Pro — ViewModel
 *
 * Visual strategy builder düğüm mantığını, 10 şablonu ve kural doğrulamasını yönetir.
 */
@HiltViewModel
class StrategyBuilderViewModel @Inject constructor(
    private val strategyRepository: StrategyRepository,
    private val templateRepository: StrategyTemplateRepository,
    private val validationRepository: StrategyValidationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StrategyBuilderUiState())
    val uiState: StateFlow<StrategyBuilderUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
        validateCurrentStrategy()
    }

    fun selectType(type: StrategyType) {
        val updated = _uiState.value.currentStrategy.copy(type = type)
        _uiState.update { it.copy(selectedType = type, currentStrategy = updated) }
        validateCurrentStrategy()
    }

    fun selectTemplate(template: StrategyModel) {
        _uiState.update { it.copy(currentStrategy = template, selectedType = template.type) }
        validateCurrentStrategy()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            templateRepository.getTemplates().collect { list ->
                _uiState.update { it.copy(templates = list, isLoading = false) }
            }
        }
    }

    private fun validateCurrentStrategy() {
        val current = _uiState.value.currentStrategy
        viewModelScope.launch {
            validationRepository.validateStrategy(current).collect { res ->
                _uiState.update { it.copy(validationResult = res) }
            }
        }
    }
}
