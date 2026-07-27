package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.DecisionEngine
import com.nexus.porsuk.domain.model.AiAgentType

class TechnicalAgent : BaseAgent() {
    override val agentName: String = "Technical Agent"
    override val agentType: AiAgentType = AiAgentType.TECHNICAL

    override suspend fun runAnalysis(request: AgentRequest): String {
        val decision = DecisionEngine.analyze(request.historicalPrices, request.volumes, request.newsTitles)
        return decision.preComputedSummary
    }

    override fun extractScore(commentary: String): Int {
        // Technical agent summary doesn't have X/100 by default, 
        // but it's pre-computed in runStructuredAnalysis in my previous attempt.
        // Let's refine.
        return super.extractScore(commentary)
    }
}
