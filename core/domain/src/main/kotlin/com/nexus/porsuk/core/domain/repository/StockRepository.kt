package com.nexus.porsuk.core.domain.repository

import com.nexus.porsuk.core.domain.db.StockDao
import com.nexus.porsuk.core.domain.db.toEntity
import com.nexus.porsuk.core.domain.entity.CompanyStock
import com.nexus.porsuk.core.domain.entity.TechnicalSignal
import com.nexus.porsuk.core.domain.network.BistStockApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface StockRepository {
    fun getStocks(): Flow<List<CompanyStock>>
    fun getStockBySymbol(symbol: String): CompanyStock?
    fun getSectors(): List<String>
    suspend fun refreshLiveQuotes(): Result<Unit>
    suspend fun toggleFavorite(symbol: String)
}

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val bistApiService: BistStockApiService,
    private val stockDao: StockDao
) : StockRepository {

    private val inMemoryStocks = MutableStateFlow(getBist100Stocks())

    override fun getStocks(): Flow<List<CompanyStock>> {
        return stockDao.getAllStocks().map { entities ->
            if (entities.isEmpty()) {
                val initial = getBist100Stocks()
                stockDao.insertStocks(initial.map { it.toEntity() })
                initial
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override fun getStockBySymbol(symbol: String): CompanyStock? {
        return inMemoryStocks.value.find { it.symbol.equals(symbol, ignoreCase = true) }
    }

    override fun getSectors(): List<String> {
        return listOf("Tümü", "Ulaştırma", "Bankacılık", "Holding", "Teknoloji", "Perakende", "Otomotiv", "Enerji", "Sanayi", "Madencilik")
    }

    override suspend fun toggleFavorite(symbol: String) {
        val current = stockDao.getStockBySymbol(symbol)
        if (current != null) {
            stockDao.updateFavoriteStatus(symbol, !current.isFavorite)
        }
    }

    override suspend fun refreshLiveQuotes(): Result<Unit> {
        val targetSymbols = inMemoryStocks.value.map { it.symbol }
        val result = bistApiService.fetchLiveBistQuotes(targetSymbols)
        return result.fold(
            onSuccess = { liveQuotes ->
                if (liveQuotes.isNotEmpty()) {
                    liveQuotes.forEach { (symbol, price) ->
                        stockDao.updateStockPrice(symbol, price)
                    }
                    val updated = inMemoryStocks.value.map { stock ->
                        val livePrice = liveQuotes[stock.symbol]
                        if (livePrice != null && livePrice > 0) {
                            stock.copy(price = livePrice)
                        } else {
                            stock
                        }
                    }
                    inMemoryStocks.value = updated
                }
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    private fun getBist100Stocks(): List<CompanyStock> {
        return listOf(
            CompanyStock(
                id = "THYAO",
                symbol = "THYAO",
                name = "Türk Hava Yolları",
                price = 318.50,
                changePercentage = 3.25,
                volume = 9450.2,
                peRatio = 4.85,
                pbRatio = 1.12,
                rsi = 64.5,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Ulaştırma",
                marketCap = "439.5 Mlyr TL",
                high52w = 345.00,
                low52w = 210.40,
                supportPrice = 308.00,
                resistancePrice = 330.00,
                aiRatingScore = 92,
                aiSummary = "Güçlü yolcu doluluk oranları, karlı kargo operasyonları ve düşük F/K çarpanı ile güçlü AL potansiyeli koruyor.",
                roe = 34.2
            ),
            CompanyStock(
                id = "GARAN",
                symbol = "GARAN",
                name = "Garanti BBVA",
                price = 126.40,
                changePercentage = 1.85,
                volume = 6120.0,
                peRatio = 3.95,
                pbRatio = 1.25,
                rsi = 58.2,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Bankacılık",
                marketCap = "530.8 Mlyr TL",
                high52w = 138.00,
                low52w = 68.50,
                supportPrice = 122.50,
                resistancePrice = 131.00,
                aiRatingScore = 88,
                aiSummary = "Net faiz marjındaki toparlanma ve yüksek sermaye yeterlilik oranı bankacılık sektöründe öne çıkarıyor.",
                roe = 38.6
            ),
            CompanyStock(
                id = "ASELS",
                symbol = "ASELS",
                name = "Aselsan Elektronik",
                price = 74.80,
                changePercentage = 4.10,
                volume = 5890.5,
                peRatio = 12.40,
                pbRatio = 3.45,
                rsi = 71.8,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Teknoloji",
                marketCap = "341.0 Mlyr TL",
                high52w = 78.50,
                low52w = 39.20,
                supportPrice = 71.00,
                resistancePrice = 77.50,
                aiRatingScore = 95,
                aiSummary = "Rekor bakiye siparişler ve ihracat odaklı büyüme ile teknik ve temel göstergeler çok güçlü.",
                roe = 29.8
            ),
            CompanyStock(
                id = "EREGL",
                symbol = "EREGL",
                name = "Eğreyli Demir Çelik",
                price = 56.25,
                changePercentage = -0.80,
                volume = 4120.3,
                peRatio = 11.20,
                pbRatio = 1.35,
                rsi = 46.2,
                technicalSignal = TechnicalSignal.NEUTRAL,
                sector = "Sanayi",
                marketCap = "196.8 Mlyr TL",
                high52w = 62.10,
                low52w = 38.90,
                supportPrice = 54.50,
                resistancePrice = 59.00,
                aiRatingScore = 72,
                aiSummary = "Küresel çelik marjlarındaki dalgalanma kısa vadede baskı yaratsa da orta vadeli yatırımlar olumlu.",
                roe = 14.5
            ),
            CompanyStock(
                id = "KCHOL",
                symbol = "KCHOL",
                name = "Koç Holding",
                price = 232.00,
                changePercentage = 2.40,
                volume = 7340.0,
                peRatio = 5.20,
                pbRatio = 1.48,
                rsi = 61.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Holding",
                marketCap = "588.3 Mlyr TL",
                high52w = 254.00,
                low52w = 135.00,
                supportPrice = 225.00,
                resistancePrice = 242.00,
                aiRatingScore = 90,
                aiSummary = "Çeşitlendirilmiş iştirak portföyü ve net aktif değerine göre iskonto oranı cazip seviyelerde.",
                roe = 31.0
            ),
            CompanyStock(
                id = "AKBNK",
                symbol = "AKBNK",
                name = "Akbank",
                price = 65.75,
                changePercentage = 1.15,
                volume = 4890.0,
                peRatio = 3.70,
                pbRatio = 1.18,
                rsi = 55.4,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Bankacılık",
                marketCap = "341.9 Mlyr TL",
                high52w = 72.40,
                low52w = 34.10,
                supportPrice = 63.80,
                resistancePrice = 68.50,
                aiRatingScore = 86,
                aiSummary = "Mevduat maliyetlerindeki normalleşme ile aktif kalitesi yüksek performans gösteriyor.",
                roe = 36.1
            ),
            CompanyStock(
                id = "SISE",
                symbol = "SISE",
                name = "Şişecam",
                price = 49.90,
                changePercentage = -1.20,
                volume = 3250.0,
                peRatio = 9.80,
                pbRatio = 1.42,
                rsi = 42.1,
                technicalSignal = TechnicalSignal.NEUTRAL,
                sector = "Sanayi",
                marketCap = "152.8 Mlyr TL",
                high52w = 57.80,
                low52w = 42.00,
                supportPrice = 48.20,
                resistancePrice = 52.50,
                aiRatingScore = 75,
                aiSummary = "Enerji maliyetleri ve küresel talep yavaşlaması dip seviyelerde yatay seyir oluşturuyor.",
                roe = 16.8
            ),
            CompanyStock(
                id = "TUPRS",
                symbol = "TUPRS",
                name = "Tüpraş",
                price = 184.20,
                changePercentage = 2.90,
                volume = 8120.0,
                peRatio = 6.10,
                pbRatio = 2.15,
                rsi = 66.8,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Enerji",
                marketCap = "354.8 Mlyr TL",
                high52w = 208.00,
                low52w = 124.00,
                supportPrice = 178.00,
                resistancePrice = 192.00,
                aiRatingScore = 93,
                aiSummary = "Dizel ve jet yakıtı ürün marjlarındaki yükseliş ile güçlü temettü verimi potansiyeli sunuyor.",
                roe = 42.0
            ),
            CompanyStock(
                id = "SAHOL",
                symbol = "SAHOL",
                name = "Sabancı Holding",
                price = 104.50,
                changePercentage = 1.95,
                volume = 4310.0,
                peRatio = 4.60,
                pbRatio = 1.15,
                rsi = 59.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Holding",
                marketCap = "213.2 Mlyr TL",
                high52w = 115.00,
                low52w = 58.00,
                supportPrice = 101.00,
                resistancePrice = 109.00,
                aiRatingScore = 87,
                aiSummary = "Yenilenebilir enerji yatırımları ve dijital dönüşüm odağı ile yüksek iskonto avantajı sürmektedir.",
                roe = 28.5
            ),
            CompanyStock(
                id = "BIMAS",
                symbol = "BIMAS",
                name = "BİM Birleşik Mağazalar",
                price = 585.00,
                changePercentage = 3.70,
                volume = 6980.0,
                peRatio = 14.80,
                pbRatio = 4.20,
                rsi = 69.4,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Perakende",
                marketCap = "355.2 Mlyr TL",
                high52w = 612.00,
                low52w = 295.00,
                supportPrice = 565.00,
                resistancePrice = 600.00,
                aiRatingScore = 94,
                aiSummary = "Defansif yapısı ve yüksek nakit akışı sayesinde enflasyonist ortamda en güçlü perakende tercihi.",
                roe = 35.4
            ),
            CompanyStock(
                id = "YKBNK",
                symbol = "YKBNK",
                name = "Yapı Kredi Bankası",
                price = 34.80,
                changePercentage = 0.90,
                volume = 4150.0,
                peRatio = 3.65,
                pbRatio = 1.14,
                rsi = 54.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Bankacılık",
                marketCap = "293.9 Mlyr TL",
                high52w = 39.50,
                low52w = 17.80,
                supportPrice = 33.50,
                resistancePrice = 36.80,
                aiRatingScore = 85,
                aiSummary = "Komisyon gelirlerindeki artış ve dijital müşteri kazanımı karlılığı destekliyor.",
                roe = 34.8
            ),
            CompanyStock(
                id = "TCELL",
                symbol = "TCELL",
                name = "Turkcell",
                price = 108.30,
                changePercentage = 2.10,
                volume = 3840.0,
                peRatio = 8.90,
                pbRatio = 2.30,
                rsi = 62.8,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Teknoloji",
                marketCap = "238.2 Mlyr TL",
                high52w = 118.00,
                low52w = 54.00,
                supportPrice = 104.00,
                resistancePrice = 112.50,
                aiRatingScore = 89,
                aiSummary = "ARPU (Kullanıcı Başı Ortalama Gelir) büyümesi ve veri merkezi yatırımları ile stabil görünüm.",
                roe = 27.4
            ),
            CompanyStock(
                id = "FROTO",
                symbol = "FROTO",
                name = "Ford Otosan",
                price = 1140.00,
                changePercentage = 1.60,
                volume = 5120.0,
                peRatio = 9.40,
                pbRatio = 5.80,
                rsi = 57.9,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Otomotiv",
                marketCap = "400.0 Mlyr TL",
                high52w = 1280.00,
                low52w = 780.00,
                supportPrice = 1110.00,
                resistancePrice = 1190.00,
                aiRatingScore = 91,
                aiSummary = "Romanya fabrikası entegrasyonu ve elektrikli ticari araç liderliği ile ihracat şampiyonu.",
                roe = 62.0
            ),
            CompanyStock(
                id = "TOASO",
                symbol = "TOASO",
                name = "Tofaş Oto. Fab.",
                price = 285.50,
                changePercentage = -0.70,
                volume = 2450.0,
                peRatio = 8.10,
                pbRatio = 3.90,
                rsi = 48.5,
                technicalSignal = TechnicalSignal.NEUTRAL,
                sector = "Otomotiv",
                marketCap = "142.7 Mlyr TL",
                high52w = 340.00,
                low52w = 210.00,
                supportPrice = 278.00,
                resistancePrice = 298.00,
                aiRatingScore = 78,
                aiSummary = "Stellantis satın alım süreci netleştikçe yeni model üretim kararları hisseye ivme kazandırabilir.",
                roe = 48.2
            ),
            CompanyStock(
                id = "KOZAL",
                symbol = "KOZAL",
                name = "Koza Altın",
                price = 24.60,
                changePercentage = 4.80,
                volume = 3890.0,
                peRatio = 16.50,
                pbRatio = 2.80,
                rsi = 73.2,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Madencilik",
                marketCap = "78.7 Mlyr TL",
                high52w = 31.20,
                low52w = 18.50,
                supportPrice = 23.20,
                resistancePrice = 25.80,
                aiRatingScore = 84,
                aiSummary = "Ons altın fiyatlarındaki rekor seviyeler madencilik karlılığını doğrudan yukarı taşıyor.",
                roe = 18.9
            ),
            CompanyStock(
                id = "MGROS",
                symbol = "MGROS",
                name = "Migros Ticaret",
                price = 542.00,
                changePercentage = 2.85,
                volume = 3120.0,
                peRatio = 11.40,
                pbRatio = 3.85,
                rsi = 65.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Perakende",
                marketCap = "98.1 Mlyr TL",
                high52w = 580.00,
                low52w = 320.00,
                supportPrice = 520.00,
                resistancePrice = 560.00,
                aiRatingScore = 91,
                aiSummary = "Online sipariş kanalları (Migros Hemen) ve hızlı mağaza açılışları ile pazar payı genişliyor.",
                roe = 38.0
            ),
            CompanyStock(
                id = "ENKAI",
                symbol = "ENKAI",
                name = "Enka İnşaat",
                price = 48.20,
                changePercentage = 1.40,
                volume = 2100.0,
                peRatio = 9.10,
                pbRatio = 1.10,
                rsi = 56.8,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Sanayi",
                marketCap = "289.2 Mlyr TL",
                high52w = 52.00,
                low52w = 31.50,
                supportPrice = 46.50,
                resistancePrice = 50.00,
                aiRatingScore = 86,
                aiSummary = "Döviz bazlı güçlü nakit pozisyonu ve uluslararası müteahhitlik portföyü ile güvenli liman.",
                roe = 13.2
            ),
            CompanyStock(
                id = "PETKM",
                symbol = "PETKM",
                name = "Petkim",
                price = 21.40,
                changePercentage = -1.80,
                volume = 1950.0,
                peRatio = 14.20,
                pbRatio = 1.65,
                rsi = 38.4,
                technicalSignal = TechnicalSignal.SELL,
                sector = "Sanayi",
                marketCap = "54.2 Mlyr TL",
                high52w = 26.50,
                low52w = 17.20,
                supportPrice = 20.80,
                resistancePrice = 22.60,
                aiRatingScore = 62,
                aiSummary = "Etilen-Nafta marjlarındaki zayıflık kısa vadeli kar baskısını artırıyor.",
                roe = 11.4
            ),
            CompanyStock(
                id = "ASTOR",
                symbol = "ASTOR",
                name = "Astor Enerji",
                price = 118.90,
                changePercentage = 5.20,
                volume = 7890.0,
                peRatio = 18.20,
                pbRatio = 6.40,
                rsi = 74.5,
                technicalSignal = TechnicalSignal.STRONG_BUY,
                sector = "Enerji",
                marketCap = "118.7 Mlyr TL",
                high52w = 142.00,
                low52w = 82.00,
                supportPrice = 112.00,
                resistancePrice = 125.00,
                aiRatingScore = 93,
                aiSummary = "Transformatör ve trafo ihracatındaki patlama ile küresel yeşil dönüşümün en hızlı büyüyen oyuncusu.",
                roe = 44.5
            ),
            CompanyStock(
                id = "KONTR",
                symbol = "KONTR",
                name = "Kontrolmatik Teknoloji",
                price = 62.40,
                changePercentage = -2.10,
                volume = 3400.0,
                peRatio = 24.50,
                pbRatio = 7.10,
                rsi = 36.2,
                technicalSignal = TechnicalSignal.SELL,
                sector = "Teknoloji",
                marketCap = "40.5 Mlyr TL",
                high52w = 105.00,
                low52w = 58.00,
                supportPrice = 60.00,
                resistancePrice = 68.00,
                aiRatingScore = 68,
                aiSummary = "Yüksek borçluluk ve çarpanlar fiyatlama üzerinde baskı oluştursa da batarya fabrikası yatırımı takip edilmeli.",
                roe = 28.0
            ),
            CompanyStock(
                id = "SASA",
                symbol = "SASA",
                name = "Sasa Polyester",
                price = 4.85,
                changePercentage = -0.40,
                volume = 2800.0,
                peRatio = 28.00,
                pbRatio = 3.90,
                rsi = 41.0,
                technicalSignal = TechnicalSignal.NEUTRAL,
                sector = "Sanayi",
                marketCap = "210.0 Mlyr TL",
                high52w = 7.20,
                low52w = 4.10,
                supportPrice = 4.60,
                resistancePrice = 5.20,
                aiRatingScore = 69,
                aiSummary = "PTA tesisi yatırımlarının tamamlanması orta vadeli potansiyel sunuyor ancak finansman giderleri yüksek.",
                roe = 14.0
            ),
            CompanyStock(
                id = "HEKTAS",
                symbol = "HEKTAS",
                name = "Hektaş",
                price = 14.20,
                changePercentage = -1.10,
                volume = 1200.0,
                peRatio = 32.00,
                pbRatio = 4.80,
                rsi = 39.5,
                technicalSignal = TechnicalSignal.SELL,
                sector = "Sanayi",
                marketCap = "35.9 Mlyr TL",
                high52w = 28.00,
                low52w = 13.00,
                supportPrice = 13.80,
                resistancePrice = 15.40,
                aiRatingScore = 60,
                aiSummary = "Tarım sektöründeki kuraklık ve girdi maliyetleri nedeniyle faaliyet kar marjları zayıf kalıyor.",
                roe = 8.5
            ),
            CompanyStock(
                id = "PGSUS",
                symbol = "PGSUS",
                name = "Pegasus Hava Yolları",
                price = 242.00,
                changePercentage = 3.10,
                volume = 4890.0,
                peRatio = 6.80,
                pbRatio = 2.10,
                rsi = 63.2,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Ulaştırma",
                marketCap = "121.0 Mlyr TL",
                high52w = 265.00,
                low52w = 160.00,
                supportPrice = 232.00,
                resistancePrice = 252.00,
                aiRatingScore = 90,
                aiSummary = "Düşük maliyetli taşıyıcı modeli ve filo gençleştirme stratejisi karlılığı destekliyor.",
                roe = 36.4
            ),
            CompanyStock(
                id = "EKGYO",
                symbol = "EKGYO",
                name = "Emlak Konut GYO",
                price = 11.85,
                changePercentage = 2.60,
                volume = 3100.0,
                peRatio = 5.90,
                pbRatio = 0.88,
                rsi = 60.1,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Holding",
                marketCap = "45.0 Mlyr TL",
                high52w = 13.50,
                low52w = 6.80,
                supportPrice = 11.20,
                resistancePrice = 12.40,
                aiRatingScore = 83,
                aiSummary = "Net aktif değerine göre yüksek iskonto (%12 PD/DD altı) gayrimenkul sektöründe fırsat sunuyor.",
                roe = 19.2
            ),
            CompanyStock(
                id = "ALARK",
                symbol = "ALARK",
                name = "Alarko Holding",
                price = 112.40,
                changePercentage = 1.70,
                volume = 1980.0,
                peRatio = 4.20,
                pbRatio = 1.30,
                rsi = 56.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Holding",
                marketCap = "48.9 Mlyr TL",
                high52w = 135.00,
                low52w = 85.00,
                supportPrice = 108.00,
                resistancePrice = 118.00,
                aiRatingScore = 88,
                aiSummary = "Tarım ve seracılık yatırımları ile elektrik dağıtım gelirleri güçlü nakit yaratıyor.",
                roe = 33.1
            ),
            CompanyStock(
                id = "ISCTR",
                symbol = "ISCTR",
                name = "İş Bankası (C)",
                price = 14.90,
                changePercentage = 1.45,
                volume = 5120.0,
                peRatio = 3.80,
                pbRatio = 1.12,
                rsi = 57.0,
                technicalSignal = TechnicalSignal.BUY,
                sector = "Bankacılık",
                marketCap = "372.5 Mlyr TL",
                high52w = 17.20,
                low52w = 8.50,
                supportPrice = 14.30,
                resistancePrice = 15.60,
                aiRatingScore = 87,
                aiSummary = "Holding yapısına dönüşüm çalışmaları ve güçlü borsa iştirak portföyü değeri artırıyor.",
                roe = 33.5
            )
        )
    }
}
