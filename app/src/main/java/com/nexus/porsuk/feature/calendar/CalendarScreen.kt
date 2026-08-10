package com.nexus.porsuk.feature.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.feature.calendar.components.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateBack: () -> Unit,
    onStockClick: (String, String) -> Unit,
    initialTab: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    val dividends by viewModel.dividends.collectAsState()
    val ipos by viewModel.ipos.collectAsState()
    val economicEvents by viewModel.economicEvents.collectAsState()

    var showAiInsight by remember { mutableStateOf(false) }

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Takvim & Analiz",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontSize = 18.sp
                        )
                        Text(
                            when (uiState.selectedTab) {
                                0 -> "Ekonomik veriler ve AI yorumları"
                                1 -> "Temettü ödemeleri ve takvimi"
                                2 -> "Halka arz takip sistemi"
                                else -> "Finansal etkinlikler hub'ı"
                            },
                            fontFamily = Manrope,
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshEvents() }) {
                        Icon(Icons.Default.Refresh, "Yenile", tint = PrimaryTeal)
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
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LineBorder)
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "📅 Ekonomik",
                    "💰 Temettü",
                    "🚀 Halka Arz"
                ).forEachIndexed { index, tabTitle ->
                    val isSelected = uiState.selectedTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryTeal else Color.Transparent)
                            .clickable {
                                viewModel.selectTab(index)
                                showAiInsight = false
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tabTitle,
                            color = if (isSelected) Color.White else SubText,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Crossfade(targetState = uiState.selectedTab, label = "calendar_tabs") { tabIndex ->
                when (tabIndex) {
                    0 -> EconomicCalendarTabContent(economicEvents)
                    1 -> DividendTabContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        dividends = dividends,
                        onStockClick = onStockClick,
                        showAiInsight = showAiInsight,
                        onToggleAiInsight = {
                            showAiInsight = !showAiInsight
                            if (showAiInsight) viewModel.generateOrakulInsight("DIVIDEND")
                        }
                    )
                    2 -> IpoTabContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        ipos = ipos,
                        showAiInsight = showAiInsight,
                        onToggleAiInsight = {
                            showAiInsight = !showAiInsight
                            if (showAiInsight) viewModel.generateOrakulInsight("IPO")
                        }
                    )
                }
            }
        }
    }
}
