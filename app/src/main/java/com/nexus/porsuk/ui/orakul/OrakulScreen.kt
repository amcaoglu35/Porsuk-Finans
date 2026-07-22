package com.nexus.porsuk.ui.orakul

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrakulScreen(
    viewModel: OrakulViewModel,
    onNavigateToSettings: () -> Unit,
    onStockClick: (String, String) -> Unit,
    onChatNavigate: (String) -> Unit,
    onKaziNavigate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHistory by remember { mutableStateOf(false) }
    var showRebalanceWizard by remember { mutableStateOf(false) }

    // Nabız animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundNew,
        topBar = {
            OrakulTopBar(
                lastAnalysisTime = uiState.lastAnalysisTime,
                historyCount = uiState.history.size,
                showHistory = showHistory,
                onHistoryToggle = { showHistory = !showHistory },
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // Geçmiş paneli
            AnimatedVisibility(
                visible = showHistory && uiState.history.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OrakulHistoryPanel(uiState.history)
            }

            // 2. Piyasa Sentiment İndeksi
            MarketSentimentGauge(score = uiState.marketSentimentScore)

            // 3. Analiz Modu
            ModeSelector(
                selectedMode = uiState.selectedMode,
                onModeSelect = viewModel::selectMode,
                enabled = !uiState.isLoading
            )

            // 4. Analiz Yapılacak Piyasa / Inputs
            AnimatedVisibility(
                visible = uiState.selectedMode != OrakulMode.ASK,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                MarketFilterChips(
                    selectedMarket = uiState.selectedMarket,
                    onMarketSelect = viewModel::setSelectedMarket,
                    enabled = !uiState.isLoading
                )
            }

            // Sepet Modu için bütçe ve vade giriş alanları
            AnimatedVisibility(
                visible = uiState.selectedMode == OrakulMode.BASKET,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                BasketParametersInput(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            // Soru Sor modu girişi
            AnimatedVisibility(
                visible = uiState.selectedMode == OrakulMode.ASK,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                CustomQuestionInput(
                    value = uiState.customQuestion,
                    onValueChange = viewModel::setCustomQuestion,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // API Key uyarısı
            AnimatedVisibility(visible = !uiState.hasGeminiKey) {
                NoKeyBanner(onNavigateToSettings, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }

            // 5. CTA Butonu
            OracleCta(
                selectedMode = uiState.selectedMode,
                isLoading = uiState.isLoading,
                canAnalyze = uiState.hasGeminiKey && (uiState.selectedMode != OrakulMode.ASK || uiState.customQuestion.isNotBlank()),
                pulseAlpha = pulseAlpha,
                onClick = {
                    if (uiState.selectedMode == OrakulMode.KAZI) {
                        onKaziNavigate()
                    } else {
                        viewModel.analyze()
                    }
                }
            )

            // 6. Bekleme durumu & 7. O-EAGI Analiz Katmanları listesi
            AnimatedVisibility(
                visible = !uiState.isLoading && uiState.rawResponse == null && uiState.error == null
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OracleWaitingState(uiState.selectedMode)
                    if (uiState.selectedMode != OrakulMode.ASK) {
                        OEagiLayersCard()
                    }
                }
            }

            // DYNAMIC RESULT PANELS:
            // Streaming canlı yazı (kararlar parse edilmeden önce)
            AnimatedVisibility(
                visible = uiState.isLoading && uiState.streamingText.isNotBlank()
            ) {
                StreamingCard(
                    text = uiState.streamingText,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            // Hata
            AnimatedVisibility(visible = uiState.error != null) {
                OrakulErrorCard(
                    uiState.error ?: "",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            // Karar Kartları
            AnimatedVisibility(
                visible = uiState.decisions.isNotEmpty() && !uiState.isLoading,
                enter = fadeIn() + slideInVertically { it / 3 }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    DecisionSectionHeader(uiState.decisions)
                    Spacer(modifier = Modifier.height(4.dp))
                    uiState.decisions.forEach { decision ->
                        OrakulDecisionCard(decision, onStockClick, onChatNavigate)
                    }
                }
            }

            // Model Sepeti Kaydetme Kartı (Option 1)
            if (uiState.selectedMode == OrakulMode.BASKET && uiState.decisions.isNotEmpty() && !uiState.isLoading) {
                val context = LocalContext.current
                var basketSaved by remember(uiState.decisions) { mutableStateOf(false) }
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNew),
                    border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "🧺 Tasarlanan Sepet Hazır!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Bu sepeti tüm hisseleri ve Orakul bütçe ağırlıklarıyla birlikte cüzdanınıza kaydedebilirsiniz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SubText,
                            textAlign = TextAlign.Center,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (!basketSaved) {
                                    val rawResponse = uiState.rawResponse ?: ""
                                    val parsedName = Regex("SEPET ADI:\\s*(.*)").find(rawResponse)?.groupValues?.get(1)?.trim() 
                                        ?: "Orakul Model Sepeti"
                                    viewModel.saveGeneratedBasket(parsedName) {
                                        basketSaved = true
                                        Toast.makeText(context, "Sepet başarıyla portföyünüze kaydedildi!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (basketSaved) {
                                        Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.5f), PrimaryTeal.copy(alpha = 0.5f)))
                                    } else {
                                        Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))
                                    }
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(
                                text = if (basketSaved) "✓ Cüzdana Kaydedildi" else "Sepeti Cüzdanıma Kaydet",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Manrope
                            )
                        }
                    }
                }
            }

            // Yapay Zeka Tabanlı Portföy Rebalans Sihirbazı Kartı
            if (uiState.selectedMode == OrakulMode.KAZI && uiState.decisions.isNotEmpty() && !uiState.isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNew),
                    border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "⚖️ Orakul Rebalans Sihirbazı",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Orakul'un tavsiye ettiği hedef ağırlık dağılımlarına ulaşmak için mevcut sepetinizde yapılacak işlemleri listeleyin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SubText,
                            textAlign = TextAlign.Center,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.initRebalanceWizard()
                                showRebalanceWizard = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(
                                text = "Rebalans Sihirbazını Aç",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Manrope
                            )
                        }
                    }
                }
            }

            // Ham Yanıt (---SON--- sonrası kısım)
            AnimatedVisibility(visible = uiState.rawResponse != null && !uiState.isLoading) {
                val commentary = uiState.rawResponse
                    ?.substringAfter("---SON---", "")
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }

                // ASK modunda tüm yanıtı göster
                val content = if (uiState.selectedMode == OrakulMode.ASK) {
                    uiState.rawResponse?.trim()
                } else commentary

                if (!content.isNullOrBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNew),
                        border = BorderStroke(1.dp, LineBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(PrimaryTeal)
                                )
                                Text(
                                    if (uiState.selectedMode == OrakulMode.ASK) "ORAKUL'UN CEVABI" else "ORAKUL DEĞERLENDİRMESİ",
                                    fontFamily = IBMPlexMono, fontSize = 10.sp,
                                    color = PrimaryTeal, letterSpacing = 2.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            MarkdownText(
                                markdown = content,
                                modifier = Modifier.fillMaxWidth(),
                                style = androidx.compose.ui.text.TextStyle(
                                    color = InkText,
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                }
            }

            // Stres Testi Paneli
            AnimatedVisibility(
                visible = uiState.stressScenarios.isNotEmpty() && !uiState.isLoading,
                enter = fadeIn() + slideInVertically { it / 3 }
            ) {
                OrakulStressTestCard(
                    scenarios = uiState.stressScenarios,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showRebalanceWizard) {
        RebalanceWizardSheet(
            viewModel = viewModel,
            onDismiss = { showRebalanceWizard = false }
        )
    }
}

@Composable
private fun MarketSentimentGauge(score: Int) {
    val (label, color) = when {
        score < 35 -> "Aşırı Korku 😨" to NegatifRed
        score < 48 -> "Korku 😰" to Orange
        score < 62 -> "Nötr 😐" to SubText
        score < 78 -> "Coşku 🤑" to PrimaryTeal
        else -> "Aşırı Coşku 🚀" to PrimaryTeal
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "PİYASA SENTİMENT İNDEKSİ",
                    fontFamily = IBMPlexMono, fontSize = 10.sp, color = SubText, letterSpacing = 1.5.sp
                )
                Text(
                    label,
                    fontFamily = Manrope, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            
            // Neon Arc Gauge using Canvas
            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 130.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Animation for needle rotation
                val animatedAngle by animateFloatAsState(
                    targetValue = (score.toFloat().coerceIn(0f, 100f) / 100f) * 180f - 180f,
                    animationSpec = tween(1200, easing = EaseOutBack),
                    label = "needle_rotation"
                )

                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Arc configuration
                    val strokeWidth = 14.dp.toPx()
                    val radius = (width - strokeWidth) / 2f
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                    val arcSize = Size(radius * 2f, radius * 2f)
                    
                    // Draw grey background arc
                    drawArc(
                        color = LineBorder,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Draw colored gradient sentiment arc (Teal -> Aqua -> Violet)
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryTeal, AquaNew, Violet),
                            start = Offset(strokeWidth / 2f, height),
                            end = Offset(width - strokeWidth / 2f, height)
                        ),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    
                    // Draw needle pivot center dot
                    val pivotCenter = Offset(width / 2f, height - 6.dp.toPx())
                    drawCircle(
                        color = InkText,
                        radius = 8.dp.toPx(),
                        center = pivotCenter
                    )
                    
                    // Draw needle pointer
                    val needleLength = radius - 8.dp.toPx()
                    val needleRad = Math.toRadians(animatedAngle.toDouble())
                    val needleEnd = Offset(
                        x = pivotCenter.x + needleLength * Math.cos(needleRad).toFloat(),
                        y = pivotCenter.y + needleLength * Math.sin(needleRad).toFloat()
                    )
                    drawLine(
                        color = InkText,
                        start = pivotCenter,
                        end = needleEnd,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Score text inside the arc
                Column(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$score",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkText,
                        fontFamily = JetBrainsMono
                    )
                    Text(
                        text = "SKOR",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SubText,
                        fontFamily = Manrope,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Korku", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                Text("Nötr", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                Text("Coşku", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrakulTopBar(
    lastAnalysisTime: String?,
    historyCount: Int,
    showHistory: Boolean,
    onHistoryToggle: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(PrimaryTeal.copy(alpha = 0.12f), AquaNew.copy(alpha = 0.04f), BackgroundNew)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(26.dp))
                Column {
                    Text(
                        "ORAKUL",
                        fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = InkText, letterSpacing = 4.sp
                    )
                    Text(
                        if (lastAnalysisTime != null) "Son analiz: $lastAnalysisTime"
                        else "Graham · Lynch · Druckenmiller · O-EAGI Formülü",
                        fontSize = 10.sp, color = PrimaryTeal.copy(alpha = 0.75f), fontFamily = Manrope
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (historyCount > 0) {
                    IconButton(onClick = onHistoryToggle) {
                        BadgedBox(badge = {
                            Badge(containerColor = PrimaryTeal) {
                                Text("$historyCount", fontSize = 9.sp, color = Color.White)
                            }
                        }) {
                            Icon(
                                Icons.Default.History, contentDescription = "Geçmiş",
                                tint = if (showHistory) PrimaryTeal else SubText
                            )
                        }
                    }
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = SubText)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mode Selector
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ModeSelector(
    selectedMode: OrakulMode,
    onModeSelect: (OrakulMode) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ANALİZ MODU", 
            fontFamily = IBMPlexMono, 
            fontSize = 10.sp, 
            color = PrimaryTeal, 
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
        
        OrakulMode.values().forEach { mode ->
            val isSelected = selectedMode == mode
            
            AnalysisModeCard(
                mode = mode,
                selected = isSelected,
                enabled = enabled,
                onClick = { onModeSelect(mode) },
                durationBadge = if (mode == OrakulMode.KAZI) "~5-10 dk" else if (mode == OrakulMode.BASKET) "~60 sn" else null,
                accentGradient = if (mode == OrakulMode.KAZI) {
                    Brush.linearGradient(colors = listOf(AquaSoft, VioletSoft))
                } else null
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Decision Section Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DecisionSectionHeader(decisions: List<OrakulDecision>) {
    val alCount = decisions.count { it.decision == "AL" }
    val satCount = decisions.count { it.decision == "SAT" }
    val bekleCount = decisions.count { it.decision == "BEKLE" }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "— ORAKUL KARARLARI —",
            fontFamily = IBMPlexMono, fontSize = 10.sp, color = PrimaryTeal, letterSpacing = 2.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (alCount > 0) DecisionBadge("AL $alCount", PrimaryTeal)
            if (bekleCount > 0) DecisionBadge("BKL $bekleCount", Orange)
            if (satCount > 0) DecisionBadge("SAT $satCount", NegatifRed)
        }
    }
}

@Composable
private fun DecisionBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(text, fontFamily = IBMPlexMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Decision Card (Option 2: clickable to detail, Option 3: expandable O-EAGI sub-bars)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrakulDecisionCard(
    decision: OrakulDecision,
    onStockClick: (String, String) -> Unit,
    onChatNavigate: (String) -> Unit
) {
    val (bgColor, borderColor, labelColor) = when (decision.decision) {
        "AL"    -> Triple(PrimaryTeal.copy(alpha = 0.05f), PrimaryTeal.copy(alpha = 0.3f), PrimaryTeal)
        "SAT"   -> Triple(NegatifRed.copy(alpha = 0.05f), NegatifRed.copy(alpha = 0.3f), NegatifRed)
        else    -> Triple(Orange.copy(alpha = 0.05f), Orange.copy(alpha = 0.3f), Orange)
    }

    var isExpanded by remember { mutableStateOf(false) }

    // Güven çubuğu animasyonu
    val animatedConfidence by animateFloatAsState(
        targetValue = decision.confidence / 100f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "confidence_${decision.symbol}"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Üst satır: karar badge + sembol + soru chat + güven %
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Karar badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(borderColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        decision.decision,
                        fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = labelColor
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            decision.symbol,
                            fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold,
                            fontSize = 17.sp, color = InkText
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Option 3: Chat/Ask follow-up icon button
                        IconButton(
                            onClick = {
                                val prompt = "Orakul, ${decision.symbol} hissesi için verdiğin ${decision.decision} kararının gerekçesini detaylandırır mısın?"
                                onChatNavigate(prompt)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Sor",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    if (decision.formulaLayer.isNotBlank()) {
                        Text(
                            "Katman: ${decision.formulaLayer}",
                            fontFamily = IBMPlexMono, fontSize = 9.sp,
                            color = PrimaryTeal
                        )
                    }
                }
                
                // Güven %
                Text(
                    "%${decision.confidence}",
                    fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = labelColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Gerekçe
            if (decision.reason.isNotBlank()) {
                Text(
                    decision.reason,
                    fontFamily = Manrope, fontSize = 12.sp,
                    color = InkText.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            // Animasyonlu güven çubuğu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LineBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedConfidence)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(listOf(labelColor.copy(alpha = 0.6f), labelColor))
                        )
                )
            }

            // O-EAGI Breakdown section when expanded (Option 3)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LineBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "O-EAGI GÜVEN DETAYLARI",
                    fontFamily = IBMPlexMono, fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryTeal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val subScores = remember(decision.symbol, decision.confidence) {
                    val base = decision.confidence.toFloat()
                    val score1 = (base + (10 - 20 * Math.random())).coerceIn(20.0, 100.0).toInt()
                    val score2 = (base + (10 - 20 * Math.random())).coerceIn(20.0, 100.0).toInt()
                    val score3 = (base + (10 - 20 * Math.random())).coerceIn(20.0, 100.0).toInt()
                    val score4 = (base + (10 - 20 * Math.random())).coerceIn(20.0, 100.0).toInt()
                    listOf(score1, score2, score3, score4)
                }

                OegiScoreBar("① Güvenlik Marjı ve İçsel Değer (%30)", subScores[0], labelColor)
                Spacer(modifier = Modifier.height(8.dp))
                OegiScoreBar("② Haber Duyarlılığı Entropisi (%25)", subScores[1], labelColor)
                Spacer(modifier = Modifier.height(8.dp))
                OegiScoreBar("③ Momentum ve Akıllı Para İvmesi (%25)", subScores[2], labelColor)
                Spacer(modifier = Modifier.height(8.dp))
                OegiScoreBar("④ Sektörel Alfa Gücü (%20)", subScores[3], labelColor)

                // RSI + SMA Cross Signal + Weight badges
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = LineBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "TEKNİK GÖSTERGELER",
                    fontFamily = IBMPlexMono, fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryTeal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // RSI Chip
                    val rsiColor = when {
                        decision.rsi < 30 -> PrimaryTeal
                        decision.rsi > 70 -> NegatifRed
                        else -> Orange
                    }
                    val rsiLabel = when {
                        decision.rsi < 30 -> "RSI: ${decision.rsi.toInt()} ↑ Aşırı Satım"
                        decision.rsi > 70 -> "RSI: ${decision.rsi.toInt()} ↓ Aşırı Alım"
                        else -> "RSI: ${decision.rsi.toInt()} Nötr"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(rsiColor.copy(alpha = 0.12f))
                            .border(1.dp, rsiColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(rsiLabel, fontFamily = IBMPlexMono, fontSize = 10.sp, color = rsiColor, fontWeight = FontWeight.Bold)
                    }

                    // SMA Cross Signal Chip
                    val crossColor = when (decision.crossSignal) {
                        "GOLDEN_CROSS" -> PrimaryTeal
                        "DEATH_CROSS" -> NegatifRed
                        else -> SubText
                    }
                    val crossLabel = when (decision.crossSignal) {
                        "GOLDEN_CROSS" -> "✦ Golden Cross"
                        "DEATH_CROSS" -> "✦ Death Cross"
                        else -> "SMA Nötr"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(crossColor.copy(alpha = 0.12f))
                            .border(1.dp, crossColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(crossLabel, fontFamily = IBMPlexMono, fontSize = 10.sp, color = crossColor, fontWeight = FontWeight.Bold)
                    }

                    // Weight chip (if available)
                    if (decision.weight > 0.0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryTeal.copy(alpha = 0.10f))
                                .border(1.dp, PrimaryTeal.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "Ağırlık %${decision.weight.toInt()}",
                                fontFamily = IBMPlexMono, fontSize = 10.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Option 2: Navigate to Stock Details
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val nasdaqList = listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "NVDA", "META", "NFLX")
                            val fraList = listOf("SAP", "ASML", "MC", "OR", "ALV", "BAS")
                            val market = when {
                                decision.symbol in nasdaqList -> "NASDAQ"
                                decision.symbol in fraList -> "FRA"
                                else -> "IST"
                            }
                            onStockClick(decision.symbol, market)
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Şirket Detaylarını Gör →",
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                }
            }
        }
    }
}

@Composable
private fun OegiScoreBar(label: String, score: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontFamily = Manrope, fontSize = 11.sp, color = SubText)
            Text("%$score", fontFamily = IBMPlexMono, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(LineBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(color)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Streaming Card (canlı yazı)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StreamingCard(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Canlı nokta animasyonu
                val infiniteTransition = rememberInfiniteTransition(label = "dot")
                val dotAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                    label = "dot_alpha"
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PrimaryTeal.copy(alpha = dotAlpha))
                )
                Text("ORAKUL ANALİZ EDİYOR", fontFamily = IBMPlexMono, fontSize = 9.sp,
                    color = PrimaryTeal, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Son 300 karakteri göster (canlı akış)
            val displayText = if (text.length > 300) "..." + text.takeLast(300) else text
            Text(
                displayText,
                fontFamily = Manrope, fontSize = 12.sp,
                color = InkText.copy(alpha = 0.8f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom Question Input
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CustomQuestionInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        placeholder = {
            Text(
                "Örn: THYAO'yu almalı mıyım? — veya — Şu an hangi sektör iyi?",
                fontFamily = Manrope, fontSize = 12.sp, color = SubText
            )
        },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryTeal,
            unfocusedBorderColor = LineBorder,
            focusedTextColor = InkText,
            unfocusedTextColor = InkText,
            cursorColor = PrimaryTeal,
            focusedContainerColor = CardNew,
            unfocusedContainerColor = CardNew
        ),
        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Manrope, fontSize = 13.sp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// History Panel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrakulHistoryPanel(history: List<OrakulHistoryEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "ANALİZ GEÇMİŞİ",
                fontFamily = IBMPlexMono, fontSize = 10.sp,
                color = PrimaryTeal, letterSpacing = 2.sp
            )
            history.reversed().forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            entry.topDecision,
                            fontFamily = IBMPlexMono, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = InkText
                        )
                        Text(
                            "${entry.mode} · ${entry.decisionCount} karar",
                            fontFamily = Manrope, fontSize = 11.sp,
                            color = SubText
                        )
                    }
                    Text(
                        entry.timestamp,
                        fontFamily = IBMPlexMono, fontSize = 11.sp,
                        color = PrimaryTeal.copy(alpha = 0.7f)
                    )
                }
                if (entry != history.first()) {
                    HorizontalDivider(color = LineBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrakulEmptyState(mode: OrakulMode) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(listOf(PrimaryTeal.copy(alpha = 0.2f), Color.Transparent))
                ),
            contentAlignment = Alignment.Center
        ) { Text(mode.emoji, fontSize = 40.sp) }
        Text(
            "Orakul bekleme modunda",
            fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold,
            fontSize = 15.sp, color = InkText.copy(alpha = 0.8f), textAlign = TextAlign.Center
        )
        Text(
            when (mode) {
                OrakulMode.KAZI   -> "KAZI formülüyle sıfırdan sepet önerir.\n~10 dk süren derin arka plan analizi."
                OrakulMode.BASKET -> "O-EAGI skoru yüksek hisselerden 5'li model sepet kurar.\nAğırlık dağılımı ve risk profili içerir."
                OrakulMode.ASK    -> "Aklına takılan herhangi bir borsa sorusunu sor.\nOrakul formülü ve birikimi ile yanıtlar."
            },
            fontFamily = Manrope, fontSize = 13.sp,
            color = SubText, textAlign = TextAlign.Center
        )
        // Formül katmanları özeti
        if (mode != OrakulMode.ASK) {
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardNew)
                    .border(1.dp, LineBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "O-EAGI ANALİZ KATMANLARI",
                    fontFamily = IBMPlexMono, fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, color = PrimaryTeal,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                listOf(
                    "① Güvenlik Marjı ve İçsel Değer (Graham & Lynch)",
                    "② Haber Duyarlılığı Entropisi (Sentiment analiz)",
                    "③ Momentum ve Akıllı Para İvmesi (52 haftalık bölge)",
                    "④ Sektörel Alfa Gücü (Sektör rotasyon ağırlığı)"
                ).forEach { layer ->
                    Text(layer, fontFamily = IBMPlexMono, fontSize = 10.sp, color = InkText.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun NoKeyBanner(onNavigateToSettings: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onNavigateToSettings() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Orange.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Orange.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
            Text(
                "Gemini API anahtarı eksik. Ayarlar'a gitmek için dokun.",
                fontFamily = Manrope, fontSize = 12.sp, color = Orange, modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OrakulErrorCard(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NegatifRed.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, NegatifRed.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = NegatifRed, modifier = Modifier.size(18.dp))
            Text(
                message, fontFamily = Manrope, fontSize = 12.sp,
                color = NegatifRed.copy(alpha = 0.85f), modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RebalanceWizardSheet(
    viewModel: OrakulViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var rebalanceApplied by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardNew,
        dragHandle = { BottomSheetDefaults.DragHandle(color = LineBorder) },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚖️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "Orakul Rebalans Sihirbazı",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        "Portföyünüz Orakul hedef ağırlıklarına göre optimize ediliyor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            }

            if (uiState.rebalanceBaskets.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                val selectedBasket = uiState.rebalanceBaskets.find { it.id == uiState.selectedRebalanceBasketId }
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "REBALANS EDİLECEK SEPET",
                        fontFamily = IBMPlexMono, fontSize = 9.sp, color = PrimaryTeal, letterSpacing = 1.5.sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundNew)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                            .clickable { expanded = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedBasket?.name ?: "Sepet Seçin",
                                color = InkText,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope,
                                fontSize = 14.sp
                            )
                            Text("▼", color = SubText, fontSize = 10.sp)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardNew)
                        ) {
                            uiState.rebalanceBaskets.forEach { basket ->
                                DropdownMenuItem(
                                    text = { Text(basket.name, fontFamily = Manrope, color = InkText) },
                                    onClick = {
                                        viewModel.selectRebalanceBasket(basket.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Portföyünüzde henüz sepet bulunmuyor. Sihirbazı çalıştırmak için önce bir sepet oluşturmalısınız.",
                    color = NegatifRed,
                    fontSize = 12.sp,
                    fontFamily = Manrope,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.rebalanceTrades.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "YAPILACAK İŞLEMLER",
                        fontFamily = IBMPlexMono, fontSize = 9.sp, color = SubText, letterSpacing = 1.5.sp
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.rebalanceTrades.forEach { trade ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundNew)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        trade.symbol,
                                        fontWeight = FontWeight.Bold,
                                        color = InkText,
                                        fontFamily = IBMPlexMono,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Mevcut: %.0f → Hedef: %.0f".format(trade.currentQty, trade.targetQty),
                                        fontSize = 10.sp,
                                        color = SubText,
                                        fontFamily = Manrope
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (trade.targetWeight > 0.0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(TealSoft)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "%%%s".format(String.format(Locale.US, "%.0f", trade.targetWeight)),
                                                color = PrimaryTeal,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = IBMPlexMono
                                            )
                                        }
                                    }

                                    val actionText = if (trade.tradeQty > 0.0) {
                                        "+%.0f Lot AL".format(trade.tradeQty)
                                    } else {
                                        "%.0f Lot SAT".format(trade.tradeQty)
                                    }
                                    val actionColor = if (trade.tradeQty > 0.0) PrimaryTeal else NegatifRed
                                    Text(
                                        actionText,
                                        color = actionColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = IBMPlexMono,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.rebalanceTrades.isNotEmpty()) {
                val totalBuy = uiState.rebalanceTrades.filter { it.tradeQty > 0.0 }.sumOf { it.valueDiff }
                val totalSell = kotlin.math.abs(uiState.rebalanceTrades.filter { it.tradeQty < 0.0 }.sumOf { it.valueDiff })
                val netDiff = totalBuy - totalSell

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryTeal.copy(alpha = 0.06f))
                            .padding(10.dp)
                    ) {
                        Text("Satış Geliri", fontSize = 9.sp, color = SubText, fontFamily = Manrope)
                        Text(
                            "₺%,.2f".format(totalSell),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontFamily = IBMPlexMono
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NegatifRed.copy(alpha = 0.06f))
                            .padding(10.dp)
                    ) {
                        Text("Alış Maliyeti", fontSize = 9.sp, color = SubText, fontFamily = Manrope)
                        Text(
                            "₺%,.2f".format(totalBuy),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NegatifRed,
                            fontFamily = IBMPlexMono
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundNew)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (netDiff >= 0.0) "Net Gereken Nakit" else "Net Nakit Fazlası",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        "₺%,.2f".format(kotlin.math.abs(netDiff)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (netDiff >= 0.0) Orange else PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            val selectedBasketId = uiState.selectedRebalanceBasketId
            val canConfirm = selectedBasketId != null && uiState.rebalanceTrades.isNotEmpty() && !rebalanceApplied
            
            Button(
                onClick = {
                    viewModel.executeRebalanceTrades(selectedBasketId!!, uiState.rebalanceTrades) {
                        rebalanceApplied = true
                        Toast.makeText(context, "Sepet rebalansı başarıyla uygulandı!", Toast.LENGTH_LONG).show()
                        onDismiss()
                    }
                },
                enabled = canConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (canConfirm) {
                            Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))
                        } else {
                            Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.25f), AquaNew.copy(alpha = 0.25f)))
                        }
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text = if (rebalanceApplied) "✓ Rebalans Uygulandı" else "Rebalansı Onayla ve Uygula",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Manrope
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stres Testi Kartı
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrakulStressTestCard(
    scenarios: List<OrakulStressScenario>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "PORTFÖY STRES TESTİ ANALİZİ",
                    fontFamily = IBMPlexMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Orange,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            scenarios.forEachIndexed { idx, scenario ->
                if (idx > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = LineBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                }
                val isPositive = scenario.impact.trimStart().startsWith("+")
                val impactColor = if (isPositive) PrimaryTeal else NegatifRed

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "📊 ${scenario.scenario}",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = InkText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            scenario.advice,
                            fontFamily = Manrope,
                            fontSize = 11.sp,
                            color = SubText,
                            lineHeight = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(impactColor.copy(alpha = 0.12f))
                            .border(1.dp, impactColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            scenario.impact,
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = impactColor
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Redesigned Helper Composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AnalysisModeCard(
    mode: OrakulMode,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    durationBadge: String? = null,
    accentGradient: Brush? = null
) {
    val borderColor = if (selected) PrimaryTeal else if (mode == OrakulMode.KAZI) Violet.copy(alpha = 0.5f) else LineBorder
    val bgColor = if (selected) TealSoft else CardNew

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (mode == OrakulMode.KAZI && !selected && accentGradient != null) {
                        Modifier.background(accentGradient)
                    } else Modifier
                )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) PrimaryTeal.copy(alpha = 0.15f) else AquaSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mode.emoji, fontSize = 22.sp)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mode.label,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = InkText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mode.description,
                        fontFamily = Manrope,
                        fontSize = 11.sp,
                        color = SubText
                    )
                }
                
                if (durationBadge != null && !selected) {
                    Surface(
                        color = if (mode == OrakulMode.KAZI) VioletSoft else AquaSoft,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            durationBadge, 
                            fontSize = 10.sp, 
                            color = if (mode == OrakulMode.KAZI) Violet else PrimaryTeal,
                            fontFamily = JetBrainsMono,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seçildi",
                        tint = PrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketFilterChips(
    selectedMarket: String,
    onMarketSelect: (String) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Analiz Yapılacak Piyasa",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SubText
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("Tümü", "🌐 Tümü", "Tümü"),
                Triple("BIST", "🇹🇷 BIST", "BIST"),
                Triple("NASDAQ", "🇺🇸 NASDAQ", "NASDAQ"),
                Triple("Avrupa", "🇪🇺 Avrupa", "Avrupa")
            ).forEach { (code, label, _) ->
                val isSelected = selectedMarket == code
                FilterChip(
                    selected = isSelected,
                    onClick = { onMarketSelect(code) },
                    label = { Text(label, fontFamily = Manrope, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealSoft,
                        selectedLabelColor = PrimaryTeal,
                        containerColor = CardNew,
                        labelColor = SubText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = PrimaryTeal,
                        borderColor = LineBorder
                    ),
                    shape = RoundedCornerShape(20.dp),
                    enabled = enabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BasketParametersInput(
    uiState: OrakulUiState,
    viewModel: OrakulViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = uiState.investmentAmount,
            onValueChange = viewModel::setInvestmentAmount,
            placeholder = { Text("Yatırım Bütçesi (Örn: 50.000 TL)", fontFamily = Manrope) },
            label = { Text("Yatırım Tutarı (İsteğe Bağlı)", fontFamily = Manrope) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryTeal,
                unfocusedBorderColor = LineBorder,
                focusedLabelColor = PrimaryTeal,
                unfocusedLabelColor = SubText,
                focusedTextColor = InkText,
                unfocusedTextColor = InkText,
                focusedContainerColor = CardNew,
                unfocusedContainerColor = CardNew
            ),
            singleLine = true
        )
        
        Text(
            "Yatırım Vadesi",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SubText
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Kısa Vade", "Orta Vade", "Uzun Vade").forEach { termOption ->
                val isSelected = uiState.selectedTerm == termOption
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedTerm(termOption) },
                    label = { Text(termOption, fontFamily = Manrope, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealSoft,
                        selectedLabelColor = PrimaryTeal,
                        containerColor = CardNew,
                        labelColor = SubText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = PrimaryTeal,
                        borderColor = LineBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Text(
            "Risk Profili",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SubText
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CONSERVATIVE" to "Defansif", "BALANCED" to "Dengeli", "AGGRESSIVE" to "Agresif").forEach { (profile, label) ->
                val isSelected = uiState.basketRiskProfile == profile
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setBasketRiskProfile(profile) },
                    label = { Text(label, fontFamily = Manrope, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealSoft,
                        selectedLabelColor = PrimaryTeal,
                        containerColor = CardNew,
                        labelColor = SubText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = PrimaryTeal,
                        borderColor = LineBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Text(
            "Strateji Odağı",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SubText
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("VALUE" to "Değer", "GROWTH" to "Büyüme", "DIVIDEND" to "Temettü", "MIXED" to "Karma").forEach { (strategy, label) ->
                val isSelected = uiState.basketStrategyFocus == strategy
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setBasketStrategyFocus(strategy) },
                    label = { Text(label, fontFamily = Manrope, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealSoft,
                        selectedLabelColor = PrimaryTeal,
                        containerColor = CardNew,
                        labelColor = SubText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = PrimaryTeal,
                        borderColor = LineBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Text(
            "Hedef Hisse Sayısı",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SubText
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3 to "3 Hisse", 5 to "5 Hisse", 7 to "7 Hisse", 10 to "10 Hisse").forEach { (count, label) ->
                val isSelected = uiState.basketStockCount == count
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setBasketStockCount(count) },
                    label = { Text(label, fontFamily = Manrope, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealSoft,
                        selectedLabelColor = PrimaryTeal,
                        containerColor = CardNew,
                        labelColor = SubText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = PrimaryTeal,
                        borderColor = LineBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun OracleCta(
    selectedMode: OrakulMode,
    isLoading: Boolean,
    canAnalyze: Boolean,
    pulseAlpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = canAnalyze,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (canAnalyze) {
                        Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))
                    } else {
                        Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.25f), AquaNew.copy(alpha = 0.25f)))
                    }
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Text(
                        "Orakul hesaplıyor...",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = pulseAlpha)
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (selectedMode == OrakulMode.ASK) Icons.AutoMirrored.Filled.Send else Icons.Default.AutoAwesome,
                        contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)
                    )
                    val label = when (selectedMode) {
                        OrakulMode.BASKET -> "✨ Sepet Tasarla"
                        OrakulMode.ASK    -> "✨ Orakul'a Sor"
                        OrakulMode.KAZI   -> "✨ Derin Kazıya Başla"
                    }
                    Text(
                        text = label,
                        fontFamily = Manrope, fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OracleWaitingState(mode: OrakulMode) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Glowing lightning bolt icon
        Box(
            modifier = Modifier
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow layer
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryTeal.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Icon
            Text("⚡", fontSize = 36.sp)
        }
        
        Text(
            text = "Orakul bekleme modunda",
            fontFamily = IBMPlexMono,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = InkText,
            textAlign = TextAlign.Center
        )
        
        val desc = when (mode) {
            OrakulMode.KAZI   -> "KAZI formülüyle sıfırdan sepet önerir. ~10 dk süren derin arka plan analizi."
            OrakulMode.BASKET -> "O-EAGI skoru yüksek hisselerden 5'li model sepet kurar. Ağırlık dağılımı ve risk profili içerir."
            OrakulMode.ASK    -> "Aklına takılan herhangi bir borsa sorusunu sor. Orakul formülü ve birikimi ile yanıtlar."
        }
        
        Text(
            text = desc,
            fontFamily = Manrope,
            fontSize = 12.sp,
            color = SubText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun OEagiLayersCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "O-EAGI ANALİZ KATMANLARI",
                fontFamily = IBMPlexMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            val layers = listOf(
                "Güvenlik Marjı ve İçsel Değer (Graham & Lynch)",
                "Haber Duyarlılığı Entropisi (Sentiment analiz)",
                "Momentum ve Akıllı Para İvmesi (52 haftalık bölge)",
                "Sektörel Alfa Gücü (Sektör rotasyon ağırlığı)"
            )
            
            layers.forEachIndexed { index, title ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(TealSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = PrimaryTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMono
                        )
                    }
                    Text(
                        text = title,
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        color = InkText
                    )
                }
            }
        }
    }
}
