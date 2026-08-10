package com.nexus.porsuk.feature.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.EarningsEvent
import com.nexus.porsuk.ui.theme.Manrope
import com.nexus.porsuk.ui.theme.SubText

@Composable
fun EarningsCalendarTabContent(
    events: List<EarningsEvent>
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Yakın zamanda bilanço açıklaması bulunmuyor.", color = SubText, fontFamily = Manrope)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                // Placeholder for EarningsEventCard
                Text(text = "Earnings: ${event.symbol}")
            }
        }
    }
}
