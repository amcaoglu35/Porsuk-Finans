package com.nexus.porsuk.feature.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.repository.PortfolioDoctorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Portfolio Doctor Engine — ViewModel
 *
 * 0-100 Portföy Sağlık Skorunu, çeşitlendirmeyi, risk ve yeniden dengeleme önerilerini yönetir.
 */
@HiltViewModel
class PortfolioDoctorViewModel @Inject constructor(
    private val doctorRepository: PortfolioDoctorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioDoctorUiState())
    val uiState: StateFlow<PortfolioDoctorUiState> = _uiState.asStateFlow()

    init {
        loadDoctorReport()
    }

    fun refreshDoctorReport() {
        _uiState.update { it.copy(isLoading = true) }
        loadDoctorReport()
    }

    private fun loadDoctorReport() {
        viewModelScope.launch {
            doctorRepository.getDoctorReport().collect { rep ->
                _uiState.update { it.copy(report = rep, isLoading = false) }
            }
        }
    }
}
