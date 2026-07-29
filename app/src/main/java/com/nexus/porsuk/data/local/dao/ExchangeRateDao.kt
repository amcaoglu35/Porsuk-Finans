package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    fun getAllExchangeRates(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE currencyPair = :currencyPair")
    fun getExchangeRate(currencyPair: String): Flow<ExchangeRateEntity?>

    @Query("SELECT * FROM exchange_rates WHERE currencyPair = :currencyPair")
    suspend fun getExchangeRateDirect(currencyPair: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(rate: ExchangeRateEntity)

    @Query("DELETE FROM exchange_rates")
    suspend fun clearExchangeRates()
}
