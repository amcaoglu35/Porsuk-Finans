package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.calculator.PortfolioCalculatorEngine
import com.nexus.porsuk.data.local.dao.PortfolioEngineDao
import com.nexus.porsuk.data.local.entity.PortfolioAssetEntity
import com.nexus.porsuk.data.local.entity.PortfolioEngineEntity
import com.nexus.porsuk.data.local.entity.PortfolioTransactionEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioEngineRepositoryImpl @Inject constructor(
    private val dao: PortfolioEngineDao,
    private val logger: DataLogger
) : PortfolioEngineRepository {

    override fun getAllPortfolios(): Flow<List<PortfolioSummary>> {
        return dao.getAllPortfolios().map { list -> list.map { it.toDomainModel() } }
    }

    override fun getPortfolioById(portfolioId: String): Flow<PortfolioSummary?> {
        return dao.getPortfolioById(portfolioId).map { it?.toDomainModel() }
    }

    override suspend fun createPortfolio(name: String, description: String, type: PortfolioType, currency: String): String {
        val newId = UUID.randomUUID().toString()
        val entity = PortfolioEngineEntity(
            portfolioId = newId,
            name = name,
            description = description,
            portfolioType = type.name,
            currency = currency
        )
        dao.insertPortfolio(entity)
        logger.logSyncEvent("PortfolioEngine", "Yeni portföy oluşturuldu: $name (ID: $newId)")
        return newId
    }

    override suspend fun updatePortfolio(portfolio: PortfolioSummary) {
        dao.updatePortfolio(portfolio.toEntityModel())
    }

    override suspend fun deletePortfolio(portfolioId: String) {
        val entity = dao.getPortfolioById(portfolioId).first()
        if (entity != null) {
            dao.deletePortfolio(entity)
            logger.logSyncEvent("PortfolioEngine", "Portföy silindi: ${entity.name}")
        }
    }
}

@Singleton
class PortfolioTransactionRepositoryImpl @Inject constructor(
    private val dao: PortfolioEngineDao,
    private val calculatorEngine: PortfolioCalculatorEngine,
    private val logger: DataLogger
) : PortfolioTransactionRepository {

    override fun getTransactionsForPortfolio(portfolioId: String): Flow<List<PortfolioTransaction>> {
        return dao.getTransactionsForPortfolio(portfolioId).map { list -> list.map { it.toDomainModel() } }
    }

    override fun getTransactionsForAsset(portfolioId: String, symbol: String): Flow<List<PortfolioTransaction>> {
        return dao.getTransactionsForAsset(portfolioId, symbol).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun addTransaction(transaction: PortfolioTransaction) {
        // 1. İşlemi kaydet
        dao.insertTransaction(transaction.toEntityModel())
        logger.logSyncEvent("PortfolioEngine", "İşlem eklendi: ${transaction.type.name} - ${transaction.symbol}")

        // 2. İlgili varlığın ortalama maliyetini ve miktarını yeniden hesapla
        val allAssetTxs = dao.getTransactionsForAsset(transaction.portfolioId, transaction.symbol).first()
        val (avgCost, newQty) = calculatorEngine.calculateAverageCostAndQuantity(allAssetTxs)

        val existingAsset = dao.getAssetBySymbol(transaction.portfolioId, transaction.symbol)
        if (newQty > 0.0) {
            val currentPrice = existingAsset?.currentPrice ?: transaction.price
            val totalValue = newQty * currentPrice
            val totalCost = newQty * avgCost
            val pnl = totalValue - totalCost
            val pnlPct = if (totalCost > 0) (pnl / totalCost) * 100.0 else 0.0

            val updatedAsset = PortfolioAssetEntity(
                assetId = existingAsset?.assetId ?: 0,
                portfolioId = transaction.portfolioId,
                symbol = transaction.symbol,
                name = existingAsset?.name ?: transaction.symbol,
                quantity = newQty,
                averageCost = avgCost,
                currentPrice = currentPrice,
                totalValue = totalValue,
                totalCost = totalCost,
                profitLoss = pnl,
                profitPercent = pnlPct,
                assetCategory = AssetCategory.fromSymbol(transaction.symbol).name,
                purchaseDate = existingAsset?.purchaseDate ?: transaction.timestamp,
                lastUpdated = System.currentTimeMillis()
            )
            dao.insertAsset(updatedAsset)
        } else if (existingAsset != null) {
            // Miktar 0'landıysa varlığı sil
            dao.deleteAsset(existingAsset)
        }
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        // İşlem silme ve varlık yeniden dengeleme
    }
}

@Singleton
class PortfolioAssetRepositoryImpl @Inject constructor(
    private val dao: PortfolioEngineDao
) : PortfolioAssetRepository {

    override fun getAssetsForPortfolio(portfolioId: String): Flow<List<PortfolioAsset>> {
        return dao.getAssetsForPortfolio(portfolioId).map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun updateAssetPrice(portfolioId: String, symbol: String, currentPrice: Double) {
        val asset = dao.getAssetBySymbol(portfolioId, symbol)
        if (asset != null) {
            val newTotalValue = asset.quantity * currentPrice
            val newPnL = newTotalValue - asset.totalCost
            val newPnLPct = if (asset.totalCost > 0) (newPnL / asset.totalCost) * 100.0 else 0.0

            val updated = asset.copy(
                currentPrice = currentPrice,
                totalValue = newTotalValue,
                profitLoss = newPnL,
                profitPercent = newPnLPct,
                lastUpdated = System.currentTimeMillis()
            )
            dao.updateAsset(updated)
        }
    }

    override suspend fun deleteAsset(portfolioId: String, symbol: String) {
        val asset = dao.getAssetBySymbol(portfolioId, symbol)
        if (asset != null) {
            dao.deleteAsset(asset)
        }
    }
}

@Singleton
class PortfolioPerformanceRepositoryImpl @Inject constructor(
    private val dao: PortfolioEngineDao,
    private val calculatorEngine: PortfolioCalculatorEngine
) : PortfolioPerformanceRepository {

    override fun getPortfolioBreakdown(portfolioId: String): Flow<PortfolioBreakdown> {
        return dao.getAssetsForPortfolio(portfolioId).map { assets ->
            calculatorEngine.calculateBreakdowns(assets)
        }
    }

    override suspend fun getRealizedProfitLoss(portfolioId: String): Double {
        val transactions = dao.getTransactionsForPortfolio(portfolioId).first()
        return calculatorEngine.calculateRealizedProfitLoss(transactions)
    }
}

// Mappers
private fun PortfolioEngineEntity.toDomainModel() = PortfolioSummary(
    id = portfolioId,
    name = name,
    description = description,
    type = PortfolioType.valueOf(portfolioType),
    currency = currency,
    totalValuation = totalValuation,
    totalCost = totalCost,
    dailyProfitLoss = dailyProfitLoss,
    totalProfitLoss = totalProfitLoss,
    returnRatePct = returnRatePct,
    totalDividends = totalDividends,
    riskScore = riskScore,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun PortfolioSummary.toEntityModel() = PortfolioEngineEntity(
    portfolioId = id,
    name = name,
    description = description,
    portfolioType = type.name,
    currency = currency,
    totalValuation = totalValuation,
    totalCost = totalCost,
    dailyProfitLoss = dailyProfitLoss,
    totalProfitLoss = totalProfitLoss,
    returnRatePct = returnRatePct,
    totalDividends = totalDividends,
    riskScore = riskScore,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

private fun PortfolioAssetEntity.toDomainModel() = PortfolioAsset(
    id = assetId,
    portfolioId = portfolioId,
    symbol = symbol,
    name = name,
    quantity = quantity,
    averageCost = averageCost,
    currentPrice = currentPrice,
    totalValue = totalValue,
    totalCost = totalCost,
    profitLoss = profitLoss,
    profitPercent = profitPercent,
    assetCategory = AssetCategory.valueOf(assetCategory),
    purchaseDate = purchaseDate,
    lastUpdated = lastUpdated
)

private fun PortfolioTransactionEntity.toDomainModel() = PortfolioTransaction(
    transactionId = transactionId,
    portfolioId = portfolioId,
    symbol = symbol,
    type = TransactionType.valueOf(transactionType),
    quantity = quantity,
    price = price,
    totalAmount = totalAmount,
    fee = fee,
    tax = tax,
    timestamp = timestamp,
    notes = notes
)

private fun PortfolioTransaction.toEntityModel() = PortfolioTransactionEntity(
    transactionId = transactionId,
    portfolioId = portfolioId,
    symbol = symbol,
    transactionType = type.name,
    quantity = quantity,
    price = price,
    totalAmount = totalAmount,
    fee = fee,
    tax = tax,
    timestamp = timestamp,
    notes = notes
)
