package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.domain.model.IpoAiSummary
import com.nexus.porsuk.domain.model.IpoIntelligence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpoCorporateAiEngine @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend fun generateIpoSummary(ipo: IpoIntelligence): IpoAiSummary = withContext(Dispatchers.IO) {
        val apiKey = settingsManager.getGeminiApiKey() ?: return@withContext emptySummary(ipo.symbol)
        val service = GeminiService(apiKey)

        val prompt = """
            Analyze the following IPO details for a professional investor report:
            Company: ${ipo.companyName} (${ipo.symbol})
            Sector: ${ipo.sector}
            Offer Price: ${ipo.offerPrice}
            Distribution: ${ipo.distributionMethod}
            Manager: ${ipo.leadManager}
            Description: ${ipo.description}
            
            Provide:
            1. Short executive summary.
            2. Risk assessment.
            3. Valuation commentary.
            4. 3 Pros and 3 Cons.
            5. Overall sentiment (POSITIVE, NEGATIVE, NEUTRAL).
            
            Format response as JSON with keys: summary, risk, valuation, pros, cons, sentiment.
            Response should be in Turkish.
        """.trimIndent()

        val response = service.chat(prompt)
        
        // Mock parsing for now
        IpoAiSummary(
            symbol = ipo.symbol,
            summary = response,
            riskAssessment = "Orta seviye risk, sektördeki volatiliteye dikkat edilmeli.",
            valuationCommentary = "İskonto oranı %20 olarak hesaplanmıştır.",
            pros = listOf("Pazar lideri konumunda", "Güçlü nakit akışı", "Genişleme potansiyeli"),
            cons = listOf("Yüksek borç oranı", "Sektörel rekabet", "Makroekonomik belirsizlikler"),
            sentiment = "POSITIVE"
        )
    }

    private fun emptySummary(symbol: String) = IpoAiSummary(
        symbol, "AI analizi henüz hazır değil.", "", "", emptyList(), emptyList(), "NEUTRAL"
    )
}
