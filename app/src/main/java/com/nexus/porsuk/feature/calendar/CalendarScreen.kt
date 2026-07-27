package com.nexus.porsuk.feature.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.nexus.porsuk.feature.calendar.components.*
import com.nexus.porsuk.ui.calendar.components.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit = {},
    onStockClick: (String, String) -> Unit = { _, _ -> },
    initialTab: Int = 0,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Ekonomik", "Şirket", "Temettü", "Bilanço", "IPO")

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Finans Takvimi & Event Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundNew,
                contentColor = PrimaryTeal,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope
                            )
                        }
                    )
                }
            }

            // Daily AI Summary Banner
            DailyAiSummaryBanner(summary = uiState.dailyAiSummary)

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryTeal)
                } else {
                    Crossfade(targetState = uiState.selectedTab, label = "calendar_tabs") { tabIndex ->
                        when (tabIndex) {
                            0 -> EconomicTab(uiState, viewModel)
                            1 -> CorporateTab(uiState)
                            2 -> DividendTab(uiState, onStockClick)
                            3 -> EarningsTab(uiState, onStockClick)
                            4 -> IpoTab(uiState, onStockClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyAiSummaryBanner(summary: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TealSoft.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = summary ?: "Orakul AI: Bugün piyasaları etkileyebilecek 3 kritik veri akışı var. Bankacılık sektöründe hareketlilik beklenebilir.",
                style = MaterialTheme.typography.bodySmall,
                color = InkText,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun EconomicTab(state: CalendarUiState, viewModel: CalendarViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.filteredEvents) { event ->
            EconomicEventCard(event = event, onAiAnalysisClick = { viewModel.triggerAiAnalysis(event.eventId) })
        }
    }
}

@Composable
fun CorporateTab(state: CalendarUiState) {
    // Placeholder for corporate events (Genel Kurul, Bedelli vb.)
    EmptyTabState("Henüz planlanmış şirket takvimi etkinliği bulunmuyor.")
}

@Composable
fun DividendTab(state: CalendarUiState, onStockClick: (String, String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.dividendEvents) { event ->
            DividendEventCard(event = event, onStockClick = { onStockClick(it, "BIST") })
        }
    }
}

@Composable
fun EarningsTab(state: CalendarUiState, onStockClick: (String, String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.earningsEvents) { event ->
            EarningsEventCard(event = event, onStockClick = { onStockClick(it, "BIST") })
        }
    }
}

@Composable
fun IpoTab(state: CalendarUiState, onStockClick: (String, String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.ipoEvents) { event ->
            IpoEventCard(event = event, onIpoClick = { onStockClick(it, event.market) })
        }
    }
}

@Composable
fun EmptyTabState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = SubText, fontFamily = Manrope, fontSize = 14.sp)
    }
}
