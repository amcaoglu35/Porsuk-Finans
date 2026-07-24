package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Financial Calculators Center — Hesaplama Geçmişi Tablosu (CalculationHistoryEntity)
 */
@Entity(
    tableName = "engine_calculator_history",
    indices = [Index(value = ["calculator_name"])]
)
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    val historyId: Long = 0,

    @ColumnInfo(name = "calculator_name")
    val calculatorName: String,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "result_text")
    val resultText: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
