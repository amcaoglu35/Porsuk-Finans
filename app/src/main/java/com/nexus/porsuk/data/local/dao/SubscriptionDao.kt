package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Porsuk Premium Membership — Room DAO Sorguları
 */
@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM engine_subscription_entitlements WHERE user_id = :userId LIMIT 1")
    fun getSubscription(userId: String = "local_user"): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(sub: SubscriptionEntity)
}
