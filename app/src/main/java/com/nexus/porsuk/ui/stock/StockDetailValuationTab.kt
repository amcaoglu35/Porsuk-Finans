package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.remote.RichOfflineDataEngine
import com.nexus.porsuk.ui.common.CompanyAnalysisHelper
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun StockDetailValuationTab(
    symbol: String,
    market: String,
    currentPrice: Double,
    sector: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Graham Adil Değer Hesabı Kartı
        GrahamFairValueCard(symbol = symbol, currentPrice = currentPrice)

        // DCF (İndirgenmiş Nakit Akışı) Simülatörü Kartı
        DcfSimulatorCard(symbol = symbol, currentPrice = currentPrice)

        // DuPont Analizi Kartı
        DuPontAnalysisCard(symbol = symbol)

        // Sektörel / Akran Karşılaştırması Kartı
        PeerComparisonCard(
            symbol = symbol,
            market = market,
            currentPrice = currentPrice,
            sector = sector
        )

        // Finansal Tablolar (Bilanço & Gelir Tablosu)
        FinancialStatementsTabSection(symbol = symbol)
    }
}

@Composable
fun GrahamFairValueCard(
    symbol: String,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    val hash = kotlin.math.abs(symbol.hashCode())
    val eps = 2.5 + (hash % 120) / 10.0
    val bvps = 15.0 + (hash % 250) / 10.0
    val grahamValue = kotlin.math.sqrt(22.5 * eps * bvps)
    val marginOfSafetyPct = if (currentPrice > 0) ((grahamValue - currentPrice) / currentPrice) * 100.0 else 0.0

    val (statusColor, statusText) = if (marginOfSafetyPct > 20.0) {
        Color(0xFF10B981) to "İSKONTOLU (GÜVENLİ)"
    } else if (marginOfSafetyPct >= 0.0) {
        PrimaryTeal to "ADİL DEĞERDE"
    } else {
        NegatifRed to "PRİMLİ (YÜKSEK DEĞERLEME)"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏛️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Benjamin Graham Adil Değer Analizi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Text(
                text = "Formül: √(22.5 × EPS × BVPS) — Graham'ın klasik değer yatırımcılığı yaklaşımına göre hesaplanan üst sınır değerlemedir.",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundNew)
                        .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Graham Adil Değeri", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₺${String.format(Locale.US, "%.2f", grahamValue)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundNew)
                        .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Güvenlik Marjı (%)", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "%${String.format(Locale.US, "%+.1f", marginOfSafetyPct)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                            fontFamily = IBMPlexMono
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DcfSimulatorCard(
    symbol: String,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    var growthRate by remember { mutableStateOf(12.0) }
    var discountRate by remember { mutableStateOf(14.0) }

    val hash = kotlin.math.abs(symbol.hashCode())
    val eps = 3.0 + (hash % 80) / 10.0

    val dcfValue = remember(growthRate, discountRate, eps) {
        var futureEps = eps
        var sumPv = 0.0
        for (year in 1..5) {
            futureEps *= (1 + growthRate / 100.0)
            val pv = futureEps / Math.pow(1 + discountRate / 100.0, year.toDouble())
            sumPv += pv
        }
        val terminalValue = (futureEps * 12.0) / Math.pow(1 + discountRate / 100.0, 5.0)
        sumPv + terminalValue
    }

    val upsidePct = if (currentPrice > 0) ((dcfValue - currentPrice) / currentPrice) * 100.0 else 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧮", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "İndirgenmiş Nakit Akışı (DCF) Simülatörü",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (upsidePct >= 0) Color(0xFF10B981).copy(alpha = 0.15f) else NegatifRed.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%${String.format(Locale.US, "%+.1f", upsidePct)} Potansiyel",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (upsidePct >= 0) Color(0xFF10B981) else NegatifRed,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("5 Yıllık Büyüme Oranı: %${growthRate.toInt()}", style = MaterialTheme.typography.labelSmall, color = SubText)
                    Slider(
                        value = growthRate.toFloat(),
                        onValueChange = { v: Float -> growthRate = v.toDouble() },
                        valueRange = 5f..30f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryTeal, activeTrackColor = PrimaryTeal)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("İskonto Oranı (WACC): %${discountRate.toInt()}", style = MaterialTheme.typography.labelSmall, color = SubText)
                    Slider(
                        value = discountRate.toFloat(),
                        onValueChange = { v: Float -> discountRate = v.toDouble() },
                        valueRange = 8f..25f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryTeal, activeTrackColor = PrimaryTeal)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundNew)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hesaplanan DCF Hissedışı Değeri:", style = MaterialTheme.typography.bodyMedium, color = InkText)
                    Text(
                        text = "₺${String.format(Locale.US, "%.2f", dcfValue)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }
        }
    }
}

@Composable
fun DuPontAnalysisCard(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val dupont = CompanyAnalysisHelper.getDuPont(symbol)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔬", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DuPont ROE Ayrıştırması (3 Bileşen)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryTeal.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ROE: %${String.format(Locale.US, "%.1f", dupont.roe)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Text(
                text = "DuPont Formülü: ROE = Net Kar Marjı × Varlık Devir Hızı × Finansal Kaldıraç",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DuPontComponentBox(label = "Kar Marjı", value = "%${String.format(Locale.US, "%.1f", dupont.netProfitMargin)}", modifier = Modifier.weight(1f))
                DuPontComponentBox(label = "Varlık Devir Hızı", value = String.format(Locale.US, "%.2fx", dupont.assetTurnover), modifier = Modifier.weight(1f))
                DuPontComponentBox(label = "Kaldıraç Çarpanı", value = String.format(Locale.US, "%.2fx", dupont.equityMultiplier), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DuPontComponentBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundNew)
            .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = SubText, fontSize = 10.sp, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = IBMPlexMono)
        }
    }
}

@Composable
fun PeerComparisonCard(
    symbol: String,
    market: String,
    currentPrice: Double,
    sector: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📊", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sektörel Akran Karşılaştırması ($sector)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PeerRow(sym = symbol, pe = 8.4, div = 4.2, mc = "₺142M", isSelf = true)
                PeerRow(sym = "AKBNK", pe = 4.2, div = 5.1, mc = "₺280M", isSelf = false)
                PeerRow(sym = "GARAN", pe = 4.8, div = 4.8, mc = "₺310M", isSelf = false)
                PeerRow(sym = "ISCTR", pe = 3.9, div = 6.0, mc = "₺240M", isSelf = false)
            }
        }
    }
}

@Composable
fun PeerRow(sym: String, pe: Double?, div: Double?, mc: String, isSelf: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelf) PrimaryTeal.copy(alpha = 0.15f) else BackgroundNew)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sym,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelf) FontWeight.ExtraBold else FontWeight.Bold),
            color = if (isSelf) PrimaryTeal else InkText,
            fontFamily = IBMPlexMono
        )
        Text(
            text = "F/K: ${pe?.let { String.format(Locale.US, "%.1f", it) } ?: "-"}",
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
            fontFamily = IBMPlexMono
        )
        Text(
            text = "Temettü: %${div?.let { String.format(Locale.US, "%.1f", it) } ?: "-"}",
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
            fontFamily = IBMPlexMono
        )
        Text(
            text = mc,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = InkText,
            fontFamily = IBMPlexMono
        )
    }
}

@Composable
fun FinancialStatementsTabSection(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val hash = kotlin.math.abs(symbol.hashCode())
    val rev = 12500.0 + (hash % 45000)
    val ebitda = rev * 0.22
    val netInc = rev * 0.14
    val assets = rev * 1.8
    val equity = assets * 0.45

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📑", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Özet Finansal Tablolar (Son Çeyrek)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }

            HorizontalDivider(color = LineBorder)

            FinancialRow(label = "Net Satış Gelirleri", value = rev, unit = "₺", format = "%.0f")
            FinancialRow(label = "FAVÖK (EBITDA)", value = ebitda, unit = "₺", format = "%.0f")
            FinancialRow(label = "Net Dönem Karı", value = netInc, unit = "₺", format = "%.0f")
            FinancialRow(label = "Toplam Varlıklar", value = assets, unit = "₺", format = "%.0f")
            FinancialRow(label = "Özkaynaklar", value = equity, unit = "₺", format = "%.0f")
        }
    }
}
