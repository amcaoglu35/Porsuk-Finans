package com.nexus.porsuk.ui.markets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.analysis.AnalysisViewModel
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

// Design System Tokens (Light Theme Aesthetic with Purple #6C4CF1 Accent)
private val PurpleAccent = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val LightSurfaceBg = Color(0xFFF8F9FD)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFE2E8F0)
private val BullishGreen = Color(0xFF10B981)
private val BearishRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsScreen(
    viewModel: AnalysisViewModel,
    onStockClick: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onScreenerClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedGlobalMarketTab by rememberSaveable { mutableIntStateOf(0) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            MarketsTopBar(
                onSearchClick = { selectedTab = 2 }, // Switch to Hisseler tab for search
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Üst Sekmeler (Scrollable TabRow with Purple Active Indicator)
            MarketsTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Sekme İçerikleri (AnimatedContent ile Yumuşak Geçiş & State Koruması)
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
                    3 -> ForexTab()
                    4 -> CommoditiesTab()
                    5 -> CryptoTab()
                    6 -> EtfTab()
                    7 -> FundsTab()
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

// ── 1. ÜST BAR ──
@Composable
private fun MarketsTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSurfaceBg)
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
                    color = TextDark
                )
                Text(
                    "F İ N A N S",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp, letterSpacing = 2.5.sp),
                    color = PurpleAccent
                )
            }
        }

        Text(
            "Piyasalar",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDark
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Ara", tint = TextDark)
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Bildirimler", tint = PurpleAccent)
            }
        }
    }
}

// ── 2. SEKMELER (Scrollable TabRow) ──
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
        contentColor = PurpleAccent,
        edgePadding = 20.dp,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = PurpleAccent
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
                        color = if (isSelected) PurpleAccent else TextSecondary
                    )
                }
            )
        }
    }
}

// ── TAB 0: ÖZET EKRANI (Existing Summary Dashboard) ──
@Composable
private fun SummaryOverviewTab(
    isVisible: Boolean,
    selectedGlobalMarketTab: Int,
    onGlobalMarketTabSelected: (Int) -> Unit,
    onStockClick: (String, String) -> Unit,
    onCalendarClick: () -> Unit,
    onScreenerClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "hero_market_cards") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
            ) {
                HeroMarketCardsRow()
            }
        }

        item(key = "sector_performance_card") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                SectorPerformanceSection()
            }
        }

        item(key = "gainers_losers_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                GainersAndLosersSection(onStockClick = onStockClick)
            }
        }

        item(key = "global_markets_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
            ) {
                GlobalMarketsSection(
                    selectedTab = selectedGlobalMarketTab,
                    onTabSelected = onGlobalMarketTabSelected
                )
            }
        }

        item(key = "world_heatmap_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
            ) {
                WorldMarketHeatmapSection()
            }
        }

        item(key = "quick_tools_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(900)) + slideInVertically(initialOffsetY = { 80 })
            ) {
                QuickToolsGridSection(
                    onCalendarClick = onCalendarClick,
                    onScreenerClick = onScreenerClick
                )
            }
        }
    }
}

// ── TAB 1: ENDEKSLER (BIST100, BIST30, NASDAQ, S&P500, DAX, FTSE, Nikkei, Hang Seng) ──
@Composable
private fun IndicesTab(onStockClick: (String, String) -> Unit) {
    val indices = remember {
        listOf(
            IndexItem("BIST 100", "🇹🇷 Türkiye", "10.456,87", "^ %1,35", "₺14.2B Hacim", true, listOf(40f, 42f, 45f, 48f, 50f)),
            IndexItem("BIST 30", "🇹🇷 Türkiye", "11.632,15", "^ %1,28", "₺11.8B Hacim", true, listOf(42f, 43f, 46f, 49f, 52f)),
            IndexItem("NASDAQ", "🇺🇸 ABD", "16.832,62", "^ %1,28", "$42.5B Hacim", true, listOf(60f, 62f, 65f, 68f, 70f)),
            IndexItem("S&P 500", "🇺🇸 ABD", "5.325,16", "^ %0,88", "$38.1B Hacim", true, listOf(50f, 52f, 51f, 55f, 58f)),
            IndexItem("DAX 40", "🇩🇪 Almanya", "18.720,40", "v %-0,25", "€6.4B Hacim", false, listOf(188f, 187f, 187.2f)),
            IndexItem("FTSE 100", "🇬🇧 İngiltere", "8.245,10", "^ %0,42", "£4.2B Hacim", true, listOf(81f, 82f, 82.45f)),
            IndexItem("Nikkei 225", "🇯🇵 Japonya", "38.650,00", "^ %0,75", "¥2.8T Hacim", true, listOf(380f, 383f, 386.5f)),
            IndexItem("Hang Seng", "🇭🇰 Hong Kong", "17.920,80", "v %-0,65", "HK$18.5B", false, listOf(181f, 180f, 179.2f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(indices, key = { it.name }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item.countryFlag, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.volume, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) BullishGreen else BearishRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class IndexItem(val name: String, val countryFlag: String, val price: String, val changePct: String, val volume: String, val isPos: Boolean, val sparkValues: List<Float>)

// ── TAB 2: HİSSELER (BIST & ABD Hisseleri + Arama, Filtre, Sıralama, Favoriler) ──
@Composable
private fun StocksTab(onStockClick: (String, String) -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableIntStateOf(0) } // 0: Tümü, 1: BIST, 2: ABD, 3: Favoriler
    var favoriteSet by remember { mutableStateOf(setOf("THYAO", "ASELS", "NVDA")) }

    val allStocks = remember {
        listOf(
            StockItem("THYAO", "Türk Hava Yolları", "₺305,25", "^ %2,87", "₺8.2B Hacim", "BIST", true),
            StockItem("ASELS", "Aselsan", "₺56,70", "^ %4,25", "₺4.5B Hacim", "BIST", true),
            StockItem("NVDA", "NVIDIA Corporation", "$128,20", "^ %3,45", "$32.4B Hacim", "NASDAQ", true),
            StockItem("AAPL", "Apple Inc.", "$224,30", "^ %1,12", "$21.8B Hacim", "NASDAQ", true),
            StockItem("KCHOL", "Koç Holding", "₺182,40", "^ %0,31", "₺1.8B Hacim", "BIST", true),
            StockItem("AKBNK", "Akbank", "₺52,15", "v %-0,42", "₺2.4B Hacim", "BIST", false),
            StockItem("TSLA", "Tesla Inc.", "$248,50", "v %-1,85", "$18.6B Hacim", "NASDAQ", false),
            StockItem("MSFT", "Microsoft Corp.", "$447,20", "^ %0,95", "$14.2B Hacim", "NASDAQ", true),
            StockItem("SISE", "Şişecam", "₺49,18", "^ %1,98", "₺950M Hacim", "BIST", true),
            StockItem("AMZN", "Amazon.com Inc.", "$186,10", "^ %1,45", "$12.9B Hacim", "NASDAQ", true)
        )
    }

    val filteredStocks = remember(searchQuery, selectedFilter, favoriteSet) {
        allStocks.filter { stock ->
            val matchesSearch = stock.symbol.contains(searchQuery, ignoreCase = true) || stock.name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                1 -> stock.market == "BIST"
                2 -> stock.market != "BIST"
                3 -> favoriteSet.contains(stock.symbol)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Arama Çubuğu
        item(key = "stock_search_bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Hisse ara (Örn: THYAO, NVDA...)", style = MaterialTheme.typography.bodyMedium, color = TextSecondary) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = PurpleAccent) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextSecondary)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = BorderColor
                ),
                singleLine = true
            )
        }

        // Filter Chips Row
        item(key = "stock_filter_chips") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Tümü", "BIST", "ABD", "⭐ Favoriler").forEachIndexed { idx, label ->
                    val isSelected = selectedFilter == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PurpleSoftBg else CardBg,
                        border = BorderStroke(1.dp, if (isSelected) PurpleAccent else BorderColor),
                        modifier = Modifier.clickable { selectedFilter = idx }
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) PurpleAccent else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Hisseler Listesi
        items(filteredStocks, key = { it.symbol }) { item ->
            val isFav = favoriteSet.contains(item.symbol)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .clickable { onStockClick(item.symbol, item.market) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PurpleSoftBg,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PurpleAccent)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                        Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favori",
                        tint = if (isFav) Color(0xFFFFB800) else TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                favoriteSet = if (isFav) favoriteSet - item.symbol else favoriteSet + item.symbol
                            }
                    )
                }
            }
        }
    }
}

private data class StockItem(val symbol: String, val name: String, val price: String, val changePct: String, val volume: String, val market: String, val isPos: Boolean)

// ── TAB 3: DÖVİZ (USD, EUR, GBP, CHF, JPY, CAD, AUD) ──
@Composable
private fun ForexTab() {
    val forexList = remember {
        listOf(
            ForexItem("USD / TRY", "Amerikan Doları", "32,65", "₺32,62 / ₺32,68", "^ %0,42", true, listOf(32f, 32.2f, 32.4f, 32.65f)),
            ForexItem("EUR / TRY", "Euro", "35,48", "₺35,44 / ₺35,52", "^ %0,35", true, listOf(35f, 35.2f, 35.48f)),
            ForexItem("GBP / TRY", "İngiliz Sterlini", "42,15", "₺42,10 / ₺42,20", "^ %0,58", true, listOf(41.5f, 41.8f, 42.15f)),
            ForexItem("CHF / TRY", "İsviçre Frangı", "36,80", "₺36,75 / ₺36,85", "v %-0,15", false, listOf(37f, 36.9f, 36.8f)),
            ForexItem("JPY / TRY", "Japon Yeni (100)", "20,85", "₺20,80 / ₺20,90", "^ %0,12", true, listOf(20.5f, 20.7f, 20.85f)),
            ForexItem("CAD / TRY", "Kanada Doları", "23,90", "₺23,85 / ₺23,95", "^ %0,22", true, listOf(23.6f, 23.8f, 23.9f)),
            ForexItem("AUD / TRY", "Avustralya Doları", "21,75", "₺21,70 / ₺21,80", "v %-0,28", false, listOf(22f, 21.9f, 21.75f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forexList, key = { it.pair }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.pair, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.spread, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = IBMPlexMono), color = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.rate, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) BullishGreen else BearishRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class ForexItem(val pair: String, val name: String, val rate: String, val spread: String, val changePct: String, val isPos: Boolean, val sparkValues: List<Float>)

// ── TAB 4: EMTİA (Altın, Gümüş, Petrol, Doğalgaz, Bakır, Platin) ──
@Composable
private fun CommoditiesTab() {
    val commodities = remember {
        listOf(
            CommodityItem("ALTIN / GR", "Gram Altın (TL)", "₺2.395,45", "^ %0,31", "🪙", true, listOf(2380f, 2390f, 2395f)),
            CommodityItem("ONS ALTIN", "Ons Altın ($)", "$2.368,20", "^ %0,45", "🔱", true, listOf(2350f, 2360f, 2368f)),
            CommodityItem("GÜMÜŞ", "Gram Gümüş (TL)", "₺29,85", "^ %1,12", "⚪", true, listOf(29f, 29.4f, 29.85f)),
            CommodityItem("BRENT PETROL", "Ham Petrol ($/Varil)", "$84.20", "^ %0,75", "🛢️", true, listOf(82f, 83f, 84.2f)),
            CommodityItem("DOĞALGAZ", "Doğalgaz ($/MMBtu)", "$2,48", "v %-1,45", "🔥", false, listOf(2.55f, 2.5f, 2.48f)),
            CommodityItem("BAKIR", "Bakır ($/Lb)", "$4,45", "^ %0,85", "🧱", true, listOf(4.3f, 4.4f, 4.45f)),
            CommodityItem("PLATİN", "Platin ($/Ons)", "$985,50", "v %-0,35", "💎", false, listOf(995f, 990f, 985.5f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(commodities, key = { it.name }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                        Surface(
                            shape = CircleShape,
                            color = PurpleSoftBg,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.iconEmoji, fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Text(item.subName, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) BullishGreen else BearishRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class CommodityItem(val name: String, val subName: String, val price: String, val changePct: String, val iconEmoji: String, val isPos: Boolean, val sparkValues: List<Float>)

// ── TAB 5: KRİPTO (Bitcoin, Ethereum, BNB, Solana, XRP, Avalanche, Dogecoin) ──
@Composable
private fun CryptoTab() {
    val cryptoList = remember {
        listOf(
            CryptoItem("Bitcoin", "BTC", "$67.450,00", "₺2.202.245", "^ %2,10", "$1.32T MCap", "$28.4B Hacim", true, listOf(65000f, 66000f, 67450f)),
            CryptoItem("Ethereum", "ETH", "$3.480,20", "₺113.628", "^ %1,85", "$418.5B MCap", "$14.2B Hacim", true, listOf(3400f, 3450f, 3480f)),
            CryptoItem("BNB", "BNB", "$582,40", "₺19.015", "^ %0,92", "$85.2B MCap", "$1.8B Hacim", true, listOf(575f, 580f, 582.4f)),
            CryptoItem("Solana", "SOL", "$142,80", "₺4.662", "v %-1,25", "$66.4B MCap", "$3.2B Hacim", false, listOf(148f, 145f, 142.8f)),
            CryptoItem("XRP", "XRP", "$0,584", "₺19,06", "^ %4,12", "$32.8B MCap", "$2.4B Hacim", true, listOf(0.55f, 0.57f, 0.584f)),
            CryptoItem("Avalanche", "AVAX", "$28,45", "₺928", "v %-0,85", "$11.2B MCap", "$480M Hacim", false, listOf(29f, 28.8f, 28.45f)),
            CryptoItem("Dogecoin", "DOGE", "$0,128", "₺4,18", "^ %3,25", "$18.6B MCap", "$1.1B Hacim", true, listOf(0.12f, 0.124f, 0.128f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cryptoList, key = { it.symbol }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                        Surface(
                            shape = CircleShape,
                            color = PurpleSoftBg,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.symbol.take(3), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp), color = PurpleAccent)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                            Text(item.marketCap, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.priceUsd, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.change24h, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) BullishGreen else BearishRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class CryptoItem(
    val name: String, val symbol: String, val priceUsd: String, val priceTry: String,
    val change24h: String, val marketCap: String, val volume24h: String, val isPos: Boolean, val sparkValues: List<Float>
)

// ── TAB 6: ETF ──
@Composable
private fun EtfTab() {
    val etfs = remember {
        listOf(
            EtfItem("SPY", "SPDR S&P 500 ETF", "$542,10", "^ %0,88", "$520B AUM", true, listOf(535f, 538f, 542.1f)),
            EtfItem("QQQ", "Invesco QQQ Trust (Nasdaq 100)", "$478,50", "^ %1,32", "$280B AUM", true, listOf(470f, 474f, 478.5f)),
            EtfItem("GLD", "SPDR Gold Shares", "$218,40", "^ %0,42", "$62B AUM", true, listOf(216f, 217f, 218.4f)),
            EtfItem("VOO", "Vanguard S&P 500 ETF", "$498,20", "^ %0,85", "$450B AUM", true, listOf(492f, 495f, 498.2f)),
            EtfItem("TLT", "iShares 20+ Year Treasury Bond", "$92,15", "v %-0,45", "$52B AUM", false, listOf(93f, 92.5f, 92.15f)),
            EtfItem("IWM", "iShares Russell 2000 ETF", "$212,80", "^ %1,85", "$75B AUM", true, listOf(208f, 210f, 212.8f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(etfs, key = { it.symbol }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.aum, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = IBMPlexMono), color = PurpleAccent)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) BullishGreen else BearishRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class EtfItem(val symbol: String, val name: String, val price: String, val changePct: String, val aum: String, val isPos: Boolean, val sparkValues: List<Float>)

// ── TAB 7: FONLAR (TEFAS / Model Sepetler) ──
@Composable
private fun FundsTab() {
    val funds = remember {
        listOf(
            FundItem("TTE", "İş Portföy Teknoloji Karma Fon", "%48,2 Yıllık Getiri", "TEFAS", "^ %1,85", true),
            FundItem("AFT", "Ak Portföy Yeni Teknolojiler Fonu", "%52,6 Yıllık Getiri", "TEFAS", "^ %2,10", true),
            FundItem("YAY", "Yapı Kredi Portföy Yabancı Teknoloji", "%46,8 Yıllık Getiri", "TEFAS", "^ %1,45", true),
            FundItem("TCD", "Tacirler Portföy Değişken Fon", "%68,4 Yıllık Getiri", "TEFAS", "^ %0,92", true),
            FundItem("IPV", "İş Portföy Elektrikli Araçlar Fonu", "%34,1 Yıllık Getiri", "TEFAS", "v %-0,45", false)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(funds, key = { it.code }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PurpleSoftBg,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.code, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = PurpleAccent)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.returnRate, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(item.dailyChange, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) BullishGreen else BearishRed)
                    }
                }
            }
        }
    }
}

private data class FundItem(val code: String, val name: String, val returnRate: String, val category: String, val dailyChange: String, val isPos: Boolean)

// ── TAB 8: TAKVİM PREVIEW ──
@Composable
private fun CalendarPreviewTab(onCalendarClick: () -> Unit) {
    val events = remember {
        listOf(
            CalendarEventItem("28 Temmuz", "FED Faiz Kararı", "ABD Merkez Bankası faiz kararı ve Fed başkanı konuşması.", "Yüksek Etki"),
            CalendarEventItem("30 Temmuz", "TCMB Enflasyon Raporu", "Merkez Bankası 3. Çeyrek Enflasyon Raporu Sunumu.", "Yüksek Etki"),
            CalendarEventItem("02 Ağustos", "BİST 100 2Ç Bilanço Dönemi", "Şirketlerin 2. çeyrek finansal sonuçlarının açıklanması.", "Orta Etki")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "calendar_header_button") {
            Button(
                onClick = onCalendarClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("📅 Tüm Temettü & Halka Arz Takvimini Aç", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
            }
        }

        items(events, key = { it.title }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = PurpleSoftBg, modifier = Modifier.size(50.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(item.date.split(" ").firstOrNull() ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PurpleAccent)
                            Text(item.date.split(" ").lastOrNull() ?: "", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                        Text(item.desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = BullishGreen.copy(alpha = 0.12f)) {
                        Text(item.impact, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = BullishGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}

private data class CalendarEventItem(val date: String, val title: String, val desc: String, val impact: String)

// ── TAB 9: HEAT MAP & ANALİZ ──
@Composable
private fun HeatMapTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "full_heatmap_section") {
            WorldMarketHeatmapSection()
        }
    }
}

// ── EXISTING COMPONENTS (Hero Market Cards, Sector Performance, Gainers/Losers, Global Markets, Heatmap, Quick Tools) ──
@Composable
private fun HeroMarketCardsRow() {
    val cardsData = remember {
        listOf(
            HeroMarketCardItem("BIST 100", "10.456,87", "^ %1,35", true, "🟣", listOf(40f, 42f, 41f, 45f, 44f, 48f, 50f)),
            HeroMarketCardItem("BIST 30", "11.632,15", "^ %1,28", true, "🔵", listOf(42f, 43f, 45f, 47f, 49f, 52f)),
            HeroMarketCardItem("DOLAR / TL", "32,65", "^ %0,42", true, "💵", listOf(32f, 32.2f, 32.4f, 32.5f, 32.65f)),
            HeroMarketCardItem("ALTIN / GR", "2.395,45", "^ %0,31", true, "🪙", listOf(2380f, 2385f, 2390f, 2395f)),
            HeroMarketCardItem("USD / EUR", "0,9142", "v %-0,28", false, "💶", listOf(95f, 94f, 93f, 92f, 91.4f)),
            HeroMarketCardItem("BRENT", "84.20", "^ %0,75", true, "🛢️", listOf(82f, 83f, 83.5f, 84.2f)),
            HeroMarketCardItem("BITCOIN", "67.450,00", "^ %2,10", true, "₿", listOf(65000f, 66000f, 67450f))
        )
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(cardsData, key = { it.title }) { item ->
            HeroMarketCard(item)
        }
    }
}

private data class HeroMarketCardItem(val title: String, val price: String, val changePct: String, val isPositive: Boolean, val iconEmoji: String, val sparkValues: List<Float>)

@Composable
private fun HeroMarketCard(item: HeroMarketCardItem) {
    val color = if (item.isPositive) BullishGreen else BearishRed

    Card(
        modifier = Modifier
            .width(145.dp)
            .shadow(4.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = PurpleSoftBg,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.iconEmoji, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(item.price, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = color)
            Spacer(modifier = Modifier.height(8.dp))

            Sparkline(
                values = item.sparkValues,
                color = color,
                modifier = Modifier.fillMaxWidth().height(28.dp),
                filled = true
            )
        }
    }
}

@Composable
private fun SectorPerformanceSection() {
    val sectors = remember {
        listOf(
            SectorItem("Bankacılık", "^ %2,45", true, listOf(40f, 45f, 44f, 48f, 52f)),
            SectorItem("Savunma", "^ %2,12", true, listOf(30f, 32f, 35f, 38f, 40f)),
            SectorItem("Teknoloji", "^ %1,85", true, listOf(50f, 52f, 51f, 54f, 56f)),
            SectorItem("Holding", "^ %0,98", true, listOf(60f, 61f, 62f, 63f)),
            SectorItem("Ulaştırma", "v %-0,35", false, listOf(70f, 69f, 68f, 67f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📊", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sektör Performansı (BIST)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tümünü Gör", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = PurpleAccent)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sectors, key = { it.name }) { sector ->
                    val color = if (sector.isPositive) BullishGreen else BearishRed
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = LightSurfaceBg,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.width(115.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(sector.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(sector.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = color)
                            Spacer(modifier = Modifier.height(6.dp))
                            Sparkline(
                                values = sector.sparkValues,
                                color = color,
                                modifier = Modifier.fillMaxWidth().height(22.dp),
                                filled = true
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class SectorItem(val name: String, val changePct: String, val isPositive: Boolean, val sparkValues: List<Float>)

@Composable
private fun GainersAndLosersSection(onStockClick: (String, String) -> Unit) {
    val gainers = remember {
        listOf(
            StockRowItem("ASELS", "₺56,70", "^ %4,25", true),
            StockRowItem("THYAO", "₺305,25", "^ %2,87", true),
            StockRowItem("KCHOL", "₺182,40", "^ %2,31", true),
            StockRowItem("SISE", "₺49,18", "^ %1,98", true),
            StockRowItem("EKGYO", "₺10,52", "^ %1,76", true)
        )
    }

    val losers = remember {
        listOf(
            StockRowItem("AKBNK", "₺52,15", "v %-0,42", false),
            StockRowItem("TUPRS", "₺165,80", "v %-0,85", false),
            StockRowItem("SASA", "₺38,72", "v %-1,22", false),
            StockRowItem("EREGL", "₺45,10", "v %-1,34", false),
            StockRowItem("PETKM", "₺17,26", "v %-1,88", false)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = BullishGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yükselenler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = TextDark)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(10.dp))

                gainers.forEach { item ->
                    StockListItemRow(item = item, onClick = { onStockClick(item.symbol, "BIST") })
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingDown, contentDescription = null, tint = BearishRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Düşenler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = TextDark)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
                }

                Spacer(modifier = Modifier.height(10.dp))

                losers.forEach { item ->
                    StockListItemRow(item = item, onClick = { onStockClick(item.symbol, "BIST") })
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

private data class StockRowItem(val symbol: String, val price: String, val changePct: String, val isPositive: Boolean)

@Composable
private fun StockListItemRow(item: StockRowItem, onClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
    val color = if (item.isPositive) BullishGreen else BearishRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.symbol, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = TextDark)
            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = TextSecondary)
        }

        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = color)
        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "Favori",
            tint = if (isFavorite) Color(0xFFFFB800) else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp).clickable { isFavorite = !isFavorite }
        )
    }
}

@Composable
private fun GlobalMarketsSection(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val filterTabs = remember { listOf("ABD", "Avrupa", "Asya", "Emtia", "Kripto") }
    val globalItems = remember {
        listOf(
            GlobalMarketItem("S&P 500", "5.325,16", "^ %0,88", true, "🇺🇸", listOf(50f, 52f, 51f, 55f, 58f)),
            GlobalMarketItem("NASDAQ", "16.832,62", "^ %1,28", true, "🇺🇸", listOf(60f, 62f, 65f, 68f, 70f)),
            GlobalMarketItem("DOW JONES", "39.872,99", "^ %0,75", true, "🇺🇸", listOf(390f, 392f, 395f, 398f))
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dünya Piyasaları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                }
                Text("Tümünü Gör >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filterTabs.forEachIndexed { idx, label ->
                    val isSelected = selectedTab == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PurpleSoftBg else LightSurfaceBg,
                        border = BorderStroke(1.dp, if (isSelected) PurpleAccent else BorderColor),
                        modifier = Modifier.clickable { onTabSelected(idx) }
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 10.5.sp),
                            color = if (isSelected) PurpleAccent else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            globalItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                        Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono), color = TextSecondary)
                    }

                    Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = BullishGreen, modifier = Modifier.weight(0.8f))
                    Sparkline(values = item.sparkValues, color = BullishGreen, modifier = Modifier.weight(1.0f).height(24.dp), filled = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.flagEmoji, fontSize = 18.sp)
                }
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            }
        }
    }
}

private data class GlobalMarketItem(val name: String, val price: String, val changePct: String, val isPositive: Boolean, val flagEmoji: String, val sparkValues: List<Float>)

@Composable
private fun WorldMarketHeatmapSection() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗺️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Piyasa Haritası", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.weight(1.3f).height(120.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Düşüş", fontSize = 8.sp, color = BearishRed, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.width(60.dp).height(4.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(BearishRed, Color.LightGray, BullishGreen))))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yükseliş", fontSize = 8.sp, color = BullishGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1.0f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RegionHeatmapRow("Kuzey Amerika", "^ %0,82", true)
                    RegionHeatmapRow("Avrupa", "^ %0,35", true)
                    RegionHeatmapRow("Asya", "v %-0,15", false)
                    RegionHeatmapRow("Gelişen Piyasalar", "^ %0,48", true)
                }
            }
        }
    }
}

@Composable
private fun RegionHeatmapRow(region: String, change: String, isPositive: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(region, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontFamily = Manrope), color = TextDark)
        Text(change, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 10.5.sp), color = if (isPositive) BullishGreen else BearishRed)
    }
}

@Composable
private fun QuickToolsGridSection(onCalendarClick: () -> Unit, onScreenerClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı Araçlar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickToolCard("Piyasa Takvimi", "Bugünkü veriler", "⚡", Color(0xFFF3F0FF), PurpleAccent, onCalendarClick, Modifier.weight(1f))
            QuickToolCard("Ekonomik Takvim", "Önemli gelişmeler", "📊", Color(0xFFEFF6FF), Color(0xFF2563EB), onCalendarClick, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickToolCard("Hareketli Hisseler", "Anlık momentum", "🔥", Color(0xFFFFF7ED), Color(0xFFEA580C), onScreenerClick, Modifier.weight(1f))
            QuickToolCard("Hisse Filtresi", "Tarama araçları", "🎯", Color(0xFFECFDF5), BullishGreen, onScreenerClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickToolCard(title: String, subtitle: String, iconEmoji: String, containerColor: Color, iconColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = containerColor, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = Manrope), color = TextDark)
                Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary, fontFamily = Manrope)
            }
        }
    }
}

// ── PREVIEW SUPPORT ──
@Preview(showBackground = true)
@Composable
private fun MarketsTopBarPreview() {
    MarketsTopBar(onSearchClick = {}, onNotificationClick = {})
}
