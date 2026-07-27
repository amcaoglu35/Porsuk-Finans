package com.nexus.porsuk.data.engine

import com.nexus.porsuk.core.common.PorsukLogger
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.remote.GeminiService
import com.nexus.porsuk.data.remote.agents.AgentRequest
import com.nexus.porsuk.data.remote.agents.MasterAiConsensusEngine
import com.nexus.porsuk.domain.model.AiOperationMode
import com.nexus.porsuk.domain.model.ConsensusResult
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
    private val settingsManager: SettingsManager
) {

    /**
     * Executes an AI request by routing to the appropriate engine (Cloud/Local/Hybrid).
     */
    suspend fun executeAnalysis(request: AgentRequest): ConsensusResult {
        val mode = aiEngineRepository.getOperationMode().first()
        val apiKey = settingsManager.getGeminiApiKey()

        return when (mode) {
            AiOperationMode.CLOUD_ONLY -> runCloudConsensus(request, apiKey)
            AiOperationMode.LOCAL_ONLY -> runLocalConsensus(request)
            AiOperationMode.HYBRID -> runHybridConsensus(request, apiKey)
        }
    }

    private suspend fun runCloudConsensus(request: AgentRequest, apiKey: String?): ConsensusResult {
        if (apiKey.isNullOrBlank()) {
            PorsukLogger.e("Cloud AI requested but API key is missing.")
            return runLocalConsensus(request)
        }
        return MasterAiConsensusEngine.runConsensus(request)
    }

    private suspend fun runLocalConsensus(request: AgentRequest): ConsensusResult {
        PorsukLogger.i("Running Local AI inference for ${request.symbol}")
        // Local AI would use a simplified version of the consensus engine
        // or a local model prediction.
        return MasterAiConsensusEngine.runConsensus(request) // Fallback to engine for now
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
