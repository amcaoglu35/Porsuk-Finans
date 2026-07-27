package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.MarketIntelligenceEngine
import com.nexus.porsuk.domain.model.AiAgentType

class MacroAgent : BaseAgent() {
    override val agentName: String = "Macro Agent"
    override val agentType: AiAgentType = AiAgentType.MACRO

    override suspend fun runAnalysis(request: AgentRequest): String {
        return MarketIntelligenceEngine.getMarketSummaryParagraph()
    }
}
