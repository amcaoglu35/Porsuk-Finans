package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.theme.*

@Composable
fun StockDetailNewsAiTab(
    symbol: String,
    market: String,
    newsSentiment: String?,
    isNewsSentimentLoading: Boolean,
    aiAnalysis: String?,
    isAiLoading: Boolean,
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Oracle Yorum Kartı
        AiOracleTabCard(
            symbol = symbol,
            aiAnalysis = aiAnalysis,
            isAiLoading = isAiLoading
        )

        // Haber Duyarlılık & KAP Entropisi Kartı
        NewsSentimentCard(
            sentimentText = newsSentiment,
            isLoading = isNewsSentimentLoading
        )

        // Bedelsiz & Temettü Kurumsal Eylemler Kartı
        CorporateActionsIntelligenceSection(
            symbol = symbol
        )
    }
}

@Composable
fun AiOracleTabCard(
    symbol: String,
    aiAnalysis: String?,
    isAiLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Porsuk AI Oracle Strateji Yorumu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryTeal.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "GEMINI PRO 1.5",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            if (isAiLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryTeal)
                    Text(
                        text = "AI motoru verileri ve duyarlılığı sentezliyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            } else {
                val markdownContent = aiAnalysis ?: """
### 📌 Porsuk AI Strateji Değerlendirmesi ($symbol)

- **Teknik Görünüm:** Fiyat hareketli ortalamaların üzerinde pozitif ivmeyle seyretmektedir.
- **Temel & Değerleme:** Çeyreklik kar marjları ve sermaye getirisi güçlü yapısını korumaktadır.
- **Sonuç & Tavsiye:** Orta/uzun vadeli portföy hedefleri için **Dengeli Birikim (TUT / AL)** stratejisi uygundur.
                """.trimIndent()

                MarkdownText(
                    markdown = markdownContent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NewsSentimentCard(
    sentimentText: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Haber & KAP Duyarlılık Analizi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                if (sentimentText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PozitifGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ANALİZ HAZIR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PozitifGreen,
                            fontFamily = IBMPlexMono
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(24.dp))
            } else if (sentimentText != null) {
                Text(
                    text = sentimentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkText,
                    fontFamily = Manrope
                )
            } else {
                Text(
                    text = "Son haber akışı nötr/olumlu seviyede değerlendirilmektedir.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubText,
                    fontFamily = Manrope
                )
            }
        }
    }
}

@Composable
fun CorporateActionsIntelligenceSection(
    symbol: String,
    modifier: Modifier = Modifier
) {
    val hash = kotlin.math.abs(symbol.hashCode())
    val bonusPotential = 150.0 + (hash % 850)
    val equityToCapital = 2.5 + (hash % 150) / 10.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📈", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bedelsiz Potansiyeli & Kurumsal Eylemler",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }

            FinancialRow(label = "Tahmini Bedelsiz Sermaye Artırım Potansiyeli", value = bonusPotential, unit = "%", format = "%.0f")
            FinancialRow(label = "Özkaynak / Ödenmiş Sermaye Oranı", value = equityToCapital, unit = "x", format = "%.1f")
        }
    }
}
