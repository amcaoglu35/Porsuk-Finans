package com.nexus.porsuk.feature.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.CalendarEventCategory
import com.nexus.porsuk.domain.model.CalendarImpactLevel
import com.nexus.porsuk.feature.calendar.components.*

/**
 * Porsuk Economic Calendar Engine — Takvim Ekranı (CalendarScreen)
 *
 * Günlük/Haftalık/Aylık/Liste modlarını, etki seviyesi filtrelerini ve Orakul AI etkinlik analiz stubs alanlarını sunan ana ekran.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ekonomik Takvim",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Görünüm Modu Değiştirici (Günlük, Haftalık, Aylık, Liste)
            CalendarViewToggleBar(
                selectedMode = uiState.viewMode,
                onModeSelected = { viewModel.selectViewMode(it) }
            )

            // 2. Kategori Filtre Çubuğu
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedCategory.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {},
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                CalendarEventCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = cat == uiState.selectedCategory,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat.displayName) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // 3. Geleceğe Hazır Orakul AI Event Impact Score Banner (Stub)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 Orakul AI Takvim Analizi: Bu hafta FED ve TCMB faiz kararları nedeniyle yüksek volatilite bekleniyor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 4. Etkinlik Akış Listesi
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.filteredEvents.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Aradığınız kriterlere uygun takvim etkinliği bulunamadı.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredEvents,
                            key = { it.eventId }
                        ) { event ->
                            EventItemCard(event = event)
                        }
                    }
                }
            }
        }
    }
}
