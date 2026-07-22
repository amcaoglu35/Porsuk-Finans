package com.nexus.porsuk.ui.fund

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

fun getCurrencySymbolForRegion(region: Region): String {
    return when (region) {
        Region.BIST -> "₺"
        Region.NASDAQ -> "$"
        Region.EUROPE -> "€"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBasketScreen(
    viewModel: CreateBasketViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Text(
                    "Yeni Fon Oluştur",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            }
        },
        bottomBar = {
            val canSave = uiState.basketName.isNotBlank() && uiState.items.isNotEmpty() && !uiState.isSaving
            val buttonBrush = if (canSave) {
                Brush.horizontalGradient(listOf(PrimaryTeal, AquaNew))
            } else {
                Brush.horizontalGradient(listOf(PrimaryTeal.copy(alpha = 0.25f), AquaNew.copy(alpha = 0.25f)))
            }
            Button(
                onClick = { viewModel.saveBasket(onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(buttonBrush),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                enabled = canSave
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Fonu Kaydet", fontWeight = FontWeight.Bold, color = if (canSave) Color.White else Color.White.copy(alpha = 0.5f), fontFamily = Manrope)
                }
            }
        },
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "FON ADI",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = SubText,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.basketName,
                    onValueChange = viewModel::onNameChange,
                    placeholder = { Text("Örn: Porsuk Teknoloji Sepeti", fontFamily = Manrope) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText,
                        cursorColor = PrimaryTeal,
                        focusedContainerColor = CardNew,
                        unfocusedContainerColor = CardNew
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Region.values().forEach { region ->
                        RegionChip(
                            region = region,
                            isSelected = uiState.selectedRegion == region,
                            onClick = { viewModel.onRegionSelect(region) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Text(
                    "Hisseler (${uiState.items.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = InkText
                )
            }

            item {
                AddStockButton { viewModel.toggleBottomSheet(true) }
            }

            if (uiState.items.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(uiState.items) { item ->
                    AddedStockItem(item, uiState.selectedRegion) { viewModel.removeItem(item) }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        if (uiState.isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleBottomSheet(false) },
                sheetState = sheetState,
                containerColor = CardNew,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(LineBorder)
                    )
                }
            ) {
                AddStockBottomSheetContent(
                    region = uiState.selectedRegion,
                    onFetchPrice = viewModel::fetchPrice,
                    onAdd = { viewModel.addItem(it) }
                )
            }
        }
    }
}

@Composable
fun RegionChip(region: Region, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) TealSoft else CardNew)
            .border(1.dp, if (isSelected) PrimaryTeal else LineBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(region.flag, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                region.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) PrimaryTeal else SubText,
                fontWeight = FontWeight.Bold,
                fontFamily = Manrope
            )
        }
    }
}

@Composable
fun AddStockButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TealSoft.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = PrimaryTeal,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
        }
        Text(
            "+ Hisse Ekle",
            color = PrimaryTeal,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = Manrope
        )
    }
}

@Composable
fun AddedStockItem(item: PendingBasketItem, region: Region, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AquaSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.symbol.take(3),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryTeal,
                    fontFamily = Manrope
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.symbol, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = Manrope, color = InkText)
                val symbol = getCurrencySymbolForRegion(region)
                Text(
                    "${item.quantity} adet · $symbol${item.buyPrice} maliyet",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = IBMPlexMono),
                    color = SubText
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NegatifRed.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Sil", tint = NegatifRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Henüz hisse eklemedin", style = MaterialTheme.typography.bodyMedium, color = SubText, fontFamily = Manrope)
        Text("Yukarıdaki butonla ilk hisseni ekleyerek başla", style = MaterialTheme.typography.labelSmall, color = SubText.copy(alpha = 0.6f), fontFamily = Manrope)
    }
}

@Composable
fun AddStockBottomSheetContent(
    region: Region,
    onFetchPrice: suspend (String) -> Double?,
    onAdd: (PendingBasketItem) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    
    var isPriceLoading by remember { mutableStateOf(false) }
    var hasAutoFetchedPriceForCurrentSymbol by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    LaunchedEffect(symbol, region) {
        val trimmedSymbol = symbol.trim().uppercase()
        if (trimmedSymbol.length >= 3 && !hasAutoFetchedPriceForCurrentSymbol) {
            isPriceLoading = true
            delay(800) // Debounce typing
            try {
                val fetchedPrice = onFetchPrice(trimmedSymbol)
                if (fetchedPrice != null && fetchedPrice > 0.0) {
                    price = String.format(Locale.US, "%.2f", fetchedPrice)
                    hasAutoFetchedPriceForCurrentSymbol = true
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                isPriceLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Hisse Ekle", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = Manrope)
        
        Column {
            Text("SEMBOL ARA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = symbol,
                onValueChange = { 
                    symbol = it.uppercase()
                    hasAutoFetchedPriceForCurrentSymbol = false
                },
                placeholder = { Text("Örn: TUPRS, AAPL...", fontFamily = Manrope) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = LineBorder,
                    focusedTextColor = InkText,
                    unfocusedTextColor = InkText,
                    cursorColor = PrimaryTeal,
                    focusedContainerColor = CardNew,
                    unfocusedContainerColor = CardNew
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ALIŞ TARİHİ", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = sdf.format(Date(date)),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.clickable {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            calendar.set(y, m, d)
                            date = calendar.timeInMillis
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = LineBorder,
                        disabledTextColor = InkText,
                        disabledContainerColor = CardNew
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("ADET", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText,
                        cursorColor = PrimaryTeal,
                        focusedContainerColor = CardNew,
                        unfocusedContainerColor = CardNew
                    )
                )
            }
        }

        Column {
            Text("ALIŞ FİYATI (MALİYET)", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            val curSym = getCurrencySymbolForRegion(region)
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                placeholder = { Text(curSym, fontFamily = Manrope) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                trailingIcon = {
                    if (isPriceLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryTeal
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = LineBorder,
                    focusedTextColor = InkText,
                    unfocusedTextColor = InkText,
                    cursorColor = PrimaryTeal,
                    focusedContainerColor = CardNew,
                    unfocusedContainerColor = CardNew
                )
            )
        }

        Button(
            onClick = {
                val q = quantity.toDoubleOrNull() ?: 0.0
                val p = price.replace(',', '.').toDoubleOrNull() ?: 0.0
                if (symbol.isNotBlank() && q > 0 && p > 0) {
                    onAdd(PendingBasketItem(symbol, q, p, date))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
            enabled = symbol.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()
        ) {
            Text("Sepete Ekle", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}
