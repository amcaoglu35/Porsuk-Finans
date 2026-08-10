package com.nexus.porsuk.ui.markets.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.PercentFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun SummaryOverviewTab(
    isVisible: Boolean,
    selectedGlobalMarketTab: Int,
    onGlobalMarketTabSelected: (Int) -> Unit,
    onStockClick: (String, String) -> Unit,
    onCalendarClick: () -> Unit,
    onScreenerClick: () -> Unit,
    prices: Map<String, PriceSnapshot> = emptyMap(),
    exchangeRates: Map<String, Double> = emptyMap()
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
                HeroMarketCardsRow(prices = prices, exchangeRates = exchangeRates)
            }
        }

        item(key = "sector_performance_card") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                SectorPerformanceSection(prices = prices)
            }
        }

        item(key = "gainers_losers_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                GainersAndLosersSection(prices = prices, onStockClick = onStockClick)
            }
        }

        item(key = "global_markets_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { 60 })
            ) {
                GlobalMarketsSection(
                    selectedTab = selectedGlobalMarketTab,
                    onTabSelected = onGlobalMarketTabSelected,
                    prices = prices
                )
            }
        }

        item(key = "quick_tools_section") {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { 70 })
            ) {
                QuickToolsGridSection(
                    onCalendarClick = onCalendarClick,
                    onScreenerClick = onScreenerClick
                )
            }
        }
    }
}

@Composable
private fun HeroMarketCardsRow(
    prices: Map<String, PriceSnapshot>,
    exchangeRates: Map<String, Double>
) {
    val usdRate = exchangeRates["USD"] ?: 34.5

    fun buildHeroItem(title: String, code: String, iconEmoji: String, defaultPrice: String, defaultChange: String, defaultPos: Boolean): HeroMarketCardItem {
        val snap = when (code) {
            "BIST100" -> prices["BIST100"] ?: prices["XU100"]
            "BIST30" -> prices["BIST30"] ?: prices["XU030"]
            "USDTRY" -> prices["USDTRY"] ?: exchangeRates["USD"]?.let { PriceSnapshot("USDTRY", it, 0.0, "DAY") }
            "EURUSD" -> prices["EURUSD"] ?: prices["EURUSD=X"]
            else -> prices[code]
        }
        if (snap != null && snap.price > 0.0) {
            val (changeStr, isPos) = PercentFormatter.formatChangePercent(snap.changePercent)
            val priceStr = when (code) {
                "EURUSD" -> String.format(java.util.Locale.US, "%.4f", snap.price)
                "USDTRY" -> String.format(java.util.Locale.US, "₺%.2f", snap.price)
                "GC=F" -> String.format(java.util.Locale.US, "₺%.2f", snap.price * usdRate / 31.1035)
                "CL=F" -> String.format(java.util.Locale.US, "$%.2f", snap.price)
                "BTC-USD" -> String.format(java.util.Locale.US, "$%,.2f", snap.price)
                else -> CurrencyFormatter.formatTRY(snap.price, "TR")
            }
            val spark = if (isPos) listOf(40f, 42f, 45f, 48f, 50f) else listOf(50f, 48f, 45f, 42f, 40f)
            return HeroMarketCardItem(title, priceStr, changeStr, isPos, iconEmoji, spark, isDataAvailable = true)
        }
        val usdFromRates = exchangeRates["USD"]
        if (code == "USDTRY" && usdFromRates != null && usdFromRates > 0.0) {
            val priceStr = String.format(java.util.Locale.US, "₺%.2f", usdFromRates)
            return HeroMarketCardItem(title, priceStr, defaultChange, defaultPos, iconEmoji, listOf(40f, 42f, 45f, 48f, 50f), isDataAvailable = true)
        }
        return HeroMarketCardItem(title, "Veri Yok", "--", true, iconEmoji, emptyList(), isDataAvailable = false)
    }

    val cardsData = remember(prices, exchangeRates) {
        listOf(
            buildHeroItem("BIST 100", "BIST100", "🟣", "10.456,87", "^ %1,35", true),
            buildHeroItem("BIST 30", "BIST30", "🔵", "11.632,15", "^ %1,28", true),
            buildHeroItem("DOLAR / TL", "USDTRY", "💵", String.format(java.util.Locale.US, "₺%.2f", usdRate), "^ %0,42", true),
            buildHeroItem("ALTIN / GR", "GC=F", "🪙", "2.395,45", "^ %0,31", true),
            buildHeroItem("EUR / USD", "EURUSD", "💶", "1,0850", "v %0,28", false),
            buildHeroItem("BRENT", "CL=F", "🛢️", "84,20", "^ %0,75", true),
            buildHeroItem("BITCOIN", "BTC-USD", "₿", "67.450,00", "^ %2,10", true)
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
    val sparkValues: List<Float>,
    val isDataAvailable: Boolean = true
)

@Composable
private fun HeroMarketCard(item: HeroMarketCardItem) {
    val color = if (item.isPositive) PozitifGreen else NegatifRed

    Card(
        modifier = Modifier
            .width(145.dp)
            .shadow(4.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.iconEmoji, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!item.isDataAvailable) {
                Text(
                    "Veri Yok",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "--",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(28.dp), contentAlignment = Alignment.Center) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            } else {
                Text(
                    item.price,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    modifier = Modifier.fillMaxWidth().height(28.dp),
                    filled = true
                )
            }
        }
    }
}

@Composable
private fun SectorPerformanceSection(prices: Map<String, PriceSnapshot>) {
    val sectors = remember {
        listOf(
            SectorItem("Bankacılık", "^ %2,45", true, listOf(40f, 45f, 44f, 48f, 52f)),
            SectorItem("Savunma", "^ %2,12", true, listOf(30f, 32f, 35f, 38f, 40f)),
            SectorItem("Teknoloji", "^ %1,85", true, listOf(50f, 52f, 51f, 54f, 56f)),
            SectorItem("Holding", "^ %0,98", true, listOf(60f, 61f, 62f, 63f)),
            SectorItem("Ulaştırma", "v %0,35", false, listOf(70f, 69f, 68f, 67f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Text("Sektör Performansı (BIST)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tümünü Gör", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sectors, key = { it.name }) { sector ->
                    val color = if (sector.isPositive) PozitifGreen else NegatifRed
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(115.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(sector.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
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
private fun GainersAndLosersSection(
    prices: Map<String, PriceSnapshot>,
    onStockClick: (String, String) -> Unit
) {
    val gainers = remember(prices) {
        val defaultList = listOf(
            StockRowItem("ASELS", "₺56,70", "^ %4,25", true),
            StockRowItem("THYAO", "₺305,25", "^ %2,87", true),
            StockRowItem("KCHOL", "₺182,40", "^ %2,31", true),
            StockRowItem("SISE", "₺49,18", "^ %1,98", true),
            StockRowItem("EKGYO", "₺10,52", "^ %1,76", true)
        )
        defaultList.map { item ->
            val snap = prices[item.symbol]
            if (snap != null) {
                val (changeStr, isPos) = PercentFormatter.formatChangePercent(snap.changePercent)
                item.copy(price = "₺${String.format("%.2f", snap.price)}", changePct = changeStr, isPositive = isPos)
            } else item
        }
    }

    val losers = remember(prices) {
        val defaultList = listOf(
            StockRowItem("AKBNK", "₺52,15", "v %0,42", false),
            StockRowItem("TUPRS", "₺165,80", "v %0,85", false),
            StockRowItem("SASA", "₺38,72", "v %1,22", false),
            StockRowItem("EREGL", "₺45,10", "v %1,34", false),
            StockRowItem("PETKM", "₺17,26", "v %1,88", false)
        )
        defaultList.map { item ->
            val snap = prices[item.symbol]
            if (snap != null) {
                val (changeStr, isPos) = PercentFormatter.formatChangePercent(snap.changePercent)
                item.copy(price = "₺${String.format("%.2f", snap.price)}", changePct = changeStr, isPositive = isPos)
            } else item
        }
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PozitifGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yükselenler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurface)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingDown, contentDescription = null, tint = NegatifRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Düşenler", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurface)
                    }
                    Text("Tümü", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
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
    val color = if (item.isPositive) PozitifGreen else NegatifRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.symbol, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurface)
            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = color)
    }
}

@Composable
private fun GlobalMarketsSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    prices: Map<String, PriceSnapshot>
) {
    val filterTabs = remember { listOf("ABD", "Avrupa", "Asya", "Emtia", "Kripto") }

    fun buildGlobalItem(name: String, code: String, flag: String, defaultPrice: String, defaultChange: String, defaultPos: Boolean): GlobalMarketItem {
        val snap = when (code) {
            "DAX" -> prices["DAX"] ?: prices["^GDAXI"]
            "FTSE" -> prices["FTSE"] ?: prices["^FTSE"]
            "N225" -> prices["N225"] ?: prices["^N225"]
            "HSI" -> prices["HSI"] ?: prices["^HSI"]
            "SPY" -> prices["SPY"] ?: prices["^GSPC"]
            "QQQ" -> prices["QQQ"] ?: prices["^IXIC"]
            "IWM" -> prices["IWM"] ?: prices["^DJI"]
            else -> prices[code]
        }
        if (snap != null && snap.price > 0.0) {
            val (changeStr, isPos) = PercentFormatter.formatChangePercent(snap.changePercent)
            val priceStr = String.format(java.util.Locale.US, "%,.2f", snap.price)
            val spark = if (isPos) listOf(50f, 52f, 51f, 55f, 58f) else listOf(58f, 55f, 51f, 52f, 50f)
            return GlobalMarketItem(name, priceStr, changeStr, isPos, flag, spark, isDataAvailable = true)
        }
        return GlobalMarketItem(name, "Veri Yok", "--", true, flag, emptyList(), isDataAvailable = false)
    }

    val globalItems = remember(prices, selectedTab) {
        when (selectedTab) {
            0 -> listOf(
                buildGlobalItem("S&P 500", "SPY", "🇺🇸", "5.325,16", "^ %0,88", true),
                buildGlobalItem("NASDAQ", "QQQ", "🇺🇸", "16.832,62", "^ %1,28", true),
                buildGlobalItem("DOW JONES", "IWM", "🇺🇸", "39.872,99", "^ %0,75", true)
            )
            1 -> listOf(
                buildGlobalItem("DAX 40", "DAX", "🇩🇪", "18.720,40", "v %0,25", false),
                buildGlobalItem("FTSE 100", "FTSE", "🇬🇧", "8.245,10", "^ %0,42", true)
            )
            2 -> listOf(
                buildGlobalItem("NIKKEI 225", "N225", "🇯🇵", "38.650,00", "^ %0,75", true),
                buildGlobalItem("HANG SENG", "HSI", "🇭🇰", "17.920,80", "v %0,65", false)
            )
            3 -> listOf(
                buildGlobalItem("Altın (Ons)", "GC=F", "🪙", "2.395,45", "^ %0,31", true),
                buildGlobalItem("Brent Petrol", "CL=F", "🛢️", "84,20", "^ %0,75", true)
            )
            4 -> listOf(
                buildGlobalItem("Bitcoin", "BTC-USD", "₿", "67.450,00", "^ %2,10", true),
                buildGlobalItem("Ethereum", "ETH-USD", "Ξ", "3.480,00", "^ %1,85", true)
            )
            else -> listOf(
                buildGlobalItem("S&P 500", "SPY", "🇺🇸", "5.325,16", "^ %0,88", true),
                buildGlobalItem("NASDAQ", "QQQ", "🇺🇸", "16.832,62", "^ %1,28", true),
                buildGlobalItem("DOW JONES", "IWM", "🇺🇸", "39.872,99", "^ %0,75", true)
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dünya Piyasaları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                }
                Text("Tümünü Gör >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filterTabs.forEachIndexed { idx, label ->
                    val isSelected = selectedTab == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { onTabSelected(idx) }
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 10.5.sp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            globalItems.forEach { item ->
                val color = if (item.isPositive) PozitifGreen else NegatifRed
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                        if (!item.isDataAvailable) {
                            Text("Veri Yok", style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = MaterialTheme.colorScheme.outline)
                        } else {
                            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (!item.isDataAvailable) {
                        Text("--", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = MaterialTheme.colorScheme.outline, modifier = Modifier.weight(0.8f))
                        Spacer(modifier = Modifier.weight(1.0f))
                    } else {
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = color, modifier = Modifier.weight(0.8f))
                        Sparkline(values = item.sparkValues, color = color, modifier = Modifier.weight(1.0f).height(24.dp), filled = true)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.flagEmoji, fontSize = 18.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
    val sparkValues: List<Float>,
    val isDataAvailable: Boolean = true
)

@Composable
private fun QuickToolsGridSection(onCalendarClick: () -> Unit, onScreenerClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("⚡ Hızlı Araçlar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickToolCard("Piyasa Takvimi", "Bugünkü veriler", "⚡", Color(0xFFF3F0FF), MaterialTheme.colorScheme.primary, onCalendarClick, Modifier.weight(1f))
            QuickToolCard("Ekonomik Takvim", "Önemli gelişmeler", "📊", Color(0xFFEFF6FF), Color(0xFF2563EB), onCalendarClick, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickToolCard("Hareketli Hisseler", "Anlık momentum", "🔥", Color(0xFFFFF7ED), Color(0xFFEA580C), onScreenerClick, Modifier.weight(1f))
            QuickToolCard("Hisse Filtresi", "Tarama araçları", "🎯", Color(0xFFECFDF5), PozitifGreen, onScreenerClick, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickToolCard(title: String, subtitle: String, iconEmoji: String, containerColor: Color, iconColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = containerColor, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconEmoji, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = Manrope)
            }
        }
    }
}
