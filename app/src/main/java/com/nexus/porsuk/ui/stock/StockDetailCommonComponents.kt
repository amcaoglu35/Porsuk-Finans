package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun IntervalSelector(
    selectedInterval: String,
    onIntervalSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val intervals = listOf("Dk", "S", "G", "A", "Y")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardNew)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        intervals.forEach { interval ->
            val isSelected = interval == selectedInterval
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) PrimaryTeal else Color.Transparent)
                    .clickable { onIntervalSelected(interval) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = interval,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = Manrope
                    ),
                    color = if (isSelected) BackgroundNew else SubText
                )
            }
        }
    }
}

@Composable
fun IndicatorChip(
    label: String,
    value: String,
    signal: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (signal.uppercase()) {
        "AL", "BULLISH", "GÜÇLÜ AL" -> PozitifGreen.copy(alpha = 0.15f) to PozitifGreen
        "SAT", "BEARISH", "GÜÇLÜ SAT" -> NegatifRed.copy(alpha = 0.15f) to NegatifRed
        else -> SubText.copy(alpha = 0.15f) to SubText
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = CardNew,
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = SubText,
                fontFamily = Manrope
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkText,
                    fontFamily = IBMPlexMono
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = signal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }
}

@Composable
fun SubScoreRow(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
            fontFamily = Manrope
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .width(80.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = LineBorder
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = InkText,
                fontFamily = IBMPlexMono
            )
        }
    }
}

@Composable
fun FinancialRow(
    label: String,
    value: Double,
    unit: String,
    format: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SubText,
            fontFamily = Manrope
        )
        Text(
            text = "${String.format(Locale.US, format, value)} $unit".trim(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = InkText,
            fontFamily = IBMPlexMono
        )
    }
}
