package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.PorsukBrainManager

class BrainAgent : PorsukAgent {
    override val agentName: String = "Brain Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        return PorsukBrainManager.buildBrainContext(
            memory = null,
            requestedSymbol = request.symbol,
            userHoldings = request.holdings,
            companies = request.companies
        )
    }
}
