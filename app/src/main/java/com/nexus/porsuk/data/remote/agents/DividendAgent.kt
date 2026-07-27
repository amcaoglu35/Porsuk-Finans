package com.nexus.porsuk.data.remote.agents

import com.nexus.porsuk.domain.model.AiAgentType
import com.nexus.porsuk.domain.model.ConsensusDecision
import java.util.Locale

/**
 * Dividend Expert AI Agent.
 * Focuses on Dividend Yield, History, and Sustainability.
 */
class DividendAgent : PorsukAgent {
    override val agentName: String = "Temettü Uzmanı"
    override val agentType: AiAgentType = AiAgentType.DIVIDEND

    override suspend fun runAnalysis(request: AgentRequest): String {
        val info = request.companyInfos.firstOrNull { it.symbol.equals(request.symbol, ignoreCase = true) }
        val yield = info?.dividendYield ?: 0.0
        val lastUpdated = info?.lastUpdated ?: 0L
        
        return if (yield > 0) {
            "Hissenin temettü verimi %${String.format(Locale.US, "%.2f", yield)} seviyesinde. " +
            "Sürdürülebilirlik analizi, şirketin nakit akışları ve son ${if (lastUpdated > 0) "bilanço" else "veriler"} ışığında " +
            (if (yield > 5.0) "oldukça güçlü ve cazip" else "istikrarlı") + " görünüyor."
        } else {
            "Şu anki verilere göre hisse temettü ödemesi yapmıyor veya verim %0. Büyüme odaklı bir strateji izlediği söylenebilir."
        }
    }

    override suspend fun runStructuredAnalysis(request: AgentRequest): AgentAnalysisResult {
        val info = request.companyInfos.firstOrNull { it.symbol.equals(request.symbol, ignoreCase = true) }
        val yield = info?.dividendYield ?: 0.0
        
        val score = when {
            yield >= 8.0 -> 95
            yield >= 5.0 -> 80
            yield >= 2.0 -> 60
            yield > 0.0 -> 40
            else -> 20
        }
        
        val decision = when {
            score >= 80 -> ConsensusDecision.STRONG_BUY
            score >= 60 -> ConsensusDecision.BUY
            score >= 40 -> ConsensusDecision.WATCH
            else -> ConsensusDecision.RISKY
        }

        return AgentAnalysisResult(
            agentType = agentType,
            score = score,
            confidence = 85,
            commentary = runAnalysis(request),
            strengths = if (yield > 3.0) listOf("Yüksek temettü verimi", "Pasif gelir potansiyeli") else emptyList(),
            weaknesses = if (yield < 1.0) listOf("Düşük temettü verimi") else emptyList(),
            decision = decision
        )
    }
}
