package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.feature.companydetail.AiOracleReport
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun TabAiOracleContent(
    report: AiOracleReport?
) {
    if (report == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
            Text("AI Oracle analizi hazırlanıyor veya finansal veri eksik.", color = SubText, fontSize = 14.sp)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // AI & Risk Scores
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ScoreCard("AI Skoru", "${report.aiScore}/100", PrimaryTeal, Modifier.weight(1f))
            ScoreCard("Risk Skoru", "${report.riskScore}/100", if (report.riskScore > 60) NegatifRed else Orange, Modifier.weight(1f))
        }

        // Recommendation & Fair Value
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LineBorder)
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Tavsiye", fontSize = 12.sp, color = SubText)
                    Text(report.recommendation, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = when(report.recommendation) {
                        "BUY" -> PrimaryTeal
                        "SELL" -> NegatifRed
                        else -> Orange
                    })
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Adil Değer", fontSize = 12.sp, color = SubText)
                    Text("${report.fairValue}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = InkText, fontFamily = IBMPlexMono)
                }
            }
        }

        // Investment Thesis
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LineBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Yatırım Tezi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkText)
                Spacer(modifier = Modifier.height(12.dp))
                MarkdownText(
                    markdown = report.investmentThesis,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = InkText, lineHeight = 20.sp)
                )
            }
        }
    }
}

@Composable
private fun ScoreCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = SubText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
