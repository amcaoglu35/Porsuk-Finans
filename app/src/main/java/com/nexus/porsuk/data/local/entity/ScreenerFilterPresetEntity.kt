package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Screener Pro Ultimate — Kaydedilmiş Filtre Tablosu (ScreenerFilterPresetEntity)
 */
@Entity(
    tableName = "engine_screener_filter_presets",
    indices = [Index(value = ["preset_name"])]
)
data class ScreenerFilterPresetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preset_id")
    val presetId: Long = 0,

    @ColumnInfo(name = "preset_name")
    val presetName: String,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "max_pe_ratio")
    val maxPeRatio: Double? = null,

    @ColumnInfo(name = "min_roe_pct")
    val minRoePct: Double? = null,

    @ColumnInfo(name = "min_master_score")
    val minMasterScore: Int? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
