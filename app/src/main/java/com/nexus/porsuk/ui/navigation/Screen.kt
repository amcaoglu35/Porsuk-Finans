package com.nexus.porsuk.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Panel : Screen("panel", "Portföy", Icons.Outlined.SpaceDashboard)
    object Sepetler : Screen("sepetler", "Sepetlerim", Icons.Outlined.AccountBalanceWallet)
    object Sohbet : Screen("ai_lab?initialPrompt={initialPrompt}", "AI Lab", Icons.Outlined.Psychology) {
        fun createRoute(initialPrompt: String) = "ai_lab?initialPrompt=${java.net.URLEncoder.encode(initialPrompt, "UTF-8")}"
    }
    object Placeholder : Screen("placeholder/{title}", "Yakında", Icons.Outlined.Psychology) {
        fun createRoute(title: String) = "placeholder/${java.net.URLEncoder.encode(title, "UTF-8")}"
    }
    object GlobalSearch : Screen("global_search", "Arama", Icons.Outlined.Search)
    object NotificationsCenter : Screen("notifications_center", "Bildirimler", Icons.Outlined.Notifications)
    object PortfolioSimulator : Screen("portfolio_simulator", "Portföy Simülatörü", Icons.Outlined.Psychology)
    object OpportunityCenter : Screen("opportunity_center", "Fırsat Merkezi", Icons.Outlined.Psychology)
    object AlarmCenter : Screen("alarm_center", "Alarm Merkezi", Icons.Outlined.Notifications)
    object AiPerformance : Screen("ai_performance", "AI Performans Merkezi", Icons.Outlined.Psychology)
    object AiStrategyBuilder : Screen("ai_strategy_builder", "AI Strateji Mimarisi", Icons.Outlined.Psychology)
    object GlobalIntelligence : Screen("global_intelligence", "Global Intelligence", Icons.Outlined.Psychology)
    object AiAutomation : Screen("ai_automation", "AI Otomasyon Merkezi", Icons.Outlined.Psychology)
    object Analiz : Screen("analiz", "Analiz", Icons.AutoMirrored.Outlined.TrendingUp)
    object Piyasalar : Screen("markets", "Piyasalar", Icons.AutoMirrored.Outlined.TrendingUp)
    object Ayarlar : Screen("ayarlar", "Ayarlar", Icons.Outlined.Settings)
    object Orakul : Screen("orakul", "Orakul", Icons.Outlined.Psychology)
    object IslemDefteri : Screen("islem_defteri", "İşlemlerim", Icons.AutoMirrored.Outlined.List)
    object Onboarding : Screen("onboarding", "Hoş Geldiniz", Icons.Outlined.Home)
    object KaziConfig : Screen("kazi_config", "Derin Kazı Yapılandırma", Icons.Outlined.Psychology)
    object KaziAnalysis : Screen("kazi_analysis", "Kazı Yapılıyor", Icons.Outlined.Psychology)
    object KaziResult : Screen("kazi_result", "Kazı Sonuçları", Icons.Outlined.Psychology)
    object Calendar : Screen("calendar?initialTab={initialTab}", "Temettü & Halka Arz", Icons.Outlined.DateRange) {
        fun createRoute(initialTab: Int = 0) = "calendar?initialTab=$initialTab"
    }
    object ModelSepetler : Screen("model_sepetler", "Model Sepetler", Icons.Outlined.AccountBalanceWallet)
    object KapRadar : Screen("kap_radar", "KAP Akıllı Para", Icons.AutoMirrored.Outlined.List)

    // Detay ve Yan Rotalar
    object BasketCreate : Screen("basket_create", "Yeni Fon", Icons.AutoMirrored.Outlined.List)
    object BasketDetail : Screen("basket_detail/{basketId}", "Fon Detayı", Icons.AutoMirrored.Outlined.List) {
        fun createRoute(basketId: Int) = "basket_detail/$basketId"
    }
    object CompanyDetail : Screen("company_detail/{symbol}?market={market}", "Şirket Detayı", Icons.Outlined.Home) {
        fun createRoute(symbol: String, market: String = "IST") = "company_detail/$symbol?market=$market"
    }
    object HisseDuello : Screen("hisse_duello?symbol1={symbol1}&symbol2={symbol2}", "Hisse Düellosu", Icons.Outlined.Psychology) {
        fun createRoute(symbol1: String = "THYAO", symbol2: String = "PGSUS") = "hisse_duello?symbol1=$symbol1&symbol2=$symbol2"
    }
    object MacroIntelligence : Screen("macro_intelligence", "Macro Intelligence Platform", Icons.AutoMirrored.Outlined.TrendingUp)
    object PortfolioOptimization : Screen("portfolio_optimization", "Portfolio Optimization Engine", Icons.AutoMirrored.Outlined.TrendingUp)
    object MultiAgentConsensus : Screen("multi_agent?symbol={symbol}", "AI Konsensüs", Icons.Outlined.Psychology) {
        fun createRoute(symbol: String) = "multi_agent?symbol=$symbol"
    }
    object AdvancedChart : Screen("advanced_chart?symbol={symbol}", "Advanced Chart", Icons.AutoMirrored.Outlined.TrendingUp) {
        fun createRoute(symbol: String) = "advanced_chart?symbol=$symbol"
    }
    object ReportingCenter : Screen("reporting_center", "Raporlar", Icons.AutoMirrored.Outlined.List)
    object AiEngineManager : Screen("ai_engine_manager", "AI Engine Manager", Icons.Outlined.Psychology)

    // Feature Module Routes
    object CloudSync : Screen("cloud_sync", "Bulut Senkronizasyon", Icons.Outlined.Settings)
    object PortfolioDoctor : Screen("portfolio_doctor", "Portföy Doktoru", Icons.Outlined.Psychology)
    object Watchlist : Screen("watchlist", "İzleme Listesi", Icons.Outlined.Search)
    object Alerts : Screen("alerts", "Alarmlar", Icons.Outlined.Notifications)
    object News : Screen("news", "Haber Merkezi", Icons.Outlined.Notifications)
    object Backtest : Screen("backtest", "Backtest Engine", Icons.AutoMirrored.Outlined.TrendingUp)
    object Dividend : Screen("dividend", "Temettü Analizi", Icons.Outlined.DateRange)
    object Risk : Screen("risk", "Risk Engine", Icons.Outlined.Psychology)
    object Scanner : Screen("scanner", "Piyasa Tarayıcı", Icons.Outlined.Search)
    object Screener : Screen("screener", "Süper Filtre", Icons.Outlined.Search)
    object Chart : Screen("chart", "Gelişmiş Grafik", Icons.AutoMirrored.Outlined.TrendingUp)
    object Technical : Screen("technical", "Teknik Analiz", Icons.AutoMirrored.Outlined.TrendingUp)
    object MasterScore : Screen("master_score", "Master Skor", Icons.Outlined.Psychology)
    object GlobalMarkets : Screen("global_markets", "Global Piyasalar", Icons.AutoMirrored.Outlined.TrendingUp)
    object AllTools : Screen("all_tools", "Tüm Araçlar", Icons.Outlined.Settings)
    object PortfolioOverview : Screen("portfolio_overview", "Portföy Özeti", Icons.Outlined.AccountBalanceWallet)
    object SecurityCenter : Screen("security_center", "Güvenlik ve Gizlilik", Icons.Outlined.Settings)
    object Upgrade : Screen("upgrade", "Premium Üyelik", Icons.Outlined.Psychology)
}
