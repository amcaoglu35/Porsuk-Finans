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
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Panel : Screen("panel", "Portföy", Icons.Outlined.SpaceDashboard)
    object Sepetler : Screen("sepetler", "Sepetlerim", Icons.Outlined.AccountBalanceWallet)
    object Sohbet : Screen("sohbet?initialPrompt={initialPrompt}", "Profesör", Icons.Outlined.Forum) {
        fun createRoute(initialPrompt: String) = "sohbet?initialPrompt=${java.net.URLEncoder.encode(initialPrompt, "UTF-8")}"
    }
    object Analiz : Screen("analiz", "Analiz", Icons.AutoMirrored.Outlined.TrendingUp)
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
    object DevOpsRelease : Screen("devops_release", "DevOps & Release Platform", Icons.Outlined.Settings)
    object PluginManager : Screen("plugin_manager", "Plugin & Extension SDK", Icons.Outlined.Settings)
    object RealtimeStreaming : Screen("realtime_streaming", "Real-Time Streaming Platform", Icons.AutoMirrored.Outlined.TrendingUp)
    object QuantResearch : Screen("quant_research", "Quant Research Studio", Icons.Outlined.Psychology)
    object AiCopilot : Screen("ai_copilot", "AI Copilot Assistant", Icons.Outlined.Psychology)
    object RegulatoryFiling : Screen("regulatory_filing", "KAP & Regulatory Filings", Icons.AutoMirrored.Outlined.List)
    object DerivativesPlatform : Screen("derivatives_platform", "Options & Futures Platform", Icons.AutoMirrored.Outlined.TrendingUp)
    object MacroIntelligence : Screen("macro_intelligence", "Macro Intelligence Platform", Icons.AutoMirrored.Outlined.TrendingUp)
    object EnterpriseApi : Screen("enterprise_api", "Enterprise API Platform", Icons.Outlined.Settings)
    object PortfolioOptimization : Screen("portfolio_optimization", "Portfolio Optimization Engine", Icons.AutoMirrored.Outlined.TrendingUp)
    object EsgPlatform : Screen("esg_platform", "ESG & Sustainability Intelligence", Icons.AutoMirrored.Outlined.List)
    object AlternativeData : Screen("alternative_data", "Alternative Data Intelligence", Icons.AutoMirrored.Outlined.TrendingUp)
    object InstitutionalIntelligence : Screen("institutional_intelligence", "Institutional & Insider Intelligence", Icons.AutoMirrored.Outlined.List)
    object EarningsCallTranscripts : Screen("earnings_call_transcripts", "Earnings Call & Transcripts Intelligence", Icons.AutoMirrored.Outlined.List)
    object CorporateEventsIntelligence : Screen("corporate_events_intelligence", "M&A & Corporate Events Intelligence", Icons.AutoMirrored.Outlined.List)
    object IpoIntelligence : Screen("ipo_intelligence", "IPO Intelligence", Icons.Outlined.Home)
    object CorporateActions : Screen("corporate_actions", "Corporate Actions", Icons.Outlined.Home)
}
