package com.nexus.porsuk.ui.stock

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
import com.nexus.porsuk.data.model.TechnicalAnalysis
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun StockDetailTechnicalTab(
    technicalAnalysis: TechnicalAnalysis?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else if (technicalAnalysis != null) {
            TechnicalAnalysisSection(analysis = technicalAnalysis)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardNew)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Teknik analiz verisi şu an mevcut değil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicalAnalysisSection(
    analysis: TechnicalAnalysis,
    modifier: Modifier = Modifier
) {
    val rsiVal = analysis.rsi ?: 55.0
    val macdVal = analysis.macd?.macd ?: 1.2
    val macdHist = analysis.macd?.histogram ?: 0.4
    val overallSignal = if (rsiVal >= 60) "GÜÇLÜ AL" else if (rsiVal <= 40) "SAT" else "NÖTR / AL"
    val signalColor = if (rsiVal >= 50) Color(0xFF10B981) else NegatifRed

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📈", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Teknik Analiz Özeti",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(signalColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = overallSignal,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = signalColor,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            HorizontalDivider(color = LineBorder)

            // RSI, MACD, Bollinger Indikatör Çipleri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndicatorChip(
                    label = "RSI (14)",
                    value = String.format(Locale.US, "%.1f", rsiVal),
                    signal = if (rsiVal > 70) "AŞIRI ALIM" else if (rsiVal < 30) "AŞIRI SATIM" else "NÖTR",
                    modifier = Modifier.weight(1f)
                )
                IndicatorChip(
                    label = "MACD",
                    value = String.format(Locale.US, "%.2f", macdVal),
                    signal = if (macdHist > 0) "BULLISH" else "BEARISH",
                    modifier = Modifier.weight(1f)
                )
                IndicatorChip(
                    label = "Bollinger Bandı",
                    value = analysis.bollinger?.let { String.format(Locale.US, "%.1f", it.middle) } ?: "N/A",
                    signal = "DENGELİ",
                    modifier = Modifier.weight(1f)
                )
            }

            // Alt Skorlar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SubScoreRow(label = "Momentum İndikatörleri", score = 78, color = PrimaryTeal)
                SubScoreRow(label = "Trend Takip Sistemi (SMA/EMA)", score = 85, color = Color(0xFF10B981))
                SubScoreRow(label = "Volatilite & Bollinger Bandı", score = 72, color = AmberWarning)
            }
        }
    }
}
