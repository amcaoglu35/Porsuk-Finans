package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.CorporateActionEntity
import com.nexus.porsuk.data.local.entity.DividendHistoryProEntity
import com.nexus.porsuk.data.local.entity.IpoIntelligenceEntity
import com.nexus.porsuk.domain.model.CorporateActionType
import com.nexus.porsuk.domain.model.IpoStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface IpoCorporateDao {

    // IPOs
    @Query("SELECT * FROM ipo_intelligence ORDER BY listingDate ASC")
    fun getAllIpos(): Flow<List<IpoIntelligenceEntity>>

    @Query("SELECT * FROM ipo_intelligence WHERE status = :status")
    fun getIposByStatus(status: IpoStatus): Flow<List<IpoIntelligenceEntity>>

    @Query("SELECT * FROM ipo_intelligence WHERE symbol = :symbol LIMIT 1")
    fun getIpoBySymbol(symbol: String): Flow<IpoIntelligenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIpo(ipo: IpoIntelligenceEntity)

    // Corporate Actions
    @Query("SELECT * FROM corporate_actions WHERE symbol = :symbol ORDER BY effective_date DESC")
    fun getActionsBySymbol(symbol: String): Flow<List<CorporateActionEntity>>

    @Query("SELECT * FROM corporate_actions WHERE type = :type ORDER BY effective_date DESC")
    fun getActionsByType(type: CorporateActionType): Flow<List<CorporateActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorporateAction(action: CorporateActionEntity)

    // Dividend History
    @Query("SELECT * FROM dividend_history_pro WHERE symbol = :symbol ORDER BY exDate DESC")
    fun getDividendHistory(symbol: String): Flow<List<DividendHistoryProEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividendHistory(items: List<DividendHistoryProEntity>)
}
