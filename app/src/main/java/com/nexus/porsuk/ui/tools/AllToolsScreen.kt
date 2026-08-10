package com.nexus.porsuk.ui.tools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

data class ToolItemData(
    val title: String,
    val description: String,
    val emoji: String,
    val route: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllToolsScreen(
    onNavigateBack: () -> Unit,
    onToolClick: (String) -> Unit
) {
    val tools = listOf(
        // Öne Çıkanlar & Teşhis
        ToolItemData("Yedekleme & Senkronizasyon", "Bulut Senkronizasyon ve Çoklu Cihaz Yönetimi", "☁️", "cloud_sync", "Öne Çıkanlar & Teşhis"),
        ToolItemData("Portföy Doktoru", "Sağlık Skoru, Çeşitlendirme ve Rebalans Önerileri", "🩺", "portfolio_doctor", "Öne Çıkanlar & Teşhis"),
        ToolItemData("İzleme Listesi", "Sınırsız Takip Listeleri ve Akıllı Klasörler", "⭐", "watchlist", "Öne Çıkanlar & Teşhis"),
        ToolItemData("Alarmlarım", "Fiyat, Yüzde ve İndikatör Alarmları", "🔔", "alerts", "Öne Çıkanlar & Teşhis"),
        ToolItemData("Aracı Kurum Merkezi", "Midas, IBKR, Alpaca ve Binance Entegrasyonu", "🏦", "broker_hub", "Öne Çıkanlar & Teşhis"),
        ToolItemData("Güvenlik & Gizlilik", "Biyometrik Kilit, Denetim Kayıtları ve Güvenlik Skoru", "🛡️", "security_center", "Öne Çıkanlar & Teşhis"),
        ToolItemData("Premium Üyelik", "Sınırsız Özellikler ve Gelişmiş AI Modelleri", "👑", "upgrade", "Öne Çıkanlar & Teşhis"),

        // Piyasa & Teknik Analiz
        ToolItemData("Haber Merkezi", "Canlı Piyasa Haberleri ve AI Duyarlılık Analizi", "📰", "news", "Piyasa & Teknik Analiz"),
        ToolItemData("Gelişmiş Grafik", "Profesyonel İnteraktif Mum Grafiği", "📈", "chart", "Piyasa & Teknik Analiz"),
        ToolItemData("Teknik Analiz", "RSI, MACD, Moving Averages ve Teknik Sinyaller", "📐", "technical", "Piyasa & Teknik Analiz"),
        ToolItemData("Piyasa Tarayıcı", "11 Hazır Strateji ile Anlık Piyasa Taraması", "🔍", "scanner", "Piyasa & Teknik Analiz"),
        ToolItemData("Süper Filtre", "Çok Kriterli Gelişmiş Şirket Filtreleme", "🎯", "screener", "Piyasa & Teknik Analiz"),
        ToolItemData("Global Piyasalar", "Küresel Endeksler, Sektörler ve Dünya Isı Haritası", "🌐", "global_markets", "Piyasa & Teknik Analiz"),
        ToolItemData("Makro Görünüm", "Enflasyon, Faiz ve Büyüme Göstergeleri (FRED)", "🌍", "macro_intelligence", "Piyasa & Teknik Analiz"),

        // Risk, Temettü & Strateji
        ToolItemData("Backtest Engine", "Geçmiş Veri ile Strateji Performans Testi", "🧪", "backtest", "Risk, Temettü & Strateji"),
        ToolItemData("Temettü Analizi", "Temettü Verimi, Büyüme ve Ödeme Takvimi", "💵", "dividend", "Risk, Temettü & Strateji"),
        ToolItemData("Risk Engine", "Volatilite, Riske Maruz Değer (VaR) ve Stres Testi", "⚠️", "risk", "Risk, Temettü & Strateji"),
        ToolItemData("Master Skor", "Temel ve Teknik Analiz Puanlama Sistemi", "🏆", "master_score", "Risk, Temettü & Strateji"),
        ToolItemData("Portföy Optimizasyonu", "Sharpe Oranı ve Verimli Sınır Analizi", "⚖️", "portfolio_optimization", "Risk, Temettü & Strateji"),
        ToolItemData("Fon Zekası", "TEFAS Yatırım Fonları için Yapay Zeka Analizi", "🤖", "fund_intelligence/TTE", "Risk, Temettü & Strateji")
    )

    val groupedTools = tools.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Tüm Finansal Araçlar",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope)
                        )
                        Text(
                            "Porsuk Finans platformundaki tüm gelişmiş modüller",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontFamily = Manrope),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedTools.forEach { (category, items) ->
                item(key = "category_$category") {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Manrope,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(items, key = { it.route }) { tool ->
                    ToolRowCard(tool = tool, onClick = { onToolClick(tool.route) })
                }
            }
        }
    }
}

@Composable
private fun ToolRowCard(
    tool: ToolItemData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black.copy(0.02f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(tool.emoji, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
