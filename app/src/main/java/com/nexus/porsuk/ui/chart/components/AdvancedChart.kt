package com.nexus.porsuk.ui.chart.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.CandleStickItem
import com.nexus.porsuk.domain.model.IndicatorType
import com.nexus.porsuk.ui.theme.*

@Composable
fun AdvancedChart(
    candles: List<CandleStickItem>,
    compareCandles: Map<String, List<CandleStickItem>> = emptyMap(),
    indicators: Map<IndicatorType, List<Double>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                }
            }
    ) {
        if (candles.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        
        // Calculate bounds including comparison data
        val allHighs = candles.map { it.high }.toMutableList()
        compareCandles.values.forEach { list -> allHighs.addAll(list.map { it.high }) }
        
        val allLows = candles.map { it.low }.toMutableList()
        compareCandles.values.forEach { list -> allLows.addAll(list.map { it.low }) }

        val maxPrice = allHighs.maxOf { it }
        val minPrice = allLows.minOf { it }
        val priceRange = maxPrice - minPrice
        
        val candleWidth = (width / candles.size) * scale
        
        // Draw primary candles
        candles.forEachIndexed { index, candle ->
            val x = index * candleWidth + offset.x
            
            val openY = (height - ((candle.open - minPrice) / priceRange * height)).toFloat()
            val closeY = (height - ((candle.close - minPrice) / priceRange * height)).toFloat()
            val highY = (height - ((candle.high - minPrice) / priceRange * height)).toFloat()
            val lowY = (height - ((candle.low - minPrice) / priceRange * height)).toFloat()
            
            val isBullish = candle.close >= candle.open
            val color = if (isBullish) EmeraldNew else NegatifRed
            
            drawLine(
                color = color,
                start = Offset(x + candleWidth / 2, highY),
                end = Offset(x + candleWidth / 2, lowY),
                strokeWidth = 1.dp.toPx()
            )
            
            val bodyHeight = kotlin.math.abs(closeY - openY).coerceAtLeast(1f)
            drawRect(
                color = color,
                topLeft = Offset(x + 2.dp.toPx(), minOf(openY, closeY)),
                size = Size(candleWidth - 4.dp.toPx(), bodyHeight)
            )
        }

        // Draw Comparison Lines
        compareCandles.forEach { (symbol, list) ->
            val color = when(symbol) {
                "THYAO" -> PrimaryTeal
                "BIMAS" -> Violet
                else -> Color.Blue
            }
            
            val points = list.mapIndexed { index, candle ->
                val x = index * candleWidth + offset.x + candleWidth / 2
                val y = (height - ((candle.close - minPrice) / priceRange * height)).toFloat()
                Offset(x, y)
            }
            
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i+1],
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Draw Indicators
        indicators.forEach { (type, data) ->
            val color = when(type) {
                IndicatorType.EMA -> Violet
                IndicatorType.SMA -> Color.Cyan
                else -> PrimaryTeal
            }
            
            val points = data.mapIndexed { index, value ->
                val x = index * candleWidth + offset.x + candleWidth / 2
                val y = (height - ((value - minPrice) / priceRange * height)).toFloat()
                Offset(x, y)
            }
            
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i+1],
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }
    }
}
