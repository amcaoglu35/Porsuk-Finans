package com.nexus.porsuk.feature.orakul

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Orakul Core Engine — ViewModel
 *
 * 8 Bağımsız analiz motorunun verilerini ve standart Orakul Raporunu yönetir.
 */
@HiltViewModel
class OrakulCoreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val analysisRepository: AnalysisRepository,
    private val financialAnalysisRepository: FinancialAnalysisRepository,
    private val technicalAnalysisRepository: TechnicalAnalysisRepository,
    private val riskRepository: OrakulRiskRepository
) : ViewModel() {

    private val symbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(OrakulUiState(symbol = symbol))
    val uiState: StateFlow<OrakulUiState> = _uiState.asStateFlow()

    init {
        runFullOrakulAnalysis(symbol)
    }

    fun analyzeSymbol(newSymbol: String) {
        _uiState.update { it.copy(symbol = newSymbol, isLoading = true) }
        runFullOrakulAnalysis(newSymbol)
    }

    private fun runFullOrakulAnalysis(targetSymbol: String) {
        viewModelScope.launch {
            // 1. Orakul Standart Analiz Raporu
            launch {
                analysisRepository.generateAnalysisReport(targetSymbol).collect { rep ->
                    _uiState.update { it.copy(report = rep) }
                }
            }

            // 2. Finansallar
            launch {
                financialAnalysisRepository.getFinancialAnalysis(targetSymbol).collect { fin ->
                    _uiState.update { it.copy(financialData = fin) }
                }
            }

            // 3. 12 Teknik Gösterge
            launch {
                technicalAnalysisRepository.getTechnicalAnalysis(targetSymbol).collect { tech ->
                    _uiState.update { it.copy(technicalData = tech) }
                }
            }

            // 4. Risk Analizi
            launch {
                riskRepository.getRiskAnalysis(targetSymbol).collect { rsk ->
                    _uiState.update { it.copy(riskData = rsk, isLoading = false) }
                }
            }
        }
    }
}
