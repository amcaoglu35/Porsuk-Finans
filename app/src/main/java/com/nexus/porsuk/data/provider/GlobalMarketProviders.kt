package com.nexus.porsuk.data.provider

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Global Markets Center — Soyut Bölge Borsa Veri Sağlayıcı Arayüzü (GlobalRegionMarketDataProvider)
 *
 * İleride yeni borsalar, REST API'lar veya WebSocket servisleri bu arayüz uygulanarak eklenir.
 */
interface GlobalRegionMarketDataProvider {
    fun getRegion(): MarketRegion
    fun fetchTickers(): List<MarketTickerItem>
    fun getExchangeStatus(): ExchangeStatusInfo
}

/**
 * Türkiye BIST, VİOP, TEFAS Sağlayıcısı (BistMarketProvider)
 */
@Singleton
class BistMarketProvider @Inject constructor() : GlobalRegionMarketDataProvider {
    override fun getRegion() = MarketRegion.TURKEY

    override fun fetchTickers(): List<MarketTickerItem> {
        return listOf(
            MarketTickerItem("THYAO.IS", "Türk Hava Yolları", MarketRegion.TURKEY, "BIST", 284.50, 4.25, "1.45B TL", 274.0, 272.0, 288.0, 273.5, MarketState.OPEN),
            MarketTickerItem("BIST100", "BIST 100 Endeksi", MarketRegion.TURKEY, "BIST", 10850.0, 1.85, "42B TL", 10650.0, 10620.0, 10920.0, 10640.0, MarketState.OPEN),
            MarketTickerItem("USD/TRY", "Dolar / Türk Lirası", MarketRegion.TURKEY, "FOREX", 32.85, 0.15, "120M $", 32.80, 32.80, 32.90, 32.78, MarketState.OPEN)
        )
    }

    override fun getExchangeStatus(): ExchangeStatusInfo {
        return ExchangeStatusInfo("Borsa İstanbul (BIST)", "TR", MarketState.OPEN, "15:45 (GMT+3)", "10:00 - 18:00")
    }
}

/**
 * ABD NYSE, NASDAQ Sağlayıcısı (UsMarketProvider)
 */
@Singleton
class UsMarketProvider @Inject constructor() : GlobalRegionMarketDataProvider {
    override fun getRegion() = MarketRegion.USA

    override fun fetchTickers(): List<MarketTickerItem> {
        return listOf(
            MarketTickerItem("NVDA", "NVIDIA Corp.", MarketRegion.USA, "NASDAQ", 124.50, 5.80, "$12.4B", 118.0, 117.5, 126.0, 118.0, MarketState.OPEN),
            MarketTickerItem("S&P500", "S&P 500 Endeksi", MarketRegion.USA, "NYSE", 5580.0, 0.95, "$48B", 5520.0, 5520.0, 5600.0, 5515.0, MarketState.OPEN)
        )
    }

    override fun getExchangeStatus(): ExchangeStatusInfo {
        return ExchangeStatusInfo("NYSE & NASDAQ", "US", MarketState.OPEN, "09:45 (EST)", "09:30 - 16:00")
    }
}

/**
 * Avrupa XETRA & LSE Sağlayıcısı (EuropeMarketProvider)
 */
@Singleton
class EuropeMarketProvider @Inject constructor() : GlobalRegionMarketDataProvider {
    override fun getRegion() = MarketRegion.EUROPE

    override fun fetchTickers(): List<MarketTickerItem> {
        return listOf(
            MarketTickerItem("DAX", "Almanya DAX 40", MarketRegion.EUROPE, "XETRA", 18450.0, 0.85, "€4.2B", 18300.0, 18300.0, 18500.0, 18280.0, MarketState.OPEN)
        )
    }

    override fun getExchangeStatus(): ExchangeStatusInfo {
        return ExchangeStatusInfo("Frankfurt XETRA", "DE", MarketState.OPEN, "14:45 (CET)", "09:00 - 17:30")
    }
}
