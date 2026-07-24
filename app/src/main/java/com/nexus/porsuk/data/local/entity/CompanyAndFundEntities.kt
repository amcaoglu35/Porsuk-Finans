package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Finans — Şirket / Hisse Senedi Varlık Tanımı (Company Entity)
 *
 * Borsalarda işlem gören şirketlerin temel künye ve piyasa değerlerini saklar.
 */
@Entity(
    tableName = "db_companies",
    indices = [
        Index(value = ["symbol"], unique = true),
        Index(value = ["isin"]),
        Index(value = ["exchange"]),
        Index(value = ["sector"])
    ]
)
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String, // Örn: THYAO, GARAN, AAPL

    @ColumnInfo(name = "isin")
    val isin: String? = null, // ISIN Kodu (Örn: TRATHYAO91M5)

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "exchange")
    val exchange: String, // BIST, NASDAQ, NYSE

    @ColumnInfo(name = "country")
    val country: String = "TR",

    @ColumnInfo(name = "sector")
    val sector: String, // Ulaşım, Bankacılık, Teknoloji

    @ColumnInfo(name = "industry")
    val industry: String, // Havayolu Taşımacılığı, Özel Banka

    @ColumnInfo(name = "currency")
    val currency: String = "TRY",

    @ColumnInfo(name = "market_cap")
    val marketCap: Double = 0.0, // Piyasa Değeri

    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @ColumnInfo(name = "website")
    val website: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Porsuk Finans — TEFAS Yatırım Fonu Varlık Tanımı (Fund Entity)
 */
@Entity(
    tableName = "db_funds",
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["category"]),
        Index(value = ["manager"])
    ]
)
data class FundEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "code")
    val code: String, // Örn: TCD, AFT, MAC

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "manager")
    val manager: String, // Portföy Yönetim Şirketi

    @ColumnInfo(name = "category")
    val category: String, // Hisse Senedi Şemsiye Fonu, Borçlanma Araçları

    @ColumnInfo(name = "risk_level")
    val riskLevel: Int = 1, // 1 (Düşük) ile 7 (Yüksek) arası risk skoru

    @ColumnInfo(name = "currency")
    val currency: String = "TRY",

    @ColumnInfo(name = "price")
    val price: Double = 0.0,

    @ColumnInfo(name = "total_assets")
    val totalAssets: Double = 0.0, // Toplam Fon Büyüklüğü

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
