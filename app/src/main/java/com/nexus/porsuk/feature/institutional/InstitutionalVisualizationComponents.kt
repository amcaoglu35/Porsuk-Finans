package com.nexus.porsuk.feature.institutional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.*

/**
 * 1. Sahiplik Dağılım Çizelgesi & Konsolidasyon (Ownership Chart)
 */
@Composable
fun OwnershipBreakdownCard(breakdown: OwnershipBreakdown?) {
    if (breakdown == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Şirket Sahiplik Dağılımı (Ownership Structure)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Halka açık paylar vs Kurumsal / Yönetici Sahipliği",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segment Horizontal Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight((breakdown.institutionalOwnershipPct / 100.0).toFloat())
                        .background(Color(0xFF1E88E5), RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight((breakdown.insiderOwnershipPct / 100.0).toFloat())
                        .background(Color(0xFF43A047))
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight((breakdown.retailOwnershipPct / 100.0).toFloat())
                        .background(Color(0xFFFB8C00), RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF1E88E5), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kurumsal: %${breakdown.institutionalOwnershipPct}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF43A047), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Insider: %${breakdown.insiderOwnershipPct}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFB8C00), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bireysel/Diğer: %${breakdown.retailOwnershipPct}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "HHI Yoğunlaşma İndeksi: ${breakdown.hhiConcentrationIndex} (Dengeli Konsolidasyon 🟢)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 2. Net Insider Aktivite Kartı
 */
@Composable
fun NetInsiderActivityCard(netActivity: NetInsiderActivity?) {
    if (netActivity == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🕵️ Net Insider Aktivite Dengesi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = netActivity.netSentiment,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Toplam Alım:", style = MaterialTheme.typography.bodySmall)
                    Text("₺${netActivity.totalBuyValue / 1_000_000.0}M (${netActivity.buyCount} İşlem)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
                }

                Column {
                    Text("Toplam Satış:", style = MaterialTheme.typography.bodySmall)
                    Text("₺${netActivity.totalSellValue / 1_000_000.0}M (${netActivity.sellCount} İşlem)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }

                Column {
                    Text("Net Akış:", style = MaterialTheme.typography.bodySmall)
                    Text("₺${netActivity.netValue / 1_000_000.0}M", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * 3. Smart Money Akış Heatmap Kartı
 */
@Composable
fun SmartMoneyFlowCard(flow: SmartMoneyFlowSummary?) {
    if (flow == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚡ Akıllı Para Baskısı & Birikim Skorları (Smart Money)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alım Baskısı Skoru:", style = MaterialTheme.typography.bodySmall)
                Text("${flow.buyingPressureScore} / 100 🔥", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Birikim Skoru (Accumulation):", style = MaterialTheme.typography.bodySmall)
                Text("${flow.accumulationScore} / 100 📈", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF43A047))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Genel Kurumsal Skor:", style = MaterialTheme.typography.bodySmall)
                Text("${flow.overallInstitutionalScore} / 100", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 4. Balina Bildirimi Uyarısı (Whale Alert Card)
 */
@Composable
fun WhaleAlertCard(alert: WhaleAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.0f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🐋 ${alert.fundOrWhaleName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = alert.companySymbol,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(alert.actionDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(alert.timestampDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${alert.transactionAmountUsd / 1_000_000.0}M",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
