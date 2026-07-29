package com.nexus.porsuk.data.remote

data class RichCompanyDetails(
    val about: String,
    val peRatio: String,
    val marketCap: String,
    val week52High: String,
    val week52Low: String,
    val dividendYield: String,
    val volume: String,
    val news: List<com.nexus.porsuk.data.local.entity.NewsItemEntity> = emptyList()
)

object RichOfflineDataEngine {
    
    // Gerçekçi Döviz baseline kurları (Live data gelmezse fallback)
    const val BASE_USD_TRY = 34.15
    const val BASE_EUR_TRY = 36.42
    const val BASE_XU100 = 10450.20

    fun getRichDetailsFor(symbol: String, name: String = "", price: Double = 0.0, market: String = "BIST"): RichCompanyDetails {
        val sym = symbol.uppercase()
        val hash = sym.hashCode()

        // 1. Hakkında Yazısı
        val customAbout = when (sym) {
            // BIST
            "THYAO" -> "Türk Hava Yolları, Türkiye'nin ulusal bayrak taşıyıcı havayolu şirketidir. Küresel uçuş ağı ve geniş filosuyla dünyanın en çok ülkesine uçan havayoludur."
            "EREGL" -> "Ereğli Demir ve Çelik Fabrikaları (Erdemir), Türkiye'nin en büyük entegre yassı çelik üreticisidir. Otomotiv, beyaz eşya ve boru imalatı sektörlerine hammadde sağlar."
            "TUPRS" -> "Tüpraş (Türkiye Petrol Rafinerileri), Türkiye'nin en büyük sanayi kuruluşu ve tek petrol rafinerisi işletmecisidir. Akaryakıt ürünleri üretiminde lider konumdadır."
            "ASELS" -> "Aselsan, Türk Silahlı Kuvvetlerini Güçlendirme Vakfı'na bağlı bir savunma sanayii devidir. Haberleşme, radar, elektronik harp ve silah sistemleri geliştirir."
            "KCHOL" -> "Koç Holding, Türkiye'nin ciro, ihracat, istihdam ve piyasa değeri açısından en büyük holdingidir. Enerji, otomotiv, finans ve dayanıklı tüketim sektörlerinde liderdir."
            "SAHOL" -> "Sabancı Holding, Türkiye'nin önde gelen sanayi ve finans gruplarından biridir. Finans, enerji, çimento, perakende ve sanayi sektörlerinde geniş bir portföye sahiptir."
            "BIMAS" -> "BİM Birleşik Mağazalar, Türkiye'nin organize perakende sektöründeki en büyük indirim marketleri zinciridir. Yüksek cirosu ve geniş mağaza ağıyla bilinir."
            "GARAN" -> "Garanti BBVA, Türkiye'nin köklü özel bankalarından biridir. İspanyol BBVA ile ortak yönetim altında faaliyet göstermekte olup bireysel ve kurumsal bankacılıkta lider konumdadır."
            "AKBNK" -> "Akbank, 1948 yılında kurulan ve Sabancı Holding bünyesinde yer alan güçlü Türk özel bankalarından biridir. Dijital bankacılık ve servet yönetiminde öncü rol üstlenmektedir."
            "TCELL" -> "Turkcell, Türkiye'nin en büyük mobil telekomünikasyon operatörüdür. Milyonlarca bireysel ve kurumsal aboneye mobil, fiber ve dijital hizmetler sunmaktadır."
            // ABD Teknoloji
            "AAPL" -> "Apple Inc., tüketici elektroniği, bilgisayar yazılımı ve çevrimiçi hizmetler alanında uzmanlaşmış Amerikan teknoloji devidir. iPhone, Mac, iPad ve Apple Watch ürünleriyle dünya lideridir. App Store, iCloud ve Apple Music gibi servislerle yüksek marjlı bir ekosistem yaratmıştır."
            "MSFT" -> "Microsoft Corporation, Windows işletim sistemi, Office yazılım grubu ve Azure bulut bilişim hizmetleriyle tanınan dünyanın en büyük yazılım firmalarından biridir. LinkedIn, GitHub ve Activision Blizzard'ı bünyesinde barındırmaktadır."
            "GOOGL" -> "Alphabet Inc., Google arama motoru, YouTube, Android işletim sistemi ve yapay zeka teknolojileriyle dünyanın en büyük bilgi ve teknoloji holdingidir. Google Cloud ve Waymo gibi yatırımlarıyla geleceği şekillendirmektedir."
            "AMZN" -> "Amazon.com Inc., e-ticaret, bulut bilişim (AWS), dijital yayıncılık ve yapay zeka alanında faaliyet gösteren dünyanın en büyük perakende ve bulut şirketidir. AWS, global bulut altyapısının lider sağlayıcısıdır."
            "NVDA" -> "NVIDIA Corporation, yapay zeka, derin öğrenme, veri merkezleri ve bilgisayar grafikleri için ekran kartları (GPU) ve çipler üreten küresel teknoloji lideridir. CUDA mimarisi ile yapay zeka devrimin kalbinde yer almaktadır."
            "META" -> "Meta Platforms (Facebook), Instagram, WhatsApp ve Threads gibi dev sosyal platformları işleten dijital medya ve reklam devi. Metaverse ve yapay zeka altyapısına ciddi yatırımlar yapmaktadır."
            "TSLA" -> "Tesla Inc., elektrikli araçlar, temiz enerji, batarya depolama sistemleri ve yapay zeka yazılımları geliştiren yenilikçi bir teknoloji ve otomotiv şirketidir. Model S, X, 3 ve Y ile EV pazarını dönüştürmüştür."
            "AVGO" -> "Broadcom Inc., ağ iletişimi, depolama, bağlantı ve endüstriyel çözümler için yarı iletkenler üreten küresel bir teknoloji devi. VMware'i satın alarak enterprise yazılım alanına da girmiştir."
            "NOW" -> "ServiceNow, kurumsal iş akışlarını ve BT hizmet yönetimini dijitalleştiren bulut tabanlı platform şirketidir. Fortune 500 şirketlerine AI-destekli otomasyon çözümleri sunar."
            "INTU" -> "Intuit, TurboTax, QuickBooks ve Mint gibi finansal yazılım ürünleriyle küçük işletmelere ve bireylere vergi ve muhasebe çözümleri sunan önde gelen Amerikan fintech şirketidir."
            "PANW" -> "Palo Alto Networks, kurumsal siber güvenlik alanında dünya lideri olan Amerikan teknoloji şirketidir. Güvenlik duvarları, bulut güvenliği ve yapay zeka destekli tehdit önleme sistemleri sunar."
            "CRWD" -> "CrowdStrike, uç nokta güvenliği ve tehdit istihbaratı alanında faaliyet gösteren yapay zeka odaklı siber güvenlik şirketidir. Falcon platformu, bulut tabanlı koruma için endüstri standardı haline gelmiştir."
            "ORCL" -> "Oracle Corporation, kurumsal veritabanı sistemleri ve bulut uygulamaları konusunda dünya lideridir. Oracle Cloud Infrastructure ve ERP yazılımları ile büyük şirketlere kritik altyapı sağlar."
            "ADBE" -> "Adobe Inc., Photoshop, Illustrator, Premiere Pro ve Acrobat gibi yaratıcı yazılımlarıyla dünya genelinde milyonlarca kreatif profesyonelin tercihi olan yazılım devidir. Adobe Creative Cloud abonelik modeliyle güçlü tekrarlayan gelir yaratmaktadır."
            "QCOM" -> "Qualcomm, akıllı telefon işlemcileri ve 5G modem teknolojisinde dünya liderlerinden biridir. Snapdragon çipleri, dünyanın önde gelen Android cihazlarında kullanılmaktadır."
            "TXN" -> "Texas Instruments, analog ve gömülü yarı iletkenler üretiminde küresel lider olup endüstriyel, otomotiv ve tüketici elektroniği sektörlerine çözümler sunmaktadır."
            // ABD Finans
            "JPM" -> "JPMorgan Chase, varlık büyüklüğü açısından ABD'nin ve dünyanın en büyük bankalarından biridir. Yatırım bankacılığı, tüketici bankacılığı ve servet yönetiminde lider konumdadır."
            "BAC" -> "Bank of America, ABD'nin en büyük tüketici bankalarından biridir. Merrill Lynch ile birleşerek servet yönetimi ve yatırım bankacılığında küresel güç haline gelmiştir."
            "GS" -> "Goldman Sachs, yatırım bankacılığı, menkul kıymet ve varlık yönetimi alanında dünyanın en prestijli finansal kuruluşlarından biridir."
            "MS" -> "Morgan Stanley, küresel yatırım bankacılığı ve servet yönetimi hizmetleri sunan ABD'nin köklü finans kuruluşlarından biridir. E*Trade'i bünyesine katarak bireysel yatırımcı tabanını genişletmiştir."
            "BLK" -> "BlackRock, iShares ETF platformu ve Aladdin risk yönetim sistemi ile dünyanın en büyük varlık yöneticisidir. Küresel portföylerde 10 trilyon dolar üzerinde varlık yönetmektedir."
            "V" -> "Visa Inc., dünya genelinde 200'den fazla ülkede faaliyet gösteren küresel dijital ödeme ağının lideridir. Her yıl milyarlarca işlemi güvenli şekilde gerçekleştirmektedir."
            "MA" -> "Mastercard, Visa ile birlikte global ödeme altyapısının iki büyük oyuncusundan biridir. Cybersource ve NuData gibi yenilikçi satın almalarla fintech alanında güçlenmektedir."
            // ABD Sağlık
            "JNJ" -> "Johnson & Johnson, ilaç, tıbbi cihaz ve tüketici sağlığı ürünleri alanında faaliyet gösteren dünyanın en büyük sağlık şirketlerinden biridir. 135 yılı aşan tarihiyle ABD ikonlarından biridir."
            "UNH" -> "UnitedHealth Group, ABD'nin en büyük sağlık sigortası şirketidir. Optum sağlık hizmetleri kolu ile pazar değeri açısından ABD'nin en büyük şirketleri arasında yer almaktadır."
            "LLY" -> "Eli Lilly, diyabet ve obezite tedavisi için geliştirdiği Tirzepatide (Mounjaro/Zepbound) ile pazar değerini hızla artırmıştır. Onkoloji ve nöroloji alanlarında da güçlü bir ar-ge portföyüne sahiptir."
            "PFE" -> "Pfizer, COVID-19 aşısı ve antiviral Paxlovid ile küresel çapta tanınan dev ilaç şirketidir. Onkoloji, immünoloji ve enfeksiyon hastalıkları alanlarında güçlü bir portföye sahiptir."
            "MRNA" -> "Moderna, mRNA teknolojisini kullanarak COVID-19 aşısı geliştiren ve bunu ticari başarıya dönüştüren yenilikçi Amerikan biyoteknoloji şirketidir. Kanser aşıları ve grip tedavileri üzerinde çalışmaktadır."
            // ABD Enerji
            "XOM" -> "ExxonMobil, dünyanın en büyük entegre enerji şirketlerinden biridir. Ham petrol, doğalgaz üretimi ve petrokimya alanlarında 140 yılı aşkın geçmişiyle küresel enerji liderliğini sürdürmektedir."
            "CVX" -> "Chevron, ABD'nin en büyük entegre enerji şirketlerinden biridir. Dünya genelinde ham petrol ve doğalgaz üretimi, rafine petrol ürünleri ve petrokimyasallar alanlarında faaliyet göstermektedir."
            "NEE" -> "NextEra Energy, ABD'nin en büyük yenilenebilir enerji üreticisidir. Rüzgar ve güneş enerjisi santrallerindeki kapasitesiyle temiz enerji dönüşümünde sektör lideri konumundadır."
            // ABD Savunma
            "LMT" -> "Lockheed Martin, F-35 savaş uçağı, uzay sistemleri ve füze savunma sistemleriyle dünyanın en büyük savunma sanayii şirketidir. ABD ordusunun en kritik tedarikçisi konumundadır."
            "RTX" -> "RTX Corporation (eski Raytheon Technologies), havacılık ve savunma alanlarında motor, silah sistemi ve elektronik çözümler üreten küresel bir savunma devi. Pratt & Whitney motor markasını bünyesinde barındırır."
            "BA" -> "Boeing, ticari havacılık, savunma sistemleri ve uzay araçları alanında faaliyet gösteren ABD'nin simge havacılık şirketidir. 737 MAX krizini aşmaya çalışırken yeni uzay ve savunma kontratlarıyla ayakta kalmaktadır."
            // ABD Sanayi
            "CAT" -> "Caterpillar, inşaat ve madencilik ekipmanları, dizel motorlar ve endüstriyel türbinler alanında dünyanın lider üreticisidir. Küresel altyapı projelerindeki talepten büyük ölçüde yararlanmaktadır."
            "GE" -> "GE Aerospace, jet motoru ve uçak motorları üretiminde dünya liderlerinden biridir. Havacılık sektörünün toparlanmasıyla birlikte güçlü bir büyüme trendine girmiştir."
            // ABD Tüketim
            "WMT" -> "Walmart, dünyanın en büyük perakende zinciri ve istihdamcısı olup ABD'den Çin'e 10.000'den fazla mağazasıyla küresel perakendenin simgesidir. Walmart+ abonelik servisi ile dijital dönüşümünü hızlandırmaktadır."
            "COST" -> "Costco Wholesale, üyelik sistemiyle çalışan dev toptan satış perakendecisidir. Yüksek üye memnuniyeti ve güçlü tekrarlayan gelir modeliyle dünyanın en değerli perakendecilerinden biri haline gelmiştir."
            "KO" -> "Coca-Cola, dünyanın en değerli içecek markasıdır. 200'den fazla ülkede 500'ü aşkın marka ve 4.000'den fazla içecek seçeneğiyle küresel pazarın tartışmasız liderliğini sürdürmektedir."
            "PEP" -> "PepsiCo, meşrubat ve gıda alanında faaliyet gösteren dev şirkettir. Pepsi, Lay's, Gatorade, Quaker ve Tropicana markaları ile küresel gıda ve içecek pazarında lider konumdadır."
            "MCD" -> "McDonald's, dünyanın en büyük fast food zinciri ve franchise işletmecisidir. 100'den fazla ülkede 40.000'i aşkın restoranıyla küresel kültürün bir parçasıdır."
            // Avrupa Fransa
            "MC.PA" -> "LVMH Moët Hennessy Louis Vuitton, lüks tüketim malları sektöründe dünya lideri olan Fransız holdingidir. Louis Vuitton, Dior, Bulgari, Moët & Chandon ve 75+ prestijli marka altında faaliyet göstermektedir."
            "OR.PA" -> "L'Oréal, kozmetik ve kişisel bakım ürünleri alanında 115 yıllık tarihi ile dünyanın lider güzellik şirketidir. Lancome, Garnier, Maybelline ve Kiehl's gibi ikonik markaları bünyesinde barındırır."
            "RMS.PA" -> "Hermès International, el yapımı deri ürünleri, ipek eşarplar ve lüks hazır giyim alanında Fransız lüksünün zirvesini temsil eden aile şirketidir. Birkin ve Kelly çantaları yatırım değeri taşıyan ikonlardır."
            "TTE.PA" -> "TotalEnergies, petrol ve gaz üretiminin yanı sıra yenilenebilir enerji ve elektrikte aktif olan küresel enerji devlerinden biridir. Güneş ve rüzgar enerjisine büyük yatırımlarla enerji dönüşümüne öncülük etmektedir."
            "AIR.PA" -> "Airbus SE, ticari uçak üretiminde Boeing ile küresel liderliği paylaşan Avrupa'nın havacılık devi. A320neo ailesi, kısa-orta menzil segmentinde dünya lideridir."
            "SAN.PA" -> "Sanofi, ilaç ve biyoteknoloji alanında faaliyet gösteren Fransa merkezli küresel sağlık şirketidir. Dupixent gibi blokbuster ilaçlarıyla güçlü büyüme ivmesi sürdürmektedir."
            "BNP.PA" -> "BNP Paribas, Avrupa'nın en büyük bankalarından biri olup 65'ten fazla ülkede faaliyet göstermektedir. Yatırım bankacılığı, bireysel bankacılık ve varlık yönetimi alanlarında güçlü bir konuma sahiptir."
            // Avrupa Almanya
            "SAP.DE" -> "SAP SE, kurumsal kaynak planlama (ERP) yazılımlarında dünya lideri olan Alman teknoloji şirketidir. S/4HANA bulut platformu ile dijital dönüşüm sağlayan küresel şirketlerin vazgeçilmez ortağıdır."
            "SIE.DE" -> "Siemens AG, otomasyon, elektrik altyapısı, raylı taşımacılık sistemleri ve tıbbi görüntüleme ekipmanları alanlarında küresel lider olan Alman sanayi devidir."
            "BMW.DE" -> "BMW AG (Bayerische Motoren Werke), lüks otomobil ve motosiklet üretiminde dünyanın en prestijli markalarından biridir. MINI ve Rolls-Royce markaları da bünyesinde yer almaktadır."
            "MBG.DE" -> "Mercedes-Benz Group, otomotiv dünyasının ikonik lüks markasıdır. EQS ve EQE gibi elektrikli modelleriyle premium EV segmentine güçlü bir geçiş yapmaktadır."
            "VOW3.DE" -> "Volkswagen AG, dünyanın en büyük araç üreticilerinden biri olup VW, Audi, Porsche, Lamborghini, Bentley ve SEAT gibi onlarca markayı bünyesinde barındırır."
            "ADS.DE" -> "Adidas AG, Nike ile birlikte küresel spor giyim ve ayakkabı pazarının iki dev oyuncusundan biridir. Üç çizgi markasıyla kültürel bir ikon haline gelen Alman spor markasıdır."
            // Avrupa İngiltere
            "SHEL.L" -> "Shell plc, İngiliz-Hollanda ortak kökenli küresel enerji devi. Petrol, gaz ve büyüyen yenilenebilir enerji portföyüyle dünyanın en büyük şirketlerinden biridir."
            "BP.L" -> "BP plc, dünya genelinde petrol, gaz ve yenilenebilir enerji projelerine sahip köklü İngiliz enerji şirketidir. Net sıfır karbon hedefi çerçevesinde agresif bir dönüşüm stratejisi uygulamaktadır."
            "AZN.L" -> "AstraZeneca, onkoloji, kardiyovasküler ve solunum yolu hastalıklarına yönelik ilaçlar geliştiren İngiliz-İsveç ilaç devlerinden biridir. Tagrisso ve Imfinzi gibi kanser ilaçları muazzam gelir yaratmaktadır."
            "GSK.L" -> "GSK plc (GlaxoSmithKline), aşılar, özel ilaçlar ve tüketici sağlığı ürünleri geliştiren küresel İngiliz sağlık şirketidir. Haleon'u bağımsızlaştırarak ilaç odaklı büyüme stratejisini netleştirmiştir."
            "HSBA.L" -> "HSBC Holdings, Asya ve Avrupa'yı birbirine bağlayan en büyük uluslararası bankalardan biridir. 65 ülkede faaliyet göstererek küresel ticareti ve servet yönetimini kolaylaştırmaktadır."
            "ULVR.L" -> "Unilever plc, Dove, Lipton, Ben & Jerry's ve Hellmann's gibi 400'den fazla tüketici markasını yöneten İngiliz-Hollanda tüketim devi. Sürdürülebilirlik odaklı stratejisiyle sektörde öncü kabul edilmektedir."
            // Avrupa İsviçre
            "NESN.SW" -> "Nestlé SA, Nescafé, KitKat, Maggi ve Purina gibi küresel markalarla dünyanın en büyük gıda ve içecek şirketidir. 190'dan fazla ülkede faaliyet gösteren İsviçre gıda devi."
            "NOVN.SW" -> "Novartis AG, ilaç ve göz sağlığı (Alcon) alanlarında faaliyet gösteren İsviçre'nin en büyük ilaç şirketidir. Kanser, kardiyovasküler ve nöroloji alanlarında güçlü bir portföye sahiptir."
            "ROG.SW" -> "Roche Holding, tanı ve ilaç alanında küresel lider konumundaki İsviçre sağlık devi. Genentech ve Chugai gibi biyoteknoloji şirketleri bünyesinde yer almakta; onkoloji ve tanı alanlarında dünya standartları belirlemektedir."
            // Avrupa İspanya
            "ITX.MC" -> "Inditex, Zara, Massimo Dutti ve Pull&Bear gibi markalarıyla küresel hızlı moda sektöründe lider konumdaki İspanyol perakende devi. 93 ülkede 7.000'den fazla mağazası bulunmaktadır."
            "SAN.MC" -> "Banco Santander, Avrupa ve Latin Amerika'da kapsamlı bankacılık hizmetleri sunan İspanyol finans devi. Euro bölgesinin piyasa değeri en yüksek bankalarından biridir."
            else -> {
                val cleanName = if (name.isNotBlank()) name else sym
                "$cleanName, kendi uzmanlık alanında faaliyet gösteren ve borsada işlem gören öncü kuruluşlardan biridir. Sektördeki yenilikçi projeleriyle büyümeye devam etmektedir."
            }
        }

        // 2. Outstanding Shares in Millions (Dolaşımdaki Lot Sayısı)
        val outstandingShares = when (sym) {
            // ABD Mega Cap
            "AAPL" -> 15400.0
            "MSFT" -> 7430.0
            "NVDA" -> 24600.0
            "GOOGL" -> 12400.0
            "AMZN" -> 10400.0
            "META" -> 2540.0
            "TSLA" -> 3180.0
            "AVGO" -> 4680.0
            "ORCL" -> 2850.0
            "CRM" -> 965.0
            "ADBE" -> 440.0
            "INTC" -> 4280.0
            "AMD" -> 1615.0
            "QCOM" -> 1110.0
            "TXN" -> 905.0
            "CSCO" -> 4050.0
            "IBM" -> 905.0
            "NOW" -> 205.0
            "INTU" -> 280.0
            "NFLX" -> 431.0
            "SHOP" -> 1280.0
            "PLTR" -> 2130.0
            "SNOW" -> 330.0
            "DDOG" -> 318.0
            "NET" -> 325.0
            "PANW" -> 1310.0
            "CRWD" -> 240.0
            // ABD Finans
            "JPM" -> 2890.0
            "BAC" -> 7950.0
            "GS" -> 310.0
            "MS" -> 1605.0
            "V" -> 2060.0
            "MA" -> 938.0
            "BLK" -> 151.0
            // ABD Sağlık
            "JNJ" -> 2410.0
            "UNH" -> 930.0
            "LLY" -> 952.0
            "PFE" -> 5650.0
            "ABBV" -> 1770.0
            "MRK" -> 2540.0
            "AMGN" -> 530.0
            "MRNA" -> 382.0
            // ABD Enerji
            "XOM" -> 4060.0
            "CVX" -> 1870.0
            "NEE" -> 2050.0
            // ABD Savunma
            "LMT" -> 248.0
            "RTX" -> 1330.0
            "BA" -> 640.0
            // ABD Sanayi
            "CAT" -> 490.0
            "GE" -> 1085.0
            // ABD Tüketim/Gıda
            "WMT" -> 8020.0
            "COST" -> 445.0
            "KO" -> 4310.0
            "PEP" -> 1380.0
            "MCD" -> 725.0
            // ABD Medya
            "DIS" -> 1840.0
            // BIST
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
            // Avrupa
            "MC.PA" -> 506.0
            "OR.PA" -> 530.0
            "RMS.PA" -> 105.0
            "TTE.PA" -> 2420.0
            "AIR.PA" -> 817.0
            "SAN.PA" -> 1255.0
            "BNP.PA" -> 1260.0
            "SAP.DE" -> 1185.0
            "SIE.DE" -> 805.0
            "ALV.DE" -> 448.0
            "BMW.DE" -> 604.0
            "MBG.DE" -> 1071.0
            "VOW3.DE" -> 501.0
            "SHEL.L" -> 6930.0
            "BP.L" -> 18600.0
            "AZN.L" -> 1560.0
            "GSK.L" -> 4220.0
            "HSBA.L" -> 18800.0
            "ULVR.L" -> 2580.0
            "NESN.SW" -> 2800.0
            "NOVN.SW" -> 2140.0
            "ROG.SW" -> 860.0
            "ITX.MC" -> 3120.0
            else -> (50 + (java.lang.Math.abs(hash) % 950)).toDouble()
        }

        val usePrice = if (price > 0.0) price else (20 + (java.lang.Math.abs(hash) % 180)).toDouble()
        val marketCapVal = (usePrice * outstandingShares) / 1000.0
        val suffix = when (market.uppercase()) {
            "BIST" -> "Milyar TL"
            "FRA", "EURONEXT" -> "Milyar EUR"
            else -> "Milyar USD"
        }
        val marketCapStr = if (marketCapVal >= 1000.0 && market != "BIST") {
            val unit = if (market.uppercase() == "FRA" || market.uppercase() == "EURONEXT") "Trilyon EUR" else "Trilyon USD"
            String.format(java.util.Locale.US, "%.2f %s", marketCapVal / 1000.0, unit)
        } else {
            String.format(java.util.Locale.US, "%.1f %s", marketCapVal, suffix)
        }

        // 3. F/K ve Temettü oranları
        val peRatioVal = (50 + (java.lang.Math.abs(hash) % 220)) / 10.0
        val divYield = if (java.lang.Math.abs(hash) % 3 == 0) "%" + String.format(java.util.Locale.US, "%.1f", (10 + (java.lang.Math.abs(hash) % 65)) / 10.0) else "N/A"

        // 4. 52 Haftalık Yüksek/Alçak
        val weekLowVal = usePrice * (0.65 + (java.lang.Math.abs(hash) % 20) / 100.0)
        val weekHighVal = usePrice * (1.15 + (java.lang.Math.abs(hash) % 35) / 100.0)
        
        val unit = if (market == "BIST") " TL" else " USD"

        // 5. Sermaye / Toplam Lot
        val volumeStr = if (outstandingShares >= 1000.0) {
            String.format(java.util.Locale.US, "%.2f Milyar Lot", outstandingShares / 1000.0)
        } else {
            String.format(java.util.Locale.US, "%.1f Milyon Lot", outstandingShares)
        }

        return RichCompanyDetails(
            about = customAbout,
            peRatio = String.format(java.util.Locale.US, "%.1f", peRatioVal),
            marketCap = marketCapStr,
            week52High = String.format(java.util.Locale.US, "%.2f%s", weekHighVal, unit),
            week52Low = String.format(java.util.Locale.US, "%.2f%s", weekLowVal, unit),
            dividendYield = divYield,
            volume = volumeStr,
            news = emptyList()
        )
    }
}
