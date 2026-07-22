package com.nexus.porsuk.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

private val TextPrimary = InkText
private val TextMuted = SubText
private val BorderLine = LineBorder
private val Background = BackgroundNew
private val Surface = CardNew
private val PositiveGreen = PrimaryTeal
private val NegativeRed = NegatifRed
private val Aqua = PrimaryTeal
private val AquaLight = TealSoft

// Şık grafik renk paleti
private val ChartColors = listOf(
    Color(0xFF0E9AA8), // Aqua/Turkuaz
    Color(0xFF3B82F6), // Mavi
    Color(0xFF8B5CF6), // Mor
    Color(0xFFEC4899), // Pembe
    Color(0xFFF59E0B), // Turuncu
    Color(0xFF10B981), // Yeşil
    Color(0xFFEF4444)  // Kırmızı
)

@Composable
fun PortfolioPieChart(
    sectorData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (sectorData.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Sektörel dağılım verisi bulunamadı.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
        return
    }

    // Toplam değeri hesaplayıp yüzdeleri çıkarıyoruz
    val total = sectorData.sumOf { it.second }
    val segments = sectorData.mapIndexed { index, (sector, value) ->
        val percentage = if (total > 0) (value / total * 100) else 0.0
        val color = ChartColors[index % ChartColors.size]
        PieSegment(
            name = sector,
            percentage = percentage.toFloat(),
            color = color
        )
    }.sortedByDescending { it.percentage }

    // Grafik çizim animasyonu için state
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSweep by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Halka (Donut) Grafiğin Çizimi
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 12.dp.toPx()
                    // Draw a subtle background track ring
                    drawArc(
                        color = BorderLine.copy(alpha = 0.4f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                    
                    var startAngle = -90f
                    val gapAngle = if (segments.size > 1) 3f else 0f
                    
                    segments.forEach { segment ->
                        val sweepAngle = segment.percentage / 100f * 360f
                        if (sweepAngle > gapAngle) {
                            drawArc(
                                color = segment.color,
                                startAngle = startAngle + gapAngle / 2f,
                                sweepAngle = (sweepAngle - gapAngle) * animateSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                            )
                        }
                        startAngle += sweepAngle
                    }
                }
                
                // Halkanın ortasındaki açıklama metni
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Dağılım",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Sektör",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Sağ Taraf Lejant (Sektör Renk ve İsimleri)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                segments.take(5).forEach { segment ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(segment.color, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = segment.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%%%.1f", segment.percentage),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextMuted
                        )
                    }
                }
                
                if (segments.size > 5) {
                    val remainingSum = segments.drop(5).sumOf { it.percentage.toDouble() }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.LightGray, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Diğer",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%%%.1f", remainingSum),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

private data class PieSegment(
    val name: String,
    val percentage: Float,
    val color: Color
)
