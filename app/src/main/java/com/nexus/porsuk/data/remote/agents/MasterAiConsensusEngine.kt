package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.ui.common.GeminiModels
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Advanced Multi-Agent Consensus Engine.
 * Aggregates results from specialized agents, detects conflicts, and facilitates debates.
 */
object MasterAiConsensusEngine {

    val allAgents: List<PorsukAgent> = listOf(
        TechnicalAgent(),
        FundamentalAgent(),
        NewsAgent(),
        MacroAgent(),
        RiskAgent(),
        PortfolioAgent(),
        DividendAgent()
    )

    /**
     * Executes all agents and produces a consensus result.
     */
    suspend fun runConsensus(request: AgentRequest): ConsensusResult = coroutineScope {
        // Run all agents in parallel
        val analysisResults = allAgents.map { agent ->
            async {
                agent.runStructuredAnalysis(request)
            }
        }.map { it.await() }

        // Calculate weighted sum
        var weightedSum = 0.0
        var totalWeight = 0.0
        
        analysisResults.forEach { result ->
            val weight = when(result.agentType) {
                AiAgentType.TECHNICAL, AiAgentType.FUNDAMENTAL -> 1.5
                AiAgentType.RISK -> 1.2
                else -> 1.0
            }
            weightedSum += result.score * weight
            totalWeight += weight
        }

        val aggregateScore = (weightedSum / totalWeight).toInt()
        
        val finalDecision = when {
            aggregateScore >= 85 -> ConsensusDecision.STRONG_BUY
            aggregateScore >= 70 -> ConsensusDecision.BUY
            aggregateScore >= 50 -> ConsensusDecision.WATCH
            aggregateScore >= 30 -> ConsensusDecision.RISKY
            else -> ConsensusDecision.SELL
        }

        val conflictNotes = detectConflicts(analysisResults)

        ConsensusResult(
            symbol = request.symbol ?: "N/A",
            finalDecision = finalDecision,
            aggregateScore = aggregateScore,
            agentAnalyses = analysisResults.map { 
                AgentAnalysis(
                    agentType = it.agentType,
                    score = it.score,
                    confidence = it.confidence,
                    commentary = it.commentary,
                    strengths = it.strengths,
                    weaknesses = it.weaknesses,
                    decision = it.decision
                )
            },
            conflictNotes = conflictNotes
        )
    }

    /**
     * Facilitates an AI debate between agents to reach a consensus.
     */
    suspend fun runDebate(apiKey: String, currentConsensus: ConsensusResult): String {
        val prompt = StringBuilder()
        prompt.append("Aşağıdaki AI ajanları bir varlık üzerinde fikir ayrılığı yaşıyor. ")
        prompt.append("Her ajan kendi uzmanlık alanına göre görüşünü savunmalı ve sonunda ortak bir karara varmalıdır.\n\n")
        
        currentConsensus.agentAnalyses.forEach { agent ->
            prompt.append("${agent.agentType.title}: ${agent.commentary}\n")
        }
        
        prompt.append("\nLütfen bir 'AI TARTIŞMASI' formatında, ajanların birbiriyle konuşmasını sağla ve sonunda KONSENSÜS KARARINI açıkla.")

        return try {
            GeminiModels.generateContentWithFallback(
                apiKey = apiKey,
                prompt = prompt.toString()
            )
        } catch (e: Exception) {
            "Tartışma sırasında bir hata oluştu: ${e.localizedMessage}"
        }
    }

    private fun detectConflicts(results: List<AgentAnalysisResult>): String? {
        val decisions = results.map { it.decision }
        val hasBuy = decisions.any { it == ConsensusDecision.STRONG_BUY || it == ConsensusDecision.BUY }
        val hasSell = decisions.any { it == ConsensusDecision.SELL || it == ConsensusDecision.RISKY }
        
        return if (hasBuy && hasSell) {
            val buyAgents = results.filter { it.decision == ConsensusDecision.STRONG_BUY || it.decision == ConsensusDecision.BUY }.map { it.agentType.title }
            val sellAgents = results.filter { it.decision == ConsensusDecision.SELL || it.decision == ConsensusDecision.RISKY }.map { it.agentType.title }
            "FİKİR AYRILIĞI: ${buyAgents.joinToString(", ")} yükseliş beklerken, ${sellAgents.joinToString(", ")} risklere dikkat çekiyor."
        } else null
    }
}
