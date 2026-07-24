package com.nexus.porsuk.data.provider

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Broker Integration Hub — Soyut Provider Arayüzü (BrokerIntegrationProvider)
 *
 * Provider Pattern + Adapter Pattern: Midas, IBKR, Alpaca ve Binance gibi tüm aracı kurum sağlayıcıları bu arayüzü uygular.
 */
interface BrokerIntegrationProvider {
    fun getProviderType(): BrokerProviderType
    fun fetchAccountInfo(): BrokerAccount
    fun fetchHoldings(): List<BrokerHoldingItem>
}

/**
 * Midas Menkul Değerler Sağlayıcısı (MidasBrokerProvider)
 */
@Singleton
class MidasBrokerProvider @Inject constructor() : BrokerIntegrationProvider {
    override fun getProviderType() = BrokerProviderType.MIDAS

    override fun fetchAccountInfo(): BrokerAccount {
        return BrokerAccount(
            accountId = "midas_acc_101",
            provider = BrokerProviderType.MIDAS,
            accountName = "Midas Yatırım Hesabı",
            status = BrokerConnectionStatus.CONNECTED,
            cashBalanceUsd = 12500.0,
            portfolioValueUsd = 48500.0,
            buyingPowerUsd = 25000.0
        )
    }

    override fun fetchHoldings(): List<BrokerHoldingItem> {
        return listOf(
            BrokerHoldingItem("THYAO.IS", 100.0, 270.0, 284.5, 1450.0),
            BrokerHoldingItem("AKBNK.IS", 500.0, 52.0, 58.2, 3100.0)
        )
    }
}

/**
 * Interactive Brokers Sağlayıcısı (InteractiveBrokersProvider)
 */
@Singleton
class InteractiveBrokersProvider @Inject constructor() : BrokerIntegrationProvider {
    override fun getProviderType() = BrokerProviderType.INTERACTIVE_BROKERS

    override fun fetchAccountInfo(): BrokerAccount {
        return BrokerAccount(
            accountId = "ibkr_acc_202",
            provider = BrokerProviderType.INTERACTIVE_BROKERS,
            accountName = "IBKR Global Account",
            status = BrokerConnectionStatus.CONNECTED,
            cashBalanceUsd = 24500.0,
            portfolioValueUsd = 112000.0,
            buyingPowerUsd = 49000.0
        )
    }

    override fun fetchHoldings(): List<BrokerHoldingItem> {
        return listOf(
            BrokerHoldingItem("NVDA", 50.0, 110.0, 124.5, 725.0),
            BrokerHoldingItem("AAPL", 30.0, 200.0, 224.2, 726.0)
        )
    }
}
