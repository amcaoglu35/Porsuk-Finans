package com.nexus.porsuk.domain.model

/**
 * 1. Provider Pattern Arayüz Türleri
 */
enum class InstitutionalProviderType(val code: String, val displayName: String, val iconEmoji: String) {
    SEC_13F("13F", "SEC 13F Institutional Filings (USA)", "🇺🇸"),
    NASDAQ_INSIDER("NSDQ", "NASDAQ Insider Trades", "📈"),
    KAP_INSIDER("KAP", "KAP Pay Alım Satım Bildirimleri", "🇹🇷"),
    FUTURE_PROVIDERS("FUTURE", "Gelecek Kurumsal Veri Sağlayıcıları", "🌐");
}

/**
 * Pozisyon Değişim Türü (Holding Change Type)
 */
enum class HoldingChangeType(val displayName: String, val iconEmoji: String) {
    NEW_POSITION("Yeni Pozisyon (New)", "✨"),
    ADDED("Pozisyon Artırıldı (Increased)", "📈"),
    REDUCED("Pozisyon Azaltıldı (Decreased)", "📉"),
    CLOSED_OUT("Pozisyon Kapatıldı (Sold Out)", "🚫"),
    UNCHANGED("Değişmedi (Unchanged)", "➡️");
}

/**
 * Insider Yönetici Görev Türü
 */
enum class InsiderRoleType(val displayName: String) {
    CEO("Chief Executive Officer (CEO)"),
    CFO("Chief Financial Officer (CFO)"),
    DIRECTOR("Yönetim Kurulu Üyesi (Director)"),
    EXECUTIVE("Üst Düzey Yönetici (Executive Officer)"),
    LARGE_SHAREHOLDER("%10+ Büyük Ortak (Major Shareholder)");
}

/**
 * Insider İşlem Yönü
 */
enum class InsiderTransactionType(val displayName: String, val isBuy: Boolean) {
    BUY("Alım (Open Market Buy)", true),
    SELL("Satış (Open Market Sell)", false),
    OPTION_EXERCISE("Opsiyon Kullanımı (Option Exercise)", true),
    GIFT("Bağış / Devir (Gift/Grant)", true);
}

/**
 * Kurumsal Yatırımcı / Fon Modeli (Institutional Investor)
 */
data class InstitutionalInvestor(
    val investorId: String,
    val investorName: String,
    val totalAumUsd: Double, // Portfolio Value
    val topHoldingSymbol: String,
    val holdingsCount: Int,
    val lastFilingDate: String,
    val turnoverPct: Double,
    val managerName: String
)

/**
 * Fon Hissedarlık Detayı (Fund Holding Item)
 */
data class InstitutionalHoldingItem(
    val investorName: String,
    val symbol: String,
    val sharesHeld: Long,
    val marketValueUsd: Double,
    val portfolioWeightPct: Double,
    val sharesChange: Long,
    val sharesChangePct: Double,
    val changeType: HoldingChangeType,
    val filingPeriod: String
)

/**
 * Insider İşlem Kaydı (Insider Trade Record)
 */
data class InsiderTradeRecord(
    val transactionId: String,
    val companySymbol: String,
    val companyName: String,
    val insiderName: String,
    val role: InsiderRoleType,
    val transactionType: InsiderTransactionType,
    val shareAmount: Long,
    val sharePrice: Double,
    val totalValue: Double,
    val sharesHeldAfter: Long,
    val transactionDate: String,
    val filingDate: String
)

/**
 * Net Insider Aktivite Özeti
 */
data class NetInsiderActivity(
    val companySymbol: String,
    val totalBuyValue: Double,
    val totalSellValue: Double,
    val netValue: Double,
    val buyCount: Int,
    val sellCount: Int,
    val netSentiment: String // "Strong Accumulation", "Net Selling", "Neutral"
)

/**
 * Sahiplik Dağılım Yapısı (Ownership Structure)
 */
data class OwnershipBreakdown(
    val companySymbol: String,
    val institutionalOwnershipPct: Double,
    val insiderOwnershipPct: Double,
    val retailOwnershipPct: Double,
    val governmentOwnershipPct: Double = 0.0,
    val floatShares: Long,
    val totalSharesOutstanding: Long,
    val hhiConcentrationIndex: Double // Herfindahl-Hirschman Index for concentration
)

data class OwnershipHistoryPoint(
    val periodLabel: String,
    val institutionalPct: Double,
    val insiderPct: Double,
    val retailPct: Double
)

/**
 * Balina Takibi Uyarıları & Akıllı Para Akışı (Whale Alerts & Smart Money)
 */
data class WhaleAlert(
    val alertId: String,
    val companySymbol: String,
    val fundOrWhaleName: String,
    val actionDescription: String,
    val transactionAmountUsd: Double,
    val impactRating: String, // "HIGH", "MEDIUM", "LOW"
    val timestampDate: String
)

data class SmartMoneyFlowSummary(
    val companySymbol: String,
    val buyingPressureScore: Double, // 0 to 100
    val sellingPressureScore: Double, // 0 to 100
    val accumulationScore: Double, // 0 to 100
    val distributionScore: Double, // 0 to 100
    val overallInstitutionalScore: Double, // 0 to 100
    val overallInsiderScore: Double // 0 to 100
)

/**
 * AI Akıllı Para Yorumları (AI Intelligence)
 */
data class SmartMoneyAiCommentary(
    val companySymbol: String,
    val smartMoneySummaryText: String,
    val institutionalCommentaryText: String,
    val insiderCommentaryText: String,
    val riskSummaryText: String,
    val opportunityDetections: List<String>
)

/**
 * Geleceğe Hazır Kurumsal Tahmin Stubs (Future Ready Stubs)
 */
data class InstitutionalFutureStubs(
    val isAiInsiderPredictionReady: Boolean = false,
    val isWhalePredictionReady: Boolean = false,
    val isFundRankingEngineReady: Boolean = false,
    val isInstitutionScoreEngineReady: Boolean = true
)
