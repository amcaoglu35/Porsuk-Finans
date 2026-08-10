package com.nexus.porsuk.feature.companydetail

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.feature.companydetail.components.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompanyDetailScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToLedger: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
    viewModel: CompanyDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val historicalPrices by viewModel.historicalPrices.collectAsState()

    val onShareClick = {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${uiState.company?.companyName ?: uiState.symbol} (${uiState.symbol}) detaylarını Porsuk Finans uygulamasında inceleyin!")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    val onAlarmClick = {
        onNavigateToAlerts()
    }

    val onTradeClick = {
        onNavigateToLedger()
    }

    Scaffold(
        bottomBar = {
            CompanyBottomActionBar(
                onWatchlistClick = { viewModel.toggleFavorite() },
                onAlarmClick = onAlarmClick,
                onTradeClick = onTradeClick
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        if (uiState.isLoading) {
            CompanyDetailShimmer(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. Header Area
                item {
                    Column {
                        if (uiState.isOffline) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFBEB))
                                    .padding(vertical = 6.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📴 Çevrimdışı Mod • Son Güncelleme: ${uiState.lastUpdatedFormatted ?: "Bilinmiyor"}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        CompanyDetailHeader(
                            company = uiState.company,
                            isFavorite = uiState.isFavorite,
                            onBack = onNavigateBack,
                            onFavoriteToggle = { viewModel.toggleFavorite() },
                            onAlarmClick = onAlarmClick,
                            onShareClick = onShareClick
                        )
                    }
                }

                // 2. Price and AI Score
                item {
                    CompanyPriceAndAiScore(
                        quote = uiState.quote,
                        aiScore = uiState.aiHistory?.masterScore ?: 85.0,
                        aiRecommendation = uiState.aiHistory?.recommendation ?: "BUY"
                    )
                }

                // 3. Main Interactive Chart
                item {
                    CompanyMainChart(
                        data = historicalPrices,
                        candles = uiState.candleStickList,
                        chartType = uiState.chartType,
                        selectedTimeFrame = uiState.selectedTimeFrame,
                        onChartTypeChange = { viewModel.setChartType(it) },
                        onTimeFrameChange = { viewModel.setTimeFrame(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 4. AI Summary & Agents
                item {
                    CompanyAiSummaryAndAgents(
                        summary = uiState.aiSummary,
                        risks = uiState.aiRisks,
                        opportunities = uiState.aiOpportunities,
                        agents = uiState.aiAgents
                    )
                }

                // 5. Quick Metrics
                item {
                    CompanyQuickMetrics(metrics = uiState.quickMetrics)
                }

                // 6. Sticky Tabs
                stickyHeader {
                    CompanyTabRow(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        modifier = Modifier
                            .background(Color(0xFFF8F9FA))
                            .padding(vertical = 4.dp)
                    )
                }

                // 7. Tab Content
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        AnimatedContent(
                            targetState = uiState.selectedTab,
                            label = "TabContentAnimation",
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                            }
                        ) { tab ->
                            when (tab) {
                                CompanyDetailTab.OVERVIEW -> TabOverviewContent(
                                    company = uiState.company,
                                    summary = uiState.aiSummary
                                )
                                CompanyDetailTab.FINANCIALS -> TabFinancialsContent(
                                    summary = uiState.financialSummary,
                                    quarterlyData = uiState.quarterlyPerformance,
                                    marginData = uiState.marginAnalysis,
                                    healthData = uiState.financialHealth
                                )
                                CompanyDetailTab.ANALYSIS -> TabAnalysisContent(
                                    valuationModules = uiState.valuationModules,
                                    qualityModules = uiState.qualityModules,
                                    riskModules = uiState.riskModules,
                                    scenarios = uiState.aiScenarios,
                                    targetPrice = uiState.aiTargetPrice,
                                    potential = uiState.aiPotentialReturn,
                                    confidence = uiState.aiConfidenceScore,
                                    summary = uiState.aiSummary
                                )
                                CompanyDetailTab.NEWS -> TabNewsContent(
                                    newsList = uiState.news
                                )
                                CompanyDetailTab.CORPORATE -> TabCorporateContent(
                                    board = uiState.boardMembers,
                                    ownership = uiState.ownershipStructure,
                                    timeline = uiState.corporateTimeline
                                )
                                CompanyDetailTab.AI_ORACLE -> TabAiOracleContent(
                                    report = uiState.aiOracleReport
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
