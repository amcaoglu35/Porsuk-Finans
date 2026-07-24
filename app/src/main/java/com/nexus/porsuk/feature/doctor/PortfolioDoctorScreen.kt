package com.nexus.porsuk.feature.doctor

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
import com.nexus.porsuk.feature.doctor.components.*

/**
 * Porsuk Portfolio Doctor Engine — Portföy Teşhis Ekranı (PortfolioDoctorScreen)
 *
 * 0-100 Portföy Sağlık Skorunu, çeşitlendirme dağılımını ve yeniden dengeleme (rebalancing) önerilerini sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioDoctorScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: PortfolioDoctorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Portfolio Doctor",
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

                // 1. Dairesel 0-100 Portföy Sağlık Skoru Kartı
                HealthScoreGaugeCard(report = uiState.report)

                // 2. Doktor Özet Teşhis Notu Kartı
                DoctorSummaryReportCard(report = uiState.report)

                // 3. Geleceğe Hazır Orakul AI Retirement Analysis & Tax Optimization Stub Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🤖 Orakul AI Emeklilik & Vergi Senaryo Analizi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Orakul AI: Mevcut $1,450 yıllık temettü geliri ile 15 yıllık emeklilik projeksiyonunuz %88 başarı oranındadır. Stopaj vergi optimizasyonu stubs aktif.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // 4. Çeşitlendirme Dağılım Kartı
                DiversificationBreakdownCard(diversification = uiState.report?.diversification)

                // 5. Yeniden Dengeleme (Rebalancing) Öneri Kartı
                RebalancingAdviceCard(items = uiState.report?.rebalancingItems ?: emptyList())

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
