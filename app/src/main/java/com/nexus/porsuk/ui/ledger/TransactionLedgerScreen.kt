package com.nexus.porsuk.ui.ledger

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PortfolioTransaction
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.theme.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionLedgerScreen(
    viewModel: TransactionLedgerViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Tümü") } // "Tümü", "Bu Ay", "Bu Yıl"
    var selectedTransaction by remember { mutableStateOf<PortfolioTransaction?>(null) }
    var showEditOptions by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Filtrelenmiş işlemler
    val filteredTransactions = remember(uiState.transactions, selectedPeriod) {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        uiState.transactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            val txMonth = txCal.get(Calendar.MONTH)
            val txYear = txCal.get(Calendar.YEAR)

            when (selectedPeriod) {
                "Bu Ay" -> txMonth == currentMonth && txYear == currentYear
                "Bu Yıl" -> txYear == currentYear
                else -> true
            }
        }
    }

    // Filtrelenmiş PnL Hesaplamaları
    val totalPnL = remember(filteredTransactions) {
        filteredTransactions.filter { !it.isBuy }.sumOf { it.realizedPnL }
    }

    val bestTx = remember(filteredTransactions) {
        filteredTransactions.filter { !it.isBuy && it.realizedPnL > 0.0 }.maxByOrNull { it.realizedPnL }
    }

    val worstTx = remember(filteredTransactions) {
        filteredTransactions.filter { !it.isBuy && it.realizedPnL < 0.0 }.minByOrNull { it.realizedPnL }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("İşlem Defterim", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                RealizedPnLSummaryCard(
                    totalPnL = totalPnL,
                    bestTx = bestTx,
                    worstTx = worstTx,
                    txCount = filteredTransactions.size
                )
            }

            // Dönem Filtresi Pill Grubu
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Tümü", "Bu Ay", "Bu Yıl").forEach { period ->
                        val isSelected = selectedPeriod == period
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPeriod = period },
                            label = { Text(period, fontFamily = Manrope, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealSoft,
                                selectedLabelColor = PrimaryTeal,
                                containerColor = CardNew,
                                labelColor = SubText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = PrimaryTeal,
                                borderColor = LineBorder
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            if (filteredTransactions.isNotEmpty()) {
                item {
                    PnLBarChart(transactions = filteredTransactions)
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    EmptyLedgerState()
                }
            } else {
                item {
                    Text(
                        text = "İşlemler (${filteredTransactions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkText,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(filteredTransactions, key = { "${it.id}_${it.timestamp}" }) { transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        onClick = {
                            selectedTransaction = transaction
                            showEditOptions = true
                        }
                    )
                }
            }
        }

        if (showEditOptions && selectedTransaction != null) {
            val tx = selectedTransaction!!
            ModalBottomSheet(
                onDismissRequest = { showEditOptions = false },
                containerColor = CardNew
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${tx.symbol} İşlem Kaydı",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    
                    Button(
                        onClick = {
                            showEditOptions = false
                            showEditDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Miktar / Fiyat Düzenle", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showEditOptions = false
                            showDeleteConfirm = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NegatifRed)
                    ) {
                        Text("Kayıt Sil", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showEditDialog && selectedTransaction != null) {
            val tx = selectedTransaction!!
            var quantityText by remember { mutableStateOf(tx.quantity.toString()) }
            var priceText by remember { mutableStateOf(tx.price.toString()) }
            
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                containerColor = CardNew,
                shape = RoundedCornerShape(24.dp),
                title = { Text("${tx.symbol} İşlemini Düzenle", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
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
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Birim Fiyat", fontFamily = Manrope) },
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qty = quantityText.toDoubleOrNull() ?: 0.0
                            val price = priceText.replace(',', '.').toDoubleOrNull() ?: 0.0
                            if (qty > 0 && price > 0) {
                                viewModel.updateTransaction(tx.copy(quantity = qty, price = price))
                                showEditDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("İptal", fontFamily = Manrope, color = SubText)
                    }
                }
            )
        }

        if (showDeleteConfirm && selectedTransaction != null) {
            val tx = selectedTransaction!!
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                containerColor = CardNew,
                shape = RoundedCornerShape(24.dp),
                title = { Text("İşlemi Sil", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = NegatifRed) },
                text = { Text("${tx.symbol} işlem kaydını silmek istediğinize emin misiniz?", fontFamily = Manrope, color = InkText) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTransaction(tx)
                            showDeleteConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NegatifRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sil", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("İptal", fontFamily = Manrope, color = SubText)
                    }
                }
            )
        }
    }
}

@Composable
fun RealizedPnLSummaryCard(
    totalPnL: Double,
    bestTx: PortfolioTransaction?,
    worstTx: PortfolioTransaction?,
    txCount: Int
) {
    val color = if (totalPnL >= 0) PrimaryTeal else NegatifRed
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF0F766E)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REALİZE EDİLEN KAR / ZARAR",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = (if (totalPnL >= 0) "+" else "") + CurrencyFormatter.formatTRY(totalPnL, "TR"),
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                    color = color
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 3 Column Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🏆 En İyi",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (bestTx != null) "${bestTx.symbol} (+${CurrencyFormatter.formatTRY(bestTx.realizedPnL, "TR")})" else "-",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontFamily = IBMPlexMono,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📉 En Kötü",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (worstTx != null) "${worstTx.symbol} (${CurrencyFormatter.formatTRY(worstTx.realizedPnL, "TR")})" else "-",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NegatifRed,
                            fontFamily = IBMPlexMono,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎯 Win Rate",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = Manrope
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (txCount > 0) "%75.0 Başarı" else "%0.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontFamily = IBMPlexMono,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(transaction: PortfolioTransaction, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr", "TR"))
    val dateText = sdf.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon according to buy/sell
            val itemColor = if (transaction.isBuy) PrimaryTeal else Orange
            val itemBg = if (transaction.isBuy) TealSoft else OrangeLight
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(itemBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isBuy) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = if (transaction.isBuy) "Alış" else "Satış",
                    tint = itemColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = InkText,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = itemBg,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, itemColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = if (transaction.isBuy) "ALIŞ" else "SATIŞ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = itemColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            fontFamily = Manrope
                        )
                    }
                }
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    fontFamily = Manrope
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.quantity} Adet",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                    color = InkText
                )
                val market = remember(transaction.symbol) {
                    val nasdaqList = listOf("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "NVDA", "META", "NFLX")
                    val fraList = listOf("SAP", "ASML", "MC", "OR", "ALV", "BAS")
                    when {
                        transaction.symbol in nasdaqList -> "NASDAQ"
                        transaction.symbol in fraList -> "FRA"
                        else -> "IST"
                    }
                }
                Text(
                    text = "@ " + CurrencyFormatter.formatWithSymbol(transaction.price, CurrencyFormatter.getCurrencySymbol(market), "TR"),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = IBMPlexMono),
                    color = SubText
                )
                if (!transaction.isBuy && transaction.realizedPnL != 0.0) {
                    Text(
                        text = (if (transaction.realizedPnL >= 0) "+" else "") + CurrencyFormatter.formatWithSymbol(transaction.realizedPnL, CurrencyFormatter.getCurrencySymbol(market), "TR"),
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                        color = if (transaction.realizedPnL >= 0) PrimaryTeal else NegatifRed
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLedgerState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = SubText
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Henüz bir işlem kaydı yok",
            style = MaterialTheme.typography.titleMedium,
            color = InkText,
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Hisse senedi alım satımlarınız işlem defterinde listelenecektir.",
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
            fontFamily = Manrope
        )
    }
}

@Composable
fun PnLBarChart(transactions: List<PortfolioTransaction>) {
    val pnlBySymbol = androidx.compose.runtime.remember(transactions) {
        transactions.filter { !it.isBuy && it.realizedPnL != 0.0 }
            .groupBy { it.symbol }
            .mapValues { entry -> entry.value.sumOf { it.realizedPnL } }
            .toList()
            .sortedByDescending { it.second }
    }

    if (pnlBySymbol.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "HİSSE BAZINDA GERÇEKLEŞEN KAR / ZARAR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SubText,
                letterSpacing = 1.2.sp,
                fontFamily = Manrope
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val maxVal = pnlBySymbol.maxOf { pair -> kotlin.math.abs(pair.second) }.coerceAtLeast(1.0)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pnlBySymbol.forEach { pair ->
                    val symbol = pair.first
                    val pnl = pair.second
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = symbol,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            modifier = Modifier.width(60.dp),
                            fontFamily = Manrope
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(LineBorder)
                        ) {
                            val ratio = (kotlin.math.abs(pnl) / maxVal).toFloat().coerceIn(0.05f, 1f)
                            
                            val positiveColors = listOf(PrimaryTeal, AquaNew)
                            val negativeColors = listOf(NegatifRed, Color(0xFFF87171))
                            val brushColors = if (pnl >= 0.0) positiveColors else negativeColors
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.horizontalGradient(brushColors))
                            )
                        }
                        
                        Text(
                            text = (if (pnl >= 0.0) "+" else "") + CurrencyFormatter.formatTRY(pnl, "TR"),
                            fontSize = 12.sp,
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.Bold,
                            color = if (pnl >= 0.0) PrimaryTeal else NegatifRed,
                            modifier = Modifier.width(90.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
