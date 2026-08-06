package com.nexus.porsuk.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.ui.graphics.Brush

/**
 * Porsuk Vico Professional Portfolio History & Technical Chart Component
 */
@Composable
fun PortfolioHistoryChart(
    portfolioValues: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF6C4CF1)
) {
    if (portfolioValues.isEmpty()) return

    val entries = remember(portfolioValues) {
        portfolioValues.mapIndexed { index, pair ->
            FloatEntry(index.toFloat(), pair.second.toFloat())
        }
    }

    val chartModel = remember(entries) {
        entryModelOf(entries)
    }

    val lineSpec = remember(lineColor) {
        LineChart.LineSpec(
            lineColor = lineColor.hashCode(),
            backgroundShader = DynamicShaders.fromBrush(
                Brush.verticalGradient(
                    listOf(
                        lineColor.copy(alpha = 0.3f),
                        lineColor.copy(alpha = 0.0f)
                    )
                )
            )
        )
    }

    Chart(
        chart = lineChart(
            lines = listOf(lineSpec)
        ),
        model = chartModel,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}

/**
 * Vico Sparkline / Price History Chart for StockDetail, Orakul and Dashboard screens.
 */
@Composable
fun VicoPriceChart(
    prices: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF00C48C)
) {
    if (prices.isEmpty()) return

    val entries = remember(prices) {
        prices.mapIndexed { index, price ->
            FloatEntry(index.toFloat(), price.toFloat())
        }
    }

    val chartModel = remember(entries) {
        entryModelOf(entries)
    }

    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = lineColor.hashCode()
                )
            )
        ),
        model = chartModel,
        modifier = modifier
    )
}
