package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing private user investment decision notes (Decision Journal).
 * Records buy/sell reasoning, target horizon, investment style, and AI evaluation feedback.
 */
@Entity(tableName = "decision_journal_entries")
data class DecisionJournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val actionType: String = "BUY", // BUY, SELL, HOLD
    val buyPrice: Double,
    val quantity: Double,
    val reason: String, // E.g., "Uzun vadeli değer yatırımı, F/K ucuz"
    val targetHorizon: String, // E.g., "3 Yıl / 150 TL"
    val investmentStyle: String, // E.g., "Değer Yatırımı", "Büyüme", "Temettü", "Momentum"
    val sector: String = "Genel",
    val createdAt: Long = System.currentTimeMillis(),
    val isEvaluated: Boolean = false,
    val aiEvaluationText: String? = null
)
