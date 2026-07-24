package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Aracı Kurum Deposu Sözleşmesi (BrokerRepository)
 */
interface BrokerRepository {
    fun getConnectedAccounts(): Flow<List<BrokerAccount>>
    suspend fun saveAccount(account: BrokerAccount)
}

/**
 * 2. Bağlantı Deposu Sözleşmesi (ConnectionRepository)
 */
interface ConnectionRepository {
    fun checkConnectionHealth(provider: BrokerProviderType): Flow<BrokerConnectionStatus>
}

/**
 * 3. Portföy Senkronizasyon Deposu Sözleşmesi (PortfolioSyncRepository)
 */
interface PortfolioSyncRepository {
    fun syncHoldings(provider: BrokerProviderType): Flow<List<BrokerHoldingItem>>
}

/**
 * 4. İşlem Geçmişi Deposu Sözleşmesi (TradeHistoryRepository)
 */
interface TradeHistoryRepository {
    fun getTradeHistory(provider: BrokerProviderType): Flow<List<String>>
}

/**
 * 5. Emir Altyapısı Deposu Sözleşmesi (OrderRepository)
 */
interface OrderRepository {
    fun prepareOrder(symbol: String, orderType: BrokerOrderType, quantity: Double): Flow<String>
}
