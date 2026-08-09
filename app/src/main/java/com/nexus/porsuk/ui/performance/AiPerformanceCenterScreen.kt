package com.nexus.porsuk.ui.performance

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.TrendingUp
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.theme.*

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)
private val CriticalRed = Color(0xFFF44336)

private data class ModuleAccuracyItem(
    val moduleName: String,
    val accuracyPct: Double,
    val totalCount: Int,
    val icon: String
)

private data class PredictionHistoryItem(
    val id: String,
    val symbol: String,
    val date: String,
    val predictionStr: String,
    val actualOutcomeStr: String,
    val isSuccessful: Boolean,
    val confidencePct: Int,
    val selfEvaluation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPerformanceCenterScreen(
    onBack: () -> Unit,
    viewModel: AiPerformanceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = remember {
        listOf("📊 İstatistikler", "🏆 Modüller & Sektörler", "📜 Geçmiş Tahminler", "🎖️ Rozetler")
    }

    val moduleLeaderboard = remember {
        listOf(
            ModuleAccuracyItem("Oracle 🔮", 92.1, 412, "🔮"),
            ModuleAccuracyItem("Risk Analizi 🛡️", 91.8, 380, "🛡️"),
            ModuleAccuracyItem("Temel Analiz 🏛️", 90.4, 290, "🏛️"),
            ModuleAccuracyItem("Portföy Doktoru 🩺", 89.5, 345, "🩺"),
            ModuleAccuracyItem("Teknik Analiz 📈", 88.2, 510, "📈"),
            ModuleAccuracyItem("Makro Analiz 🌍", 87.0, 180, "🌍"),
            ModuleAccuracyItem("AI Chat 🤖", 86.5, 620, "🤖"),
            ModuleAccuracyItem("Haber Analizi 📰", 84.6, 210, "📰")
        )
    }

    val pastPredictions = remember {
        listOf(
            PredictionHistoryItem("1", "THYAO", "20 Temmuz 2026", "Hedef ₺315 (%18 Prim)", "₺318 Gerçekleşti", true, 94, "Teknik kırılım ve 2Ç yolcu rakamları beklentileri tam doğrulukla destekledi."),
            PredictionHistoryItem("2", "ASELS", "18 Temmuz 2026", "Hedef ₺58 (%22 Prim)", "₺56.7 Gerçekleşti", true, 91, "Savunma ihracat duyuruları momentumu yükseltti."),
            PredictionHistoryItem("3", "NVDA", "15 Temmuz 2026", "Hedef $135 (%25 Prim)", "$128.2 Gerçekleşti", true, 96, "Veri merkezi çip gelirleri tahminleri tam doğrulukla doğruladı."),
            PredictionHistoryItem("4", "EREGL", "10 Temmuz 2026", "Temettü Düzeltme Hedefi", "Tarihsel Düzeltme", false, 82, "Bu tahmin haber akışındaki ani jeopolitik navlun değişimi nedeniyle ertelendi."),
            PredictionHistoryItem("5", "BTC", "05 Temmuz 2026", "Hedef $68.000 (%12 Prim)", "$67.450 Gerçekleşti", true, 88, "Spot ETF girişleri beklendiği yönde ivme üretti.")
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "AI Performance & Accuracy Center",
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
                                Toast.makeText(context, "📄 AI Şeffaflık & Doğruluk Raporu Oluşturuldu!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Violet),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF Rapor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = Violet,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedTab == idx) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedTab == idx) Violet else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                0 -> GeneralStatsTabContent()
                1 -> ModuleLeaderboardTabContent(moduleLeaderboard = moduleLeaderboard)
                2 -> PastPredictionsTabContent(pastPredictions = pastPredictions)
                3 -> BadgesTabContent()
            }
        }
    }
}

// ── TAB 1: GENEL İSTATİSTİKLER ──
@Composable
private fun GeneralStatsTabContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Genel AI Doğruluk Karnesi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.12f)) {
                            Text("%88.5 Doğruluk", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = SuccessGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatTile("Toplam Analiz", "1.480", Violet)
                        StatTile("Başarılı", "1.098", SuccessGreen)
                        StatTile("Başarısız", "142", CriticalRed)
                        StatTile("Ort. Güven", "%89.2", Violet)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("📈 30 Günlük Başarı Trend Grafiği", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    AccuracyCanvasGraph()
                }
            }
        }

        item {
            Text("⏱️ Zaman Dilimlerine Göre Başarı Oranları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TimeframeCard("Son 7 Gün", "%91.4", SuccessGreen, modifier = Modifier.weight(1f))
                TimeframeCard("Son 30 Gün", "%88.5", Violet, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TimeframeCard("Son 90 Gün", "%86.2", Violet, modifier = Modifier.weight(1f))
                TimeframeCard("Tüm Zamanlar", "%88.5", SuccessGreen, modifier = Modifier.weight(1f))
            }
        }
    }
}

// ── TAB 2: MODÜLLER & SEKTÖRLER ──
@Composable
private fun ModuleLeaderboardTabContent(moduleLeaderboard: List<ModuleAccuracyItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("🏆 AI Modülleri Başarı Lider Tablosu", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(moduleLeaderboard, key = { it.moduleName }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = VioletSoft, modifier = Modifier.size(38.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.icon, fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.moduleName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                            Text("${item.totalCount} Başarılı Analiz", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                    }

                    Text("%${item.accuracyPct}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = SuccessGreen)
                }
            }
        }
    }
}

// ── TAB 3: GEÇMİŞ TAHMİNLER ──
@Composable
private fun PastPredictionsTabContent(pastPredictions: List<PredictionHistoryItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("📜 Geçmiş Tahminler ve AI Kendini Değerlendirmesi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(pastPredictions, key = { it.id }) { item ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.symbol, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item.date, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (item.isSuccessful) SuccessGreen.copy(alpha = 0.12f) else CriticalRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                if (item.isSuccessful) "✓ Başarılı" else "✕ Başarısız",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.5.sp),
                                color = if (item.isSuccessful) SuccessGreen else CriticalRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Tahmin: ${item.predictionStr}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Sonuç: ${item.actualOutcomeStr}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Violet)

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(shape = RoundedCornerShape(10.dp), color = VioletSoft) {
                        Text("🤖 AI Retrospektif Yorum: ${item.selfEvaluation}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, lineHeight = 13.sp), color = TextDark, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

// ── TAB 4: ROZETLER ──
@Composable
private fun BadgesTabContent() {
    val badges = remember {
        listOf(
            Triple("🥇 %80 Üzeri Başarı Ustası", "Geçmiş 1000 analizde %88.5 doğruluk başarısı.", "Açıldı"),
            Triple("🏆 1000 Başarılı Analiz", "1.000 üzeri doğrulanmış başarılı piyasa analizi.", "Açıldı"),
            Triple("🎯 100 Oracle İsabeti", "Oracle tahminlerinde 100 kez tam isabet sağlandı.", "Açıldı"),
            Triple("📈 30 Gün Kesintisiz Rekor", "30 gün boyunca kesintisiz pozitif tahmin doğruluğu.", "Açıldı")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(badges, key = { it.first }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.first, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                        Text(item.second, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.12f)) {
                        Text(item.third, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp), color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
        Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = color)
    }
}

@Composable
private fun TimeframeCard(title: String, accuracy: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(accuracy, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = color)
        }
    }
}

@Composable
private fun AccuracyCanvasGraph() {
    Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        val points = listOf(78f, 82f, 80f, 85f, 84f, 89f, 88.5f)
        val stepX = size.width / (points.size - 1)
        val path = Path()

        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = size.height - ((p - 70f) / 30f * size.height)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(colors = listOf(Violet, SuccessGreen)),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AiPerformanceCenterScreenPreview() {
    AiPerformanceCenterScreen(onBack = {})
}
