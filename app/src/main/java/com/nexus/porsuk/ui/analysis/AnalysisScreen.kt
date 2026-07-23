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
                    0 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                border = BorderStroke(1.dp, BorderLine)
                            ) {
                                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text("📊 Sektör ve Bölge Dağılımı", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                                    uiState.regionBreakdown.take(5).forEach { summary ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("${summary.flag} ${summary.label}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontFamily = Manrope)
                                            Text("%${String.format(Locale.US, "%.1f", summary.allocationPercent * 100)}", style = MaterialTheme.typography.bodyMedium, color = Aqua, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricBox(
                                    value = String.format(Locale.US, "%.2f", uiState.riskMetrics.sharpeRatio),
                                    label = "Sharpe Oranı",
                                    accentColor = PrimaryTeal,
                                    tag = if (uiState.riskMetrics.sharpeRatio >= 1.0) "Yüksek" else "Dengeli",
                                    tagType = if (uiState.riskMetrics.sharpeRatio >= 1.0) MetricTagType.GOOD else MetricTagType.NEUTRAL,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricBox(
                                    value = "-%${String.format(Locale.US, "%.1f", uiState.riskMetrics.maxDrawdown)}",
                                    label = "Max Drawdown",
                                    accentColor = NegatifRed,
                                    tag = "Maksimum Düşüş",
                                    tagType = MetricTagType.BAD,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricBox(
                                    value = "%${String.format(Locale.US, "%.1f", uiState.riskMetrics.volatility)}",
                                    label = "Volatilite",
                                    accentColor = Color(0xFF7C6CF0),
                                    tag = "Oynaklık",
                                    tagType = MetricTagType.ACCENT,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    2 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                border = BorderStroke(1.dp, BorderLine)
                            ) {
                                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("📈 Endeks Karşılaştırması", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        MetricBox(value = "${if (uiState.totalChangePercent >= 0) "+" else ""}${String.format(Locale.US, "%.1f", uiState.totalChangePercent)}%", label = "Portföy", accentColor = PrimaryTeal, tag = "Getiri", tagType = MetricTagType.GOOD, modifier = Modifier.weight(1f))
                                        MetricBox(value = "${if (uiState.benchmarkChangePercent >= 0) "+" else ""}${String.format(Locale.US, "%.1f", uiState.benchmarkChangePercent)}%", label = uiState.benchmarkLabel, accentColor = Color(0xFF22B8D9), tag = "Endeks", tagType = MetricTagType.NEUTRAL, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                border = BorderStroke(1.dp, BorderLine)
                            ) {
                                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text("📊 Kâr / Zarar (P&L) Raporu", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        MetricBox(
                                            value = CurrencyFormatter.formatTRY(uiState.unrealizedPnL, formatType = numberFormat),
                                            label = "Gerçekleşmemiş P&L",
                                            accentColor = if (uiState.unrealizedPnL >= 0) PrimaryTeal else NegatifRed,
                                            tag = "Açık Pozisyon",
                                            tagType = if (uiState.unrealizedPnL >= 0) MetricTagType.GOOD else MetricTagType.BAD,
                                            modifier = Modifier.weight(1f)
                                        )
                                        MetricBox(
                                            value = CurrencyFormatter.formatTRY(uiState.realizedPnL, formatType = numberFormat),
                                            label = "Gerçekleşmiş P&L",
                                            accentColor = if (uiState.realizedPnL >= 0) Color(0xFF22B8D9) else NegatifRed,
                                            tag = "Realize Edilen",
                                            tagType = MetricTagType.NEUTRAL,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    modifier = Modifier.weight(1f).height(120.dp).clickable { if (uiState.hasGeminiKey) { viewModel.runPortfolioHealthCheck(); showHealthCheckSheet = true } else onNavigateToSettings() },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    border = BorderStroke(1.dp, BorderLine)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                        Text("🩺 Portföy Check-Up", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("Yapay Zekâ Sağlık Taraması", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f).height(120.dp).clickable { if (uiState.hasGeminiKey) showScreenerTemplatesSheet = true else onNavigateToSettings() },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Surface),
                                    border = BorderStroke(1.dp, BorderLine)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                        Text("🔍 Hisse Eleği", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("Strateji Taraması", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
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
                                viewModel.runStockScreener(template)
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
    numberFormat: String,
    onRangeSelect: (PortfolioRange) -> Unit,
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
                            text = CurrencyFormatter.formatTRY(uiState.totalPortfolioValue, formatType = numberFormat),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    val isPos = uiState.totalChangePercent >= 0
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
                                text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", uiState.totalChangePercent)}%",
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
                    PortfolioRange.values().forEach { range ->
                        val isSelected = uiState.selectedRange == range
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
                                text = range.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color(0xFF015B4A) else Color.White,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Chart
                PremiumLiveCanvasChart(
                    pricePoints = uiState.portfolioHistory,
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

@Composable
private fun EmptyAnalysisState(onCreateBasket: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📊 Henüz Bir Portföy veya Sepet Oluşturulmadı", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Analiz grafiklerini ve AI içgörülerini görebilmek için ilk sepetini oluştur.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateBasket,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
        ) {
            Text("Yeni Sepet Oluştur", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
