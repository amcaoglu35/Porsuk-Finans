package com.nexus.porsuk.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.CompanyRatioEntity
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DuelloUiState(
    val symbol1: String = "",
    val symbol2: String = "",
    val company1: Company? = null,
    val company2: Company? = null,
    val ratios1: CompanyRatioEntity? = null,
    val ratios2: CompanyRatioEntity? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class HisseDuelloViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuelloUiState())
    val uiState: StateFlow<DuelloUiState> = _uiState.asStateFlow()

    fun init(symbol1: String, symbol2: String) {
        _uiState.update { it.copy(symbol1 = symbol1, symbol2 = symbol2, isLoading = true) }
        viewModelScope.launch {
            val c1 = repository.getCompany(symbol1)
            val c2 = repository.getCompany(symbol2)
            val r1 = repository.getCompanyRatios(symbol1).firstOrNull()?.firstOrNull()
            val r2 = repository.getCompanyRatios(symbol2).firstOrNull()?.firstOrNull()
            
            _uiState.update { 
                it.copy(
                    company1 = c1,
                    company2 = c2,
                    ratios1 = r1,
                    ratios2 = r2,
                    isLoading = false
                ) 
            }
        }
    }
}
