package com.nexus.porsuk.feature.ipo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.IpoIntelligence
import com.nexus.porsuk.domain.repository.IpoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class IpoUiState(
    val ipos: List<IpoIntelligence> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IpoViewModel @Inject constructor(
    private val repository: IpoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IpoUiState())
    val uiState: StateFlow<IpoUiState> = _uiState.asStateFlow()

    init {
        loadIpos()
    }

    fun loadIpos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllIpos().collect {
                _uiState.value = _uiState.value.copy(ipos = it, isLoading = false)
            }
        }
    }
}
