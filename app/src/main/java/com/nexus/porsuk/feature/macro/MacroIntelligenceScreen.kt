package com.nexus.porsuk.feature.macro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Macro Intelligence Platform — Ana Ekran (MacroIntelligenceScreen)
 */
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
                    Column {
                        Text("Macro Intelligence Platform", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Resesyon Olasılığı: %${uiState.aiOutlook.recessionProbabilityPct} • FRED / TCMB Entegre",
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
                // Makro Sekme Çipleri (MacroDashboardTabs)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MacroDashboardTab.entries) { tab ->
                        FilterChip(
                            selected = uiState.activeTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            label = { Text("${tab.iconEmoji} ${tab.displayName}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. AI Makro Senaryo & Duruş Kartı
                    item {
                        MacroAiOutlookCard(outlook = uiState.aiOutlook)
                    }

                    // 2. Merkez Bankaları Politikaları Kartı (Central Banks)
                    item {
                        Text(
                            text = "Merkez Bankaları Politika Faizleri (Central Banks)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.centralBankPolicies) { bankPolicy ->
                        CentralBankPolicyCard(policy = bankPolicy)
                    }

                    // 3. Makroekonomik Göstergeler (Economic Indicators)
                    item {
                        Text(
                            text = "Önemli Ekonomik Göstergeler (Indicators)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.indicators) { indicator ->
                        EconomicIndicatorCard(indicator = indicator)
                    }

                    // 4. Devlet Tahvilleri & FX Piyasası (Bonds & Commodities)
                    item {
                        Text(
                            text = "Devlet Tahvili Getirileri & Emtia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.bondYields) { bond ->
                        BondYieldCard(bond = bond)
                    }

                    items(uiState.commodities) { comm ->
                        CommodityCard(commodity = comm)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroAiOutlookCard(outlook: MacroAiOutlook) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🤖 AI Makro Görünüm & Resesyon Risk Analizi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Resesyon Olasılığı: %${outlook.recessionProbabilityPct} (Düşük Risk 🟢)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("• ${outlook.inflationCommentary}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text("• ${outlook.interestRateForecastText}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun CentralBankPolicyCard(policy: CentralBankPolicy) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${policy.bankType.iconEmoji} ${policy.bankType.displayName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Faiz: %${policy.policyRatePct}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(policy.statementSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Son Karar: ${policy.lastDecisionDate} • Sonraki Toplantı: ${policy.nextMeetingDate}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EconomicIndicatorCard(indicator: EconomicIndicator) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(indicator.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Açıklanan: ${indicator.currentValue}${indicator.unit}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Önceki: ${indicator.previousValue}${indicator.unit}", style = MaterialTheme.typography.labelSmall)
                Text("Beklenti: ${indicator.forecastValue}${indicator.unit}", style = MaterialTheme.typography.labelSmall)
                Text("Tarih: ${indicator.releaseDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BondYieldCard(bond: BondYieldItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(bond.countryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Vade: ${bond.maturityYears} Yıl", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Getiri: %${bond.yieldPct} (${bond.changePct}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CommodityCard(commodity: CommodityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${commodity.name} (${commodity.commoditySymbol})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Kategori: ${commodity.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("$${commodity.priceUSD} (+%${commodity.changePct})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
