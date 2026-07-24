package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. ESG Deposu Sözleşmesi (ESGRepository)
 */
interface ESGRepository {
    fun getEsgScoreData(symbol: String): Flow<EsgScoreData>
    fun getSupportedProviders(): List<EsgProviderType>
}

/**
 * 2. Sürdürülebilirlik Deposu Sözleşmesi (SustainabilityRepository)
 */
interface SustainabilityRepository {
    fun getControversyAlerts(symbol: String): Flow<List<EsgControversyAlert>>
}

/**
 * 3. İklim & Çevre Deposu Sözleşmesi (ClimateRepository)
 */
interface ClimateRepository {
    fun getEnvironmentalPillar(symbol: String): Flow<EnvironmentalPillar>
}

/**
 * 4. Yönetişim Deposu Sözleşmesi (GovernanceRepository)
 */
interface GovernanceRepository {
    fun getGovernancePillar(symbol: String): Flow<GovernancePillar>
}

/**
 * 5. Sosyal Sorumluluk Deposu Sözleşmesi (SocialRepository)
 */
interface SocialRepository {
    fun getSocialPillar(symbol: String): Flow<SocialPillar>
}
