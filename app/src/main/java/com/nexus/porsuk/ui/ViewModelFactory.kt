package com.nexus.porsuk.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.remote.FinnhubService
import com.nexus.porsuk.data.remote.GoogleFinanceScraper
import com.nexus.porsuk.data.remote.YahooFinanceService
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.ui.fund.BasketDetailViewModel
import com.nexus.porsuk.ui.fund.CreateBasketViewModel
import com.nexus.porsuk.ui.analysis.AnalysisViewModel
import com.nexus.porsuk.ui.settings.SettingsViewModel
import com.nexus.porsuk.ui.orakul.OrakulViewModel
import com.nexus.porsuk.feature.calendar.CalendarViewModel

import com.nexus.porsuk.data.remote.ApiKeys

class FinanceViewModelFactory(
    private val context: Context,
    private val basketId: Int = -1
) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var settingsManager: SettingsManager? = null
        
        @Volatile
        private var repository: FinanceRepository? = null

        @Volatile
        private var kaziRepository: com.nexus.porsuk.data.repository.KaziRepository? = null

        fun getSettingsManager(context: Context): SettingsManager {
            return settingsManager ?: synchronized(this) {
                val instance = SettingsManager(context.applicationContext)
                settingsManager = instance
                instance
            }
        }

        fun getRepository(context: Context): FinanceRepository {
            return repository ?: synchronized(this) {
                val database = PorsukDatabase.getDatabase(context.applicationContext)
                val sm = getSettingsManager(context)
                val eventBus = com.nexus.porsuk.core.common.PorsukEventBus() // Simple instance for factory
                val instance = FinanceRepository(
                    database.assetDao(),
                    GoogleFinanceScraper(),
                    eventBus,
                    FinnhubService(ApiKeys.FINNHUB),
                    YahooFinanceService(ApiKeys.YAHOO),
                    sm
                )
                repository = instance
                instance
            }
        }

        fun getKaziRepository(context: Context): com.nexus.porsuk.data.repository.KaziRepository {
            return kaziRepository ?: synchronized(this) {
                val database = PorsukDatabase.getDatabase(context.applicationContext)
                val instance = com.nexus.porsuk.data.repository.KaziRepository(database.assetDao())
                kaziRepository = instance
                instance
            }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sm = getSettingsManager(context)
        val repo = getRepository(context)
        
        return when {
            modelClass.isAssignableFrom(FinanceViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                FinanceViewModel(repo, sm) as T
            }
            modelClass.isAssignableFrom(CreateBasketViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                CreateBasketViewModel(repo) as T
            }
            modelClass.isAssignableFrom(BasketDetailViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                BasketDetailViewModel(basketId, repo, sm) as T
            }
            modelClass.isAssignableFrom(AnalysisViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AnalysisViewModel(repo, sm) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SettingsViewModel(sm, repo) as T
            }
            modelClass.isAssignableFrom(com.nexus.porsuk.ui.chat.ChatViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                com.nexus.porsuk.ui.chat.ChatViewModel(repo, sm) as T
            }
            modelClass.isAssignableFrom(com.nexus.porsuk.ui.ledger.TransactionLedgerViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                com.nexus.porsuk.ui.ledger.TransactionLedgerViewModel(repo) as T
            }
            modelClass.isAssignableFrom(OrakulViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                OrakulViewModel(repo, sm) as T
            }
            modelClass.isAssignableFrom(com.nexus.porsuk.ui.orakul.KaziViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                com.nexus.porsuk.ui.orakul.KaziViewModel(getKaziRepository(context), context) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                val db = PorsukDatabase.getDatabase(context.applicationContext)
                val errorHandler = com.nexus.porsuk.core.network.ErrorHandler()
                val okHttpClient = okhttp3.OkHttpClient.Builder().build()
                val retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl("https://finnhub.io/api/v1/")
                    .client(okHttpClient)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()
                val finnhubApi = retrofit.create(com.nexus.porsuk.data.remote.api.FinnhubApi::class.java)
                val ds = com.nexus.porsuk.data.remote.datasource.FinnhubRemoteDataSourceImpl(finnhubApi, errorHandler)
                val calRepo = com.nexus.porsuk.data.repository.CalendarRepositoryImpl(db.calendarDao(), ds)
                CalendarViewModel(calRepo, repo, sm) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
