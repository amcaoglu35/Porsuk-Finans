package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.MarketIntelligenceEngine

class MacroAgent : PorsukAgent {
    override val agentName: String = "Macro Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        return MarketIntelligenceEngine.getMarketSummaryParagraph()
    }
}
