package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KaziRepository @Inject constructor(private val assetDao: AssetDao) {

    fun getLatestKaziRun(): Flow<KaziRun?> = assetDao.getLatestKaziRun()
    
    suspend fun startKaziRun(riskProfile: String, horizon: String, capital: Double?, excludedSectors: String): Long {
        return assetDao.insertKaziRun(KaziRun(
            status = "RUNNING",
            riskProfile = riskProfile,
            horizon = horizon,
            capital = capital,
            excludedSectors = excludedSectors
        ))
    }

    suspend fun updateKaziRunStatus(runId: Int, status: String, step: Int) {
        assetDao.getKaziRunDirect(runId)?.let {
            assetDao.updateKaziRun(it.copy(status = status, currentStep = step))
        }
    }

    suspend fun completeKaziRun(runId: Int) {
        assetDao.getKaziRunDirect(runId)?.let {
            assetDao.updateKaziRun(it.copy(status = "COMPLETED", currentStep = 6, completedAt = System.currentTimeMillis()))
        }
    }

    suspend fun failKaziRun(runId: Int) {
        assetDao.getKaziRunDirect(runId)?.let {
            assetDao.updateKaziRun(it.copy(status = "FAILED"))
        }
    }

    fun getCandidatesForRun(runId: Int): Flow<List<KaziCandidate>> = assetDao.getCandidatesForRun(runId)
    
    suspend fun saveCandidates(candidates: List<KaziCandidate>) {
        assetDao.insertKaziCandidates(candidates)
    }

    fun getKaziBasketForRun(runId: Int): Flow<KaziBasket?> = assetDao.getKaziBasketForRun(runId)
    
    suspend fun saveKaziBasket(basket: KaziBasket, items: List<KaziBasketItem>) {
        val basketId = assetDao.insertKaziBasket(basket).toInt()
        assetDao.insertKaziBasketItems(items.map { it.copy(basketId = basketId) })
    }

    fun getKaziBasketItems(basketId: Int): Flow<List<KaziBasketItem>> = assetDao.getKaziBasketItems(basketId)

    // Kazi Watches
    fun getAllKaziWatches(): Flow<List<KaziWatch>> = assetDao.getAllKaziWatches()
    suspend fun insertKaziWatch(watch: KaziWatch) = assetDao.insertKaziWatch(watch)

    suspend fun addToPortfolio(kaziBasketId: Int, basketName: String): Int {
        val kaziItems = assetDao.getKaziBasketItems(kaziBasketId).first()
        val kaziBasket = assetDao.getKaziBasketById(kaziBasketId) ?: return -1

        // Resolve market from items or use BIST
        val companies = assetDao.getAllCompaniesDirect()
        val firstSym = kaziItems.firstOrNull()?.symbol
        val market = companies.find { it.symbol == firstSym }?.market ?: "BIST"

        val basketId = assetDao.insertBasket(Basket(
            name = basketName,
            market = market
        )).toInt()

        kaziItems.forEach { item ->
            val comp = companies.find { it.symbol == item.symbol }
            assetDao.insertBasketItem(BasketItem(
                basketId = basketId,
                symbol = item.symbol,
                quantity = 10.0, // Default lot
                buyPrice = comp?.currentPrice ?: 100.0,
                buyDate = System.currentTimeMillis()
            ))
        }
        return basketId
    }
}
