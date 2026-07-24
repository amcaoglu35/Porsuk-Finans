package com.nexus.porsuk.data.calculator

import com.nexus.porsuk.data.local.entity.PortfolioAssetEntity
import com.nexus.porsuk.data.local.entity.PortfolioTransactionEntity
import com.nexus.porsuk.domain.model.AssetCategory
import com.nexus.porsuk.domain.model.BreakdownItem
import com.nexus.porsuk.domain.model.PortfolioBreakdown
import com.nexus.porsuk.domain.model.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Portfolio Engine — Hesaplama Motoru (Portfolio Calculator Engine)
 *
 * Matematiksel ve finansal hesaplamaları yürütür:
 * 1. Ortalama Maliyet (Weighted Average Cost)
 * 2. Gerçekleşen (Realized) ve Gerçekleşmemiş (Unrealized) Kar/Zarar
 * 3. Günlük ve Toplam Portföy Değerlemesi
 * 4. 5 Boyutlu Dağılım Analizi (Varlık Tipi, Sektör, Ülke, Para Birimi, Nakit Oranı)
 */
@Singleton
class PortfolioCalculatorEngine @Inject constructor() {

    /**
     * İşlem geçmişine göre ortalama maliyeti ve kalan lot miktarını hesaplar.
     */
    fun calculateAverageCostAndQuantity(transactions: List<PortfolioTransactionEntity>): Pair<Double, Double> {
        var totalQuantity = 0.0
        var totalCost = 0.0

        // İşlemleri kronolojik sıraya sok (Eskiden yeniye)
        val sortedTransactions = transactions.sortedBy { it.timestamp }

        sortedTransactions.forEach { tx ->
            val type = TransactionType.valueOf(tx.transactionType)
            when (type) {
                TransactionType.BUY, TransactionType.RIGHTS_ISSUE_PAID -> {
                    val txTotal = (tx.quantity * tx.price) + tx.fee + tx.tax
                    totalCost += txTotal
                    totalQuantity += tx.quantity
                }
                TransactionType.BONUS_ISSUE_FREE -> {
                    totalQuantity += tx.quantity // Bedelsiz maliyeti artırmaz, adedi artırıp ortalama maliyeti düşürür
                }
                TransactionType.SELL -> {
                    if (totalQuantity > 0) {
                        val avgCostBeforeSell = totalCost / totalQuantity
                        totalQuantity = (totalQuantity - tx.quantity).coerceAtLeast(0.0)
                        totalCost = (totalQuantity * avgCostBeforeSell).coerceAtLeast(0.0)
                    }
                }
                else -> { /* Temettü, Komisyon, Vergi portföy seviyesinde işlenir */ }
            }
        }

        val averageCost = if (totalQuantity > 0) totalCost / totalQuantity else 0.0
        return Pair(averageCost, totalQuantity)
    }

    /**
     * Satış işlemlerinden elde edilen gerçekleşen (Realized) Kar/Zararı hesaplar.
     */
    fun calculateRealizedProfitLoss(transactions: List<PortfolioTransactionEntity>): Double {
        var realizedPnL = 0.0
        var totalQuantity = 0.0
        var totalCost = 0.0

        val sortedTransactions = transactions.sortedBy { it.timestamp }

        sortedTransactions.forEach { tx ->
            val type = TransactionType.valueOf(tx.transactionType)
            when (type) {
                TransactionType.BUY -> {
                    totalCost += (tx.quantity * tx.price) + tx.fee
                    totalQuantity += tx.quantity
                }
                TransactionType.SELL -> {
                    if (totalQuantity > 0) {
                        val avgCost = totalCost / totalQuantity
                        val saleIncome = (tx.quantity * tx.price) - tx.fee - tx.tax
                        val costOfSoldAsset = tx.quantity * avgCost
                        realizedPnL += (saleIncome - costOfSoldAsset)

                        totalQuantity -= tx.quantity
                        totalCost -= costOfSoldAsset
                    }
                }
                TransactionType.DIVIDEND -> {
                    realizedPnL += (tx.totalAmount - tx.tax) // Net temettü geliri gerçekleşen karedir
                }
                else -> {}
            }
        }

        return realizedPnL
    }

    /**
     * Varlık listesine göre 5 boyutlu portföy dağılımlarını hesaplar.
     */
    fun calculateBreakdowns(assets: List<PortfolioAssetEntity>): PortfolioBreakdown {
        val grandTotalValuation = assets.sumOf { it.totalValue }
        if (grandTotalValuation <= 0.0) {
            return PortfolioBreakdown(emptyList(), emptyList(), emptyList(), emptyList(), 0.0)
        }

        // 1. Varlık Tipi Dağılımı
        val assetCategoryBreakdown = assets.groupBy { it.assetCategory }
            .map { (category, list) ->
                val sum = list.sumOf { it.totalValue }
                BreakdownItem(
                    categoryName = category,
                    totalValue = sum,
                    percentage = (sum / grandTotalValuation) * 100.0
                )
            }.sortedByDescending { it.totalValue }

        // 2. Para Birimi Dağılımı
        val currencyBreakdown = assets.groupBy { if (it.symbol.endsWith(".IS")) "TRY" else "USD" }
            .map { (curr, list) ->
                val sum = list.sumOf { it.totalValue }
                BreakdownItem(
                    categoryName = curr,
                    totalValue = sum,
                    percentage = (sum / grandTotalValuation) * 100.0
                )
            }.sortedByDescending { it.totalValue }

        // 3. Ülke Dağılımı
        val countryBreakdown = assets.groupBy { if (it.symbol.endsWith(".IS")) "Türkiye" else "ABD" }
            .map { (country, list) ->
                val sum = list.sumOf { it.totalValue }
                BreakdownItem(
                    categoryName = country,
                    totalValue = sum,
                    percentage = (sum / grandTotalValuation) * 100.0
                )
            }.sortedByDescending { it.totalValue }

        // 4. Nakit Oranı
        val cashTotal = assets.filter { it.assetCategory == AssetCategory.MUTUAL_FUND.name && it.symbol == "CASH" }
            .sumOf { it.totalValue }
        val cashRatioPct = (cashTotal / grandTotalValuation) * 100.0

        return PortfolioBreakdown(
            assetCategoryBreakdown = assetCategoryBreakdown,
            sectorBreakdown = emptyList(),
            countryBreakdown = countryBreakdown,
            currencyBreakdown = currencyBreakdown,
            cashRatioPercentage = cashRatioPct
        )
    }
}
