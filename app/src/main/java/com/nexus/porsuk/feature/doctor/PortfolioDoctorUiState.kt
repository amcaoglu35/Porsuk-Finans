package com.nexus.porsuk.feature.doctor

import com.nexus.porsuk.domain.model.PortfolioDoctorReport

/**
 * Porsuk Portfolio Doctor Engine — UI Ekran Durumu (PortfolioDoctorUiState)
 */
data class PortfolioDoctorUiState(
    val report: PortfolioDoctorReport? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
