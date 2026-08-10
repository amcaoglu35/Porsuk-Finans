package com.nexus.porsuk.ui.ailab

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.chat.ChatViewModel
import com.nexus.porsuk.ui.common.VoiceInputManager
import com.nexus.porsuk.ui.ailab.components.*
import com.nexus.porsuk.ui.theme.*

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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item(key = "ai_greeting_hero") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    AiGreetingHeroCard(pulseScale = pulseScale)
                }
            }

            item(key = "ai_chat_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    AiChatCardSection(
                        messages = messages,
                        inputText = textInput,
                        onInputChange = { textInput = it },
                        onSendMessage = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        onVoiceClick = {
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
                        isAiLoading = labState.isLoading,
                        onClearChat = { textInput = "" }
                    )
                }
            }

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

            item(key = "oracle_doctor_learning_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    TripleAiModulesGridSection(
                        onNavigateToPerformance = onNavigateToAiPerformance,
                        onNavigateToStrategy = onNavigateToStrategyBuilder,
                        onNavigateToIntelligence = onNavigateToGlobalIntelligence
                    )
                }
            }

            item(key = "notifications_automations_reports_grid") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    TripleNotificationsAndReportsGridSection(
                        onNavigateToNotifications = onNavigateToSettings,
                        onNavigateToReports = { onNavigateToPlaceholder("AI PDF Raporları") },
                        isPriceAlertsEnabled = autoRebalanceEnabled,
                        onPriceAlertsToggle = { enabled ->
                            autoRebalanceEnabled = enabled
                            if (enabled) {
                                labViewModel.executeAutoRebalance()
                                Toast.makeText(context, "Otomatik dengeleme başlatıldı.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        isNewsSentimentEnabled = riskAlertsEnabled,
                        onNewsSentimentToggle = { enabled ->
                            riskAlertsEnabled = enabled
                            labViewModel.setRiskMonitoring(enabled, context)
                            val msg = if (enabled) "Risk takibi aktif." else "Risk takibi kapatıldı."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        isAiAutomationEnabled = nightSummaryEnabled,
                        onAiAutomationToggle = { enabled ->
                            nightSummaryEnabled = enabled
                            labViewModel.setNightSummary(enabled, context)
                            val msg = if (enabled) "Gece özeti planlandı (21:00)." else "Gece özeti iptal edildi."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item(key = "quick_actions_buttons") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
                ) {
                    QuickActionsGridSection(
                        onNavigateToDoctor = onNavigateToDoctor,
                        onNavigateToSimulator = onNavigateToSimulator,
                        onNavigateToOpportunities = onNavigateToOpportunityCenter,
                        onNavigateToOptimization = onNavigateToDoctor // Use Doctor as placeholder
                    )
                }
            }

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
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontFamily = Manrope) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚀", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "$title çok yakında burada!",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkText,
                    fontFamily = Manrope
                )
                Text(
                    "Porsuk AI ekibi bu modül üzerinde çalışıyor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    fontFamily = Manrope
                )
            }
        }
    }
}
