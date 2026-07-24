package com.nexus.porsuk.domain.model

/**
 * Porsuk Economic Calendar & Events Engine — Genel Ekonomik Etkinlik Domain Modeli
 */
data class EconomicEvent(
    val eventId: String,
    val title: String,
    val country: String, // TR, US, EU, UK, JP
    val category: CalendarEventCategory,
    val impactLevel: CalendarImpactLevel,
    val actualValue: String? = null,
    val forecastValue: String? = null,
    val previousValue: String? = null,
    val eventTime: Long = System.currentTimeMillis(),
    val symbol: String? = null
)

/**
 * Şirket Bilanço Açıklama Etkinliği Domain Modeli
 */
data class EarningsEvent(
    val earningsId: String,
    val symbol: String,
    val companyName: String,
    val reportDate: String,
    val epsForecast: Double = 0.0,
    val epsActual: Double? = null,
    val revenueForecast: Double = 0.0,
    val revenueActual: Double? = null
)

/**
 * Temettü Etkinliği Domain Modeli
 */
data class DividendEvent(
    val dividendId: String,
    val symbol: String,
    val companyName: String,
    val exDate: String,
    val paymentDate: String,
    val amount: Double,
    val currency: String = "TRY"
)

/**
 * Geleceğe Hazır AI Orakul Etkinlik Analiz & Etki Skoru Stub Modeli
 */
data class AiCalendarImpactStub(
    val eventId: String,
    val aiImpactScore: Int = 9, // 1 - 10 Orakul Etki Skoru
    val predictedMarketVolatility: String = "Yüksek Volatilite Beklentisi",
    val recommendedStrategy: String = "FED faiz kararı öncesi pozisyon koruma stratejisi"
)
