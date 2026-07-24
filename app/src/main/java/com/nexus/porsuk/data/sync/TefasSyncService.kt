package com.nexus.porsuk.data.sync

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.TefasFundDao
import com.nexus.porsuk.data.local.entity.TefasFundEntity
import com.nexus.porsuk.data.logging.DataLogger
import com.nexus.porsuk.data.remote.datasource.TefasEngineRemoteDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk TEFAS Engine — Fon Senkronizasyon Servisi (TefasSyncService)
 *
 * TEFAS'taki tüm fonları indirir, değişen kayıtları günceller, yeni fonları ekler,
 * kapanan/tasfiye edilen fonları pasifleştirir ve detaylı rapor oluşturur.
 */
@Singleton
class TefasSyncService @Inject constructor(
    private val remoteDataSource: TefasEngineRemoteDataSource,
    private val tefasFundDao: TefasFundDao,
    private val logger: DataLogger
) {

    /**
     * Tüm TEFAS fonlarını indirir ve yerel veritabanı ile senkronize eder.
     */
    suspend fun syncAllFunds(): TefasSyncReport {
        val startTime = System.currentTimeMillis()

        return when (val result = remoteDataSource.fetchAllFunds()) {
            is NetworkResult.Success -> {
                val remoteList = result.data
                val existingFundsMap = tefasFundDao.getAllActiveFunds().first().associateBy { it.code }

                val newEntities = mutableListOf<TefasFundEntity>()
                val updateEntities = mutableListOf<TefasFundEntity>()
                val remoteCodesSet = remoteList.map { it.code }.toSet()

                remoteList.forEach { dto ->
                    val codeStr = dto.code.uppercase().trim()
                    val existing = existingFundsMap[codeStr]

                    val entity = TefasFundEntity(
                        id = existing?.id ?: 0,
                        code = codeStr,
                        name = dto.name,
                        founder = dto.founder ?: existing?.founder ?: "",
                        manager = dto.manager ?: existing?.manager ?: "",
                        umbrellaFund = dto.umbrellaFund ?: existing?.umbrellaFund ?: "",
                        fundType = dto.fundType ?: existing?.fundType ?: "",
                        riskLevel = dto.riskLevel ?: existing?.riskLevel ?: 1,
                        currency = dto.currency ?: existing?.currency ?: "TRY",
                        price = dto.price ?: existing?.price ?: 0.0,
                        totalAssets = dto.totalAssets ?: existing?.totalAssets ?: 0.0,
                        investorCount = dto.investorCount ?: existing?.investorCount ?: 0,
                        managementFee = dto.managementFee ?: existing?.managementFee ?: 0.0,
                        lastUpdated = System.currentTimeMillis(),
                        isActive = true
                    )

                    if (existing == null) {
                        newEntities.add(entity)
                    } else {
                        updateEntities.add(entity)
                    }
                }

                // Ekleme ve Güncellemeleri Room veritabanına kaydet
                if (newEntities.isNotEmpty()) tefasFundDao.insertOrUpdateFunds(newEntities)
                if (updateEntities.isNotEmpty()) tefasFundDao.insertOrUpdateFunds(updateEntities)

                // TEFAS sisteminde artık yer almayan kapanmış fonları pasifleştir
                val inactiveEntities = existingFundsMap.values
                    .filter { !remoteCodesSet.contains(it.code) }
                    .map { it.copy(isActive = false) }

                if (inactiveEntities.isNotEmpty()) {
                    tefasFundDao.insertOrUpdateFunds(inactiveEntities)
                }

                val duration = System.currentTimeMillis() - startTime
                val report = TefasSyncReport(
                    totalDownloaded = remoteList.size,
                    totalAdded = newEntities.size,
                    totalUpdated = updateEntities.size,
                    totalInactivated = inactiveEntities.size,
                    durationMs = duration,
                    isSuccess = true
                )

                logger.logSyncEvent("TefasSyncService", report.toFormattedLog())
                report
            }
            is NetworkResult.Error -> {
                logger.logError("TefasSyncService", "TEFAS senkronizasyon ağ hatası: ${result.message}")
                TefasSyncReport(isSuccess = false, errorMessage = result.message)
            }
            is NetworkResult.Exception -> {
                logger.logError("TefasSyncService", "TEFAS senkronizasyon istisnası", result.throwable)
                TefasSyncReport(isSuccess = false, errorMessage = result.throwable.localizedMessage)
            }
            is NetworkResult.Loading -> TefasSyncReport(isSuccess = false, errorMessage = "Loading")
        }
    }
}
