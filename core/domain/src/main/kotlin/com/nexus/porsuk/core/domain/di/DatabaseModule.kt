package com.nexus.porsuk.core.domain.di

import android.content.Context
import androidx.room.Room
import com.nexus.porsuk.core.domain.db.AppDatabase
import com.nexus.porsuk.core.domain.db.KapNoticeDao
import com.nexus.porsuk.core.domain.db.PortfolioDao
import com.nexus.porsuk.core.domain.db.StockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "porsuk_finans.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideStockDao(database: AppDatabase): StockDao = database.stockDao()

    @Provides
    fun providePortfolioDao(database: AppDatabase): PortfolioDao = database.portfolioDao()

    @Provides
    fun provideKapNoticeDao(database: AppDatabase): KapNoticeDao = database.kapNoticeDao()
}
