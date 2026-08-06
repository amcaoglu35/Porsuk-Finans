package com.nexus.porsuk.feature.companydetail

import com.nexus.porsuk.data.local.entity.*
import com.nexus.porsuk.domain.model.MarketQuote
import com.nexus.porsuk.ui.analysis.DuPontBreakdown
import com.nexus.porsuk.ui.analysis.PiotroskiResult
import com.nexus.porsuk.ui.analysis.FinancialHealthFlags
import com.nexus.porsuk.ui.analysis.CashFlowAnalysisSummary

/**
 * Porsuk Company Detail Module — 5 Premium Sekme Tanımları
 */
enum class CompanyDetailTab(val title: String) {
    OVERVIEW("Genel Bakış"),
    FINANCIALS("Finansallar"),
    ANALYSIS("Analizler"),
    NEWS("Haberler"),
    CORPORATE("Kurumsallar"),
    AI_ORACLE("AI Oracle")
}

enum class ChartType {
    LINE, CANDLESTICK
}

enum class ChartTimeFrame(val label: String) {
    ONE_DAY("1G"),
    ONE_WEEK("1H"),
    ONE_MONTH("1A"),
    ONE_YEAR("1Y"),
    ALL("TÜMÜ")
}

data class CandleStickData(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

/**
 * Porsuk Company Detail Module — UI Ekran Durumu (CompanyDetailUiState)
 */
data class CompanyDetailUiState(
    val symbol: String = "",
    val company: CompanyEntity? = null,
    val quote: MarketQuote? = null,
    val dividends: List<DividendEntity> = emptyList(),
    val earnings: List<EarningsEntity> = emptyList(),
    val news: List<NewsEntity> = emptyList(),
    val aiHistory: AIHistoryEntity? = null,
    val isFavorite: Boolean = false,
    val selectedTab: CompanyDetailTab = CompanyDetailTab.OVERVIEW,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val lastUpdatedFormatted: String? = null,

    // Interactive Chart Options
    val chartType: ChartType = ChartType.LINE,
    val selectedTimeFrame: ChartTimeFrame = ChartTimeFrame.ONE_MONTH,
    val candleStickList: List<CandleStickData> = emptyList(),

    // Redesign - AI & Summary
    val aiSummary: String = "",
    val aiRisks: List<String> = emptyList(),
    val aiOpportunities: List<String> = emptyList(),
    val aiTargetPrice: Double = 0.0,
    val aiPotentialReturn: Double = 0.0,
    val aiAgents: List<AiAgentConsensus> = emptyList(),

    // Redesign - Quick Metrics
    val quickMetrics: List<QuickMetricItem> = emptyList(),

    // Redesign - Financials Data
    val financialSummary: FinancialSummaryData = FinancialSummaryData(),
    val quarterlyPerformance: List<QuarterlyBarData> = emptyList(),
    val marginAnalysis: List<MarginLineData> = emptyList(),
    val financialHealth: FinancialHealthData = FinancialHealthData(),

    // Redesign - Analysis Data
    val valuationModules: List<ScoreCardData> = emptyList(),
    val qualityModules: List<ScoreCardData> = emptyList(),
    val riskModules: List<ScoreCardData> = emptyList(),
    val aiScenarios: List<AiScenarioData> = emptyList(),
    val analystConsensus: Double = 0.0,
    val aiConfidenceScore: Double = 0.0,

    // Structured Analysis Results (from ui/analysis calculators)
    val duPontBreakdown: DuPontBreakdown? = null,
    val piotroskiResult: PiotroskiResult? = null,
    val financialHealthFlags: FinancialHealthFlags? = null,
    val cashFlowSummary: CashFlowAnalysisSummary? = null,

    // Redesign - Corporate Data
    val boardMembers: List<BoardMember> = emptyList(),
    val ownershipStructure: List<OwnerData> = emptyList(),
    val corporateTimeline: List<TimelineEvent> = emptyList(),

    // AI Oracle
    val aiOracleReport: AiOracleReport? = null
)

data class AiOracleReport(
    val aiScore: Int,
    val riskScore: Int,
    val confidence: Int,
    val fairValue: Double,
    val recommendation: String,
    val investmentThesis: String
)

data class AiAgentConsensus(
    val name: String,
    val avatarUrl: String?,
    val consensus: String, // BUY, HOLD, SELL
    val confidence: Double
)

data class QuickMetricItem(
    val label: String,
    val value: String,
    val trend: Double? = null // Positive for green, negative for red
)

data class FinancialSummaryData(
    val revenue: String = "",
    val grossProfit: String = "",
    val ebitda: String = "",
    val netIncome: String = "",
    val eps: String = "",
    val equity: String = "",
    val totalAssets: String = "",
    val totalDebt: String = "",
    val netDebt: String = ""
)

data class QuarterlyBarData(
    val quarter: String,
    val revenue: Double,
    val ebitda: Double,
    val netIncome: Double
)

data class MarginLineData(
    val date: String,
    val grossMargin: Double,
    val netMargin: Double,
    val ebitdaMargin: Double
)

data class FinancialHealthData(
    val liquidity: Double = 0.0,
    val leverage: Double = 0.0,
    val interestCoverage: Double = 0.0,
    val cashPosition: Double = 0.0,
    val currentRatio: Double = 0.0,
    val quickRatio: Double = 0.0
)

data class ScoreCardData(
    val title: String,
    val value: String,
    val score: Double, // 0.0 - 1.0 for color coding
    val status: String // Undervalued, Overvalued, Excellent, etc.
)

data class AiScenarioData(
    val type: String, // Bull, Bear, Base
    val description: String,
    val targetPrice: Double,
    val probability: Double
)

data class BoardMember(
    val name: String,
    val role: String,
    val avatarUrl: String? = null
)

data class OwnerData(
    val name: String,
    val share: Double
)

data class TimelineEvent(
    val date: String,
    val title: String,
    val description: String
)
