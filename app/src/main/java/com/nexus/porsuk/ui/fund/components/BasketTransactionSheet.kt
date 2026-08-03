package com.nexus.porsuk.ui.fund.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@Composable
fun RecordTransactionBottomSheetContent(
    preFillSymbol: String,
    isBuyInitial: Boolean,
    market: String,
    onExecute: (String, Double, Double, Boolean) -> Unit
) {
    var symbol by remember { mutableStateOf(preFillSymbol) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isBuy by remember { mutableStateOf(isBuyInitial) }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "İşlem Kaydet",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = InkText,
            fontFamily = Manrope
        )

        // Buy/Sell Segmented Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LineBorder)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBuy) PrimaryTeal else Color.Transparent)
                    .clickable { isBuy = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Alış (Buy)",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Manrope,
                    color = if (isBuy) Color.White else SubText,
                    fontSize = 13.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isBuy) Orange else Color.Transparent)
                    .clickable { isBuy = false },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Satış (Sell)",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Manrope,
                    color = if (!isBuy) Color.White else SubText,
                    fontSize = 13.sp
                )
            }
        }

        // Symbol Field
        Column {
            Text("HİSSE SEMBOLÜ", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = symbol,
                onValueChange = { if (preFillSymbol.isBlank()) symbol = it.uppercase() },
                placeholder = { Text("Örn: THYAO, AAPL...", fontFamily = Manrope) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = preFillSymbol.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = LineBorder,
                    focusedTextColor = InkText,
                    unfocusedTextColor = InkText,
                    focusedContainerColor = CardNew,
                    unfocusedContainerColor = CardNew
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Quantity Field
            Column(modifier = Modifier.weight(1f)) {
                Text("ADET", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    placeholder = { Text("0", fontFamily = Manrope) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText,
                        focusedContainerColor = CardNew,
                        unfocusedContainerColor = CardNew
                    )
                )
            }

            // Price Field
            Column(modifier = Modifier.weight(1f)) {
                Text("BİRİM FİYAT", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = { Text(if (market == "NASDAQ") "$" else "₺", fontFamily = Manrope) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText,
                        focusedContainerColor = CardNew,
                        unfocusedContainerColor = CardNew
                    )
                )
            }
        }

        Button(
            onClick = {
                val q = quantity.toDoubleOrNull() ?: 0.0
                val p = price.toDoubleOrNull() ?: 0.0
                if (symbol.isNotBlank() && q > 0 && p > 0) {
                    onExecute(symbol, q, p, isBuy)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isBuy) PrimaryTeal else Orange),
            enabled = symbol.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()
        ) {
            Text(
                text = if (isBuy) "Alış İşlemi Ekle" else "Satış İşlemi Ekle",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = Manrope
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}
