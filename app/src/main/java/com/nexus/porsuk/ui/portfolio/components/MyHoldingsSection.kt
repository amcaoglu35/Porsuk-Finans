package com.nexus.porsuk.ui.portfolio.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.PortfolioAsset
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.theme.*

@Composable
fun MyHoldingsSection(
    onStockClick: (String, String) -> Unit,
    holdings: List<PortfolioAsset>,
    numberFormat: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💼", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Varlıklarım",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Varlık",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    "Adet",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Değer",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.End
                )
                Text(
                    "Günlük",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.End
                )
                Text(
                    "Toplam Getiri",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            holdings.forEach { item ->
                val uiItem = HoldingItem(
                    symbol = item.symbol,
                    name = item.name,
                    qty = String.format("%.0f", item.quantity),
                    totalValue = CurrencyFormatter.formatTRY(item.totalValue, numberFormat),
                    avgCost = "Maliyet: ${CurrencyFormatter.formatTRY(item.averageCost, numberFormat)}",
                    dailyChangePct = "%0.0",
                    dailyChangeAmt = "₺0",
                    totalReturnPct = "%${String.format("%.1f", item.profitPercent)}",
                    totalReturnAmt = CurrencyFormatter.formatTRY(item.profitLoss, numberFormat),
                    isDailyPositive = true
                )
                HoldingRowItem(item = uiItem, onClick = { onStockClick(item.symbol, "BIST") })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tüm Varlıkları Gör",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

private data class HoldingItem(
    val symbol: String,
    val name: String,
    val qty: String,
    val totalValue: String,
    val avgCost: String,
    val dailyChangePct: String,
    val dailyChangeAmt: String,
    val totalReturnPct: String,
    val totalReturnAmt: String,
    val isDailyPositive: Boolean
)

@Composable
private fun HoldingRowItem(item: HoldingItem, onClick: () -> Unit) {
    val dailyColor = if (item.isDailyPositive) PozitifGreen else NegatifRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Symbol Logo Badge
        Row(modifier = Modifier.weight(1.3f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        item.symbol.take(2),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    item.symbol,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    item.name,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Qty
        Text(
            item.qty,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center
        )

        // Value
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(
                item.totalValue,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                item.avgCost,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Daily Change
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(
                item.dailyChangePct,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                color = dailyColor
            )
            Text(
                item.dailyChangeAmt,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono),
                color = dailyColor
            )
        }

        // Total Return
        Column(modifier = Modifier.weight(1.1f), horizontalAlignment = Alignment.End) {
            Text(
                item.totalReturnPct,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono),
                color = PozitifGreen
            )
            Text(
                item.totalReturnAmt,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = IBMPlexMono),
                color = PozitifGreen
            )
        }
    }
}
