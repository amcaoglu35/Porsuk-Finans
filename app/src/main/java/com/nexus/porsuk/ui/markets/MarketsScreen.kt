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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.analysis.AnalysisViewModel
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

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
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedGlobalMarketTab by remember { mutableIntStateOf(0) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = LightSurfaceBg,
        topBar = {
            MarketsTopBar(
                onSearchClick = {},
                onNotificationClick = onNavigateToSettings
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Sekmeler (Scrollable Tabs)
            item(key = "markets_tabs") {
                MarketsTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // 3. Üst Kartlar (Hero Market Cards Row)
            item(key = "hero_market_cards") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { 30 })
                ) {
                    HeroMarketCardsRow()
                }
            }

            // 4. Sektör Performansı (BIST)
            item(key = "sector_performance_card") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
                ) {
                    SectorPerformanceSection()
                }
            }

            // 5 & 6. En Çok Yükselenler & En Çok Düşenler (Side-by-Side 2 Cards)
            item(key = "gainers_losers_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
                ) {
                    GainersAndLosersSection(onStockClick = onStockClick)
                }
            }

            // 7. Dünya Piyasaları (Global Markets)
            item(key = "global_markets_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
                ) {
                    GlobalMarketsSection(
                        selectedTab = selectedGlobalMarketTab,
                        onTabSelected = { selectedGlobalMarketTab = it }
                    )
                }
            }

            // 8. Dünya Isı Haritası / Piyasa Haritası
            item(key = "world_heatmap_section") {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
                ) {
                    WorldMarketHeatmapSection()
                }
            }

            // 9. Hızlı Araçlar (Quick Tools Grid)
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
}

// ── 1. ÜST BAR (Top Bar) ──
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
        // Logo
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

        // Title
        Text(
            "Piyasalar",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = TextDark
        )

        // Actions
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

// ── 2. SEKMELER (Scrollable Tabs) ──
@Composable
private fun MarketsTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = remember {
        listOf("Özet", "Endeksler", "Hisseler", "Döviz", "Emtia", "Kripto", "Tahviller")
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

// ── 3. ÜST KARTLAR (Hero Market Cards Row) ──
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

private data class HeroMarketCardItem(
    val title: String,
    val price: String,
    val changePct: String,
    val isPositive: Boolean,
    val iconEmoji: String,
    val sparkValues: List<Float>
)

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
                Text(
                    item.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                item.price,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                color = TextDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                item.changePct,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            Sparkline(
                values = item.sparkValues,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                filled = true
            )
        }
    }
}

// ── 4. SEKTÖR PERFORMANSI (BIST) ──
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
                    Text(
                        "Sektör Performansı (BIST)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = TextDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Tümünü Gör",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = PurpleAccent
                    )
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(22.dp),
                                filled = true
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class SectorItem(
    val name: String,
    val changePct: String,
    val isPositive: Boolean,
    val sparkValues: List<Float>
)

// ── 5 & 6. EN ÇOK YÜKSELENLER & EN ÇOK DÜŞENLER (Side-by-Side 2 Cards) ──
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
        // En Çok Yükselenler (Left Card)
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
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

        // En Çok Düşenler (Right Card)
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
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

private data class StockRowItem(
    val symbol: String,
    val price: String,
    val changePct: String,
    val isPositive: Boolean
)

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

        Text(
            item.changePct,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.5.sp),
            color = color
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "Favori",
            tint = if (isFavorite) Color(0xFFFFB800) else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier
                .size(16.dp)
                .clickable { isFavorite = !isFavorite }
        )
    }
}

// ── 7. DÜNYA PİYASALARI (Global Markets) ──
@Composable
private fun GlobalMarketsSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val filterTabs = remember { listOf("ABD", "Avrupa", "Asya", "Emtia", "Kripto") }

    val globalItems = remember {
        listOf(
            GlobalMarketItem("S&P 500", "5.325,16", "^ %0,88", true, "🇺🇸", listOf(50f, 52f, 51f, 55f, 58f)),
            GlobalMarketItem("NASDAQ", "16.832,62", "^ %1,28", true, "🇺🇸", listOf(60f, 62f, 65f, 68f, 70f)),
            GlobalMarketItem("DOW JONES", "39.872,99", "^ %0,75", true, "🇺🇸", listOf(390f, 392f, 395f, 398f))
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
                    Text("🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Dünya Piyasaları",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = TextDark
                    )
                }
                Text("Tümünü Gör >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PurpleAccent)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.5.sp
                            ),
                            color = if (isSelected) PurpleAccent else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Items List
            globalItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
                        Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono), color = TextSecondary)
                    }

                    Text(
                        item.changePct,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                        color = BullishGreen,
                        modifier = Modifier.weight(0.8f)
                    )

                    Sparkline(
                        values = item.sparkValues,
                        color = BullishGreen,
                        modifier = Modifier
                            .weight(1.0f)
                            .height(24.dp),
                        filled = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.flagEmoji, fontSize = 18.sp)
                }
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
            }
        }
    }
}

private data class GlobalMarketItem(
    val name: String,
    val price: String,
    val changePct: String,
    val isPositive: Boolean,
    val flagEmoji: String,
    val sparkValues: List<Float>
)

// ── 8. DÜNYA ISI HARİTASI / PİYASA HARİTASI ──
@Composable
private fun WorldMarketHeatmapSection() {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗺️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Piyasa Haritası",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Graphic: World Map representation
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗺️", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        // Heatmap gradient legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Düşüş", fontSize = 8.sp, color = BearishRed, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(BearishRed, Color.LightGray, BullishGreen)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yükseliş", fontSize = 8.sp, color = BullishGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Right side: Regional Breakdown List
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(region, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontFamily = Manrope), color = TextDark)
        Text(
            change,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 10.5.sp),
            color = if (isPositive) BullishGreen else BearishRed
        )
    }
}

// ── 9. HIZLI ARAÇLAR (Quick Tools Grid) ──
@Composable
private fun QuickToolsGridSection(
    onCalendarClick: () -> Unit,
    onScreenerClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı Araçlar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickToolCard(
                title = "Piyasa Takvimi",
                subtitle = "Bugünkü veriler",
                iconEmoji = "⚡",
                containerColor = Color(0xFFF3F0FF),
                iconColor = PurpleAccent,
                onClick = onCalendarClick,
                modifier = Modifier.weight(1f)
            )
            QuickToolCard(
                title = "Ekonomik Takvim",
                subtitle = "Önemli gelişmeler",
                iconEmoji = "📊",
                containerColor = Color(0xFFEFF6FF),
                iconColor = Color(0xFF2563EB),
                onClick = onCalendarClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickToolCard(
                title = "Hareketli Hisseler",
                subtitle = "Anlık momentum",
                iconEmoji = "🔥",
                containerColor = Color(0xFFFFF7ED),
                iconColor = Color(0xFFEA580C),
                onClick = onScreenerClick,
                modifier = Modifier.weight(1f)
            )
            QuickToolCard(
                title = "Hisse Filtresi",
                subtitle = "Tarama araçları",
                iconEmoji = "🎯",
                containerColor = Color(0xFFECFDF5),
                iconColor = BullishGreen,
                onClick = onScreenerClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickToolCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = containerColor,
                modifier = Modifier.size(36.dp)
            ) {
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
