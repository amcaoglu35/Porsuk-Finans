package com.nexus.porsuk.domain.model

/**
 * 11 Eklenti Kategorisi (PluginCategory)
 */
enum class PluginCategory(val displayName: String, val iconEmoji: String) {
    MARKET_DATA_PROVIDER("Piyasa Veri Sağlayıcı", "📊"),
    BROKER_PROVIDER("Aracı Kurum Entegrasyonu", "🏦"),
    AI_PROVIDER("AI & NLP Sağlayıcı", "🤖"),
    NEWS_PROVIDER("Haber & Duyuru Kaynağı", "📰"),
    SCANNER_PLUGIN("Piyasa Taraması (Scanner)", "🔍"),
    TECHNICAL_INDICATOR("Teknik Gösterge (Indicator)", "📈"),
    STRATEGY_PLUGIN("Ticaret Stratejisi (Strategy)", "💡"),
    CHART_PLUGIN("Özel Grafik Görünümü", "📉"),
    NOTIFICATION_PLUGIN("Bildirim Servisi", "🔔"),
    THEME_PLUGIN("Özel Tema & Görünüm", "🎨"),
    WIDGET_PLUGIN("Ana Ekran & Dashboard Widget", "🧩");
}

/**
 * Eklenti Çalışma Durumu (PluginState)
 */
enum class PluginState(val displayName: String) {
    INSTALLED("Yüklendi (Pasif)"),
    ENABLED("Etkin (Çalışıyor 🟢)"),
    DISABLED("Devre Dışı ⚪"),
    ERROR("Hata Alındı 🔴");
}

/**
 * Eklenti İzin Türleri (PluginPermission)
 */
enum class PluginPermission(val permissionName: String, val description: String) {
    MARKET_DATA_ACCESS("Market Data Access", "Canlı ve geçmiş borsa verilerine erişim izni."),
    PORTFOLIO_ACCESS("Portfolio Access", "Portföy bakiyesi ve varlık okuma izni."),
    BROKER_ACCESS("Broker Access", "Aracı kurum hesap işlem emri gönderme izni."),
    AI_ACCESS("AI Access", "Orakul AI motoruna istem ve veri gönderme izni."),
    NOTIFICATION_ACCESS("Notification Access", "Kullanıcıya sistem bildirimi gönderme izni."),
    NETWORK_ACCESS("Network Access", "Harici REST/WebSocket sunucularına bağlanma izni."),
    LOCAL_STORAGE_ACCESS("Local Storage Access", "Önbelleğe veri kaydetme ve okuma izni.");
}

/**
 * Eklenti Genişletme Noktası (ExtensionPoint)
 */
enum class ExtensionPoint(val pointName: String) {
    CHARTS("Gelişmiş Grafik Ekranı"),
    PORTFOLIO("Portföy & Varlık Detay"),
    MARKETS("Canlı Piyasalar & Borsa"),
    COMPANY_DETAIL("Şirket & Hisse Detay"),
    WATCHLIST("İzleme Listesi"),
    AI_WORKSPACE("Orakul AI Laboratuvarı"),
    DASHBOARD("Ana Dashboard");
}

/**
 * Eklenti Manifest Dosyası (PluginManifest)
 */
data class PluginManifest(
    val pluginId: String,
    val pluginName: String,
    val version: String = "1.0.0",
    val developerName: String = "Porsuk Ecosystem Dev",
    val category: PluginCategory = PluginCategory.TECHNICAL_INDICATOR,
    val extensionPoint: ExtensionPoint = ExtensionPoint.CHARTS,
    val minSdkVersion: String = "v3.9.0",
    val requiredPermissions: List<PluginPermission> = listOf(PluginPermission.MARKET_DATA_ACCESS),
    val signatureHash: String = "SHA256:8F92A1...PORSUK_SIGNED"
)

/**
 * Sandbox İzole Çalışma Metrikleri (PluginSandboxMetrics)
 */
data class PluginSandboxMetrics(
    val executionTimeMs: Long = 12L,
    val memoryFootprintMb: Double = 1.4,
    val activeThreadCount: Int = 1,
    val totalInvocations: Long = 1420L,
    val isCrashIsolated: Boolean = true
)

/**
 * Yüklü/Kullanılabilir Eklenti Öğesi (PluginItem)
 */
data class PluginItem(
    val manifest: PluginManifest,
    val state: PluginState = PluginState.ENABLED,
    val sandboxMetrics: PluginSandboxMetrics = PluginSandboxMetrics(),
    val isBuiltIn: Boolean = false,
    val installTimestamp: Long = System.currentTimeMillis()
)

/**
 * Geleceğe Hazır Eklenti Pazaryeri & Lisanslama Stub Modeli
 */
data class PluginMarketplaceStub(
    val isMarketplaceOnline: Boolean = true,
    val totalAvailablePluginsCount: Int = 48,
    val paidPluginsCount: Int = 12,
    val isCertificationProgramActive: Boolean = true,
    val enterpriseCloudPluginsCount: Int = 6
)
