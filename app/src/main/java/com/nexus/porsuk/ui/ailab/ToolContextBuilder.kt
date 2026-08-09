package com.nexus.porsuk.ui.ailab

import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject

class ToolContextBuilder @Inject constructor(
    private val repository: FinanceRepository,
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase
) {
    suspend fun buildPortfolioContext(): String {
        val summary = getPortfolioSummaryUseCase().first()
        val items = repository.getConsolidatedAssetsFlow().first()
        return """
        Portföy Bağlamı:
        - Toplam Varlık Değeri: ₺${String.format(Locale.US, "%,.2f", summary.totalBalance)}
        - Toplam Maliyet: ₺${String.format(Locale.US, "%,.2f", summary.totalCost)}
        - Toplam Kar/Zarar: %${String.format(Locale.US, "%.2f", summary.totalChangePercent)}
        - Aktif Pozisyon Sayısı: ${items.size}
        - Varlıklar: ${items.joinToString { "${it.symbol} (${String.format(Locale.US, "%.1f", it.quantity)} adet)" }}
        """.trimIndent()
    }

    suspend fun buildWatchlistContext(): String {
        val watchlist = repository.watchlist.first()
        if (watchlist.isEmpty()) return "İzleme listesi henüz boş."
        return """
        İzleme Listesi Bağlamı:
        - Takip Edilen Hisseler: ${watchlist.joinToString { it.symbol }}
        """.trimIndent()
    }

    suspend fun buildMarketContext(): String {
        val companies = repository.allCompanies.first().take(10)
        return """
        Piyasa Bağlamı (Popüler Hisseler):
        ${companies.joinToString("\n") { "- ${it.symbol}: ${it.name} (Fiyat: ${it.currentPrice}, Değişim: %${it.changePercent})" }}
        """.trimIndent()
    }

    suspend fun buildSectorContext(): String {
        val companies = repository.allCompanies.first()
        val sectorMap = companies.groupBy { it.sector }
        return """
        Sektörel Bağlam:
        ${sectorMap.map { (sector, list) -> 
            val avgChange = list.map { it.changePercent }.average()
            "- $sector: ${list.size} şirket, ortalama günlük değişim: %${String.format(Locale.US, "%.2f", avgChange)}" 
        }.joinToString("\n")}
        """.trimIndent()
    }
}
