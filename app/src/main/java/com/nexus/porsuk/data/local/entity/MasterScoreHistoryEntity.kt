package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Master Score Engine — Skor Geçmişi Tablosu (MasterScoreHistoryEntity)
 */
@Entity(
    tableName = "engine_master_score_history",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["timestamp"])
    ]
)
data class MasterScoreHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "score_id")
    val scoreId: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "master_score")
    val masterScore: Int,

    @ColumnInfo(name = "score_level")
    val scoreLevel: String, // EXCEPTIONAL, EXCELLENT, STRONG, NEUTRAL, WEAK, RISKY, CRITICAL

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
