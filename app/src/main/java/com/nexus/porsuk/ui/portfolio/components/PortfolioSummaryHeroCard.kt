package com.nexus.porsuk.ui.portfolio.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.remote.PortfolioDoctorMetrics
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun PortfolioSummaryHeroCard(
    totalBalance: Double,
    totalChange: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    numberFormat: String,
    riskMetrics: PortfolioDoctorMetrics?
) {
    val displayValue = remember(totalBalance, isBalanceVisible) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
    }

    val dailyValueStr = remember(totalChange, riskMetrics, numberFormat) {
        val amt = (totalBalance * totalChange / 100.0)
        val sign = if (totalChange >= 0) "^" else "v"
        "$sign %${String.format("%.2f", totalChange)} (${CurrencyFormatter.formatTRY(amt, numberFormat)}) Bugün"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.04f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Toplam Portföy Değeri",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Gizle/Göster",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onToggleVisibility)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Günlük",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Balance Amount & Mini Sparkline Graph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = IBMPlexMono,
                            fontSize = 30.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dailyValueStr,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                            color = if (totalChange >= 0) PozitifGreen else NegatifRed
                        )
                    }
                }

                // Mini Sparkline Graph
                val mockSparkValues = emptyList<Float>()
                Sparkline(
                    values = mockSparkValues,
                    color = PozitifGreen,
                    modifier = Modifier
                        .width(110.dp)
                        .height(48.dp),
                    filled = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(18.dp))

            // 4 Metrics Row (Including Prominent Animated Gauges for Risk & Health)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricColumnItem(
                    title = "Toplam Kar/Zarar",
                    value = CurrencyFormatter.formatTRY(totalBalance * totalChange / 100.0, numberFormat),
                    percentage = "${if(totalChange >=0) "^" else "v"} %${String.format("%.1f", totalChange)}",
                    isPositive = totalChange >= 0
                )
                
                // Risk Skoru Gauge
                ProminentGaugeColumnItem(
                    title = "Risk Skoru",
                    score = riskMetrics?.healthScore ?: 0,
                    maxScore = 100,
                    label = riskMetrics?.concentrationRisk?.substringBefore(" ") ?: "Nötr",
                    color = when {
                        (riskMetrics?.healthScore ?: 0) > 80 -> PozitifGreen
                        (riskMetrics?.healthScore ?: 0) > 50 -> AmberWarning
                        else -> NegatifRed
                    }
                )

                // AI Sağlık Puanı Gauge
                ProminentGaugeColumnItem(
                    title = "AI Sağlık",
                    score = riskMetrics?.healthScore ?: 0,
                    maxScore = 100,
                    label = "Detay",
                    color = PozitifGreen
                )
            }
        }
    }
}

@Composable
private fun MetricColumnItem(title: String, value: String, percentage: String, isPositive: Boolean) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = Manrope
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 11.5.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = percentage,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 10.5.sp),
            color = if (isPositive) PozitifGreen else NegatifRed
        )
    }
}

@Composable
private fun ProminentGaugeColumnItem(
    title: String,
    score: Int,
    maxScore: Int,
    label: String,
    color: Color
) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animated = true
    }

    val targetSweep = (score.toFloat() / maxScore.toFloat()) * 360f
    val sweepAngle by animateFloatAsState(
        targetValue = if (animated) targetSweep else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_sweep_$title"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = Manrope
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 4.dp.toPx()
                    drawArc(
                        color = color.copy(alpha = 0.18f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
                color = color,
                fontFamily = Manrope
            )
        }
    }
}
