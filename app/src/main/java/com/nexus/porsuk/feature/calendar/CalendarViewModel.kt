package com.nexus.porsuk.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.IpoCalendarEntry
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.CalendarRepository
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val financeRepository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _selectedDividendMarket = MutableStateFlow("Tümü")
    private val _selectedIpoStatus = MutableStateFlow("Tümü")
    private val _selectedCountry = MutableStateFlow<String?>(null)
    private val _selectedImpact = MutableStateFlow<CalendarImpactLevel?>(null)

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
        refreshEvents()
    }

    val dividends: StateFlow<List<DividendEvent>> = calendarRepository.getAllEvents()
        .map { list -> list.filter { it.category == CalendarEventCategory.DIVIDEND } }
        .combine(_selectedDividendMarket) { list, market ->
            if (market == "Tümü") {
                list.map { DividendEvent(it.eventId, it.symbol ?: "", it.title, "N/A", "N/A", 0.0) }
            } else {
                list.filter { it.country == market }.map { DividendEvent(it.eventId, it.symbol ?: "", it.title, "N/A", "N/A", 0.0) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ipos: StateFlow<List<IpoCalendarEntry>> = financeRepository.allIpos
        .combine(_selectedIpoStatus) { list, status ->
            if (status == "Tümü") list else list.filter { it.status.uppercase() == status.uppercase() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val economicEvents: StateFlow<List<EconomicEvent>> = combine(
        calendarRepository.getAllEvents(),
        _selectedCountry,
        _selectedImpact
    ) { events, country, impact ->
        events.filter { event ->
            (country == null || event.country == country) &&
            (impact == null || event.impactLevel == impact)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            calendarRepository.refreshEvents()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun selectDividendMarket(market: String) {
        _selectedDividendMarket.value = market
        _uiState.update { it.copy(selectedDividendMarket = market) }
    }

    fun selectIpoStatus(status: String) {
        _selectedIpoStatus.value = status
        _uiState.update { it.copy(selectedIpoStatus = status) }
    }

    fun filterByCountry(country: String?) {
        _selectedCountry.value = country
        _uiState.update { it.copy(selectedCountry = country) }
    }

    fun filterByImpact(impact: CalendarImpactLevel?) {
        _selectedImpact.value = impact
        _uiState.update { it.copy(selectedImpactLevel = impact) }
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

    fun generateOrakulInsight(type: String) {
        viewModelScope.launch {
            val apiKey = settingsManager.getGeminiApiKey()
            if (apiKey.isNullOrBlank()) {
                _uiState.update { it.copy(aiError = "Gemini API Key bulunamadı.", isAiLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(isAiLoading = true, aiError = null, aiInsightText = "") }
            
            try {
                val data = if (type == "DIVIDEND") {
                    dividends.value.joinToString("\n") { "${it.symbol}: ${it.companyName}, Brüt: ${it.amount} ${it.currency}" }
                } else {
                    ipos.value.joinToString("\n") { "${it.symbol}: ${it.companyName}, Fiyat: ${it.price} TL, Durum: ${it.status}" }
                }

                val prompt = com.nexus.porsuk.data.remote.GeminiPromptBuilder.buildCalendarInsightPrompt(type, data)
                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                
                service.generateRawContent(prompt).let { result ->
                    _uiState.update { it.copy(aiInsightText = result, isAiLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(aiError = e.localizedMessage, isAiLoading = false) }
            }
        }
    }

    fun toggleIpoAlarm(context: android.content.Context, entry: IpoCalendarEntry) {
        viewModelScope.launch {
            val currentAlarms = _uiState.value.activeIpoAlarms.toMutableSet()
            val isSetting = !currentAlarms.contains(entry.symbol)

            val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = android.content.Intent(context, IpoAlarmReceiver::class.java).apply {
                putExtra("SYMBOL", entry.symbol)
                putExtra("COMPANY_NAME", entry.companyName)
                putExtra("PRICE", entry.price)
                putExtra("DISTRIBUTION", entry.distributionMethod)
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                entry.symbol.hashCode(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            if (isSetting) {
                // Halka arz sabahı saat 09:00'da alarm kur
                val calendar = java.util.Calendar.getInstance().apply {
                    timeInMillis = entry.startDate
                    set(java.util.Calendar.HOUR_OF_DAY, 9)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                }
                
                // Eğer tarih geçmişse yarın kurma (zaten başladığı için direkt bildirim de atılabilir ama standart akış tarih bazlı)
                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    currentAlarms.add(entry.symbol)
                    android.widget.Toast.makeText(context, "${entry.symbol} alarmı kuruldu.", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(context, "Talep toplama zaten başlamış veya geçmiş.", android.widget.Toast.LENGTH_SHORT).show()
                }
            } else {
                alarmManager.cancel(pendingIntent)
                currentAlarms.remove(entry.symbol)
                android.widget.Toast.makeText(context, "${entry.symbol} alarmı iptal edildi.", android.widget.Toast.LENGTH_SHORT).show()
            }

            settingsManager.saveActiveIpoAlarms(currentAlarms)
        }
    }

    fun cloneModelPortfolio(name: String, market: String, items: List<Pair<String, Double>>) {
        viewModelScope.launch {
            val basketId = financeRepository.addBasket(com.nexus.porsuk.data.local.entity.Basket(name = name, market = market))
            items.forEach { (symbol, weight) ->
                val currentPrice = financeRepository.prices.value[symbol]?.price ?: 10.0
                val targetBudget = 100000.0 * weight
                val qty = (targetBudget / currentPrice).coerceAtLeast(1.0)
                financeRepository.addBasketItem(com.nexus.porsuk.data.local.entity.BasketItem(basketId = basketId.toInt(), symbol = symbol, quantity = Math.round(qty).toDouble(), buyPrice = currentPrice, buyDate = System.currentTimeMillis()))
            }
        }
    }
}
