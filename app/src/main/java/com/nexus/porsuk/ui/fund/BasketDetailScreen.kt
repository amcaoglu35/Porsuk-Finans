package com.nexus.porsuk.ui.fund

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
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
import com.nexus.porsuk.ui.common.*
import com.nexus.porsuk.ui.fund.components.*
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketDetailScreen(
    viewModel: BasketDetailViewModel,
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val numberFormat = "#,##0.00"
    val isOptimizing by viewModel.isOptimizing.collectAsState()
    val optimizationResult by viewModel.optimizationResult.collectAsState()
    val isBacktesting by viewModel.isBacktesting.collectAsState()
    val backtestResult by viewModel.backtestResult.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showOptSheet by remember { mutableStateOf(false) }
    var showCashAllocationSheet by remember { mutableStateOf(false) }

    var selectedHoldingForActions by remember { mutableStateOf<HoldingUiModel?>(null) }
    var showHoldingActionSheet by remember { mutableStateOf(false) }
    var showEditHoldingSheet by remember { mutableStateOf(false) }
    var showDeleteHoldingConfirm by remember { mutableStateOf(false) }
    var showTransactionSheet by remember { mutableStateOf(false) }
    var transactionIsBuy by remember { mutableStateOf(true) }
    var transactionPreFillSymbol by remember { mutableStateOf("") }

    // Optimizasyon sonucu geldiğinde alt sayfayı göster
    LaunchedEffect(optimizationResult) {
        if (optimizationResult != null) {
            showOptSheet = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = PrimaryTeal)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            uiState.basketName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkText,
                            fontFamily = Manrope
                        )
                        Text(
                            "${marketToFlag(uiState.market)} · ${uiState.holdings.size} hisse",
                            style = MaterialTheme.typography.bodySmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AquaSoft)
                            .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menü", tint = PrimaryTeal)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Sepeti Düzenle", fontFamily = Manrope) },
                            onClick = {
                                showMenu = false
                                showRenameDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sepeti Sil", fontFamily = Manrope) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            SpeedDialFAB(
                onAddStockClick = {
                    transactionPreFillSymbol = ""
                    transactionIsBuy = true
                    showTransactionSheet = true
                }
            )
        },
        containerColor = BackgroundNew
    ) { padding ->
        val sectorData = remember(uiState.holdings) {
            uiState.holdings.map { holding ->
                "Genel" to holding.currentValue
            }
            .groupBy { it.first }
            .map { (sector, values) ->
                sector to values.sumOf { it.second }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SepetDegeriKarti(uiState, numberFormat)
            }

            if (uiState.holdings.isNotEmpty()) {
                // Rebalance Suggestion Badge Card (if any deviation)
                if (uiState.rebalanceSuggestions.isNotEmpty()) {
                    item {
                        RebalanceSuggestionCard(suggestions = uiState.rebalanceSuggestions)
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.optimizeBasket() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            if (isOptimizing) {
                                CircularProgressIndicator(color = CardNew, modifier = Modifier.size(22.dp))
                            } else {
                                Text("✨ Profesör ile Sepeti Optimize Et", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Manrope))
                            }
                        }

                        Button(
                            onClick = { showCashAllocationSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF0F3844), Color(0xFF017A63)))),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("💰 Yeni Nakit Dağıt & Akıllı Alım Yap", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Manrope))
                        }
                    }
                }

                item {
                    BacktestCard(
                        isBacktesting = isBacktesting,
                        backtestResult = backtestResult,
                        onRunBacktest = { range -> viewModel.runBacktest(range) }
                    )
                }

                item {
                    AllocationDonutCard(uiState.holdings)
                }

                item {
                    PortfolioPieChart(sectorData = sectorData)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hisseler", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = Manrope)
                    }
                }

                items(uiState.holdings) { holding ->
                    HoldingItem(
                        holding = holding,
                        market = uiState.market,
                        numberFormat = numberFormat,
                        logoUrl = null,
                        initials = holding.symbol.take(3),
                        onClick = {
                            selectedHoldingForActions = holding
                            showHoldingActionSheet = true
                        }
                    )
                }
            } else {
                item {
                    EmptyBasketState {
                        transactionPreFillSymbol = ""
                        transactionIsBuy = true
                        showTransactionSheet = true
                    }
                }
            }
        }

        if (showHoldingActionSheet && selectedHoldingForActions != null) {
            val holding = selectedHoldingForActions!!
            ModalBottomSheet(
                onDismissRequest = { showHoldingActionSheet = false },
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
                        text = "${holding.symbol} İşlemleri",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )

                    Button(
                        onClick = {
                            showHoldingActionSheet = false
                            onStockClick(holding.symbol, uiState.market)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardNew, contentColor = InkText),
                        border = BorderStroke(1.dp, LineBorder)
                    ) {
                        Text("Şirket Detaylarını Gör", fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showHoldingActionSheet = false
                            transactionPreFillSymbol = holding.symbol
                            transactionIsBuy = true
                            showTransactionSheet = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Alış Yap (Hisse Ekle)", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showHoldingActionSheet = false
                            transactionPreFillSymbol = holding.symbol
                            transactionIsBuy = false
                            showTransactionSheet = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Satış Yap (Hisse Çıkar)", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showHoldingActionSheet = false
                            showEditHoldingSheet = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardNew, contentColor = InkText),
                        border = BorderStroke(1.dp, LineBorder)
                    ) {
                        Text("Miktarı / Maliyeti Düzenle", fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showHoldingActionSheet = false
                            showDeleteHoldingConfirm = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NegatifRed)
                    ) {
                        Text("Sepetten Tamamen Kaldır", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showTransactionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTransactionSheet = false },
                containerColor = CardNew
            ) {
                RecordTransactionBottomSheetContent(
                    preFillSymbol = transactionPreFillSymbol,
                    isBuyInitial = transactionIsBuy,
                    market = uiState.market,
                    onExecute = { symbol, quantity, price, isBuy ->
                        viewModel.executeTransaction(symbol, quantity, price, isBuy)
                        showTransactionSheet = false
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Sepeti Sil", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = NegatifRed) },
            text = { Text("Bu sepeti ve içindeki tüm hisseleri silmek istediğine emin misin?", fontFamily = Manrope, color = InkText) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBasket(onBack)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NegatifRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sil", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", fontFamily = Manrope, color = SubText)
                }
            }
        )
    }

    if (showRenameDialog) {
        var newName by remember { mutableStateOf(uiState.basketName) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Sepeti Düzenle", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Yeni Sepet Adı", fontFamily = Manrope) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.renameBasket(newName)
                            showRenameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("İptal", fontFamily = Manrope, color = SubText)
                }
            }
        )
    }

    if (showEditHoldingSheet && selectedHoldingForActions != null) {
        val holding = selectedHoldingForActions!!
        var quantityText by remember { mutableStateOf(holding.quantity.toString()) }
        var buyPriceText by remember { mutableStateOf(holding.buyPrice.toString()) }

        AlertDialog(
            onDismissRequest = { showEditHoldingSheet = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(24.dp),
            title = { Text("${holding.symbol} Verilerini Düzenle", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
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
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text("Alış Fiyatı", fontFamily = Manrope) },
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
                        val price = buyPriceText.replace(',', '.').toDoubleOrNull() ?: 0.0
                        if (qty > 0 && price > 0) {
                            viewModel.updateBasketItem(holding.id, holding.symbol, qty, price)
                            showEditHoldingSheet = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditHoldingSheet = false }) {
                    Text("İptal", fontFamily = Manrope, color = SubText)
                }
            }
        )
    }

    if (showDeleteHoldingConfirm && selectedHoldingForActions != null) {
        val holding = selectedHoldingForActions!!
        AlertDialog(
            onDismissRequest = { showDeleteHoldingConfirm = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Hisseyi Sepetten Kaldır", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = NegatifRed) },
            text = { Text("${holding.symbol} hissesini bu sepetten tamamen silmek istediğine emin misin?", fontFamily = Manrope, color = InkText) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBasketItem(holding.id)
                        showDeleteHoldingConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NegatifRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kaldır", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteHoldingConfirm = false }) {
                    Text("İptal", fontFamily = Manrope, color = SubText)
                }
            }
        )
    }

    if (showOptSheet && optimizationResult != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showOptSheet = false
                viewModel.clearOptimizationResult()
            },
            containerColor = CardNew
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "✨ Profesör'ün Optimizasyon Önerileri",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryTeal,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(16.dp))
                MarkdownText(
                    markdown = optimizationResult!!,
                    style = androidx.compose.ui.text.TextStyle(
                        color = InkText,
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        showOptSheet = false
                        viewModel.clearOptimizationResult()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Anladım, Teşekkürler!", fontFamily = Manrope, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCashAllocationSheet) {
        SmartCashAllocationSheet(
            holdings = uiState.holdings,
            market = uiState.market,
            numberFormat = numberFormat,
            onDismiss = { showCashAllocationSheet = false },
            onExecuteBatch = { purchases ->
                viewModel.executeBatchBuy(purchases)
            }
        )
    }
}
