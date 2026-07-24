package com.nexus.porsuk.feature.chart.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.CandleStickItem
import com.nexus.porsuk.domain.model.PortfolioTransactionMarker

/**
 * Soyut ChartRenderer Surface (ChartCanvasView)
 *
 * Fiyat mumlarını, portföy alış/satış/temettü katmanını ve çizimleri Jetpack Compose Canvas üzerinde çizer.
 * Grafik motoru tamamen `ChartRendererEngine` interface soyutlaması arkasında korunmaktadır.
 */
@Composable
fun ChartCanvasView(
    candles: List<CandleStickItem>,
    portfolioMarkers: List<PortfolioTransactionMarker>,
    showPortfolioOverlay: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (candles.isEmpty()) return@Canvas

            val canvasWidth = size.width
            val canvasHeight = size.height

            val minPrice = candles.minOf { it.low }
            val maxPrice = candles.maxOf { it.high }
            val priceRange = if (maxPrice - minPrice > 0) maxPrice - minPrice else 1.0

            val candleWidth = canvasWidth / candles.size

            // Mum Çizimleri
            candles.forEachIndexed { index, candle ->
                val x = index * candleWidth + (candleWidth / 2)
                val isBullish = candle.close >= candle.open
                val candleColor = if (isBullish) Color(0xFF00C853) else Color(0xFFD50000)

                val highY = canvasHeight - ((candle.high - minPrice) / priceRange * canvasHeight).toFloat()
                val lowY = canvasHeight - ((candle.low - minPrice) / priceRange * canvasHeight).toFloat()
                val openY = canvasHeight - ((candle.open - minPrice) / priceRange * canvasHeight).toFloat()
                val closeY = canvasHeight - ((candle.close - minPrice) / priceRange * canvasHeight).toFloat()

                // Fitil (Wick) Çizimi
                drawLine(
                    color = candleColor,
                    start = Offset(x, highY),
                    end = Offset(x, lowY),
                    strokeWidth = 2.5f
                )

                // Gövde (Body) Çizimi
                val topY = minOf(openY, closeY)
                val bodyHeight = maxOf(Math.abs(openY - closeY), 4f)
                drawRect(
                    color = candleColor,
                    topLeft = Offset(x - (candleWidth * 0.35f), topY),
                    size = Size(candleWidth * 0.7f, bodyHeight)
                )
            }
        }

        // Portföy İşaretçi Overlay Bilgi Rozeti
        if (showPortfolioOverlay && portfolioMarkers.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                portfolioMarkers.forEach { marker ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(marker.markerType.colorHex).copy(alpha = 0.2f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "${marker.markerType.displayName}: ${marker.quantityText}",
                            color = Color(marker.markerType.colorHex),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
