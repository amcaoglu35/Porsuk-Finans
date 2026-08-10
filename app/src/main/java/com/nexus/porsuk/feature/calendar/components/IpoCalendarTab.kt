package com.nexus.porsuk.feature.calendar.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.IpoCalendarEntry
import com.nexus.porsuk.feature.calendar.CalendarUiState
import com.nexus.porsuk.feature.calendar.CalendarViewModel
import com.nexus.porsuk.ui.theme.*

@Composable
fun IpoTabContent(
    viewModel: CalendarViewModel,
    uiState: CalendarUiState,
    ipos: List<IpoCalendarEntry>,
    showAiInsight: Boolean,
    onToggleAiInsight: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Insight Section
        item {
            OrakulInsightBanner(
                isLoading = uiState.isAiLoading,
                isVisible = showAiInsight,
                hasKey = uiState.hasGeminiKey,
                insightText = uiState.aiInsightText,
                errorText = uiState.aiError,
                onToggle = onToggleAiInsight
            )
        }

        // Filters Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Halka Arz Takvimi",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkText,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                // Status Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tümü", "Aktif", "Beklemede").forEach { status ->
                        val isSelected = uiState.selectedIpoStatus == status
                        Surface(
                            modifier = Modifier.clickable { viewModel.selectIpoStatus(status) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PrimaryTeal.copy(alpha = 0.1f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) PrimaryTeal else LineBorder)
                        ) {
                            Text(
                                status,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PrimaryTeal else SubText
                            )
                        }
                    }
                }
            }
        }

        if (ipos.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Yakın zamanda halka arz bulunmuyor.", color = SubText, fontSize = 12.sp)
                }
            }
        } else {
            items(ipos) { ipo ->
                IpoCalendarItem(ipo = ipo, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun IpoCalendarItem(
    ipo: IpoCalendarEntry,
    viewModel: CalendarViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isAlarmSet = uiState.activeIpoAlarms.contains(ipo.symbol)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PrimaryTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            ipo.symbol.take(2),
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            ipo.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            color = InkText,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = JetBrainsMono
                        )
                        Text(
                            ipo.companyName,
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleIpoAlarm(context, ipo) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isAlarmSet) PrimaryTeal else LineBorder.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = if (isAlarmSet) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                        contentDescription = "Alarm",
                        tint = if (isAlarmSet) Color.White else PrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IpoDetailMiniItem("Fiyat", "${ipo.price} TL", PrimaryTeal)
                IpoDetailMiniItem("Dağıtım", ipo.distributionMethod, InkText)
                IpoDetailMiniItem("Büyüklük", ipo.lotQuantity.toString(), InkText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline / Dates
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundNew)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = EmeraldNew, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Talep Toplama: ${formatDate(ipo.startDate)} - ${formatDate(ipo.endDate)}",
                    fontSize = 11.sp,
                    color = SubText,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun IpoDetailMiniItem(label: String, value: String, valueColor: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = SubText, fontFamily = Manrope)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = JetBrainsMono
        )
    }
}
