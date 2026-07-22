package com.nexus.porsuk.ui.fund

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.ui.FinanceViewModel
import com.nexus.porsuk.ui.BasketWithStats
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.common.BasketSummaryBento
import com.nexus.porsuk.ui.common.marketToFlag
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

@Composable
fun FundScreen(
    viewModel: FinanceViewModel, 
    onFundClick: (Int, String) -> Unit,
    onKaziNavigate: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val basketsWithStats by viewModel.basketsWithStats.collectAsState()
    val totalAllBaskets by viewModel.totalBalanceTry.collectAsState()
    val numberFormat by viewModel.numberFormat.collectAsState()
    val totalUniqueStocksCount by viewModel.totalUniqueStocksCount.collectAsState()
    val portfolioHistory by viewModel.portfolioHistory.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }

    var renamingBasketId by remember { mutableStateOf<Int?>(null) }
    var renamingBasketName by remember { mutableStateOf("") }
    var deletingBasketId by remember { mutableStateOf<Int?>(null) }

    val trLocale = Locale("tr", "TR")

    // Calculate exact weighted total basket return percent
    val overallBasketReturn = remember(basketsWithStats) {
        if (basketsWithStats.isNotEmpty()) {
            val totalVal = basketsWithStats.sumOf { it.totalValue }
            if (totalVal > 0.0) {
                basketsWithStats.sumOf { it.changePercent * (it.totalValue / totalVal) }
            } else {
                basketsWithStats.map { it.changePercent }.average()
            }
        } else {
            0.0
        }
    }

    // Calculate best basket name and return
    val bestBasket = remember(basketsWithStats) {
        basketsWithStats.maxByOrNull { it.changePercent }
    }
    val bestBasketName = bestBasket?.basket?.name ?: ""
    val bestBasketReturn = bestBasket?.changePercent ?: 0.0

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Fon Sepetlerim",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        "${basketsWithStats.size} sepet · Toplam ${CurrencyFormatter.formatTRY(totalAllBaskets, numberFormat)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AquaSoft)
                        .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        tint = PrimaryTeal
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryTeal,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Fon Ekle")
            }
        },
        containerColor = BackgroundNew
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Bento summary grid
            item {
                BasketSummaryBento(
                    totalValue = totalAllBaskets,
                    changePercent = overallBasketReturn,
                    bestBasketName = bestBasketName,
                    bestBasketReturn = bestBasketReturn,
                    totalUniqueStocks = totalUniqueStocksCount,
                    numberFormat = numberFormat
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 2. Basket list
            items(basketsWithStats) { stats ->
                BasketStatCard(
                    stats = stats,
                    numberFormat = numberFormat,
                    onClick = { onFundClick(stats.basket.id, stats.basket.name) },
                    onRename = {
                        renamingBasketId = stats.basket.id
                        renamingBasketName = stats.basket.name
                    },
                    onDelete = {
                        deletingBasketId = stats.basket.id
                    }
                )
            }

            // 3. Deep Drill / Kazi shortcut card at the end of the list
            item {
                Spacer(modifier = Modifier.height(6.dp))
                KaziShortcutCard(onClick = onKaziNavigate)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showAddDialog) {
            AddFundDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, market, templateType, category ->
                    if (templateType != null) {
                        viewModel.addBasketWithTemplate(name, market, templateType)
                    } else {
                        viewModel.addBasket(name, market, category)
                    }
                    showAddDialog = false
                }
            )
        }

        if (renamingBasketId != null) {
            var newName by remember { mutableStateOf(renamingBasketName) }
            AlertDialog(
                onDismissRequest = { renamingBasketId = null },
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
                                viewModel.renameBasket(renamingBasketId!!, newName)
                                renamingBasketId = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { renamingBasketId = null }) {
                        Text("İptal", fontFamily = Manrope, color = SubText)
                    }
                }
            )
        }

        if (deletingBasketId != null) {
            AlertDialog(
                onDismissRequest = { deletingBasketId = null },
                containerColor = CardNew,
                shape = RoundedCornerShape(24.dp),
                title = { Text("Sepeti Sil", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = NegatifRed) },
                text = { Text("Bu sepeti ve içindeki tüm hisseleri silmek istediğine emin misin?", fontFamily = Manrope, color = InkText) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteBasket(deletingBasketId!!)
                            deletingBasketId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NegatifRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sil", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deletingBasketId = null }) {
                        Text("İptal", fontFamily = Manrope, color = SubText)
                    }
                }
            )
        }
    }
}

@Composable
fun BasketStatCard(
    stats: BasketWithStats,
    numberFormat: String = "TR",
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val isPositive = stats.changePercent >= 0
    val cardColor = if (isPositive) PrimaryTeal else NegatifRed
    
    val sparklineColor = if (isPositive) {
        if (stats.basket.category == "Temettü Odaklı") PrimaryTeal else AquaNew
    } else {
        NegatifRed
    }
    
    val sparklineValues = remember(stats.changePercent) {
        if (isPositive) {
            listOf(10f, 12f, 11f, 15f, 13f, 18f + (stats.changePercent.coerceAtMost(100.0) * 0.5).toFloat())
        } else {
            listOf(15f, 13f, 14f, 10f, 12f, 8f + (stats.changePercent.coerceAtLeast(-100.0) * 0.5).toFloat())
        }
    }

    val chipColors = remember(stats.basket.category) {
        when (stats.basket.category) {
            "Temettü Odaklı" -> PrimaryTeal to TealSoft
            "Büyüme Odaklı" -> Violet to VioletSoft
            "Agresif" -> Coral to Color(0xFFFFECE8)
            else -> AquaNew to AquaSoft
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = BorderStroke(1.dp, LineBorder)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Subtle performance gradient overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(cardColor.copy(alpha = 0.02f), Color.Transparent)
                        )
                    )
            )
            
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(chipColors.second),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(marketToFlag(stats.basket.market), fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stats.basket.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    fontFamily = Manrope,
                                    color = InkText,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(chipColors.second)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stats.basket.category,
                                        color = chipColors.first,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Manrope,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${stats.basket.market} · ${stats.itemCount} hisse",
                                style = MaterialTheme.typography.bodySmall,
                                color = SubText,
                                fontFamily = Manrope
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Sparkline(
                            values = sparklineValues,
                            color = sparklineColor,
                            modifier = Modifier.size(50.dp, 24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "Menü",
                                    tint = SubText.copy(alpha = 0.6f)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Düzenle", fontFamily = Manrope) },
                                    onClick = {
                                        showMenu = false
                                        onRename()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sil", fontFamily = Manrope) },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        CurrencyFormatter.formatTRY(stats.totalValue, numberFormat),
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                        color = InkText
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPositive) "▲" else "▼",
                            color = cardColor,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                        )
                        Text(
                            NumberFormatter.formatPercentage(stats.changePercent, numberFormat),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = IBMPlexMono
                            ),
                            color = cardColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KaziShortcutCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PrimaryTeal, Coral)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Yeni bir sepet mi lazım?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Derin Kazı ile Orakul'a sıfırdan kurdur",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Detay",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundDialog(onDismiss: () -> Unit, onConfirm: (String, String, String?, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("BIST") }
    var isTemplateMode by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf("RAY_DALIO") }
    var selectedCategory by remember { mutableStateOf("Dengeli") }

    val templates = listOf(
        Triple("RAY_DALIO", "Ray Dalio All-Weather", "NASDAQ"),
        Triple("WARREN_BUFFETT", "Warren Buffett 90/10", "NASDAQ"),
        Triple("BIST_TEMETTU", "BIST Temettü 25", "BIST"),
        Triple("TECH_GIANTS", "Teknoloji Devleri", "NASDAQ")
    )

    // Pre-fill name and market when template is selected
    LaunchedEffect(isTemplateMode, selectedTemplate) {
        if (isTemplateMode) {
            val template = templates.first { it.first == selectedTemplate }
            name = when (selectedTemplate) {
                "RAY_DALIO" -> "Tüm Hava Koşulları"
                "WARREN_BUFFETT" -> "Buffett 90/10"
                "BIST_TEMETTU" -> "Temettü Sepeti"
                else -> "Teknoloji Devleri"
            }
            market = template.third
        } else {
            name = ""
            market = "BIST"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Sepet Oluştur", fontFamily = Manrope, fontWeight = FontWeight.Bold, color = InkText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Mode Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(false to "Boş Sepet", true to "Hazır Şablon").forEach { (isTemp, label) ->
                        val isSel = isTemplateMode == isTemp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) PrimaryTeal.copy(alpha = 0.12f) else CardNew)
                                .border(1.dp, if (isSel) PrimaryTeal else LineBorder, RoundedCornerShape(10.dp))
                                .clickable { isTemplateMode = isTemp }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontFamily = Manrope,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) PrimaryTeal else SubText
                            )
                        }
                    }
                }

                if (isTemplateMode) {
                    Column {
                        Text("Şablon Seçimi", style = MaterialTheme.typography.labelMedium, color = SubText, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            templates.forEach { (type, label, region) ->
                                val isSel = selectedTemplate == type
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) PrimaryTeal.copy(alpha = 0.08f) else Color.Transparent)
                                        .border(1.dp, if (isSel) PrimaryTeal else LineBorder, RoundedCornerShape(10.dp))
                                        .clickable { selectedTemplate = type }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(label, fontSize = 12.sp, fontFamily = Manrope, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, color = InkText)
                                    Text(if (region == "BIST") "🇹🇷 BIST" else "🇺🇸 ABD", fontSize = 10.sp, color = SubText, fontFamily = Manrope)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Sepet Adı", fontFamily = Manrope) },
                    placeholder = { Text("Örn: Temettü Sepeti", color = SubText) },
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
                
                if (!isTemplateMode) {
                    Column {
                        Text("Kategori Seçimi", style = MaterialTheme.typography.labelMedium, color = SubText, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Dengeli", "Temettü Odaklı", "Büyüme Odaklı", "Agresif").forEach { cat ->
                                val isSelected = selectedCategory == cat
                                val chipColor = when (cat) {
                                    "Temettü Odaklı" -> PrimaryTeal
                                    "Büyüme Odaklı" -> Violet
                                    "Agresif" -> Coral
                                    else -> AquaNew
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) chipColor.copy(alpha = 0.12f) else CardNew)
                                        .border(1.dp, if (isSelected) chipColor else LineBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedCategory = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        cat.replace(" Odaklı", ""),
                                        fontSize = 10.sp,
                                        fontFamily = Manrope,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) chipColor else SubText
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text("Piyasa Seçimi", style = MaterialTheme.typography.labelMedium, color = SubText, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("BIST", "🇹🇷 BIST", "BIST"),
                                Triple("NASDAQ", "🇺🇸 NASDAQ", "NASDAQ"),
                                Triple("FRA", "🇪🇺 Avrupa", "FRA")
                            ).forEach { (code, label, _) ->
                                val isSelected = market == code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryTeal.copy(alpha = 0.12f) else CardNew)
                                        .border(1.dp, if (isSelected) PrimaryTeal else LineBorder, RoundedCornerShape(10.dp))
                                        .clickable { market = code }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontSize = 12.sp,
                                        fontFamily = Manrope,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryTeal else SubText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        onConfirm(name, market, if (isTemplateMode) selectedTemplate else null, selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Oluştur", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = SubText)
            ) {
                Text("İptal", fontFamily = Manrope, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = CardNew,
        shape = RoundedCornerShape(16.dp)
    )
}

