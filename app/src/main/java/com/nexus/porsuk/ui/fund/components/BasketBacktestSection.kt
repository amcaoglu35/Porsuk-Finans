package com.nexus.porsuk.ui.fund.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.fund.BacktestResult
import com.nexus.porsuk.ui.theme.*

@Composable
fun BacktestCard(
    isBacktesting: Boolean,
    backtestResult: BacktestResult?,
    onRunBacktest: (String) -> Unit
) {
    var selectedRange by remember { mutableStateOf("1y") } // 3mo, 6mo, 1y

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "⚖️ Sepet Geçmiş Performansı (Backtest)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Bu sepetin seçilen vadede geçmişteki getirisini BIST100 ve USD karşısında simüle edin:",
                fontSize = 11.sp,
                color = SubText,
                fontFamily = Manrope
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val ranges = listOf("3mo" to "3 Ay", "6mo" to "6 Ay", "1y" to "1 Yıl")
                ranges.forEach { (r, label) ->
                    val isSelected = selectedRange == r
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRange = r },
                        label = { Text(label, fontFamily = Manrope, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryTeal.copy(alpha = 0.12f),
                            selectedLabelColor = PrimaryTeal,
                            containerColor = Color.Transparent,
                            labelColor = SubText
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onRunBacktest(selectedRange) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                if (isBacktesting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        "Simülasyonu Çalıştır",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Manrope)
                    )
                }
            }

            if (backtestResult != null) {
                val res = backtestResult
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = LineBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Simülasyon Sonuçları (${res.durationText})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkText,
                    fontFamily = Manrope
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = TealSoft),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sepet", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                String.format(java.util.Locale.US, "%+.1f%%", res.basketReturnPercent),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.basketReturnPercent >= 0) PrimaryTeal else NegatifRed,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = LineBorder.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BIST 100", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                String.format(java.util.Locale.US, "%+.1f%%", res.bistReturnPercent),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.bistReturnPercent >= 0) PrimaryTeal else NegatifRed,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = LineBorder.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dolar", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                String.format(java.util.Locale.US, "%+.1f%%", res.usdReturnPercent),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (res.usdReturnPercent >= 0) PrimaryTeal else NegatifRed,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = TealSoft.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🔮", fontSize = 14.sp)
                            Text(
                                "Orakul AI Simülasyon Yorumu",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal,
                                fontFamily = Manrope
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = res.description,
                            fontSize = 10.sp,
                            color = InkText,
                            fontFamily = Manrope,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
