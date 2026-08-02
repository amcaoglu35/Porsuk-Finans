package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.PriceAlert
import com.nexus.porsuk.data.remote.RichOfflineDataEngine
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Porsuk Finans — Şirket / Stock Detay Ekranı
 * HTML mockup tasarımına uygun 5 sekmeli modüler mimari.
 */
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

    var selectedMainTab by remember { mutableIntStateOf(0) }
    var selectedInterval by remember { mutableStateOf("G") }
    var showAlarmDialog by remember { mutableStateOf(false) }

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
            // 1. Üst Hero Kartı (Ticker, Fiyat, Değişim, Geri & Alarm Butonları)
            item(key = "stock_detail_hero_header") {
                StockDetailHeroHeader(
                    symbol = symbol,
                    market = market,
                    companyName = company?.name ?: "Yükleniyor...",
                    price = price,
                    change = change,
                    numberFormat = numberFormat,
                    onBack = onBack,
                    onAlarmClick = { showAlarmDialog = true }
                )
            }

            // 2. 5 Sekmeli Tab Menüsü (Material3 ScrollableTabRow API)
            item(key = "stock_detail_tab_row") {
                val tabs = listOf(
                    "Genel Bakış",
                    "Teknik",
                    "Temel & Değerleme",
                    "Kalite & Risk",
                    "Haber & AI"
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = BackgroundNew,
                    contentColor = PrimaryTeal,
                    edgePadding = 0.dp,
                    divider = { HorizontalDivider(color = LineBorder) },
                    indicator = { tabPositions ->
                        if (selectedMainTab < tabPositions.size) {
                            Box(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedMainTab])
                                    .height(3.dp)
                                    .background(PrimaryTeal)
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedMainTab == index,
                            onClick = { selectedMainTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedMainTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = Manrope,
                                    color = if (selectedMainTab == index) PrimaryTeal else SubText
                                )
                            }
                        )
                    }
                }
            }

            // 3. Seçili Sekmeye Göre İçerik Yönlendirmesi
            item(key = "stock_detail_tab_content") {
                when (selectedMainTab) {
                    0 -> StockDetailOverviewTab(
                        symbol = symbol,
                        market = market,
                        price = price,
                        change = change,
                        company = company,
                        cachedInfo = cachedInfo,
                        historicalPrices = historicalPrices,
                        isHistoryLoading = isHistoryLoading,
                        exchangeRates = exchangeRates,
                        numberFormat = numberFormat,
                        news = news,
                        offlineData = offlineData,
                        selectedInterval = selectedInterval,
                        onIntervalSelected = { selectedInterval = it },
                        onNavigateToChart = onNavigateToChart
                    )

                    1 -> StockDetailTechnicalTab(
                        technicalAnalysis = technicalAnalysis,
                        isLoading = isTechnicalLoading
                    )

                    2 -> StockDetailValuationTab(
                        symbol = symbol,
                        market = market,
                        currentPrice = price,
                        sector = company?.sector ?: "BIST"
                    )

                    3 -> StockDetailRiskTab(
                        symbol = symbol
                    )

                    4 -> StockDetailNewsAiTab(
                        symbol = symbol,
                        market = market,
                        newsSentiment = newsSentiment,
                        isNewsSentimentLoading = isNewsSentimentLoading,
                        aiAnalysis = aiAnalysis,
                        isAiLoading = isAiLoading,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Fiyat Alarmı Ekleme Diyaloğu
    if (showAlarmDialog) {
        StockPriceAlertDialog(
            symbol = symbol,
            currentPrice = price,
            onDismiss = { showAlarmDialog = false },
            onSave = { targetPrice, isAbove ->
                viewModel.viewModelScope.launch {
                    viewModel.insertPriceAlertObject(
                        PriceAlert(
                            symbol = symbol,
                            market = market,
                            targetPrice = targetPrice,
                            isAbove = isAbove
                        )
                    )
                    showAlarmDialog = false
                }
            }
        )
    }
}

/**
 * Şirket Detay Hero Başlık Kartı
 */
@Composable
private fun StockDetailHeroHeader(
    symbol: String,
    market: String,
    companyName: String,
    price: Double,
    change: Double,
    numberFormat: String,
    onBack: () -> Unit,
    onAlarmClick: () -> Unit
) {
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
            // Geri Butonu
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

            // Alarm Ekle Butonu
            IconButton(
                onClick = onAlarmClick,
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
                    text = companyName,
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
                        text = CurrencyFormatter.formatWithSymbol(
                            price,
                            CurrencyFormatter.getCurrencySymbol(market),
                            numberFormat
                        ),
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
                            color = if (change >= 0) Color(0xFF7CFFC4) else NegatifRed
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fiyat Alarmı Ekleme Diyalog Penceresi
 */
@Composable
private fun StockPriceAlertDialog(
    symbol: String,
    currentPrice: Double,
    onDismiss: () -> Unit,
    onSave: (Double, Boolean) -> Unit
) {
    var targetText by remember { mutableStateOf(if (currentPrice > 0) String.format(Locale.US, "%.2f", currentPrice * 1.05) else "") }
    var isAbove by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Fiyat Alarmı Kur ($symbol)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkText,
                fontFamily = Manrope
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Mevcut Fiyat: ₺${String.format(Locale.US, "%.2f", currentPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    fontFamily = IBMPlexMono
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("Hedef Fiyat (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isAbove,
                        onClick = { isAbove = true },
                        label = { Text("Fiyat Üstüne Çıkınca") }
                    )
                    FilterChip(
                        selected = !isAbove,
                        onClick = { isAbove = false },
                        label = { Text("Fiyat Altına Düşünce") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull()
                    if (target != null && target > 0) {
                        onSave(target, isAbove)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text("Alarmı Kaydet", color = BackgroundNew)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = SubText)
            }
        },
        containerColor = CardNew
    )
}
