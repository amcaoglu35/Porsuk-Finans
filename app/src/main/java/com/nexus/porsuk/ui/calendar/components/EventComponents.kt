package com.nexus.porsuk.ui.calendar.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun EconomicEventCard(
    event: EconomicEvent,
    onAiAnalysisClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = when (event.country.uppercase()) {
                            "TR" -> "🇹🇷"
                            "US" -> "🇺🇸"
                            "EU" -> "🇪🇺"
                            else -> "🌐"
                        },
                        fontSize = 20.sp
                    )
                    Column {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Text(
                            text = java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(java.util.Date(event.eventTime)),
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            fontFamily = IBMPlexMono
                        )
                    }
                }
                
                ImpactBadge(level = event.impactLevel)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ValueColumn("Beklenen", event.forecastValue ?: "-")
                ValueColumn("Gerçekleşen", event.actualValue ?: "-", color = if (event.actualValue != null) PrimaryTeal else SubText)
                ValueColumn("Önceki", event.previousValue ?: "-")
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = LineBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (event.aiEvaluation != null) {
                        AiImpactSection(impact = event.aiEvaluation)
                    } else {
                        Button(
                            onClick = onAiAnalysisClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal.copy(alpha = 0.1f), contentColor = PrimaryTeal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("AI Etki Analizi Oluştur", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DividendEventCard(
    event: DividendEvent,
    onStockClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(event.symbol) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(TealSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💰", fontSize = 16.sp)
                    }
                    Column {
                        Text(event.symbol, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                        Text(event.companyName, fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    }
                }
                Text(
                    "${event.amount} ${event.currency}",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    fontFamily = IBMPlexMono
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ValueColumn("Hak Kullanımı", event.exDate)
                ValueColumn("Ödeme Tarihi", event.paymentDate)
            }
        }
    }
}

@Composable
fun EarningsEventCard(
    event: EarningsEvent,
    onStockClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStockClick(event.symbol) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Violet.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 16.sp)
                    }
                    Column {
                        Text(event.symbol, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                        Text(event.companyName, fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    }
                }
                ImpactBadge(level = CalendarImpactLevel.HIGH)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ValueColumn("Açıklama Tarihi", event.reportDate)
                ValueColumn("Tahmini EPS", "${event.epsForecast}")
            }
        }
    }
}

@Composable
fun IpoEventCard(
    event: IpoIntelligence,
    onIpoClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIpoClick(event.symbol) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(AquaNew.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚀", fontSize = 16.sp)
                    }
                    Column {
                        Text(event.symbol, fontWeight = FontWeight.Bold, color = InkText, fontFamily = Manrope)
                        Text(event.companyName, fontSize = 11.sp, color = SubText, fontFamily = Manrope)
                    }
                }
                Surface(
                    color = PrimaryTeal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        event.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ValueColumn("Arz Fiyatı", "${event.offerPrice ?: "-"} TL")
                ValueColumn("Sektör", event.sector)
            }
        }
    }
}

@Composable
fun AiImpactSection(impact: AiEventImpact) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TealSoft.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("AI Analizi", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(impact.aiCommentary, style = MaterialTheme.typography.bodySmall, color = InkText)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ImpactMetric("Risk", impact.riskLevel, NegatifRed)
            ImpactMetric("Fırsat", impact.opportunityLevel, EmeraldNew)
        }
    }
}

@Composable
private fun ImpactMetric(label: String, value: Int, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubText)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$value/10", fontWeight = FontWeight.Bold, color = color, fontFamily = IBMPlexMono)
        }
    }
}

@Composable
private fun ImpactBadge(level: CalendarImpactLevel) {
    val color = Color(level.colorHex)
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = level.displayName.take(3),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ValueColumn(label: String, value: String, color: Color = InkText) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubText)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color, fontFamily = IBMPlexMono)
    }
}
