package com.nexus.porsuk.domain.model

/**
 * Makroekonomik Veri Sağlayıcı Türü (MacroProviderType)
 */
enum class MacroProviderType(val displayName: String, val iconEmoji: String) {
    FRED_US("FRED Federal Reserve Economic Data", "🇺🇸"),
    ECB_EUROPE("ECB European Central Bank Data", "🇪🇺"),
    TCMB_TURKEY("TCMB EVDS Elektronik Veri Dağıtım", "🇹🇷"),
    IMF_GLOBAL("IMF International Monetary Fund", "🌐"),
    OECD_WORLD("OECD Economic Outlook", "🏛️"),
    WORLD_BANK("World Bank Open Data", "🌍"),
    BIS_GLOBAL("BIS Bank for International Settlements", "🏦");
}

/**
 * Merkez Bankası Türü (CentralBankType)
 */
enum class CentralBankType(val displayName: String, val countryCode: String, val iconEmoji: String) {
    FED("Federal Reserve (FED)", "US", "🇺🇸"),
    ECB("European Central Bank (ECB)", "EU", "🇪🇺"),
    TCMB("Türkiye Cumhuriyet Merkez Bankası (TCMB)", "TR", "🇹🇷"),
    BOE("Bank of England (BOE)", "UK", "🇬🇧"),
    BOJ("Bank of Japan (BOJ)", "JP", "🇯🇵"),
    SNB("Swiss National Bank (SNB)", "CH", "🇨🇭"),
    PBOC("People's Bank of China (PBOC)", "CN", "🇨🇳");
}

/**
 * Makro Gösterge Kategorisi (MacroIndicatorCategory)
 */
enum class MacroIndicatorCategory(val displayName: String) {
    INFLATION("Enflasyon (CPI / PPI)"),
    GROWTH("Büyüme & GSYH (GDP)"),
    PMI("Satın Alma Yöneticileri Endeksi (PMI)"),
    EMPLOYMENT("İstihdam & İşsizlik (NFP)"),
    INTEREST_RATE("Politika Faizi & Likidite"),
    PRODUCTION("Sanayi Üretimi & Kapasite");
}

/**
 * Makroekonomik Gösterge (EconomicIndicator)
 */
data class EconomicIndicator(
    val indicatorId: String = "ind_${System.currentTimeMillis()}",
    val name: String = "TÜFE Tüketici Fiyat Endeksi (Yıllık CPI)",
    val countryCode: String = "TR",
    val category: MacroIndicatorCategory = MacroIndicatorCategory.INFLATION,
    val provider: MacroProviderType = MacroProviderType.TCMB_TURKEY,
    val currentValue: Double = 71.60,
    val previousValue: Double = 75.45,
    val forecastValue: Double = 70.20,
    val unit: String = "%",
    val releaseDate: String = "24 Temmuz 2026",
    val isMarketImpactHigh: Boolean = true
)

/**
 * Merkez Bankası Politikası (CentralBankPolicy)
 */
data class CentralBankPolicy(
    val bankType: CentralBankType = CentralBankType.TCMB,
    val policyRatePct: Double = 50.0,
    val lastDecisionDate: String = "20 Haziran 2026",
    val nextMeetingDate: String = "23 Temmuz 2026",
    val statementSummary: String = "Sıkı parasal duruş enflasyonda belirgin düşüş sağlanana kadar sürdürülecektir.",
    val balanceSheetTotalBillions: Double = 145.8
)

/**
 * Devlet Tahvili Verimi (BondYieldItem)
 */
data class BondYieldItem(
    val bondSymbol: String = "US10Y",
    val countryName: String = "ABD 10 Yıllık Tahvil",
    val maturityYears: Int = 10,
    val yieldPct: Double = 4.28,
    val changePct: Double = -0.42
)

/**
 * Emtia ve Döviz Kuru (CommodityItem)
 */
data class CommodityItem(
    val commoditySymbol: String = "XAU-USD",
    val name: String = "Ons Altın (Gold)",
    val category: String = "Değerli Metal",
    val priceUSD: Double = 2415.50,
    val changePct: Double = 1.15
)

/**
 * Makro Gösterge Paneli Sekmesi (MacroDashboardTab)
 */
enum class MacroDashboardTab(val displayName: String, val iconEmoji: String) {
    GLOBAL_HEATMAP("Küresel Isı Haritası", "🌍"),
    INFLATION("Enflasyon & ÜFE", "📈"),
    GROWTH("GSYH & PMI Büyüme", "📊"),
    INTEREST_RATES("Merkez Bankaları & Faiz", "🏦"),
    BONDS("Tahvil & Getiri Eğrisi", "📜"),
    FX_COMMODITIES("Döviz & Emtia", "💎");
}

/**
 * AI Makro Bakış ve Senaryo Analizi (MacroAiOutlook)
 */
data class MacroAiOutlook(
    val recessionProbabilityPct: Double = 18.5,
    val inflationCommentary: String = "Küresel baz etkisiyle yıllık TÜFE tarafında yumuşama devam etmekle birlikte hizmet enflasyonu direnç göstermektedir.",
    val interestRateForecastText: String = "FED'in yılın ikinci yarısında 50 baz puanlık faiz indirimine gitme olasılığı %72'dir.",
    val marketImpactSummary: String = "Gelişmekte olan ülke para birimlerinde değer kazanımı ve tahvil faizlerinde gerileme beklenmektedir."
)

/**
 * Geleceğe Hazır Makro Zeka Stub Modeli (MacroFutureStubs)
 */
data class MacroFutureStubs(
    val isAiRecessionPredictionReady: Boolean = true,
    val isAiInflationForecastActive: Boolean = true,
    val isGlobalLiquidityIndexReady: Boolean = true,
    val isMacroRiskScoreActive: Boolean = true,
    val isSatelliteDataInterfaceSupported: Boolean = false
)
