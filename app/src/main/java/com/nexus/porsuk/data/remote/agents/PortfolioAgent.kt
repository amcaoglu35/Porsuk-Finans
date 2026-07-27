package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.PortfolioDoctorEngine
import com.nexus.porsuk.domain.model.AiAgentType

class PortfolioAgent : BaseAgent() {
    override val agentName: String = "Portfolio Agent"
    override val agentType: AiAgentType = AiAgentType.PORTFOLIO

    override suspend fun runAnalysis(request: AgentRequest): String {
        if (request.holdings.isEmpty()) {
            return "Portföy kaydı bulunamadı. Genel varlık dağılımı nötr."
        }
        val doctorResult = PortfolioDoctorEngine.analyze(request.holdings, request.companies)
        return doctorResult.diagnosisSummary
    }
}
