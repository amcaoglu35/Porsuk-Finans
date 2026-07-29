package com.nexus.porsuk.feature.macro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroIntelligenceScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MacroIntelligenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Makro Ekonomi Lab", fontWeight = FontWeight.Bold, fontFamily = Manrope)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshMacro() }) {
                        Icon(Icons.Default.Refresh, "Yenile")
                    }
                }
            )
        },
        containerColor = BackgroundNew
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Tab Switcher
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MacroDashboardTab.entries) { tab ->
                    FilterChip(
                        selected = uiState.activeTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        label = { Text(tab.displayName) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        MacroAiOutlookCard(outlook = uiState.aiOutlook)
                    }

                    items(uiState.indicators) { indicator ->
                        IndicatorChartCard(
                            indicator = indicator,
                            history = viewModel.getIndicatorData(indicator.indicatorId).collectAsState(initial = emptyList()).value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroAiOutlookCard(outlook: MacroAiOutlook, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F1C))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Orakul Makro Analizi", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Resesyon Olasılığı: %${outlook.recessionProbabilityPct}",
                color = PrimaryTeal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(outlook.inflationCommentary, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
fun IndicatorChartCard(
    indicator: EconomicIndicator,
    history: List<Double>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(indicator.name, fontWeight = FontWeight.Bold, color = InkText)
                    Text(indicator.indicatorId, fontSize = 10.sp, color = SubText)
                }
                Text(
                    "${indicator.currentValue}${indicator.unit}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = PrimaryTeal,
                    fontFamily = IBMPlexMono
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mini Chart Placeholder
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(BackgroundNew.copy(alpha = 0.5f))) {
                if (history.isNotEmpty()) {
                    com.nexus.porsuk.ui.common.PremiumLiveCanvasChart(
                        prices = history,
                        color = PrimaryTeal
                    )
                } else {
                    Text("Veri yok", modifier = Modifier.align(Alignment.Center), color = SubText, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Önceki: ${indicator.previousValue}${indicator.unit}", fontSize = 11.sp, color = SubText)
                val diff = if (indicator.previousValue != 0.0) ((indicator.currentValue - indicator.previousValue) / indicator.previousValue) * 100 else 0.0
                Text("Değişim: %${String.format(Locale.US, "%.2f", diff)}", 
                    fontSize = 11.sp, 
                    color = if (indicator.currentValue >= indicator.previousValue) EmeraldNew else NegatifRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
