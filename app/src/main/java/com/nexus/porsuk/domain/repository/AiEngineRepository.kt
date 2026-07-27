package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * AI Motoru ve Yerel Modeller Deposu Sözleşmesi (AiEngineRepository)
 */
interface AiEngineRepository {
    fun getOperationMode(): Flow<AiOperationMode>
    suspend fun setOperationMode(mode: AiOperationMode)
    
    fun getAvailableModels(): Flow<List<LocalAiModel>>
    suspend fun downloadModel(modelId: String): Boolean
    suspend fun deleteModel(modelId: String): Boolean
    suspend fun setActiveModel(modelId: String)
    
    fun getEngineStatus(): Flow<AiEngineStatus>
    fun getQualityMetrics(): Flow<AiQualityMetrics?>
    
    suspend fun runLocalInference(prompt: String): String
}
