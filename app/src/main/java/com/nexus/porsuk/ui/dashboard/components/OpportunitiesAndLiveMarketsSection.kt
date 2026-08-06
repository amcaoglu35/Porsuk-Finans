package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.dashboard.LiveMarketUiModel
import com.nexus.porsuk.ui.dashboard.OpportunityUiModel
import com.nexus.porsuk.ui.theme.*

@Composable
fun DailyOpportunitiesSection(
    opportunities: List<OpportunityUiModel> = emptyList(),
    onStockClick: (String, String) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline

    val displayOpportunities = remember(opportunities) {
        if (opportunities.isNotEmpty()) opportunities else listOf(
            OpportunityUiModel("ASELS", "Aselsan", "₺56,70", "%4,25", "Güçlü Alım", true),
            OpportunityUiModel("THYAO", "Türk Hava Yolları", "₺305,25", "%2,87", "Alım Sinyali", true),
            OpportunityUiModel("KCHOL", "Koç Holding", "₺182,40", "%0,31", "Nötr", true),
            OpportunityUiModel("AKBNK", "Akbank", "₺52,15", "-%0,42", "Dikkat", false)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("🔥 Günün Fırsatları", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
            Spacer(modifier = Modifier.height(12.dp))

            displayOpportunities.forEach { item ->
                val signalColor = when {
                    item.signal.contains("Alım") -> PozitifGreen
                    item.signal.contains("Dikkat") -> NegatifRed
                    else -> AmberWarning
                }
                OpportunityRowItem(
                    item = item,
                    signalColor = signalColor,
                    onClick = { onStockClick(item.code, "BIST") }
                )
                HorizontalDivider(color = outlineColor.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun OpportunityRowItem(
    item: OpportunityUiModel,
    signalColor: Color,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stock Logo Badge
        Surface(
            shape = CircleShape,
            color = primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(item.code.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = primaryColor)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.code, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
            Text(item.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = onSurfaceVariant)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = onSurfaceColor)
            Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono, fontSize = 9.sp), color = if (item.isPositive) PozitifGreen else NegatifRed)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // AI Signal Tag Badge
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = signalColor.copy(alpha = 0.12f)
        ) {
            Text(
                item.signal,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = signalColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun LiveMarketsOverviewSection(
    liveMarkets: List<LiveMarketUiModel> = emptyList(),
    onMarketsClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    val displayMarkets = remember(liveMarkets) {
        if (liveMarkets.isNotEmpty()) liveMarkets else listOf(
            LiveMarketUiModel("BIST 100", "10.456,87", "%1,35", true, listOf(40f, 42f, 45f, 48f, 50f)),
            LiveMarketUiModel("USD/TRY", "32,65", "%0,42", true, listOf(32f, 32.2f, 32.4f, 32.65f)),
            LiveMarketUiModel("EUR/USD", "1,0850", "-%0,15", false, listOf(1.09f, 1.088f, 1.085f)),
            LiveMarketUiModel("ALTIN/GR", "2.395,45", "%0,31", true, listOf(2380f, 2390f, 2395f)),
            LiveMarketUiModel("BRENT", "84,20", "%0,75", true, listOf(82f, 83f, 84.2f)),
            LiveMarketUiModel("BITCOIN", "67.450,00", "%2,10", true, listOf(65000f, 66000f, 67450f))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌐 Piyasalar Özet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
                Text("Tüm Piyasalar >", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = primaryColor, modifier = Modifier.clickable(onClick = onMarketsClick))
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 12.dp)
            ) {
                items(displayMarkets, key = { it.title }) { item ->
                    val color = if (item.isPos) PozitifGreen else NegatifRed
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.dp, outlineColor),
                        modifier = Modifier
                            .width(118.dp)
                            .clickable(onClick = onMarketsClick)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(item.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = onSurfaceColor)
                            Text(item.price, style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono, fontSize = 9.5.sp), color = onSurfaceVariant)
                            Text(item.change, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono, fontSize = 9.sp), color = color)

                            Spacer(modifier = Modifier.height(4.dp))
                            Sparkline(
                                values = item.sparkValues,
                                color = color,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                filled = true
                            )
                        }
                    }
                }
            }
        }
    }
}
