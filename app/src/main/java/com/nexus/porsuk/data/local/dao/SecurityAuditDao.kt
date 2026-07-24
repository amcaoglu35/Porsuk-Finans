package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.SecurityAuditEntity
import com.nexus.porsuk.data.local.entity.SecuritySessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Security Center — Room DAO Sorguları
 */
@Dao
interface SecurityAuditDao {

    @Query("SELECT * FROM engine_security_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<SecurityAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: SecurityAuditEntity)

    @Query("SELECT * FROM engine_security_sessions WHERE is_active = 1")
    fun getActiveSessions(): Flow<List<SecuritySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SecuritySessionEntity)
}
