package com.nexus.porsuk.ui.ailab

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.chat.ChatViewModel
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
fun AiLabScreen(
    viewModel: ChatViewModel,
    onNavigateToSettings: () -> Unit = {},
    initialPrompt: String? = null,
    onStockClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToOracle: () -> Unit = {},
    onNavigateToDoctor: () -> Unit = {},
    onNavigateToSimulator: () -> Unit = {},
    onNavigateToOpportunityCenter: () -> Unit = {},
    onNavigateToAlarmCenter: () -> Unit = {},
    onNavigateToAiPerformance: () -> Unit = {},
    onNavigateToStrategyBuilder: () -> Unit = {},
    onNavigateToGlobalIntelligence: () -> Unit = {},
    onNavigateToPlaceholder: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }

    // Persistent Switch States (DataStore / rememberSaveable)
    var autoRebalanceEnabled by rememberSaveable { mutableStateOf(true) }
    var riskAlertsEnabled by rememberSaveable { mutableStateOf(true) }
    var nightSummaryEnabled by rememberSaveable { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        if (!initialPrompt.isNullOrBlank()) {
            viewModel.sendMessage(initialPrompt)
        }
    }

    // Breathing pulse animation for 3D AI Robot avatar
    val infiniteTransition = rememberInfiniteTransition(label = "robot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            AiLabTopBar(
                onSearchClick = { Toast.makeText(context, "AI Lab arama moduna geçildi", Toast.LENGTH_SHORT).show() },
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Standardized 24dp Card Spacing
        ) {
            // 1. AI Karşılama Kartı (Hero Header Card)
            item(key = "ai_greeting_hero") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    AiGreetingHeroCard(pulseScale = pulseScale)
                }
            }

            // 2. AI Sohbeti Kartı (Chat & Voice & New Chat)
            item(key = "ai_chat_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AiChatCardSection(
                        textInput = textInput,
                        onTextInputChange = { textInput = it },
                        onSendMessage = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        onVoiceInput = {
                            Toast.makeText(context, "🎙️ Sesli dinleme başlatıldı...", Toast.LENGTH_SHORT).show()
                        },
                        onNewChat = {
                            textInput = ""
                            Toast.makeText(context, "✨ Yeni AI Sohbeti Başlatıldı", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 3. AI Araçları (10 Interactive Feature Cards Grid)
            item(key = "ai_tools_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    AiToolsGridSection(
                        onToolClick = { toolName ->
                            when (toolName) {
                                "Oracle" -> onNavigateToOracle()
                                "Portföy Doktoru" -> onNavigateToDoctor()
                                "Risk Analizi" -> onNavigateToDoctor()
                                "Akıllı Tarayıcı" -> onNavigateToOpportunityCenter()
                                "Akıllı Bildirimler" -> onNavigateToAlarmCenter()
                                "Senaryo Simülasyonu" -> onNavigateToSimulator()
                                "Makro Analiz" -> onNavigateToGlobalIntelligence()
                                "Strateji Simülatörü" -> onNavigateToStrategyBuilder()
                                else -> onNavigateToPlaceholder(toolName)
                            }
                        }
                    )
                }
            }

            // 4. Oracle & 5. Portföy Doktoru & 7. AI Öğrenme (3 Feature Cards)
            item(key = "oracle_doctor_learning_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    TripleAiModulesGridSection(
                        onOracleClick = onNavigateToOracle,
                        onDoctorClick = onNavigateToDoctor,
                        onLearningClick = onNavigateToAiPerformance
                    )
                }
            }

            // 8. Akıllı Bildirimler & 9. AI Otomasyonları & 10. Son AI Raporları
            item(key = "notifications_automations_reports_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    TripleNotificationsAndReportsGridSection(
                        onNotificationCenterClick = onNavigateToSettings,
                        onPdfReportsClick = { onNavigateToPlaceholder("AI PDF Raporları") },
                        autoRebalanceEnabled = autoRebalanceEnabled,
                        onAutoRebalanceToggle = { autoRebalanceEnabled = it },
                        riskAlertsEnabled = riskAlertsEnabled,
                        onRiskAlertsToggle = { riskAlertsEnabled = it },
                        nightSummaryEnabled = nightSummaryEnabled,
                        onNightSummaryToggle = { nightSummaryEnabled = it }
                    )
                }
            }

            // 3. Hızlı Eylemler (4 Working Buttons)
            item(key = "quick_actions_buttons") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                ) {
                    QuickActionsGridSection(
                        onStartChat = { viewModel.sendMessage("Merhaba, bugünkü piyasaları değerlendirir misin?") },
                        onRunOracle = onNavigateToOracle,
                        onScanPortfolio = onNavigateToDoctor,
                        onNewAnalysis = onNavigateToDoctor
                    )
                }
            }
        }
    }
}

// ── 12. BOŞ SAYFA / YAKINDA PLACEHOLDER EKRANI ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isNotified by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightSurfaceBg)
            )
        },
        containerColor = LightSurfaceBg
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PurpleSoftBg,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🚧", fontSize = 42.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PurpleAccent
                    ) {
                        Text(
                            "YAKINDA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Yakında",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = PurpleAccent,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Bu özellik geliştirilmektedir.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp, color = TextSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            "Geri Dön",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            isNotified = !isNotified
                            val msg = if (isNotified) "👍 Bildirim listenize eklendi!" else "Bildirim iptal edildi"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text(
                            if (isNotified) "✓ Bildirimler Açık" else "🔔 Beni Haberdar Et",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                            color = PurpleAccent
                        )
                    }
                }
            }
        }
    }
}

// ── TOP BAR HEADER ──
@Composable
private fun AiLabTopBar(
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
            "🤖 AI Lab",
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

// ── 1. AI KARŞILAMA KARTI (Hero Greeting) ──
@Composable
private fun AiGreetingHeroCard(pulseScale: Float) {
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
                    colors = listOf(Color(0xFF1E0A4C), Color(0xFF3B1578), Color(0xFF6C4CF1))
                )
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) { // 20dp Inner Padding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BullishGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Lab Online", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Merhaba! 👋",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = Color.White
                        )
                        Text(
                            "Bugün portföyünüz ve piyasalar için AI analizleri hazır.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

// ── 2 & 6. AI SOHBETİ KARTI (Chat, Voice, New Chat) ──
@Composable
private fun AiChatCardSection(
    textInput: String,
    onTextInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceInput: () -> Unit,
    onNewChat: () -> Unit
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
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💬", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Asistan Sohbeti", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PurpleSoftBg,
                    modifier = Modifier.clickable(onClick = onNewChat)
                ) {
                    Text("✨ Yeni Sohbet", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = PurpleAccent, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = onTextInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("AI Asistana bir soru sorun (Örn: THYAO hedef fiyatı?)...", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = TextSecondary) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onVoiceInput) {
                            Icon(Icons.Outlined.Mic, contentDescription = "Sesli Giriş", tint = PurpleAccent)
                        }
                        IconButton(onClick = onSendMessage) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gönder", tint = PurpleAccent)
                        }
                    }
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightSurfaceBg,
                    unfocusedContainerColor = LightSurfaceBg,
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = BorderColor
                )
            )
        }
    }
}

// ── 3. AI ARAÇLARI (10 Interactive Cards Grid) ──
@Composable
private fun AiToolsGridSection(onToolClick: (String) -> Unit) {
    val tools = remember {
        listOf(
            AiToolItem("Oracle", "🔮", PurpleAccent),
            AiToolItem("Portföy Doktoru", "🩺", BullishGreen),
            AiToolItem("Akıllı Tarayıcı", "🔍", Color(0xFF3B82F6)),
            AiToolItem("Risk Analizi", "⚖️", RiskOrange),
            AiToolItem("Senaryo Simülasyonu", "🎭", Color(0xFF8B5CF6)),
            AiToolItem("Haber Analizi", "📰", Color(0xFF06B6D4)),
            AiToolItem("Makro Analiz", "🌐", Color(0xFFEC4899)),
            AiToolItem("Temettü Asistanı", "💰", BullishGreen),
            AiToolItem("Vergi Hesaplayıcı", "🧮", TextSecondary),
            AiToolItem("Fon Analizi", "🧺", PurpleAccent)
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
        Column(modifier = Modifier.padding(20.dp)) {
            Text("🛠️ AI Araçları & Modülleri", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tools, key = { it.title }) { tool ->
                    AnimatedAiToolCard(tool = tool, onClick = { onToolClick(tool.title) })
                }
            }
        }
    }
}

private data class AiToolItem(val title: String, val emoji: String, val color: Color)

@Composable
private fun AnimatedAiToolCard(tool: AiToolItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tool_card_scale"
    )

    Surface(
        modifier = Modifier
            .width(105.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        color = tool.color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, tool.color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(tool.emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                tool.title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp, fontFamily = Manrope),
                color = TextDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── 4, 5, 7. TRIPLE AI MODULES (Oracle, Portföy Doktoru, AI Öğrenme) ──
@Composable
private fun TripleAiModulesGridSection(
    onOracleClick: () -> Unit,
    onDoctorClick: () -> Unit,
    onLearningClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Oracle Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onOracleClick),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🔮", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Oracle Kehaneti", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Piyasa yön tahminleri ve AI sinyalleri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                }
                Button(onClick = onOracleClick, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)) {
                    Text("Oracle'ı Aç", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Portföy Doktoru Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onDoctorClick),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🩺", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Portföy Doktoru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Portföy risk ve sağlık puanlaması", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                }
                Button(onClick = onDoctorClick, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = BullishGreen)) {
                    Text("Portföyü Tara", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // AI Öğrenme Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onLearningClick),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Öğrenme Modeli", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Kişisel yatırım stilinizi öğrenen AI modeli", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                }
                OutlinedButton(onClick = onLearningClick, shape = RoundedCornerShape(12.dp)) {
                    Text("Detaylı Rapor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
                }
            }
        }
    }
}

// ── 8, 9, 10. NOTIFICATIONS, AUTOMATIONS & REPORTS ──
@Composable
private fun TripleNotificationsAndReportsGridSection(
    onNotificationCenterClick: () -> Unit,
    onPdfReportsClick: () -> Unit,
    autoRebalanceEnabled: Boolean,
    onAutoRebalanceToggle: (Boolean) -> Unit,
    riskAlertsEnabled: Boolean,
    onRiskAlertsToggle: (Boolean) -> Unit,
    nightSummaryEnabled: Boolean,
    onNightSummaryToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Otomasyonları Card with persistent Switches
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Otomasyonları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
                }

                Spacer(modifier = Modifier.height(12.dp))

                AutomationSwitchRow("Otomatik Portföy Dengelenme", autoRebalanceEnabled, onAutoRebalanceToggle)
                AutomationSwitchRow("AI Risk Uyarısı (Anlık)", riskAlertsEnabled, onRiskAlertsToggle)
                AutomationSwitchRow("Gece Piyasası Rapor Özeti", nightSummaryEnabled, onNightSummaryToggle)
            }
        }

        // Akıllı Bildirimler & Son AI Raporları Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onNotificationCenterClick),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔔", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Akıllı Bildirimler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Bildirim merkezine git", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                }
            }

            Card(
                modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onPdfReportsClick),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📄", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Son AI Raporları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("PDF rapor görüntüleyici", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AutomationSwitchRow(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = TextDark)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurpleAccent)
        )
    }
}

// ── 3. HIZLI EYLEYMLER GRID ──
@Composable
private fun QuickActionsGridSection(
    onStartChat: () -> Unit,
    onRunOracle: () -> Unit,
    onScanPortfolio: () -> Unit,
    onNewAnalysis: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı Eylemler", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionTile("AI Sohbeti Başlat", "💬", onStartChat, Modifier.weight(1f))
            QuickActionTile("Oracle'ı Aç", "🔮", onRunOracle, Modifier.weight(1f))
            QuickActionTile("Portföyü Tara", "🩺", onScanPortfolio, Modifier.weight(1f))
            QuickActionTile("Yeni Analiz", "📈", onNewAnalysis, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionTile(title: String, iconEmoji: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "quick_action_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .shadow(3.dp, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = PurpleSoftBg, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = TextDark, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

// ── PREVIEW SUPPORT ──
@Preview(showBackground = true)
@Composable
private fun AiLabTopBarPreview() {
    AiLabTopBar(onSearchClick = {}, onNotificationClick = {})
}
