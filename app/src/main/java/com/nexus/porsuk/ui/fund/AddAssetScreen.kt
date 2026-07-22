package com.nexus.porsuk.ui.fund

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    fundId: Int,
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    val companies by viewModel.allCompanies.collectAsState(initial = emptyList())
    val prices by viewModel.prices.collectAsState()

    var symbol by remember { mutableStateOf("") }
    var exchange by remember { mutableStateOf("IST") } // Default to BIST
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val filteredSuggestions = remember(symbol, companies) {
        if (symbol.isNotEmpty()) {
            companies.filter {
                it.symbol.contains(symbol, ignoreCase = true) ||
                it.name.contains(symbol, ignoreCase = true)
            }.take(5)
        } else {
            emptyList()
        }
    }

    val exactMatch = remember(symbol, companies) {
        companies.find { it.symbol.equals(symbol, ignoreCase = true) }
    }

    val currentMarketPrice = remember(exactMatch, prices) {
        exactMatch?.let {
            prices[it.symbol]?.price ?: it.currentPrice
        }
    }

    // Auto-fill exchange and price on exact symbol match if price field is empty
    LaunchedEffect(exactMatch) {
        exactMatch?.let { company ->
            exchange = company.market ?: "IST"
            if (price.isEmpty()) {
                val p = prices[company.symbol]?.price ?: company.currentPrice
                price = p.toString()
            }
        }
    }

    val currencySymbol = remember(exchange) {
        when (exchange.uppercase()) {
            "NASDAQ", "NYSE" -> "$"
            "FRA", "EURONEXT" -> "€"
            else -> "₺"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hisse Ekle", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundNew)
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it.uppercase() },
                label = { Text("Hisse Kodu (Örn: THYAO)", fontFamily = Manrope) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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

            // Hisse önerileri
            if (filteredSuggestions.isNotEmpty() && exactMatch == null) {
                Text(
                    "Eşleşen Şirketler:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText,
                    fontFamily = Manrope
                )
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredSuggestions) { company ->
                        val p = prices[company.symbol]?.price ?: company.currentPrice
                        SuggestionChip(
                            onClick = {
                                symbol = company.symbol
                                exchange = company.market ?: "IST"
                                price = p.toString()
                            },
                            label = { Text("${company.symbol} (${company.name})", fontFamily = Manrope) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CardNew,
                                labelColor = InkText
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                borderColor = LineBorder,
                                enabled = true
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = exchange,
                onValueChange = { exchange = it.uppercase() },
                label = { Text("Borsa (IST, NASDAQ, FRA)", fontFamily = Manrope) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Alış Fiyatı ($currencySymbol)", fontFamily = Manrope) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
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

                // Anlık fiyatı otomatik doldurma yardımcısı
                currentMarketPrice?.let { cmp ->
                    TextButton(
                        onClick = { price = cmp.toString() },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(
                            "Anlık Borsa Fiyatını Kullan: $currencySymbol$cmp",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryTeal,
                            fontFamily = Manrope
                        )
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Adet", fontFamily = Manrope) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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

            val canAdd = symbol.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()

            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val q = quantity.toDoubleOrNull() ?: 0.0
                    if (symbol.isNotEmpty() && p > 0 && q > 0) {
                        viewModel.addStockToFund(
                            symbol = symbol,
                            exchange = exchange,
                            price = p,
                            quantity = q,
                            date = System.currentTimeMillis(),
                            fundId = fundId
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (canAdd) {
                            Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))
                        } else {
                            Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.25f), AquaNew.copy(alpha = 0.25f)))
                        }
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
                enabled = canAdd
            ) {
                Text("Fona Ekle", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
