package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.feature.companydetail.*
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun TabAnalysisContent(
    valuationModules: List<ScoreCardData>,
    qualityModules: List<ScoreCardData>,
    riskModules: List<ScoreCardData>,
    scenarios: List<AiScenarioData>,
    targetPrice: Double,
    potential: Double,
    confidence: Double,
    summary: String,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // AI Detailed Report
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, LineBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Orakul AI Detaylı Analiz Raporu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                Spacer(modifier = Modifier.height(12.dp))
                if (summary.isBlank()) {
                    Text("Analiz raporu oluşturuluyor...", color = SubText, fontSize = 14.sp)
                } else {
                    MarkdownText(
                        markdown = summary,
                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = InkText, lineHeight = 20.sp, fontFamily = Manrope)
                    )
                }
            }
        }

        // Valuation Section
        AnalysisGridSection(title = "Değerleme Modelleri", items = valuationModules)
        
        // Quality Section
        AnalysisGridSection(title = "Kalite Metrikleri", items = qualityModules)
        
        // Risk Section
        AnalysisGridSection(title = "Risk Analizi", items = riskModules)
        
        // AI Scenarios
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Orakul AI Senaryoları", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                scenarios.forEach { scenario ->
                    ScenarioItem(scenario = scenario)
                    if (scenario != scenarios.last()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
        
        // Final Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = mainGreen),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "12 Ay Hedef Fiyat", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    Text(text = String.format("%.2f TL", targetPrice), style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Potansiyel", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    Text(text = String.format("%%%s%.1f", if (potential >= 0) "+" else "", potential), style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnalysisGridSection(title: String, items: List<ScoreCardData>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items.take(2).forEach { item ->
                ScoreCard(item = item, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items.drop(2).take(2).forEach { item ->
                ScoreCard(item = item, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ScoreCard(item: ScoreCardData, modifier: Modifier = Modifier) {
    val scoreColor = when {
        item.status == "Veri Yok" || item.score <= 0.0 -> Color(0xFF6B7280)
        item.score > 0.8 -> Color(0xFF14B88A)
        item.score > 0.5 -> Color(0xFFFFB800)
        else -> Color.Red
    }
    
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
            Text(text = item.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = scoreColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = item.status,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = scoreColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ScenarioItem(scenario: AiScenarioData) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = scenario.type, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = scenario.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = String.format("%.2f TL", scenario.targetPrice), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = String.format("Olasılık: %%%d", (scenario.probability * 100).toInt()), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
