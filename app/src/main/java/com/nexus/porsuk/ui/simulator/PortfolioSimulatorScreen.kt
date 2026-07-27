package com.nexus.porsuk.ui.simulator

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)

private data class SimAssetItem(val symbol: String, val name: String, var weightPct: Float, val category: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioSimulatorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedBacktestPeriod by rememberSaveable { mutableIntStateOf(0) } // 0: 1 Yıl, 1: 3 Yıl, 2: 5 Yıl

    var assets by remember {
        mutableStateOf(
            listOf(
                SimAssetItem("THYAO", "Türk Hava Yolları", 30f, "BİST Hisse"),
                SimAssetItem("ASELS", "Aselsan Elektronik", 25f, "BİST Hisse"),
                SimAssetItem("NVDA", "NVIDIA Corp.", 20f, "ABD Hisse"),
                SimAssetItem("TTE", "İş Portföy Teknoloji", 15f, "TEFAS Fon"),
                SimAssetItem("TRY", "TL Nakit Bakiye", 10f, "Nakit")
            )
        )
    }

    // Dynamic AI Calculations based on current sliders
    val totalWeight = assets.sumOf { it.weightPct.toDouble() }.toFloat()
    val expectedReturnPct = remember(assets) { (12.0 + (assets.find { it.symbol == "NVDA" }?.weightPct ?: 0f) * 0.25).coerceIn(8.0, 35.0) }
    val riskScore = remember(assets) { (50 + (assets.find { it.category == "BİST Hisse" }?.weightPct ?: 0f) * 0.4).toInt().coerceIn(30, 92) }
    val diversityScore = remember(assets) { (90 - (assets.maxOfOrNull { it.weightPct } ?: 30f) * 0.8).toInt().coerceIn(40, 98) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "Portföy Simülatörü & Backtest",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = TextDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextDark)
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                Toast.makeText(context, "📄 Simülasyon & Backtest Raporu Hazırlandı!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rapor Oluştur", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Tab Selector: Simülatör, Karşılaştırma, Backtest
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    divider = {}
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("📊 Simülatör", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(vertical = 10.dp))
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("⚖️ Karşılaştırma", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(vertical = 10.dp))
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("⏪ Backtest", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(vertical = 10.dp))
                    }
                }
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> SimulatorTabContent(
                    assets = assets,
                    totalWeight = totalWeight,
                    expectedReturnPct = expectedReturnPct,
                    riskScore = riskScore,
                    diversityScore = diversityScore,
                    onWeightChange = { index, newWeight ->
                        assets = assets.toMutableList().also { it[index] = it[index].copy(weightPct = newWeight) }
                    }
                )
                1 -> ComparisonTabContent(
                    expectedReturnPct = expectedReturnPct,
                    riskScore = riskScore,
                    diversityScore = diversityScore
                )
                2 -> BacktestTabContent(
                    selectedPeriod = selectedBacktestPeriod,
                    onPeriodSelect = { selectedBacktestPeriod = it }
                )
            }
        }
    }
}

// ── TAB 1: SİMÜLATÖR CONTENT ──
@Composable
private fun SimulatorTabContent(
    assets: List<SimAssetItem>,
    totalWeight: Float,
    expectedReturnPct: Double,
    riskScore: Int,
    diversityScore: Int,
    onWeightChange: (Int, Float) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Senaryo Sonuç Kartı
        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🤖", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Senaryo Analiz Motoru", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = PurpleSoftBg) {
                            Text("%${String.format("%.0f", totalWeight)} Ağırlık", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurple, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricTile("Beklenen Getiri", "+%${String.format("%.1f", expectedReturnPct)}", SuccessGreen)
                        MetricTile("Risk Skoru", "$riskScore / 100", if (riskScore > 70) WarningOrange else PrimaryPurple)
                        MetricTile("Çeşitlilik", "$diversityScore / 100", SuccessGreen)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sparkline Growth Canvas
                    Text("🔮 12 Aylık Tahmini Portföy Büyüme Simülasyonu", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    SimulationCanvasGraph()
                }
            }
        }

        // Varlık Ağırlık Ayarları (Sliders)
        item {
            Text("⚙️ Varlık Dağılımı ve Ağırlık Simülasyonu", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(assets.size) { idx ->
            val item = assets[idx]
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.symbol, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                            Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                        Text("%${item.weightPct.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PrimaryPurple)
                    }

                    Slider(
                        value = item.weightPct,
                        onValueChange = { onWeightChange(idx, it) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryPurple, activeTrackColor = PrimaryPurple)
                    )
                }
            }
        }
    }
}

// ── TAB 2: KARŞILAŞTIRMA CONTENT ──
@Composable
private fun ComparisonTabContent(
    expectedReturnPct: Double,
    riskScore: Int,
    diversityScore: Int
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("⚖️ Portföy Karşılaştırma Matrisi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Text("Mevcut Portföy vs Simüle Edilen Senaryo", style = MaterialTheme.typography.labelSmall, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    ComparisonRow("Beklenen Getiri", "+%14,2", "+%${String.format("%.1f", expectedReturnPct)}")
                    HorizontalDivider(color = BorderColor)
                    ComparisonRow("Risk Skoru", "72 / 100", "$riskScore / 100")
                    HorizontalDivider(color = BorderColor)
                    ComparisonRow("AI Sağlık Skoru", "85 / 100", "91 / 100")
                    HorizontalDivider(color = BorderColor)
                    ComparisonRow("Çeşitlilik İndeksi", "78 / 100", "$diversityScore / 100")
                    HorizontalDivider(color = BorderColor)
                    ComparisonRow("Teknoloji Ağırlığı", "%42", "%20")
                    HorizontalDivider(color = BorderColor)
                    ComparisonRow("Nakit Oranı", "%5", "%10")
                }
            }
        }
    }
}

// ── TAB 3: BACKTEST CONTENT ──
@Composable
private fun BacktestTabContent(
    selectedPeriod: Int,
    onPeriodSelect: (Int) -> Unit
) {
    val periods = remember { listOf("Son 1 Yıl", "Son 3 Yıl", "Son 5 Yıl") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEachIndexed { idx, label ->
                    val isSel = selectedPeriod == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) PrimaryPurple else CardWhite,
                        border = BorderStroke(1.dp, if (isSel) PrimaryPurple else BorderColor),
                        modifier = Modifier.weight(1f).clickable { onPeriodSelect(idx) }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (isSel) Color.White else TextDark)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("📈 Geçmiş Strateji Backtest Büyüme Grafiği", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    SimulationCanvasGraph()
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Maksimum Düşüş (Max Drawdown): -%8.2", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = WarningOrange)
                        Text("Sharpe Rasyosu: 1.85", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = SuccessGreen)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("🤖 AI Strateji Değerlendirme Raporu", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Güçlü Yönler: Teknoloji ve Temettü dengesi sayesinde boğa piyasalarında %48 ekstra getiri üretilmiştir.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Zayıf Yönler: Yüksek enflasyonist dönemlerde reel varlık koruma marjı %3 gerilemiştir.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Piyasa Başarısı: Trend yükselişlerinde %92 başarı, yatay piyasalarda %78 tutarlılık.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = PrimaryPurple)
                }
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
        Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = valueColor)
    }
}

@Composable
private fun ComparisonRow(title: String, realVal: String, simVal: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = TextDark)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(realVal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = TextSecondary)
            Text(simVal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PrimaryPurple)
        }
    }
}

@Composable
private fun SimulationCanvasGraph() {
    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
        val points = listOf(10f, 25f, 20f, 45f, 40f, 65f, 60f, 85f)
        val stepX = size.width / (points.size - 1)
        val maxY = points.maxOrNull() ?: 100f
        val path = Path()

        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = size.height - (p / maxY * size.height * 0.8f)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(colors = listOf(PrimaryPurple, SuccessGreen)),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioSimulatorScreenPreview() {
    PortfolioSimulatorScreen(onBack = {})
}
