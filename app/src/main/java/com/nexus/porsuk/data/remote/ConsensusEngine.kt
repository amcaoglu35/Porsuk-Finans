package com.nexus.porsuk.data.remote

/**
 * Consensus Evaluation Report DTO.
 */
data class ConsensusReport(
    val consensusScore: Int, // 0 to 100
    val technicalScore: Int, // 0-100 (Weight: 20%)
    val newsScore: Int, // 0-100 (Weight: 15%)
    val macroScore: Int, // 0-100 (Weight: 15%)
    val riskScore: Int, // 0-100 (Weight: 15%)
    val portfolioFitScore: Int, // 0-100 (Weight: 15%)
    val sourceTrustScore: Int, // 0-100 (Weight: 10%)
    val dataQualityScore: Int, // 0-100 (Weight: 10%)
    val isLowConsensus: Boolean, // True if consensusScore < 65
    val scoreDraggingFactors: List<String>, // List of components pulling down the score
    val consensusSummaryText: String
)

/**
 * Consensus Engine for Porsuk Finans.
 * Combines 7 weighted diagnostic categories into a single unified Consensus Score (0 to 100):
 * 1. Teknik Analiz (%20)
 * 2. Haber Duyarlılığı (%15)
 * 3. Makro Veriler (%15)
 * 4. Risk Düzeyi (%15)
 * 5. Portföy Uyumu (%15)
 * 6. Kaynak Güvenilirliği (%10)
 * 7. Veri Kalitesi (%10)
 * 
 * Issues explicit warnings if Consensus Score < 65 and explains dragging factors.
 */
object ConsensusEngine {

    fun calculateConsensus(
        technicalScore: Int = 75,
        newsScore: Int = 70,
        macroScore: Int = 80,
        riskScore: Int = 70,
        portfolioFitScore: Int = 85,
        sourceTrustScore: Int = 90,
        dataQualityScore: Int = 85
    ): ConsensusReport {
        val weightedSum = (technicalScore * 0.20) +
                (newsScore * 0.15) +
                (macroScore * 0.15) +
                (riskScore * 0.15) +
                (portfolioFitScore * 0.15) +
                (sourceTrustScore * 0.10) +
                (dataQualityScore * 0.10)

        val finalScore = weightedSum.toInt().coerceIn(0, 100)
        val isLow = finalScore < 65

        val dragList = mutableListOf<String>()
        if (technicalScore < 60) dragList.add("Teknik Göstergeler ($technicalScore/100)")
        if (newsScore < 60) dragList.add("Haber Duyarlılığı ($newsScore/100)")
        if (macroScore < 60) dragList.add("Makro Piyasa Rüzgarı ($macroScore/100)")
        if (riskScore < 60) dragList.add("Yüksek Volatilite & Risk ($riskScore/100)")
        if (portfolioFitScore < 60) dragList.add("Sektörel Portföy Uyumsuzluğu ($portfolioFitScore/100)")
        if (sourceTrustScore < 60) dragList.add("Düşük Kaynak Güvenilirliği ($sourceTrustScore/100)")
        if (dataQualityScore < 60) dragList.add("Kısıtlı Veri Kalitesi ($dataQualityScore/100)")

        val summary = "BÜTÜNLEŞİK KONSENSÜS SKORU: $finalScore/100 (Teknik: $technicalScore, Haber: $newsScore, Makro: $macroScore, Risk: $riskScore, Portföy: $portfolioFitScore, Güven: $sourceTrustScore, Kalite: $dataQualityScore)."

        return ConsensusReport(
            consensusScore = finalScore,
            technicalScore = technicalScore,
            newsScore = newsScore,
            macroScore = macroScore,
            riskScore = riskScore,
            portfolioFitScore = portfolioFitScore,
            sourceTrustScore = sourceTrustScore,
            dataQualityScore = dataQualityScore,
            isLowConsensus = isLow,
            scoreDraggingFactors = dragList,
            consensusSummaryText = summary
        )
    }
}
