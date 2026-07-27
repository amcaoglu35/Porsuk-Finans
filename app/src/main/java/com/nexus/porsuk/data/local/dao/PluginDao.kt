package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.PluginConfigEntity
import com.nexus.porsuk.data.local.entity.PluginHealthEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PluginDao {
    @Query("SELECT * FROM engine_plugin_config WHERE pluginId = :pluginId")
    suspend fun getConfig(pluginId: String): PluginConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: PluginConfigEntity)

    @Query("SELECT * FROM engine_plugin_health WHERE pluginId = :pluginId")
    fun getHealthFlow(pluginId: String): Flow<PluginHealthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHealth(health: PluginHealthEntity)
}
