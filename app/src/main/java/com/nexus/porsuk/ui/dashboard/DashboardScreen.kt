package com.nexus.porsuk.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.nexus.porsuk.data.remote.RichOfflineDataEngine
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class SmartInsightItem(
    val title: String,
    val summary: String,
    val category: String, // Portföy, Risk, Haber, Temettü, Döviz
    val icon: String,
    val impactScore: String,
    val fullExplanation: String,
    val aiCommentary: String,
    val riskAnalysis: String,
    val scenarioBullish: String,
    val scenarioBearish: String,
    val recommendedAction: String
)

// Clean Design System Tokens
private val ScreenBg = Color(0xFFFAFAFA)
private val CardSurface = Color(0xFFFFFFFF)
private val PurplePrimary = Color(0xFF6C4CF1)
private val PurpleSoft = Color(0xFFF3F0FF)
private val GreenPositive = Color(0xFF00C48C)
private val OrangeWarning = Color(0xFFFF9800)
private val RedNegative = Color(0xFFF44336)
private val TextDarkColor = Color(0xFF1E293B)
private val TextSubColor = Color(0xFF64748B)
private val LineBorderColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
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
    onChatClick: (String) -> Unit
) {
    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())
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

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = ScreenBg,
        topBar = {
            DashboardTopBar(
                alertCount = activeAlertCount.size,
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = onSettingsClick
            )
        },
        // 1) FLOATING ACTION BUTTON (AI ASSISTANT) IN BOTTOM RIGHT
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onChatClick("") },
                modifier = Modifier
                    .scale(fabPulseScale)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = PurplePrimary.copy(alpha = 0.5f),
                        spotColor = PurplePrimary.copy(alpha = 0.5f)
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
                                colors = listOf(Color(0xFF8B5CF6), PurplePrimary, Color(0xFF4C1D95))
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
                // 3) PORTFÖY KARTI (Hero Portfolio Card)
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

                // 3.5) AI COPILOT HERO CARD & SMART INSIGHTS
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

                // 4) AI PİYASA ÖZETİ (2nd Most Important Card)
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

                // 4.5) QUICK ACTIONS GRID
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
                        onPluginsClick = onPluginsClick
                    )
                }

                // 5) ORACLE KARTI (Glow + Parallax Effect)
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

                // 6) GÜNÜN FIRSATLARI (Opportunities Card with Stock Badges & Tags)
                item(key = "top_opportunities_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                    ) {
                        DailyOpportunitiesSection(onStockClick = onStockClick)
                    }
                }

                // 7) PİYASALAR KARTI (BIST, Dolar, Euro, Altın, Petrol, Bitcoin Mini Sparklines)
                item(key = "live_markets_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                    ) {
                        LiveMarketsOverviewSection(onMarketsClick = onMarketsClick)
                    }
                }

                // 8) İZLEME LİSTESİ (Watchlist Card)
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

                // 9) HABER KARTLARI (News with AI Metadata & Thumbnails)
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

// ── 2) LOGO & ÜST BAR (Top Bar with High-Res Vector Logo & Centered Title) ──
@Composable
private fun DashboardTopBar(
    alertCount: Int,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ScreenBg)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High-Res Geometric P Logo Vector Canvas
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(modifier = Modifier.size(32.dp)) {
                drawCircle(
                    brush = Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), PurplePrimary))
                )
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension * 0.28f,
                    center = Offset(size.width * 0.45f, size.height * 0.4f)
                )
                drawCircle(
                    color = PurplePrimary,
                    radius = size.minDimension * 0.16f,
                    center = Offset(size.width * 0.45f, size.height * 0.4f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    "PORSUK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = TextDarkColor
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = PurplePrimary
                )
            }
        }

        // Title
        Text(
            "Anasayfa",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDarkColor
        )

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = TextDarkColor)
            }
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        if (alertCount > 0) {
                            Badge(containerColor = RedNegative) {
                                Text("$alertCount", fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PurplePrimary)
                }
            }
        }
    }
}

// ── 3) PORTFÖY KARTI (Hero Portfolio Card) ──
@Composable
private fun DashboardPortfolioCard(
    totalBalance: Double,
    totalChange: Double,
    isBalanceVisible: Boolean,
    onToggleBalance: () -> Unit,
    numberFormat: String,
    onLedgerClick: () -> Unit
) {
    val displayValue = remember(totalBalance, isBalanceVisible) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = PurplePrimary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Toplam Portföy Değeri",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSubColor,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Gizle/Göster",
                        tint = TextSubColor,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onToggleBalance)
                    )
                }

                Button(
                    onClick = onLedgerClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSoft),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("İşlem Defteri", fontSize = 10.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = IBMPlexMono,
                            fontSize = 28.sp
                        ),
                        color = TextDarkColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "^ %2,35 (₺28.750,45) Bugün",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                        color = GreenPositive
                    )
                }

                // Gradient Area Line Chart
                val sparkValues = remember { listOf(40f, 42f, 41f, 45f, 44f, 48f, 50f, 55f, 53f, 60f) }
                Sparkline(
                    values = sparkValues,
                    color = GreenPositive,
                    modifier = Modifier
                        .width(110.dp)
                        .height(46.dp),
                    filled = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = LineBorderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(16.dp))

            // Enlarged Risk Skoru & AI Sağlık Puanı Rings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PortfolioMetricItem(title = "Toplam Getiri", value = "₺245.750,45", pct = "^ %24,36", isPos = true)
                PortfolioMetricItem(title = "Getiri (Yıl)", value = "₺325.420,15", pct = "^ %32,68", isPos = true)
                EnlargedRingItem(title = "Risk Skoru", score = "72", label = "Orta", color = OrangeWarning)
                EnlargedRingItem(title = "AI Sağlık", score = "85", label = "İyi", color = GreenPositive)
            }
        }
    }
}

@Composable
private fun PortfolioMetricItem(title: String, value: String, pct: String, isPos: Boolean) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TextSubColor, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDarkColor)
        Text(pct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = if (isPos) GreenPositive else RedNegative)
    }
}

@Composable
private fun EnlargedRingItem(title: String, score: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TextSubColor, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.5.dp.toPx()
                    drawArc(
                        color = color.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(score, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = TextDarkColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color, fontFamily = Manrope)
        }
    }
}

// ── 4) AI PİYASA ÖZETİ (Prominent 2nd Card) ──
@Composable
private fun AiMarketSummaryProminentCard(onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "AI PİYASA ÖZETİ",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = PurplePrimary,
                        fontFamily = Manrope
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onDetailClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurplePrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Short Comment
            Text(
                "\"Bankacılık ve savunma sektöründe pozitif görünüm devam ediyor. Portföy dengesi olumlu.\"",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, lineHeight = 16.sp),
                color = TextDarkColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Metric Pills Row (AI Market Score, AI Confidence, Risk, Fear&Greed, Market Pulse)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SummaryPillItem("Market Score", "78", GreenPositive, modifier = Modifier.weight(1f))
                SummaryPillItem("Confidence", "%85", GreenPositive, modifier = Modifier.weight(1f))
                SummaryPillItem("Risk", "Düşük", GreenPositive, modifier = Modifier.weight(1f))
                SummaryPillItem("Korku/Açgöz.", "55 Nötr", OrangeWarning, modifier = Modifier.weight(1f))
                SummaryPillItem("Piyasa Nabzı", "68 Pozitif", GreenPositive, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryPillItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = PurpleSoft,
        border = BorderStroke(1.dp, Color(0xFFE2D9FF))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSubColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp, fontFamily = IBMPlexMono), color = color, maxLines = 1)
        }
    }
}

// ── 5) ORACLE KARTI (Glow + Parallax Effect) ──
@Composable
private fun OracleGlowHighlightCard(onOracleClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = PurplePrimary.copy(alpha = 0.3f))
            .clickable(onClick = onOracleClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E0A4C), Color(0xFF3B1578), Color(0xFF6C4CF1))
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rotating Crystal Ball
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔮", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Oracle Bugün Ne Diyor?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Piyasalarda pozitif momentum devam ediyor. 3 gün içinde yukarı yönlü hareket beklentisi %62.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%87", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color(0xFFC084FC))
                    Text("Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// ── 6) GÜNÜN FIRSATLARI (Stock Badges & AI Signal Tags) ──
@Composable
private fun DailyOpportunitiesSection(onStockClick: (String, String) -> Unit) {
    val opportunities = remember {
        listOf(
            OpportunityItem("ASELS", "Aselsan", "₺56,70", "^ %4,25", "Güçlü Alım", GreenPositive, true),
            OpportunityItem("THYAO", "Türk Hava Yolları", "₺305,25", "^ %2,87", "Alım Sinyali", GreenPositive, true),
            OpportunityItem("KCHOL", "Koç Holding", "₺182,40", "^ %0,31", "Nötr", OrangeWarning, true),
            OpportunityItem("AKBNK", "Akbank", "₺52,15", "v %-0,42", "Dikkat", RedNegative, false)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("🔥 Günün Fırsatları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
            Spacer(modifier = Modifier.height(12.dp))

            opportunities.forEach { item ->
                OpportunityRowItem(item = item, onClick = { onStockClick(item.code, "BIST") })
                HorizontalDivider(color = LineBorderColor.copy(alpha = 0.4f))
            }
        }
    }
}

private data class OpportunityItem(
    val code: String,
    val name: String,
    val price: String,
    val changePct: String,
    val signal: String,
    val signalColor: Color,
    val isPositive: Boolean
)

@Composable
private fun OpportunityRowItem(item: OpportunityItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stock Logo Badge
        Surface(
            shape = CircleShape,
            color = PurpleSoft,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.code.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = PurplePrimary)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.code, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
            Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSubColor)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDarkColor)
            Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.sp), color = if (item.isPositive) GreenPositive else RedNegative)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // AI Signal Tag Badge
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = item.signalColor.copy(alpha = 0.12f)
        ) {
            Text(
                item.signal,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = item.signalColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ── 7) PİYASALAR KARTI (BIST, Dolar, Euro, Altın, Petrol, Bitcoin) ──
@Composable
private fun LiveMarketsOverviewSection(onMarketsClick: () -> Unit) {
    val markets = remember {
        listOf(
            MarketItem("BIST 100", "10.456,87", "^ %1,35", true, listOf(40f, 42f, 45f, 48f, 50f)),
            MarketItem("USD/TRY", "32,65", "^ %0,42", true, listOf(32f, 32.2f, 32.4f, 32.65f)),
            MarketItem("EUR/USD", "1,0850", "v %-0,15", false, listOf(1.09f, 1.088f, 1.085f)),
            MarketItem("ALTIN/GR", "2.395,45", "^ %0,31", true, listOf(2380f, 2390f, 2395f)),
            MarketItem("BRENT", "84.20", "^ %0,75", true, listOf(82f, 83f, 84.2f)),
            MarketItem("BITCOIN", "67.450,00", "^ %2,10", true, listOf(65000f, 66000f, 67450f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌐 Piyasalar Özet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
                Text("Tüm Piyasalar >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurplePrimary, modifier = Modifier.clickable(onClick = onMarketsClick))
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(markets, key = { it.title }) { item ->
                    val color = if (item.isPos) GreenPositive else RedNegative
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ScreenBg,
                        border = BorderStroke(1.dp, LineBorderColor),
                        modifier = Modifier
                            .width(115.dp)
                            .clickable(onClick = onMarketsClick)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(item.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDarkColor)
                            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = TextSubColor)
                            Text(item.change, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 9.sp), color = color)

                            Spacer(modifier = Modifier.height(4.dp))
                            Sparkline(
                                values = item.sparkValues,
                                color = color,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                filled = true
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class MarketItem(
    val title: String,
    val price: String,
    val change: String,
    val isPos: Boolean,
    val sparkValues: List<Float>
)

// ── 8) İZLEME LİSTESİ (Watchlist Card with Gradient Sparkline Fill & Favorite Star) ──
@Composable
private fun DashboardWatchlistSection(
    watchlist: List<com.nexus.porsuk.data.local.entity.WatchlistItem>,
    prices: Map<String, com.nexus.porsuk.data.local.entity.PriceSnapshot>,
    onStockClick: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("⭐ İzleme Listem", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
            Spacer(modifier = Modifier.height(12.dp))

            if (watchlist.isEmpty()) {
                Text("Henüz izleme listene hisse eklemedin.", style = MaterialTheme.typography.bodySmall, color = TextSubColor)
            } else {
                watchlist.take(5).forEach { item ->
                    val price = prices[item.symbol]?.price ?: 0.0
                    WatchlistRowItem(item = item, price = price, onClick = { onStockClick(item.symbol, "BIST") })
                    HorizontalDivider(color = LineBorderColor.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun WatchlistRowItem(
    item: com.nexus.porsuk.data.local.entity.WatchlistItem,
    price: Double,
    onClick: () -> Unit
) {
    var isFav by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
            Text("BIST", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSubColor)
        }

        Sparkline(
            values = listOf(40f, 42f, 45f, 48f, 50f),
            color = GreenPositive,
            modifier = Modifier
                .width(60.dp)
                .height(24.dp),
            filled = true
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(CurrencyFormatter.formatTRY(price, "TR"), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDarkColor)
            Text("^ %1,45", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = GreenPositive)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "Favori",
            tint = if (isFav) Color(0xFFFFB800) else TextSubColor,
            modifier = Modifier
                .size(16.dp)
                .clickable { isFav = !isFav }
        )
    }
}

@Composable
fun QuickActionsGrid(
    onLedgerClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onModelSepetlerClick: () -> Unit,
    onKapRadarClick: () -> Unit = {},
    onInstitutionalClick: () -> Unit = {},
    onReportingClick: () -> Unit = {},
    onAiEngineClick: () -> Unit = {},
    onPluginsClick: () -> Unit = {}
) {
    val actions = listOf(
        QuickActionItem(
            emoji = "📋",
            title = "İşlem Defterim",
            subtitle = "Alım / satım geçmişi",
            gradientStart = PrimaryTeal,
            gradientEnd = Color(0xFF007A58),
            accentSoft = TealSoft,
            onClick = onLedgerClick
        ),
        QuickActionItem(
            emoji = "📅",
            title = "Borsa Takvimi",
            subtitle = "Temettü & Bilanço",
            gradientStart = AquaNew,
            gradientEnd = Color(0xFF008BA3),
            accentSoft = AquaSoft,
            onClick = onCalendarClick
        ),
        QuickActionItem(
            emoji = "📊",
            title = "Kurumsal Analiz",
            subtitle = "Bloomberg Terminal",
            gradientStart = Color(0xFF1E293B),
            gradientEnd = Color(0xFF0F172A),
            accentSoft = Color(0xFFE2E8F0),
            onClick = onInstitutionalClick
        ),
        QuickActionItem(
            emoji = "📑",
            title = "Rapor Merkezi",
            subtitle = "PDF & Excel Döküm",
            gradientStart = Color(0xFF7C6CF0),
            gradientEnd = Color(0xFF5C4AD8),
            accentSoft = Color(0xFFECE9FE),
            onClick = onReportingClick
        ),
        QuickActionItem(
            emoji = "🤖",
            title = "AI Yönetimi",
            subtitle = "Cloud & Local Hibrit",
            gradientStart = Color(0xFFE8A93B),
            gradientEnd = Color(0xFFC8891E),
            accentSoft = Color(0xFFFBF1DD),
            onClick = onAiEngineClick
        ),
        QuickActionItem(
            emoji = "🧩",
            title = "Eklenti Marketi",
            subtitle = "API & Plugin Yönetimi",
            gradientStart = Color(0xFF10B981),
            gradientEnd = Color(0xFF007A58),
            accentSoft = AquaSoft,
            onClick = onPluginsClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until actions.size step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumQuickActionCard(item = actions[i], modifier = Modifier.weight(1f))
                if (i + 1 < actions.size) {
                    PremiumQuickActionCard(item = actions[i+1], modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

data class QuickActionItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val accentSoft: Color,
    val onClick: () -> Unit
)

@Composable
fun PremiumQuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier
) {
    var isCardPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isCardPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(82.dp)
            .graphicsLayer(
                scaleX = cardScale,
                scaleY = cardScale
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                item.onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(item.gradientStart.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.accentSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 20.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDarkColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSubColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ── 9) HABER KARTLARI (News Cards with AI Metadata & Thumbnails) ──
@Composable
private fun DashboardNewsSection() {
    val newsList = remember {
        listOf(
            NewsItem("BIST 100 rekor tazeledi: Bankacılık öncülüğünde yükseliş", "Piyasalar", "Yüksek Olumlu", "%89 Güven", "3 dk okuma"),
            NewsItem("Merkez Bankası faiz kararı metninde enflasyon vurgusu", "Makro", "Nötr Etki", "%92 Güven", "4 dk okuma")
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("📰 Son Haberler & AI Etki Analizi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor)
            Spacer(modifier = Modifier.height(12.dp))

            newsList.forEach { item ->
                NewsRowItem(item = item)
                HorizontalDivider(color = LineBorderColor.copy(alpha = 0.4f))
            }
        }
    }
}

private data class NewsItem(
    val title: String,
    val category: String,
    val impact: String,
    val aiConfidence: String,
    val readTime: String
)

@Composable
private fun NewsRowItem(item: NewsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // News Thumbnail Box
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PurpleSoft),
            contentAlignment = Alignment.Center
        ) {
            Text("📰", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = PurplePrimary)
                Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSubColor)
                Text(item.impact, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = GreenPositive)
                Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSubColor)
                Text(item.readTime, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSubColor)
            }
        }
    }
}

// ── 3.5) AI COPILOT HERO CARD & SMART INSIGHTS COMPONENT ──
@Composable
private fun AiCopilotHeroCard(
    onInsightClick: (SmartInsightItem) -> Unit
) {
    val sampleInsights = remember {
        listOf(
            SmartInsightItem(
                title = "Teknoloji Sektör Yoğunlaşması",
                summary = "Portföyün teknoloji hisselerine %42 fazla ağırlık vermiş.",
                category = "Portföy",
                icon = "⚡",
                impactScore = "Yüksek",
                fullExplanation = "Portföyünüzün toplam varlık dağılımında Teknoloji ve Yazılım sektör hisseleri %42 paya sahiptir. Bu oran genel piyasa ortalamasının üzerindedir.",
                aiCommentary = "Sektörel konsantrasyon teknoloji rallilerinde getiri artışı sağlasa da olası bir düzeltmede portföy volatilitesini yükseltebilir.",
                riskAnalysis = "Orta-Yüksek risk seviyesi. Sektör dağılım dengesizliği +%3.2 ek dalgalanma risk puanı eklemektedir.",
                scenarioBullish = "Teknoloji rallisinin devamında portföy %14 ek getiri üretebilir.",
                scenarioBearish = "Kar satışlarında portföy ortalama %6.8 geri çekilme yaşayabilir.",
                recommendedAction = "Portföyü %15 oranında Savunma ve Temettü hisseleri ile dengeleyin."
            ),
            SmartInsightItem(
                title = "Risk Seviyesi Yükseliş Trendinde",
                summary = "Son 7 günde portföy risk seviyen %68'e yükseldi.",
                category = "Risk",
                icon = "🛡️",
                impactScore = "Orta",
                fullExplanation = "Piyasa volatilitesi ve portföy içi betaların artması sonucu son 7 günlük risk skorunuz %58'den %68'e tırmanmıştır.",
                aiCommentary = "Yükselen volatilite kısa vadede stop-loss seviyelerinin yakından takip edilmesini gerektirir.",
                riskAnalysis = "Volatilitesi yüksek hisselerin ağırlığı %30 seviyesine yaklaşmıştır.",
                scenarioBullish = "Momentum korunursa kısa vadeli %8 prim potansiyeli mevcuttur.",
                scenarioBearish = "Olası kar satışlarında varlık koruma marjı daralabilir.",
                recommendedAction = "Nakit oranınızı %12 seviyesine yükselterek varlık koruma kalkanı oluşturun."
            ),
            SmartInsightItem(
                title = "THYAO Haber Katalizörü",
                summary = "THYAO yolcu büyüme verilerinden olumlu etkilenebilir.",
                category = "Haber",
                icon = "🚀",
                impactScore = "Güçlü",
                fullExplanation = "Havayolu yolcu sayıları ve dış hat büyüme rakamları analist beklentilerini %4.2 aşmıştır.",
                aiCommentary = "Yolcu doluluk oranları ve jet yakıtı marjları bilançoyu destekleyecek seviyededir.",
                riskAnalysis = "Düşük-Orta risk. Haber ve KAP entropy sinyalleri %88 güven aralığındadır.",
                scenarioBullish = "Hisse ₺320 direncini kırarak rekor tazeleyebilir.",
                scenarioBearish = "Jeopolitik gelişmelerde ₺295 desteği test edilebilir.",
                recommendedAction = "Mevcut pozisyonu koruyun, kademeli alım bölgesi ₺298 - ₺302."
            ),
            SmartInsightItem(
                title = "Temettü Sezonu Yaklaşıyor",
                summary = "Portföyünüzdeki 3 şirket bu çeyrek temettü dağıtacak.",
                category = "Temettü",
                icon = "💰",
                impactScore = "Fırsat",
                fullExplanation = "Portföyünüzde yer alan Ereğli, Tüpraş ve Aselsan nakit temettü tarihlerine yaklaşmaktadır.",
                aiCommentary = "Nakit akışı oluşturmak ve temettü verimini bileşik getiriye dönüştürmek için ideal zamanlama.",
                riskAnalysis = "Çok düşük risk. Temettü ödemeleri doğrudan nakit bakiyenize eklenecektir.",
                scenarioBullish = "Temettü verimi portföye %4.5 nakit girişi sağlayacaktır.",
                scenarioBearish = "Temettü sonrası hisse başı düzeltmeler kısa sürede kapanma eğilimindedir.",
                recommendedAction = "Temettü ödemelerini otomatik olarak model sepetlerde yeniden yatırıma dönüştürün."
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, LineBorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "AI COPILOT & SMART INSIGHTS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp, fontFamily = Manrope),
                            color = TextDarkColor
                        )
                        Text(
                            "Proaktif Yatırım Asistanı",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = PurplePrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenPositive.copy(alpha = 0.12f)
                ) {
                    Text(
                        "● Canlı",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                        color = GreenPositive,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Günün Özeti Grid Cards
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PurpleSoft.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌅", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Günün Özeti & Piyasa Nabzı", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = PurplePrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "BİST 100 bugün %1,35 yükseldi. Savunma ve teknoloji öncülüğünde rekor tazeleniyor. Portföyünüz bugün endeksi %0.8 yenerek daha iyi performans gösterdi.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextDarkColor.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("💡 Öne Çıkan Akıllı İçgörüler", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextSubColor)

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable Smart Insight Cards Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(sampleInsights, key = { it.title }) { item ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onInsightClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ScreenBg),
                        border = BorderStroke(1.dp, LineBorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.icon, fontSize = 16.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PurpleSoft
                                ) {
                                    Text(
                                        item.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold),
                                        color = PurplePrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDarkColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.summary, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, lineHeight = 13.sp), color = TextSubColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

// ── 3.6) INSIGHT DETAIL MODAL BOTTOM SHEET ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsightDetailBottomSheet(
    insight: SmartInsightItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(insight.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(insight.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDarkColor)
                    Surface(shape = RoundedCornerShape(8.dp), color = PurpleSoft) {
                        Text(insight.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = PurplePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Açıklama
            Text("📘 Detaylı Açıklama", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PurplePrimary)
            Text(insight.fullExplanation, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = TextDarkColor)

            Spacer(modifier = Modifier.height(12.dp))

            // AI Yorumu
            Text("🤖 AI Uzman Yorumu", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PurplePrimary)
            Text(insight.aiCommentary, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = TextDarkColor)

            Spacer(modifier = Modifier.height(12.dp))

            // Risk Analizi
            Text("🛡️ Risk Analizi & Etki", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PurplePrimary)
            Text(insight.riskAnalysis, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = TextDarkColor)

            Spacer(modifier = Modifier.height(12.dp))

            // Olası Senaryolar
            Surface(shape = RoundedCornerShape(14.dp), color = ScreenBg, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔮 Olası Senaryolar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDarkColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Boğa Senaryosu: ${insight.scenarioBullish}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = GreenPositive)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("• Ayı Senaryosu: ${insight.scenarioBearish}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSubColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Önerilen Aksiyon Button
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("🎯 Önerilen Aksiyon: ${insight.recommendedAction}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
