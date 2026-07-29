package com.nexus.porsuk.data.local.dao

import androidx.room.*
import com.nexus.porsuk.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    // Companies
    @Query("SELECT * FROM companies")
    fun getAllCompanies(): Flow<List<Company>>

    @Query("SELECT * FROM companies")
    suspend fun getAllCompaniesDirect(): List<Company>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<Company>)

    @Update
    suspend fun updateCompany(company: Company)

    @Query("SELECT * FROM companies WHERE symbol = :symbol")
    suspend fun getCompany(symbol: String): Company?

    // Baskets
    @Query("SELECT * FROM baskets")
    fun getAllBaskets(): Flow<List<Basket>>

    @Query("SELECT * FROM baskets WHERE id = :basketId")
    fun getBasketById(basketId: Int): Flow<Basket?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasket(basket: Basket): Long

    @Update
    suspend fun updateBasket(basket: Basket)

    @Delete
    suspend fun deleteBasket(basket: Basket)

    // Basket Items
    @Query("SELECT * FROM basket_items WHERE basketId = :basketId")
    fun getItemsForBasket(basketId: Int): Flow<List<BasketItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasketItem(item: BasketItem)

    @Delete
    suspend fun deleteBasketItem(item: BasketItem)

    // Watchlist
    @Query("SELECT * FROM watchlist_items")
    fun getWatchlist(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItem)

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItem)

    // Cached Info
    @Query("SELECT * FROM cached_company_info")
    fun getAllCachedInfo(): Flow<List<CachedCompanyInfo>>

    @Query("SELECT * FROM cached_company_info WHERE symbol = :symbol")
    fun getCachedInfo(symbol: String): Flow<CachedCompanyInfo?>

    @Query("SELECT * FROM cached_company_info WHERE symbol = :symbol")
    suspend fun getCachedInfoDirect(symbol: String): CachedCompanyInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedInfo(info: CachedCompanyInfo)

    // News
    @Query("SELECT * FROM news_items WHERE symbol = :symbol ORDER BY publishedAt DESC")
    fun getNewsForStock(symbol: String): Flow<List<NewsItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsItemEntity>)

    @Query("DELETE FROM baskets")
    suspend fun clearBaskets()

    @Query("DELETE FROM basket_items")
    suspend fun clearBasketItems()

    @Query("DELETE FROM watchlist_items")
    suspend fun clearWatchlist()

    @Query("DELETE FROM price_snapshots")
    suspend fun clearPriceHistory()

    @Query("SELECT * FROM basket_items")
    fun getAllBasketItemsFlow(): Flow<List<BasketItem>>

    @Query("SELECT * FROM basket_items")
    suspend fun getAllBasketItemsDirect(): List<BasketItem>

    @Query("SELECT * FROM watchlist_items")
    suspend fun getWatchlistDirect(): List<WatchlistItem>

    @Query("SELECT * FROM baskets")
    suspend fun getAllBasketsDirect(): List<Basket>

    // Price Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceAlert(alert: PriceAlert)

    @Update
    suspend fun updatePriceAlert(alert: PriceAlert)

    @Query("SELECT * FROM price_alerts WHERE isActive = 1")
    suspend fun getActivePriceAlerts(): List<PriceAlert>

    @Query("SELECT * FROM price_alerts WHERE symbol = :symbol")
    fun getAlertsForStock(symbol: String): Flow<List<PriceAlert>>

    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllPriceAlertsFlow(): Flow<List<PriceAlert>>

    @Query("DELETE FROM price_alerts WHERE id = :alertId")
    suspend fun deletePriceAlert(alertId: Int)

    // Portfolio History
    @Query("SELECT * FROM portfolio_history ORDER BY timestamp ASC")
    fun getPortfolioHistory(): Flow<List<PortfolioHistoryEntry>>

    @Query("SELECT * FROM portfolio_history ORDER BY timestamp ASC")
    suspend fun getPortfolioHistoryDirect(): List<PortfolioHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioHistoryEntry(entry: PortfolioHistoryEntry)

    @Query("DELETE FROM portfolio_history")
    suspend fun clearPortfolioHistory()

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<PortfolioTransaction>>

    @Query("SELECT * FROM transactions WHERE basketId = :basketId ORDER BY timestamp DESC")
    fun getTransactionsForBasketFlow(basketId: Int): Flow<List<PortfolioTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PortfolioTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: PortfolioTransaction)

    @Update
    suspend fun updateTransaction(transaction: PortfolioTransaction)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    // Stock History
    @Query("SELECT * FROM stock_history WHERE symbol = :symbol ORDER BY timestamp ASC")
    fun getStockHistory(symbol: String): Flow<List<StockHistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockHistoryEntry(entry: StockHistoryEntry)

    @Query("DELETE FROM stock_history WHERE symbol = :symbol")
    suspend fun clearStockHistory(symbol: String)

    // Kazi Runs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaziRun(run: KaziRun): Long

    @Update
    suspend fun updateKaziRun(run: KaziRun)

    @Query("SELECT * FROM kazi_runs ORDER BY startedAt DESC LIMIT 1")
    fun getLatestKaziRun(): Flow<KaziRun?>

    @Query("SELECT * FROM kazi_runs WHERE id = :runId")
    suspend fun getKaziRunDirect(runId: Int): KaziRun?

    // Kazi Candidates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaziCandidates(candidates: List<KaziCandidate>)

    @Query("SELECT * FROM kazi_candidates WHERE runId = :runId")
    fun getCandidatesForRun(runId: Int): Flow<List<KaziCandidate>>

    @Query("SELECT * FROM kazi_candidates WHERE runId = :runId AND selected = 1")
    suspend fun getSelectedCandidatesDirect(runId: Int): List<KaziCandidate>

    // Kazi Baskets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaziBasket(basket: KaziBasket): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaziBasketItems(items: List<KaziBasketItem>)

    @Query("SELECT * FROM kazi_baskets WHERE runId = :runId")
    fun getKaziBasketForRun(runId: Int): Flow<KaziBasket?>

    @Query("SELECT * FROM kazi_baskets WHERE id = :basketId")
    suspend fun getKaziBasketById(basketId: Int): KaziBasket?

    @Query("SELECT * FROM kazi_basket_items WHERE basketId = :basketId")
    fun getKaziBasketItems(basketId: Int): Flow<List<KaziBasketItem>>

    // Kazi Watches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaziWatch(watch: KaziWatch)

    @Query("SELECT * FROM kazi_watches")
    fun getAllKaziWatches(): Flow<List<KaziWatch>>

    // Dividend Calendar
    @Query("SELECT * FROM dividend_calendar ORDER BY exDividendDate ASC")
    fun getAllDividends(): Flow<List<DividendCalendarEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDividends(dividends: List<DividendCalendarEntry>)

    @Query("DELETE FROM dividend_calendar")
    suspend fun clearDividends()

    // IPO Calendar
    @Query("SELECT * FROM ipo_calendar ORDER BY startDate ASC")
    fun getAllIpos(): Flow<List<IpoCalendarEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIpos(ipos: List<IpoCalendarEntry>)

    @Query("DELETE FROM ipo_calendar")
    suspend fun clearIpos()

    // Economic Events
    @Query("SELECT * FROM economic_events ORDER BY date ASC")
    fun getAllEconomicEvents(): Flow<List<EconomicEventEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEconomicEvents(events: List<EconomicEventEntry>)

    @Query("DELETE FROM economic_events")
    suspend fun clearEconomicEvents()

    // Decision Journal Entries
    @Query("SELECT * FROM decision_journal_entries ORDER BY createdAt DESC")
    fun getAllJournalEntries(): Flow<List<DecisionJournalEntry>>

    @Query("SELECT * FROM decision_journal_entries ORDER BY createdAt DESC")
    suspend fun getAllJournalEntriesDirect(): List<DecisionJournalEntry>

    @Query("SELECT * FROM decision_journal_entries WHERE symbol = :symbol ORDER BY createdAt DESC")
    fun getJournalEntriesForStock(symbol: String): Flow<List<DecisionJournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: DecisionJournalEntry): Long

    @Update
    suspend fun updateJournalEntry(entry: DecisionJournalEntry)

    @Delete
    suspend fun deleteJournalEntry(entry: DecisionJournalEntry)

    // AI Analysis Audit Entries
    @Query("SELECT * FROM ai_analysis_audit_entries ORDER BY analysisDate DESC")
    fun getAllAuditEntries(): Flow<List<AiAnalysisAuditEntry>>

    @Query("SELECT * FROM ai_analysis_audit_entries ORDER BY analysisDate DESC")
    suspend fun getAllAuditEntriesDirect(): List<AiAnalysisAuditEntry>

    @Query("SELECT * FROM ai_analysis_audit_entries WHERE symbol = :symbol ORDER BY analysisDate DESC")
    fun getAuditEntriesForStock(symbol: String): Flow<List<AiAnalysisAuditEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEntry(entry: AiAnalysisAuditEntry): Long

    @Update
    suspend fun updateAuditEntry(entry: AiAnalysisAuditEntry)

    @Delete
    suspend fun deleteAuditEntry(entry: AiAnalysisAuditEntry)

    // Porsuk Brain Memory
    @Query("SELECT * FROM porsuk_brain_memory WHERE id = 1")
    fun getBrainMemory(): Flow<PorsukBrainMemory?>

    @Query("SELECT * FROM porsuk_brain_memory WHERE id = 1")
    suspend fun getBrainMemoryDirect(): PorsukBrainMemory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBrainMemory(memory: PorsukBrainMemory)

    // Proactive AI Insights
    @Query("SELECT * FROM ai_proactive_insights ORDER BY createdAt DESC")
    fun getAllInsights(): Flow<List<AiInsightEntry>>

    @Query("SELECT * FROM ai_proactive_insights ORDER BY createdAt DESC")
    suspend fun getAllInsightsDirect(): List<AiInsightEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: AiInsightEntry): Long

    @Query("DELETE FROM ai_proactive_insights WHERE id = :id")
    suspend fun deleteInsight(id: Long)

    // Financial Statements
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeStatements(statements: List<IncomeStatementEntity>)

    @Query("SELECT * FROM income_statements WHERE symbol = :symbol ORDER BY date DESC")
    fun getIncomeStatements(symbol: String): Flow<List<IncomeStatementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheets(sheets: List<BalanceSheetEntity>)

    @Query("SELECT * FROM balance_sheets WHERE symbol = :symbol ORDER BY date DESC")
    fun getBalanceSheets(symbol: String): Flow<List<BalanceSheetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashFlows(flows: List<CashFlowEntity>)

    @Query("SELECT * FROM cash_flows WHERE symbol = :symbol ORDER BY date DESC")
    fun getCashFlows(symbol: String): Flow<List<CashFlowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyRatios(ratios: List<CompanyRatioEntity>)

    @Query("SELECT * FROM company_ratios WHERE symbol = :symbol ORDER BY date DESC")
    fun getCompanyRatios(symbol: String): Flow<List<CompanyRatioEntity>>

    // Macro Data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMacroData(data: List<MacroDataEntity>)

    @Query("SELECT * FROM macro_data WHERE seriesId = :seriesId ORDER BY date ASC")
    fun getMacroData(seriesId: String): Flow<List<MacroDataEntity>>
    
    @Query("SELECT * FROM macro_data ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestMacroUpdate(): MacroDataEntity?
}
