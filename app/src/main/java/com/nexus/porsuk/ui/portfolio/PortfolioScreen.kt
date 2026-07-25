package com.nexus.porsuk.ui.portfolio

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

// Design System Tokens (Light Theme Aesthetic with Purple #6C4CF1 Accent)
private val PurpleAccent = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val LightSurfaceBg = Color(0xFFF8F9FD)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)
private val BullishGreen = Color(0xFF10B981)
private val BearishRed = Color(0xFFEF4444)

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
    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())

    var isBalanceVisible by remember { mutableStateOf(true) }
    var selectedTimeframeIndex by remember { mutableIntStateOf(0) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            PortfolioTopBar(
                onSearchClick = {},
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Portföy Özeti (Hero Card)
            item(key = "portfolio_summary_hero") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    PortfolioSummaryHeroCard(
                        totalBalance = totalBalance,
                        totalChange = totalChange,
                        isBalanceVisible = isBalanceVisible,
                        onToggleVisibility = { isBalanceVisible = !isBalanceVisible },
                        numberFormat = numberFormat
                    )
                }
            }

            // 2 & 3. Varlık Dağılımı & Sektör Dağılımı (Side-by-Side Donut Charts Grid)
            item(key = "allocations_donuts_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AllocationsDonutGridSection(onAnalysisClick = onAnalysisClick)
                }
            }

            // 4. Portföy Performansı (Performance Animated Line Chart)
            item(key = "portfolio_performance_chart") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    PortfolioPerformanceChartCard(
                        selectedTimeframe = selectedTimeframeIndex,
                        onTimeframeSelected = { selectedTimeframeIndex = it }
                    )
                }
            }

            // 5. Varlıklarım (Holdings Table List)
            item(key = "my_holdings_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    MyHoldingsSection(onStockClick = onStockClick)
                }
            }

            // 6. AI Portföy Doktoru & 7. Oracle Önerisi
            item(key = "ai_doctor_and_oracle_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    AiDoctorAndOracleSection(onAnalysisClick = onAnalysisClick)
                }
            }

            // 8. Hızlı İşlemler & 9. Raporlar (Quick Actions Grid)
            item(key = "quick_actions_and_reports") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
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

// ── 1. ÜST BAR (Top Bar) ──
@Composable
private fun PortfolioTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSurfaceBg)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🦩", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    "PORSUK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = TextDark
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = PurpleAccent
                )
            }
        }

        // Title
        Text(
            "Portföy",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDark
        )

        // Actions
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = TextDark)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PurpleAccent)
            }
        }
    }
}

// ── 1. PORTFÖY ÖZETİ (Portfolio Summary Hero Card) ──
@Composable
private fun PortfolioSummaryHeroCard(
    totalBalance: Double,
    totalChange: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    numberFormat: String
) {
    val displayValue = remember(totalBalance, isBalanceVisible) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Title + Eye Icon + Daily Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Toplam Portföy Değeri",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
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
                    color = LightSurfaceBg,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

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
                            fontSize = 28.sp
                        ),
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "^ %2,35 (₺28.750,45) Bugün",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                        color = BullishGreen
                    )
                }

                // Mini Sparkline Graph
                val mockSparkValues = remember { listOf(40f, 42f, 41f, 45f, 44f, 48f, 47f, 52f, 50f, 56f, 54f, 60f) }
                Sparkline(
                    values = mockSparkValues,
                    color = BullishGreen,
                    modifier = Modifier
                        .width(100.dp)
                        .height(42.dp),
                    filled = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(16.dp))

            // 4 Metrics Grid (Toplam Getiri, Getiri (Yıl), Portföy Risk Skoru, AI Sağlık Puanı)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumnItem(title = "Toplam Getiri", value = "₺245.750,45", percentage = "^ %24,36", isPositive = true)
                MetricColumnItem(title = "Getiri (Yıl)", value = "₺325.420,15", percentage = "^ %32,68", isPositive = true)
                GaugeColumnItem(title = "Risk Skoru", score = "72", label = "Orta", color = Color(0xFFF59E0B))
                GaugeColumnItem(title = "AI Sağlık Puanı", score = "85", label = "İyi", color = BullishGreen)
            }
        }
    }
}

@Composable
private fun MetricColumnItem(title: String, value: String, percentage: String, isPositive: Boolean) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextDark)
        Text(percentage, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = if (isPositive) BullishGreen else BearishRed)
    }
}

@Composable
private fun GaugeColumnItem(title: String, score: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.dp.toPx()
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
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(score, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp), color = TextDark)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color, fontFamily = Manrope)
        }
    }
}

// ── 2 & 3. VARLIK DAĞILIMI & SEKTÖR DAĞILIMI (Side-by-Side Donut Grid) ──
@Composable
private fun AllocationsDonutGridSection(onAnalysisClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card 1: Varlık Dağılımı
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Varlık Dağılımı", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

                Spacer(modifier = Modifier.height(12.dp))

                // Donut Chart Canvas
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        var startAngle = -90f

                        val segments = listOf(
                            62.5f to PurpleAccent,
                            18.3f to Color(0xFF3B82F6),
                            12.4f to BullishGreen,
                            4.8f to Color(0xFFF59E0B),
                            2.0f to Color(0xFFCBD5E1)
                        )

                        segments.forEach { (pct, color) ->
                            val sweep = (pct / 100f) * 360f
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Toplam", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                        Text("%100", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp), color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend List
                DonutLegendRow(color = PurpleAccent, label = "Hisse Senedi", value = "%62,5")
                DonutLegendRow(color = Color(0xFF3B82F6), label = "Nakit", value = "%18,3")
                DonutLegendRow(color = BullishGreen, label = "Yabancı Hisse", value = "%12,4")
                DonutLegendRow(color = Color(0xFFF59E0B), label = "Fon", value = "%4,8")
                DonutLegendRow(color = Color(0xFFCBD5E1), label = "Diğer", value = "%2,0")

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAnalysisClick),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurfaceBg,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Detaylı Dağılım", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = PurpleAccent)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(9.dp))
                    }
                }
            }
        }

        // Card 2: Sektör Dağılımı
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Sektör Dağılımı", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

                Spacer(modifier = Modifier.height(12.dp))

                // Donut Chart Canvas
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        var startAngle = -90f

                        val segments = listOf(
                            28.4f to PurpleAccent,
                            18.7f to Color(0xFF3B82F6),
                            12.1f to BullishGreen,
                            11.3f to Color(0xFFF59E0B),
                            8.6f to Color(0xFFEC4899),
                            20.9f to Color(0xFFCBD5E1)
                        )

                        segments.forEach { (pct, color) ->
                            val sweep = (pct / 100f) * 360f
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Toplam", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                        Text("%100", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp), color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend List
                DonutLegendRow(color = PurpleAccent, label = "Bankacılık", value = "%28,4")
                DonutLegendRow(color = Color(0xFF3B82F6), label = "Teknoloji", value = "%18,7")
                DonutLegendRow(color = BullishGreen, label = "Savunma", value = "%12,1")
                DonutLegendRow(color = Color(0xFFF59E0B), label = "Holding", value = "%11,3")
                DonutLegendRow(color = Color(0xFFEC4899), label = "Enerji", value = "%8,6")

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAnalysisClick),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurfaceBg,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sektör Analizi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = PurpleAccent)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(9.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutLegendRow(color: Color, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
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
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope), color = TextDark)
        }
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = IBMPlexMono), color = TextDark)
    }
}

// ── 4. PORTFÖY PERFORMANSI (Performance Animated Line Chart Card) ──
@Composable
private fun PortfolioPerformanceChartCard(
    selectedTimeframe: Int,
    onTimeframeSelected: (Int) -> Unit
) {
    val timeframes = remember { listOf("Günlük", "Haftalık", "Aylık", "Yılbaşı", "1Y", "Tümü") }
    val timeLabels = remember { listOf("09:30", "11:00", "12:30", "14:00", "15:30", "17:00", "18:00") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📈", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Portföy Performansı",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
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
                        color = if (isSelected) PurpleSoftBg else LightSurfaceBg,
                        border = BorderStroke(1.dp, if (isSelected) PurpleAccent else BorderColor),
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
                                color = if (isSelected) PurpleAccent else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Animated Performance Line Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val points = listOf(
                        Offset(0f, height * 0.7f),
                        Offset(width * 0.15f, height * 0.55f),
                        Offset(width * 0.3f, height * 0.65f),
                        Offset(width * 0.45f, height * 0.4f),
                        Offset(width * 0.6f, height * 0.48f),
                        Offset(width * 0.75f, height * 0.25f),
                        Offset(width * 0.9f, height * 0.35f),
                        Offset(width, height * 0.15f)
                    )

                    val path = Path()
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        path.cubicTo(
                            (prev.x + curr.x) / 2, prev.y,
                            (prev.x + curr.x) / 2, curr.y,
                            curr.x, curr.y
                        )
                    }

                    // Gradient fill below line
                    val fillPath = Path()
                    fillPath.addPath(path)
                    fillPath.lineTo(width, height)
                    fillPath.lineTo(0f, height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(PurpleAccent.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )

                    drawPath(
                        path = path,
                        color = PurpleAccent,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Current Peak Value Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PurpleAccent,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                ) {
                    Text(
                        "1.248M",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, fontFamily = IBMPlexMono),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                timeLabels.forEach { label ->
                    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = IBMPlexMono), color = TextSecondary)
                }
            }
        }
    }
}

// ── 5. VARLIKLARIM (Holdings Table List) ──
@Composable
private fun MyHoldingsSection(onStockClick: (String, String) -> Unit) {
    val holdings = remember {
        listOf(
            HoldingItem("THYAO", "Türk Hava Yolları", "120", "₺52.680,00", "₺439,00", "^ %1,42", "₺735,60", "^ %18,75", "₺8.325,60", true),
            HoldingItem("ASELS", "Aselsan", "250", "₺56.750,00", "₺227,00", "^ %2,15", "₺1.192,50", "^ %24,36", "₺11.125,40", true),
            HoldingItem("KCHOL", "Koç Holding", "80", "₺14.592,00", "₺182,40", "^ %0,66", "₺95,60", "^ %12,48", "₺1.620,80", true),
            HoldingItem("BIMAS", "BİM Birleşik Mağazalar", "45", "₺12.420,00", "₺276,00", "v %-0,48", "-₺59,40", "^ %8,21", "₺942,60", false),
            HoldingItem("USD/TRY", "Döviz", "1.200 USD", "₺38.760,00", "₺32,30", "^ %0,35", "₺134,40", "^ %6,15", "₺2.246,80", true)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💼", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
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
                Text("Varlık", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = Manrope), color = TextSecondary, modifier = Modifier.weight(1.2f))
                Text("Adet", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = Manrope), color = TextSecondary, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                Text("Değer", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = Manrope), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                Text("Günlük Değişim", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = Manrope), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                Text("Toplam Getiri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = Manrope), color = TextSecondary, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))

            holdings.forEach { item ->
                HoldingRowItem(item = item, onClick = { onStockClick(item.symbol, "BIST") })
                HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(14.dp),
                color = LightSurfaceBg,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tüm Varlıkları Gör", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PurpleAccent, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(10.dp))
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
    val dailyColor = if (item.isDailyPositive) BullishGreen else BearishRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Symbol & Logo
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = PurpleAccent)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(item.symbol, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            Text(item.totalReturnPct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = BullishGreen)
            Text(item.totalReturnAmt, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = BullishGreen)
        }
    }
}

// ── 6 & 7. AI PORTFÖY DOKTORU & ORACLE ÖNERİSİ ──
@Composable
private fun AiDoctorAndOracleSection(onAnalysisClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Portföy Doktoru Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🩺", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Portföy Doktoru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                    }

                    Button(
                        onClick = onAnalysisClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Portföyü Tara", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Doctor Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DoctorMetricItem("Portföy Sağlığı", "88/100", BullishGreen)
                    DoctorMetricItem("Risk", "Dengeli", Color(0xFFF59E0B))
                    DoctorMetricItem("Çeşitlilik", "Yüksek", BullishGreen)
                    DoctorMetricItem("Volatilite", "%14.2", PurpleAccent)
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Text("💡 AI Doktor Yorumu:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Teknoloji ağırlığınız %18.7 ile ideal seviyede. Savunma ve yenilenebilir enerji sektörlerini %5 artırarak portföy dengesini yükseltebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                    color = TextDark
                )
            }
        }

        // Oracle Önerisi Purple Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp)),
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
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔮", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Oracle Bugünü Yorumladı", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
                        Text(
                            "Piyasada pozitif momentum devam ediyor. Portföyünüz için rebalans zamanı uygun.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("%87", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color(0xFFC084FC))
                        Text("Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorMetricItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary, fontFamily = Manrope)
        Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = IBMPlexMono), color = color)
    }
}

// ── 8. HIZLI İŞLEMLER & 9. RAPORLAR ──
@Composable
private fun QuickActionsAndReportsSection(
    onLedgerClick: () -> Unit,
    onAnalysisClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp, fontFamily = Manrope),
                color = TextDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
