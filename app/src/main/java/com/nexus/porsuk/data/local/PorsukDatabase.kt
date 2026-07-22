package com.nexus.porsuk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nexus.porsuk.data.local.dao.AssetDao
import com.nexus.porsuk.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Company::class,
        Basket::class,
        BasketItem::class,
        WatchlistItem::class,
        PriceSnapshot::class,
        CachedCompanyInfo::class,
        NewsItemEntity::class,
        PriceAlert::class,
        PortfolioHistoryEntry::class,
        PortfolioTransaction::class,
        StockHistoryEntry::class,
        KaziRun::class,
        KaziCandidate::class,
        KaziBasket::class,
        KaziBasketItem::class,
        KaziWatch::class,
        DividendCalendarEntry::class,
        IpoCalendarEntry::class,
        EconomicEventEntry::class
    ],
    version = 16,
    exportSchema = false
)
abstract class PorsukDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao

    companion object {
        @Volatile
        private var INSTANCE: PorsukDatabase? = null

        fun getDatabase(context: Context): PorsukDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PorsukDatabase::class.java,
                    "porsuk_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : RoomDatabase.Callback() {
                    private fun seedDatabase(database: PorsukDatabase) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = database.assetDao()
                            
                            // 150+ şirketi ExtendedDatabaseSeeder'dan alıp fiyatlandırarak ekleyelim
                            val initialCompanies = com.nexus.porsuk.data.remote.ExtendedDatabaseSeeder.getPopularCompanies().map { company ->
                                val seededPrice = if (company.market == "BIST") {
                                    (40 + (company.symbol.hashCode() % 310)).toDouble()
                                } else {
                                    (50 + (company.symbol.hashCode() % 400)).toDouble()
                                }
                                company.copy(currentPrice = seededPrice)
                            }
                            dao.insertCompanies(initialCompanies)
                            
                            // Şirketlerin F/K, Temettü vb. detay bilgilerini başlangıçta dolduralım
                            initialCompanies.forEach { company ->
                                dao.insertCachedInfo(generateMockCompanyInfo(company))
                            }
                            
                            // Sadece seçili popüler olanları takip listesine (Watchlist) ekle
                            val popularSymbols = setOf(
                                "THYAO", "EREGL", "TUPRS", "ASELS", "KCHOL", "SAHOL", "BIMAS", "SASA",
                                "AAPL", "TSLA", "MSFT", "NVDA", "AMZN", "GOOGL", "COIN",
                                "SAP", "MC.PA"
                            )
                            initialCompanies.filter { it.symbol in popularSymbols }.forEach { company ->
                                dao.insertWatchlistItem(WatchlistItem(company.symbol))
                            }

                            // Ek olarak Dolar, Euro ve BIST100'ü de takip listesine ekle
                            dao.insertWatchlistItem(WatchlistItem("USDTRY"))
                            dao.insertWatchlistItem(WatchlistItem("EURTRY"))
                            dao.insertWatchlistItem(WatchlistItem("XU100"))

                            // Geriye dönük 15 günlük portföy geçmiş verisi ekleme
                            val now = System.currentTimeMillis()
                            val oneDayMs = 24 * 60 * 60 * 1000L
                            val initialHistory = listOf(
                                98000.0, 99200.0, 98500.0, 100400.0, 101200.0, 
                                100800.0, 102100.0, 101900.0, 103500.0, 104200.0, 
                                103800.0, 105400.0, 104900.0, 106200.0, 105800.0
                            )
                            initialHistory.forEachIndexed { idx, valTry ->
                                val entryTime = now - (15 - idx) * oneDayMs
                                dao.insertPortfolioHistoryEntry(
                                    PortfolioHistoryEntry(timestamp = entryTime, totalValue = valTry)
                                )
                            }

                            // 7. Temettü Takvimi Tohum Verisi
                            val exDate1 = now + 4 * oneDayMs
                            val exDate2 = now + 12 * oneDayMs
                            val exDate3 = now + 20 * oneDayMs
                            val exDate4 = now + 30 * oneDayMs
                            val exDate5 = now - 5 * oneDayMs // Geçmiş temettü
                            
                            val mockDividends = listOf(
                                DividendCalendarEntry(symbol = "EREGL", companyName = "Ereğli Demir Çelik", exDividendDate = exDate1, paymentDate = exDate1 + 2 * oneDayMs, rate = 3.25, yieldPercentage = 6.4, market = "BIST"),
                                DividendCalendarEntry(symbol = "TUPRS", companyName = "Tüpraş Rafinerileri", exDividendDate = exDate2, paymentDate = exDate2 + 2 * oneDayMs, rate = 6.80, yieldPercentage = 4.2, market = "BIST"),
                                DividendCalendarEntry(symbol = "KCHOL", companyName = "Koç Holding", exDividendDate = exDate3, paymentDate = exDate3 + 2 * oneDayMs, rate = 8.50, yieldPercentage = 3.5, market = "BIST"),
                                DividendCalendarEntry(symbol = "AAPL", companyName = "Apple Inc.", exDividendDate = exDate2, paymentDate = exDate2 + 10 * oneDayMs, rate = 0.25, yieldPercentage = 0.5, market = "NASDAQ"),
                                DividendCalendarEntry(symbol = "MSFT", companyName = "Microsoft Corp.", exDividendDate = exDate4, paymentDate = exDate4 + 14 * oneDayMs, rate = 0.75, yieldPercentage = 0.7, market = "NASDAQ"),
                                DividendCalendarEntry(symbol = "MC.PA", companyName = "LVMH Moët Hennessy", exDividendDate = exDate3, paymentDate = exDate3 + 8 * oneDayMs, rate = 7.50, yieldPercentage = 1.1, market = "EURONEXT"),
                                DividendCalendarEntry(symbol = "ASELS", companyName = "Aselsan Elektronik", exDividendDate = exDate5, paymentDate = exDate5 + 2 * oneDayMs, rate = 1.10, yieldPercentage = 1.8, market = "BIST")
                            )
                            dao.insertDividends(mockDividends)

                            // 8. Halka Arz Tohum Verisi
                            val mockIpos = listOf(
                                IpoCalendarEntry(
                                    symbol = "ALTNY",
                                    companyName = "Altınay Savunma Teknolojileri A.Ş.",
                                    startDate = now + 2 * oneDayMs,
                                    endDate = now + 4 * oneDayMs,
                                    price = 32.00,
                                    lotQuantity = 58823530L,
                                    distributionMethod = "Eşit Dağıtım",
                                    isCatkatEnabled = true,
                                    broker = "TSKB Yatırım",
                                    status = "UPCOMING"
                                ),
                                IpoCalendarEntry(
                                    symbol = "AGROT",
                                    companyName = "Agrotech Tarım ve Teknoloji A.Ş.",
                                    startDate = now - 1 * oneDayMs,
                                    endDate = now + 1 * oneDayMs,
                                    price = 5.21,
                                    lotQuantity = 300000000L,
                                    distributionMethod = "Eşit Dağıtım",
                                    isCatkatEnabled = true,
                                    broker = "Alnus Yatırım",
                                    status = "ACTIVE"
                                ),
                                IpoCalendarEntry(
                                    symbol = "MEGAP",
                                    companyName = "Mega Polietilen Köpük Sanayi A.Ş.",
                                    startDate = now - 15 * oneDayMs,
                                    endDate = now - 13 * oneDayMs,
                                    price = 12.80,
                                    lotQuantity = 120000000L,
                                    distributionMethod = "Oransal Dağıtım",
                                    isCatkatEnabled = false,
                                    broker = "Metro Yatırım",
                                    status = "COMPLETED"
                                ),
                                IpoCalendarEntry(
                                    symbol = "BINHO",
                                    companyName = "1000 Yatırımlar Holding A.Ş.",
                                    startDate = now + 10 * oneDayMs,
                                    endDate = now + 12 * oneDayMs,
                                    price = 125.00,
                                    lotQuantity = 9500000L,
                                    distributionMethod = "Eşit Dağıtım",
                                    isCatkatEnabled = true,
                                    broker = "A1 Capital",
                                    status = "UPCOMING"
                                )
                            )
                            dao.insertIpos(mockIpos)

                            // 9. Ekonomik Takvim Tohum Verisi
                            val mockEvents = listOf(
                                EconomicEventEntry(
                                    title = "TCMB Para Politikası Kurulu Faiz Kararı",
                                    country = "TR",
                                    date = now + 5 * oneDayMs,
                                    importance = "HIGH",
                                    previousValue = "%50.00",
                                    expectedValue = "%50.00",
                                    actualValue = null,
                                    comment = "Orakul AI: TCMB'nin faizleri sabit tutması bekleniyor. Sıkı para politikası sürdükçe bankacılık ve mevduat faizlerine duyarlı sektörlerde denge arayışı devam eder."
                                ),
                                EconomicEventEntry(
                                    title = "ABD Tüketici Fiyat Endeksi (TÜFE / Enflasyon) Yıllık",
                                    country = "US",
                                    date = now + 8 * oneDayMs,
                                    importance = "HIGH",
                                    previousValue = "%3.1",
                                    expectedValue = "%3.0",
                                    actualValue = null,
                                    comment = "Orakul AI: Enflasyondaki düşüş FED'in faiz indirim döngüsünü destekleyebilir, bu da teknoloji endekslerini (NASDAQ) yukarı taşır."
                                ),
                                EconomicEventEntry(
                                    title = "Türkiye Enflasyon Oranı (TÜFE) Aylık",
                                    country = "TR",
                                    date = now + 15 * oneDayMs,
                                    importance = "MEDIUM",
                                    previousValue = "%2.8",
                                    expectedValue = "%2.4",
                                    actualValue = null,
                                    comment = "Orakul AI: Aylık bazda enflasyonun yavaşlaması iç piyasa talebi ve BIST100 sanayi hisseleri açısından olumlu algılanacaktır."
                                ),
                                EconomicEventEntry(
                                    title = "FED Faiz Oranı Kararı (FOMC)",
                                    country = "US",
                                    date = now - 2 * oneDayMs,
                                    importance = "HIGH",
                                    previousValue = "%5.50",
                                    expectedValue = "%5.25",
                                    actualValue = "%5.25",
                                    comment = "Orakul AI: FED beklentilere paralel 25 baz puan faiz indirdi. Karar küresel risk iştahını ve hisse piyasalarını pozitif etkiledi."
                                )
                            )
                            dao.insertEconomicEvents(mockEvents)
                        }
                    }

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { seedDatabase(it) }
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        INSTANCE?.let { seedDatabase(it) }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun generateMockCompanyInfo(company: Company): CachedCompanyInfo {
            val symbol = company.symbol.uppercase()
            val name = company.name
            val sector = company.sector
            val price = company.currentPrice
            val hash = symbol.hashCode()

            // 1. Hakkında Yazısı
            val customAbout = when (symbol) {
                "THYAO" -> "Türk Hava Yolları, Türkiye'nin ulusal bayrak taşıyıcı havayolu şirketidir. Küresel uçuş ağı ve geniş filosuyla dünyanın en çok ülkesine uçan havayoludur."
                "EREGL" -> "Ereğli Demir ve Çelik Fabrikaları (Erdemir), Türkiye'nin en büyük entegre yassı çelik üreticisidir. Otomotiv, beyaz eşya ve boru imalatı sektörlerine hammadde sağlar."
                "TUPRS" -> "Tüpraş (Türkiye Petrol Rafinerileri), Türkiye'nin en büyük sanayi kuruluşu ve tek petrol rafinerisi işletmecisidir. Akaryakıt ürünleri üretiminde lider konumdadır."
                "ASELS" -> "Aselsan, Türk Silahlı Kuvvetlerini Güçlendirme Vakfı'na bağlı bir savunma sanayii devidir. Haberleşme, radar, elektronik harp ve silah sistemleri geliştirir."
                "KCHOL" -> "Koç Holding, Türkiye'nin ciro, ihracat, istihdam ve piyasa değeri açısından en büyük holdingidir. Enerji, otomotiv, finans ve dayanıklı tüketim sektörlerinde liderdir."
                "SAHOL" -> "Sabancı Holding, Türkiye'nin önde gelen sanayi ve finans gruplarından biridir. Finans, enerji, çimento, perakende ve sanayi sektörlerinde geniş bir portföye sahiptir."
                "BIMAS" -> "BİM Birleşik Mağazalar, Türkiye'nin organize perakende sektöründeki en büyük indirim marketleri zinciridir. Yüksek cirosu ve geniş mağaza ağıyla bilinir."
                "AAPL" -> "Apple Inc., tüketici elektroniği, bilgisayar yazılımı ve çevrimiçi hizmetler alanında uzmanlaşmış Amerikan teknoloji devidir. iPhone ve Mac ürünleriyle dünya lideridir."
                "TSLA" -> "Tesla Inc., elektrikli araçlar, temiz enerji, batarya depolama sistemleri ve yapay zeka yazılımları geliştiren yenilikçi bir teknoloji ve otomotiv şirketidir."
                "MSFT" -> "Microsoft Corporation, Windows işletim sistemi, Office yazılım grubu ve Azure bulut bilişim hizmetleriyle tanınan dünyanın en büyük yazılım firmalarından biridir."
                "NVDA" -> "NVIDIA Corporation, yapay zeka, derin öğrenme, veri merkezleri ve bilgisayar grafikleri için ekran kartları (GPU) ve çipler üreten küresel teknoloji lideridir."
                "GOOGL" -> "Alphabet Inc., Google arama motoru, YouTube, Android işletim sistemi ve yapay zeka teknolojileriyle dünyanın en büyük bilgi ve teknoloji holdingidir."
                "AMZN" -> "Amazon.com Inc., e-ticaret, bulut bilişim (AWS), dijital yayıncılık ve yapay zeka alanında faaliyet gösteren dünyanın en büyük perakende ve bulut şirketidir."
                "SAP" -> "SAP SE, kurumsal kaynak planlama (ERP) yazılımlarıyla tanınan, işletme yönetimi yazılımlarında dünya lideri olan Alman teknoloji şirketidir."
                "MC.PA" -> "LVMH Moët Hennessy Louis Vuitton, lüks tüketim malları sektöründe dünya lideri olan Fransız holdingidir. Moda, saat, takı ve kozmetik alanında faaliyet gösterir."
                else -> {
                    val sectorTr = when (sector.uppercase()) {
                        "TECHNOLOGY", "TEKNOLOJİ" -> "teknoloji ve yazılım geliştirme"
                        "FINANCE", "FİNANS", "BANKACILIK" -> "finans, bankacılık ve yatırım"
                        "ENERGY", "ENERJİ" -> "enerji üretimi, dağıtımı ve yenilenebilir enerji"
                        "HEALTHCARE", "SAĞLIK" -> "sağlık, biyoteknoloji ve ilaç sanayi"
                        "INDUSTRIALS", "SANAYİ" -> "ağır sanayi, makine ve endüstriyel üretim"
                        "RETAIL", "PERAKENDE" -> "perakende ticaret ve mağazacılık"
                        "TRANSPORTATION", "ULAŞIM" -> "ulaşım, lojistik ve havayolu taşımacılığı"
                        else -> "kendi uzmanlık"
                    }
                    "$name, $sectorTr alanında faaliyet gösteren ve borsada işlem gören öncü kuruluşlardan biridir. Sektördeki yenilikçi projeleriyle büyümeye devam etmektedir."
                }
            }

            // 2. Outstanding Shares in Millions (Dolaşımdaki Lot Sayısı)
            val outstandingShares = when (symbol) {
                "AAPL" -> 15400.0
                "MSFT" -> 7430.0
                "NVDA" -> 24600.0
                "GOOGL" -> 12400.0
                "AMZN" -> 10400.0
                "TSLA" -> 3180.0
                "THYAO" -> 1380.0
                "EREGL" -> 3500.0
                "TUPRS" -> 1920.0
                "ASELS" -> 4560.0
                "KCHOL" -> 2536.0
                "SAHOL" -> 2040.0
                "BIMAS" -> 607.0
                "SASA" -> 5320.0
                "GARAN" -> 4200.0
                "AKBNK" -> 5200.0
                "YKBNK" -> 8447.0
                "ISCTR" -> 10000.0
                "SISE" -> 3063.0
                "PGSUS" -> 102.0
                "FROTO" -> 351.0
                "TOASO" -> 500.0
                else -> (50 + (java.lang.Math.abs(hash) % 950)).toDouble()
            }

            val marketCapVal = (price * outstandingShares) / 1000.0
            val suffix = when (company.market?.uppercase()) {
                "BIST" -> "Milyar TL"
                "FRA", "EURONEXT" -> "Milyar EUR"
                else -> "Milyar USD"
            }
            val marketCapStr = if (marketCapVal >= 1000.0 && company.market != "BIST") {
                val unit = if (company.market?.uppercase() == "FRA" || company.market?.uppercase() == "EURONEXT") "Trilyon EUR" else "Trilyon USD"
                String.format(java.util.Locale.US, "%.2f %s", marketCapVal / 1000.0, unit)
            } else {
                String.format(java.util.Locale.US, "%.1f %s", marketCapVal, suffix)
            }

            // 3. F/K ve Temettü oranları
            val peRatio = (50 + (java.lang.Math.abs(hash) % 220)) / 10.0
            val dividendYield = if (java.lang.Math.abs(hash) % 3 == 0) {
                (10 + (java.lang.Math.abs(hash) % 65)) / 10.0
            } else null

            // 4. 52 Haftalık Yüksek/Alçak
            val week52Low = price * (0.65 + (java.lang.Math.abs(hash) % 20) / 100.0)
            val week52High = price * (1.15 + (java.lang.Math.abs(hash) % 35) / 100.0)

            // 5. Sermaye / Toplam Lot
            val volumeStr = if (outstandingShares >= 1000.0) {
                String.format(java.util.Locale.US, "%.2f Milyar Lot", outstandingShares / 1000.0)
            } else {
                String.format(java.util.Locale.US, "%.1f Milyon Lot", outstandingShares)
            }

            return CachedCompanyInfo(
                symbol = symbol,
                about = customAbout,
                peRatio = peRatio,
                marketCap = marketCapStr,
                week52High = week52High,
                week52Low = week52Low,
                dividendYield = dividendYield,
                volume = volumeStr,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
