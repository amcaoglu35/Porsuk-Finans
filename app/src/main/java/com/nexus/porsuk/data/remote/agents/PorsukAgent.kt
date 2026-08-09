package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.domain.model.AiAgentType
import com.nexus.porsuk.domain.model.ConsensusDecision

/**
 * Request DTO passed to Multi-Agent AI System.
 */
data class AgentRequest(
    val symbol: String? = null,
    val currency: String = "TRY",
    val historicalPrices: List<Double> = emptyList(),
    val volumes: List<Double> = emptyList(),
    val newsTitles: List<String> = emptyList(),
    val holdings: List<BasketItem> = emptyList(),
    val companies: List<Company> = emptyList(),
    val companyInfos: List<CachedCompanyInfo> = emptyList()
)

/**
 * Structured result from a specialized AI Agent.
 */
data class AgentAnalysisResult(
    val agentType: AiAgentType,
    val score: Int,
    val confidence: Int,
    val commentary: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val decision: ConsensusDecision
)

/**
 * Base interface for all specialized Porsuk AI Agents.
 * Each agent computes its own localized diagnosis string independently in Kotlin.
 */
interface PorsukAgent {
    val agentName: String
    val agentType: AiAgentType
    suspend fun runAnalysis(request: AgentRequest): String
    suspend fun runStructuredAnalysis(request: AgentRequest): AgentAnalysisResult
}
