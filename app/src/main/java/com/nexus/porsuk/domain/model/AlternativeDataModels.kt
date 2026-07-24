package com.nexus.porsuk.domain.model

/**
 * Alternatif Veri Sağlayıcısı Türü (AlternativeDataProviderType)
 */
enum class AlternativeDataProviderType(val displayName: String, val iconEmoji: String) {
    SATELLITE_IMAGERY("Uydu Görüntüleme Intelligence", "🛰️"),
    SHIPPING_TRACKING("Denizcilik AIS Gemi Takibi", "🚢"),
    AVIATION_INTELLIGENCE("Havacılık & Uçuş Analitiği", "✈️"),
    RETAIL_FOOT_TRAFFIC("Mağaza & Perakende Trafiği", "🏬"),
    DIGITAL_WEB_APP("Dijital Trafik & Uygulama İndirmeleri", "📱"),
    ENERGY_CONSUMPTION("Endüstriyel Enerji & Tüketim", "⚡");
}

/**
 * Alternatif Makro Endeks (AlternativeIndicatorItem)
 */
data class AlternativeIndicatorItem(
    val name: String = "Tüketici Mobilite Endeksi",
    val category: String = "Mobilite & Trafik",
    val currentValue: Double = 114.2,
    val changePct: Double = 3.5,
    val indexStatus: String = "Yüksek Aktivite"
)

/**
 * Uydu Görüntüleme Aktivitesi (SatelliteActivityItem)
 */
data class SatelliteActivityItem(
    val locationName: String = "Ambarlı Limanı & Otopark Kompleksi",
    val occupancyRatePct: Double = 84.5,
    val factoryActivityScore: Int = 92,
    val statusText: String = "Fabrika ve Depo Üretim Aktivitesi Yüksek"
)

/**
 * Denizcilik & Gemi Takip Trafiği (VesselShippingItem)
 */
data class VesselShippingItem(
    val portName: String = "Ambarlı / Kocaeli Limanı",
    val containerVolumeTons: Long = 124000L,
    val portCongestionLevel: String = "MODERATE (%12 Bekleme)",
    val activeVesselsCount: Int = 18
)

/**
 * Havacılık Uçuş Trafiği (AviationTrafficItem)
 */
data class AviationTrafficItem(
    val airportCode: String = "IST / SAW (İstanbul)",
    val commercialFlightsCount: Int = 1420,
    val cargoCapacityTons: Long = 4200L,
    val privateJetActivityLevel: String = "Yüksek (High Traffic)"
)

/**
 * Endüstriyel Enerji Tüketimi (EnergyConsumptionItem)
 */
data class EnergyConsumptionItem(
    val regionName: String = "Marmara Sanayi Bölgesi",
    val electricityConsumptionGWh: Double = 42.8,
    val oilStorageOccupancyPct: Double = 78.5,
    val naturalGasDemandStatus: String = "Stabil"
)

/**
 * Geleceğe Hazır Alternatif Veri Stub Modeli (AlternativeDataFutureStubs)
 */
data class AlternativeDataFutureStubs(
    val isComputerVisionAnalysisReady: Boolean = true,
    val isSatelliteImageAiActive: Boolean = true,
    val isGeospatialAiSupported: Boolean = true,
    val isRealTimeAlternativeSignalsReady: Boolean = true,
    val isGreenwashingDetectionSupported: Boolean = false
)
