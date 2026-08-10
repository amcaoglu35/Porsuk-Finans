package com.nexus.porsuk.ui.fund

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.ui.fund.components.*
import com.nexus.porsuk.ui.theme.*
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketDetailScreen(
    viewModel: BasketDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showTransactionSheet by remember { mutableStateOf(false) }
    var transactionPreFillSymbol by remember { mutableStateOf("") }
    var transactionIsBuy by remember { mutableStateOf(true) }

    var showHoldingActionSheet by remember { mutableStateOf(false) }
    var selectedHoldingForActions by remember { mutableStateOf<com.nexus.porsuk.ui.fund.HoldingUiModel?>(null) }

    var showEditHoldingSheet by remember { mutableStateOf(false) }
    var showDeleteHoldingConfirm by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showOptSheet by remember { mutableStateOf(false) }

    val optimizationResult by viewModel.optimizationResult.collectAsState()

    LaunchedEffect(optimizationResult) {
        if (optimizationResult != null) {
            showOptSheet = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.basketName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                            color = InkText
                        )
                        Text(
                            text = "${uiState.market} Piyasası • ${uiState.holdings.size} Varlık",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = InkText)
                    }
                },
                actions = {
                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Yeniden Adlandır", tint = PrimaryTeal)
                    }
                    IconButton(onClick = { viewModel.optimizeBasket() }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Optimize", tint = Violet)
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
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BasketValueHeader(
                    totalValue = uiState.totalValue,
                    totalProfit = uiState.profitLossAmount,
                    profitPercent = uiState.profitLossPercent,
                    currency = if (uiState.market == "BIST") "₺" else "$"
                )
            }

            item {
                Button(
                    onClick = {
                        transactionPreFillSymbol = ""
                        transactionIsBuy = true
                        showTransactionSheet = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Yeni İşlem Ekle (Alış/Satış)", fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.holdings.isNotEmpty()) {
                item {
                    Text(
                        "Varlık Dağılımı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkText,
                        fontFamily = Manrope
                    )
                }

                items(uiState.holdings, key = { it.id }) { holding ->
                    HoldingItem(
                        holding = holding,
                        market = uiState.market,
                        initials = holding.symbol.take(3),
                        onClick = {
                            selectedHoldingForActions = holding
                            showHoldingActionSheet = true
                        }
                    )
                }
            }
        }

        if (showHoldingActionSheet) {
            selectedHoldingForActions?.let { holding ->
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
                            colors = ButtonDefaults.buttonColors(containerColor = WarningGold.copy(alpha = 0.1f), contentColor = WarningGold),
                            border = BorderStroke(1.dp, WarningGold)
                        ) {
                            Text("Satış Yap (Hisse Çıkar)", fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        }
                    }
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
                    onExecute = { symbol, qty, price, isBuy ->
                        viewModel.executeTransaction(symbol, qty, price, isBuy)
                        showTransactionSheet = false
                    }
                )
            }
        }

        if (showRenameDialog) {
            var newName by remember { mutableStateOf(uiState.basketName) }
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                containerColor = CardNew,
                title = { Text("Sepeti Yeniden Adlandır", fontFamily = Manrope, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Yeni İsim") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.renameBasket(newName)
                            showRenameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
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

        if (showEditHoldingSheet) {
            selectedHoldingForActions?.let { holding ->
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
                                label = { Text("Miktar") }
                            )
                            OutlinedTextField(
                                value = buyPriceText,
                                onValueChange = { buyPriceText = it },
                                label = { Text("Maliyet") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { 
                            val q = quantityText.toDoubleOrNull() ?: 0.0
                            val p = buyPriceText.toDoubleOrNull() ?: 0.0
                            viewModel.updateBasketItem(holding.id, holding.symbol, q, p)
                            showEditHoldingSheet = false
                        }) { Text("Güncelle") }
                    }
                )
            }
        }

        if (showDeleteHoldingConfirm) {
            selectedHoldingForActions?.let { holding ->
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
                        ) {
                            Text("Evet, Sil", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteHoldingConfirm = false }) {
                            Text("İptal", fontFamily = Manrope, color = SubText)
                        }
                    }
                )
            }
        }

        if (showOptSheet) {
            ModalBottomSheet(
                onDismissRequest = { 
                    showOptSheet = false 
                    viewModel.clearOptimizationResult()
                },
                containerColor = CardNew
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding()
                ) {
                    Text(
                        "✨ Profesör'ün Optimizasyon Önerileri",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MarkdownText(
                        markdown = optimizationResult ?: "",
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
    }
}
