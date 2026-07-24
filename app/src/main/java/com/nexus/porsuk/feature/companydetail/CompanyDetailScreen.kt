package com.nexus.porsuk.feature.companydetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.companydetail.components.*

/**
 * Porsuk Company Detail Module — Şirket Detay Ekranı (CompanyDetailScreen)
 *
 * Şirketin künyesini, canlı fiyatını, finansallarını, temettü geçmişini, bilançolarını,
 * haberlerini, istatistiklerini ve geleceğe hazır Orakul AI alanlarını premium tasarımla sunar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CompanyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.symbol,
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
            CompanyDetailShimmer(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Şirket Üst Künye Kartı
                CompanyHeaderCard(
                    symbol = uiState.symbol,
                    company = uiState.company,
                    isFavorite = uiState.isFavorite,
                    onFavoriteToggle = { viewModel.toggleFavorite() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // 2. Canlı / Son Fiyat Kartı
                CompanyPriceCard(
                    quote = uiState.quote,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // 3. 7 Sekmeli Material 3 Tab Bar
                CompanyTabRow(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 4. Sekme İçeriği (7 Detay Görünümü)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        label = "TabContentAnimation"
                    ) { tab ->
                        when (tab) {
                            CompanyDetailTab.OVERVIEW -> TabOverviewContent(company = uiState.company)
                            CompanyDetailTab.FINANCIALS -> TabFinancialsContent()
                            CompanyDetailTab.DIVIDENDS -> TabDividendsContent(dividends = uiState.dividends)
                            CompanyDetailTab.EARNINGS -> TabEarningsContent(earnings = uiState.earnings)
                            CompanyDetailTab.NEWS -> TabNewsContent(newsList = uiState.news)
                            CompanyDetailTab.STATS -> TabStatsContent()
                            CompanyDetailTab.AI_ORAKUL -> TabAiOrakulContent()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
