package com.nexus.porsuk.feature.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.MembershipPlan
import com.nexus.porsuk.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Premium Membership — ViewModel
 *
 * Üyelik planlarını, izin kontrollerini ve plan yükseltme işlemlerini yönetir.
 */
@HiltViewModel
class UpgradeViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpgradeUiState())
    val uiState: StateFlow<UpgradeUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptionState()
    }

    fun selectPlan(plan: MembershipPlan) {
        _uiState.update { it.copy(selectedPlan = plan) }
    }

    fun upgradeCurrentPlan() {
        // [FIX-2] Disable upgrading until Google Play Billing is integrated
        _uiState.update { it.copy(errorMessage = "Ödeme sistemi yakında aktif edilecektir. İlginiz için teşekkürler!") }
        
        /* 
        val target = _uiState.value.selectedPlan
        viewModelScope.launch {
            subscriptionRepository.upgradePlan(target)
            loadSubscriptionState()
        }
        */
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadSubscriptionState() {
        viewModelScope.launch {
            subscriptionRepository.getActiveSubscription().collect { state ->
                _uiState.update {
                    it.copy(
                        currentPlan = state.activePlan,
                        permissions = state.allowedPermissions
                    )
                }
            }
        }
    }
}
