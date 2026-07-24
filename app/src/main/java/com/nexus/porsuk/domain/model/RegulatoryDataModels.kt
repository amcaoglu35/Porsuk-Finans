package com.nexus.porsuk.domain.model

/**
 * Düzenleyici Veri Sağlayıcı Türü (FilingProviderType)
 */
enum class FilingProviderType(val displayName: String, val iconEmoji: String) {
    KAP_TURKEY("KAP Kamuoyunu Aydınlatma Platformu", "🇹🇷"),
    SEC_EDGAR_US("SEC EDGAR (USA)", "🇺🇸"),
    EURONEXT_EU("Euronext Filings (EU)", "🇪🇺"),
    COMPANIES_HOUSE_UK("Companies House (UK)", "🇬🇧");
}

/**
 * Belge ve Bildirim Türü (FilingDocumentType)
 */
enum class FilingDocumentType(val displayName: String) {
    FINANCIAL_STATEMENT("Finansal Tablolar & Bilanço"),
    ANNUAL_REPORT("Yıllık Faaliyet Raporu (10-K / Annual)"),
    QUARTERLY_REPORT("Ara Dönem Faaliyet Raporu (10-Q)"),
    EARNINGS_RELEASE("Çeyreklik Finansal Sonuç Duyurusu"),
    MATERIAL_EVENT_DISCLOSURE("Özel Durum Açıklaması (KAP/8-K)"),
    DIVIDEND_ANNOUNCEMENT("Temettü Dağıtım Kararı"),
    GENERAL_ASSEMBLY_NOTICE("Genel Kurul Çağrısı & Kararları"),
    SHARE_BUYBACK("Geri Alınan Paylar Bildirimi"),
    CAPITAL_INCREASE("Sermaye Artırımı & Bedelsiz"),
    INSIDER_TRANSACTION("Pay Alım Satım Bildirimi (Insider)"),
    SUSTAINABILITY_REPORT("Sürdürülebilirlik Raporu"),
    ESG_REPORT("ESG & Çevre Uyum Raporu");
}

/**
 * Sınıflandırma Kategorisi (FilingCategory)
 */
enum class FilingCategory(val displayName: String, val iconEmoji: String) {
    EARNINGS("Finansallar & Bilanço", "💰"),
    DIVIDEND("Temettü & Sermaye", "🎁"),
    CORPORATE_ACTION("Kurumsal Eylemler", "🏢"),
    MANAGEMENT_CHANGE("Yönetim Değişiklikleri", "👔"),
    SHAREHOLDER_UPDATE("Ortaklık & Pay Satışı", "🤝"),
    REGULATORY_FILING("Resmi Bildirimler", "📜"),
    RISK_DISCLOSURE("Risk Açıklamaları", "⚠️"),
    LITIGATION("Hukuki & Dava Süreçleri", "⚖️"),
    MERGERS_ACQUISITIONS("Birleşme & Satın Alım (M&A)", "🤝"),
    OTHER("Diğer Duyurular", "📌");
}

/**
 * Resmi Bildirim Övesi (RegulatoryFiling)
 */
data class RegulatoryFiling(
    val filingId: String = "filing_${System.currentTimeMillis()}",
    val companySymbol: String,
    val companyName: String,
    val provider: FilingProviderType = FilingProviderType.KAP_TURKEY,
    val documentType: FilingDocumentType = FilingDocumentType.MATERIAL_EVENT_DISCLOSURE,
    val category: FilingCategory = FilingCategory.EARNINGS,
    val title: String,
    val summaryText: String = "Özel durum açıklaması detayı KAP sistemindedir.",
    val filingDate: String = "24 Temmuz 2026",
    val pdfUrl: String = "https://kap.org.tr/filing/sample.pdf",
    val isImportant: Boolean = true,
    val isDownloaded: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Şirket Zaman Çizelgesi Etkinliği (CompanyTimelineEvent)
 */
data class CompanyTimelineEvent(
    val eventId: String = "event_${System.currentTimeMillis()}",
    val companySymbol: String,
    val eventDate: String = "24 Temmuz 2026",
    val title: String,
    val category: FilingCategory = FilingCategory.EARNINGS,
    val description: String = "Şirket kurumsal zaman çizelgesi duyurusu."
)

/**
 * AI Bildirim ve Bilanço Özet Analizi (FilingAiSummary)
 */
data class FilingAiSummary(
    val filingId: String,
    val executiveSummary: String = "Şirket 2026 yılı 2. çeyreğinde net kârını geçen yılın aynı dönemine göre %42 artırarak 18.5 Milyar TL seviyesine çıkarmıştır.",
    val keyChangesList: List<String> = listOf(
        "Net satış gelirleri %38 artış kaydetti.",
        "FAVÖK marjı 1.2 puan iyileşerek %24.5 oldu.",
        "Özsermaye kârlılığı (ROE) %38.5 seviyesine yükseldi."
    ),
    val riskHighlightsList: List<String> = listOf(
        "Kur dalgalanmalarına bağlı finansal gider artışı riski.",
        "Havacılık yakıt maliyetlerindeki küresel oynaklık."
    ),
    val opportunityHighlightsList: List<String> = listOf(
        "Yaz sezonunda yüksek doluluk oranı ve filo genişlemesi.",
        "Yeni uluslararası uçuş hatlarının kârlılığa katkısı."
    ),
    val aiComparisonText: String = "Önceki çeyreğe kıyasla operasyonel marjlar korunmuş ve borçluluk oranı düşmüştür."
)

/**
 * Geleceğe Hazır Regulatory Intelligence Stub Modeli (RegulatoryFutureStubs)
 */
data class RegulatoryFutureStubs(
    val isOcrSupportReady: Boolean = true,
    val isMultiLanguageTranslationActive: Boolean = true,
    val isFilingSentimentAnalysisReady: Boolean = true,
    val isAutomaticRatioExtractionReady: Boolean = true,
    val isAutomaticDcfPreparationActive: Boolean = false
)
