package com.nexus.porsuk.ui.institutional

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionalAnalyticsScreen(
    onBack: () -> Unit,
    viewModel: InstitutionalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Piyasa", "Sektörler", "Şirket", "Portföy", "AI Analiz")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Institutional Analytics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Bloomberg Terminal & Kurumsal Veri Süiti",
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
                    0 -> MarketOverviewTab(uiState)
                    1 -> SectorAnalyticsTab(uiState)
                    2 -> CompanyAnalyticsTab(uiState)
                    3 -> PortfolioAnalyticsTab(uiState)
                    4 -> InstitutionalAiInsightsTab(uiState)
                }
            }
        }
    }
}

@Composable
fun MarketOverviewTab(state: InstitutionalUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        state.marketOverview?.let { overview ->
            item {
                MarketMetricCard(
                    title = "Piyasa Hacmi & Duyarlılık",
                    metrics = listOf(
                        "Piyasa Değeri" to "₺${overview.totalMarketCap / 1e12}T",
                        "24s Hacim" to "₺${overview.totalVolume24h / 1e9}B",
                        "AI Sentiment" to "${overview.marketSentimentScore}/100",
                        "VIX (Volatilite)" to "${overview.volatilityIndex}"
                    )
                )
            }
            
            item { Text("Top Gainers", fontWeight = FontWeight.Bold, fontFamily = Manrope) }
            items(overview.topGainers) { AssetRow(it) }
            
            item { Text("Top Losers", fontWeight = FontWeight.Bold, fontFamily = Manrope) }
            items(overview.topLosers) { AssetRow(it) }
        }
    }
}

@Composable
fun AssetRow(asset: AssetMetric) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(asset.symbol, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                Text(asset.name, fontSize = 10.sp, color = SubText)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₺${asset.price}", fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                Text(
                    text = "${if(asset.changePct > 0) "+" else ""}${asset.changePct}%",
                    color = if(asset.changePct > 0) EmeraldNew else NegatifRed,
                    fontSize = 11.sp,
                    fontFamily = IBMPlexMono
                )
            }
        }
    }
}

@Composable
fun SectorAnalyticsTab(state: InstitutionalUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.sectorAnalytics) { sector ->
            SectorCard(sector)
        }
    }
}

@Composable
fun SectorCard(sector: SectorAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(sector.sectorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "${if(sector.dailyPerf > 0) "+" else ""}${sector.dailyPerf}%",
                    color = if(sector.dailyPerf > 0) EmeraldNew else NegatifRed,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricSmall("F/K", "${sector.avgPeRatio}")
                MetricSmall("PD/DD", "${sector.avgPbRatio}")
                MetricSmall("AI Güç", "${sector.aiStrengthScore}")
            }
        }
    }
}

@Composable
fun MetricSmall(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = SubText)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = IBMPlexMono)
    }
}

@Composable
fun MarketMetricCard(title: String, metrics: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = PrimaryTeal)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                metrics.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, fontSize = 9.sp, color = SubText)
                        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = IBMPlexMono)
                    }
                }
            }
        }
    }
}

@Composable
fun CompanyAnalyticsTab(state: InstitutionalUiState) {
    // Interactive company selector or search would be here
    state.companyAnalysis?.let { analysis ->
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.05f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${analysis.symbol} Kurumsal Özet", fontWeight = FontWeight.Bold)
                        Text(analysis.financialSummary, fontSize = 12.sp, color = SubText)
                    }
                }
            }
            item {
                MarketMetricCard(
                    "Finansal Karnesi",
                    listOf(
                        "Karlılık" to "${analysis.profitabilityScore}",
                        "Büyüme" to "${analysis.growthRate}%",
                        "Borç/Özkaynak" to "${analysis.debtToEquity}",
                        "AI Skoru" to "${analysis.aiCompanyScore}"
                    )
                )
            }
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Lütfen analiz etmek istediğiniz şirketi seçin.")
    }
}

@Composable
fun PortfolioAnalyticsTab(state: InstitutionalUiState) {
    state.portfolioAnalytics?.let { perf ->
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                MarketMetricCard(
                    "Portföy Risk & Performans",
                    listOf(
                        "Sharpe" to "${perf.sharpeRatio}",
                        "Beta" to "${perf.beta}",
                        "Alpha" to "${perf.alpha}",
                        "Volatility" to "${perf.annualVolatility}%"
                    )
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Çeşitlendirme Skoru", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = perf.diversificationScore / 100f,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            color = PrimaryTeal
                        )
                        Text("${perf.diversificationScore}/100", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                    }
                }
            }
        }
    }
}

@Composable
fun InstitutionalAiInsightsTab(state: InstitutionalUiState) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(state.aiInsights) { insight ->
            Card(
                border = androidx.compose.foundation.BorderStroke(1.dp, Violet.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(containerColor = CardNew)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Violet)
                        Spacer(Modifier.width(8.dp))
                        Text(insight.title, fontWeight = FontWeight.Bold, color = Violet)
                    }
                    Text(insight.description, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text("Öneri: ${insight.actionSuggestion}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryTeal)
                }
            }
        }
    }
}
