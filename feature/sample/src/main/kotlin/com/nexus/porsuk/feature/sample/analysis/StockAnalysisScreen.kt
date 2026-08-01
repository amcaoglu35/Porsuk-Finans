package com.nexus.porsuk.feature.sample.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.core.domain.engine.AiLabTool
import com.nexus.porsuk.core.domain.engine.PortfolioDoctorReport
import com.nexus.porsuk.core.domain.engine.PortfolioSimulationReport
import com.nexus.porsuk.core.domain.engine.StockDuelResult
import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.repository.CorporateEvent
import com.nexus.porsuk.core.domain.repository.FundOverlapResult
import com.nexus.porsuk.core.domain.repository.InstitutionalHolding
import com.nexus.porsuk.core.domain.repository.KapNotice
import com.nexus.porsuk.feature.sample.list.SignalBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAnalysisScreen(
    stocks: List<CompanyStock>,
    kapNotices: List<KapNotice>,
    corporateEvents: List<CorporateEvent>,
    institutionalData: List<InstitutionalHolding>,
    aiLabTools: List<AiLabTool>,
    portfolioDoctorReport: PortfolioDoctorReport?,
    backtestReport: PortfolioSimulationReport?,
    onStockDuel: (String, String) -> StockDuelResult?,
    onFundOverlap: (String, String) -> FundOverlapResult,
    onStockClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val duelResult = remember(stocks) {
        if (stocks.size >= 2) onStockDuel(stocks[0].symbol, stocks[1].symbol) else null
    }

    val fundOverlapResult = remember {
        onFundOverlap("TCD", "MAC")
    }

    val opportunities = remember(stocks) {
        stocks.sortedByDescending { it.aiRatingScore * 0.6 + it.roe * 0.4 }.take(5)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gelişmiş Finansal Analiz & AI Lab",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Düello & Fırsatlar", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Compare, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("KAP & Kurumsal", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Radar, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Doktor & Backtest", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("AI Lab (15 Araç)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Science, contentDescription = null) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            duelResult?.let { duel ->
                                StockDuelCard(duel = duel, onStockClick = onStockClick)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎯 Dinamik Günün Fırsatları (En Yüksek AI & Temettü)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(opportunities) { stock ->
                            OpportunityCard(stock = stock, onStockClick = onStockClick)
                        }
                    }

                    1 -> {
                        item {
                            Text(
                                text = "📡 Akıllı KAP Radar (Canlı Bilanço & Özel Durumlar)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(kapNotices) { notice ->
                            KapNoticeRow(notice = notice, onStockClick = onStockClick)
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "🏛️ Kurumsal Yatırımcılar & Takas Analizi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(institutionalData) { inst ->
                            InstitutionalRow(inst = inst, onStockClick = onStockClick)
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "🏢 Kurumsal Olaylar (M&A / Sermaye Artırımı)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(corporateEvents) { event ->
                            CorporateEventCard(event = event)
                        }
                    }

                    2 -> {
                        item {
                            portfolioDoctorReport?.let { doctor ->
                                PortfolioDoctorCard(doctor = doctor)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            FundOverlapCard(overlap = fundOverlapResult)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            backtestReport?.let { backtest ->
                                BacktestCard(backtest = backtest)
                            }
                        }
                    }

                    3 -> {
                        item {
                            AnalysisHeaderCard(
                                title = "AI Lab Raporlama Paneli (15 Güçlü Araç)",
                                subtitle = "Canlı makro kurlar, VIX dalgalanması ve Piotroski F-Score bağlamıyla zenginleştirilmiş analiz raporları."
                            )
                        }

                        items(aiLabTools) { tool ->
                            AiLabToolCard(tool = tool)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisHeaderCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StockDuelCard(duel: StockDuelResult, onStockClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Compare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gerçekçi Hisse Düellosu (Piotroski & Rasyolar)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = duel.stockA.symbol, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                    Text(text = "₺${duel.stockA.price}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Piotroski: ${duel.piotroskiScoreA}/9", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Text(text = "VS", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = duel.stockB.symbol, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                    Text(text = "₺${duel.stockB.price}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Piotroski: ${duel.piotroskiScoreB}/9", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(10.dp)
            ) {
                Text(
                    text = duel.winnerReason,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OpportunityCard(stock: CompanyStock, onStockClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(stock.symbol) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stock.symbol, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    SignalBadge(signal = stock.technicalSignal)
                }
                Text(text = "F/K: ${stock.peRatio} • ROE: %${stock.roe}", style = MaterialTheme.typography.bodySmall)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF059669))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Fırsat Skor: ${stock.aiRatingScore}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun KapNoticeRow(notice: KapNotice, onStockClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(notice.symbol) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${notice.symbol} - ${notice.companyName}", fontWeight = FontWeight.Bold)
                Text(text = notice.publishTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = notice.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text(text = notice.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InstitutionalRow(inst: InstitutionalHolding, onStockClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(inst.symbol) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${inst.symbol} - Yabancı Payı: %${inst.foreignShareRatio}", fontWeight = FontWeight.Bold)
                Text(text = inst.institutionalSentiment, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
            }
            Text(text = inst.insiderTransactionSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CorporateEventCard(event: CorporateEvent) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "${event.symbol} - ${event.title}", fontWeight = FontWeight.Bold)
            Text(text = "Tarih: ${event.date} • Tutar/Oran: ${event.valueOrRate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = event.details, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PortfolioDoctorCard(doctor: PortfolioDoctorReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Portföy Doktoru (Canlı Kurlar)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "USD/TRY: ₺${doctor.usdTryRateUsed} | EUR/TRY: ₺${doctor.eurTryRateUsed}", style = MaterialTheme.typography.labelSmall)
            Text(text = "Portföy Risk Skoru: ${doctor.riskScore}/100 (${doctor.riskCategory})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = "Sharpe Oranı: ${doctor.sharpeRatio} • Çeşitlendirme: %${doctor.diversificationScore}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = doctor.doctorAdvice, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FundOverlapCard(overlap: FundOverlapResult) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Fon Çakışma (% Overlap) Analizi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${overlap.fundCodeA} - ${overlap.fundCodeB} Çakışma Oranı: %${overlap.overlapPercentage.toInt()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = "Ortak Hisseler: ${overlap.commonHoldings.joinToString()}", style = MaterialTheme.typography.bodySmall)
            Text(text = overlap.similarityLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun BacktestCard(backtest: PortfolioSimulationReport) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "1 Yıllık Backtest Simülatör (Tarihsel Ağırlıklı)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "100.000 TL İlk Yatırım ➔ ₺${backtest.currentPortfolioValueTRY.toInt()} (+%${backtest.totalReturnPercentage.toInt()})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = "BİST100 Getirisi: +%${backtest.bist100ReturnPercentage} • Alfa Üstünlüğü: +%${backtest.alphaOutperformance.toInt()}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF059669))
        }
    }
}

@Composable
fun AiLabToolCard(tool: AiLabTool) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = tool.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = tool.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = tool.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Zengin Bağlam Raporu: ${tool.enrichedContextReport}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}
