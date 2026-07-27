package com.nexus.porsuk.domain.model

import androidx.compose.ui.graphics.Color
import com.nexus.porsuk.ui.theme.*

/**
 * AI Ajan Türleri
 */
enum class AiAgentType(
    val title: String,
    val iconEmoji: String,
    val accentColor: Color,
    val description: String
) {
    TECHNICAL("Teknik Analist", "📊", PrimaryTeal, "Trend, RSI, MACD ve Hacim uzmanı."),
    FUNDAMENTAL("Temel Analist", "💎", Violet, "F/K, ROE, Nakit Akışı ve Büyüme uzmanı."),
    NEWS("Haber Analisti", "📰", AquaNew, "Haberler, KAP ve Duygu Analizi uzmanı."),
    MACRO("Makro Ekonomi", "🌍", Orange, "Faiz, Enflasyon ve Merkez Bankası uzmanı."),
    RISK("Risk Analisti", "⚠️", NegatifRed, "Volatilite, Beta ve Drawdown uzmanı."),
    PORTFOLIO("Portföy Uzmanı", "💼", DemirCelik, "Çeşitlendirme ve Rebalans uzmanı."),
    DIVIDEND("Temettü Uzmanı", "💰", Gold, "Verim ve Sürdürülebilirlik uzmanı.")
}

/**
 * Konsensüs Kararları
 */
enum class ConsensusDecision(val label: String, val color: Color, val score: Int) {
    STRONG_BUY("Güçlü Al", PrimaryTeal, 90),
    BUY("Al", EmeraldNew, 75),
    WATCH("İzle", Gold, 50),
    RISKY("Riskli", Orange, 30),
    SELL("Sat", NegatifRed, 10)
}

/**
 * Tekil Ajan Analiz Çıktısı
 */
data class AgentAnalysis(
    val agentType: AiAgentType,
    val score: Int, // 0-100
    val confidence: Int, // 0-100
    val commentary: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val decision: ConsensusDecision
)

/**
 * AI Konsensüs Sonucu
 */
data class ConsensusResult(
    val symbol: String,
    val finalDecision: ConsensusDecision,
    val aggregateScore: Int,
    val agentAnalyses: List<AgentAnalysis>,
    val conflictNotes: String?,
    val debateSummary: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Ajan Performans Takibi
 */
data class AgentPerformance(
    val agentType: AiAgentType,
    val accuracyRate: Double,
    val predictionCount: Int,
    val weight: Double
)
