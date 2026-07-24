package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Broker Integration Hub — Kaydedilmiş Aracı Kurum Hesabı Tablosu (BrokerAccountEntity)
 */
@Entity(
    tableName = "engine_broker_accounts",
    indices = [Index(value = ["account_id"])]
)
data class BrokerAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "provider_name")
    val providerName: String,

    @ColumnInfo(name = "account_name")
    val accountName: String,

    @ColumnInfo(name = "status_name")
    val statusName: String,

    @ColumnInfo(name = "portfolio_value_usd")
    val portfolioValueUsd: Double,

    @ColumnInfo(name = "last_sync_timestamp")
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
