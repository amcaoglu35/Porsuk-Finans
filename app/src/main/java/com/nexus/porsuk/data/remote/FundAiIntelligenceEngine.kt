package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FundAiIntelligenceEngine @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend fun generateFundReport(
        intelligence: FundIntelligence,
        performance: FundPerformance,
        risk: FundRiskMetrics
    ): FundIntelligenceAiSummary = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext emptySummary(intelligence.code)
        val service = GeminiService(apiKey)
        
        val prompt = """
            Analyze the following fund/ETF for a professional financial report (Morningstar Direct level):
            Fund Name: ${intelligence.name} (${intelligence.code})
            Type: ${intelligence.type}
            AUM: ${intelligence.aum} ${intelligence.currency}
            Expense Ratio: ${intelligence.expenseRatio}%
            1Y Performance: ${performance.yearly1}%
            Sharpe Ratio: ${risk.sharpeRatio}
            Max Drawdown: ${risk.maxDrawdown}%
            
            Provide:
            1. Short executive summary.
            2. 3 Pros.
            3. 3 Cons.
            4. Detailed risk assessment.
            5. Investor suitability profile.
            
            Response should be in Turkish.
        """.trimIndent()

        val response = service.chat(prompt)
        
        // Parsing logic would go here, return mock for now
        FundIntelligenceAiSummary(
            fundCode = intelligence.code,
            summary = response,
            pros = listOf("Düşük gider oranı", "Güçlü geçmiş performans", "Yüksek likidite"),
            cons = listOf("Dar sektör odaklılığı", "Yüksek volatilite", "Döviz riski"),
            riskAssessment = "Orta-Yüksek risk seviyesinde, agresif yatırımcılar için uygundur.",
            suitableFor = "Büyüme odaklı, uzun vadeli portföyler.",
            similarFunds = emptyList(),
            alternativeFunds = emptyList()
        )
    }

    private fun emptySummary(code: String) = FundIntelligenceAiSummary(
        code, "Analiz hazır değil.", emptyList(), emptyList(), "", "", emptyList(), emptyList()
    )
}
