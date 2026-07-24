package com.nexus.porsuk.data.engine

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 1. 11 Hazır Tarama Stratejisi Motoru (PresetScanEngine)
 */
@Singleton
class PresetScanEngine @Inject constructor() {

    fun executePresetScan(preset: ScanPresetCategory, market: ScanMarketType): List<ScanResultItem> {
        val sampleDatabase = listOf(
            ScanResultItem("THYAO.IS", "Türk Hava Yolları", null, ScanMarketType.BIST, 284.50, 4.25, "1.45B TL", 88, RiskLevel.LOW, ScanPresetCategory.TOP_GAINERS),
            ScanResultItem("AKBNK.IS", "Akbank", null, ScanMarketType.BIST, 58.20, 3.10, "980M TL", 85, RiskLevel.LOW, ScanPresetCategory.VALUE_STOCKS),
            ScanResultItem("NVDA", "NVIDIA Corporation", null, ScanMarketType.NASDAQ, 124.50, 5.80, "$12.4B", 94, RiskLevel.MODERATE, ScanPresetCategory.HIGH_MOMENTUM),
            ScanResultItem("AAPL", "Apple Inc.", null, ScanMarketType.NASDAQ, 224.20, 1.40, "$8.2B", 92, RiskLevel.LOW, ScanPresetCategory.AI_READY_LIST),
            ScanResultItem("YAY", "Yapı Kredi Portföy Yabancı Teknoloji Fonu", null, ScanMarketType.TEFAS, 14.85, 2.15, "120M TL", 84, RiskLevel.MODERATE, ScanPresetCategory.STRONG_GROWTH),
            ScanResultItem("BTC/USD", "Bitcoin", null, ScanMarketType.CRYPTO, 64200.0, 3.80, "$28.5B", 80, RiskLevel.HIGH, ScanPresetCategory.HIGH_VOLUME),
            ScanResultItem("FROTO.IS", "Ford Otosan", null, ScanMarketType.BIST, 980.00, 2.40, "450M TL", 90, RiskLevel.LOW, ScanPresetCategory.DIVIDEND_STOCKS),
            ScanResultItem("ONS", "Gram Altın", null, ScanMarketType.COMMODITY, 2540.0, 0.85, "320M TL", 86, RiskLevel.VERY_LOW, ScanPresetCategory.LOW_RISK)
        )

        return if (market == ScanMarketType.ALL) {
            sampleDatabase
        } else {
            sampleDatabase.filter { it.marketType == market }
        }
    }
}

/**
 * 2. Gelişmiş Çoklu Kriter Filtreleme Motoru (MultiFilterScanEngine)
 */
@Singleton
class MultiFilterScanEngine @Inject constructor() {

    fun applyFilters(items: List<ScanResultItem>, criteria: ScannerFilterCriteria): List<ScanResultItem> {
        var result = items

        if (criteria.marketType != ScanMarketType.ALL) {
            result = result.filter { it.marketType == criteria.marketType }
        }

        if (criteria.minPrice != null) {
            result = result.filter { it.lastPrice >= criteria.minPrice }
        }

        if (criteria.maxPrice != null) {
            result = result.filter { it.lastPrice <= criteria.maxPrice }
        }

        if (criteria.minMasterScore != null) {
            result = result.filter { it.masterScore >= criteria.minMasterScore }
        }

        if (criteria.riskLevelFilter != null) {
            result = result.filter { it.riskLevel == criteria.riskLevelFilter }
        }

        return result
    }
}
