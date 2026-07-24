package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.BrokerAccountDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BrokerAccountDaoModule {

    @Provides
    fun provideBrokerAccountDao(db: PorsukDatabase): BrokerAccountDao {
        return db.brokerAccountDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BrokerHubRepositoriesModule {

    @Provides
    @Singleton
    fun provideBrokerRepository(impl: BrokerRepositoryImpl): BrokerRepository = impl

    @Provides
    @Singleton
    fun provideConnectionRepository(impl: ConnectionRepositoryImpl): ConnectionRepository = impl

    @Provides
    @Singleton
    fun providePortfolioSyncRepository(impl: PortfolioSyncRepositoryImpl): PortfolioSyncRepository = impl

    @Provides
    @Singleton
    fun provideTradeHistoryRepository(impl: TradeHistoryRepositoryImpl): TradeHistoryRepository = impl

    @Provides
    @Singleton
    fun provideOrderRepository(impl: OrderRepositoryImpl): OrderRepository = impl
}
