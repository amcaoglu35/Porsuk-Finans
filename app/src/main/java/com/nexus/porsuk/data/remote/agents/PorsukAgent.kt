package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company

/**
 * Request DTO passed to Multi-Agent AI System.
 */
data class AgentRequest(
    val symbol: String? = null,
    val historicalPrices: List<Double> = emptyList(),
    val volumes: List<Double> = emptyList(),
    val newsTitles: List<String> = emptyList(),
    val holdings: List<BasketItem> = emptyList(),
    val companies: List<Company> = emptyList(),
    val companyInfos: List<CachedCompanyInfo> = emptyList()
)

/**
 * Base interface for all specialized Porsuk AI Agents.
 * Each agent computes its own localized diagnosis string independently in Kotlin.
 */
interface PorsukAgent {
    val agentName: String
    suspend fun runAnalysis(request: AgentRequest): String
}
