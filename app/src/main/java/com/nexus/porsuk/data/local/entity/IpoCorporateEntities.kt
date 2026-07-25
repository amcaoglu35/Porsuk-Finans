package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nexus.porsuk.domain.model.CorporateActionType
import com.nexus.porsuk.domain.model.IpoStatus

@Entity(
    tableName = "ipo_intelligence",
    indices = [Index(value = ["symbol"], unique = true), Index(value = ["status"])]
)
data class IpoIntelligenceEntity(
    @PrimaryKey val symbol: String,
    val companyName: String,
    val status: IpoStatus,
    val market: String,
    val sector: String,
    val offerPrice: Double?,
    val finalPrice: Double?,
    val lotQuantity: Long?,
    val issueSize: Double?,
    val distributionMethod: String,
    val startDate: Long?,
    val endDate: Long?,
    val listingDate: Long?,
    val prospectusUrl: String?,
    val leadManager: String?,
    val description: String?,
    val isShariaCompliant: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "corporate_actions",
    indices = [Index(value = ["action_id"], unique = true), Index(value = ["symbol"]), Index(value = ["type"])]
)
data class CorporateActionEntity(
    @PrimaryKey @ColumnInfo(name = "action_id") val actionId: String,
    val symbol: String,
    val type: CorporateActionType,
    @ColumnInfo(name = "announcement_date") val announcementDate: Long,
    @ColumnInfo(name = "effective_date") val effectiveDate: Long,
    val ratio: Double?,
    val amount: Double?,
    val currency: String,
    val description: String?,
    val status: String
)

@Entity(
    tableName = "dividend_history_pro",
    indices = [Index(value = ["symbol"])]
)
data class DividendHistoryProEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val exDate: Long,
    val paymentDate: Long,
    val amount: Double,
    val currency: String
)
