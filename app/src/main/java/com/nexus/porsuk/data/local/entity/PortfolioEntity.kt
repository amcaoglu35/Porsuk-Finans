package com.nexus.porsuk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Data Center — Kullanıcı Portföy Ana Tablosu
 */
@Entity(tableName = "portfolios")
data class PortfolioEntity(
    @PrimaryKey
    val portfolioId: String,
    val name: String,
    val description: String = "",
    val totalValuation: Double = 0.0,
    val currency: String = "TRY",
    val createdAtMs: Long = System.currentTimeMillis()
)

/**
 * Porsuk Data Center — Portföye Bağlı Varlık/Hisse/Fon Kalemi
 */
@Entity(
    tableName = "portfolio_items",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEntity::class,
            parentColumns = ["portfolioId"],
            childColumns = ["portfolioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioId")]
)
data class PortfolioItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portfolioId: String,
    val symbol: String, // Hisse veya Fon Kodu
    val assetType: String, // STOCK, FUND, CRYPTO, FX
    val quantity: Double, // Adet/Miktar
    val averageCost: Double, // Ortalama Maliyet
    val currentPrice: Double = 0.0,
    val lastUpdatedMs: Long = System.currentTimeMillis()
)
