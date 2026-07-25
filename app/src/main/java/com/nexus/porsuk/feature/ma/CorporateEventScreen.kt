package com.nexus.porsuk.feature.ma

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Mergers, Acquisitions & Corporate Events Intelligence Platform — Ana Ekran (CorporateEventScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorporateEventScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CorporateEventViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("M&A & Corporate Events Intelligence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Symbol: ${uiState.selectedSymbol} • PitchBook & Refinitiv Deals",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Event Type Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedEventTypeFilter == null,
                            onClick = { viewModel.filterByEventType(null) },
                            label = { Text("Tüm Olaylar") }
                        )
                    }
                    items(CorporateEventType.entries) { type ->
                        FilterChip(
                            selected = uiState.selectedEventTypeFilter == type,
                            onClick = { viewModel.filterByEventType(type) },
                            label = { Text("${type.iconEmoji} ${type.code}") }
                        )
                    }
                }

                // Platform Tab Row
                ScrollableTabRow(
                    selectedTabIndex = uiState.activeTab.ordinal,
                    edgePadding = 16.dp
                ) {
                    CorporateEventTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = { Text("${tab.iconEmoji} ${tab.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.activeTab) {
                    CorporateEventTab.MA_DEALS -> {
                        DealsTabContent(uiState = uiState)
                    }
                    CorporateEventTab.EVENTS_TIMELINE -> {
                        TimelineTabContent(uiState = uiState)
                    }
                    CorporateEventTab.DEAL_ANALYTICS -> {
                        AnalyticsTabContent(uiState = uiState)
                    }
                    CorporateEventTab.IMPACT_SYNERGIES -> {
                        ImpactTabContent(uiState = uiState)
                    }
                    CorporateEventTab.AI_INTELLIGENCE -> {
                        AiTabContent(uiState = uiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun DealsTabContent(uiState: CorporateEventUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("🤝 Aktif Birleşme & Satın Alım Anlaşmaları (M&A Deals)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.acquisitions + uiState.mergers) { deal ->
            DealCard(deal = deal)
        }
    }
}

@Composable
private fun TimelineTabContent(uiState: CorporateEventUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DealMilestoneTimelineCard(visuals = uiState.visuals)
        }

        item {
            Text("📅 Şirket Olayları Zaman Çizelgesi (Corporate Calendar)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        val filteredEvents = uiState.corporateEvents.filter {
            uiState.selectedEventTypeFilter == null || it.eventType == uiState.selectedEventTypeFilter
        }

        items(filteredEvents) { event ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${event.eventType.iconEmoji} ${event.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = event.eventDate,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Etki Skoru: ${event.impactScore} / 100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsTabContent(uiState: CorporateEventUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IndustryComparisonMultiplesCard(visuals = uiState.visuals)
        }

        uiState.visuals?.dealStatisticsMap?.let { stats ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📈 Anlaşma İstatistikleri & Çarpan Özeti", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        stats.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(key, style = MaterialTheme.typography.bodySmall)
                                Text("$value", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImpactTabContent(uiState: CorporateEventUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DealImpactAndSynergyCard(impact = uiState.impactAnalysis, ai = uiState.aiIntelligence)
        }
    }
}

@Composable
private fun AiTabContent(uiState: CorporateEventUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.aiIntelligence?.let { ai ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🤖 AI M&A Stratejik Analiz Özeti", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ai.dealSummaryText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("🌐 Stratejik Analiz:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(ai.strategicAnalysisText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 Fırsatlar & Sinerjiler:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(ai.opportunitySummaryText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("⚠️ Risk Analizi:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(ai.riskSummaryText, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 Geleceğe Hazır Predictive M&A Suite Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• AI Deal Prediction Engine: ${if (uiState.futureStubs.isAiDealPredictionActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Automatic Synergy Calculation: ${if (uiState.futureStubs.isAutomaticSynergyCalculationActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• M&A Risk Engine: ${if (uiState.futureStubs.isMaRiskEngineActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Corporate Network Graph: ${if (uiState.futureStubs.isCorporateNetworkGraphActive) "Aktif 🟢" else "Pasif (Hazır)"}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
