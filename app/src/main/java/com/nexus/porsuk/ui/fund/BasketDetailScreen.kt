package com.nexus.porsuk.ui.fund

import android.app.DatePickerDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.*
import com.nexus.porsuk.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var showOptSheet by remember { mutableStateOf(false) }
    var showCashAllocationSheet by remember { mutableStateOf(false) }
    
    var selectedHoldingForActions by remember { mutableStateOf<HoldingUiModel?>(null) }
    var showHoldingActionSheet by remember { mutableStateOf(false) }
    var showEditHoldingSheet by remember { mutableStateOf(false) }
    var showDeleteHoldingConfirm by remember { mutableStateOf(false) }
    var showTransactionSheet by remember { mutableStateOf(false) }
    var transactionIsBuy by remember { mutableStateOf(true) }
    var transactionPreFillSymbol by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()

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
                    BacktestCard(viewModel = viewModel)
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
                    EmptyBasketState { showAddBottomSheet = true }
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

@Composable
fun SepetDegeriKarti(uiState: BasketDetailUiState, numberFormat: String = "TR") {
    var selectedRange by remember { mutableStateOf("1A") }
    val isProfit = uiState.profitLossPercent >= 0
    val heroGradient = if (isProfit)
        Brush.linearGradient(listOf(Color(0xFF0D3D35), Color(0xFF0A4A40), Color(0xFF07261F)))
    else
        Brush.linearGradient(listOf(Color(0xFF3D1515), Color(0xFF4A1A1A), Color(0xFF260707)))
    val accentColor = if (isProfit) PrimaryTeal else NegatifRed
    val accentSoft = if (isProfit) Color(0x3300C896) else Color(0x33FF4B4B)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(heroGradient)
    ) {
        // Decorative glow orb
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(accentSoft, Color.Transparent)),
                    CircleShape
                )
                .blur(40.dp)
        )

        Column {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                // Label row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(accentColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "SEPET DEĞERİ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                        color = accentColor,
                        fontFamily = Manrope
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Big value
                Text(
                    CurrencyFormatter.formatWithSymbol(uiState.totalValue, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                    style = MaterialTheme.typography.headlineLarge.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Profit/Loss badge
                val pct = NumberFormatter.formatPercentage(uiState.profitLossPercent, numberFormat)
                val amount = CurrencyFormatter.formatWithSymbol(uiState.profitLossAmount, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentSoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (isProfit) "▲" else "▼",
                        fontSize = 10.sp,
                        color = accentColor,
                        fontFamily = IBMPlexMono
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "$pct  ($amount)",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                        color = accentColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeroStatBox(
                        label = "Maliyet",
                        value = CurrencyFormatter.formatWithSymbol(uiState.totalCost, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                        modifier = Modifier.weight(1f)
                    )
                    HeroStatBox(
                        label = if (isProfit) "Net Kâr" else "Net Zarar",
                        value = CurrencyFormatter.formatWithSymbol(uiState.profitLossAmount, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                        valueColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time Range Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("1H", "1A", "3A", "1Y", "Tümü").forEach { range ->
                        val isSelected = selectedRange == range
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable { selectedRange = range }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                range,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f),
                                fontFamily = Manrope
                            )
                        }
                    }
                }
            }

            // Sparkline area
            val profitLossVal = uiState.profitLossPercent
            val sparkPoints = remember(uiState.basketName, profitLossVal) {
                val list = mutableListOf<Float>()
                var current = 50f
                list.add(current)
                val step = (profitLossVal.toFloat() / 8f) * 10f
                for (i in 1..7) {
                    current += step + kotlin.random.Random.nextFloat() * 10f - 5f
                    list.add(current.coerceIn(10f, 90f))
                }
                list
            }
            Sparkline(
                values = sparkPoints,
                color = accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                filled = true
            )
        }
    }
}

@Composable
fun HeroStatBox(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.White) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontFamily = Manrope)
        Text(value, fontSize = 13.sp, color = valueColor, fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono)
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = InkText) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundNew)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 10.sp, color = SubText, fontWeight = FontWeight.Bold, fontFamily = Manrope)
        Text(value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
    }
}

@Composable
fun EmptyBasketState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bu sepette henüz hisse yok", style = MaterialTheme.typography.bodyLarge, color = SubText, fontFamily = Manrope)
        Button(
            onClick = onAdd,
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
        ) {
            Text("Hisse Ekle", fontFamily = Manrope)
        }
    }
}

@Composable
fun HoldingItem(
    holding: HoldingUiModel, 
    market: String,
    numberFormat: String = "TR",
    logoUrl: String? = null,
    initials: String = "",
    onClick: () -> Unit
) {
    val color = if (holding.changePercent >= 0) PrimaryTeal else NegatifRed
    val glowColor = if (holding.changePercent >= 0) Color(0xFF00C896) else Color(0xFFFF4B4B)

    // Animate allocation bar
    val targetProgress = (holding.allocationPercent / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.nexus.porsuk.ui.common.StockLogoBadge(
                    logoUrl = logoUrl,
                    initials = initials,
                    sectorColor = com.nexus.porsuk.ui.common.getSectorColor(holding.symbol),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(holding.symbol, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = Manrope, color = InkText)
                    Text(
                        String.format(Locale.US, "%.0f adet · %s%,.2f maliyet", holding.quantity, CurrencyFormatter.getCurrencySymbol(market), holding.buyPrice),
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }

                // Sparkline
                val changeVal = holding.changePercent
                val sparkPoints = remember(holding.symbol, changeVal) {
                    val list = mutableListOf<Float>()
                    var current = 50f
                    list.add(current)
                    val step = (changeVal.toFloat() / 7f) * 8f
                    for (i in 1..6) {
                        current += step + kotlin.random.Random.nextFloat() * 8f - 4f
                        list.add(current.coerceIn(10f, 90f))
                    }
                    list
                }
                Sparkline(
                    values = sparkPoints,
                    color = color,
                    modifier = Modifier.size(46.dp, 20.dp).padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        CurrencyFormatter.formatWithSymbol(holding.currentValue, CurrencyFormatter.getCurrencySymbol(market), numberFormat),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.Bold,
                            color = InkText
                        )
                    )
                    
                    val profitLossAmount = holding.currentValue - (holding.quantity * holding.buyPrice)
                    val profitLossStr = CurrencyFormatter.formatWithSymbol(profitLossAmount, CurrencyFormatter.getCurrencySymbol(market), numberFormat)
                    val sign = if (profitLossAmount > 0) "+" else ""
                    Text(
                        "$sign$profitLossStr (${NumberFormatter.formatPercentage(holding.changePercent, numberFormat)})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = IBMPlexMono,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Neon allocation progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BackgroundNew)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        glowColor.copy(alpha = 0.6f),
                                        glowColor
                                    )
                                )
                            )
                            .drawBehind {
                                // Soft neon glow
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color.Transparent, glowColor.copy(alpha = 0.4f))
                                    ),
                                    topLeft = Offset(0f, -4f),
                                    size = size.copy(height = size.height + 8f)
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    String.format(Locale.US, "%.1f%%", holding.allocationPercent),
                    fontSize = 10.sp,
                    fontFamily = IBMPlexMono,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllocationDonutCard(holdings: List<HoldingUiModel>) {
    val colors = listOf(PrimaryTeal, AquaNew, Color(0xFF6DE0EE), Color(0xFFFFB454), Color(0xFFC7D6DB))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Dağılım", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), fontFamily = Manrope, color = InkText)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                DistributionDonut(
                    segments = holdings.mapIndexed { index, holding -> 
                        holding.allocationPercent to colors[index % colors.size]
                    },
                    trackColor = BackgroundNew,
                    modifier = Modifier.size(76.dp)
                )
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    holdings.take(5).forEachIndexed { index, holding ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors[index % colors.size]))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                holding.symbol,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Manrope,
                                color = InkText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "%${(holding.allocationPercent * 100).toInt()}",
                                fontSize = 12.sp,
                                color = SubText,
                                fontFamily = IBMPlexMono
                            )
                        }
                    }
                }
            }
        }
    }
}

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

@Composable
fun BacktestCard(viewModel: BasketDetailViewModel) {
    val isBacktesting by viewModel.isBacktesting.collectAsState()
    val backtestResult by viewModel.backtestResult.collectAsState()
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
                onClick = { viewModel.runBacktest(selectedRange) },
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
                val res = backtestResult!!
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

