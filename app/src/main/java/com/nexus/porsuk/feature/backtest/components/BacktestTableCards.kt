package com.nexus.porsuk.feature.backtest.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.BacktestReport
import com.nexus.porsuk.domain.model.BacktestTradeLog

/**
 * Benchmark & Buy-and-Hold Karşılaştırma Kartı (BenchmarkComparisonCard)
 */
@Composable
fun BenchmarkComparisonCard(
    report: BacktestReport?,
    modifier: Modifier = Modifier
) {
    val stratReturn = report?.metrics?.totalReturnPct ?: 0.0
    val buyHoldReturn = report?.buyAndHoldReturnPct ?: 0.0
    val alphaDiff = stratReturn - buyHoldReturn

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚔️ Strateji vs Benchmark Karşılaştırması",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Strateji Getirisi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+%$stratReturn", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                }

                Column {
                    Text("Buy & Hold (${report?.benchmarkSymbol})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+%$buyHoldReturn", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }

                Column {
                    Text("Alfa (Fark)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+%$alphaDiff", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                }
            }
        }
    }
}

/**
 * İşlem Günlüğü Tablosu Kartı (TradeLogTableCard)
 */
@Composable
fun TradeLogTableCard(
    tradeLogs: List<BacktestTradeLog>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📜 İşlem Günlüğü (${tradeLogs.size} İşlem)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            tradeLogs.forEach { trade ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${trade.symbol} (${trade.orderType.displayName})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Giriş: $${trade.entryPrice} • Çıkış: $${trade.exitPrice} (${trade.durationDays} Gün)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "+$${trade.netProfitUsd} (+%${trade.returnPct})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853)
                    )
                }
            }
        }
    }
}
