package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Genel Küresel Piyasalar Deposu Sözleşmesi (GlobalMarketRepository)
 */
interface GlobalMarketRepository {
    fun getMarketTickers(region: MarketRegion): Flow<List<MarketTickerItem>>
    fun getTopGainers(): Flow<List<MarketTickerItem>>
}

/**
 * 2. Borsa Durumları Deposu Sözleşmesi (ExchangeRepository)
 */
interface ExchangeRepository {
    fun getExchangeStatus(region: MarketRegion): Flow<ExchangeStatusInfo>
}

/**
 * 3. Sektörler Deposu Sözleşmesi (SectorRepository)
 */
interface SectorRepository {
    fun getSectorPerformances(): Flow<List<SectorPerformanceItem>>
}

/**
 * 4. Küresel Endeksler Deposu Sözleşmesi (GlobalIndexRepository)
 */
interface GlobalIndexRepository {
    fun getWorldHeatMap(): Flow<WorldHeatMapData>
}
