package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Backtesting Engine — Geçmiş Test Kaydetme Tablosu (BacktestReportEntity)
 */
@Entity(
    tableName = "engine_backtest_reports",
    indices = [Index(value = ["strategy_name"])]
)
data class BacktestReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "report_id")
    val reportId: String,

    @ColumnInfo(name = "strategy_name")
    val strategyName: String,

    @ColumnInfo(name = "total_return_pct")
    val totalReturnPct: Double,

    @ColumnInfo(name = "sharpe_ratio")
    val sharpeRatio: Double,

    @ColumnInfo(name = "max_drawdown_pct")
    val maxDrawdownPct: Double,

    @ColumnInfo(name = "win_rate_pct")
    val winRatePct: Double,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
