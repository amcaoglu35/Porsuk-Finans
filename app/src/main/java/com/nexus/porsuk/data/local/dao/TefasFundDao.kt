package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.TefasFundEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk TEFAS Engine — Fon Veritabanı DAO Sorguları
 */
@Dao
interface TefasFundDao {

    @Query("SELECT * FROM tefas_funds WHERE is_active = 1 ORDER BY code ASC")
    fun getAllActiveFunds(): Flow<List<TefasFundEntity>>

    @Query("SELECT * FROM tefas_funds WHERE code = :code LIMIT 1")
    fun getFundByCode(code: String): Flow<TefasFundEntity?>

    @Query("SELECT * FROM tefas_funds WHERE umbrella_fund = :umbrellaFund AND is_active = 1 ORDER BY total_assets DESC")
    fun getFundsByUmbrella(umbrellaFund: String): Flow<List<TefasFundEntity>>

    @Query("SELECT * FROM tefas_funds WHERE manager = :manager AND is_active = 1 ORDER BY total_assets DESC")
    fun getFundsByManager(manager: String): Flow<List<TefasFundEntity>>

    @Query("SELECT * FROM tefas_funds WHERE code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%' OR manager LIKE '%' || :query || '%'")
    fun searchFunds(query: String): Flow<List<TefasFundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFunds(funds: List<TefasFundEntity>)

    @Update
    suspend fun updateFund(fund: TefasFundEntity)

    @Query("UPDATE tefas_funds SET is_active = 0 WHERE code IN (:inactiveCodes)")
    suspend fun markFundsAsInactive(inactiveCodes: List<String>)

    @Query("SELECT COUNT(*) FROM tefas_funds WHERE is_active = 1")
    suspend fun getActiveFundCount(): Int
}
