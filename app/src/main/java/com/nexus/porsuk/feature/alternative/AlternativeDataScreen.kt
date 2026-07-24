package com.nexus.porsuk.feature.alternative

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
 * Porsuk Alternative Data Intelligence Platform — Ana Ekran (AlternativeDataScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativeDataScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AlternativeDataViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Alternative Data Intelligence", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.selectedProvider.displayName} • Eagle Alpha & Quandl",
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
                // Alternatif Veri Sağlayıcı Çipleri (LazyRow)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AlternativeDataProviderType.entries) { provider ->
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
                    // 1. Öncü Alternatif Endeksler (Alternative Indicators)
                    item {
                        Text(
                            text = "Öncü Alternatif Gösterge & Mobilite Endeksleri",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.alternativeIndicators) { indicator ->
                        AlternativeIndicatorCard(indicator = indicator)
                    }

                    // 2. Uydu Görüntüleme & Fabrika Aktivitesi (Satellite Activity)
                    item {
                        Text(
                            text = "Uydu Görüntüleme & Fabrika Doluluk Aktivitesi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.satelliteActivities) { sat ->
                        SatelliteActivityCard(activity = sat)
                    }

                    // 3. Denizcilik & Liman Yoğunluğu (Shipping AIS)
                    item {
                        Text(
                            text = "Denizcilik (AIS) Gemi Trafiği & Liman Yoğunluğu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.vesselShippingData) { ship ->
                        VesselShippingCard(shipping = ship)
                    }

                    // 4. Havacılık Kargo & Özel Jet Trafiği (Aviation Intelligence)
                    item {
                        Text(
                            text = "Havacılık Uçuş & Kargo Kapasite Analitiği",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.aviationTrafficData) { aviation ->
                        AviationTrafficCard(aviation = aviation)
                    }

                    // 5. Endüstriyel Enerji Tüketimi (Energy Intelligence)
                    item {
                        Text(
                            text = "Endüstriyel Elektrik Tüketimi & Petrol Depolama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.energyConsumptionData) { energy ->
                        EnergyConsumptionCard(energy = energy)
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
private fun AlternativeIndicatorCard(indicator: AlternativeIndicatorItem) {
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
                Text(indicator.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Kategori: ${indicator.category} • ${indicator.indexStatus}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${indicator.currentValue}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = if (indicator.changePct >= 0) "+%${indicator.changePct}" else "-%${indicator.changePct}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (indicator.changePct >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SatelliteActivityCard(activity: SatelliteActivityItem) {
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
                Text("🛰️ ${activity.locationName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Fabrika Skoru: ${activity.factoryActivityScore}/100",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Otopark & Depo Doluluk Oranı: %${activity.occupancyRatePct}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(activity.statusText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun VesselShippingCard(shipping: VesselShippingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("🚢 ${shipping.portName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Konteyner Hacmi: ${shipping.containerVolumeTons} Ton", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("Liman Sıkışıklık Seviyesi: ${shipping.portCongestionLevel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text("Aktif Gemi Sayısı: ${shipping.activeVesselsCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AviationTrafficCard(aviation: AviationTrafficItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("✈️ Havalimanı: ${aviation.airportCode}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Ticari Uçuş Sayısı: ${aviation.commercialFlightsCount} / Gün", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("Hava Kargo Kapasitesi: ${aviation.cargoCapacityTons} Ton", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun EnergyConsumptionCard(energy: EnergyConsumptionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("⚡ Bölge: ${energy.regionName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Elektrik Tüketimi: ${energy.electricityConsumptionGWh} GWh", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Petrol Depolama Doluluk: %${energy.oilStorageOccupancyPct}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
