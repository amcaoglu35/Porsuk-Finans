package com.nexus.porsuk.feature.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.AutomationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutomationViewModel @Inject constructor(
    private val repository: AutomationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                repository.getAutomationRules(),
                repository.getExecutionHistory(),
                repository.getAiSuggestions()
            ) { rules, history, suggestions ->
                AutomationUiState(
                    rules = rules,
                    history = history,
                    suggestions = suggestions,
                    isLoading = false,
                    selectedTab = _uiState.value.selectedTab
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun toggleRule(ruleId: String, enabled: Boolean) {
        viewModelScope.launch {
            val rule = _uiState.value.rules.find { it.ruleId == ruleId }
            rule?.let {
                repository.saveRule(it.copy(isEnabled = enabled))
            }
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            repository.deleteRule(ruleId)
        }
    }

    fun applySuggestion(suggestionId: String) {
        viewModelScope.launch {
            repository.markSuggestionApplied(suggestionId)
            // Logic to create rule from suggestion...
        }
    }
    
    fun createRuleFromTemplate(template: AutomationRuleModel) {
        viewModelScope.launch {
            repository.saveRule(template)
        }
    }

    fun runNow(context: android.content.Context) {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.nexus.porsuk.worker.AutomationWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
    }
}
