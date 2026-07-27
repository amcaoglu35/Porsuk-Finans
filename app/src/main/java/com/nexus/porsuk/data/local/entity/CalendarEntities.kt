package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Economic Calendar & Events Engine — Genel Etkinlikler Tablosu (EconomicEventEntity)
 */
@Entity(
    tableName = "engine_economic_events",
    indices = [
        Index(value = ["event_id"], unique = true),
        Index(value = ["country"]),
        Index(value = ["impact_level"]),
        Index(value = ["category"])
    ]
)
data class EconomicEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    val eventId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "category")
    val category: String, // MACRO, ECONOMIC_DATA, EARNINGS, DIVIDEND, STOCK_IPO

    @ColumnInfo(name = "impact_level")
    val impactLevel: String, // HIGH, MEDIUM, LOW

    @ColumnInfo(name = "actual_value")
    val actualValue: String? = null,

    @ColumnInfo(name = "forecast_value")
    val forecastValue: String? = null,

    @ColumnInfo(name = "previous_value")
    val previousValue: String? = null,

    @ColumnInfo(name = "event_time")
    val eventTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "symbol")
    val symbol: String? = null,

    @ColumnInfo(name = "ai_impact_json")
    val aiImpactJson: String? = null
)

/**
 * Şirket Bilanço Açıklama Tablosu (EarningsCalendarEntity)
 */
@Entity(
    tableName = "engine_earnings_calendar",
    indices = [
        Index(value = ["earnings_id"], unique = true),
        Index(value = ["symbol"])
    ]
)
data class EarningsCalendarEntity(
    @PrimaryKey
    @ColumnInfo(name = "earnings_id")
    val earningsId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "report_date")
    val reportDate: String,

    @ColumnInfo(name = "eps_forecast")
    val epsForecast: Double = 0.0,

    @ColumnInfo(name = "eps_actual")
    val epsActual: Double? = null,

    @ColumnInfo(name = "revenue_forecast")
    val revenueForecast: Double = 0.0,

    @ColumnInfo(name = "revenue_actual")
    val revenueActual: Double? = null,

    @ColumnInfo(name = "ai_impact_json")
    val aiImpactJson: String? = null
)

/**
 * Temettü Takvimi Tablosu (DividendCalendarProEntity)
 */
@Entity(
    tableName = "engine_dividend_calendar_pro",
    indices = [
        Index(value = ["dividend_id"], unique = true),
        Index(value = ["symbol"])
    ]
)
data class DividendCalendarProEntity(
    @PrimaryKey
    @ColumnInfo(name = "dividend_id")
    val dividendId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "ex_date")
    val exDate: String,

    @ColumnInfo(name = "payment_date")
    val paymentDate: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "currency")
    val currency: String = "TRY",

    @ColumnInfo(name = "ai_impact_json")
    val aiImpactJson: String? = null
)
