package com.nexus.porsuk.data.remote

import com.nexus.porsuk.data.local.entity.Company

data class InitialBasketSeed(
    val basketName: String,
    val region: String,
    val items: List<SeedBasketItem>
)

data class SeedBasketItem(
    val symbol: String,
    val quantity: Double,
    val buyPrice: Double
)

object ExtendedDatabaseSeeder {
    fun getInitialBasketData(): InitialBasketSeed {
        return InitialBasketSeed(
            basketName = "Porsuk Örnek Sepetim",
            region = "BIST",
            items = listOf(
                SeedBasketItem(symbol = "THYAO", quantity = 50.0, buyPrice = 280.14),
                SeedBasketItem(symbol = "EREGL", quantity = 100.0, buyPrice = 45.96),
                SeedBasketItem(symbol = "TUPRS", quantity = 20.0, buyPrice = 162.30)
            )
        )
    }

    fun getPopularCompanies(): List<Company> {
        return listOf(
            // --- BIST (Türkiye) ---
            Company("THYAO", "Türk Hava Yolları", "BIST", getLogo("thy"), "THY", "Havacılık"),
            Company("EREGL", "Erdemir", "BIST", getLogo("erdemir"), "ERE", "Demir-Çelik"),
            Company("TUPRS", "Tüpraş", "BIST", getLogo("tupras"), "TUP", "Enerji"),
            Company("ASELS", "Aselsan", "BIST", getLogo("aselsan"), "ASE", "Savunma"),
            Company("SASA", "Sasa Polyester", "BIST", getLogo("sasa"), "SAS", "Kimya"),
            Company("GARAN", "Garanti BBVA", "BIST", getLogo("garanti"), "GAR", "Bankacılık"),
            Company("AKBNK", "Akbank", "BIST", getLogo("akbank"), "AKB", "Bankacılık"),
            Company("YKBNK", "Yapı Kredi", "BIST", getLogo("yapikredi"), "YKB", "Bankacılık"),
            Company("ISCTR", "İş Bankası", "BIST", getLogo("isbankasi"), "ISB", "Bankacılık"),
            Company("KCHOL", "Koç Holding", "BIST", getLogo("koc"), "KCH", "Holding"),
            Company("SAHOL", "Sabancı Holding", "BIST", getLogo("sabanci"), "SAH", "Holding"),
            Company("BIMAS", "BİM Birleşik Mağazalar", "BIST", getLogo("bim"), "BIM", "Perakende"),
            Company("SISE", "Şişecam", "BIST", getLogo("sisecam"), "SIS", "Cam/Sanayi"),
            Company("FROTO", "Ford Otosan", "BIST", getLogo("ford"), "FRO", "Otomotiv"),
            Company("TOASO", "Tofaş", "BIST", getLogo("tofas"), "TOA", "Otomotiv"),
            Company("PETKM", "Petkim", "BIST", getLogo("petkim"), "PET", "Kimya"),
            Company("HEKTS", "Hektaş", "BIST", getLogo("hektas"), "HEK", "Tarım"),
            Company("PGSUS", "Pegasus", "BIST", getLogo("pegasus"), "PGS", "Havacılık"),
            Company("TCELL", "Turkcell", "BIST", getLogo("turkcell"), "TCE", "Telekom"),
            Company("TTKOM", "Türk Telekom", "BIST", getLogo("turktelekom"), "TTK", "Telekom"),
            Company("ARCLK", "Arçelik", "BIST", getLogo("arcelik"), "ARC", "Dayanıklı Tüketim"),
            Company("VESTL", "Vestel", "BIST", getLogo("vestel"), "VES", "Elektronik"),
            Company("ARDYZ", "ARD Yazılım", "BIST", getLogo("ard"), "ARD", "Teknoloji"),
            Company("KONTR", "Kontrolmatik", "BIST", getLogo("kontrolmatik"), "KON", "Teknoloji"),
            Company("SMRTG", "Smart Güneş", "BIST", getLogo("smartgunes"), "SMR", "Enerji"),
            Company("SOKM", "Şok Marketler", "BIST", getLogo("sok"), "SOK", "Perakende"),
            Company("MGROS", "Migros", "BIST", getLogo("migros"), "MGR", "Perakende"),
            Company("ENJSA", "Enerjisa", "BIST", getLogo("enerjisa"), "ENJ", "Enerji"),
            Company("KRDMD", "Kardemir", "BIST", getLogo("kardemir"), "KRD", "Demir-Çelik"),
            Company("ODAS", "Odaş Elektrik", "BIST", getLogo("odas"), "ODA", "Enerji"),
            Company("AKSEN", "Aksa Enerji", "BIST", getLogo("aksa"), "AKS", "Enerji"),
            Company("TKFEN", "Tekfen Holding", "BIST", getLogo("tekfen"), "TKF", "İnşaat/Holding"),
            Company("ALARK", "Alarko Holding", "BIST", getLogo("alarko"), "ALA", "Holding"),
            Company("KOZAL", "Koza Altın", "BIST", getLogo("koza"), "KOZ", "Madencilik"),
            Company("KOZAA", "Koza Anadolu", "BIST", getLogo("koza"), "KOA", "Madencilik"),
            Company("IPEKE", "İpek Doğal Enerji", "BIST", getLogo("ipek"), "IPE", "Enerji"),
            Company("TSKB", "TSKB", "BIST", getLogo("tskb"), "TSK", "Bankacılık"),
            Company("HALKB", "Halkbank", "BIST", getLogo("halkbank"), "HAL", "Bankacılık"),
            Company("VAKBN", "Vakıfbank", "BIST", getLogo("vakifbank"), "VAK", "Bankacılık"),
            Company("EKGYO", "Emlak Konut", "BIST", getLogo("emlakkonut"), "EKG", "GYO"),
            Company("TRGYO", "Torunlar GYO", "BIST", getLogo("torunlar"), "TRG", "GYO"),
            Company("DOHOL", "Doğan Holding", "BIST", getLogo("dogan"), "DOH", "Holding"),
            Company("NETAS", "Netaş", "BIST", getLogo("netas"), "NET", "Teknoloji"),
            Company("OTKAR", "Otokar", "BIST", getLogo("otokar"), "OTK", "Otomotiv"),
            Company("KORDS", "Kordsa", "BIST", getLogo("kordsa"), "KOR", "Tekstil/Sanayi"),
            Company("BRISA", "Brisa", "BIST", getLogo("brisa"), "BRI", "Otomotiv"),
            Company("GOODY", "Goodyear", "BIST", getLogo("goodyear"), "GOO", "Otomotiv"),
            Company("TMSN", "Tümosan", "BIST", getLogo("tumosan"), "TMS", "Otomotiv"),
            Company("ASUZU", "Anadolu Isuzu", "BIST", getLogo("isuzu"), "ASU", "Otomotiv"),
            Company("KARSN", "Karsan", "BIST", getLogo("karsan"), "KAR", "Otomotiv"),
            Company("EGEEN", "Ege Endüstri", "BIST", getLogo("egeendustri"), "EGE", "Otomotiv"),
            Company("BRSAN", "Borusan Mannesmann", "BIST", getLogo("borusan"), "BRS", "Sanayi"),
            Company("AEFES", "Anadolu Efes", "BIST", getLogo("anadoluefes"), "AEF", "Gıda/İçecek"),
            Company("CCOLA", "Coca-Cola İçecek", "BIST", getLogo("ccola"), "CCO", "Gıda/İçecek"),
            Company("MAVI", "Mavi Giyim", "BIST", getLogo("mavi"), "MAV", "Tekstil/Perakende"),
            Company("ULKER", "Ülker Bisküvi", "BIST", getLogo("ulker"), "ULK", "Gıda"),
            Company("AKFGY", "Akfen GYO", "BIST", getLogo("akfen"), "AKF", "GYO"),
            Company("ALBRK", "Albaraka Türk", "BIST", getLogo("albaraka"), "ALB", "Bankacılık"),
            Company("ARASE", "Aras Elektrik", "BIST", getLogo("araselektrik"), "ARA", "Enerji"),
            Company("AYDEM", "Aydem Enerji", "BIST", getLogo("aydem"), "AYD", "Enerji"),
            Company("BAGFS", "Bagfaş", "BIST", getLogo("bagfas"), "BAG", "Kimya"),
            Company("BERA", "Bera Holding", "BIST", getLogo("bera"), "BER", "Holding"),
            Company("CANTE", "Çan2 Termik", "BIST", getLogo("can2"), "CAN", "Enerji"),
            Company("CIMSA", "Çimsa", "BIST", getLogo("cimsa"), "CIM", "Çimento"),
            Company("DOAS", "Doğuş Otomotiv", "BIST", getLogo("dogus"), "DOA", "Otomotiv"),
            Company("EUPWR", "Europower Enerji", "BIST", getLogo("europower"), "EUP", "Enerji"),
            Company("GESAN", "Girişim Elektrik", "BIST", getLogo("gesan"), "GES", "Enerji"),
            Company("GUBRF", "Gübre Fabrikaları", "BIST", getLogo("gubretas"), "GUB", "Kimya"),
            Company("IPEKE", "İpek Doğal Enerji", "BIST", getLogo("ipek"), "IPE", "Enerji"),
            Company("ISMEN", "İş Yatırım", "BIST", getLogo("isyatirim"), "ISM", "Finans"),
            Company("KAYSE", "Kayseri Şeker", "BIST", getLogo("kayseriseker"), "KAY", "Gıda"),
            Company("KMPUR", "Kimteks Poliüretan", "BIST", getLogo("kimteks"), "KMP", "Kimya"),
            Company("KONYA", "Konya Çimento", "BIST", getLogo("konya"), "KON", "Çimento"),
            Company("KCAER", "Kocaer Çelik", "BIST", getLogo("kocaer"), "KCA", "Sanayi"),
            Company("MIATK", "Mia Teknoloji", "BIST", getLogo("miateknoloji"), "MIA", "Teknoloji"),
            Company("OYYAT", "Oyak Yatırım", "BIST", getLogo("oyakyatirim"), "OYY", "Finans"),
            Company("OYAKC", "Oyak Çimento", "BIST", getLogo("oyakcimento"), "OYC", "Çimento"),
            Company("QUAGR", "Qua Granite", "BIST", getLogo("quagranite"), "QUA", "İnşaat"),
            Company("SMRTG", "Smart Güneş", "BIST", getLogo("smartgunes"), "SMR", "Enerji"),
            Company("SOKM", "Şok Marketler", "BIST", getLogo("sokmarket"), "SOK", "Perakende"),
            Company("TAVHL", "TAV Havalimanları", "BIST", getLogo("tav"), "TAV", "Havacılık"),
            Company("TTRAK", "Türk Traktör", "BIST", getLogo("turktraktor"), "TTR", "Otomotiv"),
            Company("YEOTK", "Yeo Teknoloji", "BIST", getLogo("yeoteknoloji"), "YEO", "Teknoloji"),
            Company("ZOREN", "Zorlu Enerji", "BIST", getLogo("zorlu"), "ZOR", "Enerji"),
            Company("ASUZU", "Anadolu Isuzu", "BIST", getLogo("isuzu"), "ASU", "Otomotiv"),
            Company("BVSAN", "Bülbüloğlu Vinç", "BIST", getLogo("bulbuloglu"), "BVS", "Sanayi"),
            Company("CWENE", "CW Enerji", "BIST", getLogo("cwenerji"), "CWE", "Enerji"),
            Company("EUREN", "Europen Endüstri", "BIST", getLogo("europen"), "EUR", "Sanayi"),
            Company("GENIL", "Gen İlaç", "BIST", getLogo("genilac"), "GEN", "Sağlık"),
            Company("GWIND", "Galata Wind", "BIST", getLogo("galatawind"), "GWI", "Enerji"),
            Company("HUNER", "Hun Enerji", "BIST", getLogo("hunenerji"), "HUN", "Enerji"),
            Company("IMASM", "İmaş Makina", "BIST", getLogo("imasmakina"), "IMA", "Sanayi"),
            Company("INVEO", "Inveo Yatırım", "BIST", getLogo("inveo"), "INV", "Holding"),
            Company("ISDMR", "İskenderun Demir Çelik", "BIST", getLogo("isdemir"), "ISD", "Sanayi"),
            Company("KLSER", "Kaleseramik", "BIST", getLogo("kaleseramik"), "KLS", "İnşaat"),
            Company("KOPOL", "Koza Polyester", "BIST", getLogo("kozapolyester"), "KOP", "Kimya"),
            Company("MTRKS", "Matriks", "BIST", getLogo("matriks"), "MTR", "Teknoloji"),
            Company("PASEU", "Pasifik Lojistik", "BIST", getLogo("pasifik"), "PAS", "Lojistik"),
            Company("SAYAS", "Say Yenilenebilir", "BIST", getLogo("sayas"), "SAY", "Enerji"),
            Company("SDTTR", "SDT Uzay ve Savunma", "BIST", getLogo("sdttr"), "SDT", "Savunma"),
            Company("SUNTK", "Sun Tekstil", "BIST", getLogo("suntekstil"), "SUN", "Tekstil"),
            Company("TATGD", "Tat Gıda", "BIST", getLogo("tat"), "TAT", "Gıda"),
            Company("VAKKO", "Vakko", "BIST", getLogo("vakko"), "VAK", "Tekstil"),

            // --- ABD (NASDAQ/NYSE) — Teknoloji ---
            Company("AAPL", "Apple Inc.", "NASDAQ", getLogo("apple"), "AAP", "Teknoloji"),
            Company("MSFT", "Microsoft", "NASDAQ", getLogo("microsoft"), "MSF", "Teknoloji"),
            Company("GOOGL", "Alphabet (Google)", "NASDAQ", getLogo("google"), "GOO", "Teknoloji"),
            Company("AMZN", "Amazon.com", "NASDAQ", getLogo("amazon"), "AMZ", "Teknoloji/Perakende"),
            Company("NVDA", "NVIDIA", "NASDAQ", getLogo("nvidia"), "NVD", "Teknoloji"),
            Company("META", "Meta Platforms", "NASDAQ", getLogo("meta"), "MET", "Teknoloji"),
            Company("TSLA", "Tesla, Inc.", "NASDAQ", getLogo("tesla"), "TSL", "Otomotiv/Enerji"),
            Company("AVGO", "Broadcom Inc.", "NASDAQ", getLogo("broadcom"), "AVG", "Teknoloji"),
            Company("ORCL", "Oracle", "NYSE", getLogo("oracle"), "ORC", "Teknoloji"),
            Company("CRM", "Salesforce", "NYSE", getLogo("salesforce"), "CRM", "Teknoloji"),
            Company("ADBE", "Adobe", "NASDAQ", getLogo("adobe"), "ADB", "Teknoloji"),
            Company("INTC", "Intel", "NASDAQ", getLogo("intel"), "INT", "Teknoloji"),
            Company("AMD", "AMD", "NASDAQ", getLogo("amd"), "AMD", "Teknoloji"),
            Company("QCOM", "Qualcomm", "NASDAQ", getLogo("qualcomm"), "QCO", "Teknoloji"),
            Company("TXN", "Texas Instruments", "NASDAQ", getLogo("ti"), "TXN", "Teknoloji"),
            Company("CSCO", "Cisco Systems", "NASDAQ", getLogo("cisco"), "CSC", "Teknoloji"),
            Company("IBM", "IBM", "NYSE", getLogo("ibm"), "IBM", "Teknoloji"),
            Company("NOW", "ServiceNow", "NYSE", getLogo("servicenow"), "NOW", "Teknoloji"),
            Company("INTU", "Intuit", "NASDAQ", getLogo("intuit"), "INT", "Teknoloji"),
            Company("NFLX", "Netflix", "NASDAQ", getLogo("netflix"), "NFL", "Medya"),
            Company("SHOP", "Shopify", "NYSE", getLogo("shopify"), "SHO", "Teknoloji"),
            Company("PLTR", "Palantir", "NYSE", getLogo("palantir"), "PLT", "Teknoloji"),
            Company("SNOW", "Snowflake", "NYSE", getLogo("snowflake"), "SNO", "Teknoloji"),
            Company("DDOG", "Datadog", "NASDAQ", getLogo("datadog"), "DDO", "Teknoloji"),
            Company("NET", "Cloudflare", "NYSE", getLogo("cloudflare"), "NET", "Teknoloji"),
            Company("PANW", "Palo Alto Networks", "NASDAQ", getLogo("paloaltonetworks"), "PAN", "Teknoloji"),
            Company("CRWD", "CrowdStrike", "NASDAQ", getLogo("crowdstrike"), "CRW", "Teknoloji"),
            Company("FTNT", "Fortinet", "NASDAQ", getLogo("fortinet"), "FTN", "Teknoloji"),
            Company("OKTA", "Okta", "NASDAQ", getLogo("okta"), "OKT", "Teknoloji"),
            Company("ZS", "Zscaler", "NASDAQ", getLogo("zscaler"), "ZSC", "Teknoloji"),
            Company("MDB", "MongoDB", "NASDAQ", getLogo("mongodb"), "MDB", "Teknoloji"),
            Company("TEAM", "Atlassian", "NASDAQ", getLogo("atlassian"), "TEA", "Teknoloji"),
            Company("ZM", "Zoom Video", "NASDAQ", getLogo("zoom"), "ZOO", "Teknoloji"),
            Company("UBER", "Uber Technologies", "NYSE", getLogo("uber"), "UBE", "Teknoloji"),
            Company("ABNB", "Airbnb", "NASDAQ", getLogo("airbnb"), "ABN", "Teknoloji"),
            Company("RBLX", "Roblox", "NYSE", getLogo("roblox"), "RBL", "Teknoloji"),
            Company("COIN", "Coinbase", "NASDAQ", getLogo("coinbase"), "COI", "Finans"),
            Company("PATH", "UiPath", "NYSE", getLogo("uipath"), "PAT", "Teknoloji"),
            Company("AI", "C3.ai", "NYSE", getLogo("c3ai"), "AI", "Teknoloji"),
            Company("AMAT", "Applied Materials", "NASDAQ", getLogo("appliedmaterials"), "AMA", "Teknoloji"),
            Company("KLAC", "KLA Corporation", "NASDAQ", getLogo("kla"), "KLA", "Teknoloji"),
            Company("LRCX", "Lam Research", "NASDAQ", getLogo("lamresearch"), "LRC", "Teknoloji"),
            Company("MU", "Micron Technology", "NASDAQ", getLogo("micron"), "MU", "Teknoloji"),
            Company("MPWR", "Monolithic Power", "NASDAQ", getLogo("monolithicpower"), "MPW", "Teknoloji"),
            Company("ON", "ON Semiconductor", "NASDAQ", getLogo("onsemi"), "ON", "Teknoloji"),
            Company("ASML", "ASML Holding", "NASDAQ", getLogo("asml"), "ASM", "Teknoloji"),
            Company("ACN", "Accenture", "NYSE", getLogo("accenture"), "ACN", "Teknoloji"),

            // --- ABD (NASDAQ/NYSE) — Finans/Bankacılık ---
            Company("JPM", "JPMorgan Chase", "NYSE", getLogo("jpmorganchase"), "JPM", "Finans"),
            Company("BAC", "Bank of America", "NYSE", getLogo("bankofamerica"), "BAC", "Finans"),
            Company("WFC", "Wells Fargo", "NYSE", getLogo("wellsfargo"), "WFC", "Finans"),
            Company("GS", "Goldman Sachs", "NYSE", getLogo("goldmansachs"), "GS", "Finans"),
            Company("MS", "Morgan Stanley", "NYSE", getLogo("morganstanley"), "MS", "Finans"),
            Company("C", "Citigroup", "NYSE", getLogo("citigroup"), "C", "Finans"),
            Company("BLK", "BlackRock", "NYSE", getLogo("blackrock"), "BLK", "Finans"),
            Company("AXP", "American Express", "NYSE", getLogo("americanexpress"), "AXP", "Finans"),
            Company("SCHW", "Charles Schwab", "NYSE", getLogo("schwab"), "SCH", "Finans"),
            Company("USB", "U.S. Bancorp", "NYSE", getLogo("usbank"), "USB", "Finans"),
            Company("PNC", "PNC Financial", "NYSE", getLogo("pnc"), "PNC", "Finans"),
            Company("V", "Visa Inc.", "NYSE", getLogo("visa"), "VIS", "Finans"),
            Company("MA", "Mastercard", "NYSE", getLogo("mastercard"), "MAS", "Finans"),
            Company("PYPL", "PayPal", "NASDAQ", getLogo("paypal"), "PYP", "Finans"),
            Company("SQ", "Block Inc.", "NYSE", getLogo("block"), "SQ", "Finans"),
            Company("AFRM", "Affirm Holdings", "NASDAQ", getLogo("affirm"), "AFR", "Finans"),
            Company("SOFI", "SoFi Technologies", "NASDAQ", getLogo("sofi"), "SOF", "Finans"),
            Company("HOOD", "Robinhood Markets", "NASDAQ", getLogo("robinhood"), "HOO", "Finans"),

            // --- ABD (NASDAQ/NYSE) — Sağlık ---
            Company("JNJ", "Johnson & Johnson", "NYSE", getLogo("jnj"), "JNJ", "Sağlık"),
            Company("UNH", "UnitedHealth Group", "NYSE", getLogo("unitedhealth"), "UNH", "Sağlık"),
            Company("LLY", "Eli Lilly", "NYSE", getLogo("lilly"), "LLY", "Sağlık"),
            Company("PFE", "Pfizer", "NYSE", getLogo("pfizer"), "PFE", "Sağlık"),
            Company("ABBV", "AbbVie", "NYSE", getLogo("abbvie"), "ABB", "Sağlık"),
            Company("MRK", "Merck & Co.", "NYSE", getLogo("merck"), "MRK", "Sağlık"),
            Company("AMGN", "Amgen", "NASDAQ", getLogo("amgen"), "AMG", "Sağlık"),
            Company("GILD", "Gilead Sciences", "NASDAQ", getLogo("gilead"), "GIL", "Sağlık"),
            Company("REGN", "Regeneron", "NASDAQ", getLogo("regeneron"), "REG", "Sağlık"),
            Company("BIIB", "Biogen", "NASDAQ", getLogo("biogen"), "BII", "Sağlık"),
            Company("MRNA", "Moderna", "NASDAQ", getLogo("moderna"), "MRN", "Sağlık"),
            Company("ISRG", "Intuitive Surgical", "NASDAQ", getLogo("intuitivesurgical"), "ISR", "Sağlık"),
            Company("TMO", "Thermo Fisher", "NYSE", getLogo("thermofisher"), "TMO", "Sağlık"),
            Company("MDT", "Medtronic", "NYSE", getLogo("medtronic"), "MDT", "Sağlık"),
            Company("CVS", "CVS Health", "NYSE", getLogo("cvshealth"), "CVS", "Sağlık"),
            Company("HUM", "Humana", "NYSE", getLogo("humana"), "HUM", "Sağlık"),
            Company("CI", "Cigna Group", "NYSE", getLogo("cigna"), "CI", "Sağlık"),
            Company("SYK", "Stryker", "NYSE", getLogo("stryker"), "SYK", "Sağlık"),
            Company("EW", "Edwards Lifesciences", "NYSE", getLogo("edwards"), "EW", "Sağlık"),
            Company("DXCM", "Dexcom", "NASDAQ", getLogo("dexcom"), "DXC", "Sağlık"),

            // --- ABD (NASDAQ/NYSE) — Enerji ---
            Company("XOM", "ExxonMobil", "NYSE", getLogo("exxonmobil"), "XOM", "Enerji"),
            Company("CVX", "Chevron", "NYSE", getLogo("chevron"), "CVX", "Enerji"),
            Company("COP", "ConocoPhillips", "NYSE", getLogo("conocophillips"), "COP", "Enerji"),
            Company("OXY", "Occidental Petroleum", "NYSE", getLogo("oxy"), "OXY", "Enerji"),
            Company("SLB", "SLB (Schlumberger)", "NYSE", getLogo("slb"), "SLB", "Enerji"),
            Company("PSX", "Phillips 66", "NYSE", getLogo("phillips66"), "PSX", "Enerji"),
            Company("VLO", "Valero Energy", "NYSE", getLogo("valero"), "VLO", "Enerji"),
            Company("EOG", "EOG Resources", "NYSE", getLogo("eog"), "EOG", "Enerji"),
            Company("NEE", "NextEra Energy", "NYSE", getLogo("nexteraenergy"), "NEE", "Enerji"),

            // --- ABD (NASDAQ/NYSE) — Savunma ---
            Company("LMT", "Lockheed Martin", "NYSE", getLogo("lockheedmartin"), "LMT", "Savunma"),
            Company("RTX", "RTX Corporation", "NYSE", getLogo("rtx"), "RTX", "Savunma"),
            Company("NOC", "Northrop Grumman", "NYSE", getLogo("northropgrumman"), "NOC", "Savunma"),
            Company("GD", "General Dynamics", "NYSE", getLogo("generaldynamics"), "GD", "Savunma"),
            Company("BA", "Boeing", "NYSE", getLogo("boeing"), "BA", "Havacılık/Savunma"),
            Company("HII", "Huntington Ingalls", "NYSE", getLogo("huntingtoningalls"), "HII", "Savunma"),

            // --- ABD (NASDAQ/NYSE) — Sanayi ---
            Company("GE", "GE Aerospace", "NYSE", getLogo("ge"), "GE", "Sanayi"),
            Company("CAT", "Caterpillar", "NYSE", getLogo("caterpillar"), "CAT", "Sanayi"),
            Company("HON", "Honeywell", "NASDAQ", getLogo("honeywell"), "HON", "Sanayi"),
            Company("ITW", "Illinois Tool Works", "NYSE", getLogo("illinoistool"), "ITW", "Sanayi"),
            Company("MMM", "3M Company", "NYSE", getLogo("3m"), "MMM", "Sanayi"),
            Company("DE", "Deere & Company", "NYSE", getLogo("deere"), "DE", "Sanayi"),
            Company("ETN", "Eaton Corporation", "NYSE", getLogo("eaton"), "ETN", "Sanayi"),

            // --- ABD (NASDAQ/NYSE) — Tüketim / Perakende ---
            Company("WMT", "Walmart", "NYSE", getLogo("walmart"), "WMT", "Perakende"),
            Company("COST", "Costco Wholesale", "NASDAQ", getLogo("costco"), "COS", "Perakende"),
            Company("TGT", "Target Corporation", "NYSE", getLogo("target"), "TGT", "Perakende"),
            Company("HD", "Home Depot", "NYSE", getLogo("homedepot"), "HD", "Perakende"),
            Company("LOW", "Lowe's Companies", "NYSE", getLogo("lowes"), "LOW", "Perakende"),
            Company("AMZN", "Amazon.com", "NASDAQ", getLogo("amazon"), "AMZ", "Perakende"),
            Company("TJX", "TJX Companies", "NYSE", getLogo("tjx"), "TJX", "Perakende"),
            Company("NKE", "Nike", "NYSE", getLogo("nike"), "NKE", "Tekstil"),
            Company("LULU", "Lululemon Athletica", "NASDAQ", getLogo("lululemon"), "LUL", "Tekstil"),

            // --- ABD (NASDAQ/NYSE) — Gıda & İçecek ---
            Company("KO", "Coca-Cola", "NYSE", getLogo("cocacola"), "KO", "Gıda"),
            Company("PEP", "PepsiCo", "NASDAQ", getLogo("pepsi"), "PEP", "Gıda"),
            Company("MCD", "McDonald's", "NYSE", getLogo("mcdonalds"), "MCD", "Gıda"),
            Company("SBUX", "Starbucks", "NASDAQ", getLogo("starbucks"), "SBU", "Gıda"),
            Company("PG", "Procter & Gamble", "NYSE", getLogo("pg"), "PG", "Tüketim"),
            Company("PM", "Philip Morris", "NYSE", getLogo("philipmorris"), "PM", "Tüketim"),
            Company("MO", "Altria Group", "NYSE", getLogo("altria"), "MO", "Tüketim"),
            Company("CL", "Colgate-Palmolive", "NYSE", getLogo("colgate"), "CL", "Tüketim"),
            Company("GIS", "General Mills", "NYSE", getLogo("generalmills"), "GIS", "Gıda"),
            Company("K", "Kellanova (Kellogg's)", "NYSE", getLogo("kelloggs"), "K", "Gıda"),
            Company("HRL", "Hormel Foods", "NYSE", getLogo("hormel"), "HRL", "Gıda"),

            // --- ABD (NASDAQ/NYSE) — Medya/Eğlence ---
            Company("DIS", "Walt Disney", "NYSE", getLogo("disney"), "DIS", "Medya"),
            Company("CMCSA", "Comcast", "NASDAQ", getLogo("comcast"), "CMC", "Medya"),
            Company("NFLX", "Netflix", "NASDAQ", getLogo("netflix"), "NFL", "Medya"),
            Company("SPOT", "Spotify", "NYSE", getLogo("spotify"), "SPO", "Medya"),
            Company("SNAP", "Snap Inc.", "NYSE", getLogo("snapchat"), "SNA", "Medya"),
            Company("PARA", "Paramount Global", "NASDAQ", getLogo("paramount"), "PAR", "Medya"),

            // --- ABD (NASDAQ/NYSE) — Otomotiv ---
            Company("F", "Ford Motor", "NYSE", getLogo("ford"), "F", "Otomotiv"),
            Company("GM", "General Motors", "NYSE", getLogo("gm"), "GM", "Otomotiv"),
            Company("RIVN", "Rivian Automotive", "NASDAQ", getLogo("rivian"), "RIV", "Otomotiv"),
            Company("LCID", "Lucid Group", "NASDAQ", getLogo("lucid"), "LCI", "Otomotiv"),
            Company("TM", "Toyota Motor", "NYSE", getLogo("toyota"), "TM", "Otomotiv"),
            Company("HMC", "Honda Motor", "NYSE", getLogo("honda"), "HMC", "Otomotiv"),
            Company("STLA", "Stellantis N.V.", "NYSE", getLogo("stellantis"), "STL", "Otomotiv"),

            // --- ABD (NASDAQ/NYSE) — Lojistik/Telekom ---
            Company("UPS", "United Parcel Service", "NYSE", getLogo("ups"), "UPS", "Lojistik"),
            Company("FDX", "FedEx", "NYSE", getLogo("fedex"), "FDX", "Lojistik"),
            Company("T", "AT&T", "NYSE", getLogo("att"), "T", "Telekom"),
            Company("VZ", "Verizon", "NYSE", getLogo("verizon"), "VZ", "Telekom"),
            Company("TMUS", "T-Mobile US", "NASDAQ", getLogo("tmobile"), "TMO", "Telekom"),

            // --- ABD (NASDAQ/NYSE) — Kripto/Madencilik ---
            Company("MARA", "Marathon Digital", "NASDAQ", getLogo("marathon"), "MAR", "Madencilik"),
            Company("RIOT", "Riot Platforms", "NASDAQ", getLogo("riot"), "RIO", "Madencilik"),
            Company("DKNG", "DraftKings", "NASDAQ", getLogo("draftkings"), "DKN", "Eğlence"),

            // --- Asya (US-listed ADR) ---
            Company("BABA", "Alibaba Group", "NYSE", getLogo("alibaba"), "BAB", "Teknoloji"),
            Company("JD", "JD.com", "NASDAQ", getLogo("jd"), "JD", "Teknoloji"),
            Company("PDD", "PDD Holdings", "NASDAQ", getLogo("pinduoduo"), "PDD", "Teknoloji"),
            Company("BIDU", "Baidu", "NASDAQ", getLogo("baidu"), "BID", "Teknoloji"),
            Company("SONY", "Sony Group", "NYSE", getLogo("sony"), "SON", "Teknoloji"),

            // ─────────────────────────────────────────────────────────
            // --- Avrupa — Fransa (Euronext Paris) ---
            Company("MC.PA", "LVMH Moët Hennessy", "EPA", getLogo("lvmh"), "LVM", "Lüks Tüketim"),
            Company("OR.PA", "L'Oréal SA", "EPA", getLogo("loreal"), "OR", "Kozmetik"),
            Company("RMS.PA", "Hermès International", "EPA", getLogo("hermes"), "RMS", "Lüks Tüketim"),
            Company("CDI.PA", "Christian Dior SE", "EPA", getLogo("dior"), "CDI", "Lüks Tüketim"),
            Company("KER.PA", "Kering SA", "EPA", getLogo("kering"), "KER", "Lüks Tüketim"),
            Company("PUB.PA", "Publicis Groupe", "EPA", getLogo("publicis"), "PUB", "Medya"),
            Company("TTE.PA", "TotalEnergies SE", "EPA", getLogo("totalenergies"), "TTE", "Enerji"),
            Company("AIR.PA", "Airbus SE", "EPA", getLogo("airbus"), "AIR", "Havacılık"),
            Company("SAN.PA", "Sanofi SA", "EPA", getLogo("sanofi"), "SAN", "Sağlık"),
            Company("BNP.PA", "BNP Paribas", "EPA", getLogo("bnpparibas"), "BNP", "Finans"),
            Company("ACA.PA", "Crédit Agricole", "EPA", getLogo("creditagricole"), "ACA", "Finans"),
            Company("SG.PA", "Société Générale", "EPA", getLogo("societegenerale"), "SG", "Finans"),
            Company("CS.PA", "AXA SA", "EPA", getLogo("axa"), "AXA", "Finans"),
            Company("RI.PA", "Pernod Ricard", "EPA", getLogo("pernodricard"), "RI", "Gıda"),
            Company("DG.PA", "Vinci SA", "EPA", getLogo("vinci"), "DG", "İnşaat"),
            Company("AI.PA", "Air Liquide", "EPA", getLogo("airliquide"), "AI", "Kimya"),
            Company("EDF.PA", "EDF (Électricité de France)", "EPA", getLogo("edf"), "EDF", "Enerji"),
            Company("VK.PA", "Vallourec SA", "EPA", getLogo("vallourec"), "VK", "Sanayi"),
            Company("STLA.PA", "Stellantis N.V.", "EPA", getLogo("stellantis"), "STL", "Otomotiv"),

            // --- Avrupa — Almanya (XETRA) ---
            Company("SAP.DE", "SAP SE", "ETR", getLogo("sap"), "SAP", "Teknoloji"),
            Company("SIE.DE", "Siemens AG", "ETR", getLogo("siemens"), "SIE", "Sanayi"),
            Company("ALV.DE", "Allianz SE", "ETR", getLogo("allianz"), "ALV", "Finans"),
            Company("BMW.DE", "BMW AG", "ETR", getLogo("bmw"), "BMW", "Otomotiv"),
            Company("MBG.DE", "Mercedes-Benz Group", "ETR", getLogo("mercedes"), "MBG", "Otomotiv"),
            Company("VOW3.DE", "Volkswagen AG", "ETR", getLogo("volkswagen"), "VOW", "Otomotiv"),
            Company("BAYN.DE", "Bayer AG", "ETR", getLogo("bayer"), "BAY", "Sağlık/Kimya"),
            Company("BAS.DE", "BASF SE", "ETR", getLogo("basf"), "BAS", "Kimya"),
            Company("DHL.DE", "DHL Group", "ETR", getLogo("dhl"), "DHL", "Lojistik"),
            Company("DTE.DE", "Deutsche Telekom", "ETR", getLogo("deutschetelekom"), "DTE", "Telekom"),
            Company("DB1.DE", "Deutsche Börse", "ETR", getLogo("deutscheboerse"), "DB1", "Finans"),
            Company("MUV2.DE", "Munich Re", "ETR", getLogo("munichre"), "MUV", "Finans"),
            Company("ADS.DE", "Adidas AG", "ETR", getLogo("adidas"), "ADS", "Tekstil"),
            Company("IFX.DE", "Infineon Technologies", "ETR", getLogo("infineon"), "IFX", "Teknoloji"),
            Company("CON.DE", "Continental AG", "ETR", getLogo("continental"), "CON", "Otomotiv"),
            Company("ASML.AS", "ASML Holding", "AMS", getLogo("asml"), "ASM", "Teknoloji"),
            Company("FRE.DE", "Fresenius SE", "ETR", getLogo("fresenius"), "FRE", "Sağlık"),

            // --- Avrupa — İngiltere (LSE) ---
            Company("SHEL.L", "Shell plc", "LSE", getLogo("shell"), "SHL", "Enerji"),
            Company("BP.L", "BP plc", "LSE", getLogo("bp"), "BP", "Enerji"),
            Company("AZN.L", "AstraZeneca", "LSE", getLogo("astrazeneca"), "AZN", "Sağlık"),
            Company("GSK.L", "GSK plc", "LSE", getLogo("gsk"), "GSK", "Sağlık"),
            Company("HSBA.L", "HSBC Holdings", "LSE", getLogo("hsbc"), "HSB", "Finans"),
            Company("BARC.L", "Barclays plc", "LSE", getLogo("barclays"), "BAR", "Finans"),
            Company("LLOY.L", "Lloyds Banking Group", "LSE", getLogo("lloyds"), "LLO", "Finans"),
            Company("NWG.L", "NatWest Group", "LSE", getLogo("natwest"), "NWG", "Finans"),
            Company("RIO.L", "Rio Tinto", "LSE", getLogo("riotinto"), "RIO", "Madencilik"),
            Company("ULVR.L", "Unilever plc", "LSE", getLogo("unilever"), "ULV", "Tüketim"),
            Company("DGE.L", "Diageo plc", "LSE", getLogo("diageo"), "DGE", "Gıda"),
            Company("BATS.L", "British American Tobacco", "LSE", getLogo("bat"), "BAT", "Tüketim"),
            Company("VOD.L", "Vodafone Group", "LSE", getLogo("vodafone"), "VOD", "Telekom"),
            Company("BT.L", "BT Group", "LSE", getLogo("bt"), "BT", "Telekom"),
            Company("MKS.L", "Marks & Spencer", "LSE", getLogo("marksandspencer"), "MKS", "Perakende"),

            // --- Avrupa — İsviçre (SIX) ---
            Company("NESN.SW", "Nestlé SA", "SWX", getLogo("nestle"), "NES", "Gıda"),
            Company("NOVN.SW", "Novartis AG", "SWX", getLogo("novartis"), "NOV", "Sağlık"),
            Company("ROG.SW", "Roche Holding", "SWX", getLogo("roche"), "ROC", "Sağlık"),
            Company("ZURN.SW", "Zurich Insurance", "SWX", getLogo("zurich"), "ZUR", "Finans"),
            Company("UBS.SW", "UBS Group", "SWX", getLogo("ubs"), "UBS", "Finans"),
            Company("ABBN.SW", "ABB Ltd.", "SWX", getLogo("abb"), "ABB", "Sanayi"),

            // --- Avrupa — Hollanda (AMS) ---
            Company("PHIA.AS", "Philips N.V.", "AMS", getLogo("philips"), "PHI", "Sağlık/Teknoloji"),
            Company("ING.AS", "ING Groep", "AMS", getLogo("ing"), "ING", "Finans"),
            Company("RAND.AS", "Randstad N.V.", "AMS", getLogo("randstad"), "RAN", "Hizmet"),
            Company("HEIA.AS", "Heineken N.V.", "AMS", getLogo("heineken"), "HEI", "Gıda"),

            // --- Avrupa — İspanya (BME) ---
            Company("ITX.MC", "Inditex (Zara)", "BME", getLogo("inditex"), "ITX", "Perakende"),
            Company("SAN.MC", "Banco Santander", "BME", getLogo("santander"), "SAN", "Finans"),
            Company("IBE.MC", "Iberdrola SA", "BME", getLogo("iberdrola"), "IBE", "Enerji"),
            Company("REP.MC", "Repsol SA", "BME", getLogo("repsol"), "REP", "Enerji"),
            Company("BBVA.MC", "BBVA", "BME", getLogo("bbva"), "BBV", "Finans"),

        )
    }

    private fun getLogo(name: String): String {
        val domain = when (name) {
            // --- BIST ---
            "thy" -> "turkishairlines.com"
            "erdemir" -> "erdemir.com.tr"
            "tupras" -> "tupras.com.tr"
            "aselsan" -> "aselsan.com"
            "sasa" -> "sasa.com.tr"
            "garanti" -> "garantibbva.com.tr"
            "akbank" -> "akbank.com"
            "yapikredi" -> "yapikredi.com.tr"
            "isbankasi" -> "isbank.com.tr"
            "koc" -> "koc.com.tr"
            "sabanci" -> "sabanci.com.tr"
            "bim" -> "bim.com.tr"
            "sisecam" -> "sisecam.com"
            "ford" -> "ford.com.tr"
            "tofas" -> "tofas.com.tr"
            "petkim" -> "petkim.com.tr"
            "hektas" -> "hektas.com.tr"
            "pegasus" -> "flypgs.com"
            "turkcell" -> "turkcell.com.tr"
            "turktelekom" -> "turktelekom.com.tr"
            "arcelik" -> "arcelikglobal.com"
            "vestel" -> "vestel.com.tr"
            "sok", "sokmarket" -> "sokmarket.com.tr"
            "migros" -> "migros.com.tr"
            "enerjisa" -> "enerjisa.com.tr"
            "kardemir" -> "kardemir.com"
            "alarko" -> "alarko.com.tr"
            "anadoluefes" -> "anadoluefes.com"
            "ccola" -> "cci.com.tr"
            "mavi" -> "mavi.com"
            "ulker" -> "ulker.com.tr"
            // --- ABD Teknoloji ---
            "apple" -> "apple.com"
            "microsoft" -> "microsoft.com"
            "google" -> "abc.xyz"
            "amazon" -> "amazon.com"
            "nvidia" -> "nvidia.com"
            "meta" -> "meta.com"
            "tesla" -> "tesla.com"
            "broadcom" -> "broadcom.com"
            "oracle" -> "oracle.com"
            "salesforce" -> "salesforce.com"
            "adobe" -> "adobe.com"
            "intel" -> "intel.com"
            "amd" -> "amd.com"
            "qualcomm" -> "qualcomm.com"
            "ti" -> "ti.com"
            "cisco" -> "cisco.com"
            "ibm" -> "ibm.com"
            "servicenow" -> "servicenow.com"
            "intuit" -> "intuit.com"
            "netflix" -> "netflix.com"
            "shopify" -> "shopify.com"
            "palantir" -> "palantir.com"
            "snowflake" -> "snowflake.com"
            "datadog" -> "datadoghq.com"
            "cloudflare" -> "cloudflare.com"
            "paloaltonetworks" -> "paloaltonetworks.com"
            "crowdstrike" -> "crowdstrike.com"
            "fortinet" -> "fortinet.com"
            "okta" -> "okta.com"
            "zscaler" -> "zscaler.com"
            "mongodb" -> "mongodb.com"
            "atlassian" -> "atlassian.com"
            "zoom" -> "zoom.us"
            "uber" -> "uber.com"
            "airbnb" -> "airbnb.com"
            "roblox" -> "roblox.com"
            "coinbase" -> "coinbase.com"
            "uipath" -> "uipath.com"
            "c3ai" -> "c3.ai"
            "appliedmaterials" -> "appliedmaterials.com"
            "kla" -> "kla.com"
            "lamresearch" -> "lamresearch.com"
            "micron" -> "micron.com"
            "monolithicpower" -> "monolithicpower.com"
            "onsemi" -> "onsemi.com"
            "asml" -> "asml.com"
            "accenture" -> "accenture.com"
            // --- ABD Finans ---
            "jpmorganchase" -> "jpmorganchase.com"
            "bankofamerica" -> "bankofamerica.com"
            "wellsfargo" -> "wellsfargo.com"
            "goldmansachs" -> "goldmansachs.com"
            "morganstanley" -> "morganstanley.com"
            "citigroup" -> "citigroup.com"
            "blackrock" -> "blackrock.com"
            "americanexpress" -> "americanexpress.com"
            "schwab" -> "schwab.com"
            "usbank" -> "usbank.com"
            "pnc" -> "pnc.com"
            "visa" -> "visa.com"
            "mastercard" -> "mastercard.com"
            "paypal" -> "paypal.com"
            "block" -> "block.xyz"
            "affirm" -> "affirm.com"
            "sofi" -> "sofi.com"
            "robinhood" -> "robinhood.com"
            // --- ABD Sağlık ---
            "jnj" -> "jnj.com"
            "unitedhealth" -> "unitedhealthgroup.com"
            "lilly" -> "lilly.com"
            "pfizer" -> "pfizer.com"
            "abbvie" -> "abbvie.com"
            "merck" -> "merck.com"
            "amgen" -> "amgen.com"
            "gilead" -> "gilead.com"
            "regeneron" -> "regeneron.com"
            "biogen" -> "biogen.com"
            "moderna" -> "modernatx.com"
            "intuitivesurgical" -> "intuitivesurgical.com"
            "thermofisher" -> "thermofisher.com"
            "medtronic" -> "medtronic.com"
            "cvshealth" -> "cvshealth.com"
            "humana" -> "humana.com"
            "cigna" -> "cigna.com"
            "stryker" -> "stryker.com"
            "edwards" -> "edwards.com"
            "dexcom" -> "dexcom.com"
            // --- ABD Enerji ---
            "exxonmobil" -> "exxonmobil.com"
            "chevron" -> "chevron.com"
            "conocophillips" -> "conocophillips.com"
            "oxy" -> "oxy.com"
            "slb" -> "slb.com"
            "phillips66" -> "phillips66.com"
            "valero" -> "valero.com"
            "eog" -> "eogresources.com"
            "nexteraenergy" -> "nexteraenergy.com"
            // --- ABD Savunma ---
            "lockheedmartin" -> "lockheedmartin.com"
            "rtx" -> "rtx.com"
            "northropgrumman" -> "northropgrumman.com"
            "generaldynamics" -> "gd.com"
            "boeing" -> "boeing.com"
            "huntingtoningalls" -> "huntingtoningalls.com"
            // --- ABD Sanayi ---
            "ge" -> "ge.com"
            "caterpillar" -> "caterpillar.com"
            "honeywell" -> "honeywell.com"
            "illinoistool" -> "itw.com"
            "3m" -> "3m.com"
            "deere" -> "deere.com"
            "eaton" -> "eaton.com"
            // --- ABD Perakende / Tüketim ---
            "walmart" -> "walmart.com"
            "costco" -> "costco.com"
            "target" -> "target.com"
            "homedepot" -> "homedepot.com"
            "lowes" -> "lowes.com"
            "tjx" -> "tjx.com"
            "nike" -> "nike.com"
            "lululemon" -> "lululemon.com"
            // --- ABD Gıda ---
            "cocacola" -> "coca-colacompany.com"
            "pepsi" -> "pepsico.com"
            "mcdonalds" -> "mcdonalds.com"
            "starbucks" -> "starbucks.com"
            "pg" -> "pg.com"
            "philipmorris" -> "pmi.com"
            "altria" -> "altria.com"
            "colgate" -> "colgatepalmolive.com"
            "generalmills" -> "generalmills.com"
            "kelloggs" -> "kellanova.com"
            "hormel" -> "hormelfoods.com"
            // --- ABD Medya ---
            "disney" -> "thewaltdisneycompany.com"
            "comcast" -> "comcast.com"
            "spotify" -> "spotify.com"
            "snapchat" -> "snap.com"
            "paramount" -> "paramount.com"
            // --- ABD Otomotiv ---
            "gm" -> "gm.com"
            "rivian" -> "rivian.com"
            "lucid" -> "lucidmotors.com"
            "toyota" -> "toyota.com"
            "honda" -> "honda.com"
            "stellantis" -> "stellantis.com"
            // --- ABD Lojistik/Telekom ---
            "ups" -> "ups.com"
            "fedex" -> "fedex.com"
            "att" -> "att.com"
            "verizon" -> "verizon.com"
            "tmobile" -> "t-mobile.com"
            // --- ABD Kripto ---
            "marathon" -> "marathondh.com"
            "riot" -> "riotplatforms.com"
            "draftkings" -> "draftkings.com"
            // --- Asya ---
            "alibaba" -> "alibaba.com"
            "jd" -> "jd.com"
            "pinduoduo" -> "pinduoduo.com"
            "baidu" -> "baidu.com"
            "tencent" -> "tencent.com"
            "netease" -> "netease.com"
            "sony" -> "sony.com"
            // --- Avrupa Fransa ---
            "lvmh" -> "lvmh.com"
            "loreal" -> "loreal.com"
            "hermes" -> "hermes.com"
            "dior" -> "dior.com"
            "kering" -> "kering.com"
            "publicis" -> "publicis.com"
            "totalenergies" -> "totalenergies.com"
            "airbus" -> "airbus.com"
            "sanofi" -> "sanofi.com"
            "bnpparibas" -> "group.bnpparibas.com"
            "creditagricole" -> "credit-agricole.com"
            "societegenerale" -> "societegenerale.com"
            "axa" -> "axa.com"
            "pernodricard" -> "pernod-ricard.com"
            "vinci" -> "vinci.com"
            "airliquide" -> "airliquide.com"
            "edf" -> "edf.com"
            "vallourec" -> "vallourec.com"
            // --- Avrupa Almanya ---
            "sap" -> "sap.com"
            "siemens" -> "siemens.com"
            "allianz" -> "allianz.com"
            "bmw" -> "bmw.com"
            "mercedes" -> "mercedes-benz.com"
            "volkswagen" -> "volkswagen.com"
            "bayer" -> "bayer.com"
            "basf" -> "basf.com"
            "dhl" -> "dhl.com"
            "deutschetelekom" -> "telekom.com"
            "deutscheboerse" -> "deutsche-boerse.com"
            "munichre" -> "munichre.com"
            "adidas" -> "adidas.com"
            "infineon" -> "infineon.com"
            "continental" -> "continental.com"
            "fresenius" -> "fresenius.com"
            // --- Avrupa İngiltere ---
            "shell" -> "shell.com"
            "bp" -> "bp.com"
            "astrazeneca" -> "astrazeneca.com"
            "gsk" -> "gsk.com"
            "hsbc" -> "hsbc.com"
            "barclays" -> "barclays.com"
            "lloyds" -> "lloydsbank.com"
            "natwest" -> "natwestgroup.com"
            "riotinto" -> "riotinto.com"
            "unilever" -> "unilever.com"
            "diageo" -> "diageo.com"
            "bat" -> "bat.com"
            "vodafone" -> "vodafone.com"
            "bt" -> "bt.com"
            "marksandspencer" -> "marksandspencer.com"
            // --- Avrupa İsviçre ---
            "nestle" -> "nestle.com"
            "novartis" -> "novartis.com"
            "roche" -> "roche.com"
            "zurich" -> "zurich.com"
            "ubs" -> "ubs.com"
            "abb" -> "abb.com"
            // --- Avrupa Hollanda ---
            "philips" -> "philips.com"
            "ing" -> "ing.com"
            "randstad" -> "randstad.com"
            "heineken" -> "heineken.com"
            // --- Avrupa İspanya ---
            "inditex" -> "inditex.com"
            "santander" -> "santander.com"
            "iberdrola" -> "iberdrola.com"
            "repsol" -> "repsol.com"
            "bbva" -> "bbva.com"
            else -> "$name.com"
        }
        return "https://logo.clearbit.com/$domain"
    }
}
