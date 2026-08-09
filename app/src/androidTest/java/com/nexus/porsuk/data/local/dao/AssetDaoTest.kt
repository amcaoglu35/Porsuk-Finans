package com.nexus.porsuk.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.entity.WatchlistItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AssetDaoTest {

    private lateinit var database: PorsukDatabase
    private lateinit var dao: AssetDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PorsukDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.assetDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetWatchlistItem() = runTest {
        val item = WatchlistItem(symbol = "AAPL", addedAt = 123456L)
        dao.insertWatchlistItem(item)

        val watchlist = dao.getWatchlist().first()
        assertThat(watchlist).contains(item)
    }

    @Test
    fun deleteWatchlistItem() = runTest {
        val item = WatchlistItem(symbol = "TSLA")
        dao.insertWatchlistItem(item)
        dao.deleteWatchlistItem(item)

        val watchlist = dao.getWatchlist().first()
        assertThat(watchlist).doesNotContain(item)
    }

    @Test
    fun getWatchlistDirectReturnsCorrectList() = runTest {
        val item1 = WatchlistItem(symbol = "MSFT")
        val item2 = WatchlistItem(symbol = "GOOG")
        dao.insertWatchlistItem(item1)
        dao.insertWatchlistItem(item2)

        val watchlist = dao.getWatchlistDirect()
        assertThat(watchlist).hasSize(2)
        assertThat(watchlist.map { it.symbol }).containsExactly("MSFT", "GOOG")
    }
}
