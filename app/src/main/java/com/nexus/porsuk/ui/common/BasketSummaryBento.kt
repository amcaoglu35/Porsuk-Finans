package com.nexus.porsuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun BasketSummaryBento(
    totalValue: Double,
    changePercent: Double,
    bestBasketName: String,
    bestBasketReturn: Double,
    totalUniqueStocks: Int,
    numberFormat: String
) {
    val isPositive = changePercent >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sol Büyük Kart (Teal -> Aqua Gradient)
        Card(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryTeal, AquaNew)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sepet Toplam Değeri",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )

                    Column {
                        Text(
                            text = CurrencyFormatter.formatTRY(totalValue, numberFormat),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IBMPlexMono
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isPositive) "▲" else "▼",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%+.2f%%", changePercent),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = IBMPlexMono
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Net Getiri",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = Manrope
                            )
                        }
                    }
                }
            }
        }

        // Sağ İki Küçük Kart (Column şeklinde)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Üst Kart: En İyi Sepet (Violet)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VioletSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, Violet.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "En İyi Sepet",
                        color = Violet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                    Column {
                        Text(
                            text = bestBasketName.ifBlank { "Yok" },
                            color = InkText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Manrope,
                            maxLines = 1
                        )
                        Text(
                            text = if (bestBasketName.isNotBlank()) String.format(java.util.Locale.US, "%+.1f%%", bestBasketReturn) else "—",
                            color = Violet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IBMPlexMono
                        )
                    }
                }
            }

            // Alt Kart: Toplam Hisse (Gold)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GoldSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Toplam Hisse",
                        color = Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                    Text(
                        text = "$totalUniqueStocks Benzersiz",
                        color = InkText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = IBMPlexMono
                    )
                }
            }
        }
    }
}
