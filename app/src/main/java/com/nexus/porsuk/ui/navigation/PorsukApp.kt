package com.nexus.porsuk.ui.navigation

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.dashboard.DashboardScreen
import com.nexus.porsuk.ui.fund.BasketDetailScreen
import com.nexus.porsuk.ui.fund.BasketDetailViewModel
import com.nexus.porsuk.ui.fund.CreateBasketScreen
import com.nexus.porsuk.ui.fund.CreateBasketViewModel
import com.nexus.porsuk.ui.fund.FundScreen
import com.nexus.porsuk.ui.analysis.AnalysisScreen
import com.nexus.porsuk.ui.analysis.AnalysisViewModel
import com.nexus.porsuk.ui.orakul.OrakulScreen
import com.nexus.porsuk.ui.orakul.OrakulViewModel
import com.nexus.porsuk.ui.settings.SettingsScreen
import com.nexus.porsuk.ui.settings.SettingsViewModel
import com.nexus.porsuk.ui.orakul.*
import com.nexus.porsuk.feature.calendar.CalendarScreen
import com.nexus.porsuk.feature.calendar.CalendarViewModel
import com.nexus.porsuk.feature.calendar.ModelSepetlerScreen
import com.nexus.porsuk.ui.theme.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PorsukApp(
    navController: NavHostController = rememberNavController()
) {
    val financeViewModel: FinanceViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val kaziViewModel: KaziViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasNotificationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Harika! Fiyat alarmları artık anında cebinde.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    PorsukTheme(darkTheme = settingsUiState.isDarkMode, trueBlack = settingsUiState.isTrueBlack) {
        if (!settingsUiState.isLoaded) {
            Box(
                modifier = Modifier.fillMaxSize().background(BackgroundNew),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    // Gizleme mantığı
                    val shouldShowBottomBar = currentRoute != null &&
                        !currentRoute.startsWith("company_detail") &&
                        !currentRoute.startsWith("basket_create") &&
                        !currentRoute.startsWith("islem_defteri") &&
                        !currentRoute.startsWith("ayarlar") &&
                        !currentRoute.startsWith("onboarding")
     
                    if (shouldShowBottomBar) {
                        // ── Premium Alt Menü ──
                        Surface(
                            color = CardNew,
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    drawLine(
                                        color = LineBorder,
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                            ) {
                                val items = listOf(
                                    Screen.Panel,
                                    Screen.Piyasalar,
                                    Screen.Analiz,
                                    Screen.Sepetler,
                                    Screen.Orakul,
                                    Screen.Sohbet
                                )

                                val selectedIndex = items.indexOfFirst { screen ->
                                    when (screen) {
                                        Screen.Sohbet   -> currentRoute == screen.route || currentRoute?.startsWith("ai_lab") == true
                                        Screen.Analiz   -> currentRoute == screen.route || currentRoute?.startsWith("analiz") == true
                                        Screen.Piyasalar -> currentRoute == screen.route || currentRoute?.startsWith("markets") == true
                                        else            -> currentRoute == screen.route
                                    }
                                }.coerceAtLeast(0)

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                ) {
                                    val itemWidth = maxWidth / items.size

                                    val animatedOffset by animateDpAsState(
                                        targetValue = itemWidth * selectedIndex + (itemWidth - 40.dp) / 2,
                                        animationSpec = tween(200, easing = LinearOutSlowInEasing),
                                        label = "indicator_offset"
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        items.forEachIndexed { idx, screen ->
                                            val selected = selectedIndex == idx

                                            val animatedScale by animateFloatAsState(
                                                targetValue = if (selected) 1.08f else 1.0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                ),
                                                label = "icon_scale_${screen.route}"
                                            )

                                            val emoji = when (screen) {
                                                Screen.Panel     -> "📊"
                                                Screen.Piyasalar -> "🌍"
                                                Screen.Analiz    -> "📈"
                                                Screen.Sepetler  -> "🧺"
                                                Screen.Orakul    -> "🔮"
                                                Screen.Sohbet    -> "🤖"
                                                else             -> "📊"
                                            }

                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) {
                                                        val targetRoute = screen.route.substringBefore("?")
                                                        if (currentRoute?.substringBefore("?") != targetRoute) {
                                                            navController.navigate(targetRoute) {
                                                                popUpTo(navController.graph.startDestinationId) {
                                                                    saveState = true
                                                                }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = emoji,
                                                    fontSize = 18.sp,
                                                    modifier = Modifier
                                                        .graphicsLayer(
                                                            scaleX = animatedScale,
                                                            scaleY = animatedScale,
                                                            alpha = if (selected) 1f else 0.6f
                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = when (screen) {
                                                        Screen.Panel     -> "Portföy"
                                                        Screen.Piyasalar -> "Piyasalar"
                                                        Screen.Analiz    -> "Analiz"
                                                        Screen.Sepetler  -> "Sepetlerim"
                                                        Screen.Orakul    -> "Orakul"
                                                        Screen.Sohbet    -> "AI Lab"
                                                        else             -> screen.title
                                                    },
                                                    fontFamily = Manrope,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.5.sp,
                                                    color = if (selected) PrimaryTeal else Color(0xFF8B978F)
                                                )
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .offset(x = animatedOffset, y = 61.dp)
                                            .size(width = 40.dp, height = 3.dp)
                                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(PrimaryTeal, AquaNew)
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = if (settingsUiState.isOnboardingCompleted) Screen.Panel.route else Screen.Onboarding.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Onboarding.route) {
                        com.nexus.porsuk.ui.onboarding.OnboardingScreen(
                            onFinished = {
                                settingsViewModel.setOnboardingCompleted(true)
                                navController.navigate(Screen.Panel.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Panel.route) {
                        DashboardScreen(
                            onStockClick = { symbol, market -> 
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onBasketClick = { basketId ->
                                navController.navigate(Screen.BasketDetail.createRoute(basketId))
                            },
                            onPortfolioClick = {
                                navController.navigate(Screen.PortfolioOverview.route)
                            },
                            onLedgerClick = {
                                navController.navigate(Screen.IslemDefteri.route)
                            },
                            onSettingsClick = {
                                navController.navigate(Screen.Ayarlar.route)
                            },
                            onNotificationsClick = {
                                navController.navigate(Screen.NotificationsCenter.route)
                            },
                            onSearchClick = {
                                navController.navigate(Screen.GlobalSearch.route)
                            },
                            onCalendarClick = {
                                navController.navigate(Screen.Calendar.route)
                            },
                            onAnalysisClick = {
                                navController.navigate(Screen.Analiz.route)
                            },
                            onMarketsClick = {
                                navController.navigate(Screen.Piyasalar.route)
                            },
                            onSeeAllBasketsClick = {
                                navController.navigate(Screen.Sepetler.route)
                            },
                            onCreateBasketClick = {
                                navController.navigate(Screen.BasketCreate.route)
                            },
                            onModelSepetlerClick = {
                                navController.navigate(Screen.ModelSepetler.route)
                            },
                            onKapRadarClick = {
                                navController.navigate(Screen.KapRadar.route)
                            },
                            onReportingClick = {
                                navController.navigate(Screen.ReportingCenter.route)
                            },
                            onAiEngineClick = {
                                navController.navigate(Screen.AiEngineManager.route)
                            },
                            onCloudSyncClick = {
                                navController.navigate(Screen.CloudSync.route)
                            },
                            onDoctorClick = {
                                navController.navigate(Screen.PortfolioDoctor.route)
                            },
                            onWatchlistClick = {
                                navController.navigate(Screen.Watchlist.route)
                            },
                            onAlertsClick = {
                                navController.navigate(Screen.Alerts.route)
                            },
                            onAllToolsClick = {
                                navController.navigate(Screen.AllTools.route)
                            },
                            onChatClick = { prompt ->
                                navController.navigate(Screen.Sohbet.createRoute(prompt))
                            }
                        )
                    }

                    composable(Screen.Sepetler.route) {
                        com.nexus.porsuk.ui.fund.FundScreen(
                            viewModel = hiltViewModel(),
                            onFundClick = { basketId, _ ->
                                navController.navigate(Screen.BasketDetail.createRoute(basketId))
                            },
                            onKaziNavigate = {
                                navController.navigate(Screen.KaziConfig.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Ayarlar.route)
                            }
                        )
                    }

                    composable(Screen.PortfolioOverview.route) {
                        com.nexus.porsuk.ui.portfolio.PortfolioScreen(
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.NotificationsCenter.route)
                            },
                            onLedgerClick = {
                                navController.navigate(Screen.IslemDefteri.route)
                            },
                            onAnalysisClick = {
                                navController.navigate(Screen.Analiz.route)
                            }
                        )
                    }

                    composable(Screen.Piyasalar.route) {
                        val analysisViewModel: AnalysisViewModel = hiltViewModel()
                        com.nexus.porsuk.ui.markets.MarketsScreen(
                            viewModel = analysisViewModel,
                            financeViewModel = financeViewModel,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onFundClick = { fundCode ->
                                // Fund Intelligence removed
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.NotificationsCenter.route)
                            },
                            onCalendarClick = {
                                navController.navigate(Screen.Calendar.route)
                            },
                            onScreenerClick = {
                                navController.navigate(Screen.KapRadar.route)
                            }
                        )
                    }

                    composable(Screen.Analiz.route) {
                        val analysisViewModel: AnalysisViewModel = hiltViewModel()
                        AnalysisScreen(
                            viewModel = analysisViewModel,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.NotificationsCenter.route)
                            },
                            onCreateBasket = {
                                navController.navigate(Screen.BasketCreate.route)
                            },
                            onNavigateToDuel = { symbol1, symbol2 ->
                                navController.navigate(Screen.HisseDuello.createRoute(symbol1, symbol2))
                            }
                        )
                    }

                    composable(
                        route = Screen.Sohbet.route,
                        arguments = listOf(
                            navArgument("initialPrompt") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val initialPrompt = backStackEntry.arguments?.getString("initialPrompt")
                        val chatViewModel: com.nexus.porsuk.ui.chat.ChatViewModel = hiltViewModel()
                        val labViewModel: com.nexus.porsuk.ui.ailab.AiLabViewModel = hiltViewModel()
                        com.nexus.porsuk.ui.ailab.AiLabScreen(
                            viewModel = chatViewModel,
                            labViewModel = labViewModel,
                            onNavigateToSettings = { navController.navigate(Screen.NotificationsCenter.route) },
                            initialPrompt = initialPrompt,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToOracle = { navController.navigate(Screen.Orakul.route) },
                            onNavigateToDoctor = { navController.navigate(Screen.PortfolioDoctor.route) },
                            onNavigateToSimulator = { navController.navigate(Screen.PortfolioSimulator.route) },
                            onNavigateToOpportunityCenter = { navController.navigate(Screen.OpportunityCenter.route) },
                            onNavigateToAlarmCenter = { navController.navigate(Screen.AlarmCenter.route) },
                            onNavigateToAiPerformance = { navController.navigate(Screen.AiPerformance.route) },
                            onNavigateToStrategyBuilder = { navController.navigate(Screen.AiStrategyBuilder.route) },
                            onNavigateToGlobalIntelligence = { navController.navigate(Screen.GlobalIntelligence.route) },
                            onNavigateToPlaceholder = { title ->
                                navController.navigate(Screen.Placeholder.createRoute(title))
                            }
                        )
                    }

                    composable(Screen.GlobalSearch.route) {
                        com.nexus.porsuk.ui.search.GlobalSearchScreen(
                            onBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(Screen.NotificationsCenter.route) {
                        com.nexus.porsuk.ui.notifications.NotificationsCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PortfolioSimulator.route) {
                        com.nexus.porsuk.ui.simulator.PortfolioSimulatorScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.OpportunityCenter.route) {
                        com.nexus.porsuk.feature.scanner.ScannerScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AlarmCenter.route) {
                        com.nexus.porsuk.feature.alerts.AlertsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiPerformance.route) {
                        com.nexus.porsuk.ui.performance.AiPerformanceCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiStrategyBuilder.route) {
                        com.nexus.porsuk.feature.strategybuilder.StrategyBuilderScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.GlobalIntelligence.route) {
                        com.nexus.porsuk.feature.globalmarkets.GlobalMarketsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.Placeholder.route,
                        arguments = listOf(
                            navArgument("title") {
                                type = NavType.StringType
                                defaultValue = "Yakında"
                            }
                        )
                    ) { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: "Yakında"
                        com.nexus.porsuk.ui.ailab.PlaceholderScreen(
                            title = title,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Orakul.route) {
                        val orakulViewModel: OrakulViewModel = hiltViewModel()
                        OrakulScreen(
                            viewModel = orakulViewModel,
                            onNavigateToSettings = {
                                navController.navigate(Screen.NotificationsCenter.route)
                            },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onChatNavigate = { prompt ->
                                navController.navigate(Screen.Sohbet.createRoute(prompt))
                            },
                            onKaziNavigate = {
                                navController.navigate(Screen.KaziConfig.route)
                            }
                        )
                    }

                    composable(Screen.KaziConfig.route) {
                        KaziConfigScreen(
                            viewModel = kaziViewModel,
                            onBack = { navController.popBackStack() },
                            onStartMining = {
                                navController.navigate(Screen.KaziAnalysis.route)
                            }
                        )
                    }

                    composable(Screen.KaziAnalysis.route) {
                        KaziAnalysisScreen(
                            viewModel = kaziViewModel,
                            onBack = { navController.popBackStack() },
                            onFinished = {
                                navController.navigate(Screen.KaziResult.route) {
                                    popUpTo(Screen.KaziAnalysis.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.KaziResult.route) {
                        KaziResultScreen(
                            viewModel = kaziViewModel,
                            onBack = { 
                                navController.popBackStack(Screen.Orakul.route, inclusive = false)
                            },
                            onStockClick = { symbol: String, market: String ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(
                        route = Screen.Calendar.route,
                        arguments = listOf(
                            navArgument("initialTab") {
                                type = NavType.IntType
                                defaultValue = 0
                            }
                        )
                    ) { backStackEntry ->
                        val initialTab = backStackEntry.arguments?.getInt("initialTab") ?: 0
                        val calendarViewModel: CalendarViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                        CalendarScreen(
                            viewModel = calendarViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            initialTab = initialTab
                        )
                    }

                    composable(Screen.ModelSepetler.route) {
                        val calendarViewModel: CalendarViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                        ModelSepetlerScreen(
                            viewModel = calendarViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.KapRadar.route) {
                        com.nexus.porsuk.ui.kap.KapSmartMoneyScreen(
                            onBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(Screen.Ayarlar.route) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.IslemDefteri.route) {
                        val ledgerViewModel: com.nexus.porsuk.ui.ledger.TransactionLedgerViewModel = hiltViewModel()
                        com.nexus.porsuk.ui.ledger.TransactionLedgerScreen(
                            viewModel = ledgerViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.BasketCreate.route) {
                        val createBasketViewModel: CreateBasketViewModel = hiltViewModel()
                        CreateBasketScreen(
                            viewModel = createBasketViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.BasketDetail.route,
                        arguments = listOf(navArgument("basketId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val basketDetailViewModel: BasketDetailViewModel = hiltViewModel()
                        BasketDetailScreen(
                            viewModel = basketDetailViewModel,
                            onBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(
                        route = Screen.CompanyDetail.route,
                        arguments = listOf(
                            navArgument("symbol") { type = NavType.StringType },
                            navArgument("market") { 
                                type = NavType.StringType
                                defaultValue = "IST"
                            }
                        )
                    ) {
                        com.nexus.porsuk.feature.companydetail.CompanyDetailScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.HisseDuello.route,
                        arguments = listOf(
                            navArgument("symbol1") { type = NavType.StringType; defaultValue = "THYAO" },
                            navArgument("symbol2") { type = NavType.StringType; defaultValue = "PGSUS" }
                        )
                    ) { backStackEntry ->
                        val symbol1 = backStackEntry.arguments?.getString("symbol1") ?: "THYAO"
                        val symbol2 = backStackEntry.arguments?.getString("symbol2") ?: "PGSUS"
                        com.nexus.porsuk.ui.stock.HisseDuelloScreen(
                            initialSymbol1 = symbol1,
                            initialSymbol2 = symbol2,
                            viewModel = financeViewModel,
                            onBack = { navController.popBackStack() },
                            onStockClick = { s, m -> navController.navigate(Screen.CompanyDetail.createRoute(s, m)) }
                        )
                    }

                    composable(Screen.MacroIntelligence.route) {
                        com.nexus.porsuk.feature.macro.MacroIntelligenceScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PortfolioOptimization.route) {
                        com.nexus.porsuk.feature.optimization.PortfolioOptimizationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiAutomation.route) {
                        com.nexus.porsuk.feature.automation.NotificationAutomationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.MultiAgentConsensus.route,
                        arguments = listOf(navArgument("symbol") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
                        com.nexus.porsuk.ui.orakul.agents.MultiAgentAnalysisScreen(
                            symbol = symbol,
                            onBack = { navController.popBackStack() },
                            onNavigateToSettings = { navController.navigate(Screen.Ayarlar.route) }
                        )
                    }

                    composable(
                        route = Screen.CompanyDetail.route,
                        arguments = listOf(
                            navArgument("symbol") { type = NavType.StringType },
                            navArgument("market") {
                                type = NavType.StringType
                                defaultValue = "IST"
                            }
                        )
                    ) {
                        com.nexus.porsuk.feature.companydetail.CompanyDetailScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToLedger = { navController.navigate(Screen.IslemDefteri.route) },
                            onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) }
                        )
                    }

                    composable(
                        route = Screen.AdvancedChart.route,
                        arguments = listOf(navArgument("symbol") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
                        com.nexus.porsuk.ui.chart.AdvancedChartStudioScreen(
                            symbol = symbol,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ReportingCenter.route) {
                        com.nexus.porsuk.feature.reporting.ReportingCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiEngineManager.route) {
                        com.nexus.porsuk.feature.ai.AiEngineManagerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.CloudSync.route) {
                        com.nexus.porsuk.feature.cloudsync.CloudSyncScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PortfolioDoctor.route) {
                        com.nexus.porsuk.feature.doctor.PortfolioDoctorScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Watchlist.route) {
                        com.nexus.porsuk.feature.watchlist.WatchlistScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToCompanyDetail = { symbol ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol))
                            }
                        )
                    }

                    composable(Screen.Alerts.route) {
                        com.nexus.porsuk.feature.alerts.AlertsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.News.route) {
                        com.nexus.porsuk.feature.news.NewsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Backtest.route) {
                        com.nexus.porsuk.feature.backtest.BacktestScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Dividend.route) {
                        com.nexus.porsuk.feature.dividend.DividendScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Risk.route) {
                        com.nexus.porsuk.feature.risk.RiskScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Scanner.route) {
                        com.nexus.porsuk.feature.scanner.ScannerScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Screener.route) {
                        com.nexus.porsuk.feature.screener.ScreenerUltimateScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Chart.route) {
                        com.nexus.porsuk.feature.chart.ChartScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Technical.route) {
                        com.nexus.porsuk.feature.technical.TechnicalScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.MasterScore.route) {
                        com.nexus.porsuk.feature.masterscore.MasterScoreScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.GlobalMarkets.route) {
                        com.nexus.porsuk.feature.globalmarkets.GlobalMarketsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.SecurityCenter.route) {
                        com.nexus.porsuk.feature.security.SecurityCenterScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Upgrade.route) {
                        com.nexus.porsuk.feature.subscription.UpgradeScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AllTools.route) {
                        com.nexus.porsuk.ui.tools.AllToolsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onToolClick = { route ->
                                navController.navigate(route)
                            }
                        )
                    }
                }
            }
        }
    }
}
