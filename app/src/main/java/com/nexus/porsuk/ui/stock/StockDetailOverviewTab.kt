package com.nexus.porsuk.ui.stock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.remote.RichCompanyDetails
import com.nexus.porsuk.ui.common.CompanyAboutCard
import com.nexus.porsuk.ui.common.FormattedCurrencyEquivalents
import com.nexus.porsuk.ui.common.FormattedDetailStatsGrid
import com.nexus.porsuk.ui.common.PremiumLiveCanvasChart
import com.nexus.porsuk.ui.common.PremiumNewsSection
import com.nexus.porsuk.ui.theme.*

@Composable
fun StockDetailOverviewTab(
    symbol: String,
    market: String,
    price: Double,
    change: Double,
    company: Company?,
    cachedInfo: CachedCompanyInfo?,
    historicalPrices: List<Double>,
    isHistoryLoading: Boolean,
    exchangeRates: Map<String, Double>,
    numberFormat: String,
    news: List<NewsItemEntity>,
    offlineData: RichCompanyDetails,
    selectedInterval: String,
    onIntervalSelected: (String) -> Unit,
    onNavigateToChart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Çevrimdışı mod göstergesi (Offline Banner)
        if (cachedInfo == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📡", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "Çevrimdışı Mod (Zenginleştirilmiş Veri Paketi)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AmberWarning,
                            fontFamily = Manrope
                        )
                        Text(
                            text = "Ağ bağlantısı olmadığından veriler yerel hafızadan sunulmaktadır.",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText,
                            fontFamily = Manrope
                        )
                    }
                }
            }
        }

        // Zaman Dilimi Seçici
        IntervalSelector(
            selectedInterval = selectedInterval,
            onIntervalSelected = onIntervalSelected
        )

        // Canlı & İnteraktif Mum / Fiyat Grafiği
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            PremiumLiveCanvasChart(
                prices = historicalPrices,
                color = if (change >= 0) PositiveGreen else NegatifRed,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Detaylı İstatistikler Grid
        FormattedDetailStatsGrid(
            info = cachedInfo,
            fallback = offlineData
        )

        // FX Karşılıkları (Dolar / Euro / Altın cinsi)
        FormattedCurrencyEquivalents(
            price = price,
            market = market,
            exchangeRates = exchangeRates
        )

        // Şirket Hakkında Kartı
        CompanyAboutCard(
            symbol = symbol,
            info = cachedInfo
        )

        // Haberler Bölümü
        PremiumNewsSection(
            news = news,
            fallback = offlineData.news
        )
    }
}
