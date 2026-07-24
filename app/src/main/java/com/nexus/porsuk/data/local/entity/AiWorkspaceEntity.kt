package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk AI Lab Platform — Yerel Saklanan Sohbet & Rapor Tablosu (AiWorkspaceEntity)
 */
@Entity(
    tableName = "engine_ai_workspace_records",
    indices = [Index(value = ["workspace_type"])]
)
data class AiWorkspaceEntity(
    @PrimaryKey
    @ColumnInfo(name = "record_id")
    val recordId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "workspace_type")
    val workspaceType: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
