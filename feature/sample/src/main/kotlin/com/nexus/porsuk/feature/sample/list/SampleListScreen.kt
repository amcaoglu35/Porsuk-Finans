package com.nexus.porsuk.feature.sample.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import com.nexus.porsuk.core.ui.component.EmptyStateView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.MacroIndicators
import com.nexus.porsuk.core.domain.entity.MarketSentiment
import com.nexus.porsuk.core.domain.repository.NewsImpact
import com.nexus.porsuk.core.domain.repository.NewsItem

@Composable
fun SampleListRoute(
    stocks: List<CompanyStock>,
    searchQuery: String,
    selectedSector: String,
    sectors: List<String>,
    marketSentiment: MarketSentiment,
    macroIndicators: MacroIndicators,
    latestNews: List<NewsItem>,
    onSearchQueryChange: (String) -> Unit,
    onSectorSelected: (String) -> Unit,
    onStockClick: (String) -> Unit,
    onOpenAnalysisMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SampleListScreen(
        stocks = stocks,
        searchQuery = searchQuery,
        selectedSector = selectedSector,
        sectors = sectors,
        marketSentiment = marketSentiment,
        macroIndicators = macroIndicators,
        latestNews = latestNews,
        onSearchQueryChange = onSearchQueryChange,
        onSectorSelected = onSectorSelected,
        onStockClick = onStockClick,
        onOpenAnalysisMenu = onOpenAnalysisMenu,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleListScreen(
    stocks: List<CompanyStock>,
    searchQuery: String,
    selectedSector: String,
    sectors: List<String>,
    marketSentiment: MarketSentiment,
    macroIndicators: MacroIndicators,
    latestNews: List<NewsItem>,
    onSearchQueryChange: (String) -> Unit,
    onSectorSelected: (String) -> Unit,
    onStockClick: (String) -> Unit,
    onOpenAnalysisMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Porsuk Finans",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "BİST Canlı Piyasa, Haberler & AI Nabzı",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onOpenAnalysisMenu,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analiz Menüsü"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analiz Menüsü", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = "Piyasalar") },
                    label = { Text("Piyasalar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenAnalysisMenu,
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analiz Menüsü") },
                    label = { Text("Analiz Menüsü") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenAnalysisMenu,
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Porsuk AI") },
                    label = { Text("Porsuk AI") }
                )
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                MarketSentimentHeader(sentiment = marketSentiment)
            }

            item {
                MacroIndicatorsBar(macro = macroIndicators)
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    placeholder = { Text("Hisse sembolü veya şirket adı ara (THYAO, Garanti)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sectors) { sector ->
                        FilterChip(
                            selected = sector == selectedSector,
                            onClick = { onSectorSelected(sector) },
                            label = { Text(sector) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BİST Hisse Listesi (${stocks.size} Şirket)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "AI Tahmin Başarısı: %${marketSentiment.aiAccuracyRate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (stocks.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "Hisse Bulunamadı",
                        subtitle = "Aradığınız '$searchQuery' sembolü veya seçtiğiniz '$selectedSector' sektörüne ait hisse senedi kaydı bulunamadı."
                    )
                }
            } else {
                items(
                    items = stocks,
                    key = { it.symbol },
                ) { stock ->
                    SampleItemRow(
                        stock = stock,
                        onStockClick = onStockClick
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Canlı Piyasa Haberleri & AI Özetleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(latestNews) { news ->
                NewsItemCard(news = news)
            }
        }
    }
}

@Composable
fun MarketSentimentHeader(sentiment: MarketSentiment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Piyasa Duygusu & Nabzı",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF059669))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Boğa ${sentiment.bullRatio}% / Ayı ${sentiment.bearRatio}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Korku & Hırs Endeksi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${sentiment.fearAndGreedIndex} / 100 (${sentiment.fearAndGreedLabel})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Canlı VIX Oynaklık",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${sentiment.vixIndex} Endeks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MacroIndicatorsBar(macro: MacroIndicators) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            MacroChip(label = "USD/TRY", value = "₺${macro.usdTry}")
        }
        item {
            MacroChip(label = "EUR/TRY", value = "₺${macro.eurTry}")
        }
        item {
            MacroChip(label = "TCMB Faiz", value = "%${macro.tcmbPolicyRate}")
        }
        item {
            MacroChip(label = "FED Faiz", value = "%${macro.fedInterestRate}")
        }
        item {
            MacroChip(label = "TCMB Enflasyon", value = "%${macro.tcmbInflation}")
        }
    }
}

@Composable
fun MacroChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NewsItemCard(news: NewsItem) {
    val (impactColor, impactText) = when (news.impact) {
        NewsImpact.POSITIVE -> Color(0xFF10B981) to "POZİTİF"
        NewsImpact.NEUTRAL -> Color(0xFF6B7280) to "NÖTR"
        NewsImpact.NEGATIVE -> Color(0xFFEF4444) to "NEGATİF"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    news.relatedSymbol?.let { symbol ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = symbol,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = "${news.source} • ${news.timeAgo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(impactColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = impactText,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = impactColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = news.aiSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
