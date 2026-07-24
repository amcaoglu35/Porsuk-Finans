package com.nexus.porsuk.feature.subscription

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Premium Membership — UI Ekran Durumu (UpgradeUiState)
 */
data class UpgradeUiState(
    val currentPlan: MembershipPlan = MembershipPlan.PREMIUM,
    val selectedPlan: MembershipPlan = MembershipPlan.PRO,
    val availablePlans: List<MembershipPlan> = MembershipPlan.entries,
    val permissions: Set<FeaturePermission> = FeaturePermission.entries.toSet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
