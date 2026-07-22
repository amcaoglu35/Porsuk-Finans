package com.nexus.porsuk.data.local

/**
 * Release Management & Semantic Version Tracking for Porsuk Finans.
 * Helps isolate features, track bug origins, and inspect changelogs per APK build.
 */
data class AppVersionRelease(
    val versionName: String,
    val versionCode: Int,
    val releaseTitle: String,
    val features: List<String>
)

object AppReleaseConfig {

    const val CURRENT_VERSION_NAME = "v0.4.0"
    const val CURRENT_VERSION_CODE = 4

    val RELEASE_HISTORY = listOf(
        AppVersionRelease(
            versionName = "v0.4.0",
            versionCode = 4,
            releaseTitle = "Prediction & Learning Audit Engine Release",
            features = listOf(
                "AI Prediction Engine (Olasılık Tahmin Motoru - Fiyat Hedefsiz)",
                "AI Accuracy & Backtest Audit Engine (7, 30 ve 90 Günlük Otomatik Denetim)",
                "Kategori Başarı Kırılımı (Teknik %81, Haber %74, Bilanço %69)",
                "Akıllı Spam Korumalı AI Bildirim Motoru",
                "Yatırım Karar Günlüğü (Decision Journal)",
                "6 Başlıklı Katı Şablonlu Yapılandırılmış JSON Analizleri"
            )
        ),
        AppVersionRelease(
            versionName = "v0.3.0",
            versionCode = 3,
            releaseTitle = "AI Core & Doctor Release",
            features = listOf(
                "Merkezi GeminiService AI Core Mimarisi",
                "Bellek İçi 5 Dakikalık AI Önbellek (AiCacheManager) Sistemi",
                "AI Portfolio Doctor Klinik Analiz Motoru (Sağlık Skoru 0-100)",
                "Lokal Karar Motoru (DecisionEngine - 0 Token Maliyeti)",
                "Market Intelligence Engine (12 Küresel Makro Varlık)"
            )
        )
    )
}
