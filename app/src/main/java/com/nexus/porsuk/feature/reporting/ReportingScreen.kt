package com.nexus.porsuk.feature.reporting

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportingCenterScreen(
    onBack: () -> Unit,
    viewModel: ReportingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Özet", "Performans", "AI Analiz", "Risk", "Vergi")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reporting Center",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Kurumsal Yatırım Raporları",
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
                    IconButton(onClick = { /* Open scheduling dialog */ }) {
                        Icon(Icons.Default.CalendarToday, "Zamanla")
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
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundNew,
                contentColor = PrimaryTeal,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
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
                    0 -> PortfolioSummaryTab(uiState, viewModel)
                    1 -> PerformanceReportTab(uiState, viewModel)
                    2 -> AiAnalysisReportTab(uiState, viewModel)
                    3 -> RiskReportTab(uiState, viewModel)
                    4 -> TaxReportTab(uiState, viewModel)
                }
            }
        }
    }
}

@Composable
fun PortfolioSummaryTab(state: ReportingUiState, viewModel: ReportingViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportHeaderCard(
                title = "Portföy Durum Raporu",
                description = "Varlık dağılımı ve kâr/zarar dökümü.",
                onGenerate = { viewModel.generateReport(ReportType.PORTFOLIO, ReportFormat.PDF) }
            )
        }
        
        state.portfolioData?.let { data ->
            item {
                MetricGrid(
                    metrics = listOf(
                        "Toplam Değer" to "₺${data.totalValue}",
                        "Toplam K/Z" to "₺${data.totalProfitLoss}",
                        "Günlük" to "${data.dailyChangePercent}%",
                        "Yıllık" to "${data.yearlyReturnPercent}%"
                    )
                )
            }
        }
    }
}

@Composable
fun AiAnalysisReportTab(state: ReportingUiState, viewModel: ReportingViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportHeaderCard(
                title = "Orakul AI Analiz Raporu",
                description = "Yapay zeka tarafından üretilen stratejik portföy yorumları.",
                onGenerate = { viewModel.generateReport(ReportType.AI_ANALYSIS, ReportFormat.PDF) }
            )
        }
        
        state.aiData?.let { data ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Yönetici Özeti", fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        Spacer(Modifier.height(8.dp))
                        Text(data.generalCommentary, fontSize = 13.sp, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceReportTab(state: ReportingUiState, viewModel: ReportingViewModel) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ReportHeaderCard(
            title = "Performans Benchmark Raporu",
            description = "Portföy vs Endeks (BIST, S&P500) kıyaslaması.",
            onGenerate = { viewModel.generateReport(ReportType.PERFORMANCE, ReportFormat.PDF) }
        )
    }
}

@Composable
fun RiskReportTab(state: ReportingUiState, viewModel: ReportingViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportHeaderCard(
                title = "Gelişmiş Risk Analiz Raporu",
                description = "Volatilite, Beta ve Drawdown metrikleri.",
                onGenerate = { viewModel.generateReport(ReportType.RISK, ReportFormat.PDF) }
            )
        }
        state.riskData?.let { data ->
            item {
                MetricGrid(
                    metrics = listOf(
                        "Sharpe" to "${data.sharpeRatio}",
                        "Beta" to "${data.beta}",
                        "Risk Skoru" to "${data.riskScore}/100",
                        "AI Güven" to "${data.aiConfidenceScore}%"
                    )
                )
            }
        }
    }
}

@Composable
fun TaxReportTab(state: ReportingUiState, viewModel: ReportingViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportHeaderCard(
                title = "Vergi Bilgilendirme Raporu",
                description = "Gerçekleşen işlemler üzerinden tahmini vergi dökümü.",
                onGenerate = { viewModel.generateReport(ReportType.TAX, ReportFormat.EXCEL) }
            )
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Orange.copy(alpha = 0.1f))) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = Orange)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Bu rapor bilgilendirme amaçlıdır ve resmi beyan yerine geçmez.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC2410C)
                    )
                }
            }
        }
    }
}

@Composable
fun ReportHeaderCard(title: String, description: String, onGenerate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, fontSize = 12.sp, color = SubText, modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = onGenerate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Icon(Icons.Default.FileDownload, null)
                Spacer(Modifier.width(8.dp))
                Text("Raporu Oluştur (PDF)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricGrid(metrics: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            metrics.forEach { (label, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label, fontSize = 10.sp, color = SubText)
                    Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = IBMPlexMono)
                }
            }
        }
    }
}
