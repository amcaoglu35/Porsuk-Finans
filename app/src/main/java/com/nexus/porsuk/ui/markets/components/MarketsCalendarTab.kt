package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun CalendarPreviewTab(onCalendarClick: () -> Unit) {
    val events = remember {
        listOf(
            CalendarEventItem("15 MAY", "THYAO Temettü Ödemesi", "Hisse başı net ₺6,25 temettü dağıtımı", "%2,1 Verim"),
            CalendarEventItem("18 MAY", "ASELS 1Ç Bilanço Açıklaması", "1. Çeyrek finansal sonuçlarının ilanı", "Yüksek Etki"),
            CalendarEventItem("22 MAY", "TCMB Faiz Kararı", "Politika faiz kararı toplantısı ve basın özeti", "Kritik"),
            CalendarEventItem("28 MAY", "BIST 100 Endeks Değişiklikleri", "2. çeyrek endeks dönemsel güncellemeleri", "Orta Etki")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "calendar_header_button") {
            Button(
                onClick = onCalendarClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("📅 Tüm Temettü & Halka Arz Takvimini Aç", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
            }
        }

        items(events, key = { it.title }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(50.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(item.date.split(" ").firstOrNull() ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.primary)
                            Text(item.date.split(" ").lastOrNull() ?: "", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                        Text(item.desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = PozitifGreen.copy(alpha = 0.12f)) {
                        Text(item.impact, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = PozitifGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}

private data class CalendarEventItem(val date: String, val title: String, val desc: String, val impact: String)
