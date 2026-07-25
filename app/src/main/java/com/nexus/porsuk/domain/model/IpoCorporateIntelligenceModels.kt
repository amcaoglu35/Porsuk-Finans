package com.nexus.porsuk.domain.model

/**
 * Porsuk Finans — IPO & Corporate Actions Intelligence Domain Models
 */

enum class IpoStatus {
    UPCOMING, ACTIVE, PRICED, TRADING, WITHDRAWN
}

data class IpoIntelligence(
    val symbol: String,
    val companyName: String,
    val status: IpoStatus,
    val market: String, // BIST, NASDAQ etc.
    val sector: String,
    val offerPrice: Double?,
    val finalPrice: Double?,
    val lotQuantity: Long?,
    val issueSize: Double?, // Total money raised
    val distributionMethod: String, // Equal, Proportional
    val startDate: Long?,
    val endDate: Long?,
    val listingDate: Long?,
    val prospectusUrl: String?,
    val leadManager: String?, // Broker
    val description: String?,
    val isShariaCompliant: Boolean = false
)

enum class CorporateActionType {
    DIVIDEND, STOCK_SPLIT, REVERSE_SPLIT, BONUS_ISSUE, RIGHTS_ISSUE,
    CAPITAL_INCREASE, SHARE_BUYBACK, SPIN_OFF, MERGER, ACQUISITION,
    DELISTING, TICKER_CHANGE
}

data class CorporateAction(
    val actionId: String,
    val symbol: String,
    val type: CorporateActionType,
    val announcementDate: Long,
    val effectiveDate: Long, // Ex-Date for dividends/splits
    val ratio: Double? = null, // Split ratio or bonus ratio
    val amount: Double? = null, // Dividend amount
    val currency: String = "TRY",
    val description: String?,
    val status: String // PENDING, COMPLETED, CANCELLED
)

data class DividendAnalytics(
    val symbol: String,
    val currentYield: Double,
    val average5YYield: Double?,
    val payoutRatio: Double?,
    val growth5Y: Double?,
    val dividendHistory: List<DividendHistoryItem>,
    val sustainabilityScore: Int, // 0-100
    val forecastYield: Double?
)

data class DividendHistoryItem(
    val exDate: Long,
    val paymentDate: Long,
    val amount: Double,
    val currency: String
)

data class CorporateCalendarEvent(
    val id: String,
    val symbol: String,
    val companyName: String,
    val title: String,
    val date: Long,
    val type: String, // IPO, DIVIDEND, SPLIT, MEETING, EARNINGS
    val importance: String // HIGH, MEDIUM, LOW
)

data class IpoAiSummary(
    val symbol: String,
    val summary: String,
    val riskAssessment: String,
    val valuationCommentary: String,
    val pros: List<String>,
    val cons: List<String>,
    val sentiment: String // POSITIVE, NEGATIVE, NEUTRAL
)
