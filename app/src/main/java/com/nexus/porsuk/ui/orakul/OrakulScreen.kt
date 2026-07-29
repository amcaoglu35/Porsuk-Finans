package com.nexus.porsuk.ui.orakul

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

// ── DESIGN SYSTEM COLOR PALETTE ──
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
fun OrakulScreen(
    viewModel: OrakulViewModel,
    onNavigateToSettings: () -> Unit = {},
    onStockClick: (String, String) -> Unit = { _, _ -> },
    onChatNavigate: (String) -> Unit = {},
    onKaziNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTimeframeIndex by remember { mutableIntStateOf(1) }
    var selectedAssetTab by remember { mutableIntStateOf(0) }
    var isRationaleExpanded by remember { mutableStateOf(false) }

    // BottomSheet state for Sector AI Explanation
    var activeSectorExplanation by remember { mutableStateOf<SectorItem?>(null) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Glowing Glass Orb & Light Rotation Animations
    val infiniteTransition = rememberInfiniteTransition(label = "oracle_orb_loop")
    
    val orbBreathingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    val orbLightRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightBackground,
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
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // 24dp Standard Card Spacing
        ) {
            // 0. Hisse Arama Çubuğu
            item(key = "hisse_search_bar") {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        placeholder = { Text("Analiz edilecek hisse (Örn: AAPL)...") },
                        trailingIcon = {
                            IconButton(onClick = { 
                                if (searchQuery.isNotBlank()) {
                                    viewModel.analyzeSymbol(searchQuery.uppercase())
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryPurple)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = PrimaryPurple
                        )
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
            }

            if (uiState.hisseReport != null) {
                // 1. Hisse Skor Kartları (10'lu Grid)
                item(key = "hisse_score_grid") {
                    HisseScoreGrid(uiState.hisseReport!!)
                }

                // 2. Hisse Detaylı Analiz Kartları
                item(key = "hisse_detailed_analysis") {
                    HisseDetailedAnalysis(uiState.hisseReport!!)
                }
            }

            // 1. Hero Cosmic Glass Orb Kartı
            item(key = "oracle_hero_card") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    OracleHeroCard(
                        orbScale = orbBreathingScale,
                        orbRotation = orbLightRotation,
                        onShareClick = { Toast.makeText(context, "Oracle tahmini paylaşıldı", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            // 2. Tahmin Metni Kartı (Madde Madde Yapı)
            item(key = "structured_forecast_summary") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    StructuredForecastCard()
                }
            }

            // 3. Zaman Filtreleri (Material 3 Filter Chips)
            item(key = "timeframe_filters") {
                OracleTimeframeFilterRow(
                    selectedIndex = selectedTimeframeIndex,
                    onIndexSelected = { selectedTimeframeIndex = it }
                )
            }

            // 4. Piyasa Yön Tahmini (Yükseliş, Yatay, Düşüş + Beklenen Hareket Aralığı Grafiği)
            item(key = "direction_probability_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(650)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MarketDirectionProbabilitySection()
                }
            }

            // 5. Oracle Skorları (6 Standart Skor Halkası ve Kısa Açıklamaları)
            item(key = "oracle_score_gauges") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    OracleScoreGaugesSection()
                }
            }

            // 6. Ana Senaryolar (Hafif Renkli Kartlar + AI Güven Rozeti)
            item(key = "main_scenarios_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(920)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    MainScenariosSection()
                }
            }

            // 7. Sektör Tahminleri & Günün Öne Çıkan Varlıkları
            item(key = "sector_forecast_and_top_assets") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(1040)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    SectorsAndTopAssetsSection(
                        selectedAssetTab = selectedAssetTab,
                        onAssetTabSelected = { selectedAssetTab = it },
                        onStockClick = onStockClick,
                        onSectorInsightClick = { sector -> activeSectorExplanation = sector }
                    )
                }
            }

            // 8. Strateji Önerisi (AI Skoru Circle Progress + Risk/Getiri/Süre)
            item(key = "oracle_strategy_recommendation") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(1160)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    OracleStrategyRecommendationCard(
                        onDetailClick = { onChatNavigate("Oracle detaylı strateji önerisini açıkla") }
                    )
                }
            }

            // 9. AI Neden Bu Kararı Verdi? (Expansion Accordion)
            item(key = "ai_decision_rationale") {
                AiDecisionRationaleAccordionCard(
                    isExpanded = isRationaleExpanded,
                    onToggleExpand = { isRationaleExpanded = !isRationaleExpanded }
                )
            }

            // 10. Oracle Güven Endeksi (İstatistik Kutucukları Grid)
            item(key = "confidence_matrix_section") {
                OracleConfidenceMatrixSection()
            }

            // 11. Hızlı İşlemler (Eylem Butonları)
            item(key = "ai_action_buttons") {
                OracleActionButtonsSection(
                    onRecalculate = { viewModel.analyze() },
                    onComparePortfolio = { onChatNavigate("Oracle tahminlerini mevcut portföyümle karşılaştır") },
                    onSendToChat = { onChatNavigate("Oracle tahminlerini detaylandır") },
                    onKaziNavigate = onKaziNavigate
                )
            }
        }
    }

    // Modal Bottom Sheet for Sector AI Insight Explanation
    if (activeSectorExplanation != null) {
        SectorExplanationBottomSheet(
            sector = activeSectorExplanation!!,
            onDismiss = { activeSectorExplanation = null }
        )
    }
}

// ── TOP BAR HEADER ──
@Composable
private fun OracleTopBar(
    onShareClick: () -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🔮", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "PORSUK ORACLE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            letterSpacing = 1.sp,
                            fontFamily = Manrope
                        ),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryPurple
                    ) {
                        Text(
                            "AI 2.0",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    "Yapay Zeka Piyasa Kehaneti & Makro Analiz Engine",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, color = TextSecondary, fontFamily = Manrope)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
            ) {
                Icon(Icons.Outlined.Share, contentDescription = "Paylaş", tint = TextDark, modifier = Modifier.size(18.dp))
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

// ── 1. HERO KART (Glass Orb, Glowing Animation & Cosmic Gradient) ──
@Composable
private fun OracleHeroCard(
    orbScale: Float,
    orbRotation: Float,
    onShareClick: () -> Unit
) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animated = true
    }

    val confidenceSweep by animateFloatAsState(
        targetValue = if (animated) (87f / 100f) * 360f else 0f,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label = "hero_confidence_sweep"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(8.dp, RoundedCornerShape(26.dp), ambientColor = PrimaryPurple.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(26.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF190640), Color(0xFF32126B), Color(0xFF5B21B6))
                )
            )
        ) {
            // Star Particle Canvas Overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val stars = listOf(
                    Offset(size.width * 0.1f, size.height * 0.2f),
                    Offset(size.width * 0.8f, size.height * 0.15f),
                    Offset(size.width * 0.65f, size.height * 0.75f),
                    Offset(size.width * 0.3f, size.height * 0.85f),
                    Offset(size.width * 0.9f, size.height * 0.6f)
                )
                stars.forEach { pos ->
                    drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 2.dp.toPx(), center = pos)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glass Orb Display
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .scale(orbScale)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFE9D5FF).copy(alpha = 0.9f),
                                            Color(0xFFC084FC).copy(alpha = 0.6f),
                                            Color(0xFF6C4CF1).copy(alpha = 0.25f)
                                        )
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Light Rotation Aura
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(orbRotation)
                                    .background(
                                        Brush.sweepGradient(
                                            colors = listOf(Color.Transparent, Color.White.copy(0.4f), Color.Transparent)
                                        )
                                    )
                            )
                            Text("🔮", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                "Oracle Piyasa Kehaneti",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "AI Tahmin Engine • Güncel",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // 2. GÜVEN GÖSTERGESİ (%87 Arc Meter)
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 4.dp.toPx()
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

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "87%",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 13.sp),
                                color = Color(0xFFC084FC)
                            )
                            Text(
                                "Güven",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── 3. TAHMİN METNİ (Structured Bullet Points Card) ──
@Composable
private fun StructuredForecastCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) { // 20dp Inner Padding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📋", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Oracle Tahmin Özeti",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ForecastItemRow("📈", "Genel Tahmin", "Piyasalarda pozitif momentum devam ediyor. BIST 100 endeksinde yukarı yönlü hareket %65 ihtimalle sürecek.", PrimaryPurple)
                ForecastItemRow("⚠", "Risk", "Küresel faiz kararları ve enflasyon verileri kısa vadeli oynaklık yaratabilir.", WarningOrange)
                ForecastItemRow("🎯", "Beklenen Hareket", "10.450 - 10.850 puan aralığı hedef bant olarak izleniyor.", SuccessGreen)
                ForecastItemRow("⏳", "Süre", "Tahmin edilen hareketin 3 - 7 gün içerisinde gerçekleşmesi öngörülüyor.", Color(0xFF3B82F6))
            }
        }
    }
}

@Composable
private fun ForecastItemRow(icon: String, title: String, description: String, accentColor: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = accentColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(icon, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp), color = accentColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp), color = TextDark)
            }
        }
    }
}

// ── 4. ZAMAN FİLTRELERİ (Material 3 Filter Chips with Smooth Animation) ──
@Composable
private fun OracleTimeframeFilterRow(
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit
) {
    val filters = remember { listOf("24 Saat", "3 Gün", "7 Gün", "1 Ay") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEachIndexed { idx, label ->
            val isSelected = selectedIndex == idx
            
            val chipBg by animateColorAsState(
                targetValue = if (isSelected) PrimaryPurple else CardWhite,
                animationSpec = tween(250),
                label = "chip_bg_$idx"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else TextSecondary,
                animationSpec = tween(250),
                label = "chip_text_$idx"
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = chipBg,
                border = BorderStroke(1.dp, if (isSelected) PrimaryPurple else BorderColor),
                shadowElevation = if (isSelected) 3.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onIndexSelected(idx) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            fontFamily = Manrope
                        ),
                        color = textColor
                    )
                }
            }
        }
    }
}

// ── 5. PİYASA YÖN TAHMİNİ (Yükseliş, Yatay, Düşüş + Expected Corridor Line Chart) ──
@Composable
private fun MarketDirectionProbabilitySection() {
    val sparkValues = remember { listOf(40f, 42f, 45f, 44f, 48f, 52f, 50f, 56f, 60f) }

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
            Text("Piyasa Yön Tahmini", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Direction Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DirectionTile("📈", "Yükseliş", "%65", SuccessGreen, modifier = Modifier.weight(1f))
                DirectionTile("➡", "Yatay", "%25", WarningOrange, modifier = Modifier.weight(1f))
                DirectionTile("📉", "Düşüş", "%10", ErrorRed, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(14.dp))

            // Target Corridor Line Chart Support
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Beklenen Hareket Aralığı", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = TextSecondary)
                    Text("10.450 - 10.850 Puan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                }

                Sparkline(
                    values = sparkValues,
                    color = SuccessGreen,
                    modifier = Modifier.width(100.dp).height(32.dp),
                    filled = true
                )
            }
        }
    }
}

@Composable
private fun DirectionTile(icon: String, title: String, pct: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = TextSecondary, fontFamily = Manrope)
            Text(pct, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 18.sp), color = color)
        }
    }
}

// ── 6. ORACLE SKORLARI (6 Uniform Gauges with Short Descriptions) ──
@Composable
private fun OracleScoreGaugesSection() {
    val scores = remember {
        listOf(
            OracleGaugeItem("Teknik", 78, "Trend güçlü", PrimaryPurple),
            OracleGaugeItem("Temel", 82, "Bilanço olumlu", SuccessGreen),
            OracleGaugeItem("Makro", 65, "Makro nötr", Color(0xFF3B82F6)),
            OracleGaugeItem("Sentiment", 70, "Alıcılar baskın", Color(0xFFEC4899)),
            OracleGaugeItem("Risk", 45, "Orta seviye", WarningOrange),
            OracleGaugeItem("Portföy", 85, "Uyum yüksek", PrimaryPurple)
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
            Text("Oracle Alt Skorları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

            Spacer(modifier = Modifier.height(16.dp))

            // 2 Rows of 3 Uniform Score Gauges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                scores.take(3).forEach { item ->
                    UniformScoreGaugeTile(item = item, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                scores.drop(3).forEach { item ->
                    UniformScoreGaugeTile(item = item, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class OracleGaugeItem(val label: String, val score: Int, val shortDesc: String, val color: Color)

@Composable
private fun UniformScoreGaugeTile(item: OracleGaugeItem, modifier: Modifier = Modifier) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }

    val sweepAngle by animateFloatAsState(
        targetValue = if (animated) (item.score / 100f) * 360f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "gauge_${item.label}"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(item.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = TextSecondary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier.size(42.dp), // Uniform Size
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                drawArc(
                    color = item.color.copy(alpha = 0.18f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = item.color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text("${item.score}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, fontFamily = IBMPlexMono), color = TextDark)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.shortDesc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold), color = item.color, textAlign = TextAlign.Center)
    }
}

// ── 7. ANA SENARYOLAR (Tinted Low-Opacity Cards + AI Güven Badges) ──
@Composable
private fun MainScenariosSection() {
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
            Text("Ana Piyasa Senaryoları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ScenarioRowCard("🟢 Pozitif Senaryo", "BIST 100 endeksi 10.850 direncini kırarak rekor tazeler.", "%65 Güven", SuccessGreen)
                ScenarioRowCard("🟡 Nötr Senaryo", "Endeks 10.200 - 10.500 dar bandında yatay konsolide olur.", "%25 Güven", WarningOrange)
                ScenarioRowCard("🔴 Negatif Senaryo", "Kar satışlarıyla 9.950 destek seviyesine geri çekilme.", "%10 Güven", ErrorRed)
            }
        }
    }
}

@Composable
private fun ScenarioRowCard(title: String, desc: String, confidenceBadge: String, themeColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = themeColor.copy(alpha = 0.08f), // Low Opacity Specification
        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold), color = themeColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp), color = TextDark)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // AI Güven Rozeti
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = themeColor.copy(alpha = 0.18f)
            ) {
                Text(
                    confidenceBadge,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, fontFamily = IBMPlexMono),
                    color = themeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── 8 & 9. SEKTÖR TAHMİNLERİ (with BottomSheet) & GÜNÜN ÖNE ÇIKAN VARLIKLARI ──
@Composable
private fun SectorsAndTopAssetsSection(
    selectedAssetTab: Int,
    onAssetTabSelected: (Int) -> Unit,
    onStockClick: (String, String) -> Unit,
    onSectorInsightClick: (SectorItem) -> Unit
) {
    val sectors = remember {
        listOf(
            SectorItem("Teknoloji", "^ %2,85", true, "Yarı iletken ve AI yazılım ihracatı kaynaklı yüksek büyüme ivmesi."),
            SectorItem("Savunma", "^ %2,15", true, "Uluslararası yeni sözleşme imzaları ve güçlü sipariş stoğu."),
            SectorItem("Bankacılık", "^ %1,42", true, "Sıkılaşma adımları ile net faiz marjında beklenen iyileşme."),
            SectorItem("Ulaştırma", "v %-0,45", false, "Jet yakıtı maliyet artışı ve sezonluk talep dengelenmesi.")
        )
    }

    val featuredAssets = remember {
        listOf(
            FeaturedAssetItem("ASELS", "Aselsan", "₺56,70", "^ %4,25", "Güçlü Alım", SuccessGreen, "%88 Güven", listOf(40f, 42f, 45f, 50f)),
            FeaturedAssetItem("THYAO", "THY", "₺305,25", "^ %2,87", "Alım Sinyali", SuccessGreen, "%85 Güven", listOf(290f, 295f, 305f)),
            FeaturedAssetItem("KCHOL", "Koç Holding", "₺182,40", "^ %0,31", "Nötr", WarningOrange, "%72 Güven", listOf(180f, 181f, 182.4f))
        )
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Sector Forecasts Card (with AI Yorumu BottomSheet trigger)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Sektör Tahminleri", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))

                sectors.forEach { sector ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sector.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                            Text(sector.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = if (sector.isPos) SuccessGreen else ErrorRed)
                        }

                        // AI Yorumu Icon Button (Triggers BottomSheet)
                        IconButton(
                            onClick = { onSectorInsightClick(sector) },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PurpleSoftBg)
                        ) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = "AI Yorumu", tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                        }
                    }
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                }
            }
        }

        // Günün Öne Çıkan Varlıkları Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Günün Öne Çıkan Varlıkları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                Spacer(modifier = Modifier.height(14.dp))

                featuredAssets.forEach { asset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onStockClick(asset.symbol, "BIST") }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stock Logo Badge (36dp)
                        Surface(
                            shape = CircleShape,
                            color = PurpleSoftBg,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(asset.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PrimaryPurple)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1.1f)) {
                            Text(asset.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                            Text(asset.confidenceRatio, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = PrimaryPurple)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.0f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = asset.signalColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    asset.signal,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = asset.signalColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Sparkline(
                            values = asset.sparkValues,
                            color = SuccessGreen,
                            modifier = Modifier.width(60.dp).height(24.dp),
                            filled = true
                        )
                    }
                    HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                }
            }
        }
    }
}

data class SectorItem(val name: String, val changePct: String, val isPos: Boolean, val aiExplanation: String)
private data class FeaturedAssetItem(
    val symbol: String, val name: String, val price: String, val changePct: String,
    val signal: String, val signalColor: Color, val confidenceRatio: String, val sparkValues: List<Float>
)

// ── 10. STRATEJİ ÖNERİSİ (AI Score Circle Arc + Risk/Return/Term Metrics) ──
@Composable
private fun OracleStrategyRecommendationCard(onDetailClick: () -> Unit) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }

    val scoreSweep by animateFloatAsState(
        targetValue = if (animated) (84f / 100f) * 360f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "strategy_score_sweep"
    )

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
                Text("🎯", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Strateji Önerisi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Score Circle Arc & Strategy Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 6.dp.toPx()
                        drawArc(
                            color = PrimaryPurple.copy(alpha = 0.18f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )
                        drawArc(
                            color = PrimaryPurple,
                            startAngle = -90f,
                            sweepAngle = scoreSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("84", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 18.sp), color = TextDark)
                        Text("AI Skor", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StrategyMetricRow("Risk Profili", "Dengeli (Orta Risk)", WarningOrange)
                    StrategyMetricRow("Getiri Potansiyeli", "%12,5 - %18,0", SuccessGreen)
                    StrategyMetricRow("Yatırım Süresi", "1 - 3 Hafta", PrimaryPurple)
                }
            }
        }
    }
}

@Composable
private fun StrategyMetricRow(label: String, valStr: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), color = TextSecondary)
        Text(valStr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp, fontFamily = IBMPlexMono), color = color)
    }
}

// ── 11. AI NEDEN BU KARARI VERDİ? (Expandable Accordion Card) ──
@Composable
private fun AiDecisionRationaleAccordionCard(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val weightedFactors = remember {
        listOf(
            WeightedFactor("Teknik Analiz", 85, PrimaryPurple),
            WeightedFactor("Temel Analiz", 78, SuccessGreen),
            WeightedFactor("Makro Veriler", 65, Color(0xFF3B82F6)),
            WeightedFactor("Haber Analizi", 82, WarningOrange),
            WeightedFactor("Sentiment", 74, Color(0xFFEC4899)),
            WeightedFactor("Geçmiş Başarı", 91, PrimaryPurple)
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
                    Text(
                        "AI Neden Bu Kararı Verdi?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = TextDark
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Aç/Kapat",
                    tint = PrimaryPurple
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    weightedFactors.forEach { factor ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(factor.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Bold), color = TextDark)
                                Text("%${factor.pct}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = factor.color)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { factor.pct / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = factor.color,
                                trackColor = factor.color.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class WeightedFactor(val label: String, val pct: Int, val color: Color)

// ── 12. ORACLE GÜVEN ENDEKSİ (5 Stat Tiles Matrix) ──
@Composable
private fun OracleConfidenceMatrixSection() {
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
            Text("Oracle Güven Endeksi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConfidenceStatTile("Son 30 Gün", "%84", SuccessGreen, modifier = Modifier.weight(1f))
                ConfidenceStatTile("Son 90 Gün", "%88", PrimaryPurple, modifier = Modifier.weight(1f))
                ConfidenceStatTile("Toplam Analiz", "1.240", Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                ConfidenceStatTile("Doğru Tahmin", "1.091", SuccessGreen, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ConfidenceStatTile(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = LightBackground,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = TextSecondary, maxLines = 1)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, fontFamily = IBMPlexMono), color = color)
        }
    }
}

// ── 13. HIZLI İŞLEMLER (Enlarged Buttons with Material Symbols & Scale Ripple Feedback) ──
@Composable
private fun OracleActionButtonsSection(
    onRecalculate: () -> Unit,
    onComparePortfolio: () -> Unit,
    onSendToChat: () -> Unit,
    onKaziNavigate: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı İşlemler", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OracleActionTile("Yeniden Hesapla", "🔄", onClick = onRecalculate, modifier = Modifier.weight(1f))
            OracleActionTile("Portföyle Kıyasla", "⚖️", onClick = onComparePortfolio, modifier = Modifier.weight(1f))
            OracleActionTile("Derin Kazı", "⛏️", onClick = onKaziNavigate, modifier = Modifier.weight(1f))
            OracleActionTile("AI Lab'e Sor", "🤖", onClick = onSendToChat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun OracleActionTile(
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
        label = "oracle_action_scale"
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
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PurpleSoftBg,
                modifier = Modifier.size(40.dp)
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

// ── MODAL BOTTOM SHEET FOR SECTOR AI INSIGHT ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectorExplanationBottomSheet(
    sector: SectorItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        scrimColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "${sector.name} Sektörü AI Yorumu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                        color = TextDark
                    )
                    Text(
                        "Oracle Makro Engine Analizi",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = PrimaryPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PurpleSoftBg,
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f))
            ) {
                Text(
                    text = sector.aiExplanation,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp, fontFamily = Manrope),
                    color = TextDark,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text("Anladım", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── PREVIEW SUPPORT ──
@Composable
private fun HisseScoreGrid(report: OracleHisseReport) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Hisse Performans Metrikleri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(14.dp))
        
        val scores = listOf(
            OracleGaugeItem("Genel AI", report.aiScore, "Potansiyel: ${report.aiScore}%", PrimaryPurple),
            OracleGaugeItem("Risk", report.riskScore, "Oynaklık: ${report.riskScore}%", WarningOrange),
            OracleGaugeItem("Büyüme", report.growthPotential, "İvme: ${report.growthPotential}%", SuccessGreen),
            OracleGaugeItem("Temettü", report.dividendScore, "Verim: ${report.dividendScore}%", Color(0xFF3B82F6)),
            OracleGaugeItem("Sağlık", report.financialHealth, "Mali Durum", SuccessGreen),
            OracleGaugeItem("Momentum", report.momentum, "Güç: ${report.momentum}%", Color(0xFFEC4899)),
            OracleGaugeItem("Volatilite", report.volatility, "Risk: ${report.volatility}%", WarningOrange),
            OracleGaugeItem("Likidite", report.liquidity, "Derinlik", PrimaryPurple),
            OracleGaugeItem("Kalite", report.qualityScore, "Şirket Kalitesi", SuccessGreen),
            OracleGaugeItem("Güven", report.confidence, "AI Güven", PrimaryPurple)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            scores.take(5).forEach { item -> UniformScoreGaugeTile(item = item, modifier = Modifier.weight(1f)) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            scores.drop(5).forEach { item -> UniformScoreGaugeTile(item = item, modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun HisseDetailedAnalysis(report: OracleHisseReport) {
    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Orakul Kararı", fontSize = 12.sp, color = TextSecondary)
                    Text(report.recommendation, fontSize = 22.sp, fontWeight = FontWeight.Black, color = when(report.recommendation) {
                        "BUY" -> SuccessGreen
                        "SELL" -> ErrorRed
                        else -> WarningOrange
                    })
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Adil Değer", fontSize = 12.sp, color = TextSecondary)
                    Text("${report.fairValue}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, fontFamily = IBMPlexMono)
                }
            }
        }

        // SWOT Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("SWOT Analizi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                SwotItem("💪 Güçlü Yönler", report.strengths, SuccessGreen)
                SwotItem("📉 Zayıf Yönler", report.weaknesses, WarningOrange)
                SwotItem("🚀 Fırsatlar", report.opportunities, Color(0xFF3B82F6))
                SwotItem("⚠️ Riskler", report.risks, ErrorRed)
            }
        }

        // Outlook and Thesis
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Görünüm ve Yatırım Tezi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Kısa Vade: ${report.shortTermOutlook}", fontSize = 13.sp, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Uzun Vade: ${report.longTermOutlook}", fontSize = 13.sp, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(12.dp))
                dev.jeziellago.compose.markdowntext.MarkdownText(markdown = report.investmentThesis)
            }
        }
    }
}

@Composable
private fun SwotItem(label: String, items: List<String>, color: Color) {
    if (items.isEmpty()) return
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        items.forEach { item ->
            Text("• $item", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
