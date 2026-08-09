package com.nexus.porsuk.data.engine

import com.nexus.porsuk.core.common.NetworkConnectivityMonitor
import com.nexus.porsuk.core.common.PorsukLogger
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.remote.GeminiService
import com.nexus.porsuk.data.remote.agents.AgentRequest
import com.nexus.porsuk.data.remote.agents.MasterAiConsensusEngine
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.AiEngineRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk AI Orchestrator.
 * The central brain that decides between Cloud and Local AI, manages caching,
 * and coordinates multi-agent consensus.
 */
@Singleton
class AiMasterOrchestrator @Inject constructor(
    private val aiEngineRepository: AiEngineRepository,
    private val settingsManager: SettingsManager,
    private val connectivityMonitor: NetworkConnectivityMonitor
) {

    /**
     * Executes an AI request by routing to the appropriate engine (Cloud/Local/Hybrid).
     */
    suspend fun executeAnalysis(request: AgentRequest): ConsensusResult {
        val mode = aiEngineRepository.getOperationMode().first()
        val apiKey = settingsManager.getGeminiApiKey()

        val isOnline = connectivityMonitor.isNetworkAvailable()

        return when (mode) {
            AiOperationMode.CLOUD_ONLY -> {
                if (isOnline) runCloudConsensus(request, apiKey)
                else runLocalConsensus(request)
            }
            AiOperationMode.LOCAL_ONLY -> runLocalConsensus(request)
            AiOperationMode.HYBRID -> {
                if (isOnline) runHybridConsensus(request, apiKey)
                else runLocalConsensus(request)
            }
        }
    }

    private suspend fun runCloudConsensus(request: AgentRequest, apiKey: String?): ConsensusResult {
        if (apiKey.isNullOrBlank()) {
            PorsukLogger.e("Cloud AI requested but API key is missing.")
            return runLocalConsensus(request)
        }
        return MasterAiConsensusEngine.runConsensus(request)
    }

    /**
     * Runs a rule-based deterministic analysis on the device.
     * 
     * NOTE: This is currently a rule-based fallback and NOT a full on-device LLM (TFLite).
     * It uses technical indicators (RSI, MACD) to generate a consensus result without
     * requiring internet access or cloud LLM calls.
     */
    private suspend fun runLocalConsensus(request: AgentRequest): ConsensusResult {
        PorsukLogger.i("Local mode: performing rule-based analysis for ${request.symbol}")
        
        val prices = request.historicalPrices
        if (prices.size < 14) {
            return ConsensusResult(
                symbol = request.symbol ?: "UNKNOWN",
                finalDecision = ConsensusDecision.WATCH,
                aggregateScore = 50,
                agentAnalyses = listOf(
                    AgentAnalysis(
                        agentType = AiAgentType.TECHNICAL,
                        score = 50,
                        confidence = 30,
                        commentary = "Yetersiz veri. Yerel analiz için en az 14 günlük fiyat geçmişi gereklidir.",
                        strengths = emptyList(),
                        weaknesses = listOf("Yetersiz Veri"),
                        decision = ConsensusDecision.WATCH
                    )
                ),
                conflictNotes = "Veri yetersizliği nedeniyle derin analiz yapılamadı."
            )
        }

        val series = Ta4jTechnicalCalculator.createBarSeries(request.symbol ?: "Stock", prices)
        val indicators = Ta4jTechnicalCalculator.calculateIndicators(series)
        
        val rsi = indicators.rsi ?: 50.0
        val macdHist = indicators.macdHist ?: 0.0
        
        val decision = when {
            rsi < 30 && macdHist > 0 -> ConsensusDecision.STRONG_BUY
            rsi < 40 -> ConsensusDecision.BUY
            rsi > 70 -> ConsensusDecision.SELL
            rsi > 60 -> ConsensusDecision.RISKY
            else -> ConsensusDecision.WATCH
        }

        val analysis = AgentAnalysis(
            agentType = AiAgentType.TECHNICAL,
            score = decision.score,
            confidence = 80,
            commentary = "Yerel kural tabanlı analiz: RSI(${"%.2f".format(rsi)}) ve MACD göstergeleri değerlendirildi. " +
                    "İnternet bağlantısı yoksa veya Yerel Mod aktifse bu sonuç gösterilir.",
            strengths = if (rsi < 40) listOf("Aşırı Satım Bölgesi", "Toparlanma Sinyali") else listOf("Stabil Trend"),
            weaknesses = if (rsi > 60) listOf("Aşırı Alım Riski") else emptyList(),
            decision = decision
        )

        return ConsensusResult(
            symbol = request.symbol ?: "UNKNOWN",
            finalDecision = decision,
            aggregateScore = decision.score,
            agentAnalyses = listOf(analysis),
            conflictNotes = null,
            debateSummary = "On-device rule engine processed technical indicators."
        )
    }

    private suspend fun runHybridConsensus(request: AgentRequest, apiKey: String?): ConsensusResult {
        // Hybrid logic: Try local first for basic metrics, use cloud for deep reasoning
        return if (!apiKey.isNullOrBlank()) {
            runCloudConsensus(request, apiKey)
        } else {
            runLocalConsensus(request)
        }
    }
    
    /**
     * Simplified chat interface through the orchestrator.
     */
    suspend fun chat(prompt: String): String {
        val apiKey = settingsManager.getGeminiApiKey() ?: return "API key required for chat."
        val service = GeminiService(apiKey)
        return service.chat(prompt)
    }
}
