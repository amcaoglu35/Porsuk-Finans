package com.nexus.porsuk.ui.portfolio.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.remote.PortfolioDoctorMetrics
import com.nexus.porsuk.ui.theme.*

@Composable
fun AllocationsDonutGridSection(
    onAnalysisClick: () -> Unit,
    riskMetrics: PortfolioDoctorMetrics?
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Varlık Dağılımı Card
            DistributionCard(
                title = "Varlık Tipi",
                modifier = Modifier.weight(1f),
                segments = listOf(
                    AssetSegment("Hisse", 85f, "", primaryColor),
                    AssetSegment("Nakit", 15f, "", Color(0xFF3B82F6))
                )
            )

            // Ülke Dağılımı Card
            DistributionCard(
                title = "Ülke Dağılımı",
                modifier = Modifier.weight(1f),
                segments = riskMetrics?.countryBreakdown?.map { (country, pct) ->
                    AssetSegment(country, pct.toFloat(), "", when(country) {
                        "ABD (US)" -> PozitifGreen
                        "Avrupa (EU)" -> AmberWarning
                        else -> primaryColor
                    })
                } ?: emptyList()
            )
        }

        // Sektör Dağılımı Card (Full Width)
        SectorAllocationCard(
            modifier = Modifier.fillMaxWidth(),
            onAnalysisClick = onAnalysisClick,
            riskMetrics = riskMetrics
        )
    }
}

@Composable
private fun DistributionCard(
    title: String,
    segments: List<AssetSegment>,
    modifier: Modifier = Modifier
) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }

    val animProgress by animateFloatAsState(
        targetValue = if (animated) 1.0f else 0.0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "dist_donut_anim"
    )

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    var startAngle = -90f
                    segments.forEach { seg ->
                        val sweep = (seg.pct / 100f) * 360f * animProgress
                        drawArc(color = seg.color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, style = Stroke(width = strokeWidth))
                        startAngle += sweep
                    }
                }
                Text(
                    "%100",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            segments.take(2).forEach { seg ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(seg.color))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            seg.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Text(
                        "%${String.format("%.0f", seg.pct)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private data class AssetSegment(val label: String, val pct: Float, val amount: String, val color: Color)

@Composable
private fun SectorAllocationCard(
    modifier: Modifier = Modifier,
    onAnalysisClick: () -> Unit,
    riskMetrics: PortfolioDoctorMetrics?
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animated = true
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    val sectors = remember(riskMetrics, primaryColor) {
        riskMetrics?.sectorBreakdown?.map { (sector, pct) ->
            val color = when {
                sector.contains("Teknoloji") -> primaryColor
                sector.contains("Banka") -> Color(0xFF3B82F6)
                sector.contains("Savunma") -> PozitifGreen
                else -> AmberWarning
            }
            SectorSegment(sector, pct.toFloat(), "Ağırlık: %${String.format("%.1f", pct)}", color)
        } ?: emptyList()
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animated) 1.0f else 0.0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "sector_donut_anim"
    )

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                "Sektör Dağılımı",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Donut Chart Canvas
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
                    var startAngle = -90f

                    sectors.forEachIndexed { idx, sec ->
                        val sweep = (sec.pct / 100f) * 360f * animProgress
                        val isSelected = idx == selectedIndex
                        drawArc(
                            color = if (isSelected) sec.color else sec.color.copy(alpha = 0.4f),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = if (isSelected) strokeWidth + 4.dp.toPx() else strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Toplam",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "%100",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sector Legend List
            sectors.forEachIndexed { idx, sec ->
                DonutLegendRow(
                    color = sec.color,
                    label = sec.label,
                    value = "%${sec.pct}",
                    isSelected = idx == selectedIndex,
                    onClick = { selectedIndex = idx }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive AI Sector Insight Commentary Box
            if (sectors.isNotEmpty()) {
                val activeSector = sectors.getOrNull(selectedIndex) ?: sectors[0]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡 ", fontSize = 12.sp)
                        Text(
                            text = activeSector.aiInsight,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private data class SectorSegment(val label: String, val pct: Float, val aiInsight: String, val color: Color)

@Composable
private fun DonutLegendRow(
    color: Color,
    label: String,
    value: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontFamily = Manrope,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.5.sp, fontFamily = IBMPlexMono),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
