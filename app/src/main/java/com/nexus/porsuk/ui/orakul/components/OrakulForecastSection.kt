package com.nexus.porsuk.ui.orakul.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText

private val CardWhite = Color(0xFFFFFFFF)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val SuccessGreen = Color(0xFF00C48C)
private val ErrorRed = Color(0xFFF44336)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)

@Composable
fun StructuredForecastCard(
    streamingText: String = "",
    symbol: String = "XU100",
    sparklineValues: List<Float> = listOf(50f, 52f, 48f, 55f, 60f, 58f, 65f, 70f),
    bullCase: String? = null,
    bearCase: String? = null,
    consensusWeights: Map<String, Int> = mapOf(
        "Temel & İçsel Değer" to 30,
        "Adli Muhasebe & Sağlık" to 25,
        "Haber & Sentiment Entropisi" to 25,
        "İvme & Teknik Teyit" to 20
    )
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Symbol & Mini Sparkline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = PurpleSoftBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = symbol,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Violet,
                            fontFamily = IBMPlexMono
                        )
                    }
                    Text(
                        text = "Fiyat Hareket Sinyali",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = Manrope
                    )
                }

                // Sparkline integration
                Sparkline(
                    values = sparklineValues,
                    color = Violet,
                    modifier = Modifier.size(60.dp, 24.dp)
                )
            }

            if (streamingText.isNotBlank()) {
                MarkdownText(
                    markdown = streamingText,
                    style = androidx.compose.ui.text.TextStyle(
                        color = TextDark,
                        fontSize = 13.sp,
                        fontFamily = Manrope,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Orakul AI henüz yeni bir analiz çalıştırmadı. Yukarıdaki arama çubuğundan sembol seçin veya analiz modunu başlatın.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = Manrope,
                    lineHeight = 18.sp
                )
            }

            // Expandable Consensus Weight Breakdown
            ExpandableWeightBreakdown(consensusWeights = consensusWeights)

            // Bull / Bear Case Panels
            if (bullCase != null || bearCase != null) {
                BullBearCasePanels(bullCase = bullCase, bearCase = bearCase)
            }
        }
    }
}

/**
 * Genişletilebilir Ağırlık Dökümü (Expandable Weight Breakdown Accordion)
 */
@Composable
fun ExpandableWeightBreakdown(consensusWeights: Map<String, Int>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleSoftBg.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Violet.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🧩", fontSize = 16.sp)
                    Text(
                        text = "Konsensüs Ağırlık Dökümü",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Violet,
                        fontFamily = Manrope
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Aç/Kapat",
                    tint = Violet
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    consensusWeights.forEach { (moduleName, weightPct) ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(moduleName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark, fontFamily = Manrope)
                                Text("%$weightPct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Violet, fontFamily = IBMPlexMono)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(weightPct / 100f)
                                        .clip(CircleShape)
                                        .background(Violet)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bull & Bear Case Panels (Boğa / Ayı Senaryoları)
 */
@Composable
fun BullBearCasePanels(bullCase: String?, bearCase: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!bullCase.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🚀", fontSize = 16.sp)
                        Text(
                            text = "Boğa Senaryosu (Bull Case)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SuccessGreen,
                            fontFamily = Manrope
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bullCase,
                        fontSize = 12.sp,
                        color = TextDark,
                        fontFamily = Manrope,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (!bearCase.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🐻", fontSize = 16.sp)
                        Text(
                            text = "Ayı Senaryosu (Bear Case)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ErrorRed,
                            fontFamily = Manrope
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bearCase,
                        fontSize = 12.sp,
                        color = TextDark,
                        fontFamily = Manrope,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
