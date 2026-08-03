package com.nexus.porsuk.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.dashboard.components.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onStockClick: (String, String) -> Unit,
    onBasketClick: (Int) -> Unit,
    onLedgerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onMarketsClick: () -> Unit = {},
    onModelSepetlerClick: () -> Unit,
    onKapRadarClick: () -> Unit = {},
    onInstitutionalClick: () -> Unit = {},
    onReportingClick: () -> Unit = {},
    onAiEngineClick: () -> Unit = {},
    onPluginsClick: () -> Unit = {},
    onCloudSyncClick: () -> Unit = {},
    onDoctorClick: () -> Unit = {},
    onWatchlistClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onAllToolsClick: () -> Unit = {},
    onChatClick: (String) -> Unit
) {
    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()
    val totalBalance by viewModel.totalBalanceTry.collectAsState()
    val totalChange by viewModel.totalChangePercent.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val activeAlertCount by viewModel.allPriceAlerts.collectAsState(initial = emptyList())

    var showSearchDialog by remember { mutableStateOf(false) }
    var isBalanceVisible by remember { mutableStateOf(true) }
    var selectedInsightForDetail by remember { mutableStateOf<SmartInsightItem?>(null) }

    // Breathing pulse animation for FAB
    val infiniteTransition = rememberInfiniteTransition()
    val fabPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Entrance animation trigger
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = backgroundColor,
        topBar = {
            DashboardTopBar(
                alertCount = activeAlertCount.size,
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = onSettingsClick
            )
        },
        // FLOATING ACTION BUTTON (AI ASSISTANT) IN BOTTOM RIGHT
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onChatClick("") },
                modifier = Modifier
                    .scale(fabPulseScale)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = primaryColor.copy(alpha = 0.5f),
                        spotColor = primaryColor.copy(alpha = 0.5f)
                    ),
                shape = CircleShape,
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF8B5CF6), primaryColor, Color(0xFF4C1D95))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 28.sp)
                }
            }
        }
    ) { padding ->
        val pullRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshAllData() },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // PORTFÖY KARTI (Hero Portfolio Card)
                item(key = "portfolio_hero_card") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                    ) {
                        DashboardPortfolioCard(
                            totalBalance = totalBalance,
                            totalChange = totalChange,
                            isBalanceVisible = isBalanceVisible,
                            onToggleBalance = { isBalanceVisible = !isBalanceVisible },
                            numberFormat = numberFormat,
                            onLedgerClick = onLedgerClick
                        )
                    }
                }

                // AI COPILOT HERO CARD & SMART INSIGHTS
                item(key = "ai_copilot_hero_card") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(450)) + slideInVertically(initialOffsetY = { 35 })
                    ) {
                        AiCopilotHeroCard(
                            onInsightClick = { insightItem ->
                                selectedInsightForDetail = insightItem
                            }
                        )
                    }
                }

                // AI PİYASA ÖZETİ (2nd Most Important Card)
                item(key = "ai_market_summary_card") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        AiMarketSummaryProminentCard(
                            onDetailClick = onMarketsClick
                        )
                    }
                }

                // QUICK ACTIONS GRID
                item(key = "quick_actions_grid") {
                    QuickActionsGrid(
                        onLedgerClick = onLedgerClick,
                        onCalendarClick = onCalendarClick,
                        onAnalysisClick = onAnalysisClick,
                        onModelSepetlerClick = onModelSepetlerClick,
                        onKapRadarClick = onKapRadarClick,
                        onInstitutionalClick = onInstitutionalClick,
                        onReportingClick = onReportingClick,
                        onAiEngineClick = onAiEngineClick,
                        onPluginsClick = onPluginsClick,
                        onCloudSyncClick = onCloudSyncClick,
                        onDoctorClick = onDoctorClick,
                        onWatchlistClick = onWatchlistClick,
                        onAlertsClick = onAlertsClick,
                        onAllToolsClick = onAllToolsClick
                    )
                }

                // ORACLE KARTI (Glow + Parallax Effect)
                item(key = "oracle_highlight_card") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                    ) {
                        OracleGlowHighlightCard(
                            onOracleClick = { onChatClick("Oracle bugünkü tahminlerini açıkla") }
                        )
                    }
                }

                // GÜNÜN FIRSATLARI (Opportunities Card with Stock Badges & Tags)
                item(key = "top_opportunities_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                    ) {
                        DailyOpportunitiesSection(onStockClick = onStockClick)
                    }
                }

                // PİYASALAR KARTI (BIST, Dolar, Euro, Altın, Petrol, Bitcoin Mini Sparklines)
                item(key = "live_markets_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                    ) {
                        LiveMarketsOverviewSection(onMarketsClick = onMarketsClick)
                    }
                }

                // İZLEME LİSTESİ (Watchlist Card)
                item(key = "watchlist_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                    ) {
                        DashboardWatchlistSection(
                            watchlist = watchlist,
                            prices = prices,
                            onStockClick = onStockClick
                        )
                    }
                }

                // HABER KARTLARI (News with AI Metadata & Thumbnails)
                item(key = "latest_news_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { 90 })
                    ) {
                        DashboardNewsSection()
                    }
                }
            }
        }

        selectedInsightForDetail?.let { insight ->
            InsightDetailBottomSheet(
                insight = insight,
                onDismiss = { selectedInsightForDetail = null }
            )
        }
    }
}
