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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.FinanceViewModelFactory
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
    val context = LocalContext.current
    val financeViewModel: FinanceViewModel = viewModel(
        factory = FinanceViewModelFactory(context)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = FinanceViewModelFactory(context)
    )
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    
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
                            viewModel = financeViewModel,
                            onStockClick = { symbol, market -> 
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onBasketClick = { basketId ->
                                navController.navigate(Screen.BasketDetail.createRoute(basketId))
                            },
                            onLedgerClick = {
                                navController.navigate(Screen.IslemDefteri.route)
                            },
                            onSettingsClick = {
                                navController.navigate(Screen.Ayarlar.route)
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
                            onModelSepetlerClick = {
                                navController.navigate(Screen.ModelSepetler.route)
                            },
                            onKapRadarClick = {
                                navController.navigate(Screen.KapRadar.route)
                            },
                            onInstitutionalClick = {
                                navController.navigate(Screen.InstitutionalAnalytics.route)
                            },
                            onReportingClick = {
                                navController.navigate(Screen.ReportingCenter.route)
                            },
                            onAiEngineClick = {
                                navController.navigate(Screen.AiEngineManager.route)
                            },
                            onPluginsClick = {
                                navController.navigate(Screen.PluginMarketplace.route)
                            },
                            onChatClick = { prompt ->
                                navController.navigate(Screen.Sohbet.createRoute(prompt))
                            }
                        )
                    }

                    composable(Screen.Sepetler.route) {
                        com.nexus.porsuk.ui.portfolio.PortfolioScreen(
                            viewModel = financeViewModel,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Ayarlar.route)
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
                        val analysisViewModel: AnalysisViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        com.nexus.porsuk.ui.markets.MarketsScreen(
                            viewModel = analysisViewModel,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Ayarlar.route)
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
                        val analysisViewModel: AnalysisViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        AnalysisScreen(
                            viewModel = analysisViewModel,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Ayarlar.route)
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
                        val chatViewModel: com.nexus.porsuk.ui.chat.ChatViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        val labViewModel: com.nexus.porsuk.ui.ailab.AiLabViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        com.nexus.porsuk.ui.ailab.AiLabScreen(
                            viewModel = chatViewModel,
                            labViewModel = labViewModel,
                            onNavigateToSettings = { navController.navigate(Screen.Ayarlar.route) },
                            initialPrompt = initialPrompt,
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            },
                            onNavigateToOracle = { navController.navigate(Screen.Orakul.route) },
                            onNavigateToDoctor = { navController.navigate(Screen.Analiz.route) },
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
                        com.nexus.porsuk.ui.opportunity.AiOpportunityCenterScreen(
                            onBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(Screen.AlarmCenter.route) {
                        com.nexus.porsuk.ui.alarm.AiAlarmCenterScreen(
                            onBack = { navController.popBackStack() },
                            onStockClick = { symbol, market ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, market))
                            }
                        )
                    }

                    composable(Screen.AiPerformance.route) {
                        com.nexus.porsuk.ui.performance.AiPerformanceCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiStrategyBuilder.route) {
                        com.nexus.porsuk.ui.strategy.AiStrategyBuilderScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.GlobalIntelligence.route) {
                        com.nexus.porsuk.ui.global.GlobalMarketIntelligenceScreen(
                            onBack = { navController.popBackStack() },
                            onMarketClick = { symbol ->
                                navController.navigate(Screen.CompanyDetail.createRoute(symbol, "BIST"))
                            }
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
                        val orakulViewModel: OrakulViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        OrakulScreen(
                            viewModel = orakulViewModel,
                            onNavigateToSettings = {
                                navController.navigate(Screen.Ayarlar.route)
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
                        val kaziViewModel: KaziViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        KaziConfigScreen(
                            viewModel = kaziViewModel,
                            onBack = { navController.popBackStack() },
                            onStartMining = {
                                navController.navigate(Screen.KaziAnalysis.route)
                            }
                        )
                    }

                    composable(Screen.KaziAnalysis.route) {
                        val kaziViewModel: KaziViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
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
                        val kaziViewModel: KaziViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
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
                        SettingsScreen(viewModel = settingsViewModel)
                    }

                    composable(Screen.IslemDefteri.route) {
                        val ledgerViewModel: com.nexus.porsuk.ui.ledger.TransactionLedgerViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        com.nexus.porsuk.ui.ledger.TransactionLedgerScreen(
                            viewModel = ledgerViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.BasketCreate.route) {
                        val createBasketViewModel: CreateBasketViewModel = viewModel(
                            factory = FinanceViewModelFactory(context)
                        )
                        CreateBasketScreen(
                            viewModel = createBasketViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.BasketDetail.route,
                        arguments = listOf(navArgument("basketId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val basketId = backStackEntry.arguments?.getInt("basketId") ?: 0
                        val basketDetailViewModel: BasketDetailViewModel = viewModel(
                            factory = FinanceViewModelFactory(context, basketId)
                        )
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

                    composable(Screen.DevOpsRelease.route) {
                        com.nexus.porsuk.feature.devops.DevOpsReleaseScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PluginManager.route) {
                        com.nexus.porsuk.feature.plugins.PluginManagerScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.RealtimeStreaming.route) {
                        com.nexus.porsuk.feature.streaming.StreamingPlatformScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.QuantResearch.route) {
                        com.nexus.porsuk.feature.quant.QuantResearchScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiCopilot.route) {
                        com.nexus.porsuk.feature.copilot.AiCopilotScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.RegulatoryFiling.route) {
                        com.nexus.porsuk.feature.filings.RegulatoryFilingScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.DerivativesPlatform.route) {
                        com.nexus.porsuk.feature.derivatives.DerivativesPlatformScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.MacroIntelligence.route) {
                        com.nexus.porsuk.feature.macro.MacroIntelligenceScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.EnterpriseApi.route) {
                        com.nexus.porsuk.feature.api.EnterpriseApiScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PortfolioOptimization.route) {
                        com.nexus.porsuk.feature.optimization.PortfolioOptimizationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.EsgPlatform.route) {
                        com.nexus.porsuk.feature.esg.EsgPlatformScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AlternativeData.route) {
                        com.nexus.porsuk.feature.alternative.AlternativeDataScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.InstitutionalIntelligence.route) {
                        com.nexus.porsuk.feature.institutional.InstitutionalScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.EarningsCallTranscripts.route) {
                        com.nexus.porsuk.feature.transcript.TranscriptScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.CorporateEventsIntelligence.route) {
                        com.nexus.porsuk.feature.ma.CorporateEventScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.IpoIntelligence.route) {
                        com.nexus.porsuk.feature.ipo.IpoIntelligenceScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.CorporateActions.route) {
                        com.nexus.porsuk.feature.ipo.CorporateActionsScreen(
                            onBack = { navController.popBackStack() }
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
                            onBack = { navController.popBackStack() }
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

                    composable(Screen.InstitutionalAnalytics.route) {
                        com.nexus.porsuk.ui.institutional.InstitutionalAnalyticsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ReportingCenter.route) {
                        com.nexus.porsuk.feature.reporting.ReportingCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PluginMarketplace.route) {
                        com.nexus.porsuk.feature.plugins.PluginMarketplaceScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.AiEngineManager.route) {
                        com.nexus.porsuk.feature.ai.AiEngineManagerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
