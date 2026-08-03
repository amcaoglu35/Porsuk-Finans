package com.nexus.porsuk.ui.markets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.analysis.AnalysisViewModel
import com.nexus.porsuk.ui.markets.components.*
import com.nexus.porsuk.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsScreen(
    viewModel: AnalysisViewModel,
    financeViewModel: com.nexus.porsuk.ui.FinanceViewModel? = null,
    onStockClick: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onScreenerClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedGlobalMarketTab by rememberSaveable { mutableIntStateOf(0) }

    val exchangeRates by (financeViewModel?.exchangeRates ?: remember { MutableStateFlow(emptyMap()) }).collectAsState()
    val prices by (financeViewModel?.prices ?: remember { MutableStateFlow(emptyMap()) }).collectAsState()
    val tefasFunds by (financeViewModel?.allTefasFunds ?: remember { MutableStateFlow(emptyList()) }).collectAsState()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        financeViewModel?.refreshExchangeRates()
        listOf("GC=F", "CL=F", "SI=F", "NG=F", "HG=F", "PL=F").forEach {
            financeViewModel?.fetchPrice(it, "COMMODITY")
        }
        listOf("BTC-USD", "ETH-USD", "BNB-USD", "SOL-USD", "XRP-USD", "AVAX-USD", "DOGE-USD").forEach {
            financeViewModel?.fetchPrice(it, "CRYPTO")
        }
        listOf("SPY", "QQQ", "GLD", "VOO", "TLT", "IWM").forEach {
            financeViewModel?.fetchPrice(it, "ETF")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MarketsTopBar(
                onSearchClick = { selectedTab = 2 },
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MarketsTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                label = "markets_tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    0 -> SummaryOverviewTab(
                        isVisible = isVisible,
                        selectedGlobalMarketTab = selectedGlobalMarketTab,
                        onGlobalMarketTabSelected = { selectedGlobalMarketTab = it },
                        onStockClick = onStockClick,
                        onCalendarClick = onCalendarClick,
                        onScreenerClick = onScreenerClick
                    )
                    1 -> IndicesTab(onStockClick = onStockClick)
                    2 -> StocksTab(onStockClick = onStockClick)
                    3 -> ForexTab(exchangeRates = exchangeRates, prices = prices)
                    4 -> CommoditiesTab(prices = prices, exchangeRates = exchangeRates)
                    5 -> CryptoTab(prices = prices, exchangeRates = exchangeRates)
                    6 -> EtfTab(prices = prices)
                    7 -> FundsTab(funds = tefasFunds)
                    8 -> CalendarPreviewTab(onCalendarClick = onCalendarClick)
                    9 -> HeatMapTab()
                    else -> SummaryOverviewTab(
                        isVisible = isVisible,
                        selectedGlobalMarketTab = selectedGlobalMarketTab,
                        onGlobalMarketTabSelected = { selectedGlobalMarketTab = it },
                        onStockClick = onStockClick,
                        onCalendarClick = onCalendarClick,
                        onScreenerClick = onScreenerClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketsTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🦩", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    "PORSUK",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            "Piyasalar",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun MarketsTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = remember {
        listOf("Özet", "Endeksler", "Hisseler", "Döviz", "Emtia", "Kripto", "ETF", "Fonlar", "Takvim", "Heat Map")
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 20.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontFamily = Manrope
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MarketsTopBarPreview() {
    MarketsTopBar(onSearchClick = {}, onNotificationClick = {})
}
