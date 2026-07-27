package com.nexus.porsuk.feature.automation

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.feature.automation.components.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationAutomationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AutomationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Automation Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundNew,
                contentColor = PrimaryTeal,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = PrimaryTeal
                    )
                }
            ) {
                listOf("Otomasyonlar", "Geçmiş", "AI Önerileri", "Şablonlar").forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope
                            )
                        }
                    )
                }
            }

            Crossfade(targetState = uiState.selectedTab, label = "tab_fade") { tabIndex ->
                when (tabIndex) {
                    0 -> AutomationsTab(uiState, viewModel)
                    1 -> HistoryTab(uiState)
                    2 -> SuggestionsTab(uiState, viewModel)
                    3 -> TemplatesTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun AutomationsTab(uiState: AutomationUiState, viewModel: AutomationViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.runNow(context) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundNew, contentColor = PrimaryTeal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tüm Otomasyonları Şimdi Çalıştır", fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            }
            if (uiState.rules.isEmpty()) {
                item { EmptyState("Henüz bir otomasyon kuralı tanımlanmadı.") }
            }
            items(uiState.rules) { rule ->
                AutomationRuleCard(
                    rule = rule,
                    onToggle = { viewModel.toggleRule(rule.ruleId, it) },
                    onDelete = { viewModel.deleteRule(rule.ruleId) }
                )
            }
        }
        
        FloatingActionButton(
            onClick = { /* Navigate to Create Custom Automation */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = PrimaryTeal,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, "Yeni Kural")
        }
    }
}

@Composable
fun HistoryTab(uiState: AutomationUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.history.isEmpty()) {
            item { EmptyState("Çalışma geçmişi henüz boş.") }
        }
        items(uiState.history) { entry ->
            AutomationHistoryCard(entry)
        }
    }
}

@Composable
fun SuggestionsTab(uiState: AutomationUiState, viewModel: AutomationViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "AI davranışlarınızı analiz ederek size özel otomasyonlar öneriyor.",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = Manrope,
                        color = InkText
                    )
                }
            }
        }
        if (uiState.suggestions.isEmpty()) {
            item { EmptyState("Şu an için yeni bir öneri yok.") }
        }
        items(uiState.suggestions) { suggestion ->
            AiSuggestionCard(
                suggestion = suggestion,
                onApply = { viewModel.applySuggestion(suggestion.suggestionId) }
            )
        }
    }
}

@Composable
fun TemplatesTab(viewModel: AutomationViewModel) {
    val templates = remember {
        listOf(
            AutomationRuleModel(title = "Her Sabah Portföy Analizi", ifConditionText = "Zaman == 09:30", actionText = "REPORT", category = AlertCategory.PORTFOLIO),
            AutomationRuleModel(title = "Haftalık AI Raporu", ifConditionText = "Zaman == Pazar 20:00", actionText = "PDF", category = AlertCategory.AI_ORAKUL_STUB),
            AutomationRuleModel(title = "Günlük Fırsat Taraması", ifConditionText = "Zaman == 18:30", actionText = "REPORT", category = AlertCategory.PERCENT_CHANGE),
            AutomationRuleModel(title = "Güçlü Teknik Sinyal", ifConditionText = "RSI < 30 AND MACD Cross", actionText = "NOTIFY", category = AlertCategory.PRICE)
        )
    }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(templates) { template ->
            TemplateCard(
                template = template,
                onUse = { viewModel.createRuleFromTemplate(template) }
            )
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Info, null, tint = SubText, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text, textAlign = TextAlign.Center, color = SubText, fontFamily = Manrope)
    }
}
