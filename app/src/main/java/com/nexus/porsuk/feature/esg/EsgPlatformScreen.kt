package com.nexus.porsuk.feature.esg

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
 * Porsuk ESG & Sustainability Intelligence Platform — Ana Ekran (EsgPlatformScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsgPlatformScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: EsgPlatformViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ESG & Sustainability Intelligence", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.esgScore.companyName} (${uiState.esgScore.companySymbol}) • ${uiState.selectedProvider.displayName}",
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
                // ESG Sağlayıcı Seçim Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(EsgProviderType.entries) { provider ->
                        FilterChip(
                            selected = uiState.selectedProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text("${provider.iconEmoji} ${provider.displayName}") }
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
                    // 1. Genel ESG Skor Kartı (Overall ESG Score)
                    item {
                        EsgOverallScoreCard(scoreData = uiState.esgScore)
                    }

                    // 2. Çevresel Sütun Kartı (Environmental Pillar)
                    item {
                        EnvironmentalPillarCard(pillar = uiState.environmentalPillar)
                    }

                    // 3. Sosyal Sütun Kartı (Social Pillar)
                    item {
                        SocialPillarCard(pillar = uiState.socialPillar)
                    }

                    // 4. Kurumsal Yönetişim Kartı (Governance Pillar)
                    item {
                        GovernancePillarCard(pillar = uiState.governancePillar)
                    }

                    // 5. ESG Tartışma & Uyarısı (Controversy Alerts)
                    item {
                        Text(
                            text = "ESG Sürdürülebilirlik Raporu & Olaylar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.controversyAlerts) { alert ->
                        EsgControversyCard(alert = alert)
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
private fun EsgOverallScoreCard(scoreData: EsgScoreData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Genel ESG Skoru: ${scoreData.overallScore}/100", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Derece: ${scoreData.ratingGrade}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = scoreData.industryRankText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PillarStat("🌿 Çevre (E)", "${scoreData.environmentalScore}")
                PillarStat("👥 Sosyal (S)", "${scoreData.socialScore}")
                PillarStat("🏛️ Yönetişim (G)", "${scoreData.governanceScore}")
            }
        }
    }
}

@Composable
private fun PillarStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EnvironmentalPillarCard(pillar: EnvironmentalPillar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("🌍 Çevresel (Environmental) Göstergeler", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Yenilenebilir Enerji Kullanımı: %${pillar.renewableEnergyUsagePct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Geri Dönüşüm Oranı: %${pillar.wasteRecyclingRatePct}", style = MaterialTheme.typography.bodySmall)
            Text("Fiziksel İklim Riski: ${pillar.climateRiskLevel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SocialPillarCard(pillar: SocialPillar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("👥 Sosyal (Social) Göstergeler", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Cinsiyet Çeşitliliği Oranı: %${pillar.genderDiversityPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("Çalışan Devir Oranı (Turnover): %${pillar.employeeTurnoverPct}", style = MaterialTheme.typography.bodySmall)
            Text("Müşteri Memnuniyet Skoru: ${pillar.customerSatisfactionScore}/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun GovernancePillarCard(pillar: GovernancePillar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("🏛️ Kurumsal Yönetişim (Governance) Göstergeler", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Bağımsız Yönetim Kurulu Oranı: %${pillar.independentDirectorsPct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("YKY Cinsiyet Çeşitliliği: %${pillar.boardGenderDiversityPct}", style = MaterialTheme.typography.bodySmall)
            Text("Denetim Kalite Skoru: ${pillar.auditQualityScore}/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun EsgControversyCard(alert: EsgControversyAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(alert.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${alert.category} • ${alert.publishedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
