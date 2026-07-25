package com.nexus.porsuk.feature.institutional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Institutional Holdings & Insider Intelligence Platform — Ana Ekran (InstitutionalScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: InstitutionalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Institutional & Insider Intelligence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Symbol: ${uiState.selectedSymbol} • ${uiState.selectedProvider.displayName}",
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
                // Provider Selector Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(InstitutionalProviderType.entries) { provider ->
                        FilterChip(
                            selected = uiState.selectedProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text("${provider.iconEmoji} ${provider.code}") }
                        )
                    }
                }

                // Scrollable Tab Row
                ScrollableTabRow(
                    selectedTabIndex = uiState.activeTab.ordinal,
                    edgePadding = 16.dp
                ) {
                    InstitutionalTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = { Text("${tab.iconEmoji} ${tab.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.activeTab) {
                    InstitutionalTab.INSTITUTIONAL_HOLDINGS -> {
                        HoldingsTabContent(uiState = uiState)
                    }
                    InstitutionalTab.INSIDER_TRADING -> {
                        InsiderTabContent(uiState = uiState, viewModel = viewModel)
                    }
                    InstitutionalTab.OWNERSHIP_STRUCTURE -> {
                        OwnershipTabContent(uiState = uiState)
                    }
                    InstitutionalTab.WHALE_TRACKER -> {
                        WhaleTrackerTabContent(uiState = uiState)
                    }
                    InstitutionalTab.AI_INTELLIGENCE -> {
                        AiIntelligenceTabContent(uiState = uiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun HoldingsTabContent(uiState: InstitutionalUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("🏛️ En Büyük Kurumsal Yatırımcılar (Top Institutional Investors)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.topInvestors) { inv ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(inv.investorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("$${inv.totalAumUsd / 1_000_000_000.0}B AUM", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Fon Yöneticisi: ${inv.managerName} • Dönem: ${inv.lastFilingDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Devir Hızı (Turnover): %${inv.turnoverPct} • En Büyük Pozisyon: ${inv.topHoldingSymbol}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            Text("📈 Hisse Bazlı Fon Pozisyonları (${uiState.selectedSymbol})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.fundHoldings) { holding ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(holding.investorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${holding.changeType.iconEmoji} ${holding.changeType.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Lot: ${holding.sharesHeld / 1_000.0}K • Ağırlık: %${holding.portfolioWeightPct}", style = MaterialTheme.typography.labelSmall)
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (holding.sharesChange >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "${if (holding.sharesChange >= 0) "+" else ""}${holding.sharesChangePct}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsiderTabContent(
    uiState: InstitutionalUiState,
    viewModel: InstitutionalViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NetInsiderActivityCard(netActivity = uiState.netInsiderActivity)
        }

        item {
            Text("Yönetici Görevi Filtrele:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedRoleFilter == null,
                        onClick = { viewModel.filterByInsiderRole(null) },
                        label = { Text("Tüm Yöneticiler") }
                    )
                }
                items(InsiderRoleType.entries) { role ->
                    FilterChip(
                        selected = uiState.selectedRoleFilter == role,
                        onClick = { viewModel.filterByInsiderRole(role) },
                        label = { Text(role.name) }
                    )
                }
            }
        }

        item {
            Text("🕵️ Son Yönetici (Insider) İşlemleri", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        val filteredTrades = uiState.insiderTrades.filter {
            uiState.selectedRoleFilter == null || it.role == uiState.selectedRoleFilter
        }

        items(filteredTrades) { trade ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(trade.insiderName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(trade.role.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (trade.transactionType.isBuy) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = trade.transactionType.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tutar: ₺${trade.totalValue / 1_000_000.0}M • Fiyat: ₺${trade.sharePrice} • Miktar: ${trade.shareAmount} Lot", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text("İşlem Tarihi: ${trade.transactionDate} (Bildirim: ${trade.filingDate})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun OwnershipTabContent(uiState: InstitutionalUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OwnershipBreakdownCard(breakdown = uiState.ownershipBreakdown)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📈 Çeyreklik Sahiplik Tarihçesi (Ownership History)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.ownershipHistory.forEach { pt ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(pt.periodLabel, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("Kurumsal: %${pt.institutionalPct} | Insider: %${pt.insiderPct} | Retail: %${pt.retailPct}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhaleTrackerTabContent(uiState: InstitutionalUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SmartMoneyFlowCard(flow = uiState.smartMoneyFlow)
        }

        item {
            Text("🐋 Balina İşlem Uyarıları & Sinyaller (Whale Alerts)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.whaleAlerts) { alert ->
            WhaleAlertCard(alert = alert)
        }
    }
}

@Composable
private fun AiIntelligenceTabContent(uiState: InstitutionalUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.aiCommentary?.let { ai ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🤖 AI Akıllı Para Özeti & Yorumu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ai.smartMoneySummaryText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("🏛️ Kurumsal Fon Değerlendirmesi:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(ai.institutionalCommentaryText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🕵️ Yönetici (Insider) Alım/Satım Yorumu:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(ai.insiderCommentaryText, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 Tespit Edilen Fırsatlar (Opportunity Detections):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        ai.opportunityDetections.forEach { opp ->
                            Text("• $opp", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 Geleceğe Hazır Predictive Suite Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• AI Insider Prediction Engine: ${if (uiState.futureStubs.isAiInsiderPredictionReady) "Aktif 🟢" else "Pasif (Hazır Stub)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Whale Prediction Engine: ${if (uiState.futureStubs.isWhalePredictionReady) "Aktif 🟢" else "Pasif (Hazır Stub)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Fund Ranking Engine: ${if (uiState.futureStubs.isFundRankingEngineReady) "Aktif 🟢" else "Pasif (Hazır Stub)"}", style = MaterialTheme.typography.bodySmall)
                    Text("• Institution Score Engine: ${if (uiState.futureStubs.isInstitutionScoreEngineReady) "Aktif (Hazır Engine) 🟢" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
