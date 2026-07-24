package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MacroRepositoryImpl @Inject constructor() : MacroRepository {
    private val outlookState = MutableStateFlow(MacroAiOutlook())

    override fun getMacroAiOutlook(): Flow<MacroAiOutlook> = outlookState.asStateFlow()

    override fun getSupportedProviders(): List<MacroProviderType> = MacroProviderType.entries.toList()
}

@Singleton
class MacroIndicatorRepositoryImpl @Inject constructor() : MacroIndicatorRepository {

    private val defaultIndicators = listOf(
        EconomicIndicator(
            indicatorId = "ind_cpi_tr",
            name = "Türkiye Tüketici Fiyat Endeksi (CPI Yıllık)",
            countryCode = "TR",
            category = MacroIndicatorCategory.INFLATION,
            provider = MacroProviderType.TCMB_TURKEY,
            currentValue = 71.60,
            previousValue = 75.45,
            forecastValue = 70.20
        ),
        EconomicIndicator(
            indicatorId = "ind_cpi_us",
            name = "ABD Tüketici Fiyat Endeksi (CPI Yıllık)",
            countryCode = "US",
            category = MacroIndicatorCategory.INFLATION,
            provider = MacroProviderType.FRED_US,
            currentValue = 3.00,
            previousValue = 3.30,
            forecastValue = 3.10
        ),
        EconomicIndicator(
            indicatorId = "ind_pmi_us",
            name = "ABD İmalat PMI Endeksi (ISM Manufacturing)",
            countryCode = "US",
            category = MacroIndicatorCategory.PMI,
            provider = MacroProviderType.FRED_US,
            currentValue = 48.5,
            previousValue = 48.7,
            forecastValue = 49.0
        ),
        EconomicIndicator(
            indicatorId = "ind_gdp_eu",
            name = "Euro Bölgesi GSYH Büyümesi (GDP Q/Q)",
            countryCode = "EU",
            category = MacroIndicatorCategory.GROWTH,
            provider = MacroProviderType.ECB_EUROPE,
            currentValue = 0.3,
            previousValue = 0.1,
            forecastValue = 0.2
        )
    )

    private val indicatorsState = MutableStateFlow(defaultIndicators)

    override fun getEconomicIndicators(): Flow<List<EconomicIndicator>> = indicatorsState.asStateFlow()

    override fun getIndicatorsByCategory(category: MacroIndicatorCategory): Flow<List<EconomicIndicator>> {
        return indicatorsState.map { list -> list.filter { it.category == category } }
    }
}

@Singleton
class CentralBankRepositoryImpl @Inject constructor() : CentralBankRepository {

    private val defaultPolicies = listOf(
        CentralBankPolicy(
            bankType = CentralBankType.TCMB,
            policyRatePct = 50.0,
            statementSummary = "Parasal sıkılaşma kararlılıkla sürdürülecektir."
        ),
        CentralBankPolicy(
            bankType = CentralBankType.FED,
            policyRatePct = 5.25,
            statementSummary = "Faiz indirimi kararı öncesi enflasyonun %2 hedefine ilerlediğinden emin olunacaktır."
        ),
        CentralBankPolicy(
            bankType = CentralBankType.ECB,
            policyRatePct = 4.25,
            statementSummary = "Faiz patikası gelen ekonomik verilere göre toplantı bazlı belirlenecektir."
        )
    )

    private val policiesState = MutableStateFlow(defaultPolicies)

    override fun getCentralBankPolicies(): Flow<List<CentralBankPolicy>> = policiesState.asStateFlow()

    override suspend fun getPolicyDetails(bank: CentralBankType): CentralBankPolicy? {
        return policiesState.value.find { it.bankType == bank }
    }
}

@Singleton
class BondRepositoryImpl @Inject constructor() : BondRepository {

    private val defaultBonds = listOf(
        BondYieldItem(bondSymbol = "US10Y", countryName = "ABD 10Y Tahvil", maturityYears = 10, yieldPct = 4.28, changePct = -0.42),
        BondYieldItem(bondSymbol = "US02Y", countryName = "ABD 2Y Tahvil", maturityYears = 2, yieldPct = 4.45, changePct = -0.68),
        BondYieldItem(bondSymbol = "TR10Y", countryName = "Türkiye 10Y Tahvil", maturityYears = 10, yieldPct = 28.50, changePct = -1.15),
        BondYieldItem(bondSymbol = "DE10Y", countryName = "Almanya 10Y Bund", maturityYears = 10, yieldPct = 2.45, changePct = +0.12)
    )

    private val bondsState = MutableStateFlow(defaultBonds)

    override fun getGovernmentBondYields(): Flow<List<BondYieldItem>> = bondsState.asStateFlow()
}

@Singleton
class FXRepositoryImpl @Inject constructor() : FXRepository {
    private val defaultFx = mapOf(
        "USD/TRY" to 32.85,
        "EUR/TRY" to 35.60,
        "EUR/USD" to 1.084,
        "GBP/USD" to 1.292,
        "DXY" to 104.35
    )

    override fun getMajorFxCrosses(): Flow<Map<String, Double>> = MutableStateFlow(defaultFx).asStateFlow()
}

@Singleton
class MacroCommodityRepositoryImpl @Inject constructor() : MacroCommodityRepository {

    private val defaultCommodities = listOf(
        CommodityItem(commoditySymbol = "XAU-USD", name = "Ons Altın", category = "Değerli Metal", priceUSD = 2415.50, changePct = 1.15),
        CommodityItem(commoditySymbol = "BRENT", name = "Brent Petrol", category = "Enerji", priceUSD = 84.20, changePct = -0.85),
        CommodityItem(commoditySymbol = "WTI", name = "WTI Ham Petrol", category = "Enerji", priceUSD = 80.40, changePct = -0.92),
        CommodityItem(commoditySymbol = "COPPER", name = "Bakır (Copper)", category = "Sanayi Metali", priceUSD = 4.35, changePct = 0.45)
    )

    private val commoditiesState = MutableStateFlow(defaultCommodities)

    override fun getCommodityPrices(): Flow<List<CommodityItem>> = commoditiesState.asStateFlow()
}
