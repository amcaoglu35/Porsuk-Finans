package com.nexus.porsuk.ui.ailab

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.nexus.porsuk.ui.chat.ChatMessage
import com.nexus.porsuk.ui.chat.ChatViewModel
import com.nexus.porsuk.ui.common.VoiceInputManager
import com.nexus.porsuk.ui.theme.*

// Semantic colors
private val BullishGreen = Color(0xFF10B981)
private val SuccessGreen = Color(0xFF10B981)
private val BearishRed = Color(0xFFEF4444)
private val RiskOrange = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiLabScreen(
    viewModel: ChatViewModel,
    labViewModel: AiLabViewModel,
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
    val labState by labViewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val toolHistory by labViewModel.toolHistory.collectAsState()
    var selectedToolForReport by remember { mutableStateOf<String?>(null) }

    val voiceInputManager = remember { VoiceInputManager(context) }
    var isListening by remember { mutableStateOf(false) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceInputManager.startListening(
                onResult = { textInput = it },
                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                onStateChange = { isListening = it }
            )
        } else {
            Toast.makeText(context, "Sesli giriş için mikrofon izni gereklidir.", Toast.LENGTH_SHORT).show()
        }
    }

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
        containerColor = MaterialTheme.colorScheme.background,
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
                        messages = messages,
                        textInput = textInput,
                        onTextInputChange = { textInput = it },
                        onSendMessage = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        onVoiceInput = {
                            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                voiceInputManager.startListening(
                                    onResult = { textInput = it },
                                    onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
                                    onStateChange = { isListening = it }
                                )
                            } else {
                                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        isListening = isListening,
                        onNewChat = {
                            textInput = ""
                            Toast.makeText(context, "✨ Yeni AI Sohbeti Başlatıldı", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 3. AI Araçları (15 Interactive Feature Cards Grid)
            item(key = "ai_tools_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    AiToolsGridSection(
                        onToolClick = { toolName ->
                            selectedToolForReport = toolName
                            labViewModel.runTool(toolName)
                        }
                    )
                }
            }

            // AI Tool Report Display
            if (selectedToolForReport != null) {
                item(key = "tool_report_card") {
                    ToolReportCard(
                        toolName = selectedToolForReport!!,
                        report = labState.toolReports[selectedToolForReport!!],
                        isLoading = labState.toolLoadingStates[selectedToolForReport!!] ?: false,
                        error = labState.toolErrorStates[selectedToolForReport!!],
                        onRetry = { labViewModel.runTool(selectedToolForReport!!) },
                        onClose = { selectedToolForReport = null }
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
                        onAutoRebalanceToggle = { enabled ->
                            autoRebalanceEnabled = enabled
                            if (enabled) {
                                labViewModel.executeAutoRebalance()
                                Toast.makeText(context, "Otomatik dengeleme başlatıldı.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        riskAlertsEnabled = riskAlertsEnabled,
                        onRiskAlertsToggle = { enabled ->
                            riskAlertsEnabled = enabled
                            labViewModel.setRiskMonitoring(enabled, context)
                            val msg = if (enabled) "Risk takibi aktif." else "Risk takibi kapatıldı."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        nightSummaryEnabled = nightSummaryEnabled,
                        onNightSummaryToggle = { enabled ->
                            nightSummaryEnabled = enabled
                            labViewModel.setNightSummary(enabled, context)
                            val msg = if (enabled) "Gece özeti planlandı (21:00)." else "Gece özeti iptal edildi."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
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

            // 11. Tool History Section
            if (toolHistory.isNotEmpty()) {
                item(key = "tool_history_header") {
                    Text(
                        "📜 Son Analiz Geçmişi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                
                items(toolHistory.reversed(), key = { it.id }) { item ->
                    ToolHistoryItem(item)
                }
            }
        }
    }
}

@Composable
fun ToolHistoryItem(history: ToolReportHistory) {
    val sdf = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()) }
    val dateString = remember(history.timestamp) { sdf.format(java.util.Date(history.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = history.toolName,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = history.report.take(150).replace("\n", " ") + "...",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🚧", fontSize = 42.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
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
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Bu özellik geliştirilmektedir.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                            color = MaterialTheme.colorScheme.primary
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
            .background(MaterialTheme.colorScheme.background)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            "🤖 AI Lab",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = MaterialTheme.colorScheme.primary)
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
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary
                    )
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
    messages: List<ChatMessage>,
    textInput: String,
    onTextInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceInput: () -> Unit,
    isListening: Boolean,
    onNewChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Chat History Scroll Area
        if (messages.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages.size) { index ->
                    ChatMessageItem(messages[index])
                }
            }
        }

        // Chat Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                        Text("AI Asistan Sohbeti", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable(onClick = onNewChat)
                    ) {
                        Text("✨ Yeni Sohbet", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = onTextInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("AI Asistana bir soru sorun (Örn: THYAO hedef fiyatı?)...", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onVoiceInput) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Outlined.Mic,
                                    contentDescription = "Sesli Giriş",
                                    tint = if (isListening) BullishGreen else MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = onSendMessage) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gönder", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        val bgColor = if (message.isUser) 
            MaterialTheme.colorScheme.primary
        else 
            MaterialTheme.colorScheme.surfaceVariant
        
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
            )
        }
    }
}

// ── 3. AI ARAÇLARI (15 Interactive Cards Grid) ──
@Composable
private fun AiToolsGridSection(onToolClick: (String) -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val tools = remember(primaryColor) {
        listOf(
            AiToolItem("Portfolio Health Check", "🩺", BullishGreen),
            AiToolItem("Stock Compare", "⚖️", primaryColor),
            AiToolItem("Sector Compare", "🏢", Color(0xFF3B82F6)),
            AiToolItem("AI Screener", "🔍", RiskOrange),
            AiToolItem("Dividend Finder", "💰", BullishGreen),
            AiToolItem("Growth Finder", "🚀", Color(0xFFEC4899)),
            AiToolItem("Value Finder", "💎", RiskOrange),
            AiToolItem("Momentum Finder", "⚡", primaryColor),
            AiToolItem("Risk Scanner", "📡", BearishRed),
            AiToolItem("Portfolio Diversification", "🧩", Color(0xFF8B5CF6)),
            AiToolItem("AI Opportunity Finder", "🌟", SuccessGreen),
            AiToolItem("AI Watchlist Analyzer", "👁️", Color(0xFF06B6D4)),
            AiToolItem("AI Earnings Summary", "📊", primaryColor),
            AiToolItem("AI News Summary", "📰", Color(0xFF64748B)),
            AiToolItem("Economic Impact Analyzer", "🌐", Color(0xFFF59E0B))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("🛠️ AI Uzman Araçları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
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

@Composable
private fun ToolReportCard(
    toolName: String,
    report: String?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(toolName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI Uzmanı analiz yapıyor...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (error != null) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hata: $error", color = BearishRed, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text("Tekrar Dene")
                    }
                }
            } else if (report != null) {
                dev.jeziellago.compose.markdowntext.MarkdownText(
                    markdown = report,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                )
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
                color = MaterialTheme.colorScheme.onSurface,
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🔮", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Oracle Kehaneti", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text("Piyasa yön tahminleri ve AI sinyalleri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick = onOracleClick, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("Oracle'ı Aç", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Portföy Doktoru Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onDoctorClick),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🩺", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Portföy Doktoru", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text("Portföy risk ve sağlık puanlaması", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 26.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI Öğrenme Modeli", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text("Kişisel yatırım stilinizi öğrenen AI modeli", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = onLearningClick, shape = RoundedCornerShape(12.dp)) {
                    Text("Detaylı Rapor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Otomasyonları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔔", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Akıllı Bildirimler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text("Bildirim merkezine git", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(
                modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(22.dp)).clickable(onClick = onPdfReportsClick),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📄", fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Son AI Raporları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    Text("PDF rapor görüntüleyici", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp), color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
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
        Text("⚡ Hızlı Eylemler", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

// ── PREVIEW SUPPORT ──
@Preview(showBackground = true)
@Composable
private fun AiLabTopBarPreview() {
    AiLabTopBar(onSearchClick = {}, onNotificationClick = {})
}
