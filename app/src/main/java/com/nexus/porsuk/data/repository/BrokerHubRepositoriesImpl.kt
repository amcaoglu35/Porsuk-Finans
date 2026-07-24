package com.nexus.porsuk.data.repository

import com.nexus.porsuk.data.local.dao.BrokerAccountDao
import com.nexus.porsuk.data.local.entity.BrokerAccountEntity
import com.nexus.porsuk.data.provider.InteractiveBrokersProvider
import com.nexus.porsuk.data.provider.MidasBrokerProvider
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrokerRepositoryImpl @Inject constructor(
    private val midasProvider: MidasBrokerProvider,
    private val ibkrProvider: InteractiveBrokersProvider,
    private val dao: BrokerAccountDao
) : BrokerRepository {

    override fun getConnectedAccounts(): Flow<List<BrokerAccount>> {
        return dao.getAllSavedAccounts().map { list ->
            if (list.isEmpty()) {
                listOf(midasProvider.fetchAccountInfo(), ibkrProvider.fetchAccountInfo())
            } else {
                list.map { entity ->
                    BrokerAccount(
                        accountId = entity.accountId,
                        provider = BrokerProviderType.valueOf(entity.providerName),
                        accountName = entity.accountName,
                        status = BrokerConnectionStatus.valueOf(entity.statusName),
                        portfolioValueUsd = entity.portfolioValueUsd
                    )
                }
            }
        }
    }

    override suspend fun saveAccount(account: BrokerAccount) {
        val entity = BrokerAccountEntity(
            accountId = account.accountId,
            providerName = account.provider.name,
            accountName = account.accountName,
            statusName = account.status.name,
            portfolioValueUsd = account.portfolioValueUsd
        )
        dao.insertAccount(entity)
    }
}

@Singleton
class ConnectionRepositoryImpl @Inject constructor() : ConnectionRepository {
    override fun checkConnectionHealth(provider: BrokerProviderType): Flow<BrokerConnectionStatus> = flow {
        emit(BrokerConnectionStatus.CONNECTED)
    }
}

@Singleton
class PortfolioSyncRepositoryImpl @Inject constructor(
    private val midasProvider: MidasBrokerProvider,
    private val ibkrProvider: InteractiveBrokersProvider
) : PortfolioSyncRepository {
    override fun syncHoldings(provider: BrokerProviderType): Flow<List<BrokerHoldingItem>> = flow {
        val holdings = when (provider) {
            BrokerProviderType.MIDAS -> midasProvider.fetchHoldings()
            BrokerProviderType.INTERACTIVE_BROKERS -> ibkrProvider.fetchHoldings()
            else -> midasProvider.fetchHoldings()
        }
        emit(holdings)
    }
}

@Singleton
class TradeHistoryRepositoryImpl @Inject constructor() : TradeHistoryRepository {
    override fun getTradeHistory(provider: BrokerProviderType): Flow<List<String>> = flow {
        emit(listOf("Alış: 100 Lot THYAO.IS", "Satış: 50 Lot NVDA"))
    }
}

@Singleton
class OrderRepositoryImpl @Inject constructor() : OrderRepository {
    override fun prepareOrder(symbol: String, orderType: BrokerOrderType, quantity: Double): Flow<String> = flow {
        emit("Prepared ${orderType.displayName} for $quantity shares of $symbol.")
    }
}
