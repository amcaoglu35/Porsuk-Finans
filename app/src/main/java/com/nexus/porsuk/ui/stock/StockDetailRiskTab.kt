package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CompanyAnalysisHelper
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun StockDetailRiskTab(
    symbol: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Piotroski F-Skoru Kartı
        PiotroskiScoreCard(symbol = symbol)

        // Altman Z-Skoru İflas Riski Kartı
        AltmanZScoreCard(symbol = symbol)

        // Temettü Güvenlik Skoru Kartı
        DividendSafetyCard(symbol = symbol)

        // AI Rekabetçi Üstünlük (Moat) Kartı
        AiMoatAnalysisCard(symbol = symbol)
    }
}

@Composable
fun PiotroskiScoreCard(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val piotroski = CompanyAnalysisHelper.getPiotroski(symbol)
    val piotroskiScore = piotroski.score

    val (scoreColor, scoreText) = if (piotroskiScore >= 7) {
        PozitifGreen to "GÜÇLÜ FİNANSAL SAĞLIK"
    } else if (piotroskiScore >= 4) {
        PrimaryTeal to "ORTA DÜZEY SAĞLIK"
    } else {
        NegatifRed to "ZAYIF FİNANSAL YAPILANMA"
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
                    Text("🛡️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Piotroski F-Skoru (9 Kriter)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(scoreColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$piotroskiScore/9 - $scoreText",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = scoreColor,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Text(
                text = "Karlılık, Kaldıraç/Likitite ve Operasyonel Verimlilik olmak üzere 9 muhasebe kriterini değerlendirir.",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                piotroski.criteria.forEach { criterion ->
                    PiotroskiCriterionRow(label = criterion.label, isMet = criterion.passed)
                }
            }
        }
    }
}

@Composable
private fun PiotroskiCriterionRow(label: String, isMet: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = SubText, fontFamily = Manrope)
        Icon(
            imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isMet) PozitifGreen else NegatifRed,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AltmanZScoreCard(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val altman = CompanyAnalysisHelper.getAltman(symbol)
    val zScore = altman.score

    val (zoneColor, zoneText) = if (zScore >= 2.99) {
        PozitifGreen to "GÜVENLİ BÖLGE (DÜŞÜK İFLAS RİSKİ)"
    } else if (zScore >= 1.81) {
        AmberWarning to "GRİ BÖLGE (DİKKATLE İZLENMELİ)"
    } else {
        NegatifRed to "RİSKLİ BÖLGE (FİNANSAL SIKIŞIKLIK)"
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
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Altman Z-Skoru İflas & İflas Riski",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(zoneColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "Z = %.2f", zScore),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = zoneColor,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Text(
                text = zoneText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = zoneColor,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun DividendSafetyCard(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val dividend = CompanyAnalysisHelper.getDividend(symbol)

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
                    Text("💰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Temettü Güvenliği & Dağıtım Oranı",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PozitifGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%${String.format(Locale.US, "%.1f", dividend.yield)} Verim",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PozitifGreen,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            FinancialRow(label = "Temettü Ödeme Oranı (Payout Ratio)", value = dividend.payoutRatio, unit = "%", format = "%.1f")
            FinancialRow(label = "Temettü Güvenlik Skoru", value = dividend.safetyScore.toDouble(), unit = "/100", format = "%.0f")
            Text(
                text = dividend.description,
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun AiMoatAnalysisCard(
    symbol: String,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏰", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Rekabetçi Hendek (Moat) Analizi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }
            Text(
                text = "Şirket yüksek marka değeri, geniş ağ etkisi ve güçlü maliyet avantajı ile sektörde 'Geniş Hendek (Wide Moat)' sınıfında yer almaktadır.",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )
        }
    }
}
