package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DerivativesRepositoryImpl @Inject constructor() : DerivativesRepository {
    override fun getSupportedProviders(): List<DerivativesProviderType> = DerivativesProviderType.entries.toList()
    override fun getSupportedAssetTypes(): List<DerivativesAssetType> = DerivativesAssetType.entries.toList()
}

@Singleton
class OptionsRepositoryImpl @Inject constructor() : OptionsRepository {

    private val defaultOptionChain = listOf(
        OptionContract(
            optionId = "c_340",
            symbol = "THYAO_C_340_202608",
            underlyingSymbol = "THYAO.IS",
            type = OptionType.CALL,
            strikePrice = 340.0,
            bid = 19.20,
            ask = 19.60,
            lastPrice = 19.40,
            impliedVolatility = 0.318
        ),
        OptionContract(
            optionId = "c_350",
            symbol = "THYAO_C_350_202608",
            underlyingSymbol = "THYAO.IS",
            type = OptionType.CALL,
            strikePrice = 350.0,
            bid = 14.50,
            ask = 14.80,
            lastPrice = 14.65,
            impliedVolatility = 0.325
        ),
        OptionContract(
            optionId = "p_350",
            symbol = "THYAO_P_350_202608",
            underlyingSymbol = "THYAO.IS",
            type = OptionType.PUT,
            strikePrice = 350.0,
            bid = 12.10,
            ask = 12.40,
            lastPrice = 12.25,
            impliedVolatility = 0.332
        ),
        OptionContract(
            optionId = "p_360",
            symbol = "THYAO_P_360_202608",
            underlyingSymbol = "THYAO.IS",
            type = OptionType.PUT,
            strikePrice = 360.0,
            bid = 18.50,
            ask = 18.90,
            lastPrice = 18.70,
            impliedVolatility = 0.340
        )
    )

    private val optionsState = MutableStateFlow(defaultOptionChain)

    override fun getOptionChain(underlyingSymbol: String): Flow<List<OptionContract>> = optionsState.asStateFlow()

    override suspend fun getOptionDetails(optionId: String): OptionContract? {
        return optionsState.value.find { it.optionId == optionId }
    }
}

@Singleton
class FuturesRepositoryImpl @Inject constructor() : FuturesRepository {

    private val defaultFutures = listOf(
        FuturesContract(
            contractSymbol = "F_THYAO0826",
            underlyingName = "THYAO Ağustos 2026 Vadeli",
            provider = DerivativesProviderType.VIOP_TURKEY,
            lastPrice = 362.50,
            initialMarginTL = 8400.0
        ),
        FuturesContract(
            contractSymbol = "F_GARAN0826",
            underlyingName = "GARAN Ağustos 2026 Vadeli",
            provider = DerivativesProviderType.VIOP_TURKEY,
            lastPrice = 124.80,
            initialMarginTL = 3200.0
        )
    )

    private val futuresState = MutableStateFlow(defaultFutures)

    override fun getFuturesContracts(underlyingSymbol: String): Flow<List<FuturesContract>> = futuresState.asStateFlow()
}

@Singleton
class GreeksRepositoryImpl @Inject constructor() : GreeksRepository {
    override suspend fun calculateGreeks(contract: OptionContract, spotPrice: Double): OptionGreeks {
        val isCall = contract.type == OptionType.CALL
        val deltaVal = if (isCall) 0.54 else -0.46
        return OptionGreeks(
            delta = deltaVal,
            gamma = 0.082,
            theta = -0.045,
            vega = 0.185,
            rho = 0.024
        )
    }
}

@Singleton
class OptionStrategyRepositoryImpl @Inject constructor() : OptionStrategyRepository {
    override fun evaluateStrategyRisk(strategy: OptionStrategyType, strikePrice: Double): OptionStrategyRisk {
        return OptionStrategyRisk(
            strategyType = strategy,
            breakEvenPoints = listOf(strikePrice - 5.0, strikePrice + 5.0)
        )
    }

    override fun getAvailableStrategies(): List<OptionStrategyType> = OptionStrategyType.entries.toList()
}
