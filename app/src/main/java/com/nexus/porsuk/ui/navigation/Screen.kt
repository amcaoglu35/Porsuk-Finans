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
}

