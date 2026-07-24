package com.nexus.porsuk.data.provider

import com.nexus.porsuk.domain.model.CandleStickItem
import com.nexus.porsuk.domain.model.ChartTimeFrame

/**
 * Porsuk Backtesting Engine — Veri Sağlayıcıdan Bağımsız Arayüz (BacktestDataProvider)
 *
 * Olay-güdümlü simülasyon motorunun veri kaynaklarından (BIST, NASDAQ, Kripto REST/Room) tamamen bağımsız çalışmasını sağlar.
 */
interface BacktestDataProvider {
    fun fetchHistoricalCandles(
        symbol: String,
        timeFrame: ChartTimeFrame,
        startDate: Long,
        endDate: Long
    ): List<CandleStickItem>
}

/**
 * Varsayılan Backtest Veri Sağlayıcı Somut Sınıfı (DefaultBacktestDataProvider)
 */
class DefaultBacktestDataProvider : BacktestDataProvider {
    override fun fetchHistoricalCandles(
        symbol: String,
        timeFrame: ChartTimeFrame,
        startDate: Long,
        endDate: Long
    ): List<CandleStickItem> {
        val now = System.currentTimeMillis()
        val dayMs = 86400000L

        return listOf(
            CandleStickItem(now - (30 * dayMs), 200.0, 205.0, 198.0, 204.0, 1000000.0),
            CandleStickItem(now - (20 * dayMs), 204.0, 218.0, 202.0, 215.0, 1200000.0),
            CandleStickItem(now - (10 * dayMs), 215.0, 230.0, 212.0, 228.0, 1500000.0),
            CandleStickItem(now - (5 * dayMs), 228.0, 250.0, 225.0, 248.0, 1800000.0),
            CandleStickItem(now, 248.0, 275.0, 245.0, 269.0, 2200000.0)
        )
    }
}
