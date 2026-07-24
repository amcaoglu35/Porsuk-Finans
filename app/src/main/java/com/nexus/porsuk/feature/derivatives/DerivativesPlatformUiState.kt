package com.nexus.porsuk.feature.derivatives

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Options, Futures & Derivatives Platform — UI Ekran Durumu (DerivativesPlatformUiState)
 */
data class DerivativesPlatformUiState(
    val selectedUnderlyingSymbol: String = "THYAO.IS",
    val activeProvider: DerivativesProviderType = DerivativesProviderType.VIOP_TURKEY,
    val selectedOptionTypeFilter: OptionType? = null,
    val optionChain: List<OptionContract> = emptyList(),
    val selectedContractGreeks: OptionGreeks = OptionGreeks(),
    val futuresContracts: List<FuturesContract> = emptyList(),
    val selectedStrategy: OptionStrategyType = OptionStrategyType.COVERED_CALL,
    val strategyRisk: OptionStrategyRisk = OptionStrategyRisk(),
    val pricingModel: OptionPricingModel = OptionPricingModel.BLACK_SCHOLES,
    val futureStubs: DerivativesFutureStubs = DerivativesFutureStubs(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
