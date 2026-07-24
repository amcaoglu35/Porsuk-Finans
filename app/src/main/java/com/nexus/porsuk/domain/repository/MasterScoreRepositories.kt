package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Merkezi Master Score Deposu Sözleşmesi (MasterScoreRepository)
 */
interface MasterScoreRepository {
    fun getMasterScore(symbol: String): Flow<MasterScoreResult>
}

/**
 * 2. Skor Geçmişi ve Trend Deposu Sözleşmesi (ScoreHistoryRepository)
 */
interface ScoreHistoryRepository {
    fun getScoreHistory(symbol: String): Flow<List<MasterScoreHistoryItem>>
    suspend fun saveScoreHistory(symbol: String, masterScore: Int, level: ScoreLevel)
}

/**
 * 3. Skor Hesaplama ve Ağırlık Yönetimi Sözleşmesi (ScoreCalculationRepository)
 */
interface ScoreCalculationRepository {
    suspend fun calculateScore(symbol: String): MasterScoreResult
    suspend fun updateComponentWeights(weights: Map<ScoreComponentType, Double>)
}
