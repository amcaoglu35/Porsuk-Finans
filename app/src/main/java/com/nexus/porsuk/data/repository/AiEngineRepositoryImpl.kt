package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.AiEngineDao
import com.nexus.porsuk.data.local.entity.LocalAiModelEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.AiEngineRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiEngineRepositoryImpl @Inject constructor(
    private val dao: AiEngineDao,
    private val settingsManager: com.nexus.porsuk.data.local.SettingsManager
) : AiEngineRepository {

    override fun getOperationMode(): Flow<AiOperationMode> = flow {
        // Implementation with DataStore/SettingsManager
        emit(AiOperationMode.HYBRID)
    }

    override suspend fun setOperationMode(mode: AiOperationMode) {
        // Save to DataStore
    }

    override fun getAvailableModels(): Flow<List<LocalAiModel>> {
        return dao.getAllModelsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun downloadModel(modelId: String): Boolean {
        // Mock download logic
        dao.updateDownloadStatus(modelId, true)
        return true
    }

    override suspend fun deleteModel(modelId: String): Boolean {
        dao.updateDownloadStatus(modelId, false)
        return true
    }

    override suspend fun setActiveModel(modelId: String) {
        // Save to settings
    }

    override fun getEngineStatus(): Flow<AiEngineStatus> = flow {
        emit(
            AiEngineStatus(
                mode = AiOperationMode.HYBRID,
                isCloudAvailable = true,
                isLocalReady = true,
                activeModelId = "porsuk_v1_tiny"
            )
        )
    }

    override fun getQualityMetrics(): Flow<AiQualityMetrics?> = flow {
        emit(
            AiQualityMetrics(
                similarityScore = 0.92,
                consistencyRate = 0.88,
                localConfidence = 85
            )
        )
    }

    override suspend fun runLocalInference(prompt: String): String {
        // Real logic would use a locally loaded model (TFLite etc.)
        return "Yerel AI Analizi: Piyasa verileri stabil görünüyor. Portföy risk skoru düşük."
    }

    private fun LocalAiModelEntity.toDomain() = LocalAiModel(
        modelId = modelId,
        name = name,
        version = version,
        sizeMb = sizeMb,
        ramUsageMb = 120.0,
        cpuUsagePct = 15.0,
        isDownloaded = isDownloaded,
        isCurrent = true,
        lastUpdated = lastUpdated
    )
}
