package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for Proactive AI Insights (AI Insight Engine).
 * Stores cause-and-effect proactive notifications derived from user behavior, portfolio shifts,
 * frequent analyses, and Porsuk Brain memory context.
 */
@Entity(tableName = "ai_proactive_insights")
data class AiInsightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String, // Cause-and-effect concise insight text
    val category: String = "PORTFOLIO_BEHAVIOR", // SECTOR_CONCENTRATION, WATCHLIST_ACTIVITY, NEWS_IMPACT
    val relatedSymbol: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val dedupeKey: String
)
