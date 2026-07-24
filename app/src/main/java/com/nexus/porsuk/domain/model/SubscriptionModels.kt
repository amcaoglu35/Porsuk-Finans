package com.nexus.porsuk.domain.model

/**
 * 5 Üyelik Planı (Membership Plans)
 */
enum class MembershipPlan(val displayName: String) {
    FREE("Ücretsiz (Free)"),
    STARTER("Başlangıç (Starter)"),
    PREMIUM("Premium (Avantajlı)"),
    PRO("Profesyonel (Pro)"),
    LIFETIME("Ömür Boyu (Lifetime)");
}

/**
 * Abonelik Periyodu (Subscription Types)
 */
enum class SubscriptionType(val displayName: String, val periodCode: String) {
    WEEKLY("Haftalık", "1W"),
    MONTHLY("Aylık", "1M"),
    QUARTERLY("3 Aylık", "3M"),
    YEARLY("Yıllık", "1Y"),
    LIFETIME("Ömür Boyu", "LIFE");
}

/**
 * 12 Özellik İzni (Feature Permissions)
 */
enum class FeaturePermission(val permissionKey: String, val displayName: String) {
    AI_CHAT("ai_chat", "Orakul AI Sohbet"),
    PORTFOLIO_DOCTOR("portfolio_doctor", "Portfolio Doctor"),
    UNLIMITED_WATCHLIST("unlimited_watchlist", "Sınırsız Takip Listesi"),
    ADVANCED_SCREENER("advanced_screener", "Screener Pro Ultimate"),
    BACKTESTING("backtesting", "Backtesting Engine"),
    STRATEGY_BUILDER("strategy_builder", "Strategy Builder Pro"),
    DIVIDEND_CENTER("dividend_center", "Dividend Intelligence"),
    ADVANCED_CHARTS("advanced_charts", "Professional Chart Center"),
    PRIORITY_SUPPORT("priority_support", "Öncelikli Destek"),
    CLOUD_SYNC("cloud_sync", "Bulut Senkronizasyon"),
    PREMIUM_NEWS("premium_news", "Haber İstihbarat Merkezi"),
    AI_LAB("ai_lab", "AI Lab Workspace");
}

/**
 * Hak ve Lisans Durumu Modeli (EntitlementState)
 */
data class EntitlementState(
    val activePlan: MembershipPlan = MembershipPlan.PREMIUM,
    val isTrialActive: Boolean = false,
    val daysRemainingInPeriod: Int = 280,
    val allowedPermissions: Set<FeaturePermission> = FeaturePermission.entries.toSet()
)
