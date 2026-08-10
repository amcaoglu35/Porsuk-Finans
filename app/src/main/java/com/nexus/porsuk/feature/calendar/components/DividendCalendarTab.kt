package com.nexus.porsuk.feature.calendar.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.domain.model.DividendEvent
import com.nexus.porsuk.feature.calendar.CalendarUiState
import com.nexus.porsuk.feature.calendar.CalendarViewModel
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun DividendTabContent(
    viewModel: CalendarViewModel,
    uiState: CalendarUiState,
    dividends: List<DividendEvent>,
    onStockClick: (String, String) -> Unit,
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

        // Dividend Calculator Card
        item {
            DividendCalculatorCard(
                shares = uiState.calcShares,
                rate = uiState.calcRate,
                result = uiState.calcResult,
                onSharesChange = { viewModel.setCalcShares(it) },
                onRateChange = { viewModel.setCalcRate(it) },
                onClear = { viewModel.clearCalculator() }
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
                    "Yaklaşan Ödemeler",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkText,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                // Market Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tümü", "TR", "US").forEach { market ->
                        val isSelected = uiState.selectedDividendMarket == market
                        Surface(
                            modifier = Modifier.clickable { viewModel.selectDividendMarket(market) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PrimaryTeal.copy(alpha = 0.1f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) PrimaryTeal else LineBorder)
                        ) {
                            Text(
                                market,
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

        if (dividends.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Seçili kriterlerde temettü verisi bulunamadı.", color = SubText, fontSize = 12.sp)
                }
            }
        } else {
            items(dividends) { event ->
                DividendCalendarItem(event = event, onClick = onStockClick)
            }
        }
    }
}

@Composable
fun DividendCalendarItem(
    event: DividendEvent,
    onClick: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(event.symbol, "IST") },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon/Initial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    event.symbol.take(1),
                    color = PrimaryTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle: Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.symbol,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkText,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = JetBrainsMono
                )
                Text(
                    event.companyName,
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(10.dp), tint = SubText)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.exDate, fontSize = 10.sp, color = SubText, fontFamily = JetBrainsMono)
                }
            }

            // Right: Values
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${event.amount} ${event.currency}",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryTeal,
                    fontWeight = FontWeight.Black,
                    fontFamily = JetBrainsMono
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldNew.copy(alpha = 0.1f)
                ) {
                    Text(
                        "TEMETTÜ", // Changed from event.yield to literal as it's missing in model
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldNew
                    )
                }
            }
        }
    }
}

@Composable
fun DividendCalculatorCard(
    shares: String,
    rate: String,
    result: Double?,
    onSharesChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Slate 900
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Temettü Hesaplayıcı", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
                if (shares.isNotEmpty() || rate.isNotEmpty()) {
                    Text(
                        "Temizle",
                        modifier = Modifier.clickable { onClear() },
                        color = PrimaryTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = shares,
                    onValueChange = onSharesChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Lot Miktarı", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = PrimaryTeal,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = rate,
                    onValueChange = onRateChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Net Temettü", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = PrimaryTeal,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            AnimatedVisibility(
                visible = result != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryTeal.copy(alpha = 0.15f))
                            .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tahmini Kazanç:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(
                                String.format(Locale.US, "%.2f TL", result ?: 0.0),
                                color = PrimaryTeal,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                fontFamily = JetBrainsMono
                            )
                        }
                    }
                }
            }
        }
    }
}
