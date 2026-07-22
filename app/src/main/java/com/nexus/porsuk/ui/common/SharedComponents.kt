package com.nexus.porsuk.ui.common

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun SpeedDialFAB(
    onAddStockClick: () -> Unit,
    onAlarmClick: () -> Unit = {}
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = fabExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniFabAction(label = "Fiyat Alarmı", icon = Icons.Default.Notifications, onClick = { 
                    onAlarmClick()
                    fabExpanded = false 
                })
                MiniFabAction(label = "Hisse Ekle", icon = Icons.Default.AddChart, onClick = { 
                    onAddStockClick()
                    fabExpanded = false 
                })
            }
        }
        Spacer(Modifier.height(12.dp))
        FloatingActionButton(
            onClick = { fabExpanded = !fabExpanded },
            containerColor = Color.Transparent,
            contentColor = Color.White,
            shape = RoundedCornerShape(17.dp),
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryTeal, AquaNew)
                    ),
                    shape = RoundedCornerShape(17.dp)
                )
        ) {
            val rotation by animateFloatAsState(if (fabExpanded) 45f else 0f, label = "fabRotation")
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.rotate(rotation))
        }
    }
}

@Composable
fun MiniFabAction(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = InkText.copy(alpha = 0.8f))
        ) {
            Text(
                text = label,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Manrope
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = CardNew,
            contentColor = PrimaryTeal,
            shape = CircleShape
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DistributionDonut(
    segments: List<Pair<Float, Color>>, // fraction (0f..1f) - renk
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(76.dp)) {
        val stroke = Stroke(width = 4.dp.toPx())
        drawArc(trackColor, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = stroke)
        var start = -90f
        segments.forEach { (fraction, color) ->
            val sweep = 360f * fraction
            drawArc(color, startAngle = start, sweepAngle = sweep, useCenter = false, style = stroke)
            start += sweep
        }
    }
}

fun marketToFlag(market: String): String = when (market.uppercase()) {
    "BIST", "IST" -> "🇹🇷"
    "NASDAQ", "NYSE" -> "🇺🇸"
    "FRA", "EURONEXT" -> "🇪🇺"
    else -> "🌐"
}
