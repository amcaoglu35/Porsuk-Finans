package com.nexus.porsuk.ui.strategy

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Psychology
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

private data class AllocationItem(val category: String, val percentage: Int, val color: Color, val desc: String)
private data class StressTestCondition(val conditionName: String, val expectedReturnStr: String, val isPositive: Boolean, val impactDesc: String)
private data class StrategyComparison(val name: String, val riskScore: Int, val returnPct: String, val sharpeRatio: Double, val diversification: String, val confidencePct: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiStrategyBuilderScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // User selections
    var selectedRiskLevel by rememberSaveable { mutableStateOf("Orta Risk") }
    var selectedHorizon by rememberSaveable { mutableStateOf("3 Yıl") }
    var selectedGoal by rememberSaveable { mutableStateOf("Sermaye Büyümesi") }
    var selectedStrategyType by rememberSaveable { mutableStateOf("Karma Strateji ⚖️") }

    val tabs = remember {
        listOf("🎯 Risk & Hedef Profili", "💡 AI Strateji & Dağılım", "📊 Stres Testi & Senaryolar", "⚖️ Strateji Karşılaştırma")
    }

    val assetAllocations = remember(selectedRiskLevel, selectedGoal) {
        listOf(
            AllocationItem("BIST Hisseleri 🇹🇷", 35, PrimaryPurple, "Ağırlıklı Havacılık, Teknoloji & Temettü hisseleri"),
            AllocationItem("ABD Hisseleri 🇺🇸", 25, SuccessGreen, "Küresel Teknoloji (AI) & Sağlık Devleri"),
            AllocationItem("TEFAS Fonları 🏦", 15, WarningOrange, "Değişken & Serbest Şemsiye Fonları"),
            AllocationItem("Fiziki Altın / Kıymetli Maden 🪙", 10, WarningOrange, "Enflasyon & Jeopolitik Çapa"),
            AllocationItem("Küresel ETF 🌍", 10, PrimaryPurple, "S&P500 & Nasdaq 100 Endeks Fonları"),
            AllocationItem("Kripto Varlıklar ⚡", 5, CriticalRed, "Lider Kriptolar (BTC & ETH)")
        )
    }

    val stressTests = remember {
        listOf(
            StressTestCondition("Boğa Piyasası 🔥", "+%34.5 Yıllık Getiri", true, "Yüksek momentum ve BIST/ABD hisse ağırlığı maksimum performans sağlar."),
            StressTestCondition("Ayı Piyasası 📉", "-%11.2 Maksimum Çekilme", false, "Altın ve TEFAS fonları düşüşü %60 oranında tamponlar."),
            StressTestCondition("Resesyon ❄️", "-%6.4 Sınırlı Etki", false, "Temettü ve sağlık hisseleri nakit akışını korur."),
            StressTestCondition("Yüksek Enflasyon 💸", "+%22.1 Reel Koruma", true, "Fiziki altın ve emtia ETF'leri enflasyon üzerinde getiri sunar."),
            StressTestCondition("Faiz Artışı 🏛️", "+%8.5 Pozitif Seyir", true, "Para piyasası fonları ve düşük borçluluklu şirketler destekler."),
            StressTestCondition("Jeopolitik Risk 🌐", "-%4.2 Geçici Çekilme", false, "Savunma sanayi hisseleri ve altın güvenli liman işlevi görür.")
        )
    }

    val strategyComparisons = remember {
        listOf(
            StrategyComparison("Karma Strateji ⚖️", 45, "%28.5 / Yıl", 2.15, "%94 (Çok Yüksek)", 92),
            StrategyComparison("Büyüme Yatırımı 🚀", 72, "%39.0 / Yıl", 1.85, "%78 (Yüksek)", 88),
            StrategyComparison("Düzenli Temettü 💰", 28, "%19.5 / Yıl", 2.45, "%88 (Yüksek)", 95),
            StrategyComparison("Düşük Volatilite 🛡️", 18, "%15.2 / Yıl", 2.65, "%96 (Maksimum)", 96)
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "AI Strategy Builder & Mimarisi",
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
                            onClick = {
                                Toast.makeText(context, "📄 Kişiselleştirilmiş AI Strateji Raporu Oluşturuldu!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Strateji Raporu", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
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
                0 -> RiskProfileTabContent(
                    selectedRisk = selectedRiskLevel,
                    onRiskSelect = { selectedRiskLevel = it },
                    selectedHorizon = selectedHorizon,
                    onHorizonSelect = { selectedHorizon = it },
                    selectedGoal = selectedGoal,
                    onGoalSelect = { selectedGoal = it }
                )
                1 -> AiStrategyTabContent(
                    selectedStrategyType = selectedStrategyType,
                    onStrategyTypeSelect = { selectedStrategyType = it },
                    allocations = assetAllocations
                )
                2 -> StressTestTabContent(stressTests = stressTests)
                3 -> StrategyComparisonTabContent(comparisons = strategyComparisons)
            }
        }
    }
}

// ── TAB 1: RİSK PROFİLİ VE HEDEF ──
@Composable
private fun RiskProfileTabContent(
    selectedRisk: String,
    onRiskSelect: (String) -> Unit,
    selectedHorizon: String,
    onHorizonSelect: (String) -> Unit,
    selectedGoal: String,
    onGoalSelect: (String) -> Unit
) {
    val risks = listOf("Düşük Risk", "Orta Risk", "Yüksek Risk", "Agresif Risk")
    val horizons = listOf("6 Ay", "1 Yıl", "3 Yıl", "5 Yıl", "10+ Yıl")
    val goals = listOf("Sermaye Büyümesi", "Düzenli Temettü", "Pasif Gelir", "Dengeli Büyüme", "Enflasyondan Korunma")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Risk Seviyesi Seçimi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        risks.forEach { risk ->
                            FilterChip(
                                selected = selectedRisk == risk,
                                onClick = { onRiskSelect(risk) },
                                label = { Text(risk, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⏳ Yatırım Süresi (Vade)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        horizons.forEach { horizon ->
                            FilterChip(
                                selected = selectedHorizon == horizon,
                                onClick = { onHorizonSelect(horizon) },
                                label = { Text(horizon, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎯 Temel Yatırım Hedefi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        goals.forEach { goal ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedGoal == goal) PurpleSoftBg else LightBackground,
                                border = BorderStroke(1.dp, if (selectedGoal == goal) PrimaryPurple else BorderColor),
                                modifier = Modifier.fillMaxWidth().clickable { onGoalSelect(goal) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(goal, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (selectedGoal == goal) PrimaryPurple else TextDark)
                                    if (selectedGoal == goal) {
                                        Icon(Icons.Outlined.Check, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── TAB 2: AI STRATEJİ & DAĞILIM ──
@Composable
private fun AiStrategyTabContent(
    selectedStrategyType: String,
    onStrategyTypeSelect: (String) -> Unit,
    allocations: List<AllocationItem>
) {
    val strategyTypes = listOf("Karma Strateji ⚖️", "Değer Yatırımı 🏛️", "Büyüme Yatırımı 🚀", "Temettü Stratejisi 💰", "Düşük Volatilite 🛡️", "ETF Stratejisi 🌍")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("💡 Önerilen Strateji Tipi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            ScrollableTabRow(
                selectedTabIndex = strategyTypes.indexOf(selectedStrategyType).coerceAtLeast(0),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                strategyTypes.forEach { st ->
                    Tab(
                        selected = selectedStrategyType == st,
                        onClick = { onStrategyTypeSelect(st) },
                        text = { Text(st, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = if (selectedStrategyType == st) PrimaryPurple else TextSecondary) }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("📊 Varlık Sınıfı Dağılım Oranları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Surface(shape = RoundedCornerShape(8.dp), color = SuccessGreen.copy(alpha = 0.12f)) {
                            Text("AI Güven: %92", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = SuccessGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    allocations.forEach { alloc ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(alloc.category, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                                Text("%${alloc.percentage}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = alloc.color)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { alloc.percentage / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = alloc.color,
                                trackColor = LightBackground
                            )
                            Text(alloc.desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleSoftBg),
                border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🧠 AI Gerekçesi (Why This Allocation?)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = PrimaryPurple)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "3 Yıllık yatırım vadeniz ve orta risk profiline göre, BIST %35 ile yüksek büyüme potansiyeli sunarken, ABD Teknoloji (%25) ve Fiziki Altın (%10) enflasyon ve kur dalgalanmalarına karşı çapa görevi üstlenmektedir.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = TextDark
                    )
                }
            }
        }
    }
}

// ── TAB 3: STRES TESTİ VE SENARYOLAR ──
@Composable
private fun StressTestTabContent(stressTests: List<StressTestCondition>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("📊 6 Piyasa Koşulunda Stres Testi Simülasyonu", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(stressTests, key = { it.conditionName }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.conditionName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Text(item.expectedReturnStr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPositive) SuccessGreen else CriticalRed)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.impactDesc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp), color = TextSecondary)
                }
            }
        }
    }
}

// ── TAB 4: STRATEJİ KARŞILAŞTIRMA ──
@Composable
private fun StrategyComparisonTabContent(comparisons: List<StrategyComparison>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("⚖️ Stratejiler Yan Yana Karşılaştırma Matrisi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(comparisons, key = { it.name }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        Surface(shape = RoundedCornerShape(8.dp), color = PrimaryPurple.copy(alpha = 0.12f)) {
                            Text("AI Skoru: %${item.confidencePct}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PrimaryPurple, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Risk Skoru", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                            Text("${item.riskScore} / 100", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                        }
                        Column {
                            Text("Beklenen Getiri", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                            Text(item.returnPct, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = SuccessGreen)
                        }
                        Column {
                            Text("Sharpe Skoru", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                            Text("${item.sharpeRatio}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = PrimaryPurple)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiStrategyBuilderScreenPreview() {
    AiStrategyBuilderScreen(onBack = {})
}
