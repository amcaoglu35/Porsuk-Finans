package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.porsuk.domain.model.AiAgentType

/**
 * AI Ajanlarının Başarı Oranlarını Takip Eden Tablo
 */
@Entity(tableName = "engine_agent_performance")
data class AgentPerformanceEntity(
    @PrimaryKey
    val agentType: String, // AiAgentType.name
    val totalPredictions: Int = 0,
    val successfulPredictions: Int = 0,
    val accuracyRate: Double = 0.0,
    val currentWeight: Double = 1.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
