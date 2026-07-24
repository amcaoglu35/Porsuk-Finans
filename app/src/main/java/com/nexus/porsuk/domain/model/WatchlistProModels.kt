package com.nexus.porsuk.domain.model

/**
 * Takip Listesi Grubu Domain Modeli
 */
data class WatchlistGroup(
    val groupId: String,
    val title: String,
    val isFavorite: Boolean = false,
    val smartCategory: SmartCategory? = null,
    val itemCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Takip Listesi Kalemi Domain Modeli
 */
data class WatchlistItemPro(
    val itemId: Long = 0,
    val groupId: String,
    val symbol: String,
    val name: String,
    val category: AssetCategory,
    val lastPrice: Double = 0.0,
    val dailyChange: Double = 0.0,
    val dailyChangePct: Double = 0.0,
    val volume: Long = 0,
    val notes: String? = null,
    val tags: List<String> = emptyList(),
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır Alarm Altyapısı Stub Modeli
 */
data class WatchlistAlertStub(
    val alertId: String,
    val symbol: String,
    val type: AlertTypeStub,
    val targetValue: Double = 0.0,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır Watchlist Analiz & AI Skor Modeli (Orakul Analysis Stub)
 */
data class WatchlistAnalysisStub(
    val groupId: String,
    val aiScore: Double = 85.0, // 0 - 100 Orakul AI Skoru
    val riskDistribution: String = "Düşük-Orta Risk",
    val topSector: String = "Teknoloji",
    val topCountry: String = "Türkiye (%60), ABD (%40)"
)
