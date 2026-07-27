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
import androidx.compose.runtime.*
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

    var isBalanceVisible by remember { mutableStateOf(true) }
    var selectedTimeframeIndex by remember { mutableIntStateOf(0) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // 24dp Card Spacing Specification
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
                        numberFormat = numberFormat
                    )
                }
            }

            // 2. Varlık Dağılımı & 3. Sektör Dağılımı (Side-by-Side Donut Grid)
            item(key = "allocations_donuts_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(550)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AllocationsDonutGridSection(onAnalysisClick = onAnalysisClick)
                }
            }

            // 4. Portföy Performansı (Chart & Hover Touch Tooltip)
            item(key = "portfolio_performance_chart") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    PortfolioPerformanceChartCard(
                        selectedTimeframe = selectedTimeframeIndex,
                        onTimeframeSelected = { selectedTimeframeIndex = it }
                    )
                }
            }

            // 5. Varlıklarım Tablosu
            item(key = "my_holdings_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(850)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MyHoldingsSection(onStockClick = onStockClick)
                }
            }

            // 6. AI Portföy Doktoru & 7. Oracle Kartı
            item(key = "ai_doctor_and_oracle_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AiDoctorAndOracleSection(onAnalysisClick = onAnalysisClick)
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
    numberFormat: String
) {
    val displayValue = remember(totalBalance, isBalanceVisible) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
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
                            text = "^ %2,35 (₺28.750,45) Bugün",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                            color = SuccessGreen
                        )
                    }
                }

                // Mini Sparkline Graph (Enlarged size)
                val mockSparkValues = remember { listOf(40f, 42f, 41f, 45f, 44f, 48f, 47f, 52f, 50f, 56f, 54f, 62f) }
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
                MetricColumnItem(title = "Toplam Getiri", value = "₺245.750,45", percentage = "^ %24,36", isPositive = true)
                MetricColumnItem(title = "Getiri (Yıl)", value = "₺325.420,15", percentage = "^ %32,68", isPositive = true)
                
                // Risk Skoru Gauge
                ProminentGaugeColumnItem(
                    title = "Risk Skoru",
                    score = 72,
                    maxScore = 100,
                    label = "Orta",
                    color = WarningOrange
                )

                // AI Sağlık Puanı Gauge
                ProminentGaugeColumnItem(
                    title = "AI Sağlık Puanı",
                    score = 85,
                    maxScore = 100,
                    label = "İyi",
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

// ── 3 & 4. VARLIK DAĞILIMI VE SEKTÖR DAĞILIMI (Side-by-Side Donut Grid) ──
@Composable
private fun AllocationsDonutGridSection(onAnalysisClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Varlık Dağılımı Card
        AssetAllocationCard(
            modifier = Modifier.weight(1f),
            onAnalysisClick = onAnalysisClick
        )

        // Sektör Dağılımı Card
        SectorAllocationCard(
            modifier = Modifier.weight(1f),
            onAnalysisClick = onAnalysisClick
        )
    }
}

@Composable
private fun AssetAllocationCard(
    modifier: Modifier = Modifier,
    onAnalysisClick: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animated = true
    }

    val segments = remember {
        listOf(
            AssetSegment("Hisse Senedi", 62.5f, "₺153.500", PrimaryPurple),
            AssetSegment("Nakit", 18.3f, "₺44.950", Color(0xFF3B82F6)),
            AssetSegment("Yabancı Hisse", 12.4f, "₺30.450", SuccessGreen),
            AssetSegment("Fon", 4.8f, "₺11.780", WarningOrange),
            AssetSegment("Diğer", 2.0f, "₺4.910", Color(0xFF94A3B8))
        )
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animated) 1.0f else 0.0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "asset_donut_anim"
    )

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Varlık Dağılımı", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

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

                    segments.forEachIndexed { idx, seg ->
                        val sweep = (seg.pct / 100f) * 360f * animProgress
                        val isSelected = idx == selectedIndex
                        drawArc(
                            color = if (isSelected) seg.color else seg.color.copy(alpha = if (selectedIndex == -1) 1.0f else 0.4f),
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

            // Interactive Segment Info Box (Tıklanınca Gösterilen Yapı)
            if (selectedIndex in segments.indices) {
                val sel = segments[selectedIndex]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = sel.color.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, sel.color.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sel.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = sel.color)
                        Text("%${sel.pct} • ${sel.amount}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                    }
                }
            }

            // Segment Legend List
            segments.forEachIndexed { idx, seg ->
                DonutLegendRow(
                    color = seg.color,
                    label = seg.label,
                    value = "%${seg.pct}",
                    isSelected = idx == selectedIndex,
                    onClick = { selectedIndex = if (selectedIndex == idx) -1 else idx }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAnalysisClick),
                shape = RoundedCornerShape(14.dp),
                color = LightBackground,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylı Dağılım", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = PrimaryPurple)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

private data class AssetSegment(val label: String, val pct: Float, val amount: String, val color: Color)

@Composable
private fun SectorAllocationCard(
    modifier: Modifier = Modifier,
    onAnalysisClick: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) } // Default selected sector
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animated = true
    }

    val sectors = remember {
        listOf(
            SectorSegment("Teknoloji", 28.4f, "Teknoloji ağırlığınız ideal seviyede.", PrimaryPurple),
            SectorSegment("Bankacılık", 18.7f, "Bankacılık sektörü güçlü temettü sağlıyor.", Color(0xFF3B82F6)),
            SectorSegment("Savunma", 12.1f, "Savunma sanayi sipariş momentumu yüksek.", SuccessGreen),
            SectorSegment("Holding", 11.3f, "Holding portföy çeşitliliğinizi destekliyor.", WarningOrange),
            SectorSegment("Enerji", 8.6f, "Yenilenebilir enerji potansiyeli yüksek.", Color(0xFFEC4899))
        )
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
    onTimeframeSelected: (Int) -> Unit
) {
    val timeframes = remember { listOf("Günlük", "Haftalık", "Aylık", "Yılbaşı", "1Y", "Tümü") }
    val timeLabels = remember { listOf("09:30", "11:00", "12:30", "14:00", "15:30", "17:00", "18:00") }

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
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    touchOffset = offset
                                    val approxVal = 1050000 + ((1.0f - (offset.y / size.height)) * 235450).toInt()
                                    hoveredPrice = "₺${String.format("%,d", approxVal)}"
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val points = listOf(
                        Offset(0f, height * 0.75f),
                        Offset(width * 0.15f, height * 0.60f),
                        Offset(width * 0.30f, height * 0.68f),
                        Offset(width * 0.45f, height * 0.38f),
                        Offset(width * 0.60f, height * 0.45f),
                        Offset(width * 0.75f, height * 0.20f),
                        Offset(width * 0.90f, height * 0.30f),
                        Offset(width, height * 0.10f)
                    )

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

            // Time Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                timeLabels.forEach { label ->
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
                ChartSummaryMetric("Minimum", "₺1.050.000", ErrorRed)
                ChartSummaryMetric("Maksimum", "₺1.285.450", SuccessGreen)
                ChartSummaryMetric("Ortalama", "₺1.168.200", PrimaryPurple)
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
                HoldingRowItem(item = item, onClick = { onStockClick(item.symbol, "BIST") })
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
private fun AiDoctorAndOracleSection(onAnalysisClick: () -> Unit) {
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

                    // AI Confidence Badge (%88 Güven)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleSoftBg,
                        border = BorderStroke(1.dp, PrimaryPurple.copy(0.3f))
                    ) {
                        Text(
                            "%88 Güven",
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
                    DoctorMetricItem("Portföy Sağlığı", "88/100", SuccessGreen)
                    DoctorMetricItem("Risk", "Dengeli", WarningOrange)
                    DoctorMetricItem("Çeşitlilik", "Yüksek", SuccessGreen)
                    DoctorMetricItem("Volatilite", "%14.2", PrimaryPurple)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(14.dp))

                // Bullet Point Structured AI Commentary (7th Requirement Spec)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiBulletItem("✅", "Güçlü Yanlar", "Teknoloji ve bankacılık sektör ağırlığı yüksek verimlilik sunuyor.")
                    AiBulletItem("⚠", "Riskler", "Savunma ve enerji sektörlerinde dönemsel dalgalanma riski mevcut.")
                    AiBulletItem("💡", "Öneriler", "Yenilenebilir enerjiye %5 ilave ağırlık vererek Sharpe oranını %12 artırabilirsiniz.")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAnalysisClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Portföyü Tara & Rebalans Yap", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
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
                        "Piyasada pozitif momentum devam ediyor. Portföyünüz için rebalans zamanı uygun.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Circular Progress Arc for %87 Güven (8th Requirement Spec)
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
                                sweepAngle = confidenceSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Text("%87", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = IBMPlexMono), color = Color(0xFFC084FC))
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
