package com.nexus.porsuk.domain.model

/**
 * Porsuk Finans — Piyasa Durumu Domain Modeli (Market Status)
 */
data class MarketStatus(
    val exchange: ExchangeType,
    val isOpen: Boolean,
    val session: String, // REGULAR, PRE_MARKET, AFTER_HOURS, CLOSED
    val timezone: String = "UTC",
    val holiday: String? = null,
    val openTime: String = "10:00",
    val closeTime: String = "18:00"
)
