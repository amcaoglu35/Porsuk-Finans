package com.nexus.porsuk.ui.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.chart.components.AdvancedChart
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedChartStudioScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: ChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadData(symbol, ChartTimeFrame.DAILY)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$symbol - Advanced Chart Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Real-time AI Technical Analysis",
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
                actions = {
                    IconButton(onClick = { viewModel.toggleAiAnalysis() }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Analiz",
                            tint = if (uiState.isAiLoading) PrimaryTeal else InkText
                        )
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
            val aiAnalysis = uiState.aiAnalysis
            // Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryTeal)
                } else {
                    AdvancedChart(
                        candles = uiState.candles,
                        compareCandles = uiState.compareCandles,
                        indicators = uiState.indicators
                    )
                }
                
                // AI Analysis Overlay
                if (aiAnalysis != null) {
                    AiAnalysisOverlay(analysis = aiAnalysis)
                }
            }
            
            // Bottom Controls (Placeholder)
            ChartBottomToolbar(
                selectedType = uiState.settings.chartType,
                onTypeChange = { viewModel.changeChartType(it) }
            )
        }
    }
}

@Composable
fun AiAnalysisOverlay(analysis: AiChartAnalysis) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(260.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("AI TEKNİK RAPOR", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text("Trend: ${analysis.trend}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            analysis.pattern?.let {
                Text("Formasyon: $it", fontSize = 11.sp, color = Violet)
            }
            Spacer(Modifier.height(4.dp))
            Text(analysis.scenario, fontSize = 10.sp, color = SubText, lineHeight = 14.sp)
        }
    }
}

@Composable
fun ChartBottomToolbar(
    selectedType: ChartType,
    onTypeChange: (ChartType) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardNew,
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartType.entries.forEach { type ->
                IconButton(onClick = { onTypeChange(type) }) {
                    Text(type.icon, fontSize = 18.sp, color = if (selectedType == type) PrimaryTeal else SubText)
                }
            }
            Divider(modifier = Modifier.height(24.dp).width(1.dp))
            // Indicators, Drawings, Save buttons...
        }
    }
}
