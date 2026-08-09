package com.nexus.porsuk.feature.fundintelligence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.usecase.fund.FundFullIntelligence
import com.nexus.porsuk.domain.usecase.fund.GetFundIntelligenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FundIntelligenceUiState(
    val data: FundFullIntelligence? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FundIntelligenceViewModel @Inject constructor(
    private val getFundIntelligenceUseCase: GetFundIntelligenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FundIntelligenceUiState())
    val uiState: StateFlow<FundIntelligenceUiState> = _uiState.asStateFlow()

    fun loadFundDetails(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getFundIntelligenceUseCase(code).collect {
                _uiState.value = _uiState.value.copy(data = it, isLoading = false)
            }
        }
    }
}
