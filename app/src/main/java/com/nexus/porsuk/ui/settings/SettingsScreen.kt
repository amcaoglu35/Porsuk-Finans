package com.nexus.porsuk.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.settings.components.*
import com.nexus.porsuk.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit = {}
) {
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
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = InkText
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Ayarlar",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                color = InkText
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

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
        CurrencySelectionDialog(
            currentCurrency = uiState.baseCurrency,
            onCurrencySelected = { 
                viewModel.setBaseCurrency(it)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showApiKeyDialog) {
        GeminiApiKeyDialog(
            hasApiKey = uiState.hasGeminiApiKey,
            onSaveKey = { viewModel.saveApiKey(it) },
            onDismiss = { showApiKeyDialog = false }
        )
    }

    if (showFmpApiKeyDialog) {
        FmpApiKeyDialog(
            hasApiKey = uiState.hasFmpApiKey,
            onSaveKey = { viewModel.saveFmpApiKey(it) },
            onDismiss = { showFmpApiKeyDialog = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showResetDialog) {
        ResetDataDialog(
            onConfirm = { 
                viewModel.resetAllData { showResetDialog = false }
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
fun CurrencySelectionDialog(
    currentCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                            .clickable { onCurrencySelected(curr) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentCurrency == curr,
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
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = SubText, fontFamily = Manrope)
            }
        }
    )
}

@Composable
fun GeminiApiKeyDialog(
    hasApiKey: Boolean,
    onSaveKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempKey by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
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
                    text = if (hasApiKey) "Anahtar Durumu: Kayıtlı (Mevcut)" else "Anahtar Durumu: Kayıtlı değil",
                    color = if (hasApiKey) PrimaryTeal else NegatifRed,
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
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveKey(tempKey)
                    onDismiss()
                },
                enabled = tempKey.length > 10,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = SubText)
            }
        }
    )
}

@Composable
fun FmpApiKeyDialog(
    hasApiKey: Boolean,
    onSaveKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempKey by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNew,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                "FMP API Anahtarı",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkText,
                fontFamily = Manrope
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (hasApiKey) "Anahtar Durumu: Kayıtlı" else "Anahtar Durumu: Kayıtlı değil",
                    color = if (hasApiKey) PrimaryTeal else NegatifRed,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Manrope
                )
                Text(
                    "Finansal oranlar ve bilanço verileri için Financial Modeling Prep anahtarı gereklidir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText,
                    fontFamily = Manrope
                )
                OutlinedTextField(
                    value = tempKey,
                    onValueChange = { tempKey = it },
                    label = { Text("FMP API Key", fontFamily = Manrope) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveKey(tempKey)
                    onDismiss()
                },
                enabled = tempKey.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Kaydet", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = SubText)
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNew,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text("Porsuk Finans v1.0.0", fontWeight = FontWeight.Bold, fontFamily = Manrope)
        },
        text = {
            Column {
                Text(
                    "Yapay Zeka Destekli Portföy ve Piyasa Analiz Platformu.",
                    color = InkText,
                    fontFamily = Manrope
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "© 2026 Nexus AI Labs. Tüm hakları saklıdır.",
                    fontSize = 11.sp,
                    color = SubText,
                    fontFamily = Manrope
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun ResetDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardNew,
        title = { Text("Tüm Verileri Sıfırla?", fontWeight = FontWeight.Bold, color = NegatifRed) },
        text = {
            Text("Bu işlem tüm portföy, sepet ve ayarlarınızı kalıcı olarak silecektir. Emin misiniz?")
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = NegatifRed)) {
                Text("Evet, Sıfırla")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}
