package com.nexus.porsuk.ui.automation

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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
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

private data class AutomationRuleItem(
    val id: String,
    val title: String,
    val ifCondition: String,
    val thenAction: String,
    val frequency: String, // Saatlik, Günlük, Haftalık, Aylık
    val lastRun: String,
    var isActive: Boolean = true
)

private data class AutomationExecutionLog(
    val id: String,
    val ruleTitle: String,
    val timestamp: String,
    val durationMs: Long,
    val isSuccess: Boolean,
    val outputResult: String
)

private data class AutomationTemplate(val title: String, val icon: String, val desc: String, val frequency: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAutomationHubScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showCreateModal by remember { mutableStateOf(false) }

    val tabs = remember {
        listOf("⚡ Otomasyonlar", "📜 Çalışma Geçmişi", "💡 AI Önerileri", "📑 Şablonlar")
    }

    var rules by remember {
        mutableStateOf(
            listOf(
                AutomationRuleItem("1", "Günlük Fırsat Taraması", "EĞER AI Skoru > 90 VE Risk < 30", "O ZAMAN Akıllı Bildirim Gönder & Fırsat Merkezine Ekle", "Günlük (08:30)", "Bugün 08:30", true),
                AutomationRuleItem("2", "Aşırı Satım RSI Alarmı", "EĞER RSI < 30 VE Hacim > %50 Artış", "O ZAMAN Otomatik Watchlist'e Ekle & Alarm Kur", "Saatlik", "10 dk önce", true),
                AutomationRuleItem("3", "Haftalık Portföy Risk Raporu", "EĞER Tek Sektör Ağırlığı > %40", "O ZAMAN PDF Risk Analiz Raporu Oluştur", "Haftalık (Pazartesi)", "22 Temmuz", true),
                AutomationRuleItem("4", "KAP Haber Duyarlılık Otomasyonu", "EĞER Kritik KAP Haberi Yayımlanırsa", "O ZAMAN Oracle Derin Analizi Başlat", "Anlık", "Bugün 02:15", true)
            )
        )
    }

    val executionLogs = remember {
        listOf(
            AutomationExecutionLog("101", "Günlük Fırsat Taraması", "Bugün 08:30:02", 420, true, "THYAO & ASELS için 90+ AI Skoru tespit edildi. Bildirim gönderildi."),
            AutomationExecutionLog("102", "Aşırı Satım RSI Alarmı", "Bugün 03:15:10", 280, true, "EREGL RSI 28.4 seviyesine geriledi. Takip listesine eklendi."),
            AutomationExecutionLog("103", "KAP Haber Duyarlılık Otomasyonu", "Bugün 02:15:45", 850, true, "NVDA yeni çip duyurusu analiz edildi. Pozitif etki skorlandı."),
            AutomationExecutionLog("104", "Haftalık Portföy Risk Raporu", "22 Temmuz 09:00", 1120, true, "Portföy teknoloji ağırlığı %42 olarak tespit edildi. PDF Rapor oluşturuldu.")
        )
    }

    val templates = remember {
        listOf(
            AutomationTemplate("Her Sabah Portföy Analizi 🌅", "🌅", "Her sabah borsa açılışında portföy risk ve fırsat özetini sunar.", "Günlük 09:15"),
            AutomationTemplate("Haftalık AI Performans Raporu 📊", "📊", "Her Pazar akşamı AI tahmin doğruluğunu ve portföy getirisini özetler.", "Haftalık"),
            AutomationTemplate("Yeni Temettü Şirketleri Taraması 💰", "💰", "Yüksek temettü verimi açıklayan şirketleri anında tespit eder.", "Anlık"),
            AutomationTemplate("Güçlü Teknik Sinyal Taraması ⚡", "⚡", "Golden Cross & MACD kırılımı gerçekleştiren varlıkları listeler.", "Saatlik"),
            AutomationTemplate("Büyük KAP Haber Kontrolü 📰", "📰", "KAP haberlerini AI ile okuyarak olumlu/olumsuz skorlama yapar.", "Anlık"),
            AutomationTemplate("Portföy Risk & Yoğunlaşma Kontrolü 🛡️", "🛡️", "Tek sektör veya hisse yoğunluğu %35'i aşınca uyarı tetikler.", "Günlük")
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "AI Automation Hub",
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
                            Text("Otomasyon Kur", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedTab == idx) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedTab == idx) PrimaryPurple else TextSecondary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> ActiveRulesTabContent(
                    rules = rules,
                    onToggleActive = { id, active ->
                        rules = rules.map { if (it.id == id) it.copy(isActive = active) else it }
                    },
                    onRunNow = { rule ->
                        Toast.makeText(context, "⚡ ${rule.title} otomasyonu anında çalıştırıldı!", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = { ruleId ->
                        rules = rules.filter { it.id != ruleId }
                        Toast.makeText(context, "🗑️ Otomasyon kuralı silindi", Toast.LENGTH_SHORT).show()
                    }
                )
                1 -> ExecutionLogsTabContent(logs = executionLogs)
                2 -> AiRecommendationsTabContent(
                    onAddRecommended = { title ->
                        val newRule = AutomationRuleItem((rules.size + 1).toString(), title, "EĞER AI Algoritması Tetiklenirse", "O ZAMAN Akıllı Rapor Gönder", "Haftalık", "Şimdi", true)
                        rules = listOf(newRule) + rules
                        Toast.makeText(context, "✨ Önerilen otomasyon aktif edildi!", Toast.LENGTH_SHORT).show()
                    }
                )
                3 -> TemplatesTabContent(
                    templates = templates,
                    onUseTemplate = { tmpl ->
                        val newRule = AutomationRuleItem((rules.size + 1).toString(), tmpl.title, "EĞER ${tmpl.desc}", "O ZAMAN Rapor Gönder", tmpl.frequency, "Şimdi", true)
                        rules = listOf(newRule) + rules
                        Toast.makeText(context, "✨ ${tmpl.title} şablonu eklendi!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        if (showCreateModal) {
            CreateAutomationBottomSheet(
                onDismiss = { showCreateModal = false },
                onCreate = { newRule ->
                    rules = listOf(newRule) + rules
                    showCreateModal = false
                    Toast.makeText(context, "✨ Yeni otomasyon kuralı oluşturuldu!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

// ── TAB 1: AKTİF OTOMASYONLAR ──
@Composable
private fun ActiveRulesTabContent(
    rules: List<AutomationRuleItem>,
    onToggleActive: (String, Boolean) -> Unit,
    onRunNow: (AutomationRuleItem) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("⚡ Tanımlı AI Otomasyon Kuralları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(rules, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
                        }

                        Switch(
                            checked = item.isActive,
                            onCheckedChange = { onToggleActive(item.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryPurple)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(shape = RoundedCornerShape(10.dp), color = PurpleSoftBg) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📌 ${item.ifCondition}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = PrimaryPurple)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🚀 ${item.thenAction}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = TextDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item.frequency, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { onRunNow(item) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Outlined.PlayArrow, contentDescription = "Şimdi Çalıştır", tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDelete(item.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Sil", tint = CriticalRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── TAB 2: ÇALIŞMA GEÇMİŞİ ──
@Composable
private fun ExecutionLogsTabContent(logs: List<AutomationExecutionLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("📜 Arka Plan Otomasyon Çalışma Günlüğü", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(logs, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.ruleTitle, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                        Text(item.timestamp, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Sonuç: ${item.outputResult}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextDark)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Çalışma Süresi: ${item.durationMs}ms", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                        Text("✓ Tamamlandı", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = SuccessGreen)
                    }
                }
            }
        }
    }
}

// ── TAB 3: AI ÖNERİLERİ ──
@Composable
private fun AiRecommendationsTabContent(onAddRecommended: (String) -> Unit) {
    val recs = remember {
        listOf(
            "Her Pazartesi Sabahı Portföy Yoğunlaşma Riski Kontrolü 🛡️",
            "BIST 100 Endeksi %2 Düştüğünde Nakit & Altın Fırsat Taraması 🪙",
            "Portföydeki Hisse Hedef Fiyata Ulaşınca Kar Al Uyarısı 🎯"
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("💡 Davranışlarınıza Göre Önerilen AI Otomasyonlar", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(recs, key = { it }) { title ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextDark, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAddRecommended(title) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("Aktif Et", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
            }
        }
    }
}

// ── TAB 4: ŞABLONLAR ──
@Composable
private fun TemplatesTabContent(templates: List<AutomationTemplate>, onUseTemplate: (AutomationTemplate) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("📑 Hazır Hazırlanmış AI Otomasyon Şablonları", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = TextDark)
        }

        items(templates, key = { it.title }) { tmpl ->
            Card(
                modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tmpl.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = TextDark)
                        Text(tmpl.desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onUseTemplate(tmpl) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("Kullan", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAutomationBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (AutomationRuleItem) -> Unit
) {
    var title by remember { mutableStateOf("Özel AI Otomasyonu") }
    var ifCondition by remember { mutableStateOf("AI Skoru > 90 VE Risk < 25") }
    var thenAction by remember { mutableStateOf("Bildirim Gönder & Watchlist'e Ekle") }
    var frequency by remember { mutableStateOf("Günlük") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text("➕ Yeni AI Otomasyon Kuralı Oluştur", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = TextDark)
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Otomasyon Adı") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ifCondition,
                onValueChange = { ifCondition = it },
                label = { Text("EĞER Koşulu (Örn: AI Skoru > 90, RSI < 30)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = thenAction,
                onValueChange = { thenAction = it },
                label = { Text("O ZAMAN Aksiyonu (Örn: Bildirim Gönder, PDF Rapor)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val newRule = AutomationRuleItem(
                        id = System.currentTimeMillis().toString(),
                        title = title,
                        ifCondition = "EĞER $ifCondition",
                        thenAction = "O ZAMAN $thenAction",
                        frequency = frequency,
                        lastRun = "Şimdi",
                        isActive = true
                    )
                    onCreate(newRule)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Otomasyonu Kaydet & Başlat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiAutomationHubScreenPreview() {
    AiAutomationHubScreen(onBack = {})
}
