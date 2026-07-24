package com.nexus.porsuk.data.local.entity

import androidx.room.*

/**
 * Porsuk Watchlist Pro — Takip Listesi Grubu Tablosu
 */
@Entity(
    tableName = "pro_watchlist_groups",
    indices = [
        Index(value = ["group_id"], unique = true),
        Index(value = ["is_favorite"])
    ]
)
data class WatchlistGroupEntity(
    @PrimaryKey
    @ColumnInfo(name = "group_id")
    val groupId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "smart_category")
    val smartCategory: String? = null, // DIVIDEND, GROWTH, TECHNOLOGY vb.

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Porsuk Watchlist Pro — Takip Listesi Kalemi Tablosu
 */
@Entity(
    tableName = "pro_watchlist_items",
    foreignKeys = [
        ForeignKey(
            entity = WatchlistGroupEntity::class,
            parentColumns = ["group_id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["group_id"]),
        Index(value = ["symbol"])
    ]
)
data class WatchlistItemProEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    val itemId: Long = 0,

    @ColumnInfo(name = "group_id")
    val groupId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "tags")
    val tags: String = "", // Virgülle ayrılmış etiketler

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Porsuk Watchlist Pro — Geleceğe Hazır 7 Bildirim / Alarm Tablosu
 */
@Entity(
    tableName = "pro_watchlist_alerts",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["is_enabled"])
    ]
)
data class WatchlistAlertStubEntity(
    @PrimaryKey
    @ColumnInfo(name = "alert_id")
    val alertId: String,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "alert_type")
    val alertType: String, // PRICE, VOLUME, NEWS, DIVIDEND, EARNINGS, AI_SCORE, RISK

    @ColumnInfo(name = "target_value")
    val targetValue: Double = 0.0,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
