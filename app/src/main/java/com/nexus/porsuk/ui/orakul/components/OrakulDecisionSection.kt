package com.nexus.porsuk.ui.orakul.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.orakul.OracleHisseReport
import com.nexus.porsuk.ui.theme.*

private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)
private val ErrorRed = Color(0xFFF44336)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)

@Composable
fun MarketDirectionProbabilitySection(marketSentimentScore: Int = 65) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Piyasa Yön Tahmini",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextDark,
                    fontFamily = Manrope
                )

                Surface(
                    color = PurpleSoftBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Duyarlılık Skoru: $marketSentimentScore/100",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple,
                            fontFamily = IBMPlexMono
                        )
                        Text(
                            text = "(tahmini)",
                            fontSize = 9.sp,
                            color = TextSecondary,
                            fontFamily = Manrope
                        )
                    }
                }
            }

            // Direction Probability Bars (Yükseliş, Yatay, Düşüş)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DirectionCard(
                    title = "Yükseliş",
                    probability = "%62",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                DirectionCard(
                    title = "Yatay",
                    probability = "%24",
                    color = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
                DirectionCard(
                    title = "Düşüş",
                    probability = "%14",
                    color = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DirectionCard(title: String, probability: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(probability, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color, fontFamily = IBMPlexMono)
        }
    }
}

@Composable
fun OracleScoreGaugesSection() {
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
            Text(
                text = "Orakul 6 Boyutlu Skor Göstergeleri",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                fontFamily = Manrope
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreGaugeItem("Temel Güç", "82", SuccessGreen, Modifier.weight(1f))
                ScoreGaugeItem("Teknik İvme", "74", SuccessGreen, Modifier.weight(1f))
                ScoreGaugeItem("Risk Puanı", "35", ErrorRed, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreGaugeItem("Temettü Verimi", "68", WarningOrange, Modifier.weight(1f))
                ScoreGaugeItem("Nakit Akışı", "88", SuccessGreen, Modifier.weight(1f))
                ScoreGaugeItem("Sektör Liderliği", "91", PrimaryPurple, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ScoreGaugeItem(label: String, score: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PurpleSoftBg.copy(alpha = 0.4f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(score, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = IBMPlexMono)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, textAlign = TextAlign.Center, fontFamily = Manrope)
    }
}

@Composable
fun MainScenariosSection() {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Piyasa Regimi & Ana Senaryolar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                fontFamily = Manrope
            )

            ScenarioCard("Boğa Katalizörü", "TCMB faiz indirim döngüsü başlangıcı ile sanayi ve gayrimenkul sektörlerinde hızlı değer artış beklentisi.", SuccessGreen)
            ScenarioCard("Baz Senaryo", "Endeksin %15 bandı içerisinde dalgalanarak enflasyon üzeri net reel getiri sunmaya devam etmesi.", PrimaryPurple)
        }
    }
}

@Composable
fun ScenarioCard(title: String, desc: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 11.sp, color = TextDark, fontFamily = Manrope, lineHeight = 16.sp)
        }
    }
}

@Composable
fun HisseScoreGrid(report: OracleHisseReport) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sembol Analiz Metrikleri",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                fontFamily = Manrope
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreGaugeItem("AI Puanı", "${report.aiScore}", PrimaryPurple, Modifier.weight(1f))
                ScoreGaugeItem("Risk Skoru", "${report.riskScore}", ErrorRed, Modifier.weight(1f))
                ScoreGaugeItem("Büyüme Potansiyeli", "${report.growthPotential}", SuccessGreen, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreGaugeItem("Finansal Sağlık", "${report.financialHealth}", SuccessGreen, Modifier.weight(1f))
                ScoreGaugeItem("Temettü Skoru", "${report.dividendScore}", WarningOrange, Modifier.weight(1f))
                ScoreGaugeItem("Kalite Puanı", "${report.qualityScore}", PrimaryPurple, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun HisseDetailedAnalysis(report: OracleHisseReport) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Yatırım Tezi & Görünüm",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextDark,
                fontFamily = Manrope
            )

            if (report.investmentThesis.isNotBlank()) {
                Text(
                    text = report.investmentThesis,
                    fontSize = 12.sp,
                    color = TextDark,
                    fontFamily = Manrope,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
