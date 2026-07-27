package com.nexus.porsuk.ui.opportunity

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
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

data class OpportunityAssetItem(
    val symbol: String,
    val name: String,
    val price: String,
    val changePct: String,
    val isPos: Boolean,
    val aiScore: Int,
    val scoreCategory: String,
    val confidencePct: Int,
    val riskScore: Int,
    val expectedReturnStr: String,
    val newsImpact: String,
    val techStatus: String,
    val fundStatus: String,
    val category: String,
    val market: String,
    val aiRationale: String,
    var isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiOpportunityCenterScreen(
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedFilterIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedItemForReasoning by remember { mutableStateOf<OpportunityAssetItem?>(null) }

    val categoryTabs = remember {
        listOf("🔥 Trend Olanlar", "📈 En Güçlü AI Skoru", "💎 Gizli Fırsatlar", "🏆 En Güçlü Temettüler", "⚡ Teknik Kırılım", "📊 Hacim Patlaması")
    }

    val filterChips = remember {
        listOf("Tümü", "BIST", "ABD", "Avrupa", "ETF", "Fon", "Kripto", "Temettü", "Büyüme", "Değer", "Momentum")
    }

    var opportunities by remember {
        mutableStateOf(
            listOf(
                OpportunityAssetItem(
                    symbol = "THYAO",
                    name = "Türk Hava Yolları",
                    price = "₺305,25",
                    changePct = "^ %2,87",
                    isPos = true,
                    aiScore = 92,
                    scoreCategory = "Çok Güçlü",
                    confidencePct = 94,
                    riskScore = 32,
                    expectedReturnStr = "+%18.5",
                    newsImpact = "OLUMLU",
                    techStatus = "Yükseliş Trendi",
                    fundStatus = "Ucuz F/K (4.2)",
                    category = "🔥 Trend Olanlar",
                    market = "BIST",
                    aiRationale = "THYAO, yolcu büyüme rakamlarının analist beklentilerini %4.2 aşması ve rasyolarının tarihsel ortalamalara göre %35 iskontolu olması nedeniyle AI tarafından 'Çok Güçlü' olarak derecelendirilmiştir."
                ),
                OpportunityAssetItem(
                    symbol = "ASELS",
                    name = "Aselsan Elektronik",
                    price = "₺56,70",
                    changePct = "^ %4,25",
                    isPos = true,
                    aiScore = 88,
                    scoreCategory = "Güçlü",
                    confidencePct = 91,
                    riskScore = 28,
                    expectedReturnStr = "+%22.0",
                    newsImpact = "OLUMLU",
                    techStatus = "Kırılım Gerçekleşti",
                    fundStatus = "Güçlü Bilanço",
                    category = "📈 En Güçlü AI Skoru",
                    market = "BIST",
                    aiRationale = "Savunma sanayii yeni ihracat sözleşmeleri ve teknik 55.0 TL direncinin hacimli kırılması hissede ivmelenmeyi desteklemektedir."
                ),
                OpportunityAssetItem(
                    symbol = "NVDA",
                    name = "NVIDIA Corporation",
                    price = "$128,20",
                    changePct = "^ %3,45",
                    isPos = true,
                    aiScore = 95,
                    scoreCategory = "Çok Güçlü",
                    confidencePct = 96,
                    riskScore = 45,
                    expectedReturnStr = "+%30.0",
                    newsImpact = "ÇOK OLUMLU",
                    techStatus = "ATH Kırılımı",
                    fundStatus = "Hiper Büyüme",
                    category = "💎 Gizli Fırsatlar",
                    market = "NASDAQ",
                    aiRationale = "Yapay zeka veri merkezi çip talebinin 3 kat artması ve veri merkezi marjlarının %78 seviyesine ulaşması hisse momentumunu güçlü kılmaktadır."
                ),
                OpportunityAssetItem(
                    symbol = "EREGL",
                    name = "Ereğli Demir Çelik",
                    price = "₺48,90",
                    changePct = "^ %1,12",
                    isPos = true,
                    aiScore = 84,
                    scoreCategory = "Güçlü",
                    confidencePct = 89,
                    riskScore = 24,
                    expectedReturnStr = "+%14.5",
                    newsImpact = "NÖTR",
                    techStatus = "Destek Seviyesinde",
                    fundStatus = "%9.2 Temettü Verimi",
                    category = "🏆 En Güçlü Temettüler",
                    market = "BIST",
                    aiRationale = "Ereğli, %9.2 tahmin edilen nakit temettü verimi ve yeşil çelik dönüşüm yatırımları ile uzun vadeli temettü portföyleri için yüksek cazibe sunmaktadır."
                ),
                OpportunityAssetItem(
                    symbol = "BTC",
                    name = "Bitcoin",
                    price = "$67.450",
                    changePct = "^ %2,10",
                    isPos = true,
                    aiScore = 86,
                    scoreCategory = "Güçlü",
                    confidencePct = 87,
                    riskScore = 65,
                    expectedReturnStr = "+%25.0",
                    newsImpact = "OLUMLU",
                    techStatus = "Altın Kapanış",
                    fundStatus = "ETF Girişleri",
                    category = "⚡ Teknik Kırılım",
                    market = "BINANCE",
                    aiRationale = "Spot ETF net girişlerinin son 10 günde $1.4 milyarı aşması ve madenci satışlarının durulması teknik boğa bayrağı formasyonunu teyit etmektedir."
                ),
                OpportunityAssetItem(
                    symbol = "TTE",
                    name = "İş Portföy Teknoloji Fonu",
                    price = "₺4,82",
                    changePct = "^ %1,85",
                    isPos = true,
                    aiScore = 89,
                    scoreCategory = "Güçlü",
                    confidencePct = 92,
                    riskScore = 38,
                    expectedReturnStr = "+%48.2 Yıllık",
                    newsImpact = "OLUMLU",
                    techStatus = "Yukarı İvme",
                    fundStatus = "TEFAS Lideri",
                    category = "📊 Hacim Patlaması",
                    market = "TEFAS",
                    aiRationale = "Fon portföyündeki küresel teknoloji devlerinin bilanço başarıları fon getiri performansını TEFAS sıralamasında zirveye taşımıştır."
                )
            )
        )
    }

    val filteredList = remember(query, selectedCategoryIndex, selectedFilterIndex, opportunities) {
        val catName = categoryTabs.getOrNull(selectedCategoryIndex) ?: ""
        val filterName = filterChips.getOrNull(selectedFilterIndex) ?: "Tümü"

        opportunities.filter { item ->
            val matchesQuery = query.isBlank() || item.symbol.contains(query, ignoreCase = true) || item.name.contains(query, ignoreCase = true)
            val matchesCategory = catName.isBlank() || item.category == catName
            val matchesFilter = filterName == "Tümü" || item.market == filterName || (filterName == "Temettü" && item.category.contains("Temettü"))
            matchesQuery && matchesCategory && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "AI Watchlist & Fırsat Merkezi",
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

                // Search Box
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder = { Text("Fırsatlarda Hisse, Fon, Kripto ara...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = PrimaryPurple) },
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
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = BorderColor
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterChips.size) { idx ->
                        val isSel = selectedFilterIndex == idx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) PurpleSoftBg else CardWhite,
                            border = BorderStroke(1.dp, if (isSel) PrimaryPurple else BorderColor),
                            modifier = Modifier.clickable { selectedFilterIndex = idx }
                        ) {
                            Text(
                                filterChips[idx],
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, fontSize = 11.sp),
                                color = if (isSel) PrimaryPurple else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    categoryTabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedCategoryIndex == idx,
                            onClick = { selectedCategoryIndex = idx },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedCategoryIndex == idx) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedCategoryIndex == idx) PrimaryPurple else TextSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = LightBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredList, key = { it.symbol }) { item ->
                OpportunityCardItem(
                    item = item,
                    onStockClick = { onStockClick(item.symbol, item.market) },
                    onReasoningClick = { selectedItemForReasoning = item },
                    onAlertClick = {
                        Toast.makeText(context, "🔔 ${item.symbol} için AI Fiyat Uyarısı Kuruldu!", Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteToggle = {
                        opportunities = opportunities.map {
                            if (it.symbol == item.symbol) it.copy(isFavorite = !it.isFavorite) else it
                        }
                        Toast.makeText(context, if (!item.isFavorite) "⭐ Takip Listesine Eklendi" else "Takip Listesinden Çıkarıldı", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        selectedItemForReasoning?.let { item ->
            AiReasoningBottomSheet(
                item = item,
                onDismiss = { selectedItemForReasoning = null }
            )
        }
    }
}

@Composable
private fun OpportunityCardItem(
    item: OpportunityAssetItem,
    onStockClick: () -> Unit,
    onReasoningClick: () -> Unit,
    onAlertClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(22.dp))
            .clickable { onStockClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Symbol, Name, Favorite Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = PurpleSoftBg,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.symbol.take(2), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold), color = PrimaryPurple)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.symbol, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = LightBackground) {
                                Text(item.market, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        }
                        Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favori",
                        tint = if (item.isFavorite) WarningOrange else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Score Badge & Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PurpleSoftBg
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖 AI Skoru: ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                        Text("${item.aiScore}/100 ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PrimaryPurple)
                        Text("(${item.scoreCategory})", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = PrimaryPurple)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(item.price, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = TextDark)
                    Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) SuccessGreen else Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical & Fundamental Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.12f)) {
                    Text(item.techStatus, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Surface(shape = RoundedCornerShape(8.dp), color = LightBackground) {
                    Text(item.fundStatus, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: "❓ Neden?" and "🔔 Alarm Kur"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReasoningClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryPurple),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Text("❓ Neden Öneriliyor?", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryPurple)
                }

                Button(
                    onClick = onAlertClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Outlined.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Alarm Kur", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiReasoningBottomSheet(
    item: OpportunityAssetItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤖", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("${item.symbol} - AI Neden Öneriyor?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Text("AI Güven Seviyesi: %${item.confidencePct}", style = MaterialTheme.typography.labelSmall, color = PrimaryPurple)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("💡 Analiz Gerekçesi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PrimaryPurple)
            Text(item.aiRationale, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp), color = TextDark)

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Beklenen Getiri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    Text(item.expectedReturnStr, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = SuccessGreen)
                }
                Column {
                    Text("Risk Puanı", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    Text("${item.riskScore} / 100", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = WarningOrange)
                }
                Column {
                    Text("Haber Etkisi", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    Text(item.newsImpact, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = PrimaryPurple)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Anladım", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiOpportunityCenterScreenPreview() {
    AiOpportunityCenterScreen(onBack = {}, onStockClick = { _, _ -> })
}
