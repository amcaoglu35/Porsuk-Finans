package com.nexus.porsuk.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.DividendCalendarEntry
import com.nexus.porsuk.data.local.entity.IpoCalendarEntry
import com.nexus.porsuk.data.local.entity.EconomicEventEntry
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CalendarUiState(
    val isLoading: Boolean = false,
    val selectedDividendMarket: String = "Tümü", // Tümü, BIST, NASDAQ, Avrupa
    val selectedIpoStatus: String = "Tümü", // Tümü, ACTIVE, UPCOMING, COMPLETED
    
    // Temettü Hesaplayıcı State
    val calcShares: String = "",
    val calcRate: String = "",
    val calcResult: Double? = null,
    
    // Orakul Yapay Zeka Insights State
    val aiInsightText: String = "",
    val isAiLoading: Boolean = false,
    val aiError: String? = null,
    val hasGeminiKey: Boolean = false,
    val activeIpoAlarms: Set<String> = emptySet()
)

class CalendarViewModel(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    private val _selectedDividendMarket = MutableStateFlow("Tümü")
    private val _selectedIpoStatus = MutableStateFlow("Tümü")

    init {
        viewModelScope.launch {
            val key = settingsManager.getGeminiApiKey()
            _uiState.update { it.copy(hasGeminiKey = !key.isNullOrBlank()) }
        }
        viewModelScope.launch {
            settingsManager.activeIpoAlarms.collect { alarms ->
                _uiState.update { it.copy(activeIpoAlarms = alarms) }
            }
        }
    }

    val dividends: StateFlow<List<DividendCalendarEntry>> = repository.allDividends
        .combine(_selectedDividendMarket) { list, market ->
            if (market == "Tümü") {
                list
            } else {
                list.filter { entry ->
                    when (market) {
                        "BIST" -> entry.market.uppercase() == "BIST"
                        "NASDAQ" -> entry.market.uppercase() == "NASDAQ" || entry.market.uppercase() == "NYSE"
                        "Avrupa" -> entry.market.uppercase() == "FRA" || entry.market.uppercase() == "EURONEXT"
                        else -> true
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ipos: StateFlow<List<IpoCalendarEntry>> = repository.allIpos
        .combine(_selectedIpoStatus) { list, status ->
            if (status == "Tümü") {
                list
            } else {
                list.filter { it.status.uppercase() == status.uppercase() }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val economicEvents: StateFlow<List<EconomicEventEntry>> = repository.allEconomicEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDividendMarket(market: String) {
        _selectedDividendMarket.value = market
        _uiState.update { it.copy(selectedDividendMarket = market) }
    }

    fun selectIpoStatus(status: String) {
        _selectedIpoStatus.value = status
        _uiState.update { it.copy(selectedIpoStatus = status) }
    }

    // Temettü Hesaplayıcı Metotları
    fun setCalcShares(shares: String) {
        _uiState.update { it.copy(calcShares = shares) }
        calculateDividend()
    }

    fun setCalcRate(rate: String) {
        _uiState.update { it.copy(calcRate = rate) }
        calculateDividend()
    }

    private fun calculateDividend() {
        val shares = _uiState.value.calcShares.toDoubleOrNull()
        val rate = _uiState.value.calcRate.toDoubleOrNull()
        if (shares != null && rate != null) {
            _uiState.update { it.copy(calcResult = shares * rate) }
        } else {
            _uiState.update { it.copy(calcResult = null) }
        }
    }

    fun clearCalculator() {
        _uiState.update { it.copy(calcShares = "", calcRate = "", calcResult = null) }
    }

    // Orakul Yapay Zeka Metodu
    fun generateOrakulInsight(type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiInsightText = "", aiError = null) }
            try {
                val apiKey = settingsManager.getGeminiApiKey()
                if (apiKey.isNullOrBlank()) {
                    _uiState.update { it.copy(isAiLoading = false, aiError = "Gemini API anahtarı bulunamadı. Ayarlar'dan ekleyin.") }
                    return@launch
                }

                val prompt = when (type) {
                    "DIVIDEND" -> {
                        val divList = dividends.value.joinToString("\n") { 
                            "• ${it.symbol} | ${it.companyName} | Hak Kazanma: ${formatDate(it.exDividendDate)} | Brüt Temettü: ${it.rate} ${getCurrencySymbol(it.market)} | Verim: %${it.yieldPercentage}"
                        }
                        """
                            Sen "ORAKUL" adında, Wall Street'in en acımasız ve en deneyimli temettü simsarı ve finans üstadısın.
                            Kullanıcıya aşağıdaki temettü takvimine ve verilerine göre asimetrik ve keskin bir temettü yatırım raporu sun.
                            
                            Yaklaşan Temettü Listesi:
                            $divList
                            
                            GÖREVLER:
                            1. Temettü listesindeki en cazip (temettü verimi ve sektör kararlılığı açısından yüksek) 2-3 şirketi detaylı analiz et.
                            2. Temettü ödemesinden faydalanıp anında satma (Dividend Stripping) tuzağına karşı uyarılarda bulun ve Orakul'un tescilli uzun vadeli bileşik getiri (Compound Interest) formülünü açıkla.
                            3. Makro ekonomik koşullara göre (enflasyon, faiz vb.) temettü büyümesi (Dividend Growth Investing) yapabilecek hisseleri işaret et.
                            4. Otoriter, kendinden emin, profesyonel borsa simsarı dili kullan. "Olabilir", "belki" gibi kelimeler kullanma. Net AL/BEKLE tavsiyeleri ver.
                        """.trimIndent()
                    }
                    else -> {
                        val ipoList = ipos.value.joinToString("\n") {
                            "• ${it.symbol} | ${it.companyName} | Talep Tarihi: ${formatDate(it.startDate)} - ${formatDate(it.endDate)} | Fiyat: ${it.price} TL | Dağıtım: ${it.distributionMethod} | Lider: ${it.broker} | Katılım Endeksi: ${if (it.isCatkatEnabled) "Uygun" else "Uygun Değil"} | Durum: ${it.status}"
                        }
                        """
                            Sen "ORAKUL" adında, halka arz (IPO) piyasalarının efsanevi lideri ve en acımasız borsa simsarı/üstadısın.
                            Kullanıcıya aşağıdaki halka arz (IPO) listesine göre net, iddialı ve kazanç odaklı bir halka arz analiz raporu sun.
                            
                            Halka Arz Listesi:
                            $ipoList
                            
                            GÖREVLER:
                            1. Yaklaşan ve aktif olan halka arzlar arasından talep miktarı, fiyatlandırma kalitesi ve dağıtım yöntemi (Eşit/Oransal) açısından tavan serisi potansiyeli en yüksek olanları seçip O-EAGI benzeri mantıkla analiz et.
                            2. Katılım endeksi kriterinin ve lider aracı kurumların (broker) geçmiş halka arz başarılarının önemini açıkla.
                            3. Borsaya yeni girmiş (COMPLETED) şirketlerin ilk günlerdeki fiyat hareketlerini ve "tavan bozma" anlarındaki simsar taktiklerini anlat.
                            4. Otoriter, keskin ve net bir simsar üslubuyla konuş.
                        """.trimIndent()
                    }
                }

                // AI Model Çağrısı
                var accumulated = ""
                var success = false
                var lastException: Exception? = null
                val models = com.nexus.porsuk.ui.common.GeminiModels.fallbackList

                for (modelName in models) {
                    try {
                        val generativeModel = com.google.ai.client.generativeai.GenerativeModel(
                            modelName = modelName,
                            apiKey = apiKey
                        )
                        val responseStream = generativeModel.generateContentStream(prompt)
                        responseStream.collect { chunk ->
                            accumulated += chunk.text ?: ""
                            _uiState.update { it.copy(aiInsightText = accumulated) }
                        }
                        success = true
                        break
                    } catch (e: Exception) {
                        lastException = e
                    }
                }

                if (!success) {
                    throw lastException ?: Exception("Yapay zeka modeli başlatılamadı.")
                }

                _uiState.update { it.copy(isAiLoading = false) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAiLoading = false,
                        aiError = e.message ?: "Bilinmeyen bir hata oluştu."
                    )
                }
            }
        }
    }

    fun toggleIpoAlarm(context: android.content.Context, entry: IpoCalendarEntry) {
        val symbol = entry.symbol
        val currentAlarms = _uiState.value.activeIpoAlarms.toMutableSet()
        val isAdding = !currentAlarms.contains(symbol)

        val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = android.content.Intent(context, IpoAlarmReceiver::class.java).apply {
            putExtra("SYMBOL", entry.symbol)
            putExtra("COMPANY_NAME", entry.companyName)
            putExtra("PRICE", entry.price)
            putExtra("LOTS", entry.lotQuantity)
            putExtra("DISTRIBUTION", entry.distributionMethod)
        }
        
        val requestCode = symbol.hashCode()
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        if (isAdding) {
            currentAlarms.add(symbol)
            val now = System.currentTimeMillis()
            var alarmTime = entry.startDate
            if (alarmTime <= now + 60000) {
                alarmTime = now + 5000 // demo/testing in 5 seconds
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        android.app.AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                }
                android.widget.Toast.makeText(
                    context, 
                    "$symbol için halka arz alarmı kuruldu!", 
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } catch (e: SecurityException) {
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
                android.widget.Toast.makeText(
                    context, 
                    "$symbol alarmı kuruldu.", 
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            currentAlarms.remove(symbol)
            alarmManager.cancel(pendingIntent)
            android.widget.Toast.makeText(
                context, 
                "$symbol alarmı iptal edildi.", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        _uiState.update { it.copy(activeIpoAlarms = currentAlarms) }
        viewModelScope.launch {
            settingsManager.saveActiveIpoAlarms(currentAlarms)
        }
    }

    private fun formatDate(timestamp: Long): String {
        return java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale("tr")).format(java.util.Date(timestamp))
    }

    private fun getCurrencySymbol(market: String): String {
        return when (market.uppercase()) {
            "NASDAQ", "NYSE" -> "$"
            "FRA", "EURONEXT" -> "€"
            else -> "₺"
        }
    }

    fun cloneModelPortfolio(name: String, market: String, items: List<Pair<String, Double>>) {
        viewModelScope.launch {
            val basketId = repository.addBasket(com.nexus.porsuk.data.local.entity.Basket(name = name, market = market))
            val currentPrices = repository.prices.value
            val companies = repository.allCompanies.first()
            val companyMap = companies.associateBy { it.symbol }
            
            items.forEach { (symbol, weight) ->
                val currentPrice = currentPrices[symbol]?.price ?: companyMap[symbol]?.currentPrice ?: 10.0
                // Default target portfolio budget is 100,000 TL for model portfolio allocation
                val targetBudget = 100000.0 * weight
                val qty = (targetBudget / currentPrice).coerceAtLeast(1.0)
                
                repository.addBasketItem(
                    com.nexus.porsuk.data.local.entity.BasketItem(
                        basketId = basketId.toInt(),
                        symbol = symbol,
                        quantity = Math.round(qty).toDouble(),
                        buyPrice = currentPrice,
                        buyDate = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
