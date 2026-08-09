package com.nexus.porsuk.ui.orakul

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.data.repository.KaziRepository
import com.nexus.porsuk.worker.KaziAnalysisWorker
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KaziUiState(
    val latestRun: KaziRun? = null,
    val candidates: List<KaziCandidate> = emptyList(),
    val basket: KaziBasket? = null,
    val basketItems: List<KaziBasketItem> = emptyList(),
    val riskProfile: String = "BALANCED",
    val horizon: String = "MEDIUM",
    val strategyFocus: String = "VALUE",
    val capital: String = "",
    val excludedSectors: Set<String> = emptySet(),
    val companies: List<Company> = emptyList(),
    val targetStockCount: Int = 5,
    val reasoningDepth: String = "DEEP",
    val cashBufferPct: Double = 10.0,
    val minQualityScore: Int = 0
)

@HiltViewModel
class KaziViewModel @Inject constructor(
    private val repository: KaziRepository,
    private val financeRepository: FinanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KaziUiState())
    val uiState: StateFlow<KaziUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            financeRepository.allCompanies.collect { list ->
                _uiState.update { it.copy(companies = list) }
            }
        }
        viewModelScope.launch {
            repository.getLatestKaziRun().collect { run ->
                _uiState.update { it.copy(latestRun = run) }
                if (run != null) {
                    launch {
                        repository.getCandidatesForRun(run.id).collect { candidates ->
                            _uiState.update { it.copy(candidates = candidates) }
                        }
                    }
                    launch {
                        repository.getKaziBasketForRun(run.id).collect { basket ->
                            _uiState.update { it.copy(basket = basket) }
                            if (basket != null) {
                                repository.getKaziBasketItems(basket.id).collect { items ->
                                    _uiState.update { it.copy(basketItems = items) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setTargetStockCount(count: Int) {
        _uiState.update { it.copy(targetStockCount = count) }
    }

    fun setReasoningDepth(depth: String) {
        _uiState.update { it.copy(reasoningDepth = depth) }
    }

    fun setCashBufferPct(pct: Double) {
        _uiState.update { it.copy(cashBufferPct = pct) }
    }

    fun setMinQualityScore(score: Int) {
        _uiState.update { it.copy(minQualityScore = score) }
    }

    fun setRiskProfile(profile: String) {
        _uiState.update { it.copy(riskProfile = profile) }
    }

    fun setHorizon(horizon: String) {
        _uiState.update { it.copy(horizon = horizon) }
    }

    fun setStrategyFocus(focus: String) {
        _uiState.update { it.copy(strategyFocus = focus) }
    }

    fun setCapital(capital: String) {
        _uiState.update { it.copy(capital = capital) }
    }

    fun toggleSector(sector: String) {
        _uiState.update { state ->
            val newSet = if (state.excludedSectors.contains(sector)) {
                state.excludedSectors - sector
            } else {
                state.excludedSectors + sector
            }
            state.copy(excludedSectors = newSet)
        }
    }

    fun startMining() {
        val state = _uiState.value
        viewModelScope.launch {
            val encodedHorizon = "${state.horizon}|${state.strategyFocus}|${state.targetStockCount}|${state.reasoningDepth}|${state.cashBufferPct}|${state.minQualityScore}"
            val runId = repository.startKaziRun(
                riskProfile = state.riskProfile,
                horizon = encodedHorizon,
                capital = state.capital.toDoubleOrNull(),
                excludedSectors = state.excludedSectors.joinToString(",")
            )

            val workRequest = OneTimeWorkRequestBuilder<KaziAnalysisWorker>()
                .setInputData(workDataOf("runId" to runId.toInt()))
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "KaziAnalysis_${runId}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun addToPortfolio(basketName: String, onCompleted: () -> Unit) {
        val basketId = _uiState.value.basket?.id ?: return
        viewModelScope.launch {
            repository.addToPortfolio(basketId, basketName)
            onCompleted()
        }
    }
}
