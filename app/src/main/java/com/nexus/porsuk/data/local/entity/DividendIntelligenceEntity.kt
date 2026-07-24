package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Dividend Intelligence Center — Temettü Takip Tablosu (DividendIntelligenceEntity)
 */
@Entity(
    tableName = "engine_dividend_intelligence_watchlist",
    indices = [Index(value = ["symbol"])]
)
data class DividendIntelligenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "dividend_yield_pct")
    val dividendYieldPct: Double,

    @ColumnInfo(name = "annual_dividend_usd")
    val annualDividendUsd: Double,

    @ColumnInfo(name = "safety_score")
    val safetyScore: Int,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = true,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
