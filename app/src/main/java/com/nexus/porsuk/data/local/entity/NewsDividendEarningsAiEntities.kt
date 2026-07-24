package com.nexus.porsuk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Porsuk Finans — Finansal Haberler Varlık Tanımı (News Entity)
 */
@Entity(
    tableName = "db_news",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["published_at"])
    ]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String? = null, // Bağlantılı Sembol (Varsa)

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "source")
    val source: String, // Bloomberg, KAP, AA Finans

    @ColumnInfo(name = "published_at")
    val publishedAt: Long,

    @ColumnInfo(name = "url")
    val url: String
)

/**
 * Porsuk Finans — Temettü Takvimi Varlık Tanımı (Dividend Entity)
 */
@Entity(
    tableName = "db_dividends",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["ex_date"])
    ]
)
data class DividendEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "ex_date")
    val exDate: Long, // Hak Kullanım Tarihi

    @ColumnInfo(name = "payment_date")
    val paymentDate: Long, // Ödeme Tarihi

    @ColumnInfo(name = "amount")
    val amount: Double, // Hisse Başı Net Temettü

    @ColumnInfo(name = "currency")
    val currency: String = "TRY"
)

/**
 * Porsuk Finans — Bilanço / Kazanç Raporları (Earnings Entity)
 */
@Entity(
    tableName = "db_earnings",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["report_date"])
    ]
)
data class EarningsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "report_date")
    val reportDate: Long,

    @ColumnInfo(name = "eps_actual")
    val epsActual: Double? = null, // Gerçekleşen Hisse Başı Kar

    @ColumnInfo(name = "eps_estimate")
    val epsEstimate: Double? = null, // Tahmini Hisse Başı Kar

    @ColumnInfo(name = "revenue")
    val revenue: Double? = null, // Toplam Ciro / Hasılat

    @ColumnInfo(name = "quarter")
    val quarter: String // Q1, Q2, Q3, Q4
)

/**
 * Porsuk Finans — Orakul AI Analiz Geçmişi Varlık Tanımı (AIHistory Entity)
 */
@Entity(
    tableName = "db_ai_history",
    indices = [
        Index(value = ["symbol"]),
        Index(value = ["analysis_date"])
    ]
)
data class AIHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "master_score")
    val masterScore: Double, // 0.0 - 100.0 Orakul Master Skoru

    @ColumnInfo(name = "confidence")
    val confidence: Double, // %0 - %100 AI Güven Derecesi

    @ColumnInfo(name = "recommendation")
    val recommendation: String, // GÜÇLÜ AL, AL, NÖTR, SAT, GÜÇLÜ SAT

    @ColumnInfo(name = "analysis_date")
    val analysisDate: Long = System.currentTimeMillis()
)

/**
 * Porsuk Finans — Uygulama Tercih ve Ayarlar Varlık Tanımı (AppSettings Entity)
 */
@Entity(tableName = "db_app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 1, // Tekil ayar kaydı (Singleton ID = 1)

    @ColumnInfo(name = "theme")
    val theme: String = "SYSTEM", // SYSTEM, DARK, LIGHT

    @ColumnInfo(name = "language")
    val language: String = "TR",

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = false
)
