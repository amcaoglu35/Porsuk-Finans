package com.nexus.porsuk.ui.ailab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.repository.FinanceRepository
import com.nexus.porsuk.feature.ailab.AiLabUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AiLabViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiLabUiState())
    val uiState: StateFlow<AiLabUiState> = _uiState.asStateFlow()

    fun runTool(toolName: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    toolLoadingStates = state.toolLoadingStates + (toolName to true),
                    toolErrorStates = state.toolErrorStates + (toolName to null)
                )
            }
            
            try {
                val apiKey = settingsManager.getGeminiApiKey()
                if (apiKey.isNullOrBlank()) {
                    throw Exception("Gemini API Key eksik. Lütfen Ayarlar menüsünden geçerli bir API Key tanımlayın.")
                }

                // Gerçek verilerden oluşturulan zengin bağlam (Context)
                val contextData = when(toolName) {
                    "Portfolio Health Check" -> {
                        val items = repository.getAllBasketItemsDirect()
                        "Portföy Varlıkları: " + items.joinToString { "${it.symbol} (${it.quantity} lot, Alış: ${it.buyPrice} TL)" }
                    }
                    "Stock Compare" -> {
                        val watchlist = repository.watchlist.first()
                        val companies = repository.allCompanies.first().filter { it.symbol in watchlist.map { w -> w.symbol } }.take(5)
                        "Karşılaştırılacak Hisseler: " + companies.joinToString { "${it.symbol} (${it.name}, Fiyat: ${it.currentPrice}, Değişim: %${it.changePercent})" }
                    }
                    "Sector Compare" -> {
                        val companies = repository.allCompanies.first()
                        val sectorMap = companies.groupBy { it.sector }
                        "Sektör Dağılımları: " + sectorMap.map { (sector, list) -> "$sector: ${list.size} şirket, ort. değişim: %${list.map { c -> c.changePercent }.average()}" }.joinToString("; ")
                    }
                    "AI Screener" -> {
                        val companies = repository.allCompanies.first().take(15)
                        "Taranan Hisseler: " + companies.joinToString { "${it.symbol} (Fiyat: ${it.currentPrice}, Sektör: ${it.sector})" }
                    }
                    "Dividend Finder" -> {
                        val companies = repository.allCompanies.first().take(15)
                        "Temettü Adayları: " + companies.joinToString { "${it.symbol} (${it.name})" }
                    }
                    "Growth Finder" -> {
                        val companies = repository.allCompanies.first().filter { it.changePercent > 0 }.take(10)
                        "Büyüme Odaklı Hisseler: " + companies.joinToString { "${it.symbol} (%${it.changePercent} artış)" }
                    }
                    "Value Finder" -> {
                        val companies = repository.allCompanies.first().take(10)
                        "Değer Odaklı Hisseler: " + companies.joinToString { "${it.symbol} (Fiyat: ${it.currentPrice})" }
                    }
                    "Momentum Finder" -> {
                        val companies = repository.allCompanies.first().sortedByDescending { it.changePercent }.take(10)
                        "Momentum Liderleri: " + companies.joinToString { "${it.symbol} (%${it.changePercent})" }
                    }
                    "Risk Scanner" -> {
                        val items = repository.getAllBasketItemsDirect()
                        "Risk Taraması Yapılacak Varlıklar: " + items.joinToString { it.symbol }
                    }
                    "Portfolio Diversification" -> {
                        val items = repository.getAllBasketItemsDirect()
                        "Çeşitlendirme Verileri: " + items.joinToString { "${it.symbol} (${it.quantity})" }
                    }
                    "AI Opportunity Finder" -> {
                        val dipStocks = repository.allCompanies.first().sortedBy { it.changePercent }.take(10)
                        "Fırsat Adayları: " + dipStocks.joinToString { "${it.symbol} (%${it.changePercent})" }
                    }
                    "AI Watchlist Analyzer" -> {
                        val watchlist = repository.watchlist.first()
                        "Takip Listesi Hisseleri: " + watchlist.joinToString { it.symbol }
                    }
                    "AI Earnings Summary" -> {
                        val companies = repository.allCompanies.first().take(8)
                        "Bilanço Özeti İstenen Hisseler: " + companies.joinToString { it.symbol }
                    }
                    "AI News Summary" -> {
                        val watchlist = repository.watchlist.first()
                        val firstSymbol = watchlist.firstOrNull()?.symbol ?: "THYAO"
                        val newsList = repository.getNews(firstSymbol).first().take(5)
                        "Son Haberler ($firstSymbol): " + newsList.joinToString { it.title }
                    }
                    "Economic Impact Analyzer" -> {
                        val indicators = repository.getMacroIndicators()
                        "Makroekonomik Göstergeler: $indicators"
                    }
                    else -> {
                        val companies = repository.allCompanies.first().take(10)
                        "Piyasa Bağlamı: " + companies.joinToString { it.symbol }
                    }
                }

                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val report = service.runLabTool(toolName, contextData)
                
                _uiState.update { state ->
                    state.copy(
                        toolReports = state.toolReports + (toolName to report),
                        toolLoadingStates = state.toolLoadingStates + (toolName to false)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        toolErrorStates = state.toolErrorStates + (toolName to e.localizedMessage),
                        toolLoadingStates = state.toolLoadingStates + (toolName to false)
                    )
                }
            }
        }
    }
}
