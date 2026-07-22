package com.nexus.porsuk.data.remote.agents

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Master AI Orchestrator for Porsuk Finans.
 * Coordinates all specialized domain agents (Portfolio, News, Technical, Macro, Risk, Brain) in parallel in Kotlin.
 * Synthesizes their localized summaries into a unified Master AI prompt payload for Gemini.
 */
object MasterAiOrchestrator {

    val defaultAgents: List<PorsukAgent> = listOf(
        PortfolioAgent(),
        NewsAgent(),
        TechnicalAgent(),
        MacroAgent(),
        RiskAgent(),
        BrainAgent()
    )

    /**
     * Executes all agents asynchronously in parallel and combines their localized diagnosis outputs.
     */
    suspend fun runMultiAgentPipeline(
        request: AgentRequest,
        customAgents: List<PorsukAgent> = defaultAgents
    ): String = coroutineScope {
        val summaries = customAgents.map { agent ->
            async {
                "【${agent.agentName}】: ${agent.runAnalysis(request)}"
            }
        }.map { it.await() }

        summaries.joinToString("\n\n")
    }
}
