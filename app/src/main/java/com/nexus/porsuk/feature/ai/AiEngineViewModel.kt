package com.nexus.porsuk.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.AiEngineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiEngineUiState(
    val operationMode: AiOperationMode = AiOperationMode.CLOUD_ONLY,
    val availableModels: List<LocalAiModel> = emptyList(),
    val engineStatus: AiEngineStatus? = null,
    val qualityMetrics: AiQualityMetrics? = null,
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class AiEngineViewModel @Inject constructor(
    private val repository: AiEngineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiEngineUiState())
    val uiState: StateFlow<AiEngineUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                repository.getOperationMode(),
                repository.getAvailableModels(),
                repository.getEngineStatus(),
                repository.getQualityMetrics()
            ) { mode, models, status, metrics ->
                AiEngineUiState(
                    operationMode = mode,
                    availableModels = models,
                    engineStatus = status,
                    qualityMetrics = metrics,
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

    fun setMode(mode: AiOperationMode) {
        viewModelScope.launch {
            repository.setOperationMode(mode)
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            repository.downloadModel(modelId)
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            repository.deleteModel(modelId)
        }
    }

    fun setActiveModel(modelId: String) {
        viewModelScope.launch {
            repository.setActiveModel(modelId)
        }
    }
}
