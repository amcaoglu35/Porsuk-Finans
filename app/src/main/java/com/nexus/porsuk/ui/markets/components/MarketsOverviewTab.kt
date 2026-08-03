package com.nexus.porsuk.ui.markets.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun SummaryOverviewTab(
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

            Text(item.price, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
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
    var isFavorite by remember { mutableStateOf(false) }
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
        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "Favori",
            tint = if (isFavorite) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                        Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PozitifGreen, modifier = Modifier.weight(0.8f))
                    Sparkline(values = item.sparkValues, color = PozitifGreen, modifier = Modifier.weight(1.0f).height(24.dp), filled = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.flagEmoji, fontSize = 18.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

private data class GlobalMarketItem(val name: String, val price: String, val changePct: String, val isPositive: Boolean, val flagEmoji: String, val sparkValues: List<Float>)

@Composable
fun WorldMarketHeatmapSection() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗺️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Piyasa Haritası", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
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
                            Text("Düşüş", fontSize = 8.sp, color = NegatifRed, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.width(60.dp).height(4.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(NegatifRed, Color.LightGray, PozitifGreen))))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yükseliş", fontSize = 8.sp, color = PozitifGreen, fontWeight = FontWeight.Bold)
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
        Text(region, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
        Text(change, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 10.5.sp), color = if (isPositive) PozitifGreen else NegatifRed)
    }
}

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
