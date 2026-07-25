package com.nexus.porsuk.ui.analysis

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

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
private val RiskOrange = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onStockClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToSettings: () -> Unit = {},
    onCreateBasket: () -> Unit = {},
    onNavigateToDuel: (String, String) -> Unit = { _, _ -> }
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            AnalysisTopBar(
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
            // 2. Yatay Sekmeler (Scrollable Tabs)
            item(key = "analysis_tabs") {
                AnalysisTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // 1. AI Genel Değerlendirme (AI General Overview Hero Card)
            item(key = "ai_general_overview_hero") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    AiGeneralOverviewCard(
                        onDetailClick = { viewModel.runPortfolioHealthCheck() }
                    )
                }
            }

            // 2. Hızlı Analiz Modülleri (Quick Analysis Modules Row)
            item(key = "quick_analysis_modules") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    QuickAnalysisModulesRow(
                        onModuleClick = { viewModel.runStockScreener("Genel Analiz") }
                    )
                }
            }

            // 3. Analiz Modülleri (Detailed Analysis Modules List Card)
            item(key = "detailed_analysis_modules") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    DetailedAnalysisModulesSection(
                        onModuleClick = { viewModel.runStockScreener(it) }
                    )
                }
            }

            // 4, 5, 6. Piyasa Nabzı & Korku/Açgözlülük & Volatilite (3 Cards Triple Grid)
            item(key = "market_pulse_and_fear_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    TripleGaugesGridSection()
                }
            }

            // 7. AI Accuracy (AI Accuracy Purple Card)
            item(key = "ai_accuracy_card") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    AiAccuracyCardSection()
                }
            }

            // 8 & 9. AI Eylem Merkezi & Senaryo Merkezi (AI Action & Scenario Hub Card)
            item(key = "ai_action_and_scenario_hub") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                ) {
                    AiActionAndScenarioHubCard(
                        onRunAnalysis = { viewModel.runPortfolioHealthCheck() },
                        onRunOracle = { viewModel.generateRebalanceReport() }
                    )
                }
            }
        }
    }
}

// ── ÜST BAR (Top Bar) ──
@Composable
private fun AnalysisTopBar(
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

        Text(
            "Analiz",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDark
        )

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

// ── YATAY SEKMELER (Scrollable Tabs) ──
@Composable
private fun AnalysisTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = remember {
        listOf("Genel Bakış", "Teknik", "Temel", "Haber", "Makro", "Portföy", "Oracle Araçları")
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = PurpleAccent,
        edgePadding = 20.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = PurpleAccent
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontFamily = Manrope
                        ),
                        color = if (isSelected) PurpleAccent else TextSecondary
                    )
                }
            )
        }
    }
}

// ── 1. AI GENEL DEĞERLENDİRME (AI General Overview Hero Card) ──
@Composable
private fun AiGeneralOverviewCard(onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "AI GENEL DEĞERLENDİRME",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = PurpleAccent,
                        fontFamily = Manrope
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onDetailClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylı Rapor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Ring Gauge (78/100 AI Piyasa Skoru)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .weight(0.9f),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        drawArc(
                            color = PurpleSoftBg,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )
                        drawArc(
                            color = PurpleAccent,
                            startAngle = -90f,
                            sweepAngle = 280f, // 78%
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("78", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text("/100", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                        Text("AI Piyasa Skoru", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
                        Text("Pozitif", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = BullishGreen)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Oracle Comment & 3 Metric Pills
                Column(modifier = Modifier.weight(1.3f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Oracle Yorumu", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Piyasada pozitif momentum devam ediyor. Bankacılık ve savunma sektörlerinde güçlü görünüm sürüyor.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextDark,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3 Metric Pills Row (AI Güven, Risk Seviyesi, Piyasa Trendi)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MetricPillCard("AI Güven", "85%", BullishGreen, modifier = Modifier.weight(1f))
                        MetricPillCard("Risk Seviyesi", "Düşük", BullishGreen, modifier = Modifier.weight(1f))
                        MetricPillCard("Piyasa Trendi", "Yükseliş ↗", BullishGreen, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPillCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = LightSurfaceBg,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary, maxLines = 1)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, fontFamily = IBMPlexMono), color = color, maxLines = 1)
        }
    }
}

// ── 2. HIZLI ANALİZ MODÜLLERİ (Quick Analysis Modules Row) ──
@Composable
private fun QuickAnalysisModulesRow(onModuleClick: () -> Unit) {
    val modules = remember {
        listOf(
            QuickModuleItem("Teknik Analiz", "📈"),
            QuickModuleItem("Temel Analiz", "📊"),
            QuickModuleItem("Haber Analizi", "📰"),
            QuickModuleItem("Makro Analiz", "🌐"),
            QuickModuleItem("Portföy Etki", "🍕"),
            QuickModuleItem("Senaryo Simülasyonu", "⚙️")
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(modules, key = { it.title }) { item ->
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .clickable(onClick = onModuleClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = PurpleSoftBg,
                    border = BorderStroke(1.dp, Color(0xFFD8CEFF)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.iconEmoji, fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = Manrope),
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class QuickModuleItem(val title: String, val iconEmoji: String)

// ── 3. ANALİZ MODÜLLERİ (Detailed Analysis Modules List Card) ──
@Composable
private fun DetailedAnalysisModulesSection(onModuleClick: (String) -> Unit) {
    val detailedModules = remember {
        listOf(
            DetailedModuleItem("Teknik Analiz", "RSI, MACD, EMA ve daha fazlası", "75 /100", BullishGreen, "📈", listOf(40f, 45f, 42f, 48f, 50f)),
            DetailedModuleItem("Temel Analiz", "F/K, PD/DD, ROE, Bilanço ve daha fazlası", "80 /100", BullishGreen, "📊", listOf(50f, 52f, 55f, 58f, 60f)),
            DetailedModuleItem("Haber Analizi", "Haberlerin hisse üzerindeki etkisi", "78 /100", RiskOrange, "📰", listOf(30f, 32f, 31f, 35f, 38f)),
            DetailedModuleItem("Makro Analiz", "Döviz, faiz, enflasyon ve endeksler", "68 /100", RiskOrange, "🌐", listOf(60f, 59f, 62f, 65f, 68f)),
            DetailedModuleItem("Portföy Etki Analizi", "Portföyünüz üzerindeki olası etkiler", "72 /100", BullishGreen, "🍕", listOf(40f, 43f, 45f, 48f, 52f)),
            DetailedModuleItem("Senaryo Simülasyonu", "Faiz, kur, enflasyon senaryoları", "Yeni", PurpleAccent, "⚙️", listOf(50f, 55f, 52f, 58f, 60f))
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
            Text(
                "ANALİZ MODÜLLERİ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = PurpleAccent,
                fontFamily = Manrope
            )

            Spacer(modifier = Modifier.height(14.dp))

            detailedModules.forEach { item ->
                DetailedModuleRow(item = item, onClick = { onModuleClick(item.title) })
                HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))
            }
        }
    }
}

private data class DetailedModuleItem(
    val title: String,
    val description: String,
    val scoreBadge: String,
    val scoreColor: Color,
    val iconEmoji: String,
    val sparkValues: List<Float>
)

@Composable
private fun DetailedModuleRow(item: DetailedModuleItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = PurpleSoftBg,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.iconEmoji, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1.3f)) {
            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Text(item.description, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = item.scoreColor.copy(alpha = 0.12f)
        ) {
            Text(
                item.scoreBadge,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp, fontFamily = IBMPlexMono),
                color = item.scoreColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Sparkline(
            values = item.sparkValues,
            color = item.scoreColor,
            modifier = Modifier
                .width(60.dp)
                .height(24.dp),
            filled = true
        )

        Spacer(modifier = Modifier.width(6.dp))

        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextSecondary.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
    }
}

// ── 4, 5, 6. PİYASA NABZI & KORKU/AÇGÖZLÜLÜK & VOLATİLİTE TRIPLE GRID ──
@Composable
private fun TripleGaugesGridSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Piyasa Nabzı
        GaugeCardItem(
            title = "PİYASA NABZI",
            score = "68",
            statusLabel = "Orta Pozitif",
            statusColor = BullishGreen,
            modifier = Modifier.weight(1f)
        )

        // Card 2: Korku & Açgözlülük
        GaugeCardItem(
            title = "KORKU & AÇGÖZLÜLÜK",
            score = "55",
            statusLabel = "Nötr",
            statusColor = RiskOrange,
            modifier = Modifier.weight(1f)
        )

        // Card 3: Volatilite Endeksi
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("VOLATİLİTE ENDEKSİ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp), color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Text("18,45", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 20.sp), color = TextDark)
                Text("Düşük", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = BullishGreen)

                Spacer(modifier = Modifier.height(8.dp))
                val vixValues = remember { listOf(22f, 20f, 19f, 18.45f) }
                Sparkline(
                    values = vixValues,
                    color = PurpleAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    filled = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = PurpleAccent)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}

@Composable
private fun GaugeCardItem(
    title: String,
    score: String,
    statusLabel: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp), color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            // Half-Arc Gauge Canvas
            Box(
                modifier = Modifier
                    .size(85.dp, 45.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 7.dp.toPx()
                    drawArc(
                        color = BearishRed,
                        startAngle = 180f,
                        sweepAngle = 60f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = RiskOrange,
                        startAngle = 240f,
                        sweepAngle = 60f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = BullishGreen,
                        startAngle = 300f,
                        sweepAngle = 60f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(score, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 18.sp), color = TextDark)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(statusLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = statusColor)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { }
            ) {
                Text("Detaylar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = PurpleAccent)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(8.dp))
            }
        }
    }
}

// ── 7. AI ACCURACY (AI Accuracy Purple Card) ──
@Composable
private fun AiAccuracyCardSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF512DA8), Color(0xFF6C4CF1))
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0x33FFFFFF),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Accuracy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
                    Text("Doğru tahmin oranlarımız ve öğrenme ilerlememiz", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = Color.White.copy(alpha = 0.85f))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text("Başarı Oranı", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.8f))
                    Text("81%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color.White)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text("Toplam Analiz", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.8f))
                    Text("3.281", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

// ── 8 & 9. AI EYLEM MERKEZİ & SENARYO MERKEZİ ──
@Composable
private fun AiActionAndScenarioHubCard(
    onRunAnalysis: () -> Unit,
    onRunOracle: () -> Unit
) {
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
            Text("🤖 AI Eylem & Senaryo Merkezi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "\"Bugün yeni alımlar için piyasa koşulları olumlu.\"",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp),
                color = PurpleAccent
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRunAnalysis,
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Analizi Başlat", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                Button(
                    onClick = onRunOracle,
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Oracle Çalıştır", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                Button(
                    onClick = onRunAnalysis,
                    modifier = Modifier.weight(1f).height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BullishGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Portföyü Tara", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            Text("⚙️ Senaryo Merkezi (Makro Simülasyonlar)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            // Scenario Chips
            val scenarios = remember {
                listOf("Faiz %5 artarsa", "Dolar 50 TL olursa", "Altın %10 yükselirse", "+ Yeni Senaryo")
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scenarios) { scenario ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightSurfaceBg,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            scenario,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = TextDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
