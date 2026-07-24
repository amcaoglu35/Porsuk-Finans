package com.nexus.porsuk.feature.masterscore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.repository.MasterScoreRepository
import com.nexus.porsuk.domain.repository.ScoreHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Master Score Engine — ViewModel (MasterScoreViewModel)
 *
 * 0-100 Genel Master Skorunu, 8 alt skor bileşenini ve geçmiş skor trendlerini yönetir.
 */
@HiltViewModel
class MasterScoreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val masterScoreRepository: MasterScoreRepository,
    private val scoreHistoryRepository: ScoreHistoryRepository
) : ViewModel() {

    private val symbol: String = savedStateHandle["symbol"] ?: "THYAO.IS"

    private val _uiState = MutableStateFlow(MasterScoreUiState(symbol = symbol))
    val uiState: StateFlow<MasterScoreUiState> = _uiState.asStateFlow()

    init {
        loadMasterScore(symbol)
        loadScoreHistory(symbol)
    }

    fun calculateForSymbol(newSymbol: String) {
        _uiState.update { it.copy(symbol = newSymbol, isLoading = true) }
        loadMasterScore(newSymbol)
        loadScoreHistory(newSymbol)
    }

    private fun loadMasterScore(targetSymbol: String) {
        viewModelScope.launch {
            masterScoreRepository.getMasterScore(targetSymbol).collect { result ->
                _uiState.update { it.copy(scoreResult = result, isLoading = false) }
                // Skoru geçmiş veritabanına kaydet
                scoreHistoryRepository.saveScoreHistory(targetSymbol, result.masterScore, result.level)
            }
        }
    }

    private fun loadScoreHistory(targetSymbol: String) {
        viewModelScope.launch {
            scoreHistoryRepository.getScoreHistory(targetSymbol).collect { history ->
                _uiState.update { it.copy(scoreHistory = history) }
            }
        }
    }
}
