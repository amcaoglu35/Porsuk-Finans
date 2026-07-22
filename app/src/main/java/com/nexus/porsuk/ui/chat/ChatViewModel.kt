package com.nexus.porsuk.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Locale

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Merhaba! Ben Borsa Profesörü. Sana portföyün, hisse senetlerin ve piyasa durumun hakkında rehberlik etmek için buradayım. Nereden başlayalım?", false)
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val hasApiKey = settingsManager.geminiApiKeyFlow.map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), !settingsManager.getGeminiApiKey().isNullOrBlank())



    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 1. Kullanıcı mesajını listeye ekle
        val userMsg = ChatMessage(text, true)
        _messages.update { it + userMsg }

        // 2. Gemini kurulmamışsa uyar
        val apiKey = settingsManager.getGeminiApiKey()
        if (apiKey.isNullOrBlank()) {
            _messages.update { it + ChatMessage("Hata: Lütfen öncelikle Ayarlar sayfamından geçerli bir Gemini API anahtarı tanımlayın.", false) }
            return
        }



        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Canlı web araması yap (gerekli ise)
                val webContext = searchWebIfRelevant(text)

                // 3. Portföy verilerini çekip bağlam oluştur
                val baskets = repository.allBaskets.first()
                val companies = repository.allCompanies.first()
                val watchlist = repository.watchlist.first()
                val companyMap = companies.associateBy { it.symbol }

                // Tüm sepet elemanlarını elde et
                val allBasketItems = repository.getAllBasketItemsDirect()

                val portfolioContext = StringBuilder()
                portfolioContext.append("Kullanıcı Portföy Bilgileri:\n")
                
                baskets.forEach { basket ->
                    portfolioContext.append("- Sepet: ${basket.name} (${basket.market})\n")
                    val items = allBasketItems.filter { it.basketId == basket.id }
                    val currency = when (basket.market.uppercase()) {
                        "NASDAQ", "NYSE" -> "USD"
                        "FRA", "EURONEXT" -> "EUR"
                        else -> "TL"
                    }
                    items.forEach { item ->
                        val company = companyMap[item.symbol]
                        val currentPrice = company?.currentPrice ?: item.buyPrice
                        portfolioContext.append("  * Hisse: ${item.symbol}, Miktar: ${item.quantity}, Alış Fiyatı: ${item.buyPrice} $currency, Güncel Fiyat: $currentPrice $currency\n")
                    }
                }

                portfolioContext.append("\nİzleme Listesi: ")
                portfolioContext.append(watchlist.joinToString { it.symbol })
                portfolioContext.append("\n")

                // 4. Gemini AI Motorunu Çağır (Merkezi GeminiService)
                val geminiService = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                val replyText = geminiService.chat(
                    prompt = text,
                    portfolioContext = portfolioContext.toString(),
                    webContext = webContext.toString()
                )
                _messages.update { it + ChatMessage(replyText, false) }
            } catch (e: Exception) {
                _messages.update { it + ChatMessage(com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e), false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun searchWebIfRelevant(text: String): String {
        val query = text.lowercase(Locale.ROOT)
        val isRelevant = query.contains("haber") || query.contains("güncel") || 
                         query.contains("son durum") || query.contains("neden") ||
                         query.contains("ne oldu") || query.contains("fiyat") ||
                         query.contains("endeks") || query.contains("piyasa")
        
        if (!isRelevant) return ""
        
        val searchResults = searchDuckDuckGo(text)
        return if (searchResults.startsWith("Arama yapılamadı")) "" 
               else "\nİnternet Canlı Arama Sonuçları:\n$searchResults\n"
    }

    private suspend fun searchDuckDuckGo(query: String): String = withContext(Dispatchers.IO) {
        try {
            val url = "https://html.duckduckgo.com/html/?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                .timeout(6000)
                .get()
            
            val results = doc.select(".result__body").take(3).mapIndexed { index, element ->
                val title = element.select(".result__title").text()
                val snippet = element.select(".result__snippet").text()
                "${index + 1}. $title: $snippet"
            }.joinToString("\n")
            
            if (results.isBlank()) "Sonuç bulunamadı." else results
        } catch (e: Exception) {
            "Arama yapılamadı."
        }
    }
}
