package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.MarketQuote

private val PositiveGreen = Color(0xFF00C853)
private val NegativeRed = Color(0xFFD50000)

/**
 * Porsuk Company Detail Module — Canlı Fiyat Kartı (CompanyPriceCard)
 *
 * Güncel Fiyat, Günlük % Değişim, Açılış, Kapanış, En Yüksek, En Düşük, Hacim ve Piyasa Değerini sunar.
 */
@Composable
fun CompanyPriceCard(
    quote: MarketQuote?,
    modifier: Modifier = Modifier
) {
    val price = quote?.lastPrice ?: 0.0
    val changeAmt = quote?.dailyChange ?: 0.0
    val changePct = quote?.dailyChangePct ?: 0.0
    val currency = quote?.currency ?: "TRY"

    val changeColor = when {
        changePct > 0 -> PositiveGreen
        changePct < 0 -> NegativeRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val changePrefix = if (changePct > 0) "+" else ""

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Fiyat ve Günlük Değişim Başlık Alanı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Güncel Fiyat",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$price $currency",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = changeColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$changePrefix$changeAmt ($changePrefix${String.format("%.2f", changePct)}%)",
                            color = changeColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Fiyat Metrikleri Izgarası (Açılış, Kapanış, Yüksek, Düşük, Hacim, Piyasa Değeri)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("Açılış", "${quote?.open ?: "-"} $currency")
                MetricColumn("Günlük En Yüksek", "${quote?.high ?: "-"} $currency")
                MetricColumn("Günlük En Düşük", "${quote?.low ?: "-"} $currency")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("Önc. Kapanış", "${quote?.previousClose ?: "-"} $currency")
                MetricColumn("Hacim", formatLargeNumber(quote?.volume?.toDouble()))
                MetricColumn("Piyasa Değeri", formatLargeNumber(quote?.marketCap))
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatLargeNumber(valNum: Double?): String {
    if (valNum == null || valNum == 0.0) return "-"
    return when {
        valNum >= 1_000_000_000 -> String.format("%.2f Milyar", valNum / 1_000_000_000.0)
        valNum >= 1_000_000 -> String.format("%.1f Milyon", valNum / 1_000_000.0)
        else -> String.format("%.0f", valNum)
    }
}
