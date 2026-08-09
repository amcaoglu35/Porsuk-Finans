package com.nexus.porsuk.ui.orakul.agents

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
import com.nexus.porsuk.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiAgentAnalysisScreen(
    symbol: String,
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: MultiAgentAnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(symbol) {
        viewModel.runAnalysis(symbol)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$symbol - AI Konsensüs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "7 Farklı AI Ajanı Analiz Ediyor",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.consensus?.let { result ->
                    // Consensus Summary Card
                    item {
                        ConsensusSummaryCard(result)
                    }

                    // Conflict Notes
                    result.conflictNotes?.let {
                        item {
                            ConflictCard(it)
                        }
                    }

                    // Debate Button
                    item {
                        Button(
                            onClick = {
                                val apiKey = viewModel.getGeminiApiKey()
                                if (apiKey.isNullOrBlank()) {
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "AI Tartışması için Gemini API anahtarı gereklidir.",
                                            actionLabel = "Ayarlar",
                                            duration = SnackbarDuration.Long
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            onNavigateToSettings()
                                        }
                                    }
                                } else {
                                    viewModel.startDebate(apiKey)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Violet),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.isDebating
                        ) {
                            if (uiState.isDebating) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Forum, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Ajanlar Tartışsın (AI Debate)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Debate Summary
                    result.debateSummary?.let {
                        item {
                            DebateResultCard(it)
                        }
                    }

                    // Agent Individual Cards
                    item {
                        Text("Ajan Detayları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }

                    items(result.agentAnalyses) { analysis ->
                        AgentAnalysisCard(analysis)
                    }
                }
            }
        }
    }
}

@Composable
fun ConsensusSummaryCard(result: ConsensusResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(result.finalDecision.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${result.aggregateScore}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = result.finalDecision.color,
                    fontFamily = JetBrainsMono
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = result.finalDecision.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = result.finalDecision.color,
                fontFamily = Manrope
            )
            
            Text(
                text = "Genel AI Konsensüs Skoru",
                style = MaterialTheme.typography.labelMedium,
                color = SubText,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun ConflictCard(notes: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Orange.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Orange.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = Orange)
            Spacer(modifier = Modifier.width(12.dp))
            Text(notes, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope)
        }
    }
}

@Composable
fun DebateResultCard(summary: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Violet.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Violet.coerceAlpha(0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = Violet)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tartışma Sonucu", fontWeight = FontWeight.Bold, color = Violet, fontFamily = Manrope)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(summary, style = MaterialTheme.typography.bodySmall, color = InkText, fontFamily = Manrope)
        }
    }
}

@Composable
fun AgentAnalysisCard(analysis: AgentAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(analysis.agentType.iconEmoji, fontSize = 20.sp)
                    Text(analysis.agentType.title, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(analysis.decision.color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(analysis.decision.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = analysis.decision.color)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(analysis.commentary, style = MaterialTheme.typography.bodySmall, color = SubText, fontFamily = Manrope)
            
            if (analysis.strengths.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                analysis.strengths.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, tint = PrimaryTeal, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(it, fontSize = 10.sp, color = PrimaryTeal)
                    }
                }
            }
        }
    }
}

private fun Color.coerceAlpha(alpha: Float): Color = this.copy(alpha = alpha)
