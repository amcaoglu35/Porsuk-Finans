package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.domain.model.AiAgentType
import com.nexus.porsuk.domain.model.ConsensusDecision

/**
 * Base helper for AI Agents to provide default implementations for structured analysis.
 */
abstract class BaseAgent : PorsukAgent {
    
    override suspend fun runStructuredAnalysis(request: AgentRequest): AgentAnalysisResult {
        val commentary = runAnalysis(request)
        val score = extractScore(commentary)
        
        val decision = when {
            score >= 80 -> ConsensusDecision.STRONG_BUY
            score >= 65 -> ConsensusDecision.BUY
            score >= 45 -> ConsensusDecision.WATCH
            score >= 25 -> ConsensusDecision.RISKY
            else -> ConsensusDecision.SELL
        }

        return AgentAnalysisResult(
            agentType = agentType,
            score = score,
            confidence = 75,
            commentary = commentary,
            strengths = extractPoints(commentary, true),
            weaknesses = extractPoints(commentary, false),
            decision = decision
        )
    }

    protected open fun extractScore(commentary: String): Int {
        // Attempt to find something like "Score: 75/100" in the string
        val match = Regex("(\\d+)/100").find(commentary)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 50
    }

    protected open fun extractPoints(commentary: String, positive: Boolean): List<String> {
        // Placeholder for real extraction logic
        return emptyList()
    }
}
