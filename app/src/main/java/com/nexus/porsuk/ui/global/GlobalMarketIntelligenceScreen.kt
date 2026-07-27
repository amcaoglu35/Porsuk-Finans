package com.nexus.porsuk.ui.global

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)
private val CriticalRed = Color(0xFFF44336)

private data class GlobalMarketIndex(
    val name: String,
    val flag: String,
    val valueStr: String,
    val dailyChangePct: Double,
    val weeklyChangePct: Double,
    val monthlyChangePct: Double,
    val volumeStr: String,
    val aiTrendScore: Int,
    val region: String
)

private data class CrossMarketCorrelation(
    val title: String,
    val relationScore: String,
    val isPositive: Boolean,
    val summary: String,
    val impactDetails: String
)

private data class MacroEventItem(val title: String, val date: String, val importance: String, val forecast: String, val impact: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalMarketIntelligenceScreen(
    onBack: () -> Unit,
    onMarketClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedRegionFilter by rememberSaveable { mutableStateOf("Tümü") }
    var showBriefingModal by remember { mutableStateOf(false) }

    val tabs = remember {
        listOf("🌍 Piyasalar & Heatmap", "🔄 AI Korelasyon Engine", "🏙️ Sektörler & Takvim", "⚖️ Piyasa Karşılaştırma")
    }

    val regionFilters = remember {
        listOf("Tümü", "🇹🇷 BIST", "🇺🇸 ABD", "🇪🇺 Avrupa", "🇯🇵 Asya", "🪙 Kripto", "💵 Döviz", "🥇 Emtia", "📈 Tahvil")
    }

    val globalIndices = remember {
        listOf(
            GlobalMarketIndex("BIST 100", "🇹🇷", "10.850,40", 1.85, 4.20, 12.4, "₺112,4 MLR", 92, "🇹🇷 BIST"),
            GlobalMarketIndex("S&P 500", "🇺🇸", "5.620,10", 0.95, 2.10, 5.8, "$48,2 MLR", 88, "🇺🇸 ABD"),
            GlobalMarketIndex("Nasdaq 100", "🇺🇸", "19.840,60", 1.42, 3.80, 8.2, "$62,5 MLR", 94, "🇺🇸 ABD"),
            GlobalMarketIndex("DAX 40", "🇪🇺", "18.420,15", -0.32, 0.85, 2.1, "€14,8 MLR", 78, "🇪🇺 Avrupa"),
            GlobalMarketIndex("Nikkei 225", "🇯🇵", "39.150,00", 1.15, 2.90, 6.4, "¥3.2 Trilyon", 86, "🇯🇵 Asya"),
            GlobalMarketIndex("Bitcoin", "🪙", "$67.850,00", 2.85, 8.40, 18.5, "$34,1 MLR", 90, "🪙 Kripto"),
            GlobalMarketIndex("USD/TRY", "💵", "₺33.15", 0.12, 0.45, 1.8, "$8,4 MLR", 82, "💵 Döviz"),
            GlobalMarketIndex("Ons Altın", "🥇", "$2.415,80", 0.65, 1.95, 4.1, "$18,9 MLR", 89, "🥇 Emtia"),
            GlobalMarketIndex("US 10Y Tahvil", "📈", "%4.18", -1.20, -2.40, -5.2, "$120 MLR", 75, "📈 Tahvil")
        )
    }

    val filteredIndices = remember(selectedRegionFilter, globalIndices) {
        if (selectedRegionFilter == "Tümü") globalIndices
        else globalIndices.filter { it.region == selectedRegionFilter }
    }

    val correlations = remember {
        listOf(
            CrossMarketCorrelation("S&P 500 ➔ BIST 100", "%82 Yüksek Pozitif Korelasyon", true, "ABD teknoloji yükselişi BIST teknoloji hisselerini doğrudan destekliyor.", "Nasdaq %1 yükseldiğinde BIST100 ortalama %0.45 pozitif tepki vermektedir."),
            CrossMarketCorrelation("DXY (Dolar Endeksi) ➔ Ons Altın", "%76 Güçlü Negatif Korelasyon", false, "Dolar endeksindeki çekilme ons altını yukarı itiyor.", "DXY 104 seviyesinin altına indiğinde Ons Altın $2.420 hedefine ilerlemektedir."),
            CrossMarketCorrelation("US 10Y Tahvil Faizi ➔ Küresel Teknoloji", "%88 Yönlü Hassasiyet", false, "ABD 10 yıllık tahvil faizlerindeki gerileme büyüme hisselerini rahatlatıyor.", "Tahvil faizinin %4.20 altına inmesi Nasdaq rallisini tetiklemektedir."),
            CrossMarketCorrelation("Brent Petrol ➔ Ulaştırma & Havacılık", "%74 Maliyet Etkisi", false, "Petrol fiyatlarındaki düşüş THYAO ve PGSUS yakıt maliyetlerini düşürmektedir.", "Brent petrol $80 altına indikçe havacılık marjları %4.2 genişlemektedir.")
        )
    }

    val macroEvents = remember {
        listOf(
            MacroEventItem("FED Faiz Kararı 🏛️", "31 Temmuz 2026", "🔴 Kritik", "%5.25 (Sabit)", "Küresel risk iştahı ve dolar endeksi üzerinde doğrudan belirleyici."),
            MacroEventItem("TCMB Enflasyon Raporu 🇹🇷", "08 Ağustos 2026", "🔴 Kritik", "Yıllık %38.2", "BIST bankacılık ve perakende hisselerini etkilemektedir."),
            MacroEventItem("ABD Tarım Dışı İstihdam 📊", "02 Ağustos 2026", "🟠 Yüksek", "+185 Bin", "FED faiz indirimi patikasını netleştirecektir.")
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "Global Market Intelligence",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = TextDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextDark)
                        }
                    },
                    actions = {
                        Button(
                            onClick = { showBriefingModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Public, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Küresel Özet", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedTab == idx) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedTab == idx) PrimaryPurple else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> GlobalMarketsHeatmapTabContent(
                    regionFilters = regionFilters,
                    selectedFilter = selectedRegionFilter,
                    onFilterSelect = { selectedRegionFilter = it },
                    indices = filteredIndices
                )
                1 -> CorrelationEngineTabContent(correlations = correlations)
                2 -> SectorsAndCalendarTabContent(macroEvents = macroEvents)
                3 -> MarketComparisonTabContent(indices = globalIndices)
            }
        }

        if (showBriefingModal) {
            DailyBriefingBottomSheet(onDismiss = { showBriefingModal = false })
        }
    }
}

// ── TAB 1: GLOBAL HEATMAP & PIYASALAR ──
@Composable
private fun GlobalMarketsHeatmapTabContent(
    regionFilters: List<String>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    indices: List<GlobalMarketIndex>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScrollableTabRow(
                selectedTabIndex = regionFilters.indexOf(selectedFilter).coerceAtLeast(0),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                regionFilters.forEach { reg ->
                    Tab(
                        selected = selectedFilter == reg,
                        onClick = { onFilterSelect(reg) },
                        text = { Text(reg, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (selectedFilter == reg) PrimaryPurple else TextSecondary) }
                    )
                }
            }
        }

        item {
            Text("🗺️ Küresel Piyasa Performance Heatmap", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(indices, key = { it.name }) { item ->
            val isPositive = item.dailyChangePct >= 0
            val cardBg = if (isPositive) SuccessGreen.copy(alpha = 0.08f) else CriticalRed.copy(alpha = 0.08f)
            val strokeColor = if (isPositive) SuccessGreen.copy(alpha = 0.3f) else CriticalRed.copy(alpha = 0.3f)

            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, strokeColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.flag, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Text("Hacim: ${item.volumeStr}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(item.valueStr, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                        Text(
                            "${if (isPositive) "+" else ""}${item.dailyChangePct}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                            color = if (isPositive) SuccessGreen else CriticalRed
                        )
                    }
                }
            }
        }
    }
}

// ── TAB 2: AI KORELASYON ENGINE ──
@Composable
private fun CorrelationEngineTabContent(correlations: List<CrossMarketCorrelation>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("🔄 Piyasalar Arası Etkileşim & Korelasyon Analizi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(correlations, key = { it.title }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Surface(shape = RoundedCornerShape(8.dp), color = PrimaryPurple.copy(alpha = 0.12f)) {
                            Text(item.relationScore, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp), color = PrimaryPurple, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(item.summary, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(shape = RoundedCornerShape(10.dp), color = PurpleSoftBg) {
                        Text("🤖 AI Analizi: ${item.impactDetails}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, lineHeight = 13.sp), color = TextDark, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

// ── TAB 3: SEKTÖRLER VE MAKTRO TAKVİM ──
@Composable
private fun SectorsAndCalendarTabContent(macroEvents: List<MacroEventItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("🗓️ Küresel Makroekonomik Takvim", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(macroEvents, key = { it.title }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                        Text(item.date, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Beklenti / Önceki: ${item.forecast}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Etki: ${item.impact}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                }
            }
        }
    }
}

// ── TAB 4: PİYASA KARŞILAŞTIRMA ──
@Composable
private fun MarketComparisonTabContent(indices: List<GlobalMarketIndex>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("⚖️ Küresel Borsalar & Varlık Karşılaştırma Matrisi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(indices, key = { it.name }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.flag, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                            Text("Aylık: +${item.monthlyChangePct}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = SuccessGreen)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("AI Skoru: ${item.aiTrendScore}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurple)
                        Text(item.volumeStr, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyBriefingBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text("🌐 Günlük AI Küresel Piyasa Özeti", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "ABD piyasalarındaki teknoloji yükselişi BIST teknoloji hisselerini desteklerken, Avrupa tarafında DAX endeksi sınırlı kar satışlarıyla yatay seyretmektedir. Dolar endeksindeki gerileme Ons Altını $2.415 seviyesine taşırken, petrol fiyatlarındaki düşüş havacılık marjlarını olumlu etkilemektedir.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
                color = TextDark
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GlobalMarketIntelligenceScreenPreview() {
    GlobalMarketIntelligenceScreen(onBack = {})
}
