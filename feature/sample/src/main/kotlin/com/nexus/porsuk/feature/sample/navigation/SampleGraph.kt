package com.nexus.porsuk.feature.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexus.porsuk.feature.sample.SampleViewModel
import com.nexus.porsuk.feature.sample.analysis.StockAnalysisScreen
import com.nexus.porsuk.feature.sample.detail.StockDetailScreen
import com.nexus.porsuk.feature.sample.list.SampleListRoute

fun EntryProviderScope<NavKey>.sampleGraph(
    navigator: SampleNavigator,
    sharedViewModel: @Composable () -> SampleViewModel,
) {
    entry<SampleDestination.StockList> {
        val viewModel = sharedViewModel()
        val stocks by viewModel.stocks.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedSector by viewModel.selectedSector.collectAsStateWithLifecycle()
        val marketSentiment by viewModel.marketSentiment.collectAsStateWithLifecycle()
        val macroIndicators by viewModel.macroIndicators.collectAsStateWithLifecycle()
        val latestNews by viewModel.latestNews.collectAsStateWithLifecycle()

        SampleListRoute(
            stocks = stocks,
            searchQuery = searchQuery,
            selectedSector = selectedSector,
            sectors = viewModel.sectors,
            marketSentiment = marketSentiment,
            macroIndicators = macroIndicators,
            latestNews = latestNews,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            onSectorSelected = { viewModel.onSectorSelected(it) },
            onStockClick = { symbol -> navigator.navigateToStockDetail(symbol) },
            onOpenAnalysisMenu = { navigator.navigateToStockAnalysis() }
        )
    }

    entry<SampleDestination.StockAnalysis> {
        val viewModel = sharedViewModel()
        val stocks by viewModel.stocks.collectAsStateWithLifecycle()
        val kapNotices by viewModel.kapNotices.collectAsStateWithLifecycle()
        val corporateEvents by viewModel.corporateEvents.collectAsStateWithLifecycle()
        val institutionalData by viewModel.institutionalData.collectAsStateWithLifecycle()
        val aiLabTools by viewModel.aiLabTools.collectAsStateWithLifecycle()
        val doctorReport by viewModel.portfolioDoctorReport.collectAsStateWithLifecycle()
        val backtestReport by viewModel.backtestReport.collectAsStateWithLifecycle()

        StockAnalysisScreen(
            stocks = stocks,
            kapNotices = kapNotices,
            corporateEvents = corporateEvents,
            institutionalData = institutionalData,
            aiLabTools = aiLabTools,
            portfolioDoctorReport = doctorReport,
            backtestReport = backtestReport,
            onStockDuel = { symbolA, symbolB -> viewModel.runDuel(symbolA, symbolB) },
            onFundOverlap = { codeA, codeB -> viewModel.calculateFundOverlap(codeA, codeB) },
            onStockClick = { symbol -> navigator.navigateToStockDetail(symbol) },
            onBackClick = { navigator.navigateBack() }
        )
    }

    entry<SampleDestination.StockDetail> { destination ->
        val viewModel = sharedViewModel()
        val selectedStock by viewModel.selectedStock.collectAsStateWithLifecycle()

        LaunchedEffect(destination.symbol) {
            viewModel.selectStock(destination.symbol)
        }

        StockDetailScreen(
            stock = selectedStock,
            onBackClick = { navigator.navigateBack() }
        )
    }
}
