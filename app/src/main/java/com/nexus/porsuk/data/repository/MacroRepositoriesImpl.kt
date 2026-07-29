package com.nexus.porsuk.data.repository

import com.nexus.porsuk.core.common.NetworkResult
import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.MacroDataEntity
import com.nexus.porsuk.data.remote.datasource.FredRemoteDataSource
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MacroRepositoryImpl @Inject constructor() : MacroRepository {
    private val outlookState = MutableStateFlow(MacroAiOutlook())

    override fun getMacroAiOutlook(): Flow<MacroAiOutlook> = outlookState.asStateFlow()

    override fun getSupportedProviders(): List<MacroProviderType> = MacroProviderType.entries.toList()
}

@Singleton
class MacroIndicatorRepositoryImpl @Inject constructor(
    private val fredDataSource: FredRemoteDataSource,
    private val assetDao: AssetDao
) : MacroIndicatorRepository {

    private val seriesIds = mapOf(
        "FEDFUNDS" to ("Fed Politika Faizi" to MacroIndicatorCategory.INTEREST_RATE),
        "CPIAUCSL" to ("ABD Tüketici Enflasyonu (CPI)" to MacroIndicatorCategory.INFLATION),
        "PPIACO" to ("ABD Üretici Enflasyonu (PPI)" to MacroIndicatorCategory.INFLATION),
        "GDP" to ("ABD GSYH Büyümesi" to MacroIndicatorCategory.GROWTH),
        "UNRATE" to ("ABD İşsizlik Oranı" to MacroIndicatorCategory.EMPLOYMENT),
        "DGS10" to ("ABD 10 Yıllık Tahvil Faizi" to MacroIndicatorCategory.BONDS),
        "DGS02" to ("ABD 2 Yıllık Tahvil Faizi" to MacroIndicatorCategory.BONDS),
        "VIXCLS" to ("VIX Korku Endeksi" to MacroIndicatorCategory.VOLATILITY_FX),
        "DTWEXBGS" to ("Dolar Endeksi (DXY)" to MacroIndicatorCategory.VOLATILITY_FX),
        "M2SL" to ("ABD M2 Para Arzı" to MacroIndicatorCategory.GROWTH),
        "UMCSENT" to ("Tüketici Güven Endeksi" to MacroIndicatorCategory.GROWTH)
    )

    override fun getEconomicIndicators(): Flow<List<EconomicIndicator>> = flow {
        val indicators = seriesIds.map { (id, info) ->
            val history = assetDao.getMacroData(id).first()
            val latest = history.lastOrNull()
            val previous = if (history.size > 1) history[history.size - 2] else null
            
            EconomicIndicator(
                indicatorId = id,
                name = info.first,
                countryCode = "US",
                category = info.second,
                provider = MacroProviderType.FRED_US,
                currentValue = latest?.value ?: 0.0,
                previousValue = previous?.value ?: 0.0,
                unit = if (id.contains("RATE") || id.contains("DGS") || id.contains("FUNDS")) "%" else ""
            )
        }
        emit(indicators)
    }

    override fun getIndicatorsByCategory(category: MacroIndicatorCategory): Flow<List<EconomicIndicator>> {
        return getEconomicIndicators().map { list -> list.filter { it.category == category } }
    }

    override suspend fun refreshIndicators(): Result<Unit> {
        seriesIds.keys.forEach { seriesId ->
            val result = fredDataSource.getObservations(seriesId)
            if (result is NetworkResult.Success) {
                val entities = result.data.observations?.map { obs ->
                    MacroDataEntity(
                        seriesId = seriesId,
                        date = obs.date,
                        value = obs.value.toDoubleOrNull() ?: 0.0
                    )
                } ?: emptyList()
                assetDao.insertMacroData(entities)
            }
        }
        return Result.success(Unit)
    }

    override fun getIndicatorHistory(indicatorId: String): Flow<List<Double>> {
        return assetDao.getMacroData(indicatorId).map { list -> list.map { it.value } }
    }
}

@Singleton
class CentralBankRepositoryImpl @Inject constructor() : CentralBankRepository {
    override fun getCentralBankPolicies(): Flow<List<CentralBankPolicy>> = flowOf(emptyList())
    override suspend fun getPolicyDetails(bank: CentralBankType): CentralBankPolicy? = null
}

@Singleton
class BondRepositoryImpl @Inject constructor() : BondRepository {
    override fun getGovernmentBondYields(): Flow<List<BondYieldItem>> = flowOf(emptyList())
}

@Singleton
class FXRepositoryImpl @Inject constructor() : FXRepository {
    override fun getMajorFxCrosses(): Flow<Map<String, Double>> = flowOf(emptyMap())
}

@Singleton
class MacroCommodityRepositoryImpl @Inject constructor() : MacroCommodityRepository {
    override fun getCommodityPrices(): Flow<List<CommodityItem>> = flowOf(emptyList())
}
