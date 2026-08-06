package com.nexus.porsuk.ui.fund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.entity.BasketItem
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import com.nexus.porsuk.data.local.SettingsManager

data class HoldingUiModel(
    val id: Int,
    val symbol: String,
    val quantity: Double,
    val buyPrice: Double,
    val currentValue: Double,
    val changePercent: Double,
    val allocationPercent: Float
)

data class RebalanceSuggestion(
    val symbol: String,
    val currentPercent: Float,
    val targetPercent: Float,
    val diffPercent: Float,
    val isOverweight: Boolean,
    val description: String
)

data class BasketDetailUiState(
    val basketName: String = "",
    val market: String = "",
    val totalValue: Double = 0.0,
    val totalCost: Double = 0.0,
    val profitLossAmount: Double = 0.0,
    val profitLossPercent: Double = 0.0,
    val holdings: List<HoldingUiModel> = emptyList(),
    val rebalanceSuggestions: List<RebalanceSuggestion> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class BacktestResult(
    val durationText: String,
    val basketReturnPercent: Double,
    val bistReturnPercent: Double,
    val usdReturnPercent: Double,
    val description: String
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val basketId: Int = savedStateHandle.get<Int>("basketId") ?: savedStateHandle.get<String>("basketId")?.toIntOrNull() ?: -1

    private val _isBacktesting = MutableStateFlow(false)
    val isBacktesting: StateFlow<Boolean> = _isBacktesting

    private val _backtestResult = MutableStateFlow<BacktestResult?>(null)
    val backtestResult: StateFlow<BacktestResult?> = _backtestResult

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing

    private val _optimizationResult = MutableStateFlow<String?>(null)
    val optimizationResult: StateFlow<String?> = _optimizationResult

    fun clearOptimizationResult() {
        _optimizationResult.value = null
    }

    fun clearBacktestResult() {
        _backtestResult.value = null
    }

    fun optimizeBasket() {
        viewModelScope.launch {
            _isOptimizing.value = true
            val apiKey = settingsManager.getGeminiApiKey()
            if (apiKey.isNullOrBlank()) {
                _optimizationResult.value = "Hata: Yapay zeka optimizasyon önerisi alabilmek için lütfen önce Ayarlar sayfasından geçerli bir Gemini API anahtarı kaydedin."
                _isOptimizing.value = false
                return@launch
            }

            try {
                val companies = repository.allCompanies.first()
                val basket = repository.getBasketById(basketId).first() ?: return@launch
                val items = repository.getBasketItems(basketId).first()
                val companyMap = companies.associateBy { it.symbol }
                val cachedInfoList = repository.getAllCachedInfo().first()
                val cachedInfoMap = cachedInfoList.associateBy { it.symbol }
                
                // Fetch news for each item
                val newsMap = mutableMapOf<String, List<com.nexus.porsuk.data.local.entity.NewsItemEntity>>()
                items.forEach { item ->
                    try {
                        val newsList = repository.getNews(item.symbol).first()
                        newsMap[item.symbol] = newsList.take(3)
                    } catch (_: Exception) {}
                }

                // Compute portfolio metrics
                val currencySymbol = com.nexus.porsuk.ui.common.CurrencyFormatter.getCurrencySymbol(basket.market)
                val totalValue = items.sumOf { item ->
                    val company = companyMap[item.symbol]
                    val currentPrice = company?.currentPrice ?: item.buyPrice
                    item.quantity * currentPrice
                }

                val sectorWeights = mutableMapOf<String, Double>()
                var hhi = 0.0 // Herfindahl-Hirschman Index
                items.forEach { item ->
                    val company = companyMap[item.symbol]
                    val currentPrice = company?.currentPrice ?: item.buyPrice
                    val weight = if (totalValue > 0) (item.quantity * currentPrice) / totalValue else 0.0
                    hhi += (weight * 100) * (weight * 100)
                    val sector = company?.sector ?: "Diğer"
                    sectorWeights[sector] = (sectorWeights[sector] ?: 0.0) + weight
                }

                val portfolioText = StringBuilder()
                portfolioText.append("## SEPET ADI: ${basket.name} (${basket.market})\n")
                portfolioText.append("### Genel Portföy Metrikleri:\n")
                portfolioText.append("- Toplam Sepet Değeri: $currencySymbol${String.format(java.util.Locale.US, "%.2f", totalValue)}\n")
                portfolioText.append("- Sepet Yoğunlaşma Endeksi (HHI): ${String.format(java.util.Locale.US, "%.1f", hhi)} ")
                portfolioText.append(when {
                    hhi < 1500 -> "(Yüksek Çeşitlilik)\n"
                    hhi < 2500 -> "(Orta Yoğunlaşma)\n"
                    else -> "(Yüksek Yoğunlaşma - Riskli)\n"
                })
                portfolioText.append("- Sektörel Ağırlıklar:\n")
                sectorWeights.forEach { (sector, weight) ->
                    portfolioText.append("  • $sector: %${String.format(java.util.Locale.US, "%.1f", weight * 100)}\n")
                }
                
                portfolioText.append("\n### Detaylı Varlık Analiz Verileri:\n")
                items.forEach { item ->
                    val company = companyMap[item.symbol]
                    val info = cachedInfoMap[item.symbol]
                    val currentPrice = company?.currentPrice ?: item.buyPrice
                    val currentValue = item.quantity * currentPrice
                    val weightPercent = if (totalValue > 0) (currentValue / totalValue) * 100 else 0.0
                    val changePercent = company?.changePercent ?: 0.0
                    
                    portfolioText.append("- **Hisse: ${item.symbol}** (${company?.name ?: "Bilinmeyen"})\n")
                    portfolioText.append("  • Sektör: ${company?.sector ?: "Bilinmeyen"}\n")
                    portfolioText.append("  • Sepet Ağırlığı: %${String.format(java.util.Locale.US, "%.1f", weightPercent)}\n")
                    portfolioText.append("  • Pozisyon Büyüklüğü: ${item.quantity} adet (Maliyet: $currencySymbol${item.buyPrice}, Güncel: $currencySymbol$currentPrice)\n")
                    portfolioText.append("  • Güncel Değer: $currencySymbol${String.format(java.util.Locale.US, "%.2f", currentValue)} (Günlük Değişim: %${String.format(java.util.Locale.US, "%+.2f", changePercent)})\n")
                    
                    if (info != null) {
                        portfolioText.append("  • Finansal Rasyolar: F/K (P/E) Oranı: ${info.peRatio ?: "Bilinmiyor"}, Temettü Verimi: %${info.dividendYield?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "Bilinmiyor"}, Piyasa Değeri: ${info.marketCap ?: "Bilinmiyor"}\n")
                        portfolioText.append("  • 52 Haftalık Aralık: $currencySymbol${info.week52Low ?: "Bilinmiyor"} - $currencySymbol${info.week52High ?: "Bilinmiyor"}\n")
                    }
                    
                    val stockNews = newsMap[item.symbol] ?: emptyList()
                    if (stockNews.isNotEmpty()) {
                        portfolioText.append("  • Son Haber Akışı ve Duyarlılık:\n")
                        stockNews.forEach { newsItem ->
                            portfolioText.append("    * \"${newsItem.title}\" (Kaynak: ${newsItem.source}, Duyarlılık: ${newsItem.sentiment ?: "NEUTRAL"})\n")
                        }
                    }
                    portfolioText.append("\n")
                }

                val prompt = """
                    Sen finans dünyasının efsane isimlerinin (Warren Buffett, Benjamin Graham, Peter Lynch) yatırım felsefelerine tamamen hakim, son derece profesyonel, esprili ve veri odaklı bir "Borsa Profesörü" finansal analistisin. 
                    
                    Aşağıda detaylı verileri (sektörel dağılım, yoğunlaşma endeksi, finansal rasyolar, güncel fiyatlar ve en son haber akışları/duyarlılık analizleri) paylaşılan portföyü analiz et. 
                    
                    Görevin:
                    1. Portföyün risk durumunu, sektörel çeşitlendirmesini ve hisselerin genel değerleme durumlarını (pahalı/ucuz) Warren Buffett ve Benjamin Graham prensipleriyle analiz et.
                    2. Portföyü optimize etmek (daha dengeli, yüksek potansiyelli ve risklere karşı korunaklı hale getirmek) için net, eyleme dökülebilir önerilerde bulun.
                    3. Analizini zengin bir Markdown formatında sun. Başlıklar, emojiler, kalın metinler ve bir **Öneri Dağılım Tablosu** kullan.
                    
                    Raporunun aşağıdaki bölümleri içermesini sağla:
                    
                    ### 📊 1. PORTFÖYÜN GENEL DURUMU & RİSK ANALİZİ
                    - Portföyün sektörel dağılımı ve HHI değeri hakkında yorum yap. HHI değerini yorumlarken çeşitlendirmenin yeterli olup olmadığını bilimsel olarak değerlendir.
                    - Portföydeki en güçlü ve en riskli (örneğin aşırı pahalı F/K'ya sahip veya haber akışı negatif olan) pozisyonları belirt.
                    
                    ### 🛠️ 2. OPTİMİZASYON & DENGELEME ÖNERİLERİ
                    - Her hisse için ağırlık artırma, azaltma, sabit tutma veya başka bir sektöre kaydırma önerilerini açıkla.
                    - Önerilen yeni ağırlık dağılımlarını gösteren şık bir **Markdown Tablosu** hazırla. Tablo şu sütunları içermelidir:
                      | Hisse Senedi | Sektör | Mevcut Ağırlık | Önerilen Ağırlık | Önerilen Aksiyon |
                    
                    ### 💡 3. AKADEMİK DEĞERLENDİRME (BUFFETT & GRAHAM PERSPEKTİFİ)
                    - Graham'in "Güvenlik Marjı" (Margin of Safety) prensibi çerçevesinde hisselerin F/K ve 52 haftalık fiyat aralıklarına göre analizi.
                    - Buffett'ın "Moat" (Hendek) ve Lynch'in büyüme kategorilerine atıfta bulunarak hisselerin orta-uzun vadeli potansiyellerini değerlendir.
                    
                    ### 📣 Yasal Uyarı:
                    *Bu rapor tamamen yapay zeka tarafından üretilmiş finansal simülasyon yorumudur ve yatırım tavsiyesi (YTD) kapsamında değildir.*
                    
                    VERİLER:
                    $portfolioText
                """.trimIndent()

                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                _optimizationResult.value = service.getBasketOptimization(portfolioText.toString())
            } catch (e: Exception) {
                _optimizationResult.value = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
            } finally {
                _isOptimizing.value = false
            }
        }
    }

    private val _errorMessage = MutableStateFlow<String?>(null)

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    val uiState: StateFlow<BasketDetailUiState> = combine(
        repository.getBasketById(basketId),
        repository.getBasketItems(basketId),
        repository.prices,
        repository.allCompanies,
        _errorMessage
    ) { basket: com.nexus.porsuk.data.local.entity.Basket?, items: List<com.nexus.porsuk.data.local.entity.BasketItem>, pricesMap: Map<String, com.nexus.porsuk.data.local.entity.PriceSnapshot>, companies: List<com.nexus.porsuk.data.local.entity.Company>, errorMsg: String? ->
        if (basket == null) return@combine BasketDetailUiState(errorMessage = errorMsg)

        val companyMap = companies.associateBy { it.symbol }
        var totalValue = 0.0
        var totalCost = 0.0

        val holdings = items.map { item ->
            val company = companyMap[item.symbol]
            val currentPrice = pricesMap[item.symbol]?.price 
                ?: company?.currentPrice?.takeIf { it > 0.0 } 
                ?: item.buyPrice
            val itemValue = item.quantity * currentPrice
            val itemCost = item.quantity * item.buyPrice
            
            totalValue += itemValue
            totalCost += itemCost

            HoldingUiModel(
                id = item.id,
                symbol = item.symbol,
                quantity = item.quantity,
                buyPrice = item.buyPrice,
                currentValue = itemValue,
                changePercent = if (item.buyPrice > 0) (currentPrice - item.buyPrice) / item.buyPrice * 100 else 0.0,
                allocationPercent = 0f // Calculated below
            )
        }

        val finalHoldings = holdings.map { 
            val costAllocation = if (totalCost > 0) (it.quantity * it.buyPrice / totalCost).toFloat() else 0f
            it.copy(allocationPercent = if (totalValue > 0) (it.currentValue / totalValue).toFloat() else costAllocation)
        }

        val profitLoss = totalValue - totalCost

        // Calculate Rebalance Suggestions
        val targetMap = mutableMapOf<String, Float>()
        try {
            val targetJson = settingsManager.targetAllocationJson.first()
            if (targetJson.isNotBlank()) {
                val obj = org.json.JSONObject(targetJson)
                obj.keys().forEach { key -> targetMap[key] = obj.getDouble(key).toFloat() }
            }
        } catch (_: Exception) {}

        val rebalanceSuggestions = mutableListOf<RebalanceSuggestion>()
        if (finalHoldings.size >= 2) {
            val defaultEqualTarget = 100.0f / finalHoldings.size
            finalHoldings.forEach { holding ->
                val targetPct = targetMap[holding.symbol] ?: defaultEqualTarget
                val currentPct = holding.allocationPercent * 100f
                val diff = currentPct - targetPct
                if (kotlin.math.abs(diff) >= 5.0f) {
                    val isOver = diff > 0
                    val sign = if (isOver) "+" else ""
                    val actionText = if (isOver) "fazla ağırlıklı" else "düşük ağırlıklı"
                    val desc = "${holding.symbol}: Hedef %${String.format(java.util.Locale.US, "%.1f", targetPct)}, Şu an %${String.format(java.util.Locale.US, "%.1f", currentPct)} ($sign${String.format(java.util.Locale.US, "%.1f", diff)}% $actionText)"
                    rebalanceSuggestions.add(
                        RebalanceSuggestion(
                            symbol = holding.symbol,
                            currentPercent = currentPct,
                            targetPercent = targetPct,
                            diffPercent = diff,
                            isOverweight = isOver,
                            description = desc
                        )
                    )
                }
            }
        }

        BasketDetailUiState(
            basketName = basket.name,
            market = basket.market,
            totalValue = totalValue,
            totalCost = totalCost,
            profitLossAmount = profitLoss,
            profitLossPercent = if (totalCost > 0) (profitLoss / totalCost) * 100 else 0.0,
            holdings = finalHoldings,
            rebalanceSuggestions = rebalanceSuggestions,
            isLoading = false,
            errorMessage = errorMsg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BasketDetailUiState())

    init {
        viewModelScope.launch {
            repository.getBasketItems(basketId).collect { items ->
                items.forEach { item ->
                    val basket = repository.getBasketById(basketId).first()
                    basket?.let { b ->
                        val result = repository.refreshPrice(item.symbol, b.market)
                        if (result is com.nexus.porsuk.data.remote.ScrapeResult.Success) {
                            repository.prices.update { it + (item.symbol to result.data) }
                        }
                    }
                }
            }
        }
    }

    fun deleteBasket(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.getBasketById(basketId).first()?.let {
                repository.deleteBasket(it)
                onSuccess()
            }
        }
    }

    fun renameBasket(newName: String) {
        viewModelScope.launch {
            repository.getBasketById(basketId).first()?.let {
                repository.updateBasket(it.copy(name = newName))
            }
        }
    }

    fun addStockToBasket(item: PendingBasketItem) {
        viewModelScope.launch {
            val cleanSymbol = item.symbol.trim().uppercase()
            val items = repository.getBasketItems(basketId).first()
            val existingItem = items.find { it.symbol.uppercase() == cleanSymbol }
            
            if (existingItem != null) {
                _errorMessage.value = "$cleanSymbol hisse senedi zaten bu sepette mevcut. Bir hisse 2 kez eklenemez."
                return@launch
            }

            _errorMessage.value = null
            repository.executeTransaction(
                basketId = basketId,
                symbol = cleanSymbol,
                quantity = item.quantity,
                price = item.buyPrice,
                isBuy = true
            )
        }
    }

    fun executeTransaction(symbol: String, quantity: Double, price: Double, isBuy: Boolean) {
        viewModelScope.launch {
            repository.executeTransaction(
                basketId = basketId,
                symbol = symbol,
                quantity = quantity,
                price = price,
                isBuy = isBuy
            )
        }
    }

    fun executeBatchBuy(purchases: List<Triple<String, Double, Double>>) {
        viewModelScope.launch {
            purchases.forEach { (symbol, qty, price) ->
                if (qty > 0 && price > 0) {
                    repository.executeTransaction(
                        basketId = basketId,
                        symbol = symbol,
                        quantity = qty,
                        price = price,
                        isBuy = true
                    )
                }
            }
        }
    }

    fun runBacktest(range: String) {
        viewModelScope.launch {
            _isBacktesting.value = true
            _backtestResult.value = null
            
            try {
                val stateVal = uiState.value
                val holdings = stateVal.holdings
                if (holdings.isEmpty()) {
                    _isBacktesting.value = false
                    return@launch
                }

                val holdingHistoricalLists = mutableMapOf<String, List<Double>>()
                holdings.forEach { holding ->
                    val result = repository.fetchHistoricalPrices(holding.symbol, stateVal.market, range, "1d")
                    if (result is com.nexus.porsuk.data.remote.ScrapeResult.Success && result.data.isNotEmpty()) {
                        holdingHistoricalLists[holding.symbol] = result.data
                    }
                }

                val bistResult = repository.fetchHistoricalPrices("XU100", "BIST", range, "1d")
                val bistPrices = if (bistResult is com.nexus.porsuk.data.remote.ScrapeResult.Success) bistResult.data else emptyList()

                val usdResult = repository.fetchHistoricalPrices("USDTRY", "USD", range, "1d")
                val usdPrices = if (usdResult is com.nexus.porsuk.data.remote.ScrapeResult.Success) usdResult.data else emptyList()

                val durationText = when (range) {
                    "3mo" -> "3 Ay"
                    "6mo" -> "6 Ay"
                    else -> "1 Yıl"
                }

                val bistReturn = if (bistPrices.size >= 2) {
                    ((bistPrices.last() - bistPrices.first()) / bistPrices.first()) * 100.0
                } else {
                    0.0
                }

                val usdReturn = if (usdPrices.size >= 2) {
                    ((usdPrices.last() - usdPrices.first()) / usdPrices.first()) * 100.0
                } else {
                    0.0
                }

                var basketTotalReturn = 0.0
                var totalWeight = 0.0
                holdings.forEach { holding ->
                    val prices = holdingHistoricalLists[holding.symbol]
                    if (prices != null && prices.size >= 2) {
                        val stockReturn = ((prices.last() - prices.first()) / prices.first()) * 100.0
                        basketTotalReturn += stockReturn * holding.allocationPercent
                        totalWeight += holding.allocationPercent
                    }
                }
                
                val finalBasketReturn = if (totalWeight > 0.0) basketTotalReturn / totalWeight else 0.0

                val apiKey = settingsManager.getGeminiApiKey()
                var orakulComment = ""
                
                if (!apiKey.isNullOrBlank()) {
                    val prompt = """
                        Sen "ORAKUL" adında, Wall Street'in en keskin ve acımasız borsa simsarısın.
                        Kullanıcı bir sepet oluşturdu ve bu sepetin son $durationText vadeli geçmiş performans testini (backtest) gerçekleştirdi.
                        
                        Sonuçlar:
                        - Sepet Getirisi: %${String.format(java.util.Locale.US, "%.2f", finalBasketReturn)}
                        - BIST 100 Getirisi: %${String.format(java.util.Locale.US, "%.2f", bistReturn)}
                        - Dolar (USDTRY) Getirisi: %${String.format(java.util.Locale.US, "%.2f", usdReturn)}
                        
                        Sepetteki hisseler ve ağırlıkları:
                        ${holdings.joinToString("\n") { "- ${it.symbol}: %${String.format(java.util.Locale.US, "%.1f", it.allocationPercent * 100)}" }}
                        
                        GÖREV:
                        Orakul diliyle (kendinden emin, otoriter, keskin ve samimi simsar üslubuyla) bu performansı değerlendir.
                        Sepetin endeksi veya doları yenip yenemediğini vurgula, bu başarının sebebini veya başarısızlığın kaynağını hisselere göre açıkla.
                        En fazla 3-4 cümlelik vurucu bir yorum yap. "Olabilir", "belki" deme.
                    """.trimIndent()

                    val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                    val basketItems = holdings.map { com.nexus.porsuk.data.local.entity.BasketItem(basketId = 0, symbol = it.symbol, quantity = it.quantity, buyPrice = it.buyPrice, buyDate = System.currentTimeMillis()) }
                    orakulComment = service.getBasketOrakulComment(finalBasketReturn, bistReturn, usdReturn, basketItems)
                }

                if (orakulComment.isBlank()) {
                    val relativeToBist = finalBasketReturn - bistReturn
                    orakulComment = if (relativeToBist > 0.0) {
                        "Fena değil simsar! Sepetiniz BIST100 endeksine karşı %${String.format(java.util.Locale.US, "%.2f", relativeToBist)} daha fazla getiri sağladı. Doğru hisseleri seçmişsiniz, bileşik getirinin gücü sizinle olsun!"
                    } else {
                        "Maalesef bu sepet endeks gerisinde kalmış. Dağılımı ve hisse seçimlerinizi gözden geçirmelisiniz. Orakul'un analizlerinden faydalanın!"
                    }
                }

                _backtestResult.value = BacktestResult(
                    durationText = durationText,
                    basketReturnPercent = finalBasketReturn,
                    bistReturnPercent = bistReturn,
                    usdReturnPercent = usdReturn,
                    description = orakulComment
                )

            } catch (e: Exception) {
                // Ignore
            } finally {
                _isBacktesting.value = false
            }
        }
    }

    fun updateBasketItem(itemId: Int, symbol: String, quantity: Double, buyPrice: Double) {
        viewModelScope.launch {
            val items = repository.getBasketItems(basketId).first()
            val existingItem = items.find { it.id == itemId }
            val buyDate = existingItem?.buyDate ?: System.currentTimeMillis()
            
            val updated = BasketItem(
                id = itemId,
                basketId = basketId,
                symbol = symbol,
                quantity = quantity,
                buyPrice = buyPrice,
                buyDate = buyDate
            )
            repository.addBasketItem(updated)
        }
    }

    fun deleteBasketItem(itemId: Int) {
        viewModelScope.launch {
            val items = repository.getBasketItems(basketId).first()
            val existingItem = items.find { it.id == itemId }
            existingItem?.let {
                repository.deleteBasketItem(it)
            }
        }
    }
}
