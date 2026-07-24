package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.SubscriptionDao
import com.nexus.porsuk.data.local.entity.SubscriptionEntity
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val dao: SubscriptionDao
) : SubscriptionRepository {

    override fun getActiveSubscription(): Flow<EntitlementState> {
        return dao.getSubscription().map { entity ->
            if (entity != null) {
                EntitlementState(
                    activePlan = MembershipPlan.valueOf(entity.planName),
                    isTrialActive = false,
                    daysRemainingInPeriod = 300,
                    allowedPermissions = FeaturePermission.entries.toSet()
                )
            } else {
                EntitlementState()
            }
        }
    }

    override suspend fun upgradePlan(plan: MembershipPlan) {
        val entity = SubscriptionEntity(
            userId = "local_user",
            planName = plan.name,
            isActive = true
        )
        dao.insertSubscription(entity)
    }
}

@Singleton
class BillingRepositoryImpl @Inject constructor() : BillingRepository {
    override fun checkBillingStatus(): Flow<Boolean> = flow {
        emit(true)
    }
}

@Singleton
class EntitlementRepositoryImpl @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : EntitlementRepository {
    override fun hasPermission(permission: FeaturePermission): Flow<Boolean> {
        return subscriptionRepository.getActiveSubscription().map { state ->
            state.allowedPermissions.contains(permission)
        }
    }
}

@Singleton
class MembershipRepositoryImpl @Inject constructor() : MembershipRepository {
    override fun getAvailablePlans(): Flow<List<MembershipPlan>> = flow {
        emit(MembershipPlan.entries)
    }
}
