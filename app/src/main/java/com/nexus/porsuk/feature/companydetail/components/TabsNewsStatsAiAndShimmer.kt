package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.data.local.entity.NewsEntity

/**
 * 5. Sekme — Haberler ve KAP Duyuruları (TabNewsContent)
 */
@Composable
fun TabNewsContent(
    newsList: List<NewsEntity>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Son Haberler & KAP Duyuruları",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val sampleNews = if (newsList.isEmpty()) {
            listOf(
                NewsEntity(id = 1L, title = "THY 2025 Yılı Yeni Uçak Siparişlerini Düzenledi", summary = "Şirket küresel büyüme hedefleri kapsamında filosunu genişletiyor.", source = "KAP", publishedAt = System.currentTimeMillis(), url = ""),
                NewsEntity(id = 2L, title = "Yolcu Sayısında Rekor Artış Kaydedildi", summary = "Aylık yolcu sayısı geçen yılın aynı dönemine göre %12 arttı.", source = "Bloomberg HT", publishedAt = System.currentTimeMillis() - 86400000, url = "")
            )
        } else newsList

        sampleNews.forEach { news ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = news.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bugün",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 6. Sekme — İstatistikler (TabStatsContent)
 */
@Composable
fun TabStatsContent(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Piyasa ve Risk İstatistikleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Beta (Piyasa Duyarlılığı)", "1.12")
            InfoRow("Volatilite (Yıllık)", "%24.5")
            InfoRow("Ortalama Hacim (30 Günlük)", "42.8 Milyon Lot")
            InfoRow("52 Hafta En Yüksek", "315.00 TRY")
            InfoRow("52 Hafta En Düşük", "210.00 TRY")
            InfoRow("F/K Oranı (P/E Ratio)", "4.85")
            InfoRow("PD/DD Oranı (P/B Ratio)", "1.24")
        }
    }
}

/**
 * 7. Sekme — Geleceğe Hazır AI Orakul Stubs (TabAiOrakulContent)
 */
@Composable
fun TabAiOrakulContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AI Orakul & Gelişmiş Analizler (Gelecek Özellik)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        AiPlaceholderCard("🤖 Orakul AI Master Score", "Orakul Master Skoru: 88/100 (GÜÇLÜ AL). Temel veriler, bilanço karlılığı ve sektör ivmesi pozitif sinyal veriyor.")
        AiPlaceholderCard("📈 Teknik Analiz Modülü", "RSI: 58 (Nötr-Al), MACD: Pozitif Kesişim, Hareketli Ortalamalar: 50 ve 200 Günlük HO üstünde seyrediyor.")
        AiPlaceholderCard("🛡️ Risk ve Volatilite Analizi", "Risk Derecesi: Düşük-Orta. Şirketin borçluluk oranı güvenli bölgede yer alıyor.")
        AiPlaceholderCard("🔮 AI Fiyat Tahminleri", "Orakul AI 6 Aylık Hedef Fiyat: 340.00 TRY (Potansiyel: +%19.0).")
    }
}

@Composable
private fun AiPlaceholderCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Yüklenme Ekranı — Shimmer / Skeleton Effect
 */
@Composable
fun CompanyDetailShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
        )
    }
}
