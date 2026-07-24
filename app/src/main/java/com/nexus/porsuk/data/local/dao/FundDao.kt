package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Finans — TEFAS Yatırım Fonları DAO
 */
@Dao
interface FundDao {

    @Query("SELECT * FROM db_funds WHERE is_active = 1 ORDER BY code ASC")
    fun getAllFunds(): Flow<List<FundEntity>>

    @Query("SELECT * FROM db_funds WHERE code = :code LIMIT 1")
    fun getFundByCode(code: String): Flow<FundEntity?>

    @Query("SELECT * FROM db_funds WHERE category = :category ORDER BY price DESC")
    fun getFundsByCategory(category: String): Flow<List<FundEntity>>

    @Query("SELECT * FROM db_funds WHERE code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchFunds(query: String): Flow<List<FundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: FundEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunds(funds: List<FundEntity>)

    @Update
    suspend fun updateFund(fund: FundEntity)

    @Delete
    suspend fun deleteFund(fund: FundEntity)
}
