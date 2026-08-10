package com.nexus.porsuk.ui.search

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.FinanceViewModel

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)
private val SuccessGreen = Color(0xFF00C48C)
private val ErrorRed = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())
    val tefasFunds by viewModel.allTefasFunds.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()

    val categories = remember {
        listOf("Tümü", "Hisseler", "Fonlar", "Kripto", "Döviz", "Emtia")
    }

    val dynamicSearchItems = remember(companies, tefasFunds, prices) {
        val list = mutableListOf<SearchResultItem>()

        // 1. Canlı Hisse Senetleri
        companies.forEach { comp ->
            val snap = prices[comp.symbol]
            val priceVal = snap?.price ?: comp.currentPrice
            val changeVal = snap?.changePercent ?: 0.0
            val isPos = changeVal >= 0
            val prefix = if (comp.market.equals("NASDAQ", true) || comp.market.equals("NYSE", true)) "$" else "₺"
            list.add(
                SearchResultItem(
                    symbol = comp.symbol,
                    title = comp.companyName,
                    category = "Hisseler",
                    valueStr = "$prefix${String.format(java.util.Locale.US, "%.2f", priceVal)}",
                    changeStr = "${if (isPos) "+" else ""}%${String.format(java.util.Locale.US, "%.2f", changeVal)}",
                    market = comp.market.ifBlank { "BIST" },
                    isPos = isPos
                )
            )
        }

        // 2. TEFAS Yatırım Fonları
        tefasFunds.forEach { fund ->
            list.add(
                SearchResultItem(
                    symbol = fund.code,
                    title = fund.title,
                    category = "Fonlar",
                    valueStr = "₺${String.format(java.util.Locale.US, "%.4f", fund.price)}",
                    changeStr = "TEFAS",
                    market = "TEFAS",
                    isPos = true
                )
            )
        }

        // 3. Sabit Piyasa Göstergeleri
        list.add(SearchResultItem("USD/TRY", "Amerikan Doları", "Döviz", "₺32,65", "+%0,42", "FOREX", true))
        list.add(SearchResultItem("EUR/TRY", "Euro", "Döviz", "₺35,40", "+%0,28", "FOREX", true))
        list.add(SearchResultItem("ALTIN", "Gram Altın", "Emtia", "₺2.395,45", "+%0,31", "PIYASA", true))
        list.add(SearchResultItem("BTC", "Bitcoin", "Kripto", "$67.450,00", "+%2,10", "BINANCE", true))
        list.add(SearchResultItem("ETH", "Ethereum", "Kripto", "$3.480,20", "+%1,85", "BINANCE", true))

        list
    }

    val filteredItems = remember(query, selectedCategoryIndex, dynamicSearchItems) {
        dynamicSearchItems.filter { item ->
            val matchesQuery = query.isBlank() || 
                item.symbol.contains(query, ignoreCase = true) || 
                item.title.contains(query, ignoreCase = true)
            val categoryFilter = categories.getOrNull(selectedCategoryIndex) ?: "Tümü"
            val matchesCategory = categoryFilter == "Tümü" || item.category == categoryFilter
            matchesQuery && matchesCategory
        }.take(50)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "Global Arama Engine",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = TextDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Search Input Field
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder = { Text("Hisse, Fon, Kripto, Döviz veya Haber ara...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Violet) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle", tint = TextSecondary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = Violet,
                        unfocusedBorderColor = BorderColor
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Categories Scrollable Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.size) { idx ->
                        val isSelected = selectedCategoryIndex == idx
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) VioletSoft else CardWhite,
                            border = BorderStroke(1.dp, if (isSelected) Violet else BorderColor),
                            modifier = Modifier.clickable { selectedCategoryIndex = idx }
                        ) {
                            Text(
                                categories[idx],
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Violet else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        containerColor = LightBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredItems, key = { "${it.category}_${it.symbol}" }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(18.dp))
                        .clickable { onStockClick(item.symbol, item.market) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VioletSoft,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = Violet)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1.2f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = LightBackground) {
                                    Text(item.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                            Text(item.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                            Text(item.valueStr, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                            Text(item.changeStr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) SuccessGreen else ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

private data class SearchResultItem(
    val symbol: String,
    val title: String,
    val category: String,
    val valueStr: String,
    val changeStr: String,
    val market: String,
    val isPos: Boolean
)

@Preview(showBackground = true)
@Composable
private fun GlobalSearchScreenPreview() {
    GlobalSearchScreen(onBack = {}, onStockClick = { _, _ -> })
}
