package com.nexus.porsuk.ui.ailab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.feature.ailab.AiLabUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import java.util.UUID

data class ToolReportHistory(
    val toolName: String,
    val report: String,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

@HiltViewModel
class AiLabViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager,
    private val autoRebalanceService: com.nexus.porsuk.data.engine.AutoRebalanceService,
    private val contextBuilder: ToolContextBuilder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiLabUiState())
    val uiState: StateFlow<AiLabUiState> = _uiState.asStateFlow()

    private val _toolHistory = MutableStateFlow<List<ToolReportHistory>>(emptyList())
    val toolHistory: StateFlow<List<ToolReportHistory>> = _toolHistory.asStateFlow()

    fun executeAutoRebalance() {
        viewModelScope.launch {
            // Target: 25% THYAO, 25% ASELS, 20% KCHOL, 15% AKBNK, 15% SISE
            val target = mapOf(
                "THYAO" to 25.0,
                "ASELS" to 25.0,
                "KCHOL" to 20.0,
                "AKBNK" to 15.0,
                "SISE" to 15.0
            )
            autoRebalanceService.executeAutoRebalance(target)
        }
    }

    fun setRiskMonitoring(enabled: Boolean, context: android.content.Context) {
        if (enabled) {
            com.nexus.porsuk.worker.RiskAlertWorker.schedule(context)
        } else {
            com.nexus.porsuk.worker.RiskAlertWorker.cancel(context)
        }
    }

    fun setNightSummary(enabled: Boolean, context: android.content.Context) {
        if (enabled) {
            com.nexus.porsuk.worker.NightSummaryWorker.schedule(context)
        } else {
            com.nexus.porsuk.worker.NightSummaryWorker.cancel(context)
        }
    }

    fun runTool(toolName: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    toolLoadingStates = state.toolLoadingStates + (toolName to true),
                    toolErrorStates = state.toolErrorStates + (toolName to null)
                )
            }
            
            try {
                val apiKey = settingsManager.getGeminiApiKey()
                if (apiKey.isNullOrBlank()) {
                    throw Exception("Gemini API Key eksik. Lütfen Ayarlar menüsünden geçerli bir API Key tanımlayın.")
                }

                // ── BÖLÜM A: Bağlam Oluşturucu (Standardize Context) ──
                val baseContext = contextBuilder.buildPortfolioContext()
                
                val toolSpecificContext = when(toolName) {
                    "Portfolio Health Check", "Portfolio Diversification", "Risk Scanner" -> 
                        contextBuilder.buildPortfolioContext()
                    
                    "Stock Compare", "AI Watchlist Analyzer", "AI News Summary" -> 
                        contextBuilder.buildWatchlistContext()
                    
                    "Sector Compare" -> 
                        contextBuilder.buildSectorContext()
                    
                    "AI Screener", "Dividend Finder", "Growth Finder", "Value Finder", "Momentum Finder", "AI Opportunity Finder" -> 
                        contextBuilder.buildMarketContext()
                    
                    else -> contextBuilder.buildMarketContext()
                }

                val fullContext = """
                    $baseContext
                    
                    Aşağıdaki araç özelinde ek bağlam:
                    $toolSpecificContext
                """.trimIndent()

                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val report = service.runLabTool(toolName, fullContext)
                
                _uiState.update { state ->
                    state.copy(
                        toolReports = state.toolReports + (toolName to report),
                        toolLoadingStates = state.toolLoadingStates + (toolName to false)
                    )
                }

                _toolHistory.update { history ->
                    history + ToolReportHistory(
                        toolName = toolName,
                        report = report,
                        timestamp = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        toolErrorStates = state.toolErrorStates + (toolName to e.localizedMessage),
                        toolLoadingStates = state.toolLoadingStates + (toolName to false)
                    )
                }
            }
        }
    }
}
