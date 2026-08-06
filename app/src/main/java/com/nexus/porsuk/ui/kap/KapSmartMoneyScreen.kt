package com.nexus.porsuk.ui.kap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class KapDisclosureItem(
    val id: Int,
    val symbol: String,
    val companyName: String,
    val category: String,        // "PATRON ALIMI", "GERİ ALIM", "YENİ İŞ İLİŞKİSİ", "BEDELSİZ", "TEMETTÜ"
    val summary: String,
    val impactRating: String,    // "ÇOK OLUMLU (+)", "OLUMLU (+)", "NÖTR"
    val impactColorHex: Long,
    val timeStr: String
)

object KapSmartMoneyProvider {

    fun getDisclosures(): List<KapDisclosureItem> = listOf(
        KapDisclosureItem(
            id = 1,
            symbol = "THYAO",
            companyName = "Türk Hava Yolları",
            category = "YENİ İŞ İLİŞKİSİ",
            summary = "Avrupa hatlarında 12 yeni uçak kiralama ve filo genişletme anlaşması imzalandı.",
            impactRating = "ÇOK OLUMLU (+)",
            impactColorHex = 0xFF00A878,
            timeStr = "10:14"
        ),
        KapDisclosureItem(
            id = 2,
            symbol = "KCHOL",
            companyName = "Koç Holding",
            category = "GERİ ALIM",
            summary = "Şirket yönetim kurulu kararıyla 500.000 adet pay geri alımı gerçekleştirildi.",
            impactRating = "OLUMLU (+)",
            impactColorHex = 0xFF00A878,
            timeStr = "09:45"
        ),
        KapDisclosureItem(
            id = 3,
            symbol = "ASELS",
            companyName = "Aselsan",
            category = "YENİ İŞ İLİŞKİSİ",
            summary = "Savunma Sanayii Başkanlığı ile $48.5M tutarında yeni ihracat sözleşmesi imzalandı.",
            impactRating = "ÇOK OLUMLU (+)",
            impactColorHex = 0xFF00A878,
            timeStr = "09:30"
        ),
        KapDisclosureItem(
            id = 4,
            symbol = "EREGL",
            companyName = "Ereğli Demir Çelik",
            category = "PATRON ALIMI",
            summary = "Yönetim kurulu üyesi tarafından borsadan 250.000 lot hisse alımı yapıldığı bildirildi.",
            impactRating = "OLUMLU (+)",
            impactColorHex = 0xFF00A878,
            timeStr = "09:15"
        ),
        KapDisclosureItem(
            id = 5,
            symbol = "TUPRS",
            companyName = "Tüpraş",
            category = "TEMETTÜ",
            summary = "Hisse başına net ₺6.85 nakit kar payı dağıtım takvimi açıklandı.",
            impactRating = "OLUMLU (+)",
            impactColorHex = 0xFF00A878,
            timeStr = "Dün"
        )
    )
import com.nexus.porsuk.core.domain.repository.KapCategory
import com.nexus.porsuk.core.domain.repository.KapNotice
import com.nexus.porsuk.core.domain.repository.KapScraperService

// UYARI: Web scraping bağımlılığı. kap.org.tr HTML/DOM yapısı değişirse scraper güncellenmelidir.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KapSmartMoneyScreen(
    onBack: () -> Unit,
    onStockClick: (String, String) -> Unit,
    scraperService: KapScraperService = remember { KapScraperService() }
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rawNotices by remember { mutableStateOf<List<KapNotice>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("Tümü") }

    LaunchedEffect(Unit) {
        isLoading = true
        val result = scraperService.fetchLatestKapNotices()
        if (result.isSuccess) {
            rawNotices = result.getOrDefault(emptyList())
            errorMessage = null
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "KAP duyuruları çekilemedi. kap.org.tr HTML yapısı değişmiş veya erişim kısıtlanmış olabilir."
        }
        isLoading = false
    }

    val disclosures = remember(rawNotices) {
        rawNotices.mapIndexed { idx, n ->
            KapDisclosureItem(
                id = idx + 1,
                symbol = n.symbol,
                companyName = n.companyName,
                category = when (n.category) {
                    KapCategory.BILANCO -> "BİLANÇO"
                    KapCategory.PAY_ALIM_SATIM -> "PATRON / ALIM"
                    KapCategory.SERMAYE_ARTRIMI -> "SERMAYE"
                    KapCategory.TEMETTU -> "TEMETTÜ"
                    else -> "ÖZEL DURUM"
                },
                summary = n.summary.ifBlank { n.title },
                impactRating = if (n.isImportant) "ÇOK OLUMLU (+)" else "OLUMLU (+)",
                impactColorHex = 0xFF00A878,
                timeStr = n.publishTime
            )
        }
    }

    val filtered = remember(disclosures, selectedCategory) {
        if (selectedCategory == "Tümü") disclosures
        else disclosures.filter { it.category == selectedCategory }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Canlı KAP Akıllı Para Radarı",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            color = InkText,
                            fontSize = 18.sp
                        )
                        Text(
                            "Patron alımları, geri alımlar ve yeni iş sözleşmeleri",
                            fontFamily = Manrope,
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                },
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(PrimaryTeal, Color(0xFF0E2D35))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📢", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "KAP Akıllı Para & Geri Alım Radarı",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontFamily = Manrope
                                )
                                Text(
                                    "Şirket içi alımlar ve kurumsal sözleşmeler anlık analiz edilir",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontFamily = Manrope
            // Loading indicator item
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryTeal)
                    }
                }
            }

            // Error Message item (no fake fallback!)
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFA39E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⚠️ KAP Duyuruları Alınamadı", fontWeight = FontWeight.Bold, color = Color(0xFFCF1322), fontSize = 14.sp, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(errorMessage!!, fontSize = 12.sp, color = Color(0xFF595959), fontFamily = Manrope)
                        }
                    }
                }
            }

            // Disclosures list
            items(filtered) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStockClick(item.symbol, "BIST") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNew),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f, fill = false),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    item.companyName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SubText,
                                    fontFamily = Manrope,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.timeStr, style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = IBMPlexMono)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AquaSoft)
                                    .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(item.category, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(item.impactColorHex).copy(alpha = 0.15f))
                                    .border(1.dp, Color(item.impactColorHex).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(item.impactRating, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(item.impactColorHex), fontFamily = IBMPlexMono)
                            }
                        }

                        Text(item.summary, style = MaterialTheme.typography.bodyMedium, color = InkText, fontFamily = Manrope, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}
