package com.nexus.porsuk.feature.derivatives

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
 * Porsuk Options, Futures & Derivatives Platform — Ana Ekran (DerivativesPlatformScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DerivativesPlatformScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DerivativesPlatformViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Options, Futures & Derivatives", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.selectedUnderlyingSymbol} • ${uiState.activeProvider.displayName}",
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
                // Veri Sağlayıcı Seçimi (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DerivativesProviderType.entries) { provider ->
                        FilterChip(
                            selected = uiState.activeProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text("${provider.iconEmoji} ${provider.displayName}") }
                        )
                    }
                }

                // Call / Put Filtresi (LazyRow)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedOptionTypeFilter == null,
                        onClick = { viewModel.selectOptionTypeFilter(null) },
                        label = { Text("Tüm Opsiyonlar") }
                    )
                    FilterChip(
                        selected = uiState.selectedOptionTypeFilter == OptionType.CALL,
                        onClick = { viewModel.selectOptionTypeFilter(OptionType.CALL) },
                        label = { Text("Call (Alım)") }
                    )
                    FilterChip(
                        selected = uiState.selectedOptionTypeFilter == OptionType.PUT,
                        onClick = { viewModel.selectOptionTypeFilter(OptionType.PUT) },
                        label = { Text("Put (Satım)") }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Black-Scholes Opsiyon Yunanları Kartı
                    item {
                        GreeksSummaryCard(greeks = uiState.selectedContractGreeks)
                    }

                    // 2. Opsiyon Strateji Oluşturucu (Strategy Risk)
                    item {
                        StrategyBuilderCard(
                            selectedStrategy = uiState.selectedStrategy,
                            risk = uiState.strategyRisk,
                            onStrategySelect = { viewModel.selectStrategy(it) }
                        )
                    }

                    // 3. Opsiyon Zinciri (Option Chain List)
                    item {
                        Text(
                            text = "Opsiyon Zinciri (Option Chain - VİOP / CBOE)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val filteredChain = uiState.optionChain.filter {
                        uiState.selectedOptionTypeFilter == null || it.type == uiState.selectedOptionTypeFilter
                    }

                    items(filteredChain) { contract ->
                        OptionContractCard(
                            contract = contract,
                            onInspectGreeks = { viewModel.inspectContractGreeks(contract) }
                        )
                    }

                    // 4. Vadeli İşlemler (Futures Contracts)
                    item {
                        Text(
                            text = "VİOP Vadeli İşlem Sözleşmeleri (Futures)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.futuresContracts) { futures ->
                        FuturesContractCard(futures = futures)
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
private fun GreeksSummaryCard(greeks: OptionGreeks) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🧮 Black-Scholes Opsiyon Yunanları (Greeks Engine)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Δ Delta: ${greeks.delta}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Γ Gamma: ${greeks.gamma}", style = MaterialTheme.typography.bodySmall)
                Text("Θ Theta: ${greeks.theta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ν Vega: ${greeks.vega}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("ρ Rho: ${greeks.rho}", style = MaterialTheme.typography.bodySmall)
                Text("Vanna: ${greeks.vanna}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StrategyBuilderCard(
    selectedStrategy: OptionStrategyType,
    risk: OptionStrategyRisk,
    onStrategySelect: (OptionStrategyType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Opsiyon Strateji Risk & Payoff Analizi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(OptionStrategyType.entries) { strat ->
                    FilterChip(
                        selected = selectedStrategy == strat,
                        onClick = { onStrategySelect(strat) },
                        label = { Text("${strat.iconEmoji} ${strat.displayName}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Maksimum Kâr: ${risk.maxProfitText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Maksimum Zarar: ${risk.maxLossText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            Text("Kâra Geçiş (Break-Even): ${risk.breakEvenPoints.joinToString(" TL / ")} TL", style = MaterialTheme.typography.bodySmall)
            Text("Kazanma Olasılığı (POP): %${risk.probabilityOfProfitPct}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OptionContractCard(
    contract: OptionContract,
    onInspectGreeks: () -> Unit
) {
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
                    text = "${contract.type.code} • Strike: ${contract.strikePrice} TL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "IV: %${(contract.impliedVolatility * 100).toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Alış (Bid): ${contract.bid} TL", style = MaterialTheme.typography.bodySmall)
                Text("Satış (Ask): ${contract.ask} TL", style = MaterialTheme.typography.bodySmall)
                Text("Son: ${contract.lastPrice} TL", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Text("Hacim: ${contract.volume} • Açık Pozisyon (OI): ${contract.openInterest}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onInspectGreeks,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Yunanları Hesapla & İncele", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun FuturesContractCard(futures: FuturesContract) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "${futures.contractSymbol} - ${futures.underlyingName}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Son Fiyat: ${futures.lastPrice} TL • Kademe (Tick): ${futures.tickSize}", style = MaterialTheme.typography.bodySmall)
            Text("Başlangıç Teminatı (Margin): ${futures.initialMarginTL} TL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
