package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ESGRepositoryImpl @Inject constructor() : ESGRepository {
    private val esgState = MutableStateFlow(EsgScoreData())

    override fun getEsgScoreData(symbol: String): Flow<EsgScoreData> = esgState.asStateFlow()
    override fun getSupportedProviders(): List<EsgProviderType> = EsgProviderType.entries.toList()
}

@Singleton
class SustainabilityRepositoryImpl @Inject constructor() : SustainabilityRepository {
    private val alertsState = MutableStateFlow(
        listOf(
            EsgControversyAlert(title = "Karbon Emisyon %15 Azaltıldı", severity = "Düşük (Pozitif Etki)", category = "Çevre & İklim"),
            EsgControversyAlert(title = "Bağımsız YSK & Denetim Raporu Yayınlandı", severity = "Düşük (Pozitif Etki)", category = "Kurumsal Yönetişim")
        )
    )

    override fun getControversyAlerts(symbol: String): Flow<List<EsgControversyAlert>> = alertsState.asStateFlow()
}

@Singleton
class ClimateRepositoryImpl @Inject constructor() : ClimateRepository {
    private val climateState = MutableStateFlow(EnvironmentalPillar())

    override fun getEnvironmentalPillar(symbol: String): Flow<EnvironmentalPillar> = climateState.asStateFlow()
}

@Singleton
class GovernanceRepositoryImpl @Inject constructor() : GovernanceRepository {
    private val govState = MutableStateFlow(GovernancePillar())

    override fun getGovernancePillar(symbol: String): Flow<GovernancePillar> = govState.asStateFlow()
}

@Singleton
class SocialRepositoryImpl @Inject constructor() : SocialRepository {
    private val socialState = MutableStateFlow(SocialPillar())

    override fun getSocialPillar(symbol: String): Flow<SocialPillar> = socialState.asStateFlow()
}
