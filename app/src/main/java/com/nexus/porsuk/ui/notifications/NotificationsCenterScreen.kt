package com.nexus.porsuk.ui.notifications

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.ramcosta.composedestinations.annotation.Destination

@Destination(start = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsCenterScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = remember { listOf("Genel", "Portföy", "Oracle", "AI", "Piyasalar") }

    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem("1", "🎯 Oracle Hedef Fiyat Uyarısı", "BIST 100 10.450 puan direncini test ediyor. %87 güven devam ediyor.", "10 dk önce", "Oracle", false),
                NotificationItem("2", "🩺 Portföy Doktoru Taraması", "Portföyünüzün sağlık puanı %85 seviyesinde. Risk seviyeniz ideal.", "1 saat önce", "Portföy", false),
                NotificationItem("3", "📈 THYAO Hacim İvmesi", "THYAO son 1 saatte ₺1.2B hacme ulaştı, günlük yükseliş %2.87.", "2 saat önce", "Piyasalar", true),
                NotificationItem("4", "🤖 AI Raporu Hazırlandı", "Haftalık AI piyasa ve risk analiz raporunuz hazırlandı.", "Dün", "AI", true),
                NotificationItem("5", "🔔 BİST Bilanço Hatırlatması", "2Ç Bilanço dönemi başlıyor. Portföyünüzdeki şirketler güncellendi.", "3 gün önce", "Genel", true)
            )
        )
    }

    val filteredNotifications = remember(selectedTabIndex, notifications) {
        val activeCategory = tabs.getOrNull(selectedTabIndex) ?: "Genel"
        if (activeCategory == "Genel") notifications else notifications.filter { it.category == activeCategory }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LightBackground)) {
                TopAppBar(
                    title = {
                        Text(
                            "Bildirim Merkezi",
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
                        IconButton(onClick = {
                            notifications = notifications.map { it.copy(isRead = true) }
                            Toast.makeText(context, "Tümü okundu olarak işaretlendi", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = "Tümünü Okundu İşaretle", tint = PrimaryPurple)
                        }
                        IconButton(onClick = {
                            notifications = emptyList()
                            Toast.makeText(context, "Bildirimler temizlendi", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Temizle", tint = TextSecondary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
                )

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent,
                    contentColor = PrimaryPurple,
                    edgePadding = 20.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, label ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selectedTabIndex == index) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = if (selectedTabIndex == index) PrimaryPurple else TextSecondary
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
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔕", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Henüz bildirim bulunmuyor", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDark)
                    Text("Yeni piyasa ve AI uyarıları burada görünecektir.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .clickable {
                                notifications = notifications.map {
                                    if (it.id == item.id) it.copy(isRead = true) else it
                                }
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isRead) CardWhite else PurpleSoftBg.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, if (item.isRead) BorderColor else PrimaryPurple.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (item.isRead) LightBackground else PrimaryPurple.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = null,
                                        tint = if (item.isRead) TextSecondary else PrimaryPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextDark)
                                    Text(item.time, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.message, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp), color = TextDark.copy(alpha = 0.85f))
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val category: String,
    val isRead: Boolean
)

@Preview(showBackground = true)
@Composable
private fun NotificationsCenterScreenPreview() {
    NotificationsCenterScreen(onBack = {})
}
