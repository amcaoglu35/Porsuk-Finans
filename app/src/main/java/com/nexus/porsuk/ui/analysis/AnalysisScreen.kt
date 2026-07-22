package com.nexus.porsuk.ui.analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.*
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.Locale

private val TextPrimary = InkText
private val TextMuted = SubText
private val BorderLine = LineBorder
private val Background = BackgroundNew
private val Surface = CardNew
private val PositiveGreen = PrimaryTeal
private val NegativeRed = NegatifRed
private val Aqua = PrimaryTeal
private val AquaLight = TealSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onStockClick: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateBasket: () -> Unit,
    onNavigateToDuel: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()

    val aiRebalance by viewModel.aiRebalance.collectAsState()
    val isRebalanceLoading by viewModel.isRebalanceLoading.collectAsState()

    val screenerResult by viewModel.screenerResult.collectAsState()
    val isScreenerLoading by viewModel.isScreenerLoading.collectAsState()

    val aiRecommendations by viewModel.aiRecommendations.collectAsState()
    val isRecsLoading by viewModel.isRecsLoading.collectAsState()

    val healthCheckResult by viewModel.portfolioHealthCheckResult.collectAsState()
    val isHealthChecking by viewModel.isHealthChecking.collectAsState()

    var showRebalanceSheet by remember { mutableStateOf(false) }
    var showHealthCheckSheet by remember { mutableStateOf(false) }
    var showScreenerTemplatesSheet by remember { mutableStateOf(false) }
    var showScreenerResultSheet by remember { mutableStateOf(false) }
    var showRecsSheet by remember { mutableStateOf(false) }

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Aqua)
            }
        } else if (uiState.basketCount == 0) {
            EmptyAnalysisState(onCreateBasket)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Sabit Üst Blok (Hero + Fiyat & Performans Grafiği)
                item(key = "fixed_top_block") {
                    AnalizTopBlock(
                        uiState = uiState,
                        numberFormat = numberFormat,
                        onRangeSelect = viewModel::onRangeSelected,
                        onNavigateToSettings = onNavigateToSettings
                    )
                }

                // 2. Sekmeler (Grafiğin ALTINDA - 5 Sekme)
                item(key = "tab_row") {
                    AnalizTabRow(
                        selectedIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it }
                    )
                }

                // 3. Sekme İçerikleri
                when (selectedTabIndex) {
                    0 -> overviewTabItems(uiState, numberFormat, onStockClick)
                    1 -> riskSimulationTabItems(uiState)
                    2 -> marketPulseTabItems()
                    3 -> incomeTaxTabItems(uiState, numberFormat)
                    4 -> oracleToolsTabItems(
                        uiState = uiState,
                        onNavigateToSettings = onNavigateToSettings,
                        onRunHealthCheck = {
                            viewModel.runPortfolioHealthCheck()
                            showHealthCheckSheet = true
                        },
                        onOpenScreener = { showScreenerTemplatesSheet = true },
                        onGetRecs = {
                            viewModel.generateInvestmentRecommendations()
                            showRecsSheet = true
                        },
                        onGetRebalance = {
                            viewModel.generateRebalanceReport()
                            showRebalanceSheet = true
                        },
                        onNavigateToDuel = { onNavigateToDuel("THYAO", "PGSUS") }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (showHealthCheckSheet) {
        ModalBottomSheet(
            onDismissRequest = { showHealthCheckSheet = false },
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🩺 Portföy Sağlık Raporu (Check-Up)", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                if (isHealthChecking) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Aqua)
                    }
                } else {
                    MarkdownText(markdown = healthCheckResult ?: "Yanıt alınamadı.", style = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp))
                }
            }
        }
    }

    if (showScreenerTemplatesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showScreenerTemplatesSheet = false },
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🔍 Yapay Zeka Hisse Eleği", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Hazır strateji şablonlarından birini seçerek BIST hisselerini filtreleyin:", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                val templates = remember {
                    listOf(
                        "Graham Değer Avcısı (Düşük F/K + Yüksek Güvenlik Marjı)",
                        "Buffett Hendekli Büyüme (ROE > %25 + Düşük Borç)",
                        "Yüksek Temettü & Pasif Gelir (Verim > %8)",
                        "Piotroski Güçlü Bilanço (F-Score ≥ 7)"
                    )
                }
                templates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showScreenerTemplatesSheet = false
                                viewModel.runFundamentalScreener(template)
                                showScreenerResultSheet = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundNew),
                        border = BorderStroke(1.dp, BorderLine)
                    ) {
                        Text(template, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showScreenerResultSheet) {
        ModalBottomSheet(
            onDismissRequest = { showScreenerResultSheet = false },
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🔍 Elek Sonuçları", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                if (isScreenerLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Aqua)
                    }
                } else {
                    MarkdownText(markdown = screenerResult ?: "Sonuç bulunamadı.", style = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp))
                }
            }
        }
    }

    if (showRecsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRecsSheet = false },
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("💡 Profesör'ün Alım Önerileri", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                if (isRecsLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Aqua)
                    }
                } else {
                    MarkdownText(markdown = aiRecommendations ?: "Öneri oluşturulamadı.", style = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp))
                }
            }
        }
    }

    if (showRebalanceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRebalanceSheet = false },
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("⚖️ Rebalans Raporu", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                if (isRebalanceLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Aqua)
                    }
                } else {
                    MarkdownText(markdown = aiRebalance ?: "Rapor hazırlanamadı.", style = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 14.sp, lineHeight = 22.sp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SABİT ÜST BLOK (HERO + PORTFÖY DEĞERİ + GRAFİK)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnalizTopBlock(
    uiState: AnalysisUiState,
    numberFormat: NumberFormatOption,
    onRangeSelect: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0B1F1C), Color(0xFF017A63), Color(0xFF015B4A))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Analiz", style = MaterialTheme.typography.titleLarge, color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        Text("Tüm portföyünün genel görünümü", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f), fontFamily = Manrope)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Ayarlar", tint = Color.White)
                    }
                }

                // Değer & % Değişim
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Toplam Portföy Değeri", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontFamily = Manrope)
                        Text(
                            text = CurrencyFormatter.format(uiState.totalPortfolioValue, numberFormat = numberFormat),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    val isPos = uiState.performanceChangePct >= 0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isPos) TealSoft.copy(alpha = 0.25f) else RedSoft.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = if (isPos) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isPos) PrimaryTeal else NegatifRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", uiState.performanceChangePct)}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isPos) PrimaryTeal else NegatifRed,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Timeframe Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val ranges = listOf("1H", "1A", "3A", "1Y", "Tümü")
                    ranges.forEach { range ->
                        val isSelected = uiState.selectedTimeRange == range
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.12f))
                                .clickable { onRangeSelect(range) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = range,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color(0xFF015B4A) else Color.White,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Chart (Mint->Aqua glow effect canvas chart)
                val chartPoints = remember(uiState.selectedTimeRange, uiState.totalPortfolioValue) {
                    val base = uiState.totalPortfolioValue.toFloat().coerceAtLeast(100f)
                    val change = uiState.performanceChangePct.toFloat()
                    val hash = uiState.selectedTimeRange.hashCode()
                    val pts = mutableListOf<Float>()
                    for (i in 0..10) {
                        val factor = 1f + (change / 100f) * (i / 10f) + (kotlin.math.sin(i.toDouble() + hash) * 0.02f).toFloat()
                        pts.add(base * factor)
                    }
                    pts
                }

                PremiumLiveCanvasChart(
                    pricePoints = chartPoints,
                    isGlassStyle = true
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SEKMELER ROW (GRAFİĞİN ALTINDA)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnalizTabRow(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = remember {
        listOf(
            "📊 Genel Bakış",
            "⚖️ Risk & Simülasyon",
            "🌐 Piyasa Nabzı",
            "💰 Gelir & Vergi",
            "🔮 Orakul Araçları"
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(tabs.size) { index ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (isSelected) 4.dp else 0.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = PrimaryTeal.copy(alpha = 0.3f),
                        spotColor = PrimaryTeal.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) {
                            Brush.horizontalGradient(listOf(PrimaryTeal, Color(0xFF22B8D9)))
                        } else {
                            Brush.linearGradient(listOf(Surface, Surface))
                        }
                    )
                    .border(
                        1.dp,
                        if (isSelected) Color.Transparent else BorderLine,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = tabs[index],
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextMuted,
                    fontFamily = Manrope
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 0: GENEL BAKIŞ
// ─────────────────────────────────────────────────────────────────────────────
private fun LazyListScope.overviewTabItems(
    uiState: AnalysisUiState,
    numberFormat: NumberFormatOption,
    onStockClick: (String, String) -> Unit
) {
    // 1. Sektör Dağılımı Kartı
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = TextPrimary.copy(alpha = 0.04f)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, BorderLine)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("📊 Piyasa & Sektör Dağılımı", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                uiState.sectorAllocations.take(4).forEach { (sector, pct) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sector, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
                            Text("%${String.format(Locale.US, "%.1f", pct)}", style = MaterialTheme.typography.bodySmall, color = Aqua, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { (pct / 100f).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Aqua,
                            trackColor = AquaLight
                        )
                    }
                }
            }
        }
    }

    // 2. En İyi / En Kötü Hisseler (Soft Gradient Kartlar)
    item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top Winner
            val winner = uiState.topGainers.firstOrNull()
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = TextPrimary.copy(alpha = 0.04f)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, BorderLine)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.matchParentSize().background(Brush.linearGradient(listOf(TealSoft, Surface))))
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🟢 EN İYİ PERFORMANS", style = MaterialTheme.typography.labelSmall, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, fontSize = 8.5.sp)
                        Text(winner?.symbol ?: "THYAO", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = JetBrainsMono)
                        Text("+%${String.format(Locale.US, "%.2f", winner?.changePct ?: 4.85)}", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono)
                    }
                }
            }

            // Top Loser
            val loser = uiState.topLosers.firstOrNull()
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = TextPrimary.copy(alpha = 0.04f)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, BorderLine)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.matchParentSize().background(Brush.linearGradient(listOf(RedSoft, Surface))))
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🔴 EN ZAYIF PERFORMANS", style = MaterialTheme.typography.labelSmall, color = NegatifRed, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, fontSize = 8.5.sp)
                        Text(loser?.symbol ?: "EREGL", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = JetBrainsMono)
                        Text("-%${String.format(Locale.US, "%.2f", kotlin.math.abs(loser?.changePct ?: -2.15))}", style = MaterialTheme.typography.bodySmall, color = NegatifRed, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono)
                    }
                }
            }
        }
    }

    // 3. Benchmark Karşılaştırması
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = TextPrimary.copy(alpha = 0.04f)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, BorderLine)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📈 Benchmark Karşılaştırması", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricBox(value = "+%${String.format(Locale.US, "%.1f", uiState.performanceChangePct)}", label = "Portföyün", accentColor = PrimaryTeal, tag = "Üstün", tagType = MetricTagType.GOOD, modifier = Modifier.weight(1f))
                    MetricBox(value = "+%12.4", label = "BIST 100", accentColor = Color(0xFF22B8D9), tag = "Endeks", tagType = MetricTagType.NEUTRAL, modifier = Modifier.weight(1f))
                    MetricBox(value = "+%8.1", label = "Gram Altın", accentColor = Color(0xFFE8A93B), tag = "Emtia", tagType = MetricTagType.NEUTRAL, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 1: RİSK & SİMÜLASYON
// ─────────────────────────────────────────────────────────────────────────────
private fun LazyListScope.riskSimulationTabItems(uiState: AnalysisUiState) {
    // 1. MetricBox Grid (Sharpe, Max Drawdown, Volatility)
    item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricBox(
                value = String.format(Locale.US, "%.2f", uiState.riskMetrics.sharpeRatio),
                label = "Sharpe Oranı",
                accentColor = PrimaryTeal,
                tag = if (uiState.riskMetrics.sharpeRatio >= 1.0) "Yüksek Getiri" else "Orta",
                tagType = if (uiState.riskMetrics.sharpeRatio >= 1.0) MetricTagType.GOOD else MetricTagType.NEUTRAL,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                value = "-%${String.format(Locale.US, "%.1f", uiState.riskMetrics.maxDrawdownPct)}",
                label = "Maks Düşüş",
                accentColor = NegatifRed,
                tag = "Zirveden Kayıp",
                tagType = MetricTagType.BAD,
                modifier = Modifier.weight(1f)
            )
            MetricBox(
                value = String.format(Locale.US, "%.2f", uiState.riskMetrics.beta),
                label = "Beta Katsayısı",
                accentColor = Color(0xFF7C6CF0),
                tag = "Piyasa Oynaklığı",
                tagType = MetricTagType.ACCENT,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // 2. Gelişmiş Risk Metrikleri Card
    item {
        val advRisk = remember(uiState) { AdvancedRiskMetricsCalculator.calculate() }
        AdvancedRiskMetricsCard(metrics = advRisk)
    }

    // 3. Portföy Korelasyon Matrisi Card
    item {
        val corrMatrix = remember(uiState) { PortfolioCorrelationCalculator.calculate() }
        PortfolioCorrelationMatrixCard(matrix = corrMatrix)
    }

    // 4. Monte Carlo Simülasyonu Card
    item {
        val simulation = remember(uiState.totalPortfolioValue) { MonteCarloSimulationEngine.runSimulation(uiState.totalPortfolioValue) }
        MonteCarloSimulationCard(simulation = simulation)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 2: PİYASA NABZI
// ─────────────────────────────────────────────────────────────────────────────
private fun LazyListScope.marketPulseTabItems() {
    // 1. Piyasa Genişliği & MKK Yabancı Payı
    item {
        val breadth = remember { MarketBreadthCalculator.calculate() }
        MarketBreadthCard(breadth = breadth)
    }

    // 2. TCMB Makro & Getiri Eğrisi
    item {
        val macro = remember { MacroMetricsCalculator.getMacroData() }
        MacroMetricsCard(macro = macro)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 3: GELİR & VERGİ
// ─────────────────────────────────────────────────────────────────────────────
private fun LazyListScope.incomeTaxTabItems(uiState: AnalysisUiState, numberFormat: NumberFormatOption) {
    // 1. Yıllık Pasif Gelir & Temettü Card
    item {
        DividendYieldCard(dividendSummary = uiState.dividendSummary)
    }

    // 2. P&L Analizi (Gerçekleşmiş & Gerçekleşmemiş Kâr/Zarar)
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = TextPrimary.copy(alpha = 0.04f)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, BorderLine)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("📊 Kâr / Zarar (P&L) Analizi", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricBox(
                        value = "${String.format(Locale.US, "%,.0f", uiState.totalPortfolioValue * 0.14)} TL",
                        label = "Gerçekleşmemiş Kâr",
                        accentColor = PrimaryTeal,
                        tag = "Açık Pozisyon",
                        tagType = MetricTagType.GOOD,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        value = "${String.format(Locale.US, "%,.0f", uiState.totalPortfolioValue * 0.03)} TL",
                        label = "Gerçekleşmiş Kâr",
                        accentColor = Color(0xFF22B8D9),
                        tag = "Kapatılan",
                        tagType = MetricTagType.NEUTRAL,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB 4: ORAKUL ARAÇLARI
// ─────────────────────────────────────────────────────────────────────────────
private fun LazyListScope.oracleToolsTabItems(
    uiState: AnalysisUiState,
    onNavigateToSettings: () -> Unit,
    onRunHealthCheck: () -> Unit,
    onOpenScreener: () -> Unit,
    onGetRecs: () -> Unit,
    onGetRebalance: () -> Unit,
    onNavigateToDuel: () -> Unit
) {
    // 1. Yapay Zeka Asistan Kartları (Check-Up + Screener)
    item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Check-up
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .clickable { if (uiState.hasGeminiKey) onRunHealthCheck() else onNavigateToSettings() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, BorderLine)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.matchParentSize().background(Brush.linearGradient(listOf(Color(0xFF7C6CF0).copy(alpha = 0.05f), Color.Transparent))))
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF7C6CF0).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Text("🩺", fontSize = 20.sp)
                        }
                        Column {
                            Text("Portföy Check-Up", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                            Text("Sağlık Raporu & Risk", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 11.sp, fontFamily = Manrope)
                        }
                    }
                }
            }

            // Screener
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .clickable { if (uiState.hasGeminiKey) onOpenScreener() else onNavigateToSettings() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, BorderLine)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.matchParentSize().background(Brush.linearGradient(listOf(Aqua.copy(alpha = 0.05f), Color.Transparent))))
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Aqua.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Text("🔍", fontSize = 20.sp)
                        }
                        Column {
                            Text("Hisse Eleği", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                            Text("AI Formül Taraması", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 11.sp, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }

    // 2. Orakul AI Hisse Düellosu Giriş Kartı (CTA Button -> Full-Screen Duel)
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF7C6CF0).copy(alpha = 0.2f)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, LineBorder)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF7C6CF0).copy(alpha = 0.08f), PrimaryTeal.copy(alpha = 0.05f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🥊", fontSize = 18.sp)
                            Text("Orakul AI Hisse Düellosu", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = Manrope)
                        }
                        Text(
                            "İki hisseyi seç, 5 finansal raundda hangisinin daha güçlü olduğunu AI motoruna analiz ettir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onNavigateToDuel,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryTeal, Color(0xFF22B8D9)))),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(" Düello Başlat ⚔️", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // 3. Profesör'ün Alım Önerileri Kartı
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clickable { if (uiState.hasGeminiKey) onGetRecs() else onNavigateToSettings() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, BorderLine)
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(WarningGold.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Text("💡", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Profesör'ün Alım Önerileri", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    Text("BIST'te öne çıkan fırsat analizleri", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 11.sp, fontFamily = Manrope)
                }
            }
        }
    }

    // 4. Profesör'ün Yorumu / AI Rebalans Raporu Kartı
    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (uiState.hasGeminiKey) onGetRebalance() else onNavigateToSettings() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, BorderLine)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("👨‍🏫", fontSize = 20.sp)
                    Text("Profesör'ün Portföy Yorumu & Rebalans", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                Text("Portföyünün varlık ağırlıklarını Graham & Buffett felsefesiyle optimize et.", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontFamily = Manrope)
            }
        }
    }
}
