package com.nexus.porsuk.domain.model

/**
 * 10 Hesaplayıcı Kategorisi (Calculator Categories)
 */
enum class CalculatorCategory(val displayName: String, val iconEmoji: String) {
    INVESTMENT("Yatırım & Bileşik Faiz", "📈"),
    DCA("Dolar Maliyet Ortalaması (DCA)", "📅"),
    DIVIDEND("Temettü & DRIP", "💰"),
    RETIREMENT("Emeklilik & FIRE", "🏖️"),
    PORTFOLIO("Portföy & Risk", "📊"),
    LOAN("Kredi & Kredi Kartı", "💳"),
    TAX("Vergi & Net Kâr", "🏛️"),
    CURRENCY("Döviz & Enflasyon", "💱"),
    VALUATION("İçsel Değer & Graham", "💎"),
    RISK("Pozisyon Büyüklüğü & Kelly", "🛡️");
}

/**
 * Hesaplama Giriş ve Sonuç Modeli (CalculationResult)
 */
data class CalculationResult(
    val calculatorName: String,
    val category: CalculatorCategory,
    val primaryResultValue: Double,
    val primaryResultText: String,
    val secondaryDetails: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır AI Financial Advisor & FIRE Optimizer Stub Modeli
 */
data class AiFireOptimizerStub(
    val currentAge: Int = 30,
    val targetFireAge: Int = 45,
    val requiredCapitalUsd: Double = 600000.0,
    val aiAdvisorRecommendation: String = "Orakul AI: Yıllık $12.000 tasarruf ve %8 ortalama bileşik getiri ile 45 yaşında Lean FIRE hedefinize ulaşabilirsiniz."
)
