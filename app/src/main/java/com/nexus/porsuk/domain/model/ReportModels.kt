package com.nexus.porsuk.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Rapor Türleri
 */
enum class ReportType(val label: String, val iconEmoji: String) {
    PORTFOLIO("Portföy Raporu", "💼"),
    PERFORMANCE("Performans Raporu", "📈"),
    AI_ANALYSIS("AI Analiz Raporu", "🧠"),
    RISK("Risk Raporu", "⚠️"),
    TAX("Vergi Raporu", "🧾"),
    CUSTOM("Özel Rapor", "🛠️")
}

/**
 * Rapor Formatları
 */
enum class ReportFormat(val extension: String) {
    PDF("pdf"),
    EXCEL("xlsx"),
    CSV("csv")
}

/**
 * Rapor Zamanlama
 */
enum class ReportSchedule(val label: String) {
    MANUAL("Manuel"),
    DAILY("Günlük"),
    WEEKLY("Haftalık"),
    MONTHLY("Aylık")
}

/**
 * Genel Rapor Modeli
 */
data class EnterpriseReport(
    val reportId: String = "rep_${System.currentTimeMillis()}",
    val title: String,
    val type: ReportType,
    val createdAt: Long = System.currentTimeMillis(),
    val format: ReportFormat,
    val status: String = "COMPLETED", // COMPLETED, PENDING, FAILED
    val fileUri: String? = null,
    val aiSummary: String? = null
)

/**
 * Portföy Rapor Detayları
 */
data class PortfolioReportData(
    val totalValue: Double,
    val totalProfitLoss: Double,
    val dailyChangePercent: Double,
    val monthlyReturnPercent: Double,
    val yearlyReturnPercent: Double,
    val assetDistribution: Map<String, Double>, // Sector/Asset -> Percentage
    val holdings: List<ReportHoldingItem>
)

data class ReportHoldingItem(
    val symbol: String,
    val quantity: Double,
    val currentPrice: Double,
    val profitLoss: Double,
    val weight: Double
)

/**
 * AI Rapor Detayları
 */
data class AiReportData(
    val generalCommentary: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val risks: List<String>,
    val opportunities: List<String>,
    val rebalancingSuggestions: List<String>
)

/**
 * Risk Rapor Detayları
 */
data class RiskReportData(
    val volatility: Double,
    val beta: Double,
    val sharpeRatio: Double,
    val maxDrawdown: Double,
    val riskScore: Int,
    val aiConfidenceScore: Int
)

/**
 * Vergi Rapor Özeti
 */
data class TaxReportData(
    val realizedTradesCount: Int,
    val totalRealizedPL: Double,
    val estimatedTaxAmount: Double,
    val tradeHistory: List<TaxTradeItem>,
    val legalDisclaimer: String = "Bu rapor sadece bilgilendirme amaçlıdır. Resmi vergi beyanı yerine geçmez."
)

data class TaxTradeItem(
    val symbol: String,
    val buyDate: Long,
    val sellDate: Long,
    val profitLoss: Double
)
