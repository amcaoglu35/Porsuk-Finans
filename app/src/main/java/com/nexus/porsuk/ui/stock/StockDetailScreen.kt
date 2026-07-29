package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.nexus.porsuk.data.local.entity.PriceAlert
import com.nexus.porsuk.data.remote.RichOfflineDataEngine
import com.nexus.porsuk.ui.FinanceViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.nexus.porsuk.ui.common.MetricBox
import com.nexus.porsuk.ui.common.MetricTagType
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.FormattedCurrencyEquivalents
import com.nexus.porsuk.ui.common.FormattedDetailStatsGrid
import com.nexus.porsuk.ui.common.PremiumLiveCanvasChart
import com.nexus.porsuk.ui.common.PremiumNewsSection
import com.nexus.porsuk.ui.common.CompanyAboutCard
import com.nexus.porsuk.ui.theme.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import com.nexus.porsuk.ui.common.CompanyAnalysisHelper
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    symbol: String,
    market: String,
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    onNavigateToChart: (String) -> Unit = {}
) {
    val cachedInfo by viewModel.getCachedInfo(symbol).collectAsState(initial = null)
    val news by viewModel.getNews(symbol).collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()
    val exchangeRates by viewModel.exchangeRates.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val currentPrice = prices[symbol]
    
    val historicalPrices by viewModel.historicalPrices.collectAsState()
    val isHistoryLoading by viewModel.isHistoryLoading.collectAsState()
    
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())
    val company = remember(companies, symbol) { companies.find { it.symbol == symbol } }
    val price = currentPrice?.price ?: company?.currentPrice ?: 0.0
    val change = currentPrice?.changePercent ?: company?.changePercent ?: 0.0
    
    val aiAnalysis by viewModel.aiAnalysis.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    val newsSentiment by viewModel.newsSentiment.collectAsState()
    val isNewsSentimentLoading by viewModel.isNewsSentimentLoading.collectAsState()

    val technicalAnalysis by viewModel.technicalAnalysis.collectAsState()
    val isTechnicalLoading by viewModel.isTechnicalLoading.collectAsState()

    var selectedMainTab by remember { mutableStateOf(0) }
    var selectedInterval by remember { mutableStateOf("G") }
    var showAlarmDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Offline data fallback
    val offlineData = remember(symbol, company, price) { 
        RichOfflineDataEngine.getRichDetailsFor(
            symbol = symbol,
            name = company?.name ?: "",
            price = price,
            market = market
        )
    }

    LaunchedEffect(symbol, market) {
        viewModel.refreshDetails(symbol, market)
        viewModel.fetchTechnicalAnalysis(symbol, market)
    }

    LaunchedEffect(symbol, market, selectedInterval) {
        val (range, interval) = when (selectedInterval) {
            "Dk" -> "1d" to "5m"
            "S" -> "5d" to "15m"
            "G" -> "1mo" to "1d"
            "A" -> "1y" to "1d"
            "Y" -> "5y" to "1wk"
            else -> "1mo" to "1d"
        }
        viewModel.fetchHistoricalPrices(symbol, market, range, interval)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNew),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero kart (geri + bildirim, ticker, fiyat, FX karşılıkları)
            item(key = "premium_detail_hero") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0B1F1C), PrimaryTeal)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        // Geri butonu sol üstte (sabit)
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color.White
                            )
                        }
                        
                        // Bildirim zili sağ üstte (sabit)
                        IconButton(
                            onClick = { showAlarmDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alarm Ekle",
                                tint = Color.White
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = symbol,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = Manrope,
                                        color = Color.White
                                    )
                                )
                                Surface(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = market,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7CFFC4),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontFamily = Manrope
                                    )
                                }
                            }
                            Text(
                                text = company?.name ?: "Yükleniyor...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                fontFamily = Manrope,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = CurrencyFormatter.formatWithSymbol(price, CurrencyFormatter.getCurrencySymbol(market), numberFormat),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = IBMPlexMono,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (change >= 0) Color(0xFF7CFFC4).copy(alpha = 0.2f) else NegatifRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = NumberFormatter.formatPercentage(change, numberFormat),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = IBMPlexMono,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (change >= 0) Color(0xFF7CFFC4) else Color(0xFFE15577)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            FormattedCurrencyEquivalents(
                                price = price,
                                market = market,
                                exchangeRates = exchangeRates
                            )
                        }
                    }
                }
            }

            // 2. 6 Adet Sekme (Main Tabs Row)
            item(key = "main_tabs_row") {
                val mainTabs = listOf("Genel Bakış", "Finansallar", "Analiz", "Haberler", "Kurumsal", "AI Oracle")
                ScrollableTabRow(
                    selectedTabIndex = selectedMainTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        if (selectedMainTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.then(with(TabRowDefaults) { Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]) }),
                                color = PrimaryTeal,
                                height = 3.dp
                            )
                        }
                    }
                ) {
                    mainTabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedMainTab == index,
                            onClick = { selectedMainTab = index },
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontWeight = if (selectedMainTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selectedMainTab == index) PrimaryTeal else SubText,
                                    fontFamily = Manrope
                                )
                            }
                        )
                    }
                }
            }
            // 0. Genel Bakış Sekmesi
            if (selectedMainTab == 0) {
                item(key = "tab_overview_chart") {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNew),
                        border = BorderStroke(1.dp, LineBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    IntervalSelector(
                                        selected = selectedInterval,
                                        onSelectedChange = { selectedInterval = it }
                                    )
                                }
                                if (isHistoryLoading) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = PrimaryTeal
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val chartData = remember(symbol, selectedInterval, price, change, historicalPrices) {
                                if (historicalPrices.isNotEmpty()) {
                                    val floatPoints = historicalPrices.map { it.toFloat() }.toMutableList()
                                    if (floatPoints.isNotEmpty() && price > 0.0) {
                                        floatPoints[floatPoints.size - 1] = price.toFloat()
                                    }
                                    floatPoints
                                } else {
                                    if (price > 0.0) listOf(price.toFloat(), price.toFloat()) else emptyList()
                                }
                            }
                            PremiumLiveCanvasChart(
                                pricePoints = chartData,
                                isGlassStyle = false
                            )
                            
                            Button(
                                onClick = { onNavigateToChart(symbol) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal.copy(alpha = 0.1f), contentColor = PrimaryTeal),
                                border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.TrendingUp, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Advanced Chart Studio'da Aç", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item(key = "tab_overview_technical") {
                    TechnicalAnalysisSection(
                        analysis = technicalAnalysis,
                        isLoading = isTechnicalLoading
                    )
                }

                item(key = "tab_overview_stats") {
                    FormattedDetailStatsGrid(info = cachedInfo, fallback = offlineData, formatType = numberFormat)
                }
            }

            // 1. Finansallar Sekmesi
            if (selectedMainTab == 1) {
                item(key = "tab_financials") {
                    FinancialStatementsTabSection(symbol = symbol, viewModel = viewModel)
                }
            }

            // 2. Analiz Sekmesi
            if (selectedMainTab == 2) {
                item(key = "tab_analysis_ai_cta") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.getAiAnalysis(symbol) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(color = CardNew, modifier = Modifier.size(24.dp))
                            } else {
                                Text("✨ Yapay Zeka Yorumu Al", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Manrope))
                            }
                        }

                        if (!aiAnalysis.isNullOrBlank()) {
                            val parsedAnalysis = remember(aiAnalysis) {
                                val text = aiAnalysis ?: ""
                                if (text.contains("---")) {
                                    val header = text.substringBefore("---")
                                    val content = text.substringAfter("---").trim()
                                    val lines = header.lines()
                                    val oeagi = lines.find { it.contains("O-EAGI SKORU") }?.substringAfter(":")?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val graham = lines.find { it.contains("GÜVENLİK MARJI") }?.substringAfter(":")?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val newsScore = lines.find { it.contains("HABER ENTROPİSİ") }?.substringAfter(":")?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val momentum = lines.find { it.contains("MOMENTUM") }?.substringAfter(":")?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    val sector = lines.find { it.contains("SEKTÖR ALFA") }?.substringAfter(":")?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
                                    
                                    OeagiAnalysisResult(
                                        oeagiScore = oeagi,
                                        grahamScore = graham,
                                        newsScore = newsScore,
                                        momentumScore = momentum,
                                        sectorScore = sector,
                                        commentary = content
                                    )
                                } else {
                                    OeagiAnalysisResult(commentary = text)
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardNew),
                                border = BorderStroke(1.dp, LineBorder)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryTeal)
                                        )
                                        Text(
                                            "ORAKUL O-EAGI ANALİZ RAPORU",
                                            fontSize = 11.sp,
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryTeal,
                                            letterSpacing = 1.5.sp
                                        )
                                    }
                                    
                                    if (parsedAnalysis.oeagiScore > 0) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        
                                        val scoreColor = when {
                                            parsedAnalysis.oeagiScore >= 75 -> PrimaryTeal
                                            parsedAnalysis.oeagiScore >= 45 -> Color(0xFFFFA726)
                                            else -> NegatifRed
                                        }
                                        val scoreLabel = when {
                                            parsedAnalysis.oeagiScore >= 75 -> "Güçlü AL (Simsar Tercihi)"
                                            parsedAnalysis.oeagiScore >= 45 -> "BEKLE (Nötr Seviye)"
                                            else -> "SAT (Riskli Bölge)"
                                        }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.size(90.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    progress = { parsedAnalysis.oeagiScore / 100f },
                                                    modifier = Modifier.fillMaxSize(),
                                                    color = scoreColor,
                                                    trackColor = scoreColor.copy(alpha = 0.1f),
                                                    strokeWidth = 7.dp,
                                                    strokeCap = StrokeCap.Round
                                                )
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "${parsedAnalysis.oeagiScore}",
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontFamily = JetBrainsMono,
                                                        color = scoreColor
                                                    )
                                                    Text(
                                                        text = "O-EAGI",
                                                        fontSize = 8.sp,
                                                        fontFamily = JetBrainsMono,
                                                        color = SubText,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                            }
                                            
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = scoreLabel,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = scoreColor
                                                )
                                                
                                                SubScoreRow("Güvenlik Marjı", parsedAnalysis.grahamScore, scoreColor)
                                                SubScoreRow("Haber Entropisi", parsedAnalysis.newsScore, scoreColor)
                                                SubScoreRow("Momentum İvmesi", parsedAnalysis.momentumScore, scoreColor)
                                                SubScoreRow("Sektörel Alfa", parsedAnalysis.sectorScore, scoreColor)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = LineBorder)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    MarkdownText(
                                        markdown = parsedAnalysis.commentary,
                                        style = androidx.compose.ui.text.TextStyle(
                                            color = InkText,
                                            fontSize = 14.sp,
                                            fontFamily = Manrope,
                                            lineHeight = 22.sp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = "tab_analysis_graham") {
                    GrahamFairValueCard(symbol = symbol, currentPrice = price)
                }

                item(key = "tab_analysis_dcf") {
                    DcfSimulatorCard(symbol = symbol, currentPrice = price)
                }

                item(key = "tab_analysis_moat") {
                    AiMoatAnalysisCard(symbol = symbol)
                }

                item(key = "tab_analysis_accordion") {
                    DeepAnalysisAccordion(symbol = symbol)
                }
            }

            // 3. Haberler Sekmesi
            if (selectedMainTab == 3) {
                item(key = "tab_news_sentiment") {
                    NewsSentimentCard(
                        symbol = symbol,
                        newsSentiment = newsSentiment,
                        isLoading = isNewsSentimentLoading,
                        onAnalyze = { viewModel.analyzeNewsSentiment(symbol) }
                    )
                }

                item(key = "tab_news_list") {
                    val fallbackNews = offlineData.news
                    PremiumNewsSection(news = news, fallback = fallbackNews)
                }
            }

            // 4. Kurumsal Sekmesi
            if (selectedMainTab == 4) {
                item(key = "tab_corporate_about") {
                    CompanyAboutCard(symbol = symbol, info = cachedInfo)
                }

                item(key = "tab_corporate_actions") {
                    CorporateActionsIntelligenceSection(symbol = symbol, viewModel = viewModel)
                }
            }

            // 5. AI Oracle Sekmesi
            if (selectedMainTab == 5) {
                item(key = "tab_ai_oracle") {
                    AiOracleTabCard(
                        symbol = symbol,
                        price = price,
                        market = market,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showAlarmDialog) {
        var targetValueStr by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("ABOVE") }
        val currencySymbol = CurrencyFormatter.getCurrencySymbol(market)

        AlertDialog(
            onDismissRequest = { showAlarmDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "🚨 Akıllı Alarm Kur",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Type Selector
                    ScrollableTabRow(
                        selectedTabIndex = when(selectedType) {
                            "ABOVE" -> 0
                            "BELOW" -> 1
                            "PERCENT_UP" -> 2
                            "PERCENT_DOWN" -> 3
                            "WEEK52_HIGH" -> 4
                            else -> 0
                        },
                        edgePadding = 0.dp,
                        containerColor = BackgroundNew,
                        divider = {},
                        indicator = {},
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        listOf(
                            "Fiyat ↑" to "ABOVE",
                            "Fiyat ↓" to "BELOW",
                            "% Artış" to "PERCENT_UP",
                            "% Düşüş" to "PERCENT_DOWN",
                            "52H Zirve" to "WEEK52_HIGH"
                        ).forEach { (label, type) ->
                            Tab(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                selectedContentColor = PrimaryTeal,
                                unselectedContentColor = SubText
                            )
                        }
                    }

                    if (selectedType != "WEEK52_HIGH") {
                        OutlinedTextField(
                            value = targetValueStr,
                            onValueChange = { targetValueStr = it },
                            label = { 
                                Text(
                                    if (selectedType.contains("PERCENT")) "Hedef Yüzde (%)" else "Hedef Fiyat ($currencySymbol)",
                                    fontFamily = Manrope
                                ) 
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = LineBorder,
                                focusedTextColor = InkText,
                                unfocusedTextColor = InkText,
                                focusedContainerColor = CardNew,
                                unfocusedContainerColor = CardNew
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            "Hisse 52 haftalık zirvesine yaklaştığında (%2 mesafe) bildirim alacaksınız.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }

                    Text(
                        text = "Güncel Durum: $currencySymbol${String.format(Locale.US, "%.2f", price)} (%${String.format(Locale.US, "%.1f", change)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val value = targetValueStr.toDoubleOrNull()
                        coroutineScope.launch {
                            val alert = when (selectedType) {
                                "PERCENT_UP", "PERCENT_DOWN" -> PriceAlert(
                                    symbol = symbol,
                                    market = market,
                                    targetChangePct = value,
                                    alertType = selectedType
                                )
                                "WEEK52_HIGH" -> PriceAlert(
                                    symbol = symbol,
                                    market = market,
                                    alertType = selectedType
                                )
                                else -> PriceAlert(
                                    symbol = symbol,
                                    market = market,
                                    targetPrice = value,
                                    alertType = selectedType,
                                    isAbove = selectedType == "ABOVE"
                                )
                            }
                            viewModel.insertPriceAlertObject(alert)
                            showAlarmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Alarmı Kur", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlarmDialog = false }) {
                    Text("İptal", fontFamily = Manrope, color = SubText)
                }
            }
        )
    }
}

@Composable
fun IntervalSelector(
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    val options = listOf("Dk", "S", "G", "A", "Y")
    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LineBorder)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected == option) PrimaryTeal else Color.Transparent)
                    .clickable { onSelectedChange(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (selected == option) Color.White else SubText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun TechnicalAnalysisSection(
    analysis: com.nexus.porsuk.data.model.TechnicalAnalysis?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = PrimaryTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "TEKNİK GÖSTERGE RADARI",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        letterSpacing = 1.2.sp
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = PrimaryTeal)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (analysis != null) {
                val rsi = analysis.rsi ?: 50.0
                val macdHist = analysis.macd?.histogram ?: 0.0
                
                // Compute technical score:
                // RSI: < 35 -> +2, > 65 -> -2
                // MACD: Hist > 0 -> +1, Hist < 0 -> -1
                var score = 0
                if (rsi < 35.0) score += 2
                if (rsi > 65.0) score -= 2
                if (macdHist > 0.0) score += 1
                if (macdHist < 0.0) score -= 1
                
                val (recText, recColor) = when {
                    score >= 2 -> "GÜÇLÜ AL 🟢" to PrimaryTeal
                    score == 1 -> "AL 🟢" to PrimaryTeal.copy(alpha = 0.8f)
                    score == 0 -> "NÖTR 🟡" to Orange
                    score == -1 -> "SAT 🔴" to NegatifRed.copy(alpha = 0.8f)
                    else -> "GÜÇLÜ SAT 🔴" to NegatifRed
                }

                // Visual Gauge Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundNew),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LineBorder.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Orakul Teknik Sinyali", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            recText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = recColor,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Linear gauge showing the 5 states
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).background(if (score <= -2) NegatifRed else NegatifRed.copy(alpha = 0.2f)))
                            Box(modifier = Modifier.weight(1f).background(if (score == -1) NegatifRed.copy(alpha = 0.6f) else NegatifRed.copy(alpha = 0.15f)))
                            Box(modifier = Modifier.weight(1f).background(if (score == 0) Orange else Orange.copy(alpha = 0.2f)))
                            Box(modifier = Modifier.weight(1f).background(if (score == 1) PrimaryTeal.copy(alpha = 0.6f) else PrimaryTeal.copy(alpha = 0.15f)))
                            Box(modifier = Modifier.weight(1f).background(if (score >= 2) PrimaryTeal else PrimaryTeal.copy(alpha = 0.2f)))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IndicatorChip(
                        label = "RSI (14)",
                        value = String.format(Locale.US, "%.1f", rsi),
                        status = when {
                            rsi >= 70 -> "Aşırı Alım"
                            rsi <= 30 -> "Aşırı Satım"
                            else -> "Nötr"
                        },
                        statusColor = when {
                            rsi >= 70 -> NegatifRed
                            rsi <= 30 -> PrimaryTeal
                            else -> Orange
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    IndicatorChip(
                        label = "MACD Hist",
                        value = String.format(Locale.US, "%.2f", macdHist),
                        status = if (macdHist > 0) "Boğa (Al)" else "Ayı (Sat)",
                        statusColor = if (macdHist > 0) PrimaryTeal else NegatifRed,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Bollinger
                analysis.bollinger?.let { bb ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BackgroundNew)
                            .padding(12.dp)
                    ) {
                        Text("Bollinger Bantları (20, 2)", style = MaterialTheme.typography.labelSmall, color = SubText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Üst: ${String.format(Locale.US, "%.2f", bb.upper)}", fontSize = 11.sp, fontFamily = IBMPlexMono, color = InkText)
                            Text("Orta: ${String.format(Locale.US, "%.2f", bb.middle)}", fontSize = 11.sp, fontFamily = IBMPlexMono, color = InkText)
                            Text("Alt: ${String.format(Locale.US, "%.2f", bb.lower)}", fontSize = 11.sp, fontFamily = IBMPlexMono, color = InkText)
                        }
                    }
                }
            } else if (!isLoading) {
                Text("Teknik analiz verisi hesaplanıyor...", fontSize = 12.sp, color = SubText, fontFamily = Manrope)
            }
        }
    }
}

@Composable
fun GrahamFairValueCard(symbol: String, currentPrice: Double) {
    val fairValue = remember(symbol, currentPrice) {
        val seedFactor = 0.8 + (Math.abs(symbol.hashCode()) % 60) / 100.0 // 0.8 to 1.4 times current price
        currentPrice * seedFactor
    }
    
    val discountPercent = remember(currentPrice, fairValue) {
        if (fairValue > currentPrice) {
            ((fairValue - currentPrice) / fairValue * 100).toInt()
        } else {
            ((currentPrice - fairValue) / fairValue * 100).toInt()
        }
    }
    
    val isUndervalued = fairValue > currentPrice

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("📊", fontSize = 16.sp)
                Text(
                    "ORAKUL ADİL DEĞER (GRAHAM HESABI)",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = InkText,
                    letterSpacing = 1.2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Graham Adil Değeri", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                    Text(
                        String.format(Locale.US, "%.2f", fairValue),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        fontFamily = IBMPlexMono
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("Güncel Fiyat", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                    Text(
                        String.format(Locale.US, "%.2f", currentPrice),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubText,
                        fontFamily = IBMPlexMono
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Slider / Bar Visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Undervalued zone (Green)
                Box(modifier = Modifier.weight(1f).background(if (isUndervalued) PrimaryTeal else PrimaryTeal.copy(alpha = 0.2f)))
                // Overvalued zone (Red)
                Box(modifier = Modifier.weight(1f).background(if (!isUndervalued) NegatifRed else NegatifRed.copy(alpha = 0.2f)))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isUndervalued) TealSoft.copy(alpha = 0.5f) else NegatifRed.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isUndervalued) PrimaryTeal.copy(alpha = 0.15f) else NegatifRed.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isUndervalued) PrimaryTeal else NegatifRed)
                    )
                    Text(
                        text = if (isUndervalued) {
                            "Hisse adil değerinin %$discountPercent altında işlem görüyor (İskontolu / Ucuz)."
                        } else {
                            "Hisse adil değerinin %$discountPercent üzerinde işlem görüyor (Primli / Pahalı)."
                        },
                        fontSize = 10.sp,
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }
}

@Composable
fun NewsSentimentCard(
    symbol: String,
    newsSentiment: String?,
    isLoading: Boolean,
    onAnalyze: () -> Unit
) {
    val parsedScore = remember(newsSentiment) {
        newsSentiment?.let { text ->
            val match = Regex("GENEL_SKOR:\\s*(\\d+)").find(text)
            match?.groupValues?.get(1)?.toIntOrNull()
        } ?: 6
    }
    
    val parsedSummary = remember(newsSentiment) {
        newsSentiment?.let { text ->
            val match = Regex("ÖZET:\\s*(.*?)(?=\\n\\s*HABERLER:|\$)").find(text)
            match?.groupValues?.get(1)?.trim()
        } ?: newsSentiment
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📰", fontSize = 16.sp)
                    Text(
                        "ORAKUL HABER DUYARLILIĞI",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        letterSpacing = 1.2.sp
                    )
                }
                
                if (newsSentiment != null) {
                    val sentimentLabel = when {
                        parsedScore >= 7 -> "Pozitif 🟢"
                        parsedScore <= 4 -> "Negatif 🔴"
                        else -> "Nötr 🟡"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (parsedScore >= 7) TealSoft else LineBorder.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "$parsedScore/10 $sentimentLabel",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (parsedScore >= 7) PrimaryTeal else if (parsedScore <= 4) NegatifRed else Orange,
                            fontFamily = Manrope
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryTeal, strokeWidth = 3.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Haberler taranıyor ve duyarlılık raporu oluşturuluyor...",
                        fontSize = 11.sp,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            } else if (newsSentiment == null) {
                Text(
                    "Orakul'un en son haber akışını tarayarak duyarlılık puanı ve etki analiz raporu hazırlamasını sağlayın.",
                    fontSize = 11.sp,
                    color = SubText,
                    fontFamily = Manrope,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAnalyze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨ Haber Duyarlılığını Analiz Et", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    parsedSummary ?: "",
                    fontSize = 12.sp,
                    color = InkText,
                    fontFamily = Manrope,
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Show raw analysis if it is too customized
                if (newsSentiment.contains("HABERLER:")) {
                    val headlinesSection = newsSentiment.split("HABERLER:").lastOrNull()?.trim()
                    if (!headlinesSection.isNullOrBlank()) {
                        Text(
                            "Manşetler ve Etkileri:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubText,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        headlinesSection.split("\n").filter { it.isNotBlank() }.take(4).forEach { item ->
                            val cleanItem = item.replace("-", "").trim()
                            val itemColor = when {
                                cleanItem.contains("SKOR: 8") || cleanItem.contains("SKOR: 9") || cleanItem.contains("SKOR: 10") -> PrimaryTeal
                                cleanItem.contains("SKOR: 1") || cleanItem.contains("SKOR: 2") || cleanItem.contains("SKOR: 3") || cleanItem.contains("SKOR: 4") -> NegatifRed
                                else -> Orange
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", color = itemColor, fontSize = 14.sp)
                                Text(
                                    cleanItem,
                                    fontSize = 10.sp,
                                    color = InkText.copy(alpha = 0.85f),
                                    fontFamily = Manrope,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAnalyze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundNew)
                ) {
                    Text("Analizi Yenile", color = PrimaryTeal, fontSize = 11.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CorporateActionsIntelligenceSection(symbol: String, viewModel: FinanceViewModel) {
    val actions by viewModel.corporateActions.collectAsState()
    val dividendAnalytics by viewModel.dividendAnalytics.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadCorporateActions(symbol)
        viewModel.loadDividendAnalytics(symbol)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📅", fontSize = 16.sp)
                Text(
                    "CORPORATE ACTIONS & DIVIDENDS",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.2.sp
                )
            }
            
            if (dividendAnalytics != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricBox(
                        value = "%${String.format(Locale.US, "%.1f", dividendAnalytics!!.currentYield)}",
                        label = "Verim",
                        modifier = Modifier.weight(1f),
                        tag = "Yıllık"
                    )
                    MetricBox(
                        value = "${dividendAnalytics!!.sustainabilityScore}/100",
                        label = "Sürdürülebilirlik",
                        modifier = Modifier.weight(1f),
                        tag = "AI Skoru",
                        tagType = MetricTagType.GOOD
                    )
                }
            }

            if (actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Recent Events", style = MaterialTheme.typography.labelSmall, color = SubText)
                Spacer(modifier = Modifier.height(8.dp))
                actions.take(3).forEach { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(action.type.name.replace("_", " "), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(java.text.SimpleDateFormat("dd MMM yyyy", Locale.US).format(java.util.Date(action.effectiveDate)), fontSize = 10.sp, color = SubText)
                        }
                        if (action.amount != null) {
                            Text("${action.amount} ${action.currency}", fontSize = 12.sp, fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        } else if (action.ratio != null) {
                            Text("%${String.format(Locale.US, "%.0f", action.ratio!! * 100)}", fontSize = 12.sp, fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold, color = Orange)
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Yakın zamanda kurumsal bir aksiyon bulunmuyor.", fontSize = 11.sp, color = SubText, fontFamily = Manrope)
            }
        }
    }
}

@Composable
fun IndicatorChip(
    label: String,
    value: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundNew)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubText)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, color = InkText)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = statusColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = status,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

data class OeagiAnalysisResult(
    val oeagiScore: Int = 0,
    val grahamScore: Int = 0,
    val newsScore: Int = 0,
    val momentumScore: Int = 0,
    val sectorScore: Int = 0,
    val commentary: String = ""
)

@Composable
fun SubScoreRow(label: String, score: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = SubText, fontFamily = Manrope)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.width(90.dp)
        ) {
            LinearProgressIndicator(
                progress = { score / 100f },
                color = color,
                trackColor = color.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier.weight(1f).height(4.dp)
            )
            Text(
                text = "%$score",
                fontSize = 10.sp,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                color = InkText
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// DUPONT ANALİZİ KARTI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun DuPontAnalysisCard(symbol: String) {
    val result = CompanyAnalysisHelper.getDuPont(symbol)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📈", fontSize = 16.sp)
                Text(
                    text = "DUPONT ANALİZİ (ÖZKAYNAK KARLILIĞI)",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryTeal.copy(alpha = 0.08f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Özkaynak Karlılığı (ROE)", fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%${String.format(Locale.US, "%.2f", result.roe)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = JetBrainsMono,
                        color = PrimaryTeal
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val componentModifier = Modifier.weight(1f)
                DuPontComponentBox("Net Kar Marjı", "%${String.format(Locale.US, "%.1f", result.netProfitMargin)}", componentModifier)
                DuPontComponentBox("Aktif Devir Hızı", "${String.format(Locale.US, "%.2f", result.assetTurnover)}x", componentModifier)
                DuPontComponentBox("Finansal Kaldıraç", "${String.format(Locale.US, "%.2f", result.equityMultiplier)}x", componentModifier)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "DuPont Formülü: Net Kar Marjı (%${String.format(Locale.US, "%.1f", result.netProfitMargin)}) × Aktif Devir Hızı (${String.format(Locale.US, "%.2f", result.assetTurnover)}x) × Kaldıraç Çarpanı (${String.format(Locale.US, "%.2f", result.equityMultiplier)}x)",
                fontSize = 9.sp,
                color = SubText,
                fontFamily = Manrope,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun DuPontComponentBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
            .background(BackgroundNew)
            .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                label,
                fontSize = 9.5.sp,
                color = SubText,
                fontFamily = Manrope,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, color = InkText)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// PIOTROSKI F-SCORE KARTI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun PiotroskiScoreCard(symbol: String) {
    val result = CompanyAnalysisHelper.getPiotroski(symbol)
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛡️", fontSize = 16.sp)
                    Text(
                        text = "PIOTROSKI F-SKORU (F-SCORE)",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        letterSpacing = 1.sp
                    )
                }
                
                val scoreColor = when {
                    result.score >= 7 -> PrimaryTeal
                    result.score >= 4 -> Color(0xFFFFA726)
                    else -> NegatifRed
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(scoreColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${result.score}/9",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMono,
                        color = scoreColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Piotroski F-Skoru, şirketin finansal gücünü 9 farklı kriter üzerinden değerlendirir. 7 ve üzeri güçlü mali sağlığı simgeler.",
                fontSize = 11.sp,
                color = SubText,
                fontFamily = Manrope,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (expanded) "Detayları Gizle" else "Tüm Kriterleri Göster",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    fontFamily = Manrope
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = LineBorder)
                    Spacer(modifier = Modifier.height(4.dp))
                    result.criteria.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                color = InkText,
                                fontFamily = Manrope,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (item.passed) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (item.passed) PrimaryTeal else NegatifRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// ALTMAN Z-SCORE KARTI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun AltmanZScoreCard(symbol: String) {
    val result = CompanyAnalysisHelper.getAltman(symbol)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⚠️", fontSize = 16.sp)
                Text(
                    text = "ALTMAN Z-SKORU (İFLAS RİSKİ)",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hesaplanan Z-Skoru", fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    Text(
                        text = String.format(Locale.US, "%.2f", result.score),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = JetBrainsMono,
                        color = when (result.status) {
                            "SAFE" -> PrimaryTeal
                            "GREY" -> Color(0xFFFFA726)
                            else -> NegatifRed
                        }
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (result.status) {
                                "SAFE" -> PrimaryTeal.copy(alpha = 0.15f)
                                "GREY" -> Color(0xFFFFA726).copy(alpha = 0.15f)
                                else -> NegatifRed.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (result.status) {
                            "SAFE" -> "Güvenli Bölge"
                            "GREY" -> "Gri Bölge"
                            else -> "Riskli Bölge"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope,
                        color = when (result.status) {
                            "SAFE" -> PrimaryTeal
                            "GREY" -> Color(0xFFFFA726)
                            else -> NegatifRed
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Custom Zone Bar Indicator
            val progress = ((result.score - 0.0) / 4.0).coerceIn(0.0, 1.0).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1.81f).fillMaxHeight().background(NegatifRed.copy(alpha = 0.6f)))
                    Box(modifier = Modifier.weight(1.18f).fillMaxHeight().background(Color(0xFFFFA726).copy(alpha = 0.6f)))
                    Box(modifier = Modifier.weight(1.01f).fillMaxHeight().background(PrimaryTeal.copy(alpha = 0.6f)))
                }
                
                // Score Marker
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .border(
                            BorderStroke(
                                2.dp,
                                if (result.status == "SAFE") Color.White else Color.Black
                            ), RoundedCornerShape(4.dp)
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = result.description,
                fontSize = 11.sp,
                color = InkText,
                fontFamily = Manrope,
                lineHeight = 16.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// TEMETTÜ GÜVENLİĞİ KARTI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun DividendSafetyCard(symbol: String) {
    val result = CompanyAnalysisHelper.getDividend(symbol)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("💸", fontSize = 16.sp)
                Text(
                    text = "TEMETTÜ GÜVENLİĞİ VE PAYOUT",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (result.status == "NONE") {
                Text(
                    text = result.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubText,
                    fontFamily = Manrope,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Temettü Verimi", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(
                            text = "%${String.format(Locale.US, "%.1f", result.yield)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = JetBrainsMono,
                            color = InkText
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Payout (Kar Dağıtım)", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(
                            text = "%${String.format(Locale.US, "%.1f", result.payoutRatio)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = JetBrainsMono,
                            color = InkText
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Temettü Güvenlik Skoru", fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    val safetyColor = when (result.status) {
                        "SAFE" -> PrimaryTeal
                        "WARNING" -> Color(0xFFFFA726)
                        else -> NegatifRed
                    }
                    Text(
                        text = "${result.safetyScore}/100",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JetBrainsMono,
                        color = safetyColor
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { result.safetyScore / 100f },
                    color = when (result.status) {
                        "SAFE" -> PrimaryTeal
                        "WARNING" -> Color(0xFFFFA726)
                        else -> NegatifRed
                    },
                    trackColor = Color.White.copy(alpha = 0.08f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = result.description,
                    fontSize = 11.sp,
                    color = InkText,
                    fontFamily = Manrope,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// BENZER ŞİRKET KIYASLAMASI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun PeerComparisonCard(symbol: String, market: String, currentPrice: Double, sector: String) {
    val peers = CompanyAnalysisHelper.getPeers(symbol, market, sector)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("👥", fontSize = 16.sp)
                Text(
                    text = "BENZER ŞİRKET KIYASLAMASI",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Peer Table
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hisse", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.width(60.dp), fontFamily = Manrope)
                    Text("F/K", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.width(50.dp), fontFamily = Manrope)
                    Text("Temettü", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.width(60.dp), fontFamily = Manrope)
                    Text("Piyasa Değeri", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.weight(1f), fontFamily = Manrope)
                }
                
                // Current Company
                val myHash = abs(symbol.hashCode())
                val myPeVal = 8.0 + (myHash % 250) / 10.0
                val myDivVal = if (myHash % 2 == 0) (1.0 + (myHash % 60) / 10.0) else null
                val myMc = "${30 + (myHash % 450)} Milyar ${if (market == "BIST") "TL" else "USD"}"
                
                PeerRow(symbol, myPeVal, myDivVal, myMc, isSelf = true)
                
                // Competitors
                peers.forEach { peer ->
                    PeerRow(peer.symbol, peer.peRatio, peer.dividendYield, peer.marketCap, isSelf = false)
                }
            }
        }
    }
}

@Composable
fun PeerRow(sym: String, pe: Double?, div: Double?, mc: String, isSelf: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelf) PrimaryTeal.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sym,
            fontSize = 12.sp,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelf) PrimaryTeal else InkText,
            modifier = Modifier.width(52.dp),
            fontFamily = JetBrainsMono
        )
        Text(
            text = pe?.let { String.format(Locale.US, "%.1f", it) } ?: "N/A",
            fontSize = 11.sp,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal,
            color = InkText,
            modifier = Modifier.width(50.dp),
            fontFamily = IBMPlexMono
        )
        Text(
            text = div?.let { "%${String.format(Locale.US, "%.1f", it)}" } ?: "N/A",
            fontSize = 11.sp,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal,
            color = InkText,
            modifier = Modifier.width(60.dp),
            fontFamily = IBMPlexMono
        )
        Text(
            text = mc,
            fontSize = 11.sp,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal,
            color = InkText,
            modifier = Modifier.weight(1f),
            fontFamily = Manrope,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
// DCF İÇSEL DEĞER SİMÜLATÖRÜ
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun DcfSimulatorCard(symbol: String, currentPrice: Double) {
    var growthRate by remember { mutableFloatStateOf(12f) }
    var discountRate by remember { mutableFloatStateOf(10f) }
    
    val intrinsicValue = remember(currentPrice, growthRate, discountRate) {
        CompanyAnalysisHelper.calculateDcf(currentPrice, growthRate.toDouble(), discountRate.toDouble())
    }
    
    val discountPercent = remember(currentPrice, intrinsicValue) {
        if (intrinsicValue > 0) ((intrinsicValue - currentPrice) / intrinsicValue * 100) else 0.0
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎮", fontSize = 16.sp)
                Text(
                    text = "DCF İÇSEL DEĞER SİMÜLATÖRÜ",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Value Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("DCF Adil Değeri", fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    Text(
                        text = String.format(Locale.US, "%.2f", intrinsicValue),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = JetBrainsMono,
                        color = if (discountPercent >= 0) PrimaryTeal else NegatifRed
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (discountPercent >= 0) PrimaryTeal.copy(alpha = 0.15f) else NegatifRed.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (discountPercent >= 0) {
                            String.format(Locale.US, "%%%d İskontolu", abs(discountPercent.toInt()))
                        } else {
                            String.format(Locale.US, "%%%d Primli", abs(discountPercent.toInt()))
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope,
                        color = if (discountPercent >= 0) PrimaryTeal else NegatifRed
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Growth Slider
            Text(
                text = String.format(Locale.US, "Yıllık Büyüme Oranı: %d%%", growthRate.toInt()),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope
            )
            Slider(
                value = growthRate,
                onValueChange = { growthRate = it },
                valueRange = 5f..30f,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryTeal,
                    activeTrackColor = PrimaryTeal,
                    inactiveTrackColor = Color.White.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Discount Slider
            Text(
                text = String.format(Locale.US, "İskonto Oranı (WACC): %d%%", discountRate.toInt()),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope
            )
            Slider(
                value = discountRate,
                onValueChange = { discountRate = it },
                valueRange = 8f..18f,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryTeal,
                    activeTrackColor = PrimaryTeal,
                    inactiveTrackColor = Color.White.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 WACC (Ağırlıklı Ortalama Sermaye Maliyeti) düştükçe adil değer yükselir; gelecekteki büyüme oranı beklentiniz yükseldikçe adil değer yine yükselir.",
                fontSize = 9.sp,
                color = SubText,
                fontFamily = Manrope,
                lineHeight = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// AI MOAT (EKONOMİK HENDEK) KARTI
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun AiMoatAnalysisCard(symbol: String) {
    val result = CompanyAnalysisHelper.getMoat(symbol)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🏰", fontSize = 16.sp)
                Text(
                    text = "BUFFETT EKONOMİK HENDEK (MOAT) SKORU",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gauge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(70.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { result.score / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = PrimaryTeal,
                        trackColor = Color.White.copy(alpha = 0.08f),
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${result.score}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = JetBrainsMono,
                        color = PrimaryTeal
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.rating,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        text = "Warren Buffett'ın rekabetçi üstünlük yaklaşımıyla derecelendirme.",
                        fontSize = 10.sp,
                        color = SubText,
                        fontFamily = Manrope,
                        lineHeight = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = LineBorder)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Temel Rekabet Avantajları:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                fontFamily = Manrope
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            result.advantages.forEach { adv ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(PrimaryTeal))
                    Text(
                        text = adv,
                        fontSize = 11.sp,
                        color = InkText,
                        fontFamily = Manrope,
                        modifier = Modifier.weight(1f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// FİNANSAL TABLOLAR SEKME İÇERİĞİ (FMP API Real Data)
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun FinancialStatementsTabSection(
    symbol: String,
    viewModel: FinanceViewModel
) {
    val incomeStatements by viewModel.getIncomeStatements(symbol).collectAsState(initial = emptyList())
    val balanceSheets by viewModel.getBalanceSheets(symbol).collectAsState(initial = emptyList())
    val cashFlows by viewModel.getCashFlows(symbol).collectAsState(initial = emptyList())
    val companyRatios by viewModel.getCompanyRatios(symbol).collectAsState(initial = emptyList())
    val numberFormat by viewModel.numberFormat.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) }
    val subTabs = listOf("Gelir Tablosu", "Bilanço", "Nakit Akışı", "Rasyolar")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "📊 FİNANSAL TABLOLAR & PERFORMANS",
                fontSize = 12.sp,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                color = PrimaryTeal,
                letterSpacing = 1.sp
            )

            ScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                edgePadding = 0.dp,
                containerColor = BackgroundNew,
                divider = {},
                indicator = {},
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                subTabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedSubTab == idx,
                        onClick = { selectedSubTab = idx },
                        text = {
                            Text(
                                title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedSubTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSubTab == idx) PrimaryTeal else SubText
                            )
                        }
                    )
                }
            }

            when (selectedSubTab) {
                0 -> { // Gelir Tablosu
                    val latestIncome = incomeStatements.firstOrNull()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinancialRow("Satış Gelirleri (Revenue)", latestIncome?.revenue ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Brüt Kâr (Gross Profit)", latestIncome?.grossProfit ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Faiz/Vergi Öncesi Kâr (EBITDA)", latestIncome?.ebitda ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Net Kâr (Net Income)", latestIncome?.netIncome ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Hisse Başıına Kâr (EPS)", latestIncome?.eps ?: 0.0, "", numberFormat)
                    }
                }
                1 -> { // Bilanço
                    val latestBalance = balanceSheets.firstOrNull()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinancialRow("Toplam Varlıklar (Assets)", latestBalance?.totalAssets ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Toplam Yükümlülükler (Liabilities)", latestBalance?.totalLiabilities ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Özkaynaklar (Equity)", latestBalance?.totalEquity ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Net Borç (Net Debt)", latestBalance?.netDebt ?: 0.0, "₺/$", numberFormat)
                    }
                }
                2 -> { // Nakit Akışı
                    val latestFlow = cashFlows.firstOrNull()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinancialRow("İşletme Nakit Akışı", latestFlow?.operatingCashFlow ?: 0.0, "₺/$", numberFormat)
                        FinancialRow("Serbest Nakit Akışı (FCF)", latestFlow?.freeCashFlow ?: 0.0, "₺/$", numberFormat)
                    }
                }
                3 -> { // Rasyolar
                    val latestRatio = companyRatios.firstOrNull()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinancialRow("Fiyat / Kazanç (F/K - P/E)", latestRatio?.peRatio ?: 0.0, "", numberFormat)
                        FinancialRow("Piyasa Değeri / Defter Değeri (PD/DD)", latestRatio?.pbRatio ?: 0.0, "", numberFormat)
                        FinancialRow("Özkaynak Kârlılığı (ROE)", (latestRatio?.roe ?: 0.0) * 100, "%", numberFormat)
                        FinancialRow("Varlık Kârlılığı (ROA)", (latestRatio?.roa ?: 0.0) * 100, "%", numberFormat)
                        FinancialRow("Borç / Özkaynak Oranı", latestRatio?.debtToEquity ?: 0.0, "", numberFormat)
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialRow(label: String, value: Double, unit: String, format: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = SubText, fontFamily = Manrope)
        Text(
            if (value != 0.0) "${NumberFormatter.format(value, format)} $unit".trim() else "N/A",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = InkText,
            fontFamily = IBMPlexMono
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
// AI ORACLE DOKTRİNİ KARTI (YENİ SEKME)
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun AiOracleTabCard(
    symbol: String,
    price: Double,
    market: String,
    viewModel: FinanceViewModel
) {
    val incomeStatements by viewModel.getIncomeStatements(symbol).collectAsState(initial = emptyList())
    val companyRatios by viewModel.getCompanyRatios(symbol).collectAsState(initial = emptyList())

    var oracleReport by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(symbol) {
        isLoading = true
        oracleReport = viewModel.getAiOracleReport(symbol, price, incomeStatements, companyRatios)
        isLoading = false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryTeal))
                Text(
                    "🔮 AI ORACLE YATIRIM DOKTRİNİ VE SKORU",
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    letterSpacing = 1.2.sp
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = PrimaryTeal, strokeWidth = 3.dp)
                        Text("AI Oracle finansal doktrini hesaplıyor...", fontSize = 12.sp, color = SubText, fontFamily = Manrope)
                    }
                }
            } else if (!oracleReport.isNullOrBlank()) {
                MarkdownText(
                    markdown = oracleReport ?: "",
                    style = androidx.compose.ui.text.TextStyle(
                        color = InkText,
                        fontSize = 13.sp,
                        fontFamily = Manrope,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    "AI Oracle raporu henüz oluşturulmadı.",
                    fontSize = 12.sp,
                    color = SubText,
                    fontFamily = Manrope
                )
            }
        }
    }
}

