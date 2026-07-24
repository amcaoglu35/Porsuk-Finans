package com.nexus.porsuk.feature.risk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.risk.components.*

/**
 * Porsuk Risk Engine — Risk Analiz Ekranı (RiskScreen)
 *
 * 6 Risk kategorisinde (Market, Liquidity, Financial, Business, Price, Portfolio) analiz raporunu ve hedging önerilerini sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: RiskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Risk Intelligence (${uiState.symbol})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Genel Risk Seviyesi Rozet Kartı
                OverallRiskBadgeCard(report = uiState.report)

                // 2. Geleceğe Hazır Orakul AI Crash & Volatility Forecast Stub Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🤖 Orakul AI Crash Prediction & Volatility Forecast",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Orakul Risk AI: Önümüzdeki 30 gün içinde çöküş olasılığı %2.1 (Çok Düşük). Bilanço Altman Z-Score 3.85 ile güvenli bölgededir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // 3. 6 Risk Metrikleri Izgarası
                RiskCategoryGridCard(report = uiState.report)

                // 4. Koruma & Hedging Önerileri Kartı
                ProtectionRecommendationsCard(recommendations = uiState.report?.protectionRecommendations ?: emptyList())

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
