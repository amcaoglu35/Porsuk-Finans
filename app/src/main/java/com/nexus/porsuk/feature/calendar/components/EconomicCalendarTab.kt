package com.nexus.porsuk.feature.calendar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.CalendarImpactLevel
import com.nexus.porsuk.domain.model.EconomicEvent
import com.nexus.porsuk.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EconomicCalendarTabContent(
    events: List<EconomicEvent>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Etkinlik bulunamadı.", color = SubText)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    EconomicEventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EconomicEventCard(event: EconomicEvent, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(marketToFlag(event.country), fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(event.title, fontWeight = FontWeight.Bold, color = InkText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            SimpleDateFormat("HH:mm", Locale.US).format(Date(event.eventTime)),
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                }
                ImpactBadge(level = event.impactLevel)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ValueColumn("Beklenen", event.forecastValue ?: "-")
                ValueColumn("Gerçekleşen", event.actualValue ?: "-", color = PrimaryTeal)
                ValueColumn("Önceki", event.previousValue ?: "-")
            }
        }
    }
}

@Composable
fun ImpactBadge(level: CalendarImpactLevel, modifier: Modifier = Modifier) {
    val (color, text) = when (level) {
        CalendarImpactLevel.HIGH -> NegatifRed to "YÜKSEK"
        CalendarImpactLevel.MEDIUM -> WarningGold to "ORTA"
        CalendarImpactLevel.LOW -> EmeraldNew to "DÜŞÜK"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            fontFamily = JetBrainsMono
        )
    }
}

@Composable
fun ValueColumn(
    label: String, 
    value: String, 
    modifier: Modifier = Modifier, 
    color: Color = SubText
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = SubText, fontFamily = Manrope)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = JetBrainsMono,
            textAlign = TextAlign.Center
        )
    }
}

fun marketToFlag(country: String): String {
    return when (country.uppercase()) {
        "US", "USA" -> "🇺🇸"
        "TR", "TUR" -> "🇹🇷"
        "EU", "EUR" -> "🇪🇺"
        "GB", "GBP" -> "🇬🇧"
        "DE", "GER" -> "🇩🇪"
        "JP", "JPY" -> "🇯🇵"
        "CN", "CHN" -> "🇨🇳"
        else -> "🌐"
    }
}
