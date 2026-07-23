package com.nexus.porsuk.ui.orakul

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.entity.Basket
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.remote.OracleAnalysisEngine
import com.nexus.porsuk.data.remote.OraclePortfolioReport
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

enum class OrakulMode(val label: String, val emoji: String, val description: String) {
    KAZI("Derin Kazı", "⛏️", "Sıfırdan sepet · KAZI formülü"),
    BASKET("Sepet Tasarımı", "🧺", "Model sepet kur"),
    ASK("Soru Sor", "💬", "Orakul'a direkt sor")
}

data class OrakulDecision(
    val symbol: String,
    val decision: String,       // "AL", "BEKLE", "SAT"
    val reason: String,
    val confidence: Int,        // 0–100
    val formulaLayer: String = "",
    val weight: Double = 0.0,
    val rsi: Double = 50.0,
    val sma20: Double = 0.0,
    val sma50: Double = 0.0,
    val crossSignal: String = "NÖTR" // "GOLDEN_CROSS", "DEATH_CROSS", "NÖTR"
)

data class RebalanceTrade(
    val symbol: String,
    val currentQty: Double,
    val currentPrice: Double,
    val targetWeight: Double,
    val targetQty: Double,
    val tradeQty: Double,       // Pozitif = Al, Negatif = Sat
    val valueDiff: Double,
    val decision: String
)

data class OrakulHistoryEntry(
    val timestamp: String,
    val mode: String,
    val decisionCount: Int,
    val topDecision: String      // "THYAO → AL"
)

data class OrakulStressScenario(
    val scenario: String,        // Örn: "Dolar Kuru %20 Artarsa"
    val impact: String,          // Örn: "+%12.4" veya "-%8.2"
    val advice: String           // Örn: "Döviz bazlı varlıklar koruyor..."
)

data class OrakulUiState(
    val isLoading: Boolean = false,
    val selectedMode: OrakulMode = OrakulMode.KAZI,
    val streamingText: String = "",      // Canlı streaming yanıt
    val rawResponse: String? = null,
    val decisions: List<OrakulDecision> = emptyList(),
    val lastAnalysisTime: String? = null,
    val hasGeminiKey: Boolean = false,
    val error: String? = null,
    val customQuestion: String = "",
    val investmentAmount: String = "",
    val selectedTerm: String = "Orta Vade", // "Kısa Vade", "Orta Vade", "Uzun Vade"
    val selectedMarket: String = "Tümü", // "Tümü", "BIST", "NASDAQ", "Avrupa"
    val history: List<OrakulHistoryEntry> = emptyList(),
    val marketSentimentScore: Int = 65,
    val rebalanceTrades: List<RebalanceTrade> = emptyList(),
    val rebalanceBaskets: List<Basket> = emptyList(),
    val selectedRebalanceBasketId: Int? = null,
    val stressScenarios: List<OrakulStressScenario> = emptyList(),
    val basketRiskProfile: String = "BALANCED", // CONSERVATIVE, BALANCED, AGGRESSIVE
    val basketStrategyFocus: String = "VALUE", // VALUE, GROWTH, DIVIDEND, MIXED
    val basketStockCount: Int = 5,
    val basketCashPct: Double = 10.0,
    val basketReport: OraclePortfolioReport? = null   // Oracle 2.0 — 17 metrik analiz raporu
)

class OrakulViewModel(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrakulUiState())
    val uiState: StateFlow<OrakulUiState> = _uiState

    init {
        viewModelScope.launch {
            val key = settingsManager.getGeminiApiKey()
            val companies = repository.allCompanies.first()
            val avgChange = if (companies.isNotEmpty()) companies.map { it.changePercent }.average() else 1.2
            val calculatedSentiment = (50 + avgChange * 12).toInt().coerceIn(15, 95)
            _uiState.update {
                it.copy(
                    hasGeminiKey = !key.isNullOrBlank(),
                    marketSentimentScore = calculatedSentiment
                )
            }
        }
    }

    fun selectMode(mode: OrakulMode) {
        _uiState.update {
            it.copy(
                selectedMode = mode,
                rawResponse = null,
                streamingText = "",
                decisions = emptyList(),
                error = null
            )
        }
    }

    fun setCustomQuestion(q: String) {
        _uiState.update { it.copy(customQuestion = q) }
    }

    fun setInvestmentAmount(amount: String) {
        _uiState.update { it.copy(investmentAmount = amount) }
    }

    fun setSelectedTerm(term: String) {
        _uiState.update { it.copy(selectedTerm = term) }
    }

    fun setSelectedMarket(market: String) {
        _uiState.update { it.copy(selectedMarket = market) }
    }

    fun setBasketRiskProfile(profile: String) {
        _uiState.update { it.copy(basketRiskProfile = profile) }
    }

    fun setBasketStrategyFocus(focus: String) {
        _uiState.update { it.copy(basketStrategyFocus = focus) }
    }

    fun setBasketStockCount(count: Int) {
        _uiState.update { it.copy(basketStockCount = count) }
    }

    fun setBasketCashPct(pct: Double) {
        _uiState.update { it.copy(basketCashPct = pct) }
    }

    fun analyze() {
        val currentMode = _uiState.value.selectedMode
        val question = _uiState.value.customQuestion
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null, rawResponse = null, streamingText = "", decisions = emptyList())
            }
            try {
                val apiKey = settingsManager.getGeminiApiKey()
                if (apiKey.isNullOrBlank()) {
                    _uiState.update { it.copy(isLoading = false, error = "Gemini API anahtarı bulunamadı. Ayarlar'dan ekleyin.") }
                    return@launch
                }

                val allCompanies = repository.allCompanies.first()
                val selectedMarket = _uiState.value.selectedMarket
                val companiesFiltered = when (selectedMarket) {
                    "BIST" -> allCompanies.filter { it.market?.uppercase() == "BIST" }
                    "NASDAQ" -> allCompanies.filter { it.market?.uppercase() == "NASDAQ" || it.market?.uppercase() == "NYSE" }
                    "Avrupa" -> allCompanies.filter { it.market?.uppercase() == "FRA" || it.market?.uppercase() == "EURONEXT" }
                    else -> allCompanies
                }

                val baskets = repository.allBaskets.first()
                val allItems = repository.getAllBasketItemsDirect()
                val companyMap = allCompanies.associateBy { it.symbol }

                val rates = repository.exchangeRates.value
                val usdRate = rates["USD"] ?: 34.5
                val eurRate = rates["EUR"] ?: 37.2
                val pricesMap = repository.prices.value

                // Portföy USD bazlı getiri map'i
                val portfolioUsdGainMap = mutableMapOf<String, String>()
                allItems.forEach { item ->
                    val company = companyMap[item.symbol]
                    val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
                    val buyUsd = when (company?.market?.uppercase()) {
                        "NASDAQ", "NYSE" -> item.buyPrice
                        "FRA", "EURONEXT" -> item.buyPrice * (eurRate / usdRate)
                        else -> item.buyPrice / usdRate // BIST (TL)
                    }
                    val currentUsd = when (company?.market?.uppercase()) {
                        "NASDAQ", "NYSE" -> currentPrice
                        "FRA", "EURONEXT" -> currentPrice * (eurRate / usdRate)
                        else -> currentPrice / usdRate // BIST (TL)
                    }
                    val usdGain = if (buyUsd > 0) ((currentUsd - buyUsd) / buyUsd * 100.0) else 0.0
                    portfolioUsdGainMap[item.symbol.uppercase()] = String.format(java.util.Locale.US, "%+.1f%%", usdGain)
                }

                val filteredCompanies = companiesFiltered

                // Daha zengin teknik veri, USD bazlı getiri, global borsa desteği
                val companyInfoList = filteredCompanies.map { c ->
                    val info = try { repository.getCachedInfo(c.symbol).first() } catch (_: Exception) { null }
                    val newsList = try { repository.getNews(c.symbol).first() } catch (_: Exception) { emptyList() }
                    
                    val newsSummary = if (newsList.isNotEmpty()) {
                        newsList.take(3).joinToString("; ") { "${it.title} (Duyarlılık: ${it.sentiment ?: "NEUTRAL"})" }
                    } else "Son haber bulunamadı"

                    val high52: Double? = info?.week52High
                    val low52: Double? = info?.week52Low
                    val price: Double = pricesMap[c.symbol]?.price ?: c.currentPrice

                    // 52h içindeki fiyat pozisyonu (0 = dip, 100 = zirve)
                    val position52 = if (high52 != null && low52 != null && high52 > low52) {
                        val pct = ((price - low52) / (high52 - low52) * 100.0).toInt().coerceIn(0, 100)
                        "$pct% (52h arası)"
                    } else "?"

                    // Tahmini momentum bölgesi
                    val momentumZone: String = if (high52 != null && low52 != null && high52 > low52) {
                        val pct: Double = (price - low52) / (high52 - low52)
                        when {
                            pct < 0.25 -> "DİP BÖLGE"
                            pct < 0.50 -> "ALT ORTA"
                            pct < 0.75 -> "ÜST ORTA"
                            else       -> "ZİRVE BÖLGE"
                        }
                    } else "?"

                    // Tahmini RSI (52h pozisyona dayalı yaklaşım, gerçek RSI için OHLCV gerekir)
                    val approxRsi: String = if (high52 != null && low52 != null && high52 > low52) {
                        val pct = (price - low52) / (high52 - low52)
                        val rsi = (pct * 100.0).coerceIn(10.0, 90.0)
                        String.format(java.util.Locale.US, "%.0f", rsi)
                    } else "50"

                    // Piyasa tag (BIST, NASDAQ, NYSE, FRA)
                    val marketTag = when (c.market?.uppercase()) {
                        "NASDAQ" -> "[NASDAQ]"
                        "NYSE" -> "[NYSE]"
                        "FRA", "EURONEXT" -> "[AVRUPA]"
                        else -> "[BIST]"
                    }

                    val usdGainStr = portfolioUsdGainMap[c.symbol.uppercase()] ?: "—"
                    val currencySymbol = when (c.market?.uppercase()) {
                        "NASDAQ", "NYSE" -> "USD"
                        "FRA", "EURONEXT" -> "EUR"
                        else -> "TL"
                    }

                    "• ${c.symbol} $marketTag | ${c.name} | Sektör: ${c.sector} | Fiyat: $price $currencySymbol | " +
                    "F/K: ${info?.peRatio ?: "?"} | " +
                    "52h Pozisyon: $position52 | Momentum Bölgesi: $momentumZone | " +
                    "Yaklaşık RSI(14): $approxRsi | " +
                    "USD Bazlı Portföy Getirisi: $usdGainStr | " +
                    "Temettü: ${info?.dividendYield ?: "?"} | Piyasa Değeri: ${info?.marketCap ?: "?"} | " +
                    "Haber Akışı: $newsSummary"
                }
                val companyLines = companyInfoList.joinToString("\n")

                val portfolioLines = if (baskets.isNotEmpty()) {
                    val sb = StringBuilder()
                    baskets.forEach { basket ->
                        val currencySymbol = when (basket.market.uppercase()) {
                            "NASDAQ", "NYSE" -> "USD"
                            "FRA", "EURONEXT" -> "EUR"
                            else -> "TL"
                        }
                        sb.append("Sepet: ${basket.name} (${basket.market})\n")
                        allItems.filter { it.basketId == basket.id }.forEach { item ->
                            val cp = companyMap[item.symbol]?.currentPrice ?: item.buyPrice
                            val gain = if (item.buyPrice > 0) ((cp - item.buyPrice) / item.buyPrice * 100) else 0.0
                            sb.append("  ${item.symbol}: Alış ${item.buyPrice} $currencySymbol → Şimdi $cp $currencySymbol (${String.format("%+.1f", gain)}%)\n")
                        }
                    }
                    sb.toString()
                } else "Portföyde henüz hisse yok."

                // STREAMING: buildPrompt() ile mode'a özgü doğru prompt üretilir ve GeminiService'e gönderilir
                val orakulPrompt = buildPrompt(currentMode, companyLines, portfolioLines, question)
                val service = com.nexus.porsuk.data.remote.GeminiService(apiKey)
                var accumulated = ""
                service.getOrakulStream(orakulPrompt).collect { chunk ->
                    accumulated += chunk
                    _uiState.update { it.copy(streamingText = accumulated) }
                }

                // Streaming bitti, parse et
                val parsed = if (currentMode != OrakulMode.ASK) parseDecisions(accumulated) else emptyList()
                val stressScenarios = if (currentMode == OrakulMode.BASKET) {
                    parseStressScenarios(accumulated)
                } else emptyList()
                val now = java.text.SimpleDateFormat("HH:mm", java.util.Locale("tr")).format(java.util.Date())

                // Oracle 2.0: 17 metrik portföy raporu (sadece BASKET modunda)
                val basketReport: OraclePortfolioReport? = if (currentMode == OrakulMode.BASKET && parsed.isNotEmpty()) {
                    val allCompaniesForReport = repository.allCompanies.first()
                    val cachedInfos = parsed.mapNotNull { d ->
                        try { repository.getCachedInfo(d.symbol).first() } catch (_: Exception) { null }
                    }
                    val rates = repository.exchangeRates.value
                    val usdRate = rates["USD"] ?: 34.5
                    OracleAnalysisEngine.analyze(parsed, allCompaniesForReport, cachedInfos, usdRate)
                } else null

                // Geçmişe ekle
                val historyEntry = OrakulHistoryEntry(
                    timestamp = now,
                    mode = currentMode.label,
                    decisionCount = parsed.size,
                    topDecision = parsed.firstOrNull()?.let { "${it.symbol} → ${it.decision}" } ?: "Yorum analizi"
                )
                val newHistory = (_uiState.value.history + historyEntry).takeLast(5)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rawResponse = accumulated,
                        decisions = parsed,
                        stressScenarios = stressScenarios,
                        lastAnalysisTime = now,
                        error = null,
                        history = newHistory,
                        basketReport = basketReport
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = com.nexus.porsuk.ui.common.GeminiErrorParser.parse(e)
                    )
                }
            }
        }
    }

    private fun buildPrompt(
        mode: OrakulMode,
        companyLines: String,
        portfolioLines: String,
        customQuestion: String
    ): String {
        val selectedMarket = _uiState.value.selectedMarket
        val marketName = when (selectedMarket) {
            "BIST" -> "BIST (Türkiye)"
            "NASDAQ" -> "NASDAQ / NYSE (ABD)"
            "Avrupa" -> "Avrupa (FRA / EURONEXT)"
            else -> "Tüm Piyasalar (BIST, NASDAQ, NYSE, Avrupa)"
        }

        val rates = repository.exchangeRates.value
        val usdRate = rates["USD"] ?: 34.5
        val eurRate = rates["EUR"] ?: 37.2

        val orakulPersona = """
            Sen "ORAKUL 2.0" adında, Wall Street ve BIST'in en üst düzey Baş Kantitatif Stratejisti (Head of Quant Research) ve Fon Yöneticisisin. 
            
            GÜNCEL MAKRO & PİYASA REGİMASI (TCMB EVDS & KURLAR):
            - 1 USD = $usdRate TL | 1 EUR = $eurRate TL
            - TCMB Politika Faizi: %50.0 | Yıllık TÜFE Enflasyonu: %61.8 | Reel Efektif Kur (REER): 61.5
            - Tahvil Getiri Eğrisi: 2Y %42.5 vs 10Y %28.3 (Ters Getiri Eğrisi / Macro Regime)
            
            KANTİTATİF VE ADLİ MUHASEBE ANALİZ ÇERÇEVESİ (KUZEY YILDIZI):
            Hisseleri analiz ederken aşağıdaki 5 disiplinli adım sırasını (Chain-of-Thought) harfiyen uygularsın:
            1. **Altman Z-Score & Beneish M-Score Tespiti:** Şirket iflas riski (Z < 1.81) veya muhasebe manipülasyon şüphesi (M > -1.78) var mı? Kırmızı bayrak varsa anında cezalandır.
            2. **DuPont ROE Ayrıştırması:** ROE kârlılığı (Net Marj × Varlık Devir Hızı × Kaldıraç) kaliteli faaliyet nakit akışından mı yoksa aşırı borçlanmadan mı geliyor?
            3. **FCF Yield & Nakit Kalitesi:** Serbest Nakit Akışı (FCF) verimi ve İşletme Nakit Akışı / Net Kâr oranı sağlıklı mı?
            4. **O-EAGI 2.0 Skorlaması (0-100):**
               - %30 Temel & İçsel Değer Güvenliği (Graham/Lynch, FCF Yield)
               - %25 Adli Muhasebe & Sağlık Skoru (Altman Z-Score, Beneish M-Score, Piotroski F-Score)
               - %25 Haber & Sentiment Entropisi (KAP bildirimi, duyarlılık)
               - %20 İvme & Teknik Teyit (Stochastic RSI, 50/200 SMA Cross)
            
            Karar Eşikleri:
            - O-EAGI >= 75: Kesin "AL" (Asimetrik yüksek getiri fırsatı)
            - 45 <= O-EAGI < 75: "BEKLE" (Pozisyonu koru veya izle)
            - O-EAGI < 45: Kesin "SAT" (Nakit korumasına geç)
            
            ÜSLUBUN:
            - Keskin, matematiksel olarak sarsılmaz, otoriter ve derinlemesine finans jargonuna hakim.
            - Kaçamak kelimeler ("olabilir", "belki") ASLA kullanmazsın. Raporlarını rakamsal rasyolar, DuPont kırılımları ve makro reçetelerle zenginleştirirsin.
        """.trimIndent()

        return when (mode) {
            OrakulMode.BASKET -> {
                val currencyUnit = when (selectedMarket) {
                    "NASDAQ" -> "USD"
                    "Avrupa" -> "EUR"
                    else -> "TL"
                }

                val budgetText = if (_uiState.value.investmentAmount.isNotBlank()) {
                    "Kullanıcının yatırım yapacağı bütçe: ${_uiState.value.investmentAmount} $currencyUnit"
                } else "Kullanıcının bütçesi belirsiz."

                val termText = "Kullanıcının yatırım vadesi tercihi: ${_uiState.value.selectedTerm} (Kısa Vade: 1-3 Ay, Orta Vade: 6-12 Ay, Uzun Vade: 1-3 Yıl)"
                val riskProfile = _uiState.value.basketRiskProfile
                val strategyFocus = _uiState.value.basketStrategyFocus
                val stockCount = _uiState.value.basketStockCount
                val cashPct = _uiState.value.basketCashPct

                val marketPhilosophyText = when (selectedMarket) {
                    "BIST" -> "TL bazlı enflasyon koruması ve hisse senedi getiri potansiyelini ele alış şekli"
                    "NASDAQ" -> "USD bazlı küresel teknoloji büyümesi ve faiz politikalarını ele alış şekli"
                    "Avrupa" -> "EUR bazlı euro bölgesi sanayi büyümesi ve istikrar politikalarını ele alış şekli"
                    else -> "global/BIST karışımının USD döviz riskini ele alış şekli"
                }

                """
                $orakulPersona
                
                Analiz edilecek piyasa: $marketName
                YATIRIM BÜTÇESİ: $budgetText
                YATIRIM VADESİ: $termText
                YATIRIM RİSK PROFİLİ: $riskProfile (CONSERVATIVE: Defansif, BALANCED: Dengeli, AGGRESSIVE: Agresif)
                YATIRIM STRATEJİ ODAĞI: $strategyFocus (VALUE: Değer, GROWTH: Büyüme, DIVIDEND: Temettü, MIXED: Karma)
                HEDEF HİSSE SAYISI: $stockCount hisse
                NAKİT ORANI: %$cashPct (Sepette ayrılacak korumalı nakit oranı)
                
                Aşağıdaki $marketName hisseleri arasından O-EAGI formülünü geçenleri seç ve efsanevi bir $stockCount hisselik model sepet tasarla. Listede yer almayan hiçbir hisseyi sepete ekleme.
                Hisselerin ağırlık dağılımlarını O-EAGI puanlarına, teknik sinyal (RSI/SMA), haber duyarlılıklarına, seçilen yatırım vadesine, risk profiline ve strateji odağına göre belirle.
                Sepet ağırlığı toplamı %100'dür. Bunun %$cashPct oranını 'KORUMALI NAKİT' rezervi olarak ayır ve kalan %${100.0 - cashPct} ağırlığı seçilen $stockCount hisse arasında dağıt!
                Eğer kullanıcı bütçe belirtmişse, toplam bütçeyi ağırlıklara bölerek her bir hisseden tam olarak kaç adet alması gerektiğini ve hisse başına bütçe payını ($currencyUnit cinsinden) açıkça göster.
                Sepete borsa simsarının vizyonunu yansıtan özgün ve tescilli bir isim ver.
                
                Hisse Verileri, Teknik Göstergeler ve Haber Akışları:
                $companyLines
                
                ÇIKTI FORMATI:
                SEPET ADI: [isim]
                ---ORAKUL KARARLARI---
                [SEMBOL] | AL | [Katman: O-EAGI Puanı: [Puan] – Ağırlık % [Yüzde] – RSI: [Değer] – SMA: [GOLDEN_CROSS/DEATH_CROSS/NÖTR]] | GÜVENİLİRLİK: [0-100]
                ---SON---
                
                Ardından: Sepetin ismi, O-EAGI bazlı ağırlıklandırma felsefesi, bütçe paylaştırma reçetesi, sepetin $marketPhilosophyText, son haberlerin bu sepete etkisi ve simsarın makro ekonomik zafer senaryosunu son derece detaylı ve kapsamlı olarak açıkla.
                
                Son olarak bu sepet için 2 kritik stres senaryosu analiz et:
                ---STRES TESTİ---
                SENARYO: Dolar Kuru %20 Artarsa | ETKİ: [+/-%X.X] | TAVSİYE: [kısa reçete]
                SENARYO: Borsada %10 Düşüş Olursa | ETKİ: [+/-%X.X] | TAVSİYE: [kısa reçete]
                ---STRES SONU---
                """.trimIndent()
            }

            OrakulMode.ASK -> {
                val selectedMarket = _uiState.value.selectedMarket
                val marketName = when (selectedMarket) {
                    "BIST" -> "BIST (Türkiye)"
                    "NASDAQ" -> "NASDAQ / NYSE (ABD)"
                    "Avrupa" -> "Avrupa (FRA / EURONEXT)"
                    else -> "Tüm Piyasalar (BIST, NASDAQ, NYSE, Avrupa)"
                }
                """
                $orakulPersona
                
                Seçili piyasa bağlamı: $marketName
                
                Kullanıcı sana şu soruyu soruyor:
                "$customQuestion"
                
                Mevcut portföy bağlamı:
                $portfolioLines
                
                İzleme listesi ve güncel haber/veriler:
                $companyLines
                
                Bu soruyu bir Wall Street üstadı ve efsanevi borsa simsarı olarak yanıtla:
                - Kaçamak cevaplardan kaçın, kesin ve keskin konuş.
                - Eğer soru bir veya birkaç hisse hakkındaysa, hemen O-EAGI formülünü hesaplayıp puanını açıkla.
                - Cevabında borsa literatüründeki efsane isimlerin yaklaşımlarına ve formülüne atıflar yap.
                - Analizi son derece uzun, kapsamlı ve detaylı tut. En az 6-8 detaylı paragraf halinde açıkla, her argümanı finansal rasyolarla derinlemesine destekle.
                """.trimIndent()
            }

            OrakulMode.KAZI -> "DERİN KAZI MODU: Bu mod arka planda çalışır."
        }
    }

    /**
     * "SEMBOL | AL | Katman: ... – gerekçe | GÜVENİLİRLİK: 85" satırlarını parse eder
     */
    private fun parseDecisions(text: String): List<OrakulDecision> {
        val decisions = mutableListOf<OrakulDecision>()
        val block = text
            .substringAfter("---ORAKUL KARARLARI---", "")
            .substringBefore("---SON---", "")
            .trim()
        if (block.isBlank()) return emptyList()

        block.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("SEPET ADI")) return@forEach
            val parts = trimmed.split("|").map { it.trim() }
            if (parts.size >= 3) {
                val symbol = parts[0].replace("•", "").replace("-", "").trim()
                val decision = when {
                    parts[1].contains("AL") && !parts[1].contains("SAT") -> "AL"
                    parts[1].contains("SAT") -> "SAT"
                    else -> "BEKLE"
                }
                val reasonRaw = parts.getOrElse(2) { "" }
                // "Katman: X – gerekçe" ayrıştırması
                val formulaLayer = if (reasonRaw.startsWith("Katman:")) {
                    reasonRaw.substringAfter("Katman:").substringBefore("–").trim()
                } else ""
                val reason = if (formulaLayer.isNotBlank()) {
                    reasonRaw.substringAfter("–").trim()
                } else reasonRaw
                
                val weightMatch = Regex("Ağırlık\\s*%\\s*(\\d+)").find(reasonRaw)
                val weight = weightMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

                // RSI parsing: "RSI: 65" or "RSI:65"
                val rsiMatch = Regex("RSI:\\s*([0-9]+(?:\\.[0-9]+)?)").find(reasonRaw)
                val rsi = rsiMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 50.0

                // SMA/Cross signal parsing
                val crossSignal = when {
                    reasonRaw.contains("GOLDEN_CROSS", ignoreCase = true) -> "GOLDEN_CROSS"
                    reasonRaw.contains("DEATH_CROSS", ignoreCase = true) -> "DEATH_CROSS"
                    else -> "NÖTR"
                }

                val confidenceRaw = parts.getOrElse(3) { "" }
                val confidence = Regex("\\d+").find(confidenceRaw)?.value?.toIntOrNull() ?: 70
                if (symbol.isNotBlank()) {
                    decisions.add(
                        OrakulDecision(
                            symbol = symbol,
                            decision = decision,
                            reason = reason,
                            confidence = confidence.coerceIn(0, 100),
                            formulaLayer = formulaLayer,
                            weight = weight,
                            rsi = rsi,
                            crossSignal = crossSignal
                        )
                    )
                }
            }
        }
        return decisions
    }

    /**
     * ---STRES TESTİ--- bloğunu parse eder
     * Format: SENARYO: X | ETKİ: +/-Y% | TAVSİYE: Z
     */
    private fun parseStressScenarios(text: String): List<OrakulStressScenario> {
        val scenarios = mutableListOf<OrakulStressScenario>()
        val block = text
            .substringAfter("---STRES TESTİ---", "")
            .substringBefore("---STRES SONU---", "")
            .trim()
        if (block.isBlank()) return emptyList()

        block.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank() || !trimmed.startsWith("SENARYO:", ignoreCase = true)) return@forEach
            val parts = trimmed.split("|").map { it.trim() }
            if (parts.size >= 3) {
                val scenario = parts[0].substringAfter("SENARYO:").trim()
                val impact = parts[1].substringAfter("ETKİ:").trim()
                val advice = parts[2].substringAfter("TAVSİYE:").trim()
                if (scenario.isNotBlank()) {
                    scenarios.add(OrakulStressScenario(scenario, impact, advice))
                }
            }
        }
        return scenarios
    }

    fun saveGeneratedBasket(basketName: String, onCompleted: () -> Unit) {
        val amountStr = _uiState.value.investmentAmount.replace(Regex("[^0-9]"), "")
        val totalBudget = amountStr.toDoubleOrNull() ?: 10000.0
        val decisions = _uiState.value.decisions

        viewModelScope.launch {
            if (decisions.isEmpty()) return@launch

            // 1. Resolve region/market based on first decision symbol
            val firstSymbol = decisions.first().symbol
            val companies = repository.allCompanies.first()
            val company = companies.find { it.symbol.equals(firstSymbol, ignoreCase = true) }
            val marketStr = when (company?.market?.uppercase()) {
                "NASDAQ", "NYSE" -> "NASDAQ"
                "FRA", "EURONEXT" -> "FRA"
                else -> "BIST"
            }

            // 2. Insert basket
            val basket = Basket(name = basketName, market = marketStr)
            val basketId = repository.addBasket(basket).toInt()

            val rates = repository.exchangeRates.value
            val usdRate = rates["USD"] ?: 34.5
            val eurRate = rates["EUR"] ?: 37.2

            val pricesMap = repository.prices.value
            // 3. Insert transactions and items
            decisions.forEach { decision ->
                val companies = repository.allCompanies.first()
                val company = companies.find { it.symbol == decision.symbol }
                val price = pricesMap[decision.symbol]?.price ?: company?.currentPrice ?: 100.0

                // Parse weight from reason (e.g. "Ağırlık % 30")
                val weightMatch = Regex("Ağırlık\\s*%\\s*(\\d+)").find(decision.reason)
                val weightVal = weightMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 20.0

                val allocatedAmount = totalBudget * (weightVal / 100.0)
                // Convert allocatedAmount (in TL) to the stock's local currency before quantity division
                val allocatedAmountLocal = when (company?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> allocatedAmount / usdRate
                    "FRA", "EURONEXT" -> allocatedAmount / eurRate
                    else -> allocatedAmount
                }
                val quantity = if (price > 0) (allocatedAmountLocal / price).toInt().coerceAtLeast(1).toDouble() else 1.0

                repository.executeTransaction(
                    basketId = basketId,
                    symbol = decision.symbol,
                    quantity = quantity,
                    price = price,
                    isBuy = true
                )
            }

            onCompleted()
        }
    }

    fun executeRebalance(onCompleted: () -> Unit) {
        val decisions = _uiState.value.decisions
        viewModelScope.launch {
            val baskets = repository.allBaskets.first()
            val allItems = repository.getAllBasketItemsDirect()

            // 1. Process SAT decisions
            val satSymbols = decisions.filter { it.decision == "SAT" }.map { it.symbol.uppercase() }
            allItems.filter { it.symbol.uppercase() in satSymbols }.forEach { item ->
                repository.executeTransaction(
                    basketId = item.basketId,
                    symbol = item.symbol,
                    quantity = item.quantity,
                    price = item.buyPrice,
                    isBuy = false
                )
            }

            // 2. Process AL decisions
            val alSymbols = decisions.filter { it.decision == "AL" }.map { it.symbol.uppercase() }
            if (baskets.isNotEmpty() && alSymbols.isNotEmpty()) {
                val firstBasketId = baskets.first().id
                val pricesMap = repository.prices.value
                alSymbols.forEach { symbol ->
                    val exists = allItems.any { it.basketId == firstBasketId && it.symbol.uppercase() == symbol }
                    if (!exists) {
                        val companies = repository.allCompanies.first()
                        val company = companies.find { it.symbol.uppercase() == symbol }
                        val price = pricesMap[symbol]?.price ?: company?.currentPrice ?: 100.0
                        val quantity = 10.0 // Default lot
                        repository.executeTransaction(
                            basketId = firstBasketId,
                            symbol = symbol,
                            quantity = quantity,
                            price = price,
                            isBuy = true
                        )
                    }
                }
            }

            onCompleted()
        }
    }

    fun initRebalanceWizard() {
        viewModelScope.launch {
            val baskets = repository.allBaskets.first()
            val selectedId = baskets.firstOrNull()?.id
            _uiState.update {
                it.copy(
                    rebalanceBaskets = baskets,
                    selectedRebalanceBasketId = selectedId
                )
            }
            if (selectedId != null) {
                calculateRebalanceTrades(selectedId)
            }
        }
    }

    fun selectRebalanceBasket(basketId: Int) {
        _uiState.update { it.copy(selectedRebalanceBasketId = basketId) }
        calculateRebalanceTrades(basketId)
    }

    fun calculateRebalanceTrades(basketId: Int) {
        val decisions = _uiState.value.decisions
        if (decisions.isEmpty()) return

        viewModelScope.launch {
            val companies = repository.allCompanies.first()
            val companyMap = companies.associateBy { it.symbol }
            
            val allItems = repository.getAllBasketItemsDirect().filter { it.basketId == basketId }
            val itemMap = allItems.associateBy { it.symbol.uppercase() }

            val rates = repository.exchangeRates.value
            val usdRate = rates["USD"] ?: 34.5
            val eurRate = rates["EUR"] ?: 37.2
            val pricesMap = repository.prices.value
            
            var totalValue = 0.0
            allItems.forEach { item ->
                val company = companyMap[item.symbol]
                val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
                val rate = when (company?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT" -> eurRate
                    else -> 1.0
                }
                totalValue += item.quantity * currentPrice * rate
            }

            if (totalValue <= 0.0) {
                val budgetStr = _uiState.value.investmentAmount.replace(Regex("[^0-9]"), "")
                totalValue = budgetStr.toDoubleOrNull() ?: 10000.0
            }

            val trades = mutableListOf<RebalanceTrade>()

            decisions.forEach { decision ->
                val company = companyMap[decision.symbol]
                val currentPrice = pricesMap[decision.symbol]?.price ?: company?.currentPrice ?: 100.0
                val rate = when (company?.market?.uppercase()) {
                    "NASDAQ", "NYSE" -> usdRate
                    "FRA", "EURONEXT" -> eurRate
                    else -> 1.0
                }

                val existingItem = itemMap[decision.symbol.uppercase()]
                val currentQty = existingItem?.quantity ?: 0.0

                val targetWeight = if (decision.decision == "SAT") 0.0 else decision.weight
                
                val targetValueInCurrency = (totalValue * (targetWeight / 100.0)) / rate
                val targetQtyRaw = if (currentPrice > 0) targetValueInCurrency / currentPrice else 0.0
                val targetQty = kotlin.math.round(targetQtyRaw).coerceAtLeast(0.0)

                val tradeQty = targetQty - currentQty
                val valueDiff = tradeQty * currentPrice * rate

                if (tradeQty != 0.0 || targetWeight > 0.0) {
                    trades.add(RebalanceTrade(
                        symbol = decision.symbol,
                        currentQty = currentQty,
                        currentPrice = currentPrice,
                        targetWeight = targetWeight,
                        targetQty = targetQty,
                        tradeQty = tradeQty,
                        valueDiff = valueDiff,
                        decision = decision.decision
                    ))
                }
            }

            allItems.forEach { item ->
                val sym = item.symbol.uppercase()
                if (decisions.none { it.symbol.uppercase() == sym }) {
                    val company = companyMap[item.symbol]
                    val currentPrice = pricesMap[item.symbol]?.price ?: company?.currentPrice ?: item.buyPrice
                    val rate = when (company?.market?.uppercase()) {
                        "NASDAQ", "NYSE" -> usdRate
                        "FRA", "EURONEXT" -> eurRate
                        else -> 1.0
                    }
                    val tradeQty = -item.quantity
                    val valueDiff = tradeQty * currentPrice * rate

                    trades.add(RebalanceTrade(
                        symbol = item.symbol,
                        currentQty = item.quantity,
                        currentPrice = currentPrice,
                        targetWeight = 0.0,
                        targetQty = 0.0,
                        tradeQty = tradeQty,
                        valueDiff = valueDiff,
                        decision = "SAT"
                    ))
                }
            }

            _uiState.update { it.copy(rebalanceTrades = trades) }
        }
    }

    fun executeRebalanceTrades(basketId: Int, trades: List<RebalanceTrade>, onCompleted: () -> Unit) {
        viewModelScope.launch {
            trades.forEach { trade ->
                if (trade.tradeQty > 0.0) {
                    repository.executeTransaction(
                        basketId = basketId,
                        symbol = trade.symbol,
                        quantity = trade.tradeQty,
                        price = trade.currentPrice,
                        isBuy = true
                    )
                } else if (trade.tradeQty < 0.0) {
                    repository.executeTransaction(
                        basketId = basketId,
                        symbol = trade.symbol,
                        quantity = kotlin.math.abs(trade.tradeQty),
                        price = trade.currentPrice,
                        isBuy = false
                    )
                }
            }
            onCompleted()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
