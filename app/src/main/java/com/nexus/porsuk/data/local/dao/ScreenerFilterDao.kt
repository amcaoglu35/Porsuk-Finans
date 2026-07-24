package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.ScreenerFilterPresetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Screener Pro Ultimate — Room DAO Sorguları
 */
@Dao
interface ScreenerFilterDao {

    @Query("SELECT * FROM engine_screener_filter_presets ORDER BY created_at DESC")
    fun getAllSavedPresets(): Flow<List<ScreenerFilterPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: ScreenerFilterPresetEntity)

    @Query("DELETE FROM engine_screener_filter_presets WHERE preset_id = :presetId")
    suspend fun deletePreset(presetId: Long)
}
