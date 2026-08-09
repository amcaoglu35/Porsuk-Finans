package com.nexus.porsuk.feature.fundintelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.theme.*
import com.nexus.porsuk.ui.common.*
import com.nexus.porsuk.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundIntelligenceScreen(
    fundCode: String,
    onBack: () -> Unit,
    viewModel: FundIntelligenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fundCode) {
        viewModel.loadFundDetails(fundCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fon Zekası Analizi", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
        } else if (uiState.data != null) {
            val data = uiState.data!!
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FundHeaderSection(fundCode, data.info?.name ?: "Yatırım Fonu")
                }
                
                item {
                    FundPerformanceSection(data.performance)
                }
                
                item {
                    FundHoldingsSection(data.allocation?.topHoldings ?: emptyList())
                }
                
                item {
                    FundRiskSection(data.riskMetrics)
                }
                
                item {
                    FundAiIntelligenceSection(data.info?.description)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Fon verileri yüklenemedi.", color = SubText)
            }
        }
    }
}

@Composable
fun FundHeaderSection(code: String, name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = code, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal, fontFamily = JetBrainsMono)
            Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = InkText)
            Text(text = "Professional Analytics Dashboard", style = MaterialTheme.typography.labelSmall, color = SubText)
        }
    }
}

@Composable
fun FundPerformanceSection(performance: FundPerformance?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Performans Zekası", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricBox(value = "+%${performance?.daily ?: "0.0"}", label = "Günlük", modifier = Modifier.weight(1f))
                MetricBox(value = "+%${performance?.ytd ?: "0.0"}", label = "Yılbaşından Beri", modifier = Modifier.weight(1f))
                MetricBox(value = "+%${performance?.yearly1 ?: "0.0"}", label = "1 Yıl", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FundHoldingsSection(holdings: List<FundHolding>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("En Büyük Pozisyonlar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            
            if (holdings.isEmpty()) {
                Text("Varlık dağılım verisi mevcut değil.", style = MaterialTheme.typography.bodySmall, color = SubText)
            } else {
                holdings.forEach { holding ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(holding.symbol, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Text("%${String.format("%.1f", holding.weight)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    }
                }
            }
        }
    }
}

@Composable
fun FundRiskSection(risk: FundRiskMetrics?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Risk Analitiği", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox(value = "${risk?.sharpeRatio ?: "0.0"}", label = "Sharpe", modifier = Modifier.weight(1f))
                MetricBox(value = "${risk?.beta ?: "0.0"}", label = "Beta", modifier = Modifier.weight(1f))
                MetricBox(value = "%${risk?.maxDrawdown ?: "0.0"}", label = "Drawdown", accentColor = NegatifRed, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FundAiIntelligenceSection(description: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧠", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Insight Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description ?: "Bu fon için yapay zeka analizi hazırlanıyor...",
                style = MaterialTheme.typography.bodySmall,
                color = InkText,
                lineHeight = 18.sp
            )
        }
    }
}
