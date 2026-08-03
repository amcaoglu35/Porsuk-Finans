package com.nexus.porsuk.ui.fund.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.DistributionDonut
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.fund.HoldingUiModel
import com.nexus.porsuk.ui.fund.RebalanceSuggestion
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun HoldingItem(
    holding: HoldingUiModel,
    market: String,
    numberFormat: String = "TR",
    logoUrl: String? = null,
    initials: String = "",
    onClick: () -> Unit
) {
    val color = if (holding.changePercent >= 0) PrimaryTeal else NegatifRed
    val glowColor = if (holding.changePercent >= 0) Color(0xFF00C896) else Color(0xFFFF4B4B)

    // Animate allocation bar
    val targetProgress = (holding.allocationPercent / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.nexus.porsuk.ui.common.StockLogoBadge(
                    logoUrl = logoUrl,
                    initials = initials,
                    sectorColor = com.nexus.porsuk.ui.common.getSectorColor(holding.symbol),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(holding.symbol, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = Manrope, color = InkText)
                    Text(
                        String.format(Locale.US, "%.0f adet · %s%,.2f maliyet", holding.quantity, CurrencyFormatter.getCurrencySymbol(market), holding.buyPrice),
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }

                // Sparkline
                val changeVal = holding.changePercent
                val sparkPoints = remember(holding.symbol, changeVal) {
                    val list = mutableListOf<Float>()
                    var current = 50f
                    list.add(current)
                    val step = (changeVal.toFloat() / 7f) * 8f
                    for (i in 1..6) {
                        current += step + kotlin.random.Random.nextFloat() * 8f - 4f
                        list.add(current.coerceIn(10f, 90f))
                    }
                    list
                }
                Sparkline(
                    values = sparkPoints,
                    color = color,
                    modifier = Modifier.size(46.dp, 20.dp).padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        CurrencyFormatter.formatWithSymbol(holding.currentValue, CurrencyFormatter.getCurrencySymbol(market), numberFormat),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.Bold,
                            color = InkText
                        )
                    )

                    val profitLossAmount = holding.currentValue - (holding.quantity * holding.buyPrice)
                    val profitLossStr = CurrencyFormatter.formatWithSymbol(profitLossAmount, CurrencyFormatter.getCurrencySymbol(market), numberFormat)
                    val sign = if (profitLossAmount > 0) "+" else ""
                    Text(
                        "$sign$profitLossStr (${NumberFormatter.formatPercentage(holding.changePercent, numberFormat)})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Neon allocation progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BackgroundNew)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        glowColor.copy(alpha = 0.6f),
                                        glowColor
                                    )
                                )
                            )
                            .drawBehind {
                                // Soft neon glow
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color.Transparent, glowColor.copy(alpha = 0.4f))
                                    ),
                                    topLeft = Offset(0f, -4f),
                                    size = size.copy(height = size.height + 8f)
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    String.format(Locale.US, "%.1f%%", holding.allocationPercent),
                    fontSize = 10.sp,
                    fontFamily = IBMPlexMono,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllocationDonutCard(holdings: List<HoldingUiModel>) {
    val colors = listOf(PrimaryTeal, AquaNew, Color(0xFF6DE0EE), Color(0xFFFFB454), Color(0xFFC7D6DB))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Dağılım", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), fontFamily = Manrope, color = InkText)
            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                DistributionDonut(
                    segments = holdings.mapIndexed { index, holding ->
                        holding.allocationPercent to colors[index % colors.size]
                    },
                    trackColor = BackgroundNew,
                    modifier = Modifier.size(76.dp)
                )

                Spacer(modifier = Modifier.width(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    holdings.take(5).forEachIndexed { index, holding ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors[index % colors.size]))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                holding.symbol,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope,
                                color = InkText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "%${(holding.allocationPercent * 100).toInt()}",
                                fontSize = 12.sp,
                                color = SubText,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Rebalance Suggestion Badge / Card (Yeniden Dengele Önerisi Rozeti)
 * Sepet varlıklarının hedef dağılımdan sapma durumlarını gösteren görsel öneri kartı.
 */
@Composable
fun RebalanceSuggestionCard(suggestions: List<RebalanceSuggestion>) {
    if (suggestions.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⚖️", fontSize = 18.sp)
                Text(
                    text = "Yeniden Dengeleme Önerisi",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AmberWarning,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = AmberWarning.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${suggestions.size} Hisse Sapmalı",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberWarning,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Text(
                text = "Sepetinizin mevcut ağırlıkları hedef dağılımdan %5 üzeri sapma göstermektedir:",
                style = MaterialTheme.typography.bodySmall,
                color = SubText,
                fontFamily = Manrope
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                suggestions.forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (suggestion.isOverweight) NegatifRed else PrimaryTeal)
                            )
                            Text(
                                text = suggestion.symbol,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = InkText,
                                fontFamily = IBMPlexMono
                            )
                        }

                        Text(
                            text = suggestion.description.substringAfter("${suggestion.symbol}: "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (suggestion.isOverweight) NegatifRed else PrimaryTeal,
                            fontFamily = Manrope
                        )
                    }
                }
            }
        }
    }
}
