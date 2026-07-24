package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.BacktestReportEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Backtesting Engine — Room DAO Sorguları
 */
@Dao
interface BacktestReportDao {

    @Query("SELECT * FROM engine_backtest_reports ORDER BY created_at DESC")
    fun getAllSavedReports(): Flow<List<BacktestReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: BacktestReportEntity)

    @Query("DELETE FROM engine_backtest_reports WHERE report_id = :reportId")
    suspend fun deleteReport(reportId: String)
}
