package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.feature.companydetail.*

/**
 * 1. Sekme — Genel Bilgiler (TabOverviewContent)
 */
@Composable
fun TabOverviewContent(
    company: com.nexus.porsuk.data.local.entity.CompanyEntity?,
    summary: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Şirket Özeti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                dev.jeziellago.compose.markdowntext.MarkdownText(
                    markdown = summary,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = InkText,
                        lineHeight = 22.sp,
                        fontFamily = Manrope
                    )
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

                InfoRow("ISIN", company?.isin ?: "N/A")
                InfoRow("Ülke", company?.country ?: "N/A")
                InfoRow("Sektör", company?.sector ?: "N/A")
                InfoRow("Endüstri", company?.industry ?: "N/A")
                InfoRow("Web", company?.website ?: "N/A")
            }
        }
    }
}

/**
 * 2. Sekme — Finansallar (TabFinancialsContent)
 */
@Composable
fun TabFinancialsContent(
    summary: FinancialSummaryData,
    quarterlyData: List<QuarterlyBarData>,
    marginData: List<MarginLineData>,
    healthData: FinancialHealthData,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)
    
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Özet Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Finansal Özet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Hasılat", summary.revenue)
                InfoRow("FAVÖK", summary.ebitda)
                InfoRow("Net Kar", summary.netIncome)
                InfoRow("EPS", summary.eps)
                InfoRow("Özsermaye", summary.equity)
                InfoRow("Toplam Borç", summary.totalDebt)
                InfoRow("Net Borç", summary.netDebt)
            }
        }
        
        // Çeyreklik Performans (Barlar)
        if (quarterlyData.isNotEmpty()) {
            FinancialSectionCard(title = "Çeyreklik Performans (Revenue vs Net Income)") {
                QuarterlyPerformanceChart(data = quarterlyData)
            }
        }

        // Temel Oranlar (Rasyolar)
        FinancialSectionCard(title = "Karlılık ve Verimlilik (ROE & ROA)") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HealthProgressBar("Özkaynak Karlılığı (ROE)", healthData.liquidity) // Using dummy mappings for now if healthData is incomplete
                HealthProgressBar("Aktif Karlılığı (ROA)", healthData.leverage)
            }
        }
    }
}

@Composable
fun FinancialSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun HealthProgressBar(label: String, progress: Double) {
    val mainGreen = Color(0xFF14B88A)
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = String.format("%.2f", progress), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress.toFloat(),
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = if (progress > 0.7) mainGreen else if (progress > 0.4) Color(0xFFFFB800) else Color.Red,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun QuarterlyPerformanceChart(data: List<QuarterlyBarData>) {
    val mainGreen = Color(0xFF14B88A)
    val ebitdaColor = Color(0xFF34D399)
    val netIncomeColor = Color(0xFF6EE7B7)
    
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = 20.dp.toPx()
        val spacing = width / data.size
        
        val maxVal = data.maxOfOrNull { it.revenue } ?: 1.0
        
        data.forEachIndexed { index, item ->
            val x = index * spacing + spacing / 2
            
            // Revenue Bar
            val rHeight = (item.revenue / maxVal) * height
            drawRect(
                color = mainGreen.copy(alpha = 0.8f),
                topLeft = androidx.compose.ui.geometry.Offset(x - barWidth / 2, (height - rHeight).toFloat()),
                size = androidx.compose.ui.geometry.Size(barWidth, rHeight.toFloat())
            )
            
            // EBITDA Bar
            val eHeight = (item.ebitda / maxVal) * height
            drawRect(
                color = ebitdaColor,
                topLeft = androidx.compose.ui.geometry.Offset(x - barWidth / 4, (height - eHeight).toFloat()),
                size = androidx.compose.ui.geometry.Size(barWidth / 2, eHeight.toFloat())
            )
        }
    }
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        data.forEach { Text(text = it.quarter, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
fun MarginAnalysisChart(data: List<MarginLineData>) {
    val mainGreen = Color(0xFF14B88A)
    
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        
        val path = Path()
        data.forEachIndexed { index, item ->
            val x = index * spacing
            val y = height - (item.netMargin / 50.0 * height).toFloat() // Normalized to 50% max
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = mainGreen,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        LegendItem("Net Marj", mainGreen)
        LegendItem("Brüt Marj", mainGreen.copy(alpha = 0.5f))
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
