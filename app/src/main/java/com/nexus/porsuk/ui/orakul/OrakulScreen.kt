package com.nexus.porsuk.ui.orakul

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.platform.LocalContext
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
fun OrakulScreen(
    viewModel: OrakulViewModel,
    onNavigateToSettings: () -> Unit = {},
    onStockClick: (String, String) -> Unit = { _, _ -> },
    onChatNavigate: (String) -> Unit = {},
    onKaziNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTimeframeIndex by remember { mutableIntStateOf(1) }
    var selectedAssetTab by remember { mutableIntStateOf(0) }
    var isRationaleExpanded by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Glowing Crystal Ball Animation
    val infiniteTransition = rememberInfiniteTransition()
    val crystalGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            OracleTopBar(
                onShareClick = { Toast.makeText(context, "Oracle tahmini kopyalandı", Toast.LENGTH_SHORT).show() },
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
            // 1. Hero Kart (Cosmic Deep Purple Container)
            item(key = "oracle_hero_card") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    OracleHeroCard(
                        crystalGlowScale = crystalGlowScale,
                        onShareClick = { Toast.makeText(context, "Oracle tahmini paylaşıldı", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            // 2. Zaman Filtreleri (Timeframe Pills Row)
            item(key = "timeframe_filters") {
                OracleTimeframeFilterRow(
                    selectedIndex = selectedTimeframeIndex,
                    onIndexSelected = { selectedTimeframeIndex = it }
                )
            }

            // 3. Piyasa Yön Tahmini (Direction Probability 3 Cards + Expected Range Grid)
            item(key = "direction_probability_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MarketDirectionProbabilitySection()
                }
            }

            // 4. Oracle Puanları (Oracle Score Gauges 6 Items)
            item(key = "oracle_score_gauges") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    OracleScoreGaugesSection()
                }
            }

            // 5. Ana Senaryolar (Main Scenarios Card List)
            item(key = "main_scenarios_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    MainScenariosSection()
                }
            }

            // 6 & 7. Sektör Tahminleri & Günün Öne Çıkan Varlıkları (Side-by-Side 2 Cards Grid)
            item(key = "sector_forecast_and_top_assets") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    SectorsAndTopAssetsSection(
                        selectedAssetTab = selectedAssetTab,
                        onAssetTabSelected = { selectedAssetTab = it },
                        onStockClick = onStockClick
                    )
                }
            }

            // 8. Strateji Önerisi (Oracle Strategy Card)
            item(key = "oracle_strategy_recommendation") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                ) {
                    OracleStrategyRecommendationCard(
                        onDetailClick = { onChatNavigate("Oracle detaylı strateji önerisini açıkla") }
                    )
                }
            }

            // 9. AI Neden Bu Kararı Verdi? (Expandable Decision Rationale Card)
            item(key = "ai_decision_rationale") {
                AiDecisionRationaleAccordionCard(
                    isExpanded = isRationaleExpanded,
                    onToggleExpand = { isRationaleExpanded = !isRationaleExpanded }
                )
            }

            // 10. Güven Skoru (Confidence Matrix Card)
            item(key = "confidence_matrix_section") {
                OracleConfidenceMatrixSection()
            }

            // 11. AI Eylemleri (4 Action Buttons)
            item(key = "ai_action_buttons") {
                OracleActionButtonsSection(
                    onRecalculate = { viewModel.runOracleAnalysis(forceRefresh = true) },
                    onComparePortfolio = { onChatNavigate("Oracle tahminlerini mevcut portföyümle karşılaştır") },
                    onSendToChat = { onChatNavigate("Oracle tahminlerini detaylandır") }
                )
            }
        }
    }
}

// ── ÜST BAR (Top Bar) ──
@Composable
private fun OracleTopBar(
    onShareClick: () -> Unit,
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Oracle",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                color = TextDark
            )
            Text(
                "AI Tahmin & Öngörü Merkezi",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary,
                fontFamily = Manrope
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onShareClick) {
                Icon(Icons.Outlined.Share, contentDescription = "Paylaş", tint = TextDark)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PurpleAccent)
            }
        }
    }
}

// ── 1. HERO KART (Oracle Hero Card) ──
@Composable
private fun OracleHeroCard(
    crystalGlowScale: Float,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(8.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F0529), Color(0xFF1E0A4C), Color(0xFF3B1578))
                )
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Status Badge & Sürüm Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BullishGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Oracle Aktif", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left 3D Glowing Crystal Ball Graphic
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(crystalGlowScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFC084FC), Color(0xFF6C4CF1), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔮", fontSize = 56.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Middle Column: Title & Description
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("Bugünkü Oracle Tahmini", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Piyasalarda pozitif momentum devam ediyor.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Teknik göstergeler, haber akışı ve makro veriler birleştiğinde; 3 gün içinde yukarı yönlü hareket beklentisi güçleniyor.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right Side: Gauge Column Card
                    Column(
                        modifier = Modifier.weight(0.9f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Arc Gauge (87% Güven Oranı)
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 8.dp.toPx()
                                drawArc(
                                    color = Color(0x33FFFFFF),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = Color(0xFFC084FC),
                                    startAngle = -90f,
                                    sweepAngle = 313f, // 87%
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("87%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color.White)
                                Text("Yüksek Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = BullishGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HeroSmallBadge(label = "Tahmin Ufku", value = "3 Gün")
                        Spacer(modifier = Modifier.height(4.dp))
                        HeroSmallBadge(label = "Oracle Sürümü", value = "v4.2.1")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Share Button
                Surface(
                    modifier = Modifier
                        .clickable(onClick = onShareClick)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0x33FFFFFF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tahmini Paylaş", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSmallBadge(label: String, value: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0x22FFFFFF)) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp), color = Color.White.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp, fontFamily = IBMPlexMono), color = Color.White)
        }
    }
}

// ── 2. ZAMAN FİLTRELERİ (Timeframe Pills Row) ──
@Composable
private fun OracleTimeframeFilterRow(
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit
) {
    val filters = remember { listOf("1 Gün", "3 Gün", "1 Hafta", "2 Hafta", "1 Ay", "3 Ay", "6 Ay", "1 Yıl") }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters.size) { idx ->
            val isSelected = selectedIndex == idx
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) PurpleAccent else CardBg,
                border = BorderStroke(1.dp, if (isSelected) PurpleAccent else BorderColor),
                modifier = Modifier
                    .shadow(if (isSelected) 3.dp else 0.dp, RoundedCornerShape(14.dp))
                    .clickable { onIndexSelected(idx) }
            ) {
                Text(
                    filters[idx],
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = if (isSelected) Color.White else TextSecondary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ── 3. PİYASA YÖN TAHMİNİ (Market Direction Probability 3 Cards + Expected Range Grid) ──
@Composable
private fun MarketDirectionProbabilitySection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Piyasa Yön Tahmini ⓘ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Column: 3 Probability Cards
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProbabilityRowCard("Yukarı Yön İhtimali", "^ %62", "Artıyor", BullishGreen)
                ProbabilityRowCard("Yatay Seyir İhtimali", "~ %23", "Azalıyor", RiskOrange)
                ProbabilityRowCard("Aşağı Yön İhtimali", "v %15", "Azalıyor", BearishRed)
            }

            // Right Column: Expected Range & Sparkline Card
            Card(
                modifier = Modifier
                    .weight(1.0f)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Beklenen Hareket Aralığı", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("BIST 100", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("10.450 – 11.250", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 13.sp), color = TextDark)

                    Spacer(modifier = Modifier.height(12.dp))

                    val rangeValues = remember { listOf(10450f, 10600f, 10800f, 11000f, 11250f) }
                    Sparkline(
                        values = rangeValues,
                        color = PurpleAccent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        filled = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("10.450", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = IBMPlexMono), color = BearishRed)
                        Text("11.250", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = IBMPlexMono), color = BullishGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProbabilityRowCard(title: String, pct: String, status: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                Text(pct, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 13.sp), color = TextDark)
            }
            Text(status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = color)
        }
    }
}

// ── 4. ORACLE PUANLARI (Oracle Score Gauges 6 Items) ──
@Composable
private fun OracleScoreGaugesSection() {
    val scores = remember {
        listOf(
            ScoreGaugeItem("Teknik", 85, "Güçlü", BullishGreen),
            ScoreGaugeItem("Temel", 78, "İyi", BullishGreen),
            ScoreGaugeItem("Haber Akışı", 82, "Güçlü", BullishGreen),
            ScoreGaugeItem("Makro", 74, "İyi", BullishGreen),
            ScoreGaugeItem("Sentiment", 81, "Güçlü", BullishGreen),
            ScoreGaugeItem("Genel Skor", 87, "87", PurpleAccent)
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
            Text("Oracle Tahmin Skorları ⓘ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(scores, key = { it.title }) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(65.dp)
                    ) {
                        Text(item.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = TextDark, maxLines = 1)
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 5.dp.toPx()
                                drawArc(
                                    color = PurpleSoftBg,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                drawArc(
                                    color = item.color,
                                    startAngle = -90f,
                                    sweepAngle = (item.score / 100f) * 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Text(item.score.toString(), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 14.sp), color = TextDark)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp), color = item.color)
                    }
                }
            }
        }
    }
}

private data class ScoreGaugeItem(
    val title: String,
    val score: Int,
    val status: String,
    val color: Color
)

// ── 5. ANA SENARYOLAR (Main Scenarios Card List) ──
@Composable
private fun MainScenariosSection() {
    val scenarios = remember {
        listOf(
            ScenarioItem("Pozitif Senaryo", "Olumlu veri akışı ve güçlü teknik görünüm.", "%62", "11.150 – 11.800", "+%6,3", BullishGreen, "↗️"),
            ScenarioItem("Nötr Senaryo", "Karışık sinyaller, yatay seyir beklentisi.", "%23", "10.450 – 11.150", "%0 - %3", RiskOrange, "➖"),
            ScenarioItem("Negatif Senaryo", "Olumsuz gelişmeler ve satış baskısı.", "%15", "9.850 – 10.450", "-%5,7", BearishRed, "↘️")
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
            Text("Ana Senaryolar ⓘ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))

            scenarios.forEach { item ->
                ScenarioRowItem(item = item)
                HorizontalDivider(color = BorderColor.copy(alpha = 0.4f))
            }
        }
    }
}

private data class ScenarioItem(
    val title: String,
    val description: String,
    val probability: String,
    val targetRange: String,
    val potentialReturn: String,
    val color: Color,
    val iconEmoji: String
)

@Composable
private fun ScenarioRowItem(item: ScenarioItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = item.color.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.iconEmoji, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1.3f)) {
            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Text(item.description, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Column(modifier = Modifier.weight(0.7f), horizontalAlignment = Alignment.End) {
            Text("İhtimal", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSecondary)
            Text(item.probability, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
        }

        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.End) {
            Text("Hedef Aralık", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSecondary)
            Text(item.targetRange, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = TextDark)
        }

        Column(modifier = Modifier.weight(0.9f), horizontalAlignment = Alignment.End) {
            Text("Potansiyel Getiri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSecondary)
            Text(item.potentialReturn, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = item.color)
        }
    }
}

// ── 6 & 7. SEKTÖR TAHMİNLERİ & GÜNÜN ÖNE ÇIKAN VARLIKLARI (Side-by-Side 2 Cards Grid) ──
@Composable
private fun SectorsAndTopAssetsSection(
    selectedAssetTab: Int,
    onAssetTabSelected: (Int) -> Unit,
    onStockClick: (String, String) -> Unit
) {
    val sectorForecasts = remember {
        listOf(
            SectorForecastItem("Bankacılık", "^ Pozitif", BullishGreen, 0.85f),
            SectorForecastItem("Savunma", "^ Pozitif", BullishGreen, 0.80f),
            SectorForecastItem("Teknoloji", "~ Nötr", RiskOrange, 0.55f),
            SectorForecastItem("Enerji", "~ Nötr", RiskOrange, 0.50f),
            SectorForecastItem("Ulaştırma", "v Negatif", BearishRed, 0.30f)
        )
    }

    val topAssets = remember {
        listOf(
            TopAssetItem("ASELS", "Güçlü Alım Sinyali", "₺56,70", "^ %4,25", true),
            TopAssetItem("THYAO", "Alım Sinyali", "₺305,25", "^ %2,87", true),
            TopAssetItem("KCHOL", "Nötr", "₺182,40", "^ %0,31", true),
            TopAssetItem("AKBNK", "Dikkat", "₺52,15", "v %-0,42", false),
            TopAssetItem("XAU/USD", "Alım Sinyali", "2.395,45", "^ %0,62", true)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left Card: Sektör Tahminleri
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sektör Tahminleri ⓘ", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp), color = TextDark)
                    Text("Tümünü Gör", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(10.dp))

                sectorForecasts.forEach { item ->
                    SectorForecastRow(item = item)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Right Card: Günün Öne Çıkan Varlıkları
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Günün Öne Çıkan Varlıkları ⓘ", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp), color = TextDark)

                Spacer(modifier = Modifier.height(8.dp))

                // Asset Sub-tabs
                val assetTabs = listOf("Hisseler", "Döviz", "Emtia", "Kripto")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    assetTabs.forEachIndexed { idx, label ->
                        val isSelected = selectedAssetTab == idx
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PurpleSoftBg else LightSurfaceBg,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAssetTabSelected(idx) }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium), color = if (isSelected) PurpleAccent else TextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                topAssets.forEach { item ->
                    TopAssetRow(item = item, onClick = { onStockClick(item.code, "BIST") })
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

private data class SectorForecastItem(val name: String, val status: String, val color: Color, val progress: Float)

@Composable
private fun SectorForecastRow(item: SectorForecastItem) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = TextDark)
            Text(item.status, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = item.color)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { item.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = item.color,
            trackColor = LightSurfaceBg
        )
    }
}

private data class TopAssetItem(
    val code: String,
    val signal: String,
    val price: String,
    val changePct: String,
    val isPositive: Boolean
)

@Composable
private fun TopAssetRow(item: TopAssetItem, onClick: () -> Unit) {
    val color = if (item.isPositive) BullishGreen else BearishRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.code, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = TextDark)
            Text(item.signal, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = color)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = TextDark)
            Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.sp), color = color)
        }
    }
}

// ── 8. STRATEJİ ÖNERİSİ (Oracle Strategy Card) ──
@Composable
private fun OracleStrategyRecommendationCard(onDetailClick: () -> Unit) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👑", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Oracle Strateji Önerisi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Kısa vadede kademeli alım stratejisi uygun. Bankacılık ve savunma sektörlerinde fırsatlar öne çıkıyor. Stop-loss seviyelerine dikkat ederek pozisyon yönetimi yapın.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                    color = TextDark,
                    modifier = Modifier.weight(1.2f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Cybernetic AI Ring Graphic
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 4.dp.toPx()
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
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Text("AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PurpleAccent)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDetailClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSoftBg),
                border = BorderStroke(1.dp, Color(0xFFD8CEFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Detaylı Stratejiyi Gör >", fontSize = 11.sp, color = PurpleAccent, fontWeight = FontWeight.Bold, fontFamily = Manrope)
            }
        }
    }
}

// ── 9. AI NEDEN BU KARARI VERDİ? (Expandable Decision Rationale Card) ──
@Composable
private fun AiDecisionRationaleAccordionCard(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Neden Bu Kararı Verdi?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                    RationaleItemRow("Teknik Göstergeler", "RSI (62) Pozitif bölgede, MACD yukarı kesişim sağladı.")
                    RationaleItemRow("Temel Veriler", "BIST-100 genelinde F/K 7.2x ile tarihsel ortalamanın altında.")
                    RationaleItemRow("Makro Ekonomi", "Merkez Bankası faiz kararı beklentilere paralel.")
                    RationaleItemRow("Haber Akışı", "Savunma sanayii yeni ihracat sözleşmeleri olumlu etkiledi.")
                }
            }
        }
    }
}

@Composable
private fun RationaleItemRow(title: String, desc: String) {
    Column {
        Text("• $title", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
        Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp), color = TextDark)
    }
}

// ── 10. GÜVEN SKORU (Confidence Matrix Card) ──
@Composable
private fun OracleConfidenceMatrixSection() {
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
                Text("📊", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Oracle Güven Endeksi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfidenceMatrixItem("AI Confidence", "%87", BullishGreen)
                ConfidenceMatrixItem("Son 30 Gün", "%84", BullishGreen)
                ConfidenceMatrixItem("Son 90 Gün", "%81", BullishGreen)
                ConfidenceMatrixItem("Toplam Analiz", "4.120", TextDark)
                ConfidenceMatrixItem("Doğru Tahmin", "3.460", PurpleAccent)
            }
        }
    }
}

@Composable
private fun ConfidenceMatrixItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 11.5.sp), color = color)
    }
}

// ── 11. AI EYLEMLERİ (4 Action Buttons Grid) ──
@Composable
private fun OracleActionButtonsSection(
    onRecalculate: () -> Unit,
    onComparePortfolio: () -> Unit,
    onSendToChat: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OracleActionButtonCard(title = "Oracle Yeniden Hesapla", iconEmoji = "🔄", onClick = onRecalculate, modifier = Modifier.weight(1f))
            OracleActionButtonCard(title = "PDF Oluştur", iconEmoji = "📄", onClick = { }, modifier = Modifier.weight(1f))
            OracleActionButtonCard(title = "Portföyümle Karşılaştır", iconEmoji = "⚖️", onClick = onComparePortfolio, modifier = Modifier.weight(1f))
            OracleActionButtonCard(title = "AI Sohbetine Gönder", iconEmoji = "💬", onClick = onSendToChat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun OracleActionButtonCard(
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
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
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
