package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Makroekonomik Platform Deposu Sözleşmesi (MacroRepository)
 */
interface MacroRepository {
    fun getMacroAiOutlook(): Flow<MacroAiOutlook>
    fun getSupportedProviders(): List<MacroProviderType>
}

/**
 * 2. Ekonomik Göstergeler Deposu Sözleşmesi (MacroIndicatorRepository)
 */
interface MacroIndicatorRepository {
    fun getEconomicIndicators(): Flow<List<EconomicIndicator>>
    fun getIndicatorsByCategory(category: MacroIndicatorCategory): Flow<List<EconomicIndicator>>
}

/**
 * 3. Merkez Bankaları Deposu Sözleşmesi (CentralBankRepository)
 */
interface CentralBankRepository {
    fun getCentralBankPolicies(): Flow<List<CentralBankPolicy>>
    suspend fun getPolicyDetails(bank: CentralBankType): CentralBankPolicy?
}

/**
 * 4. Tahvil Piyasası Deposu Sözleşmesi (BondRepository)
 */
interface BondRepository {
    fun getGovernmentBondYields(): Flow<List<BondYieldItem>>
}

/**
 * 5. Döviz Piyasası Deposu Sözleşmesi (FXRepository)
 */
interface FXRepository {
    fun getMajorFxCrosses(): Flow<Map<String, Double>>
}

/**
 * 6. Emtia Piyasası Deposu Sözleşmesi (MacroCommodityRepository)
 */
interface MacroCommodityRepository {
    fun getCommodityPrices(): Flow<List<CommodityItem>>
}
