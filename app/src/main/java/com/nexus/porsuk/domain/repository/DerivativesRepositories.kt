package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Türev Ürünler Deposu Sözleşmesi (DerivativesRepository)
 */
interface DerivativesRepository {
    fun getSupportedProviders(): List<DerivativesProviderType>
    fun getSupportedAssetTypes(): List<DerivativesAssetType>
}

/**
 * 2. Opsiyon Zinciri Deposu Sözleşmesi (OptionsRepository)
 */
interface OptionsRepository {
    fun getOptionChain(underlyingSymbol: String): Flow<List<OptionContract>>
    suspend fun getOptionDetails(optionId: String): OptionContract?
}

/**
 * 3. Vadeli İşlemler Deposu Sözleşmesi (FuturesRepository)
 */
interface FuturesRepository {
    fun getFuturesContracts(underlyingSymbol: String): Flow<List<FuturesContract>>
}

/**
 * 4. Yunanlar & Fiyatlama Deposu Sözleşmesi (GreeksRepository)
 */
interface GreeksRepository {
    suspend fun calculateGreeks(contract: OptionContract, spotPrice: Double): OptionGreeks
}

/**
 * 5. Opsiyon Stratejisi Deposu Sözleşmesi (OptionStrategyRepository)
 */
interface OptionStrategyRepository {
    fun evaluateStrategyRisk(strategy: OptionStrategyType, strikePrice: Double): OptionStrategyRisk
    fun getAvailableStrategies(): List<OptionStrategyType>
}
