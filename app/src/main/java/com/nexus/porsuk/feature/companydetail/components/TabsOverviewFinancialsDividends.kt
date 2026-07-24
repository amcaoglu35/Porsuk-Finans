package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.local.entity.DividendEntity
import com.nexus.porsuk.data.local.entity.EarningsEntity

/**
 * 1. Sekme — Genel Bilgiler (TabOverviewContent)
 */
@Composable
fun TabOverviewContent(
    company: CompanyEntity?,
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
                text = "Şirket Hakkında",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${company?.companyName ?: "Şirket"}, ${company?.sector ?: "Finans"} alanında faaliyet gösteren ve ${company?.exchange ?: "BIST"} borsasında işlem gören öncü kuruluşlardan biridir.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            InfoRow("ISIN Kodu", company?.isin ?: "TRATHYAO91M5")
            InfoRow("Merkez Ülke", company?.country ?: "Türkiye")
            InfoRow("Sektör", company?.sector ?: "Ulaşım / Havacılık")
            InfoRow("İş Kolu / Industry", company?.industry ?: "Hava Yolu Taşımacılığı")
            InfoRow("Çalışan Sayısı", "38,500+")
            InfoRow("CEO / Genel Müdür", "Ahmet Bolat (Temsili)")
            InfoRow("Web Sitesi", company?.website ?: "https://www.thy.com")
        }
    }
}

/**
 * 2. Sekme — Finansallar (TabFinancialsContent)
 */
@Composable
fun TabFinancialsContent(
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
                text = "Özet Finansal Tablo (Son Çeyrek)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Toplam Gelir (Hasılat)", "504.2 Milyar TL")
            InfoRow("Net Kar", "163.8 Milyar TL")
            InfoRow("FAVÖK (EBITDA)", "122.4 Milyar TL")
            InfoRow("Toplam Borç", "210.5 Milyar TL")
            InfoRow("Özsermaye", "295.1 Milyar TL")
            InfoRow("Nakit ve Nakit Benzerleri", "84.2 Milyar TL")
            InfoRow("Net Kar Marjı", "%32.4")
            InfoRow("Özsermaye Karlılığı (ROE)", "%55.2")
        }
    }
}

/**
 * 3. Sekme — Temettü (TabDividendsContent)
 */
@Composable
fun TabDividendsContent(
    dividends: List<DividendEntity>,
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
                text = "Temettü Geçmişi & Verim",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Ortalama Temettü Verimi", "%4.2")
            InfoRow("Son Dağıtım Tarihi", "15 Mayıs 2025")
            InfoRow("Hisse Başı Net Ödeme", "6.50 TRY")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "Geçmiş Ödemeler",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (dividends.isEmpty()) {
                Text(
                    text = "2025: 6.50 TRY / Hisse (%4.2 Verim)\n2024: 5.20 TRY / Hisse (%3.8 Verim)\n2023: 4.10 TRY / Hisse (%4.0 Verim)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                dividends.forEach { d ->
                    InfoRow("Ödeme Tarihi: ${d.paymentDate}", "${d.amount} ${d.currency}")
                }
            }
        }
    }
}

/**
 * 4. Sekme — Kazançlar / Bilanço (TabEarningsContent)
 */
@Composable
fun TabEarningsContent(
    earnings: List<EarningsEntity>,
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
                text = "Çeyreklik Bilanço ve Beklentiler",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoRow("Son EPS (Hisse Başı Kar)", "32.40 TRY")
            InfoRow("Piyasa Beklentisi (Estimate)", "30.10 TRY")
            InfoRow("Sürpriz Oranı (Surprise)", "+%7.6 (Beklenti Üstü)")

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "Geçmiş Çeyrek Sonuçları",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "2025 Q1: EPS 32.40 (Beklenen: 30.10) - BAŞARILI\n2024 Q4: EPS 28.50 (Beklenen: 29.00) - PARALEL\n2024 Q3: EPS 41.20 (Beklenen: 38.50) - BAŞARILI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
