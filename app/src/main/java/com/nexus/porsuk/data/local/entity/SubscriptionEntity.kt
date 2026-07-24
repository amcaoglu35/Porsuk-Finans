package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Porsuk Premium Membership — Abonelik ve İzin Tablosu (SubscriptionEntity)
 */
@Entity(tableName = "engine_subscription_entitlements")
data class SubscriptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String = "local_user",

    @ColumnInfo(name = "plan_name")
    val planName: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long = System.currentTimeMillis() + (365L * 86400000L),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
