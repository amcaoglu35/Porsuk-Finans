package com.nexus.porsuk.ui.alarm

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

private val LightBackground = Color(0xFFFAFAFA)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF6C4CF1)
private val PurpleSoftBg = Color(0xFFF3F0FF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BorderColor = Color(0xFFF1F5F9)
private val SuccessGreen = Color(0xFF00C48C)
private val WarningOrange = Color(0xFFFF9800)
private val CriticalRed = Color(0xFFF44336)

data class AlarmRuleItem(
    val id: String,
    val symbol: String,
    val title: String,
    val conditionDesc: String,
    val category: String, // Fiyat, AI, Haber, Teknik, Portföy, Temettü
    val priority: String, // 🟢 Düşük, 🟡 Orta, 🟠 Yüksek, 🔴 Kritik
    val timeAgo: String,
    var isActive: Boolean = true,
    var isMuted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAlarmCenterScreen(
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }
    var showCreateModal by remember { mutableStateOf(false) }

    val categoryTabs = remember {
        listOf("🔔 Fiyat Alarmları", "🤖 AI Alarmları", "📰 Haber Alarmları", "⚡ Teknik Alarmları", "💼 Portföy Alarmları", "💰 Temettü Alarmları")
    }

    var alarmRules by remember {
        mutableStateOf(
            listOf(
                AlarmRuleItem("1", "THYAO", "THYAO Üst Hedef Alarmı", "Fiyat > ₺315,00 seviyesini aşınca uyar", "🔔 Fiyat Alarmları", "🟠 Yüksek", "5 dk önce"),
                AlarmRuleItem("2", "ASELS", "AI Skoru 90 Zirve Sinyali", "AI Skoru > 90 olunca otomatik tetikle", "🤖 AI Alarmları", "🔴 Kritik", "1 saat önce"),
                AlarmRuleItem("3", "NVDA", "KAP & Haber Analitik Alarmı", "Yolcu/Çip haberinde kritik duyarlılık", "📰 Haber Alarmları", "🟡 Orta", "2 saat önce"),
                AlarmRuleItem("4", "BTC", "Golden Cross Kırılım Alarmı", "EMA 50 / 200 kesişim formasyonu", "⚡ Teknik Alarmları", "🟢 Düşük", "Dün"),
                AlarmRuleItem("5", "PORTFÖY", "Teknik Yoğunlaşma Riski", "Teknoloji ağırlığı > %40 olunca", "💼 Portföy Alarmları", "🟠 Yüksek", "3 gün önce"),
                AlarmRuleItem("6", "EREGL", "Temettü Hak Ediş Hatırlatıcısı", "Temettü Ödeme Tarihi: 15 Ağustos", "💰 Temettü Alarmları", "🟢 Düşük", "1 hafta önce")
            )
        )
    }

    val filteredRules = remember(selectedCategoryIndex, alarmRules) {
        val catName = categoryTabs.getOrNull(selectedCategoryIndex) ?: ""
        if (catName.isBlank()) alarmRules else alarmRules.filter { it.category == catName }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "AI Alarm & Akıllı Bildirimler",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope),
                            color = TextDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = TextDark)
                        }
                    },
                    actions = {
                        Button(
                            onClick = { showCreateModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yeni Alarm", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    categoryTabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedCategoryIndex == idx,
                            onClick = { selectedCategoryIndex = idx },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedCategoryIndex == idx) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedCategoryIndex == idx) PrimaryPurple else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        containerColor = LightBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredRules, key = { it.id }) { item ->
                AlarmRuleCardItem(
                    item = item,
                    onToggleActive = { active ->
                        alarmRules = alarmRules.map { if (it.id == item.id) it.copy(isActive = active) else it }
                    },
                    onSnooze = {
                        Toast.makeText(context, "⏰ ${item.symbol} alarmı 1 saat ertelendi", Toast.LENGTH_SHORT).show()
                    },
                    onMute = {
                        alarmRules = alarmRules.map { if (it.id == item.id) it.copy(isMuted = !it.isMuted) else it }
                        Toast.makeText(context, if (!item.isMuted) "🔕 Alarm sessize alındı" else "🔔 Alarm sesi açıldı", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        alarmRules = alarmRules.filter { it.id != item.id }
                        Toast.makeText(context, "🗑️ Alarm silindi", Toast.LENGTH_SHORT).show()
                    },
                    onDetailClick = {
                        if (item.symbol != "PORTFÖY") onStockClick(item.symbol, "BIST")
                    }
                )
            }
        }

        if (showCreateModal) {
            CreateAlarmBottomSheet(
                onDismiss = { showCreateModal = false },
                onCreate = { newRule ->
                    alarmRules = listOf(newRule) + alarmRules
                    showCreateModal = false
                    Toast.makeText(context, "✨ Yeni ${newRule.symbol} alarmı oluşturuldu!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun AlarmRuleCardItem(
    item: AlarmRuleItem,
    onToggleActive: (Boolean) -> Unit,
    onSnooze: () -> Unit,
    onMute: () -> Unit,
    onDelete: () -> Unit,
    onDetailClick: () -> Unit
) {
    val priorityColor = when (item.priority) {
        "🔴 Kritik" -> CriticalRed
        "🟠 Yüksek" -> WarningOrange
        "🟡 Orta" -> WarningOrange.copy(alpha = 0.8f)
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Symbol, Title, Priority, Active Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = PurpleSoftBg, modifier = Modifier.size(38.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(item.symbol.take(2), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = PrimaryPurple)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.symbol, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = priorityColor.copy(alpha = 0.12f)) {
                                Text(item.priority, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = priorityColor, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        }
                        Text(item.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Switch(
                    checked = item.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryPurple)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("📌 Koşul: ${item.conditionDesc}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = TextDark)

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Görüntüle, Ertele, Sessize Al, Sil
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.timeAgo, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onSnooze, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Snooze, contentDescription = "Ertele", tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onMute, modifier = Modifier.size(32.dp)) {
                        Icon(if (item.isMuted) Icons.Outlined.NotificationsOff else Icons.Outlined.Notifications, contentDescription = "Sessize Al", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Sil", tint = CriticalRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAlarmBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (AlarmRuleItem) -> Unit
) {
    var symbol by remember { mutableStateOf("THYAO") }
    var condition by remember { mutableStateOf("Fiyat > ₺320,00") }
    var selectedCategory by remember { mutableStateOf("🔔 Fiyat Alarmları") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("➕ Yeni AI Akıllı Alarm Kur", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it },
                label = { Text("Varlık Sembolü (Örn: THYAO, ASELS, NVDA)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = condition,
                onValueChange = { condition = it },
                label = { Text("Alarm Hedefi / Koşulu (Örn: Fiyat > ₺320, AI Skoru > 90)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val newRule = AlarmRuleItem(
                        id = System.currentTimeMillis().toString(),
                        symbol = symbol.uppercase(),
                        title = "$symbol Özel Alarmı",
                        conditionDesc = condition,
                        category = selectedCategory,
                        priority = "🟠 Yüksek",
                        timeAgo = "Şimdi"
                    )
                    onCreate(newRule)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Alarmı Oluştur", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiAlarmCenterScreenPreview() {
    AiAlarmCenterScreen(onBack = {})
}
