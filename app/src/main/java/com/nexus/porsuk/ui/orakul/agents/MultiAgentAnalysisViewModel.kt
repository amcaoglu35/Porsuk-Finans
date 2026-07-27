package com.nexus.porsuk.ui.orakul.agents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.remote.agents.AgentRequest
import com.nexus.porsuk.data.remote.agents.MasterAiConsensusEngine
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.model.ConsensusResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MultiAgentUiState(
    val consensus: ConsensusResult? = null,
    val isLoading: Boolean = false,
    val isDebating: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class MultiAgentAnalysisViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiAgentUiState())
    val uiState: StateFlow<MultiAgentUiState> = _uiState.asStateFlow()

    fun runAnalysis(symbol: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Prepare request with all context
                val companies = financeRepository.getAllCompaniesDirect()
                val info = financeRepository.getCachedInfo(symbol).firstOrNull()
                
                val request = AgentRequest(
                    symbol = symbol,
                    companies = companies,
                    companyInfos = listOfNotNull(info)
                )
                
                val result = MasterAiConsensusEngine.runConsensus(request)
                _uiState.update { it.copy(consensus = result, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun startDebate(apiKey: String) {
        val current = _uiState.value.consensus ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDebating = true) }
            val debate = MasterAiConsensusEngine.runDebate(apiKey, current)
            _uiState.update { it.copy(
                isDebating = false,
                consensus = it.consensus?.copy(debateSummary = debate)
            ) }
        }
    }
}
