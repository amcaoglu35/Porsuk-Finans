package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.WatchlistProDao
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WatchlistProDaoModule {

    @Provides
    fun provideWatchlistProDao(db: PorsukDatabase): WatchlistProDao {
        return db.watchlistProDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object WatchlistProRepositoriesModule {

    @Provides
    @Singleton
    fun provideWatchlistProRepository(impl: WatchlistProRepositoryImpl): WatchlistProRepository = impl

    @Provides
    @Singleton
    fun provideWatchlistItemProRepository(impl: WatchlistItemProRepositoryImpl): WatchlistItemProRepository = impl

    @Provides
    @Singleton
    fun provideFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository = impl
}
