package com.nexus.porsuk.domain.model

/**
 * 10 Şirket Olayı Türü (Corporate Event Types)
 */
enum class CorporateEventType(val code: String, val title: String, val iconEmoji: String) {
    MERGER("MERGER", "Birleşme (Merger)", "🤝"),
    ACQUISITION("ACQ", "Satın Alım (Acquisition)", "🛍️"),
    SPIN_OFF("SPIN", "Şirket Bölünmesi (Spin-Off)", "✂️"),
    JOINT_VENTURE("JV", "Ortak Girişim (Joint Venture)", "🤛"),
    STRATEGIC_PARTNERSHIP("PARTNER", "Stratejik Ortaklık", "🌐"),
    ASSET_SALE("ASSET", "Varlık Satışı (Asset Sale)", "🏛️"),
    RESTRUCTURING("RESTRUCT", "Yeniden Yapılanma (Restructuring)", "🏗️"),
    BANKRUPTCY("BANKRUPT", "İflas / Konkordato", "⚠️"),
    DELISTING("DELIST", "KOT Çıkarma (Delisting)", "🚫"),
    TICKER_CHANGE("TICKER", "Sembol Değişimi (Ticker Change)", "🔤");
}

/**
 * İşlem Durumu (Deal Status)
 */
enum class DealStatus(val displayName: String, val iconEmoji: String) {
    PROPOSED("Teklif Edildi (Proposed)", "💡"),
    PENDING("Onay Bekliyor (Pending)", "⏳"),
    APPROVED("Regülasyon Onaylı (Approved)", "✅"),
    COMPLETED("Tamamlandı (Completed)", "🎉"),
    TERMINATED("İptal Edildi (Terminated)", "❌");
}

/**
 * Ödeme Yöntemi (Payment Method)
 */
enum class DealPaymentType(val displayName: String) {
    ALL_CASH("Nakit Ödeme (All-Cash)"),
    ALL_STOCK("Hisse Takası (All-Stock)"),
    CASH_AND_STOCK("Nakit + Hisse Karışık (Mixed)");
}

/**
 * Şirket Olayı Öğesi (Corporate Event)
 */
data class CorporateEvent(
    val eventId: String,
    val companySymbol: String,
    val companyName: String,
    val eventType: CorporateEventType,
    val eventDate: String,
    val title: String,
    val description: String,
    val impactScore: Double, // 0.0 to 100.0
    val isUpcoming: Boolean = false
)

/**
 * M&A İşlem Analitiği Öğesi (Deal Analytics Item)
 */
data class DealAnalyticsItem(
    val dealId: String,
    val acquirerSymbol: String,
    val acquirerName: String,
    val targetSymbol: String,
    val targetName: String,
    val dealValueUsd: Double,
    val premiumPaidPct: Double, // Premium paid over market price
    val evEbitdaMultiple: Double, // Enterprise Value / EBITDA
    val paymentType: DealPaymentType = DealPaymentType.CASH_AND_STOCK,
    val status: DealStatus = DealStatus.PENDING,
    val announcementDate: String,
    val expectedClosingDate: String,
    val financialAdvisors: List<String> = emptyList(),
    val legalAdvisors: List<String> = emptyList()
)

/**
 * İşlem Etki Analizi (Impact Analysis)
 */
data class DealImpactAnalysis(
    val dealId: String,
    val revenueImpactPct: Double,
    val marketCapImpactPct: Double,
    val industryImpactSummary: String,
    val competitiveImpactSummary: String,
    val riskAnalysisSummary: String
)

/**
 * AI Akıllı M&A Analitiği & Sinerji Yorumu
 */
data class DealAiIntelligence(
    val dealId: String,
    val dealSummaryText: String,
    val strategicAnalysisText: String,
    val costSynergyUsd: Double,
    val revenueSynergyUsd: Double,
    val riskSummaryText: String,
    val opportunitySummaryText: String
)

/**
 * İşlem Görselleştirme Verileri
 */
data class DealVisuals(
    val dealTimelineMilestones: List<DealMilestoneStep>,
    val industryComparisonMultiples: Map<String, Double>, // Company -> EV/EBITDA
    val dealStatisticsMap: Map<String, Double>
)

data class DealMilestoneStep(
    val stepIndex: Int,
    val title: String,
    val dateLabel: String,
    val isCompleted: Boolean
)

/**
 * Geleceğe Hazır M&A Stubs
 */
data class CorporateEventFutureStubs(
    val isAiDealPredictionActive: Boolean = false,
    val isAutomaticSynergyCalculationActive: Boolean = true,
    val isMaRiskEngineActive: Boolean = true,
    val isCorporateNetworkGraphActive: Boolean = false
)
