package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val currencyPair: String, // e.g. "USDTRY", "EURTRY", "USD/EUR"
    val base: String,
    val target: String,
    val rate: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
