package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * TEFAS Yatırım Fonu Kurumsal Room Veri Varlığı (TefasFundEntity)
 *
 * TEFAS platformundaki fonların 13 temel alanını saklar ve indekslerle sorgu performansını artırır.
 */
@Entity(
    tableName = "tefas_funds",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["manager"]),
        Index(value = ["umbrella_fund"]),
        Index(value = ["is_active"])
    ]
)
data class TefasFundEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "code")
    val code: String, // Örn: TCD, AFT, MAC

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "founder")
    val founder: String = "",

    @ColumnInfo(name = "manager")
    val manager: String = "", // Portföy Yönetim Şirketi

    @ColumnInfo(name = "umbrella_fund")
    val umbrellaFund: String = "", // Şemsiye Fon Türü

    @ColumnInfo(name = "fund_type")
    val fundType: String = "",

    @ColumnInfo(name = "risk_level")
    val riskLevel: Int = 1, // 1 (Düşük) ile 7 (Yüksek) arası

    @ColumnInfo(name = "currency")
    val currency: String = "TRY",

    @ColumnInfo(name = "price")
    val price: Double = 0.0,

    @ColumnInfo(name = "total_assets")
    val totalAssets: Double = 0.0, // Toplam Portföy Büyüklüğü

    @ColumnInfo(name = "investor_count")
    val investorCount: Long = 0, // Katılımcı Sayısı

    @ColumnInfo(name = "management_fee")
    val managementFee: Double = 0.0, // Yıllık Yönetim Ücreti %

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
