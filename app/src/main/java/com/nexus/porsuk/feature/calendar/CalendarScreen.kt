package com.nexus.porsuk.feature.calendar

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.IpoCalendarEntry
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

@Composable
fun EconomicCalendarTabContent(
    events: List<EconomicEvent>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Etkinlik bulunamadı.", color = SubText)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EconomicEventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EconomicEventCard(event: EconomicEvent, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(marketToFlag(event.country), fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(event.title, fontWeight = FontWeight.Bold, color = InkText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            SimpleDateFormat("HH:mm", Locale.US).format(Date(event.eventTime)),
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                }
                ImpactBadge(level = event.impactLevel)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ValueColumn("Beklenen", event.forecastValue ?: "-")
                ValueColumn("Gerçekleşen", event.actualValue ?: "-", color = PrimaryTeal)
                ValueColumn("Önceki", event.previousValue ?: "-")
            }
        }
    }
}

@Composable
fun ImpactBadge(level: CalendarImpactLevel, modifier: Modifier = Modifier) {
    val color = Color(level.colorHex)
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            level.displayName,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ValueColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = InkText
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = SubText)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = IBMPlexMono)
    }
}

fun marketToFlag(country: String): String {
    return when (country.uppercase()) {
        "TR", "TURKEY" -> "🇹🇷"
        "US", "USA", "UNITED STATES" -> "🇺🇸"
        "EU", "EUROPE", "EURO ZONE" -> "🇪🇺"
        "UK", "UNITED KINGDOM" -> "🇬🇧"
        "JP", "JAPAN" -> "🇯🇵"
        else -> "🌐"
    }
}

@Composable
fun DividendTabContent(
    viewModel: CalendarViewModel,
    uiState: CalendarUiState,
    dividends: List<DividendEvent>,
    onStockClick: (String, String) -> Unit,
    showAiInsight: Boolean,
    onToggleAiInsight: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Market Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Tümü" to "🌐 Tümü",
                    "BIST" to "🇹🇷 BIST",
                    "NASDAQ" to "🇺🇸 NASDAQ",
                    "Avrupa" to "🇪🇺 Avrupa"
                ).forEach { (code, label) ->
                    val isSelected = uiState.selectedDividendMarket == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectDividendMarket(code) },
                        label = { Text(label, fontFamily = Manrope, fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Temettü Gelir Hesaplayıcı Kartı
        item {
            DividendCalculatorCard(
                shares = uiState.calcShares,
                rate = uiState.calcRate,
                result = uiState.calcResult,
                onSharesChange = viewModel::setCalcShares,
                onRateChange = viewModel::setCalcRate,
                onClear = viewModel::clearCalculator
            )
        }

        // Orakul Analiz Kartı
        item {
            OrakulInsightBanner(
                isLoading = uiState.isAiLoading,
                hasKey = uiState.hasGeminiKey,
                showInsight = showAiInsight,
                title = "Orakul Temettü Analiz Raporu",
                description = "En yüksek verimli temettü şirketlerini ve Orakul stratejilerini görmek için tıkla.",
                onToggle = onToggleAiInsight
            )
        }

        // Orakul AI Rapor İçeriği
        if (showAiInsight && (uiState.aiInsightText.isNotBlank() || uiState.isAiLoading || uiState.aiError != null)) {
            item {
                OrakulInsightContentCard(
                    text = uiState.aiInsightText,
                    isLoading = uiState.isAiLoading,
                    error = uiState.aiError
                )
            }
        }

        item {
            Text(
                "Ödeme Takvimi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkText,
                fontFamily = Manrope,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (dividends.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Yaklaşan temettü ödemesi bulunamadı.", color = SubText, fontFamily = Manrope)
                }
            }
        } else {
            items(dividends, key = { "dividend_${it.dividendId}" }) { entry ->
                DividendCalendarItem(entry, onStockClick)
            }
        }
    }
}

@Composable
fun IpoTabContent(
    viewModel: CalendarViewModel,
    uiState: CalendarUiState,
    ipos: List<IpoCalendarEntry>,
    showAiInsight: Boolean,
    onToggleAiInsight: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Tümü" to "🌐 Tümü",
                    "UPCOMING" to "⏳ Yaklaşan",
                    "ACTIVE" to "🔥 Aktif Talep",
                    "COMPLETED" to "✓ İşlem Gören"
                ).forEach { (code, label) ->
                    val isSelected = uiState.selectedIpoStatus == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectIpoStatus(code) },
                        label = { Text(label, fontFamily = Manrope, fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Orakul Analiz Kartı
        item {
            OrakulInsightBanner(
                isLoading = uiState.isAiLoading,
                hasKey = uiState.hasGeminiKey,
                showInsight = showAiInsight,
                title = "Orakul Halka Arz Analiz Raporu",
                description = "Aktif halka arzları ve O-EAGI tavan serisi potansiyellerini görmek için tıklayın.",
                onToggle = onToggleAiInsight
            )
        }

        // Orakul AI Rapor İçeriği
        if (showAiInsight && (uiState.aiInsightText.isNotBlank() || uiState.isAiLoading || uiState.aiError != null)) {
            item {
                OrakulInsightContentCard(
                    text = uiState.aiInsightText,
                    isLoading = uiState.isAiLoading,
                    error = uiState.aiError
                )
            }
        }

        item {
            Text(
                "Şirket Listesi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkText,
                fontFamily = Manrope,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (ipos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Halka arz şirketi bulunamadı.", color = SubText, fontFamily = Manrope)
                }
            }
        } else {
            items(ipos, key = { "ipo_${it.id}" }) { entry ->
                IpoCalendarItem(entry, viewModel)
            }
        }
    }
}

@Composable
fun DividendCalendarItem(
    entry: DividendEvent,
    onStockClick: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        TimelineConnector(color = PrimaryTeal)
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onStockClick(entry.symbol, "BIST") }
                .padding(vertical = 4.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            entry.symbol,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = TealSoft,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "BIST",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                fontFamily = Manrope
                            )
                        }
                    }
                    Text(
                        text = "Verim: %--",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    entry.companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubText,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Hak Kazanma", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(entry.exDate, fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = IBMPlexMono)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Ödeme Tarihi", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(entry.paymentDate, fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = IBMPlexMono)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Brüt Ödeme", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text("${entry.amount} ${entry.currency}", fontSize = 12.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                    }
                }
            }
        }
    }
}

@Composable
fun IpoCalendarItem(entry: IpoCalendarEntry, viewModel: CalendarViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isAlarmSet = uiState.activeIpoAlarms.contains(entry.symbol)

    val statusColor = when (entry.status.uppercase()) {
        "ACTIVE" -> PrimaryTeal
        "UPCOMING" -> Color(0xFFF59E0B)
        else -> SubText
    }
    val statusText = when (entry.status.uppercase()) {
        "ACTIVE" -> "Talep Topluyor"
        "UPCOMING" -> "Yaklaşan"
        else -> "Borsada İşlem Gören"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        TimelineConnector(color = statusColor)
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            entry.symbol,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = statusColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontFamily = Manrope
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${entry.price} TL",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontFamily = IBMPlexMono,
                            fontSize = 15.sp
                        )
                        
                        if (entry.status.uppercase() != "COMPLETED") {
                            IconButton(
                                onClick = { 
                                    viewModel.toggleIpoAlarm(context, entry)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAlarmSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Alarm",
                                    tint = if (isAlarmSet) PrimaryTeal else SubText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = SubText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    entry.companyName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubText,
                    fontFamily = Manrope
                )
                
                if (entry.isCatkatEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(10.dp))
                        }
                        Text("Katılım Endeksine Uygun", fontSize = 10.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Talep Toplama Tarihleri", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text("${formatDate(entry.startDate)} - ${formatDate(entry.endDate)}", fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = IBMPlexMono)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Lot Miktarı (Arz)", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(String.format(Locale.US, "%,d Lot", entry.lotQuantity), fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = IBMPlexMono)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Dağıtım Yöntemi", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(entry.distributionMethod, fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = Manrope)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Konsorsiyum Lideri", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                        Text(entry.broker, fontSize = 12.sp, color = InkText, fontWeight = FontWeight.SemiBold, fontFamily = Manrope)
                    }
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        "🧮 Halka Arz Dağıtım Tahmincisi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Katılacak tahmini kişi sayısını seçerek kaç lot düşeceğini görün:",
                        fontSize = 10.sp,
                        color = SubText,
                        fontFamily = Manrope
                    )
                    
                    var selectedOption by remember { mutableStateOf(2.5) }
                    val options = listOf(1.5, 2.0, 2.5, 3.0, 3.5, 4.0)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        options.forEach { opt ->
                            val isSelected = selectedOption == opt
                            val bg = if (isSelected) PrimaryTeal else LineBorder.copy(alpha = 0.3f)
                            val textCol = if (isSelected) Color.White else InkText
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bg)
                                    .clickable { selectedOption = opt }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${opt}M", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textCol, fontFamily = IBMPlexMono)
                            }
                        }
                    }
                    
                    val estLot = (entry.lotQuantity / (selectedOption * 1000000)).toInt().coerceAtLeast(1)
                    val estCost = estLot * entry.price
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TealSoft)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Düşecek Tahmini Lot", fontSize = 9.sp, color = SubText, fontFamily = Manrope)
                            Text("$estLot Lot", fontSize = 13.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Tahmini Gereken Tutar", fontSize = 9.sp, color = SubText, fontFamily = Manrope)
                            Text(String.format(Locale.US, "%,.2f TL", estCost), fontSize = 13.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    val orakulEstParticipants = 2.8
                    val orakulEstLot = (entry.lotQuantity / (orakulEstParticipants * 1000000)).toInt().coerceAtLeast(1)
                    val orakulEstCost = orakulEstLot * entry.price
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TealSoft.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔮", fontSize = 12.sp)
                            Text(
                                "Orakul AI Tahmini: Bu arza ~2.8M katılım bekliyor. Tahmini Dağıtım: $orakulEstLot Lot (~${String.format(Locale.US, "%,d", orakulEstCost.toInt())} TL).",
                                fontSize = 9.sp,
                                color = PrimaryTeal,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DividendCalculatorCard(
    shares: String,
    rate: String,
    result: Double?,
    onSharesChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                Text(
                    "Temettü Gelir Hesaplayıcı",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = shares,
                    onValueChange = onSharesChange,
                    label = { Text("Lot Adedi", fontSize = 11.sp, fontFamily = Manrope) },
                    placeholder = { Text("Örn: 500", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, unfocusedBorderColor = LineBorder, focusedLabelColor = PrimaryTeal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rate,
                    onValueChange = onRateChange,
                    label = { Text("Brüt Oran (Hisse)", fontSize = 11.sp, fontFamily = Manrope) },
                    placeholder = { Text("Örn: 3.25", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryTeal, unfocusedBorderColor = LineBorder, focusedLabelColor = PrimaryTeal),
                    singleLine = true
                )
            }
            if (result != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                val stopaj = result * 0.10
                val netIncome = result - stopaj
                val taxLimitExceeded = result >= 230000.0

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealSoft)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Brüt Temettü Geliri:",
                            fontSize = 12.sp,
                            color = SubText,
                            fontFamily = Manrope
                        )
                        Text(
                            text = String.format(Locale.US, "%,.2f ₺", result),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontFamily = IBMPlexMono
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Stopaj Kesintisi (%10):",
                            fontSize = 12.sp,
                            color = SubText,
                            fontFamily = Manrope
                        )
                        Text(
                            text = String.format(Locale.US, "-%,.2f ₺", stopaj),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NegatifRed,
                            fontFamily = IBMPlexMono
                        )
                    }

                    HorizontalDivider(color = LineBorder.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Net Temettü Geliri:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontFamily = Manrope
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(Locale.US, "%,.2f ₺", netIncome),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryTeal,
                                fontFamily = IBMPlexMono
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onClear,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle", tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                if (taxLimitExceeded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                        border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 16.sp)
                            Text(
                                "Yıllık toplam temettü geliriniz beyan sınırını (230.000 ₺) aşıyor. Yıl sonunda Gelir Vergisi beyannamesi vermeniz gerekebilir.",
                                fontSize = 10.sp,
                                color = Color(0xFFC2410C),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrakulInsightBanner(
    isLoading: Boolean,
    hasKey: Boolean,
    showInsight: Boolean,
    title: String,
    description: String,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (hasKey) onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(TealSoft.copy(alpha = 0.5f), Color.Transparent))
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔮", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = Manrope
                    )
                    Text(
                        text = if (!hasKey) "Gemini API Key bulunamadı. Ayarlar'dan ekleyin." else description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
                if (hasKey) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PrimaryTeal, strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (showInsight) Icons.Default.Close else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrakulInsightContentCard(
    text: String,
    isLoading: Boolean,
    error: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal)
                )
                Text(
                    "🔮 ORAKUL ANALİZ RAPORU",
                    fontFamily = IBMPlexMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (error != null) {
                Text(error, color = NegatifRed, fontFamily = Manrope, fontSize = 13.sp)
            } else if (isLoading && text.isBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PrimaryTeal, strokeWidth = 2.dp)
                    Text("Orakul verileri okuyor...", fontSize = 12.sp, fontFamily = Manrope, color = SubText)
                }
            } else {
                MarkdownText(
                    markdown = text,
                    style = androidx.compose.ui.text.TextStyle(color = InkText, fontFamily = Manrope, fontSize = 13.sp),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun TimelineConnector(
    color: Color,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(28.dp)
    ) {
        // Üst çizgi
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isFirst) Color.Transparent else LineBorder.copy(alpha = 0.5f))
        )
        // Parlayan neon nokta
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, BackgroundNew, CircleShape)
        )
        // Alt çizgi
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(if (isLast) Color.Transparent else LineBorder.copy(alpha = 0.5f))
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(timestamp))
}
