package com.nexus.porsuk.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CompanyLogo
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.common.SpeedDialFAB
import com.nexus.porsuk.ui.common.PortfolioPieChart
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.data.remote.RichOfflineDataEngine
import com.nexus.porsuk.ui.theme.*
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel, 
    onStockClick: (String, String) -> Unit,
    onBasketClick: (Int) -> Unit,
    onLedgerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onModelSepetlerClick: () -> Unit,
    onKapRadarClick: () -> Unit = {},
    onChatClick: (String) -> Unit
) {
    val tickerData by viewModel.tickerData.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())
    val baskets by viewModel.allBaskets.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())
    val totalBalance by viewModel.totalBalanceTry.collectAsState()
    val totalChange by viewModel.totalChangePercent.collectAsState()
    val profitMode by viewModel.profitMode.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    // val portfolioHistory by viewModel.portfolioHistory.collectAsState()
    // val xu100History by viewModel.xu100History.collectAsState()
    val sectorData by viewModel.portfolioSectorData.collectAsState()
    
    var showSearchDialog by remember { mutableStateOf(false) }
    var showHealthCheckSheet by remember { mutableStateOf(false) }
    val healthCheckResult by viewModel.portfolioHealthCheckResult.collectAsState()
    val isHealthChecking by viewModel.isHealthChecking.collectAsState()
    
    var showRebalanceSheet by remember { mutableStateOf(false) }
    val rebalanceResult by viewModel.portfolioRebalanceResult.collectAsState()
    val isRebalancing by viewModel.isRebalancing.collectAsState()

    val cachedInfoList by viewModel.allCachedInfo.collectAsState(initial = emptyList())
    val cachedInfoMap = remember(cachedInfoList) { cachedInfoList.associateBy { it.symbol } }

    var searchQuery by remember { mutableStateOf("") } // Evrensel arama
    var filterQuery by remember { mutableStateOf("") } // Hisse ara
    var selectedMarketFilter by remember { mutableStateOf("Tümü") }
    var dividendOnly by remember { mutableStateOf(false) }
    var lowPeOnly by remember { mutableStateOf(false) }
    var listTab by remember { mutableIntStateOf(0) } // 0=Takip, 1=Popüler, 2=Tüm, 3=Sepetler

    val trLocale = Locale("tr", "TR")
    val activeAlertCount by viewModel.allPriceAlerts.collectAsState(initial = emptyList())

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopBar(onLedgerClick = onLedgerClick, onProfileClick = onSettingsClick, alertCount = activeAlertCount.size) },
        floatingActionButton = {
            SpeedDialFAB(
                onAddStockClick = { showSearchDialog = true }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshAllData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundNew),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item(key = "moving_ticker") { MovingTickerBar(tickerData) }
                item(key = "spacer_ticker") { Spacer(modifier = Modifier.height(12.dp)) }

                // 4. Toplam Varlık hero kartı (Getiri Hesabı Seçici İçine Entegre Edildi)
                item(key = "total_asset_card") { 
                    val cardTitle = when (profitMode) {
                        FinanceViewModel.ProfitCalculationMode.NOMINAL -> "TOPLAM VARLIK (NOMİNAL)"
                        FinanceViewModel.ProfitCalculationMode.INFLATION_ADJUSTED -> "TOPLAM VARLIK (REEL ENFLASYON)"
                        FinanceViewModel.ProfitCalculationMode.USD_ADJUSTED -> "TOPLAM VARLIK (DOLAR BAZLI)"
                    }
                    BalancedTotalAssetCard(
                        totalValueTry = CurrencyFormatter.formatTRY(totalBalance, numberFormat).replace("₺", ""),
                        totalValueUsd = CurrencyFormatter.formatWithSymbol(totalBalance / (viewModel.exchangeRates.value["USD"] ?: RichOfflineDataEngine.BASE_USD_TRY), "", numberFormat).trim(),
                        totalValueEur = CurrencyFormatter.formatWithSymbol(totalBalance / (viewModel.exchangeRates.value["EUR"] ?: RichOfflineDataEngine.BASE_EUR_TRY), "", numberFormat).trim(),
                        percentageChange = String.format(Locale.US, "%+.2f%%", totalChange),
                        cardTitle = cardTitle,
                        profitMode = profitMode,
                        onProfitModeChange = { viewModel.setProfitMode(it) }
                    )
                }
                item(key = "spacer_asset") { Spacer(modifier = Modifier.height(12.dp)) }

                    item(key = "quick_actions") {
                        QuickActionsGrid(
                            onLedgerClick = onLedgerClick,
                            onCalendarClick = onCalendarClick,
                            onAnalysisClick = onAnalysisClick,
                            onModelSepetlerClick = onModelSepetlerClick,
                            onKapRadarClick = onKapRadarClick
                        )
                    }
                    item(key = "spacer_quick") { Spacer(modifier = Modifier.height(16.dp)) }

                    item(key = "oracle_wisdom") {
                        val wisdomText = remember(watchlist, totalBalance, totalChange) {
                            val rand = java.util.Random(System.currentTimeMillis() / (24 * 3600 * 1000))
                            if (totalChange >= 0) {
                                val positives = listOf(
                                    "Piyasa yeşil! Orakul der ki: 'Asimetrik fırsatlar sabredenleri bekler. Kâr realizasyonu yapmayı unutma!' 📈",
                                    "Warren Buffett'ın dediği gibi: 'Gelgit çekildiğinde kimin çıplak yüzdüğünü görürüz.' Disiplinli kal, risk kontrolünü elden bırakma! 🧠",
                                    "Portföyün parlıyor! Ancak coşkuya kapılma, portföyün çeşitlendirme puanını (Check-up) kontrol ederek dengeni koru. ⚖️"
                                )
                                positives[rand.nextInt(positives.size)]
                            } else {
                                val negatives = listOf(
                                    "Piyasa kırmızıya boyanmış. Graham der ki: 'Düşüşler sadece kaliteli hisseleri indirimli almak için harika fırsatlardır!' ⛏️",
                                    "Kırmızı günler Orakul'un en sevdiği günlerdir. 'Borsa, sabırsızlardan sabırlılara para aktarma aracıdır.' Sakin kal. 🔮",
                                    "Portföy değer kaybetmiş görünebilir. Panik yapma! Sektörel rotasyonun gücüne güven ve Orakul'un Rebalans Raporu'nu incele. 🛡️"
                                )
                                negatives[rand.nextInt(negatives.size)]
                            }
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardNew),
                            border = BorderStroke(1.dp, LineBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0x3B6366F1),
                                                Color(0x1F8B5CF6)
                                            )
                                        )
                                    )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔮", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Orakul'un Günlük Bilgeliği",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryTeal,
                                            fontFamily = Manrope
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = wisdomText,
                                        fontSize = 12.sp,
                                        color = InkText,
                                        fontFamily = Manrope,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { onChatClick("Orakul, bugünkü borsa bilgelik mesajın hakkında detaylı analiz yapar mısın?") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.align(Alignment.End).height(32.dp)
                                    ) {
                                        Text("Orakul'a Soru Sor", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                                    }
                                }
                            }
                        }
                    }
                    item(key = "spacer_wisdom") { Spacer(modifier = Modifier.height(16.dp)) }

                    item(key = "market_sentiment") {
                        val sentimentScore = remember(companies, prices) {
                            if (companies.isEmpty()) 65
                            else {
                                val avgChange = companies.map { c ->
                                    prices[c.symbol]?.changePercent ?: c.changePercent
                                }.average()
                                (50 + avgChange * 10).toInt().coerceIn(10, 99)
                            }
                        }
                        
                        val sentimentLabel = when {
                            sentimentScore < 35 -> "AŞIRI KORKU 😨"
                            sentimentScore < 65 -> "NÖTR 😐"
                            else -> "AŞIRI AÇGÖZLÜK 🤑"
                        }
                        val sentimentColor = when {
                            sentimentScore < 35 -> NegatifRed
                            sentimentScore < 65 -> Orange
                            else -> PrimaryTeal
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CardNew),
                            border = BorderStroke(1.dp, LineBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Piyasa Duyarlılık İndeksi",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InkText,
                                        fontFamily = Manrope
                                    )
                                    Text(
                                        text = "$sentimentScore / 100",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = sentimentColor,
                                        fontFamily = IBMPlexMono
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Color Gradient Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFFF1744),
                                                    Color(0xFFFF9100),
                                                    Color(0xFF00E676)
                                                )
                                            )
                                        )
                                ) {
                                    // Pointer indicator
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(sentimentScore / 100f)
                                            .background(Color.Transparent)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .border(2.dp, sentimentColor, CircleShape)
                                                .align(Alignment.CenterEnd)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Aşırı Korku", fontSize = 8.sp, color = SubText, fontFamily = Manrope)
                                    Text(sentimentLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = sentimentColor, fontFamily = Manrope)
                                    Text("Aşırı İyimser", fontSize = 8.sp, color = SubText, fontFamily = Manrope)
                                }
                            }
                        }
                    }
                    item(key = "portfolio_heatmap") {
                        val allBasketItems by viewModel.allBasketItems.collectAsState(initial = emptyList())
                        val heatmapItems = remember(allBasketItems, prices, companies) {
                            val companyMap = companies.associateBy { it.symbol }
                            allBasketItems.map { item ->
                                val currentPrice = prices[item.symbol]?.price ?: companyMap[item.symbol]?.currentPrice ?: item.buyPrice
                                val changePct = prices[item.symbol]?.changePercent ?: companyMap[item.symbol]?.changePercent ?: 0.0
                                com.nexus.porsuk.ui.common.HeatmapItem(
                                    symbol = item.symbol,
                                    value = item.quantity * currentPrice,
                                    changePercent = changePct
                                )
                            }
                        }
                        com.nexus.porsuk.ui.common.PortfolioHeatmap(
                            items = heatmapItems,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onAssetClick = { sym -> onStockClick(sym, "BIST") }
                        )
                    }
                    item(key = "spacer_heatmap") { Spacer(modifier = Modifier.height(16.dp)) }

                    // 6. Orakul Akıllı Rebalans Raporu banner
                    item(key = "ai_rebalance_card") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clickable {
                                    viewModel.runPortfolioRebalance()
                                    showRebalanceSheet = true
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardNew),
                            border = BorderStroke(1.dp, LineBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFFFF0E6),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚖️",
                                        fontSize = 32.sp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Orakul Akıllı Rebalans Raporu",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Orange,
                                            fontFamily = Manrope
                                        )
                                        Text(
                                            text = "Portföy ağırlıklarını O-EAGI formülüne göre optimize etmek ve dengelemek için tıkla.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SubText,
                                            fontFamily = Manrope
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item(key = "spacer_rebalance") { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    // 7. Bugün Öne Çıkanlar (Highlights)
                    if (watchlist.isNotEmpty()) {
                        item(key = "highlights_header") { SectionHeader(title = "Bugün Öne Çıkanlar") }
                        item(key = "highlights_row") {
                            val highlights = watchlist.map { item ->
                                val change = prices[item.symbol]?.changePercent ?: 0.0
                                val company = companies.find { it.symbol == item.symbol }
                                val market = company?.market ?: "BIST"
                                Triple(item.symbol, change, market)
                            }.sortedByDescending { abs(it.second) }.take(4)
                            
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(highlights, key = { "highlight_${it.first}" }) { (symbol, change, market) ->
                                    HighlightChip(symbol, change, numberFormat, onClick = { onStockClick(symbol, market) })
                                }
                            }
                        }
                        item(key = "spacer_highlights") { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    items(baskets.filter { it.market == "BIST" || it.market == "IST" }, key = { "basket_${it.id}" }) { basket ->
                        BasketCard(basket, onClick = { onBasketClick(basket.id) })
                    }
                    item(key = "spacer_baskets") { Spacer(modifier = Modifier.height(16.dp)) }

                    // 10. Yapay Zeka Portföy Check-up banner
                    item(key = "ai_health_card") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clickable {
                                    viewModel.runPortfolioHealthCheck()
                                    showHealthCheckSheet = true
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardNew),
                            border = BorderStroke(1.dp, LineBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                AquaSoft,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🩺",
                                        fontSize = 32.sp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Yapay Zeka Portföy Check-up'ı",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = PrimaryTeal,
                                            fontFamily = Manrope
                                        )
                                        Text(
                                            text = "Portföyünün risk ve çeşitlilik durumunu analiz etmek ve sağlık puanı almak için tıkla.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SubText,
                                            fontFamily = Manrope
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item(key = "spacer_health") { Spacer(modifier = Modifier.height(16.dp)) }

                    // ── OKX tarzı inline pill-tab + liste ──
                    item(key = "list_tab_switcher") {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Arama çubuğu her zaman görünür
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Hisse, şirket ara...", fontFamily = Manrope, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SubText, modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = SubText, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryTeal,
                                    unfocusedBorderColor = LineBorder,
                                    focusedContainerColor = CardNew,
                                    unfocusedContainerColor = CardNew,
                                    focusedTextColor = InkText,
                                    unfocusedTextColor = InkText,
                                    focusedPlaceholderColor = SubText,
                                    unfocusedPlaceholderColor = SubText
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            // Pill tab row
                            ScrollableTabRow(
                                selectedTabIndex = listTab,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = Color.Transparent,
                                contentColor = PrimaryTeal,
                                edgePadding = 20.dp,
                                divider = {},
                                indicator = {}
                            ) {
                                val tabs = listOf("Takip Listem", "Popüler", "Tüm Hisseler", "Sepetler")
                                tabs.forEachIndexed { index, label ->
                                    val isSelected = listTab == index
                                    Tab(
                                        selected = isSelected,
                                        onClick = { listTab = index },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(bottom = 10.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(if (isSelected) PrimaryTeal else CardNew)
                                                .border(1.dp, if (isSelected) PrimaryTeal else LineBorder, RoundedCornerShape(20.dp))
                                                .padding(horizontal = 16.dp, vertical = 7.dp)
                                        ) {
                                            Text(
                                                label,
                                                color = if (isSelected) Color.White else SubText,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontFamily = Manrope,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Arama aktifken her zaman sonuçları göster
                    if (searchQuery.isNotEmpty()) {
                        val searchResults = companies.filter {
                            it.symbol.contains(searchQuery, ignoreCase = true) || it.name.contains(searchQuery, ignoreCase = true)
                        }.take(8)
                        items(searchResults, key = { "search_res_${it.symbol}" }) { company ->
                            QuickSearchItem(
                                company = company,
                                onStockClick = { onStockClick(company.symbol, company.market) },
                                onQuickAdd = { viewModel.addToWatchlist(company.symbol) }
                            )
                        }
                    } else when (listTab) {
                        // ── TAB 0: TAKİP LİSTEM ──
                        0 -> {
                            if (watchlist.isEmpty()) {
                                item(key = "watchlist_empty") {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("👁", fontSize = 32.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Takip listeniz boş.", fontFamily = Manrope, fontSize = 14.sp, color = SubText)
                                            Text("+ butonuna basarak hisse ekleyebilirsiniz.", fontFamily = Manrope, fontSize = 11.sp, color = SubText)
                                        }
                                    }
                                }
                            } else {
                                items(watchlist, key = { "wl_${it.symbol}" }) { item ->
                                    val company = companies.find { it.symbol == item.symbol }
                                    val price = prices[item.symbol]?.price ?: company?.currentPrice ?: 0.0
                                    val change = prices[item.symbol]?.changePercent ?: company?.changePercent ?: 0.0
                                    
                                    val dismissState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { dismissValue ->
                                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                                viewModel.removeFromWatchlist(item)
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    )
                                    
                                    SwipeToDismissBox(
                                        state = dismissState,
                                        backgroundContent = {
                                            val bgColor = when (dismissState.dismissDirection) {
                                                SwipeToDismissBoxValue.EndToStart -> NegatifRed
                                                else -> Color.Transparent
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(bgColor),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Kaldır",
                                                        tint = Color.White,
                                                        modifier = Modifier.padding(end = 16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        enableDismissFromStartToEnd = false
                                    ) {
                                        HisseKarti(
                                            symbol = item.symbol,
                                            name = company?.name ?: "Şirket Adı",
                                            price = price,
                                            change = change,
                                            market = company?.market ?: "BIST",
                                            numberFormat = numberFormat,
                                            logoUrl = company?.logoUrl,
                                            initials = company?.logoInitials ?: item.symbol.take(3),
                                            onClick = { onStockClick(item.symbol, company?.market ?: "BIST") }
                                        )
                                    }
                                }
                            }
                        }
                        // ── TAB 1: POPÜLER ──
                        1 -> {
                            val popularBistSymbols = listOf("GARAN", "AKBNK", "BIMAS", "THYAO", "EREGL", "TUPRS", "ASELS", "KCHOL", "SISE", "PGSUS", "KOZAL", "FROTO", "ISCTR", "HEKTS", "TTKOM")
                            items(popularBistSymbols, key = { "popular_$it" }) { symbol ->
                                val company = companies.find { it.symbol == symbol }
                                val price = prices[symbol]?.price ?: company?.currentPrice ?: 0.0
                                val change = prices[symbol]?.changePercent ?: company?.changePercent ?: 0.0
                                HisseKarti(
                                    symbol = symbol,
                                    name = company?.name ?: symbol,
                                    price = price,
                                    change = change,
                                    market = "BIST",
                                    numberFormat = numberFormat,
                                    logoUrl = company?.logoUrl,
                                    initials = company?.logoInitials ?: symbol.take(3),
                                    onClick = { onStockClick(symbol, "BIST") }
                                )
                            }
                        }
                        // ── TAB 2: TÜM HİSSELER ──
                        2 -> {
                            item(key = "all_stocks_filters") {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Tümü", "BIST", "Amerika", "Avrupa").forEach { marketOption ->
                                            val isSelected = selectedMarketFilter == marketOption
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { selectedMarketFilter = marketOption },
                                                label = { Text(marketOption, fontFamily = Manrope, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = TealSoft,
                                                    selectedLabelColor = PrimaryTeal,
                                                    containerColor = CardNew,
                                                    labelColor = SubText
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = isSelected,
                                                    selectedBorderColor = PrimaryTeal,
                                                    borderColor = LineBorder
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = dividendOnly,
                                            onClick = { dividendOnly = !dividendOnly },
                                            label = { Text("Temettü 💰", fontFamily = Manrope, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealSoft, selectedLabelColor = PrimaryTeal, containerColor = CardNew, labelColor = SubText),
                                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = dividendOnly, selectedBorderColor = PrimaryTeal, borderColor = LineBorder),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        FilterChip(
                                            selected = lowPeOnly,
                                            onClick = { lowPeOnly = !lowPeOnly },
                                            label = { Text("Düşük F/K 📉", fontFamily = Manrope, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TealSoft, selectedLabelColor = PrimaryTeal, containerColor = CardNew, labelColor = SubText),
                                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = lowPeOnly, selectedBorderColor = PrimaryTeal, borderColor = LineBorder),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                    }
                                }
                            }
                            item(key = "spacer_all_filters") { Spacer(modifier = Modifier.height(8.dp)) }
                            val filteredCompanies = companies.filter { company ->
                                val matchesMarket = when (selectedMarketFilter) {
                                    "BIST" -> company.market == "BIST"
                                    "Amerika" -> company.market == "NASDAQ" || company.market == "NYSE"
                                    "Avrupa" -> company.market == "FRA" || company.market == "EURONEXT"
                                    else -> true
                                }
                                val info = cachedInfoMap[company.symbol]
                                val matchesDividend = !dividendOnly || (info?.dividendYield != null && info.dividendYield > 0.0)
                                val matchesLowPe = !lowPeOnly || (info?.peRatio != null && info.peRatio < 15.0)
                                matchesMarket && matchesDividend && matchesLowPe
                            }
                            if (filteredCompanies.isEmpty()) {
                                item(key = "filtered_empty") {
                                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                        Text("Kriterlere uygun hisse bulunamadı.", fontFamily = Manrope, fontSize = 14.sp, color = SubText)
                                    }
                                }
                            } else {
                                items(filteredCompanies, key = { "filtered_${it.symbol}" }) { company ->
                                    val price = prices[company.symbol]?.price ?: company.currentPrice
                                    val change = prices[company.symbol]?.changePercent ?: company.changePercent
                                    val info = cachedInfoMap[company.symbol]
                                    HisseKarti(
                                        symbol = company.symbol,
                                        name = company.name,
                                        price = price,
                                        change = change,
                                        market = company.market,
                                        numberFormat = numberFormat,
                                        logoUrl = company.logoUrl,
                                        initials = company.logoInitials ?: company.symbol.take(3),
                                        onClick = { onStockClick(company.symbol, company.market) }
                                    )
                                }
                            }
                        }
                        // ── TAB 3: SEPETLER ──
                        3 -> {
                            if (baskets.isEmpty()) {
                                item(key = "baskets_empty") {
                                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🗂️", fontSize = 32.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Henüz sepetiniz yok.", fontFamily = Manrope, fontSize = 14.sp, color = SubText)
                                        }
                                    }
                                }
                            } else {
                                items(baskets, key = { "basket_tab_${it.id}" }) { basket ->
                                    BasketCard(basket, onClick = { onBasketClick(basket.id) })
                                }
                            }
                        }
                    }
                    item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

        if (showSearchDialog) {
            StockSearchDialog(
                viewModel = viewModel,
                onDismiss = { showSearchDialog = false }
            )
        }

        if (showHealthCheckSheet) {
            ModalBottomSheet(
                onDismissRequest = { showHealthCheckSheet = false },
                containerColor = CardNew,
                dragHandle = { BottomSheetDefaults.DragHandle(color = LineBorder) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "🩺 Portföy Sağlık Check-up Raporu",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isHealthChecking) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryTeal)
                        }
                    } else {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(scrollState)
                        ) {
                            dev.jeziellago.compose.markdowntext.MarkdownText(
                                markdown = healthCheckResult,
                                style = androidx.compose.ui.text.TextStyle(
                                    color = InkText,
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    lineHeight = 20.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showHealthCheckSheet = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }
            }
        }

        if (showRebalanceSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRebalanceSheet = false },
                containerColor = CardNew,
                dragHandle = { BottomSheetDefaults.DragHandle(color = LineBorder) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "⚖️ Orakul Portföy Rebalans Raporu",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isRebalancing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Orange)
                        }
                    } else {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(scrollState)
                        ) {
                            dev.jeziellago.compose.markdowntext.MarkdownText(
                                markdown = rebalanceResult,
                                modifier = Modifier.fillMaxWidth(),
                                style = androidx.compose.ui.text.TextStyle(
                                    color = InkText,
                                    fontFamily = Manrope,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showRebalanceSheet = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovingTickerBar(
    tickerData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val displayData = remember(tickerData) {
        if (tickerData.isNotEmpty()) tickerData else {
            listOf(
                "EURTRY" to 36.42 + kotlin.random.Random.nextDouble(-0.1, 0.1), 
                "USDTRY" to 34.15 + kotlin.random.Random.nextDouble(-0.1, 0.1), 
                "XU100" to 10450.0 + kotlin.random.Random.nextDouble(-10.0, 10.0), 
                "ONS" to 2645.20 + kotlin.random.Random.nextDouble(-5.0, 5.0),
                "BTCUSD" to 94250.0 + kotlin.random.Random.nextDouble(-100.0, 100.0),
                "ETHUSD" to 3450.0 + kotlin.random.Random.nextDouble(-10.0, 10.0),
                "GAU" to 3045.0 + kotlin.random.Random.nextDouble(-5.0, 5.0),
                "AAPL" to 228.40 + kotlin.random.Random.nextDouble(-1.0, 1.0),
                "NVDA" to 142.10 + kotlin.random.Random.nextDouble(-1.0, 1.0)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryTeal, AquaNew)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    velocity = 45.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(8) {
                displayData.forEach { (name, price) ->
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            String.format(Locale.US, "%,.2f", price),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = IBMPlexMono
                        )
                    }
                }
            }
        }

        // Left fade mask
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(24.dp)
                .background(Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.8f), Color.Transparent)))
        )

        // Right fade mask
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(24.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, AquaNew.copy(alpha = 0.8f))))
        )
    }
}

@Composable
fun TopBar(onLedgerClick: () -> Unit, onProfileClick: () -> Unit, alertCount: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Porsuk Finans",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                color = InkText
            )
            Text(
                "Hoş geldin 👋",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Alarm rozeti
            if (alertCount > 0) {
                Box {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Alarmlar", tint = PrimaryTeal)
                    }
                    // Kırmızı badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NegatifRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (alertCount > 9) "9+" else "$alertCount",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Manrope
                        )
                    }
                }
            }

            IconButton(
                onClick = onLedgerClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AquaSoft)
                    .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.History, contentDescription = "İşlem Defteri", tint = PrimaryTeal)
            }
            
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AquaSoft)
                    .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profil", tint = PrimaryTeal)
            }
        }
    }
}

@Composable
fun BalancedTotalAssetCard(
    totalValueTry: String,
    totalValueUsd: String,
    totalValueEur: String,
    percentageChange: String,
    cardTitle: String = "TOPLAM VARLIK",
    profitMode: FinanceViewModel.ProfitCalculationMode,
    onProfitModeChange: (FinanceViewModel.ProfitCalculationMode) -> Unit
) {
    val isPositive = !percentageChange.contains("-")
    val color = if (isPositive) Color(0xFF7CFFC4) else Color(0xFFE15577)
    val bgColor = if (isPositive) Color(0xFF7CFFC4).copy(alpha = 0.12f) else Color(0xFFE15577).copy(alpha = 0.12f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF7C6CF0).copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF191033),
                            Color(0xFF241454),
                            Color(0xFF1B0F3D)
                        )
                    )
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .align(Alignment.BottomCenter)
                    .alpha(0.08f)
            ) {
                val width = size.width
                val height = size.height
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0f, height * 0.75f)
                path.cubicTo(width * 0.25f, height * 0.9f, width * 0.5f, height * 0.4f, width * 0.75f, height * 0.55f)
                path.lineTo(width, height * 0.2f)
                drawPath(
                    path = path,
                    color = Color(0xFF7CFFC4),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            cardTitle,
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.2.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val dynamicFontSize = when {
                            totalValueTry.length > 13 -> 18.sp
                            totalValueTry.length > 10 -> 22.sp
                            totalValueTry.length > 8  -> 25.sp
                            else                      -> 28.sp
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "₺",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = (dynamicFontSize.value * 0.75f).sp),
                                color = Color(0xFF7CFFC4),
                                modifier = Modifier.padding(bottom = 3.dp, end = 3.dp)
                            )
                            Text(
                                totalValueTry,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = dynamicFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = IBMPlexMono
                                ),
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    
                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                "", 
                                tint = color, 
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                percentageChange,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = IBMPlexMono,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = color,
                                maxLines = 1
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(18.dp))
                
                // Profit Mode Selector (Getiri Hesabı)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        FinanceViewModel.ProfitCalculationMode.NOMINAL to "Nominal",
                        FinanceViewModel.ProfitCalculationMode.INFLATION_ADJUSTED to "Reel Enflasyon",
                        FinanceViewModel.ProfitCalculationMode.USD_ADJUSTED to "Dolar Bazlı"
                    ).forEach { (mode, label) ->
                        val isSelected = profitMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onProfitModeChange(mode) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = Manrope
                                ),
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text("USD Karşılığı", style = MaterialTheme.typography.labelSmall.copy(fontFamily = Manrope), color = Color(0xFF94A3B8))
                        Text(
                            "$totalValueUsd $", 
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = IBMPlexMono
                            ),
                            color = Color.White
                        )
                    }
                    Column {
                        Text("EUR Karşılığı", style = MaterialTheme.typography.labelSmall.copy(fontFamily = Manrope), color = Color(0xFF94A3B8))
                        Text(
                            "$totalValueEur €", 
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = IBMPlexMono
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyEquivalent(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Manrope), color = SubText)
        Text(
            value, 
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = IBMPlexMono
            ),
            color = InkText
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
        color = InkText,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun HighlightChip(symbol: String, change: Double, numberFormat: String = "TR", onClick: () -> Unit) {
    val color = if (change >= 0) PrimaryTeal else NegatifRed
    val bgColor = if (change >= 0) TealSoft else RedSoft
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(symbol, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkText, fontFamily = Manrope)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                NumberFormatter.formatPercentage(change, numberFormat),
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = IBMPlexMono
            )
        }
    }
}

@Composable
fun BasketCard(basket: com.nexus.porsuk.data.local.entity.Basket, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AquaSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = PrimaryTeal)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(basket.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = InkText)
                Text(basket.market, style = MaterialTheme.typography.bodyMedium, color = SubText, fontFamily = Manrope)
            }
        }
    }
}

@Composable
fun HisseKarti(
    symbol: String, 
    name: String, 
    price: Double, 
    change: Double, 
    market: String,
    numberFormat: String = "TR",
    logoUrl: String? = null,
    initials: String = "",
    onClick: () -> Unit
) {
    val color = if (change >= 0) PrimaryTeal else NegatifRed
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.nexus.porsuk.ui.common.StockLogoBadge(
                logoUrl = logoUrl,
                initials = initials,
                sectorColor = com.nexus.porsuk.ui.common.getSectorColor(symbol),
                modifier = Modifier.size(36.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    symbol, 
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope,
                        color = InkText
                    )
                )
                Text(
                    name, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = SubText,
                    fontFamily = Manrope,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Directional Sparkline (Trends based on change value)
            val mockSparkData = remember(symbol, change) {
                val list = mutableListOf<Float>()
                var current = 50f
                list.add(current)
                val step = (change.toFloat() / 15f) * 10f
                for (i in 1..14) {
                    current += step + kotlin.random.Random.nextFloat() * 10f - 5f
                    list.add(current.coerceIn(10f, 90f))
                }
                list
            }
            Sparkline(
                values = mockSparkData,
                color = color,
                modifier = Modifier
                    .size(60.dp, 28.dp)
                    .padding(horizontal = 8.dp),
                filled = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    CurrencyFormatter.formatWithSymbol(price, CurrencyFormatter.getCurrencySymbol(market), numberFormat),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = IBMPlexMono,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                )
                Text(
                    NumberFormatter.formatPercentage(change, numberFormat),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = IBMPlexMono,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = color
                )
            }
        }
    }
}

@Composable
fun BistHighlightCard(symbol: String, price: Double, change: Double, numberFormat: String = "TR", onClick: () -> Unit) {
    val color = if (change >= 0) PrimaryTeal else NegatifRed
    Card(
        modifier = Modifier
            .width(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(symbol, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = Manrope, color = InkText)
            
            // Directional Sparkline (Trends based on change value)
            val mockData = remember(symbol, change) {
                val list = mutableListOf<Float>()
                var current = 50f
                list.add(current)
                val step = (change.toFloat() / 10f) * 8f
                for (i in 1..9) {
                    current += step + kotlin.random.Random.nextFloat() * 8f - 4f
                    list.add(current.coerceIn(10f, 90f))
                }
                list
            }
            Sparkline(
                values = mockData,
                color = color,
                modifier = Modifier.fillMaxWidth().height(26.dp).padding(vertical = 4.dp),
                filled = true
            )
            
            Text(
                CurrencyFormatter.formatTRY(price, numberFormat),
                fontSize = 12.sp,
                fontFamily = IBMPlexMono,
                fontWeight = FontWeight.Bold,
                color = InkText
            )
            Text(
                NumberFormatter.formatPercentage(change, numberFormat),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = IBMPlexMono
            )
        }
    }
}

@Composable
private fun QuickSearchItem(
    company: com.nexus.porsuk.data.local.entity.Company,
    onStockClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStockClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.nexus.porsuk.ui.common.StockLogoBadge(
            logoUrl = company.logoUrl,
            initials = company.logoInitials ?: company.symbol.take(3),
            sectorColor = com.nexus.porsuk.ui.common.getSectorColor(company.symbol),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(company.symbol, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = InkText)
            Text(company.name, style = MaterialTheme.typography.bodySmall, color = SubText, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onQuickAdd) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = "Ekle", tint = PrimaryTeal)
        }
    }
}

@Composable
fun StockSearchDialog(viewModel: FinanceViewModel, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("IST") }
    val results by viewModel.searchResults.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNew,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Yeni Hisse Takip Et", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = InkText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it.uppercase() },
                    placeholder = { Text("Sembol Ara (Örn: THYAO)", fontFamily = Manrope) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedLabelColor = PrimaryTeal,
                        unfocusedLabelColor = SubText,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Custom market selector pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LineBorder)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("IST" to "BIST", "NASDAQ" to "NASDAQ", "FRA" to "Avrupa").forEach { (mKey, mLabel) ->
                        val isSelected = market == mKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryTeal else Color.Transparent)
                                .clickable { market = mKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                mLabel,
                                color = if (isSelected) Color.White else SubText,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Button(
                    onClick = { viewModel.searchStock(query, market) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ara", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(results) { stock ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    viewModel.addToWatchlist(stock.symbol)
                                    onDismiss()
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stock.symbol, fontWeight = FontWeight.Bold, fontFamily = Manrope, color = InkText)
                            val currency = when (market) {
                                "NASDAQ" -> "$"
                                "FRA" -> "€"
                                else -> "₺"
                            }
                            Text("$currency${stock.price}", fontFamily = IBMPlexMono, color = InkText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Kapat", color = SubText, fontFamily = Manrope, fontWeight = FontWeight.SemiBold) }
        }
    )
}

@Composable
fun QuickActionsGrid(
    onLedgerClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onModelSepetlerClick: () -> Unit,
    onKapRadarClick: () -> Unit = {}
) {
    val actions = listOf(
        QuickActionItem(
            emoji = "📋",
            title = "İşlem Defterim",
            subtitle = "Alım / satım geçmişi",
            gradientStart = PrimaryTeal,
            gradientEnd = Color(0xFF017A63),
            accentSoft = TealSoft,
            onClick = onLedgerClick
        ),
        QuickActionItem(
            emoji = "📅",
            title = "Takvim & Temettü",
            subtitle = "Gelişmeler & tarihler",
            gradientStart = AquaNew,
            gradientEnd = Color(0xFF1897B4),
            accentSoft = AquaSoft,
            onClick = onCalendarClick
        ),
        QuickActionItem(
            emoji = "📈",
            title = "Hisse Eleği & Risk",
            subtitle = "Filtrele & analiz et",
            gradientStart = Violet,
            gradientEnd = Color(0xFF5C4AD8),
            accentSoft = VioletSoft,
            onClick = onAnalysisClick
        ),
        QuickActionItem(
            emoji = "🎯",
            title = "Model Sepetler",
            subtitle = "Hazır AI sepetleri",
            gradientStart = Gold,
            gradientEnd = Color(0xFFC8891E),
            accentSoft = GoldSoft,
            onClick = onModelSepetlerClick
        ),
        QuickActionItem(
            emoji = "📢",
            title = "KAP Akıllı Para",
            subtitle = "Geri Alım & Patron",
            gradientStart = EmeraldNew,
            gradientEnd = Color(0xFF007A58),
            accentSoft = AquaSoft,
            onClick = onKapRadarClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PremiumQuickActionCard(item = actions[0], modifier = Modifier.weight(1f))
            PremiumQuickActionCard(item = actions[1], modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PremiumQuickActionCard(item = actions[2], modifier = Modifier.weight(1f))
            PremiumQuickActionCard(item = actions[3], modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PremiumQuickActionCard(item = actions[4], modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

data class QuickActionItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val accentSoft: Color,
    val onClick: () -> Unit
)

@Composable
fun PremiumQuickActionCard(
    item: QuickActionItem,
    modifier: Modifier = Modifier
) {
    var isCardPressed by remember { mutableStateOf(false) }
    val cardScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isCardPressed) 0.95f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessHigh
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(82.dp)
            .scale(cardScale)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                isCardPressed = true
                item.onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                item.gradientStart.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(item.gradientStart, item.gradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        item.title,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkText,
                        fontFamily = Manrope,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        item.subtitle,
                        fontSize = 9.5.sp,
                        color = SubText,
                        fontFamily = Manrope,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = item.gradientStart.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }

    LaunchedEffect(isCardPressed) {
        if (isCardPressed) {
            kotlinx.coroutines.delay(120)
            isCardPressed = false
        }
    }
}

// Legacy QuickActionButton kept for backward compatibility
@Composable
fun QuickActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(68.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AquaSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                Text(subtitle, fontSize = 9.sp, color = SubText, fontFamily = Manrope)
            }
        }
    }
}
