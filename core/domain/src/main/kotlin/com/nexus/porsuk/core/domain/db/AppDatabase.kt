package com.nexus.porsuk.core.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        StockEntity::class,
        PortfolioBasketEntity::class,
        KapNoticeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun kapNoticeDao(): KapNoticeDao
}
