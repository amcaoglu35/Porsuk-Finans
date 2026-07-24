package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.BrokerAccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Broker Integration Hub — Room DAO Sorguları
 */
@Dao
interface BrokerAccountDao {

    @Query("SELECT * FROM engine_broker_accounts ORDER BY last_sync_timestamp DESC")
    fun getAllSavedAccounts(): Flow<List<BrokerAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: BrokerAccountEntity)

    @Query("DELETE FROM engine_broker_accounts WHERE account_id = :accountId")
    suspend fun deleteAccount(accountId: String)
}
