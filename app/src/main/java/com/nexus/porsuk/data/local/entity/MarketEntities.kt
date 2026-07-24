package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Porsuk Data Center — Piyasa Canlı/Gecikmeli Fiyat Tablosu
 */
@Entity(tableName = "market_quotes")
data class MarketQuoteEntity(
    @PrimaryKey
    val symbol: String,
    val currentPrice: Double,
    val changeAmount: Double,
    val changePct: Double,
    val dayHigh: Double = 0.0,
    val dayLow: Double = 0.0,
    val volume: Long = 0,
    val marketType: String, // BIST, CRYPTO, COMMODITY, FOREX
    val lastUpdatedMs: Long = System.currentTimeMillis()
)
