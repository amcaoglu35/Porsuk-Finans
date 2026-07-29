package com.nexus.porsuk.ui.portfolio

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import com.nexus.porsuk.data.remote.PortfolioDoctorMetrics
import com.nexus.porsuk.domain.model.PortfolioAsset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

// ── DESIGN SYSTEM COLOR PALETTE (Light Theme Specification) ──
private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFF44336)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: FinanceViewModel,
    onStockClick: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onLedgerClick: () -> Unit = {},
    onAnalysisClick: () -> Unit = {}
) {
    val totalBalance by viewModel.totalBalanceTry.collectAsState()
    val totalChange by viewModel.totalChangePercent.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val consolidatedHoldings by viewModel.consolidatedHoldings.collectAsState()
    val portfolioChartData by viewModel.portfolioChartData.collectAsState()
    val selectedTimeframe by viewModel.selectedChartTimeframe.collectAsState()
    val riskMetrics by viewModel.portfolioRiskMetrics.collectAsState()
    val aiInsight by viewModel.aiPortfolioInsight.collectAsState()

    var isBalanceVisible by remember { mutableStateOf(true) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        if (portfolioChartData.isEmpty()) {
            viewModel.updateChartTimeframe(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightBackground,
        topBar = {
            PortfolioTopBar(
                onSearchClick = {},
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshAllData() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Portföy Özeti Kartı
                item(key = "portfolio_summary_hero") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        PortfolioSummaryHeroCard(
                            totalBalance = totalBalance,
                            totalChange = totalChange,
                            isBalanceVisible = isBalanceVisible,
                            onToggleVisibility = { isBalanceVisible = !isBalanceVisible },
                            numberFormat = numberFormat,
                            riskMetrics = riskMetrics
                        )
                    }
                }

                // 2. Varlık Dağılımı & 3. Sektör Dağılımı (Side-by-Side Donut Grid)
                item(key = "allocations_donuts_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(550)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        AllocationsDonutGridSection(
                            onAnalysisClick = onAnalysisClick,
                            riskMetrics = riskMetrics
                        )
                    }
                }

                // 4. Portföy Performansı (Chart & Hover Touch Tooltip)
                item(key = "portfolio_performance_chart") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        PortfolioPerformanceChartCard(
                            selectedTimeframe = selectedTimeframe,
                            onTimeframeSelected = { viewModel.updateChartTimeframe(it) },
                            chartData = portfolioChartData,
                            numberFormat = numberFormat
                        )
                    }
                }

                // 5. Varlıklarım Tablosu
                item(key = "my_holdings_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(850)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        MyHoldingsSection(
                            onStockClick = onStockClick,
                            holdings = consolidatedHoldings,
                            numberFormat = numberFormat
                        )
                    }
                }

                // 6. AI Portföy Doktoru & 7. Oracle Kartı
                item(key = "ai_doctor_and_oracle_section") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        AiDoctorAndOracleSection(
                            onAnalysisClick = onAnalysisClick,
                            riskMetrics = riskMetrics,
                            aiInsight = aiInsight,
                            onGenerateInsight = { viewModel.generateAiPortfolioInsight() }
                        )
                    }
                }

                // 8. Hızlı İşlemler
                item(key = "quick_actions_and_reports") {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(1150)) + slideInVertically(initialOffsetY = { 40 })
                    ) {
                        QuickActionsAndReportsSection(
                            onLedgerClick = onLedgerClick,
                            onAnalysisClick = onAnalysisClick
                        )
                    }
                }
            }
        }
    }
}

// ── 1. SAYFA BAŞLIĞI (Top Bar with Subtitle) ──
@Composable
private fun PortfolioTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBackground)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo & Brand
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🦩", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sepetlerim",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        fontFamily = Manrope
                    ),
                    color = TextDark
                )
                Text(
                    text = "Kendi yatırım sepetlerini ve performansını yönet.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Manrope
                    ),
                    color = TextSecondary
                )
            }
        }

        // Actions (Search & Notification)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = TextDark, modifier = Modifier.size(18.dp))
            }
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurpleSoftBg)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PrimaryPurple, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ── 2. PORTFÖY ÖZETİ KARTI (Hero Summary Card with Animated Progress Gauges) ──
@Composable
private fun PortfolioSummaryHeroCard(
    totalBalance: Double,
    totalChange: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    numberFormat: String,
    riskMetrics: PortfolioDoctorMetrics?
) {
    val displayValue = remember(totalBalance, isBalanceVisible) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
    }

    val dailyValueStr = remember(totalChange, riskMetrics, numberFormat) {
        val amt = (totalBalance * totalChange / 100.0)
        val sign = if (totalChange >= 0) "^" else "v"
        "$sign %${String.format("%.2f", totalChange)} (${CurrencyFormatter.formatTRY(amt, numberFormat)}) Bugün"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.04f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) { // 20dp Inner Padding Specification
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Toplam Portföy Değeri",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Gizle/Göster",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onToggleVisibility)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightBackground,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Günlük",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Balance Amount & Mini Sparkline Graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = IBMPlexMono,
                            fontSize = 30.sp
                        ),
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dailyValueStr,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                            color = if (totalChange >= 0) SuccessGreen else ErrorRed
                        )
                    }
                }

                // Mini Sparkline Graph (Enlarged size)
                val mockSparkValues = emptyList<Float>()
                Sparkline(
                    values = mockSparkValues,
                    color = SuccessGreen,
                    modifier = Modifier
                        .width(110.dp)
                        .height(48.dp),
                    filled = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(18.dp))

            // 4 Metrics Row (Including Prominent Animated Gauges for Risk & Health)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricColumnItem(
                    title = "Toplam Kar/Zarar",
                    value = CurrencyFormatter.formatTRY(totalBalance * totalChange / 100.0, numberFormat),
                    percentage = "${if(totalChange >=0) "^" else "v"} %${String.format("%.1f", totalChange)}",
                    isPositive = totalChange >= 0
                )
                
                // Risk Skoru Gauge
                ProminentGaugeColumnItem(
                    title = "Risk Skoru",
                    score = riskMetrics?.healthScore ?: 0,
                    maxScore = 100,
                    label = riskMetrics?.concentrationRisk?.substringBefore(" ") ?: "Nötr",
                    color = when {
                        (riskMetrics?.healthScore ?: 0) > 80 -> SuccessGreen
                        (riskMetrics?.healthScore ?: 0) > 50 -> WarningOrange
                        else -> ErrorRed
                    }
                )

                // AI Sağlık Puanı Gauge
                ProminentGaugeColumnItem(
                    title = "AI Sağlık",
                    score = riskMetrics?.healthScore ?: 0,
                    maxScore = 100,
                    label = "Detay",
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun MetricColumnItem(title: String, value: String, percentage: String, isPositive: Boolean) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 11.5.sp), color = TextDark)
        Text(percentage, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 10.5.sp), color = if (isPositive) SuccessGreen else ErrorRed)
    }
}

@Composable
private fun ProminentGaugeColumnItem(
    title: String,
    score: Int,
    maxScore: Int,
    label: String,
    color: Color
) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animated = true
    }

    val targetSweep = (score.toFloat() / maxScore.toFloat()) * 360f
    val sweepAngle by animateFloatAsState(
        targetValue = if (animated) targetSweep else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_sweep_$title"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp), // Larger gauge
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 4.dp.toPx()
                    drawArc(
                        color = color.copy(alpha = 0.18f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text("$score", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp), color = TextDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = color, fontFamily = Manrope)
        }
    }
}

// ── 3 & 4. DAĞILIM ANALİZLERİ (Side-by-Side Donut Grid) ──
@Composable
private fun AllocationsDonutGridSection(
    onAnalysisClick: () -> Unit,
    riskMetrics: PortfolioDoctorMetrics?
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Varlık Dağılımı Card
            DistributionCard(
                title = "Varlık Tipi",
                modifier = Modifier.weight(1f),
                segments = listOf(
                    AssetSegment("Hisse", 85f, "", PrimaryPurple),
                    AssetSegment("Nakit", 15f, "", Color(0xFF3B82F6))
                )
            )

            // Ülke Dağılımı Card
            DistributionCard(
                title = "Ülke Dağılımı",
                modifier = Modifier.weight(1f),
                segments = riskMetrics?.countryBreakdown?.map { (country, pct) ->
                    AssetSegment(country, pct.toFloat(), "", when(country) {
                        "ABD (US)" -> SuccessGreen
                        "Avrupa (EU)" -> WarningOrange
                        else -> PrimaryPurple
                    })
                } ?: emptyList()
            )
        }

        // Sektör Dağılımı Card (Full Width)
        SectorAllocationCard(
            modifier = Modifier.fillMaxWidth(),
            onAnalysisClick = onAnalysisClick,
            riskMetrics = riskMetrics
        )
    }
}

@Composable
private fun DistributionCard(
    title: String,
    segments: List<AssetSegment>,
    modifier: Modifier = Modifier
) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }

    val animProgress by animateFloatAsState(
        targetValue = if (animated) 1.0f else 0.0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "dist_donut_anim"
    )

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    var startAngle = -90f
                    segments.forEach { seg ->
                        val sweep = (seg.pct / 100f) * 360f * animProgress
                        drawArc(color = seg.color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, style = Stroke(width = strokeWidth))
                        startAngle += sweep
                    }
                }
                Text("%100", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
            }
            Spacer(modifier = Modifier.height(12.dp))
            segments.take(2).forEach { seg ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(seg.color))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(seg.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary, maxLines = 1)
                    }
                    Text("%${String.format("%.0f", seg.pct)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = TextDark)
                }
            }
        }
    }
}

private data class AssetSegment(val label: String, val pct: Float, val amount: String, val color: Color)

@Composable
private fun SectorAllocationCard(
    modifier: Modifier = Modifier,
    onAnalysisClick: () -> Unit,
    riskMetrics: PortfolioDoctorMetrics?
) {
    var selectedIndex by remember { mutableIntStateOf(0) } // Default selected sector
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animated = true
    }

    val sectors = remember(riskMetrics) {
        riskMetrics?.sectorBreakdown?.map { (sector, pct) ->
            val color = when {
                sector.contains("Teknoloji") -> PrimaryPurple
                sector.contains("Banka") -> Color(0xFF3B82F6)
                sector.contains("Savunma") -> SuccessGreen
                else -> WarningOrange
            }
            SectorSegment(sector, pct.toFloat(), "Ağırlık: %${String.format("%.1f", pct)}", color)
        } ?: emptyList()
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animated) 1.0f else 0.0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "sector_donut_anim"
    )

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Sektör Dağılımı", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

            Spacer(modifier = Modifier.height(14.dp))

            // Donut Chart Canvas (Enlarged 135dp radius)
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
                    var startAngle = -90f

                    sectors.forEachIndexed { idx, sec ->
                        val sweep = (sec.pct / 100f) * 360f * animProgress
                        val isSelected = idx == selectedIndex
                        drawArc(
                            color = if (isSelected) sec.color else sec.color.copy(alpha = 0.4f),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = if (isSelected) strokeWidth + 4.dp.toPx() else strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Toplam", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                    Text("%100", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp), color = TextDark)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sector Legend List
            sectors.forEachIndexed { idx, sec ->
                DonutLegendRow(
                    color = sec.color,
                    label = sec.label,
                    value = "%${sec.pct}",
                    isSelected = idx == selectedIndex,
                    onClick = { selectedIndex = idx }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive AI Sector Insight Commentary Box (4th Requirement Spec)
            val activeSector = sectors.getOrNull(selectedIndex) ?: sectors[0]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PurpleSoftBg,
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡 ", fontSize = 12.sp)
                    Text(
                        text = activeSector.aiInsight,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold),
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private data class SectorSegment(val label: String, val pct: Float, val aiInsight: String, val color: Color)

@Composable
private fun DonutLegendRow(
    color: Color,
    label: String,
    value: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontFamily = Manrope, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium), color = TextDark)
        }
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.5.sp, fontFamily = IBMPlexMono), color = TextDark)
    }
}

// ── 5. PORTFÖY PERFORMANSI (Performance Chart with Smooth Curve, Hover Point, & Min/Max/Avg Metrics) ──
@Composable
private fun PortfolioPerformanceChartCard(
    selectedTimeframe: Int,
    onTimeframeSelected: (Int) -> Unit,
    chartData: List<Double>,
    numberFormat: String
) {
    val timeframes = remember { listOf("1G", "1H", "1A", "3A", "6A", "1Y", "Tümü") }
    
    val displayData = remember(chartData) {
        if (chartData.isEmpty()) listOf(0.0, 0.0, 0.0, 0.0, 0.0) else chartData
    }

    var animState by remember { mutableStateOf(false) }
    LaunchedEffect(selectedTimeframe) {
        animState = false
        animState = true
    }

    val chartAnimProgress by animateFloatAsState(
        targetValue = if (animState) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "chart_line_draw_anim"
    )

    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var hoveredPrice by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📈", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Portföy Performansı",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = TextDark
                    )
                }

                if (hoveredPrice != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryPurple,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = hoveredPrice!!,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timeframe Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEachIndexed { idx, label ->
                    val isSelected = selectedTimeframe == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PurpleSoftBg else LightBackground,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else BorderColor),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTimeframeSelected(idx) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) PrimaryPurple else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smooth Curve Line Chart with Touch Interactive Hover Point
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(displayData) {
                            detectTapGestures(
                                onPress = { offset ->
                                    touchOffset = offset
                                    val idx = (offset.x / size.width * (displayData.size - 1)).toInt().coerceIn(0, displayData.size - 1)
                                    val valAtPoint = displayData[idx]
                                    hoveredPrice = CurrencyFormatter.formatTRY(valAtPoint, numberFormat)
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val minVal = displayData.minOrNull() ?: 0.0
                    val maxVal = displayData.maxOrNull() ?: 1.0
                    val range = (maxVal - minVal).coerceAtLeast(1.0)

                    val points = displayData.mapIndexed { idx, value ->
                        val x = if (displayData.size > 1) idx.toFloat() / (displayData.size - 1) * width else width / 2
                        val y = height - ((value - minVal) / range * height).toFloat().coerceIn(0f, height.toFloat())
                        Offset(x, y)
                    }

                    if (points.isEmpty()) return@Canvas

                    // Path drawing animation mask
                    val currentWidth = width * chartAnimProgress

                    val path = Path()
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        if (curr.x <= currentWidth) {
                            path.cubicTo(
                                (prev.x + curr.x) / 2, prev.y,
                                (prev.x + curr.x) / 2, curr.y,
                                curr.x, curr.y
                            )
                        }
                    }

                    // Gradient fill beneath curve
                    val fillPath = Path()
                    fillPath.addPath(path)
                    fillPath.lineTo(currentWidth, height)
                    fillPath.lineTo(0f, height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryPurple.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )

                    drawPath(
                        path = path,
                        color = PrimaryPurple,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Active Touch Point Circle
                    touchOffset?.let { offset ->
                        drawCircle(
                            color = PrimaryPurple,
                            radius = 6.dp.toPx(),
                            center = Offset(offset.x.coerceIn(0f, width), offset.y.coerceIn(0f, height))
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(offset.x.coerceIn(0f, width), offset.y.coerceIn(0f, height))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Labels Placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Açılış", "Kapanış").forEach { label ->
                    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = IBMPlexMono), color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(14.dp))

            // Min, Max, Avg Statistics (5th Requirement Spec)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChartSummaryMetric("Minimum", CurrencyFormatter.formatTRY(displayData.minOrNull() ?: 0.0, numberFormat), ErrorRed)
                ChartSummaryMetric("Maksimum", CurrencyFormatter.formatTRY(displayData.maxOrNull() ?: 0.0, numberFormat), SuccessGreen)
                ChartSummaryMetric("Ortalama", CurrencyFormatter.formatTRY(displayData.average(), numberFormat), PrimaryPurple)
            }
        }
    }
}

@Composable
private fun ChartSummaryMetric(title: String, value: String, color: Color) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = IBMPlexMono), color = color)
    }
}

// ── 6. VARLIKLARIM TABLOSU (Holdings Table with Spacious Rows & Larger Logos) ──
@Composable
private fun MyHoldingsSection(
    onStockClick: (String, String) -> Unit,
    holdings: List<PortfolioAsset>,
    numberFormat: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💼", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Varlıklarım",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Varlık", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.weight(1.3f))
                Text("Adet", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                Text("Değer", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                Text("Günlük", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                Text("Toplam Getiri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor)

            holdings.forEach { item ->
                val uiItem = HoldingItem(
                    symbol = item.symbol,
                    name = item.name,
                    qty = String.format("%.0f", item.quantity),
                    totalValue = CurrencyFormatter.formatTRY(item.totalValue, numberFormat),
                    avgCost = "Maliyet: ${CurrencyFormatter.formatTRY(item.averageCost, numberFormat)}",
                    dailyChangePct = "%0.0", // TODO: Add daily change to PortfolioAsset if needed
                    dailyChangeAmt = "₺0",
                    totalReturnPct = "%${String.format("%.1f", item.profitPercent)}",
                    totalReturnAmt = CurrencyFormatter.formatTRY(item.profitLoss, numberFormat),
                    isDailyPositive = true
                )
                HoldingRowItem(item = uiItem, onClick = { onStockClick(item.symbol, "BIST") })
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(14.dp),
                color = LightBackground,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tüm Varlıkları Gör", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryPurple, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

private data class HoldingItem(
    val symbol: String,
    val name: String,
    val qty: String,
    val totalValue: String,
    val avgCost: String,
    val dailyChangePct: String,
    val dailyChangeAmt: String,
    val totalReturnPct: String,
    val totalReturnAmt: String,
    val isDailyPositive: Boolean
)

@Composable
private fun HoldingRowItem(item: HoldingItem, onClick: () -> Unit) {
    val dailyColor = if (item.isDailyPositive) SuccessGreen else ErrorRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp), // Spacious row padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Enlarge Symbol Logo Badge (38dp)
        Row(modifier = Modifier.weight(1.3f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.symbol.take(2), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp), color = PrimaryPurple)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(item.symbol, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        // Qty
        Text(item.qty, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDark, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)

        // Value
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(item.totalValue, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDark)
            Text(item.avgCost, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = TextSecondary)
        }

        // Daily Change
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(item.dailyChangePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = dailyColor)
            Text(item.dailyChangeAmt, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = dailyColor)
        }

        // Total Return
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(item.totalReturnPct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = SuccessGreen)
            Text(item.totalReturnAmt, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = SuccessGreen)
        }
    }
}

// ── 7. AI PORTFÖY DOKTORU & 8. ORACLE KARTI ──
@Composable
private fun AiDoctorAndOracleSection(
    onAnalysisClick: () -> Unit,
    riskMetrics: PortfolioDoctorMetrics?,
    aiInsight: String?,
    onGenerateInsight: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // AI Portföy Doktoru Card (Structured Bullet Points & Confidence Badge)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🩺", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Portföy Doktoru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    }

                    // AI Confidence Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleSoftBg,
                        border = BorderStroke(1.dp, PrimaryPurple.copy(0.3f))
                    ) {
                        Text(
                            "%${riskMetrics?.healthScore ?: 0} Sağlık",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, fontFamily = IBMPlexMono),
                            color = PrimaryPurple,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Doctor Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DoctorMetricItem("Portföy Sağlığı", "${riskMetrics?.healthScore ?: 0}/100", SuccessGreen)
                    DoctorMetricItem("Risk", riskMetrics?.currencyRisk?.substringBefore(" ") ?: "Nötr", WarningOrange)
                    DoctorMetricItem("Çeşitlilik", if((riskMetrics?.sectorBreakdown?.size ?: 0) > 3) "Yüksek" else "Düşük", SuccessGreen)
                    DoctorMetricItem("Volatilite", "%${String.format("%.1f", riskMetrics?.volatilityPercent ?: 0.0)}", PrimaryPurple)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(14.dp))

                // AI Insight or Loading/Empty State
                if (aiInsight != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = aiInsight,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                            color = TextDark,
                            fontFamily = Manrope
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Portföyün için AI analizi oluşturulmadı.", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onGenerateInsight,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("AI Analizi Oluştur", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAnalysisClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple.copy(alpha = 0.1f), contentColor = PrimaryPurple),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Detaylı Analiz & Rebalans", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            }
        }

        // Oracle Önerisi Card (Pulsating Crystal Ball & Circular Confidence Ring)
        OracleHighlightCard()
    }
}

@Composable
private fun AiBulletItem(emoji: String, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(emoji, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = TextDark)
            Text(description, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp), color = TextSecondary)
        }
    }
}

@Composable
private fun OracleHighlightCard() {
    // Pulsating Glow Animation for Crystal Ball
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "oracle_pulse"
    )

    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animated = true
    }

    val confidenceSweep by animateFloatAsState(
        targetValue = if (animated) (87f / 100f) * 360f else 0f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "oracle_confidence_sweep"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = PrimaryPurple.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF200B54), Color(0xFF3B1578), Color(0xFF5B21B6))
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsating Crystal Ball
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔮", fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Oracle Bugünü Yorumladı", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Porsuk piyasaları senin için kokluyor... Gerçek zamanlı verilerle yakında burada!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Circular Progress Arc for %0 Güven (Waiting for API)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 3.5.dp.toPx()
                            drawArc(
                                color = Color.White.copy(alpha = 0.2f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth)
                            )
                            drawArc(
                                color = Color(0xFFC084FC),
                                startAngle = -90f,
                                sweepAngle = 0f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text("%0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = IBMPlexMono), color = Color(0xFFC084FC))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
private fun DoctorMetricItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, fontFamily = IBMPlexMono), color = color)
    }
}

// ── 9. HIZLI İŞLEMLER (Enlarged Cards with M3 Scale + Ripple Touch Feedback) ──
@Composable
private fun QuickActionsAndReportsSection(
    onLedgerClick: () -> Unit,
    onAnalysisClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı İşlemler", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTileCard(
                title = "Para Yatır / Çek",
                iconEmoji = "👛",
                onClick = onLedgerClick,
                modifier = Modifier.weight(1f)
            )
            ActionTileCard(
                title = "Portföy Analizi",
                iconEmoji = "📉",
                onClick = onAnalysisClick,
                modifier = Modifier.weight(1f)
            )
            ActionTileCard(
                title = "Vergi Simülasyonu",
                iconEmoji = "🧮",
                onClick = onAnalysisClick,
                modifier = Modifier.weight(1f)
            )
            ActionTileCard(
                title = "Raporlarım",
                iconEmoji = "📑",
                onClick = onAnalysisClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionTileCard(
    title: String,
    iconEmoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "action_tile_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(0.02f))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(40.dp) // Larger Material Icon container
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = Manrope),
                color = TextDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── PREVIEW SUPPORT ──
@Preview(showBackground = true)
@Composable
private fun PortfolioTopBarPreview() {
    PortfolioTopBar(onSearchClick = {}, onNotificationClick = {})
}
