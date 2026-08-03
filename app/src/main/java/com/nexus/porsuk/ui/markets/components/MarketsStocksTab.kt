package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun StocksTab(onStockClick: (String, String) -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableIntStateOf(0) }
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
        item(key = "stock_search_bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Hisse ara (Örn: THYAO, NVDA...)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Temizle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )
        }

        item(key = "stock_filter_chips") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Tümü", "BIST", "ABD", "⭐ Favoriler").forEachIndexed { idx, label ->
                    val isSelected = selectedFilter == idx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { selectedFilter = idx }
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filteredStocks, key = { it.symbol }) { item ->
            val isFav = favoriteSet.contains(item.symbol)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .clickable { onStockClick(item.symbol, item.market) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(item.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) PozitifGreen else NegatifRed)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favori",
                        tint = if (isFav) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
