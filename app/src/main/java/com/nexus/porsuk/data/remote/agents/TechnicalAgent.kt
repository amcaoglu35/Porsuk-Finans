package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.DecisionEngine

class TechnicalAgent : PorsukAgent {
    override val agentName: String = "Technical Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        val decision = DecisionEngine.analyze(request.historicalPrices, request.volumes, request.newsTitles)
        return decision.preComputedSummary
    }
}
