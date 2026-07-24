package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.engine.*
import com.nexus.porsuk.data.provider.*
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalMarketRepositoryImpl @Inject constructor(
    private val bistProvider: BistMarketProvider,
    private val usProvider: UsMarketProvider,
    private val europeProvider: EuropeMarketProvider,
    private val rankingEngine: GlobalRankingEngine
) : GlobalMarketRepository {

    override fun getMarketTickers(region: MarketRegion): Flow<List<MarketTickerItem>> = flow {
        val tickers = when (region) {
            MarketRegion.TURKEY -> bistProvider.fetchTickers()
            MarketRegion.USA -> usProvider.fetchTickers()
            MarketRegion.EUROPE -> europeProvider.fetchTickers()
            else -> bistProvider.fetchTickers() + usProvider.fetchTickers()
        }
        emit(tickers)
    }

    override fun getTopGainers(): Flow<List<MarketTickerItem>> = flow {
        val all = bistProvider.fetchTickers() + usProvider.fetchTickers() + europeProvider.fetchTickers()
        emit(rankingEngine.getTopGainers(all))
    }
}

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val bistProvider: BistMarketProvider,
    private val usProvider: UsMarketProvider,
    private val europeProvider: EuropeMarketProvider
) : ExchangeRepository {

    override fun getExchangeStatus(region: MarketRegion): Flow<ExchangeStatusInfo> = flow {
        val status = when (region) {
            MarketRegion.TURKEY -> bistProvider.getExchangeStatus()
            MarketRegion.USA -> usProvider.getExchangeStatus()
            MarketRegion.EUROPE -> europeProvider.getExchangeStatus()
            else -> bistProvider.getExchangeStatus()
        }
        emit(status)
    }
}

@Singleton
class SectorRepositoryImpl @Inject constructor(
    private val heatMapEngine: WorldHeatMapEngine
) : SectorRepository {
    override fun getSectorPerformances(): Flow<List<SectorPerformanceItem>> = flow {
        emit(heatMapEngine.getSectorPerformances())
    }
}

@Singleton
class GlobalIndexRepositoryImpl @Inject constructor(
    private val heatMapEngine: WorldHeatMapEngine
) : GlobalIndexRepository {
    override fun getWorldHeatMap(): Flow<WorldHeatMapData> = flow {
        emit(heatMapEngine.getWorldHeatMap())
    }
}
