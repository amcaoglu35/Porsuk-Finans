package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Strategy Builder Pro — Kaydedilmiş Strateji Tablosu (StrategyEntity)
 */
@Entity(
    tableName = "engine_user_strategies",
    indices = [Index(value = ["strategy_name"])]
)
data class StrategyEntity(
    @PrimaryKey
    @ColumnInfo(name = "strategy_id")
    val strategyId: String,

    @ColumnInfo(name = "strategy_name")
    val strategyName: String,

    @ColumnInfo(name = "strategy_type")
    val strategyType: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "stop_loss_pct")
    val stopLossPct: Double,

    @ColumnInfo(name = "take_profit_pct")
    val takeProfitPct: Double,

    @ColumnInfo(name = "version")
    val version: Int = 1,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
