package com.nexus.porsuk.domain.model

/**
 * Porsuk Portfolio Engine — Dağılım Kalemi (Breakdown Item)
 */
data class BreakdownItem(
    val categoryName: String,
    val totalValue: Double,
    val percentage: Double
)

/**
 * Porsuk Portfolio Engine — Portföy Dağılım Analizleri (5 Boyutlu Dağılım)
 */
data class PortfolioBreakdown(
    val assetCategoryBreakdown: List<BreakdownItem>, // Hisse, Fon, Kripto, Döviz, Nakit
    val sectorBreakdown: List<BreakdownItem>, // Ulaşım, Bankacılık, Teknoloji vb.
    val countryBreakdown: List<BreakdownItem>, // TR, US, EU
    val currencyBreakdown: List<BreakdownItem>, // TRY, USD, EUR
    val cashRatioPercentage: Double = 0.0
)

/**
 * Portföy Özeti Domain Modeli
 */
data class PortfolioSummary(
    val id: String,
    val name: String,
    val description: String,
    val type: PortfolioType,
    val currency: String,
    val totalValuation: Double,
    val totalCost: Double,
    val dailyProfitLoss: Double,
    val totalProfitLoss: Double,
    val returnRatePct: Double,
    val totalDividends: Double,
    val riskScore: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Portföy Varlık Kalemi Domain Modeli
 */
data class PortfolioAsset(
    val id: Long,
    val portfolioId: String,
    val symbol: String,
    val name: String,
    val quantity: Double,
    val averageCost: Double,
    val currentPrice: Double,
    val totalValue: Double,
    val totalCost: Double,
    val profitLoss: Double,
    val profitPercent: Double,
    val assetCategory: AssetCategory,
    val purchaseDate: Long,
    val lastUpdated: Long
)

/**
 * Portföy İşlem Geçmişi Domain Modeli
 */
data class PortfolioTransaction(
    val transactionId: Long,
    val portfolioId: String,
    val symbol: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val totalAmount: Double,
    val fee: Double,
    val tax: Double,
    val timestamp: Long,
    val notes: String? = null
)
