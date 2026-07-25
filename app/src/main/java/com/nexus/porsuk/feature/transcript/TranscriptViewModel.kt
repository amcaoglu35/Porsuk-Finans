package com.nexus.porsuk.feature.transcript

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Earnings Call & Transcripts Intelligence Platform — ViewModel
 */
@HiltViewModel
class TranscriptViewModel @Inject constructor(
    private val transcriptRepository: TranscriptRepository,
    private val earningsCallRepository: EarningsCallRepository,
    private val speakerRepository: SpeakerRepository,
    private val searchRepository: TranscriptSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranscriptUiState())
    val uiState: StateFlow<TranscriptUiState> = _uiState.asStateFlow()

    init {
        loadSymbolCalls(_uiState.value.selectedSymbol)
    }

    fun selectTab(tab: TranscriptTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectCall(callId: String) {
        _uiState.update { it.copy(selectedCallId = callId, isLoading = true) }
        loadTranscriptDetail(callId)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            executeSearch(query)
        } else if (query.isEmpty()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun executeSearch(query: String) {
        viewModelScope.launch {
            val results = searchRepository.searchTranscripts(query, _uiState.value.selectedSymbol)
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    fun filterByCallType(type: EarningsCallType?) {
        _uiState.update { it.copy(selectedCallType = type) }
    }

    fun changeSymbol(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol, isLoading = true) }
        loadSymbolCalls(symbol)
    }

    private fun loadSymbolCalls(symbol: String) {
        viewModelScope.launch {
            launch {
                earningsCallRepository.getRecentEarningsCalls(symbol).collect { calls ->
                    val activeCallId = calls.firstOrNull()?.callId ?: "call_q2_2026"
                    _uiState.update { it.copy(recentCalls = calls, selectedCallId = activeCallId) }
                    loadTranscriptDetail(activeCallId)
                }
            }

            launch {
                searchRepository.getFutureStubs().collect { stubs ->
                    _uiState.update { it.copy(futureStubs = stubs) }
                }
            }
        }
    }

    private fun loadTranscriptDetail(callId: String) {
        viewModelScope.launch {
            launch {
                transcriptRepository.getTranscript(callId).collect { transcript ->
                    _uiState.update { it.copy(currentCall = transcript, isLoading = false) }
                }
            }

            launch {
                transcriptRepository.getTranscriptUtterances(callId).collect { utts ->
                    _uiState.update { it.copy(utterances = utts) }
                }
            }

            launch {
                speakerRepository.getSpeakersForCall(callId).collect { spks ->
                    _uiState.update { it.copy(speakers = spks) }
                }
            }

            launch {
                val mgmt = transcriptRepository.getManagementAnalysis(callId)
                _uiState.update { it.copy(managementAnalysis = mgmt) }
            }

            launch {
                val qna = earningsCallRepository.getQnaExchanges(callId)
                _uiState.update { it.copy(qnaExchanges = qna) }
            }

            launch {
                val ai = transcriptRepository.getTranscriptAiSummary(callId)
                _uiState.update { it.copy(aiSummary = ai) }
            }

            launch {
                val vis = transcriptRepository.getTranscriptVisuals(callId)
                _uiState.update { it.copy(visuals = vis) }
            }
        }
    }
}
