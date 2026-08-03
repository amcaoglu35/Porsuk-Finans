package com.nexus.porsuk.ui.fund.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.fund.HoldingUiModel
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCashAllocationSheet(
    holdings: List<HoldingUiModel>,
    market: String,
    numberFormat: String = "TR",
    onDismiss: () -> Unit,
    onExecuteBatch: (List<Triple<String, Double, Double>>) -> Unit
) {
    var cashInput by remember { mutableStateOf("10000") }
    var selectedStrategy by remember { mutableStateOf("EQUALLY") }
    val context = LocalContext.current

    val totalCash = cashInput.replace(',', '.').toDoubleOrNull() ?: 0.0

    val calculatedOrders = remember(totalCash, selectedStrategy, holdings) {
        if (totalCash <= 0 || holdings.isEmpty()) emptyList()
        else {
            val list = mutableListOf<Triple<String, Double, Double>>()
            val count = holdings.size

            if (selectedStrategy == "EQUALLY") {
                val cashPerStock = totalCash / count
                holdings.forEach { h ->
                    val currentPrice = if (h.quantity > 0) h.currentValue / h.quantity else h.buyPrice
                    if (currentPrice > 0) {
                        val lots = (cashPerStock / currentPrice).toInt()
                        list.add(Triple(h.symbol, lots.toDouble(), currentPrice))
                    }
                }
            } else {
                val totalCurrentVal = holdings.sumOf { it.currentValue }
                holdings.forEach { h ->
                    val weight = if (totalCurrentVal > 0) h.currentValue / totalCurrentVal else 1.0 / count
                    val cashForStock = totalCash * weight
                    val currentPrice = if (h.quantity > 0) h.currentValue / h.quantity else h.buyPrice
                    if (currentPrice > 0) {
                        val lots = (cashForStock / currentPrice).toInt()
                        list.add(Triple(h.symbol, lots.toDouble(), currentPrice))
                    }
                }
            }
            list
        }
    }

    val totalSpent = calculatedOrders.sumOf { it.second * it.third }
    val remainingCash = (totalCash - totalSpent).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardNew
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💰", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "Akıllı Nakit Dağıtım Asistanı",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = Manrope
                    )
                    Text(
                        "Sepete ekleyeceğiniz nakit tutarı hisselere otomatik bölüştürün",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            }

            OutlinedTextField(
                value = cashInput,
                onValueChange = { cashInput = it },
                label = { Text("Yatırılacak Nakit Tutar (${CurrencyFormatter.getCurrencySymbol(market)})", fontFamily = Manrope) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = LineBorder,
                    focusedLabelColor = PrimaryTeal,
                    unfocusedLabelColor = SubText,
                    focusedTextColor = InkText,
                    unfocusedTextColor = InkText,
                    focusedContainerColor = CardNew,
                    unfocusedContainerColor = CardNew
                )
            )

            Text("Dağıtım Stratejisi:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = Manrope)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedStrategy == "EQUALLY",
                    onClick = { selectedStrategy = "EQUALLY" },
                    label = { Text("⚖️ Eşit Bölüştür", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryTeal, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = selectedStrategy == "WEIGHTED",
                    onClick = { selectedStrategy = "WEIGHTED" },
                    label = { Text("📊 Mevcut Oranlarla", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryTeal, selectedLabelColor = Color.White)
                )
            }

            if (calculatedOrders.isNotEmpty()) {
                Text("Önerilen Alım Reçetesi:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = Manrope)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    calculatedOrders.forEach { (sym, lots, price) ->
                        val cost = lots * price
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BackgroundNew)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sym, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryTeal, fontFamily = IBMPlexMono)
                                Text("${lots.toInt()} Lot × ${CurrencyFormatter.formatWithSymbol(price, CurrencyFormatter.getCurrencySymbol(market), numberFormat)}", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                            }
                            Text(
                                CurrencyFormatter.formatWithSymbol(cost, CurrencyFormatter.getCurrencySymbol(market), numberFormat),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = InkText,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealSoft)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Toplam Harcanacak: ${CurrencyFormatter.formatWithSymbol(totalSpent, CurrencyFormatter.getCurrencySymbol(market), numberFormat)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, fontFamily = Manrope)
                    Text("Kalan Nakit: ${CurrencyFormatter.formatWithSymbol(remainingCash, CurrencyFormatter.getCurrencySymbol(market), numberFormat)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubText, fontFamily = Manrope)
                }

                Button(
                    onClick = {
                        onExecuteBatch(calculatedOrders)
                        android.widget.Toast.makeText(context, "Tüm alımlar sepete işlendi!", android.widget.Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Tüm Alımları Sepetime Uygula", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
