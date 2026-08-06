package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.AmberWarning
import com.nexus.porsuk.ui.theme.IBMPlexMono
import com.nexus.porsuk.ui.theme.Manrope
import com.nexus.porsuk.ui.theme.PozitifGreen

@Composable
fun AiMarketSummaryProminentCard(
    onDetailClick: () -> Unit,
    commentText: String = "\"Bankacılık ve savunma sektöründe pozitif görünüm devam ediyor. Portföy dengesi olumlu.\"",
    marketScore: String = "78",
    confidence: String = "%85",
    riskLevel: String = "Düşük",
    fearGreedIndex: String = "55 Nötr",
    marketPulse: String = "68 Pozitif"
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "AI PİYASA ÖZETİ",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = primaryColor,
                        fontFamily = Manrope
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onDetailClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detaylar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = primaryColor, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = primaryColor, modifier = Modifier.size(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Short Comment
            Text(
                commentText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, lineHeight = 16.sp),
                color = onSurfaceColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Metric Pills Row (AI Market Score, AI Confidence, Risk, Fear&Greed, Market Pulse)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SummaryPillItem("Market Score", marketScore, PozitifGreen, modifier = Modifier.weight(1f))
                SummaryPillItem("Confidence", confidence, PozitifGreen, modifier = Modifier.weight(1f))
                SummaryPillItem("Risk", riskLevel, PozitifGreen, modifier = Modifier.weight(1f))
                SummaryPillItem("Korku/Açgöz.", fearGreedIndex, AmberWarning, modifier = Modifier.weight(1f))
                SummaryPillItem("Piyasa Nabzı", marketPulse, PozitifGreen, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryPillItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = primaryContainer,
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp, fontFamily = IBMPlexMono), color = color, maxLines = 1)
        }
    }
}

@Composable
fun OracleGlowHighlightCard(
    onOracleClick: () -> Unit,
    titleText: String = "Oracle Bugün Ne Diyor?",
    predictionText: String = "Piyasalarda pozitif momentum devam ediyor. 3 gün içinde yukarı yönlü hareket beklentisi %62.",
    confidenceScore: String = "%87"
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = primaryColor.copy(alpha = 0.3f))
            .clickable(onClick = onOracleClick),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E0A4C), Color(0xFF3B1578), primaryColor)
                )
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rotating Crystal Ball
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔮", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(titleText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        predictionText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(confidenceScore, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = Color(0xFFC084FC))
                    Text("Güven", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}
