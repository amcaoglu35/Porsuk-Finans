package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Abonelik Deposu Sözleşmesi (SubscriptionRepository)
 */
interface SubscriptionRepository {
    fun getActiveSubscription(): Flow<EntitlementState>
    suspend fun upgradePlan(plan: MembershipPlan)
}

/**
 * 2. Faturalandırma Deposu Sözleşmesi (BillingRepository)
 */
interface BillingRepository {
    fun checkBillingStatus(): Flow<Boolean>
}

/**
 * 3. İzinler Deposu Sözleşmesi (EntitlementRepository)
 */
interface EntitlementRepository {
    fun hasPermission(permission: FeaturePermission): Flow<Boolean>
}

/**
 * 4. Üyelik Planları Deposu Sözleşmesi (MembershipRepository)
 */
interface MembershipRepository {
    fun getAvailablePlans(): Flow<List<MembershipPlan>>
}
