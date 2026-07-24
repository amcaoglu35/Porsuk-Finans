package com.nexus.porsuk.data.engine

import com.nexus.porsuk.data.filter.*
import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. 10 Akıllı Paket Filtre Motoru (SmartFilterPresetEngine)
 */
@Singleton
class SmartFilterPresetEngine @Inject constructor() {

    fun getSampleDatabase(): List<ScreenerResultItem> {
        return listOf(
            ScreenerResultItem("THYAO.IS", "Türk Hava Yolları", null, ScanMarketType.BIST, 284.50, 4.25, "1.45B TL", "12.8B $", 88, RiskLevel.LOW, 4.85, 1.15, 32.4, 0.0, 3.85, 8),
            ScreenerResultItem("AKBNK.IS", "Akbank", null, ScanMarketType.BIST, 58.20, 3.10, "980M TL", "4.2B $", 85, RiskLevel.LOW, 3.45, 0.95, 28.5, 5.2, 3.42, 7),
            ScreenerResultItem("FROTO.IS", "Ford Otosan", null, ScanMarketType.BIST, 980.00, 2.40, "450M TL", "8.5B $", 90, RiskLevel.LOW, 9.20, 3.80, 45.0, 6.8, 4.10, 9),
            ScreenerResultItem("NVDA", "NVIDIA Corp.", null, ScanMarketType.NASDAQ, 124.50, 5.80, "$12.4B", "$3.1T", 94, RiskLevel.MODERATE, 42.0, 18.5, 68.0, 0.05, 5.80, 9),
            ScreenerResultItem("AAPL", "Apple Inc.", null, ScanMarketType.NASDAQ, 224.20, 1.40, "$8.2B", "$3.4T", 92, RiskLevel.LOW, 31.0, 45.0, 140.0, 0.55, 6.20, 8)
        )
    }
}

/**
 * 2. Specification & Çoklu Sıralama Motoru (ScreenerUltimateEngine)
 */
@Singleton
class ScreenerUltimateEngine @Inject constructor(
    private val presetEngine: SmartFilterPresetEngine
) {

    fun executeUltimateScan(criteria: ScreenerUltimateCriteria): List<ScreenerResultItem> {
        val allItems = presetEngine.getSampleDatabase()
        val specs = mutableListOf<FilterSpecification<ScreenerResultItem>>()

        if (criteria.maxPeRatio != null) {
            specs.add(PeFilterSpecification(criteria.maxPeRatio))
        }

        if (criteria.minAltmanZScore != null) {
            specs.add(AltmanZFilterSpecification(criteria.minAltmanZScore))
        }

        if (criteria.minMasterScore != null) {
            specs.add(MasterScoreFilterSpecification(criteria.minMasterScore))
        }

        val combinedSpec = AndSpecification(specs)
        return allItems.filter { combinedSpec.isSatisfiedBy(it) }
    }
}
