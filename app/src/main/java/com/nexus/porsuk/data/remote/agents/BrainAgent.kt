package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.PorsukBrainManager
import com.nexus.porsuk.domain.model.AiAgentType

class BrainAgent : BaseAgent() {
    override val agentName: String = "Brain Agent"
    override val agentType: AiAgentType = AiAgentType.TECHNICAL

    override suspend fun runAnalysis(request: AgentRequest): String {
        return PorsukBrainManager.buildBrainContext(
            memory = null,
            requestedSymbol = request.symbol,
            userHoldings = request.holdings,
            companies = request.companies
        )
    }
}
