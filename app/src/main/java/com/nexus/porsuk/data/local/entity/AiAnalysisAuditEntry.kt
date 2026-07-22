package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for AI Analysis Accuracy & Backtest Audit System.
 * Records every AI stock analysis date, initial price, prediction, analysis category (TECHNICAL, NEWS, FUNDAMENTAL), and confidence score.
 * Automatically tracks and evaluates actual price returns at 7-day, 30-day, and 90-day checkpoints.
 */
@Entity(tableName = "ai_analysis_audit_entries")
data class AiAnalysisAuditEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val analysisDate: Long = System.currentTimeMillis(),
    val initialPrice: Double,
    val predictionType: String, // BULLISH, BEARISH, NEUTRAL
    val analysisCategory: String = "TECHNICAL", // TECHNICAL, NEWS, FUNDAMENTAL
    val confidenceScore: Int,
    val analysisSummary: String,
    
    // 7-Day Checkpoint
    val priceDay7: Double? = null,
    val returnPctDay7: Double? = null,
    val isSuccessDay7: Boolean? = null,
    
    // 30-Day Checkpoint
    val priceDay30: Double? = null,
    val returnPctDay30: Double? = null,
    val isSuccessDay30: Boolean? = null,
    
    // 90-Day Checkpoint
    val priceDay90: Double? = null,
    val returnPctDay90: Double? = null,
    val isSuccessDay90: Boolean? = null,
    
    val auditStatus: String = "PENDING", // PENDING, CHECKED_7D, CHECKED_30D, COMPLETED
    val failureReason: String? = null
)
