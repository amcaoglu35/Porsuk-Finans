package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.AiAnalysisCacheEntity
import com.nexus.porsuk.data.local.entity.LocalAiModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiEngineDao {
    @Query("SELECT * FROM engine_local_models")
    fun getAllModelsFlow(): Flow<List<LocalAiModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: LocalAiModelEntity)

    @Query("UPDATE engine_local_models SET isDownloaded = :downloaded WHERE modelId = :modelId")
    suspend fun updateDownloadStatus(modelId: String, downloaded: Boolean)

    @Query("DELETE FROM engine_local_models WHERE modelId = :modelId")
    suspend fun deleteModel(modelId: String)

    @Query("SELECT * FROM engine_ai_cache WHERE cacheKey = :key")
    suspend fun getCachedAnalysis(key: String): AiAnalysisCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(entry: AiAnalysisCacheEntity)
}
