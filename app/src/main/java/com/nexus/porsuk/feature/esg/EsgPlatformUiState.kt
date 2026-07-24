package com.nexus.porsuk.feature.esg

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk ESG & Sustainability Intelligence Platform — UI Ekran Durumu (EsgPlatformUiState)
 */
data class EsgPlatformUiState(
    val selectedProvider: EsgProviderType = EsgProviderType.MSCI_ESG,
    val esgScore: EsgScoreData = EsgScoreData(),
    val environmentalPillar: EnvironmentalPillar = EnvironmentalPillar(),
    val socialPillar: SocialPillar = SocialPillar(),
    val governancePillar: GovernancePillar = GovernancePillar(),
    val controversyAlerts: List<EsgControversyAlert> = emptyList(),
    val futureStubs: EsgFutureStubs = EsgFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
