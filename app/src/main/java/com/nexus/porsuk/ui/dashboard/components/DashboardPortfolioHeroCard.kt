package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun DashboardPortfolioCard(
    totalBalance: Double,
    totalChange: Double,
    isBalanceVisible: Boolean,
    onToggleBalance: () -> Unit,
    numberFormat: String,
    onLedgerClick: () -> Unit,
    totalGainValue: Double = 0.0,
    totalGainPercent: Double = 0.0,
    annualGainValue: Double = 0.0,
    annualGainPercent: Double = 0.0,
    riskScore: Int = 68,
    aiHealthScore: Int = 85
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    val displayValue = remember(totalBalance, isBalanceVisible, numberFormat) {
        if (isBalanceVisible) CurrencyFormatter.formatTRY(totalBalance, numberFormat) else "₺••••••••"
    }

    val changeTL = remember(totalBalance, totalChange) {
        if (totalChange != 0.0) totalBalance - (totalBalance / (1.0 + totalChange / 100.0)) else 0.0
    }
    val isChangePos = totalChange >= 0
    val changeFormattedTL = CurrencyFormatter.formatTRY(Math.abs(changeTL), numberFormat)
    val changeText = if (isChangePos) {
        "^ %${String.format(Locale.US, "%.2f", totalChange)} ($changeFormattedTL) Bugün"
    } else {
        "v %${String.format(Locale.US, "%.2f", Math.abs(totalChange))} ($changeFormattedTL) Bugün"
    }
    val changeColor = if (isChangePos) PozitifGreen else NegatifRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = primaryColor.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Toplam Portföy Değeri",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = onSurfaceVariant,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Gizle/Göster",
                        tint = onSurfaceVariant,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(onClick = onToggleBalance)
                    )
                }

                Button(
                    onClick = onLedgerClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryContainer),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("İşlem Defteri", fontSize = 10.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = IBMPlexMono,
                            fontSize = if (displayValue.length > 12) 22.sp else 26.sp
                        ),
                        color = onSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = changeText,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                        color = changeColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Gradient Area Line Chart
                val sparkValues = remember(totalChange) {
                    if (totalChange >= 0) listOf(40f, 42f, 41f, 45f, 44f, 48f, 50f, 55f, 53f, 60f)
                    else listOf(60f, 55f, 53f, 50f, 48f, 44f, 45f, 41f, 42f, 40f)
                }
                Sparkline(
                    values = sparkValues,
                    color = changeColor,
                    modifier = Modifier
                        .width(80.dp)
                        .height(44.dp),
                    filled = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = outlineColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(16.dp))

            // Enlarged Risk Skoru & AI Sağlık Puanı Rings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalGainFormatted = CurrencyFormatter.formatTRY(totalGainValue, numberFormat)
                val totalGainPctStr = if (totalGainPercent >= 0) "^ %${String.format(Locale.US, "%.2f", totalGainPercent)}"
                else "v %${String.format(Locale.US, "%.2f", Math.abs(totalGainPercent))}"
                PortfolioMetricItem(title = "Toplam Getiri", value = totalGainFormatted, pct = totalGainPctStr, isPos = totalGainPercent >= 0)

                val annualGainFormatted = CurrencyFormatter.formatTRY(annualGainValue, numberFormat)
                val annualGainPctStr = if (annualGainPercent >= 0) "^ %${String.format(Locale.US, "%.2f", annualGainPercent)}"
                else "v %${String.format(Locale.US, "%.2f", Math.abs(annualGainPercent))}"
                PortfolioMetricItem(title = "Getiri (Yıl)", value = annualGainFormatted, pct = annualGainPctStr, isPos = annualGainPercent >= 0)

                val riskLabel = if (riskScore > 75) "Yüksek" else if (riskScore > 40) "Orta" else "Düşük"
                val riskColor = if (riskScore > 75) NegatifRed else if (riskScore > 40) AmberWarning else PozitifGreen
                EnlargedRingItem(title = "Risk Skoru", score = "$riskScore", label = riskLabel, color = riskColor)

                val healthLabel = if (aiHealthScore > 75) "İyi" else if (aiHealthScore > 40) "Orta" else "Zayıf"
                val healthColor = if (aiHealthScore > 75) PozitifGreen else if (aiHealthScore > 40) AmberWarning else NegatifRed
                EnlargedRingItem(title = "AI Sağlık", score = "$aiHealthScore", label = healthLabel, color = healthColor)
            }
        }
    }
}

@Composable
private fun PortfolioMetricItem(title: String, value: String, pct: String, isPos: Boolean) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = onSurfaceColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(pct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = if (isPos) PozitifGreen else NegatifRed)
    }
}

@Composable
private fun EnlargedRingItem(title: String, score: String, label: String, color: Color) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = onSurfaceVariant, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 3.5.dp.toPx()
                    drawArc(
                        color = color.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(score, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp), color = onSurfaceColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color, fontFamily = Manrope)
        }
    }
}
