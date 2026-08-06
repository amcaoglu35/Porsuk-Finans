package com.nexus.porsuk.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showFmpApiKeyDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAlarmsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundNew)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Ayarlar",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
            color = InkText
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { ProfileCard(uiState.userName, hasApiKey = uiState.hasGeminiApiKey) }

            item {
                SettingsGroup(title = "GÖRÜNÜM") {
                    var visibleIndex = 0
                    SettingsSwitchRow(
                        icon = Icons.Default.DarkMode,
                        title = "Karanlık Mod",
                        checked = uiState.isDarkMode,
                        colorIndex = visibleIndex++,
                        onCheckedChange = viewModel::setDarkMode
                    )
                    if (uiState.isDarkMode) {
                        HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                        SettingsSwitchRow(
                            icon = Icons.Default.Contrast,
                            title = "True Black OLED",
                            checked = uiState.isTrueBlack,
                            colorIndex = visibleIndex++,
                            onCheckedChange = viewModel::setTrueBlack
                        )
                    }
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.Payments,
                        title = "Ana Para Birimi",
                        value = uiState.baseCurrency,
                        colorIndex = visibleIndex++,
                        onClick = { showCurrencyDialog = true }
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.Numbers,
                        title = "Sayı Formatı",
                        value = if (uiState.numberFormat == "TR") "1.234,56" else "1,234.56",
                        valueFontFamily = IBMPlexMono,
                        colorIndex = visibleIndex,
                        onClick = { viewModel.setNumberFormat(if (uiState.numberFormat == "TR") "US" else "TR") }
                    )
                }
            }

            item {
                SettingsGroup(title = "BİLDİRİMLER") {
                    SettingsSwitchRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Fiyat Alarmları",
                        checked = uiState.priceAlertsEnabled,
                        colorIndex = 0,
                        onCheckedChange = viewModel::setPriceAlerts
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = "Alarmları Yönet",
                        value = "${uiState.activeAlerts.size} Aktif",
                        colorIndex = 1,
                        onClick = { showAlarmsSheet = true }
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsSwitchRow(
                        icon = Icons.Default.Summarize,
                        title = "Günlük Özet Bildirimi",
                        checked = uiState.dailySummaryEnabled,
                        colorIndex = 2,
                        onCheckedChange = viewModel::setDailySummary
                    )
                }
            }

            item {
                SettingsGroup(title = "VERİ & GÜNCELLEME") {
                    SettingsRow(
                        icon = Icons.Default.Update,
                        title = "Güncelleme Sıklığı",
                        value = "${uiState.updateFrequencyMinutes} dk",
                        valueFontFamily = IBMPlexMono,
                        colorIndex = 0,
                        onClick = { viewModel.setUpdateFrequency(if (uiState.updateFrequencyMinutes == 2) 5 else 2) }
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.Key,
                        title = "Gemini API Anahtarı",
                        value = if (uiState.hasGeminiApiKey) "Tanımlı" else "Eksik",
                        colorIndex = 1,
                        onClick = { showApiKeyDialog = true }
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = "FMP API Anahtarı (Şifreli)",
                        value = if (uiState.hasFmpApiKey) "Tanımlı" else "Eksik",
                        colorIndex = 2,
                        onClick = { showFmpApiKeyDialog = true }
                    )
                }
            }

            item {
                SettingsGroup(title = "HAKKINDA") {
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = "Uygulama Hakkında",
                        colorIndex = 0,
                        onClick = { showAboutDialog = true }
                    )
                    HorizontalDivider(color = LineBorder, thickness = 0.5.dp)
                    SettingsRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Tüm Verileri Sıfırla",
                        isDestructive = true,
                        onClick = { showResetDialog = true }
                    )
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "Porsuk v1.0.0",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMono),
                        color = SubText.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Ana Para Birimi Seç",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            text = {
                Column {
                    listOf("TRY", "USD", "EUR").forEach { curr ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setBaseCurrency(curr)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.baseCurrency == curr,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryTeal)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(curr, fontFamily = Manrope, color = InkText)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Kapat", color = SubText, fontFamily = Manrope)
                }
            }
        )
    }

    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf("") }
        var isVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Gemini API Anahtarı",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (uiState.hasGeminiApiKey) "Anahtar Durumu: Kayıtlı (Mevcut)" else "Anahtar Durumu: Kayıtlı değil",
                        color = if (uiState.hasGeminiApiKey) PrimaryTeal else NegatifRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                    Text(
                        "Analizler ve sohbet robotu için Google AI Studio'dan aldığınız anahtarı girin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("API Key", fontFamily = Manrope) },
                        placeholder = { Text("AIzaSy...", fontFamily = Manrope) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isVisible = !isVisible }) {
                                Icon(
                                    if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = SubText
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = LineBorder,
                            focusedTextColor = InkText,
                            unfocusedTextColor = InkText,
                            focusedContainerColor = CardNew,
                            unfocusedContainerColor = CardNew
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Anahtar Al (Google AI Studio) ↗",
                        color = PrimaryTeal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .clickable { uriHandler.openUri("https://aistudio.google.com/apikey") }
                            .padding(vertical = 4.dp)
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.hasGeminiApiKey) {
                        TextButton(
                            onClick = {
                                viewModel.saveApiKey("")
                                showApiKeyDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = NegatifRed)
                        ) {
                            Text("Anahtarı Sil", fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            if (tempKey.isNotBlank()) {
                                viewModel.saveApiKey(tempKey)
                            }
                            showApiKeyDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(10.dp),
                        enabled = tempKey.isNotBlank()
                    ) {
                        Text("Kaydet", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("İptal", color = SubText, fontFamily = Manrope)
                }
            }
        )
    }

    if (showFmpApiKeyDialog) {
        var tempFmpKey by remember { mutableStateOf("") }
        var isFmpVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showFmpApiKeyDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "FMP API Anahtarı (Financial Modeling Prep)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (uiState.hasFmpApiKey) "Anahtar Durumu: Kayıtlı (Encrypted)" else "Anahtar Durumu: Kayıtlı değil",
                        color = if (uiState.hasFmpApiKey) PrimaryTeal else NegatifRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope
                    )
                    Text(
                        "Canlı piyasa ve borsa verileri için FMP API anahtarınızı girin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                    OutlinedTextField(
                        value = tempFmpKey,
                        onValueChange = { tempFmpKey = it },
                        label = { Text("FMP API Key", fontFamily = Manrope) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (isFmpVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isFmpVisible = !isFmpVisible }) {
                                Icon(
                                    if (isFmpVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = SubText
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = LineBorder,
                            focusedTextColor = InkText,
                            unfocusedTextColor = InkText,
                            focusedContainerColor = CardNew,
                            unfocusedContainerColor = CardNew
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.hasFmpApiKey) {
                        TextButton(
                            onClick = {
                                viewModel.saveFmpApiKey("")
                                showFmpApiKeyDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = NegatifRed)
                        ) {
                            Text("Sil", fontFamily = Manrope, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = {
                            if (tempFmpKey.isNotBlank()) {
                                viewModel.saveFmpApiKey(tempFmpKey)
                            }
                            showFmpApiKeyDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(10.dp),
                        enabled = tempFmpKey.isNotBlank()
                    ) {
                        Text("Kaydet", color = Color.White, fontFamily = Manrope, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showFmpApiKeyDialog = false }) {
                    Text("İptal", color = SubText, fontFamily = Manrope)
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Verileri Sıfırla",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NegatifRed,
                    fontFamily = Manrope
                )
            },
            text = {
                Text(
                    "Tüm sepetleriniz ve takip listeniz silinecektir. Bu işlem geri alınamaz.",
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetAllData { showResetDialog = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = NegatifRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sıfırla", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("İptal", color = SubText, fontFamily = Manrope)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = CardNew,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Uygulama Hakkında",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Porsuk Portföy Takip",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        "Porsuk, hisse senedi sepetlerinizi yönetmenizi, analiz etmenizi ve Borsa Profesörü yapay zekasıyla portföyünüz hakkında fikir alışverişi yapmanızı sağlayan modern bir finans aracıdır.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText,
                        fontFamily = Manrope
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            }
        )
    }

    if (showAlarmsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAlarmsSheet = false },
            containerColor = CardNew,
            dragHandle = { BottomSheetDefaults.DragHandle(color = LineBorder) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "🚨 Kurulan Fiyat Alarmları",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkText,
                    fontFamily = Manrope
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.activeAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aktif fiyat alarmı bulunmuyor.", color = SubText, fontSize = 14.sp, fontFamily = Manrope)
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.activeAlerts.forEach { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BackgroundNew, RoundedCornerShape(12.dp))
                                    .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        alert.symbol,
                                        fontWeight = FontWeight.Bold,
                                        color = InkText,
                                        fontSize = 16.sp,
                                        fontFamily = Manrope
                                    )
                                    Text(
                                        text = if (alert.isAbove) "Hedef >= ${alert.targetPrice} ${com.nexus.porsuk.ui.common.CurrencyFormatter.getCurrencySymbol(alert.market)}"
                                        else "Hedef <= ${alert.targetPrice} ${com.nexus.porsuk.ui.common.CurrencyFormatter.getCurrencySymbol(alert.market)}",
                                        fontSize = 13.sp,
                                        color = SubText,
                                        fontFamily = Manrope
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deletePriceAlert(alert.id) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Sil",
                                        tint = NegatifRed
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showAlarmsSheet = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                }
            }
        }
    }


}

@Composable
fun ProfileCard(userName: String, hasApiKey: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0B1F1C),
                            Color(0xFF017A63),
                            Color(0xFF00A388)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AquaNew, PrimaryTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(2).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = Manrope,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        userName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontFamily = Manrope
                        )
                    )
                    Text(
                        "Porsuk Premium Kullanıcısı",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Manrope
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // API Key durumu
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (hasApiKey) PrimaryTeal else NegatifRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasApiKey) "Gemini API Aktif" else "Gemini API Eksik",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasApiKey) PrimaryTeal else NegatifRed,
                            fontFamily = JetBrainsMono
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontFamily = IBMPlexMono
            ),
            fontSize = 10.sp,
            color = PrimaryTeal,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNew),
            border = BorderStroke(1.dp, LineBorder)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    isDestructive: Boolean = false,
    colorIndex: Int = 0,
    valueFontFamily: androidx.compose.ui.text.font.FontFamily? = null,
    onClick: () -> Unit
) {
    val (iconColor, iconBg) = if (isDestructive) {
        NegatifRed to RedSoft
    } else {
        when (colorIndex % 4) {
            0 -> PrimaryTeal to TealSoft
            1 -> AquaNew to AquaSoft
            2 -> Gold to GoldSoft
            else -> Violet to VioletSoft
        }
    }
    
    val titleColor = if (isDestructive) NegatifRed else InkText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = valueFontFamily ?: Manrope,
                    fontWeight = FontWeight.Bold
                ),
                color = SubText
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SubText,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    colorIndex: Int = 0,
    onCheckedChange: (Boolean) -> Unit
) {
    val (iconColor, iconBg) = when (colorIndex % 4) {
        0 -> PrimaryTeal to TealSoft
        1 -> AquaNew to AquaSoft
        2 -> Gold to GoldSoft
        else -> Violet to VioletSoft
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Medium),
            color = InkText,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryTeal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = LineBorder,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
