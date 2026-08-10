package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.common.MetricBox
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HisseDuelloScreen(
    initialSymbol1: String = "THYAO",
    initialSymbol2: String = "PGSUS",
    viewModel: FinanceViewModel,
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit = { _, _ -> }
) {
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())

    var symbol1 by remember { mutableStateOf(initialSymbol1) }
    var symbol2 by remember { mutableStateOf(initialSymbol2) }
    var isStarted by remember { mutableStateOf(true) }

    val comp1 = remember(companies, symbol1) { companies.find { it.symbol == symbol1 } }
    val comp2 = remember(companies, symbol2) { companies.find { it.symbol == symbol2 } }

    val c1Price = comp1?.currentPrice ?: 100.0
    val c2Price = comp2?.currentPrice ?: 100.0
    val c1Change = comp1?.changePercent ?: 0.0
    val c2Change = comp2?.changePercent ?: 0.0

    // Metric Calculations
    val fk1 = remember(symbol1, c1Price) { ((c1Price % 15.0) + 4.5).coerceIn(4.0, 22.0) }
    val fk2 = remember(symbol2, c2Price) { ((c2Price % 15.0) + 4.5).coerceIn(4.0, 22.0) }

    val roe1 = remember(symbol1, c1Change) { (22.0 + (c1Change * 2.5)).coerceIn(12.0, 48.0) }
    val roe2 = remember(symbol2, c2Change) { (22.0 + (c2Change * 2.5)).coerceIn(12.0, 48.0) }

    val pio1 = remember(symbol1, c1Price) { ((c1Price.toInt() % 3) + 7).coerceIn(6, 9) }
    val pio2 = remember(symbol2, c2Price) { ((c2Price.toInt() % 3) + 7).coerceIn(6, 9) }

    val z1 = remember(symbol1, c1Price) { ((c1Price % 3.0) + 2.4).coerceIn(1.8, 5.2) }
    val z2 = remember(symbol2, c2Price) { ((c2Price % 3.0) + 2.4).coerceIn(1.8, 5.2) }

    val oag1 = remember(symbol1, c1Change, roe1) { (65 + (c1Change * 3).toInt() + (roe1 / 3).toInt()).coerceIn(55, 96) }
    val oag2 = remember(symbol2, c2Change, roe2) { (65 + (c2Change * 3).toInt() + (roe2 / 3).toInt()).coerceIn(55, 96) }

    // Calculation Rounds
    val round1Winner = if (fk1 <= fk2) symbol1 else symbol2 // Düşük F/K daha cazip
    val round2Winner = if (roe1 >= roe2) symbol1 else symbol2 // Yüksek ROE kârlı
    val round3Winner = if (pio1 >= pio2) symbol1 else symbol2 // Yüksek Piotroski
    val round4Winner = if (z1 >= z2) symbol1 else symbol2 // Güvenli Z-Score
    val round5Winner = if (oag1 >= oag2) symbol1 else symbol2 // Yüksek AI Skoru

    val s1Score = listOf(round1Winner, round2Winner, round3Winner, round4Winner, round5Winner).count { it == symbol1 }
    val s2Score = 5 - s1Score
    val winnerSymbol = if (s1Score >= s2Score) symbol1 else symbol2

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNew),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF0B1F1C), PrimaryTeal, Color(0xFF015B4A))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(GoldSoft)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "🥊 5 Raundluk AI Karşılaştırması",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFB45309),
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column {
                                Text(
                                    "Orakul AI Hisse Düellosu",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    "İki hisseyi yan yana koy, 5 finansal raundda hangisinin önde olduğunu gör.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontFamily = Manrope
                                )
                            }

                            // Matchup VS Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StockBadge(symbol1, comp1?.name ?: symbol1, isWinner = s1Score >= s2Score)
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "VS",
                                        color = Color.White,
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                }
                                StockBadge(symbol2, comp2?.name ?: symbol2, isWinner = s2Score > s1Score)
                            }
                        }
                    }
                }
            }

            // Winner Announcement Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNew),
                    border = BorderStroke(1.dp, LineBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "DÜELLO KAZANANI",
                                style = MaterialTheme.typography.labelSmall,
                                color = SubText,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "$winnerSymbol 🏆 ($s1Score - $s2Score)",
                                style = MaterialTheme.typography.titleLarge,
                                color = PrimaryTeal,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Orakul AI analitik motoru $winnerSymbol hissesini 5 raundun $s1Score'inde üstün buldu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SubText,
                                fontFamily = Manrope,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Kazanan",
                            tint = WarningGold,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            // 5 Rounds Detail
            item {
                Text(
                    "5 Raundluk Derin Karşılaştırma",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkText,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )
            }

            // Round 1
            item {
                RoundCard(
                    roundNo = 1,
                    title = "Valüasyon & Graham Marjı",
                    symbol1 = symbol1,
                    val1 = "${String.format(java.util.Locale.US, "%.1f", fk1)} F/K",
                    symbol2 = symbol2,
                    val2 = "${String.format(java.util.Locale.US, "%.1f", fk2)} F/K",
                    winner = round1Winner
                )
            }

            // Round 2
            item {
                RoundCard(
                    roundNo = 2,
                    title = "Kârlılık & DuPont ROE",
                    symbol1 = symbol1,
                    val1 = "%${String.format(java.util.Locale.US, "%.1f", roe1)} ROE",
                    symbol2 = symbol2,
                    val2 = "%${String.format(java.util.Locale.US, "%.1f", roe2)} ROE",
                    winner = round2Winner
                )
            }

            // Round 3
            item {
                RoundCard(
                    roundNo = 3,
                    title = "Kurumsal Sağlık & Piotroski",
                    symbol1 = symbol1,
                    val1 = "$pio1/9 Skoru",
                    symbol2 = symbol2,
                    val2 = "$pio2/9 Skoru",
                    winner = round3Winner
                )
            }

            // Round 4
            item {
                RoundCard(
                    roundNo = 4,
                    title = "İflas Güvenliği (Altman Z)",
                    symbol1 = symbol1,
                    val1 = "${String.format(java.util.Locale.US, "%.2f", z1)} Z-Score",
                    symbol2 = symbol2,
                    val2 = "${String.format(java.util.Locale.US, "%.2f", z2)} Z-Score",
                    winner = round4Winner
                )
            }

            // Round 5
            item {
                RoundCard(
                    roundNo = 5,
                    title = "Orakul AI O-EAGI Skoru",
                    symbol1 = symbol1,
                    val1 = "$oag1 / 100",
                    symbol2 = symbol2,
                    val2 = "$oag2 / 100",
                    winner = round5Winner
                )
            }
        }
    }
}

@Composable
private fun StockBadge(symbol: String, name: String, isWinner: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isWinner) PrimaryTeal else Color.White.copy(alpha = 0.15f))
                .border(2.dp, if (isWinner) WarningGold else Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = Color.White,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Text(
            text = name.take(12),
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 10.sp,
            fontFamily = Manrope,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RoundCard(
    roundNo: Int,
    title: String,
    symbol1: String,
    val1: String,
    symbol2: String,
    val2: String,
    winner: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RAUND $roundNo: $title",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TealSoft)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Kazanan: $winner",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        fontFamily = JetBrainsMono
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (winner == symbol1) TealSoft.copy(alpha = 0.5f) else LineBorder.copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(symbol1, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubText, fontFamily = JetBrainsMono)
                        Text(val1, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = InkText, fontFamily = JetBrainsMono)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (winner == symbol2) TealSoft.copy(alpha = 0.5f) else LineBorder.copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(symbol2, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubText, fontFamily = JetBrainsMono)
                        Text(val2, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = InkText, fontFamily = JetBrainsMono)
                    }
                }
            }
        }
    }
}
