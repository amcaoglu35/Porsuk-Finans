package com.nexus.porsuk.feature.backtest.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.BacktestMetrics
import com.nexus.porsuk.domain.model.EquityPoint

/**
 * Sermaye ve Drawdown Eğrisi Görselleştirme Kartı (EquityCurveCard)
 */
@Composable
fun EquityCurveCard(
    points: List<EquityPoint>,
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
                text = "📈 Sermaye Eğrisi (Equity Curve)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (points.isEmpty()) return@Canvas

                    val width = size.width
                    val height = size.height

                    val minEq = points.minOf { it.equityValue }
                    val maxEq = points.maxOf { it.equityValue }
                    val eqRange = if (maxEq - minEq > 0) maxEq - minEq else 1.0

                    val stepX = width / (points.size - 1)

                    for (i in 0 until points.size - 1) {
                        val p1 = points[i]
                        val p2 = points[i + 1]

                        val x1 = i * stepX
                        val y1 = height - ((p1.equityValue - minEq) / eqRange * height).toFloat()

                        val x2 = (i + 1) * stepX
                        val y2 = height - ((p2.equityValue - minEq) / eqRange * height).toFloat()

                        drawLine(
                            color = Color(0xFF00C853),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 4f
                        )
                    }
                }
            }
        }
    }
}

/**
 * 16 Finansal Performans Metriği Kartı (MetricsGridCard)
 */
@Composable
fun MetricsGridCard(
    metrics: BacktestMetrics,
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
                text = "📊 Performans ve Risk Metrikleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Toplam Getiri", "+%${metrics.totalReturnPct}", Color(0xFF00C853))
                MetricItem("Sharpe Oranı", "${metrics.sharpeRatio}", MaterialTheme.colorScheme.primary)
                MetricItem("Sortino Oranı", "${metrics.sortinoRatio}", MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Max Drawdown", "%${metrics.maxDrawdownPct}", Color(0xFFD50000))
                MetricItem("Win Rate", "%${metrics.winRatePct}", Color(0xFF00C853))
                MetricItem("Profit Factor", "${metrics.profitFactor}", MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
