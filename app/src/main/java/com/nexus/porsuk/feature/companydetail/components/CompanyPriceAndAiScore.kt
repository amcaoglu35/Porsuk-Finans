package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.MarketQuote
import java.util.*

@Composable
fun CompanyPriceAndAiScore(
    quote: MarketQuote?,
    aiScore: Double,
    aiRecommendation: String,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)
    val price = quote?.lastPrice ?: 0.0
    val change = quote?.dailyChange ?: 0.0
    val changePct = quote?.dailyChangePct ?: 0.0
    val isPositive = change >= 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Price Info
        Column {
            Text(
                text = String.format(Locale.US, "%.2f %s", price, quote?.currency ?: "TRY"),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.US, "%s%.2f (%%%s%.2f)", 
                        if (isPositive) "+" else "", change,
                        if (isPositive) "+" else "", changePct),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) mainGreen else Color.Red
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sub-metrics
            Row {
                PriceSubMetric("Gün", String.format(Locale.US, "%.1f - %.1f", quote?.low ?: 0.0, quote?.high ?: 0.0))
                Spacer(modifier = Modifier.width(12.dp))
                PriceSubMetric("Hacim", "45.2M")
            }
        }

        // AI Score Circle
        Box(contentAlignment = Alignment.Center) {
            AiScoreCircle(score = aiScore)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = aiScore.toInt().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = mainGreen
                )
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Recommendation Label
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(mainGreen)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = aiRecommendation,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PriceSubMetric(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AiScoreCircle(score: Double) {
    val mainGreen = Color(0xFF14B88A)
    val backgroundGreen = mainGreen.copy(alpha = 0.1f)
    
    Canvas(modifier = Modifier.size(80.dp)) {
        drawArc(
            color = backgroundGreen,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = mainGreen,
            startAngle = -90f,
            sweepAngle = (score / 100 * 360).toFloat(),
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
