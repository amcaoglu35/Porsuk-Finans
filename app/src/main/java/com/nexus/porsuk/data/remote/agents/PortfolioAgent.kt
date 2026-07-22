package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.data.remote.PortfolioDoctorEngine

class PortfolioAgent : PorsukAgent {
    override val agentName: String = "Portfolio Agent"

    override suspend fun runAnalysis(request: AgentRequest): String {
        if (request.holdings.isEmpty()) {
            return "Portföy kaydı bulunamadı. Genel varlık dağılımı nötr."
        }
        val doctorResult = PortfolioDoctorEngine.analyze(request.holdings, request.companies)
        return doctorResult.diagnosisSummary
    }
}
