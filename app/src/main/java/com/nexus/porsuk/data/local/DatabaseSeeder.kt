package com.nexus.porsuk.data.local

import com.nexus.porsuk.data.local.entity.Company

object DatabaseSeeder {
    /**
     * Uygulamanın anasayfasında BIST, NASDAQ ve Avrupa'nın en popüler 17 dev şirketinin 
     * otomatik olarak listelenmesi için zengin bir başlangıç listesi hazırlar.
     */
    fun getPopularCompanies(): List<Company> {
        return listOf(
            // --- TÜRKİYE BORSASI (BIST) ---
            Company("THYAO", "Türk Hava Yolları", "BIST", "https://logo.clearbit.com/turkishairlines.com", "THY", "Havacılık"),
            Company("EREGL", "Erdemir Çelik", "BIST", "https://logo.clearbit.com/erdemir.com.tr", "ERE", "Sanayi"),
            Company("TUPRS", "Tüpraş", "BIST", "https://logo.clearbit.com/tupras.com.tr", "TUP", "Enerji"),
            Company("ASELS", "Aselsan Savunma", "BIST", "https://logo.clearbit.com/aselsan.com", "ASE", "Savunma"),
            Company("KCHOL", "Koç Holding", "BIST", "https://logo.clearbit.com/koc.com.tr", "KCH", "Holding"),
            Company("SAHOL", "Sabancı Holding", "BIST", "https://logo.clearbit.com/sabanci.com", "SAH", "Holding"),
            Company("BIMAS", "BİM Birleşik Mağazalar", "BIST", "https://logo.clearbit.com/bim.com.tr", "BIM", "Perakende"),
            Company("SASA", "Sasa Polyester", "BIST", "https://logo.clearbit.com/sasa.com.tr", "SAS", "Kimya"),
            
            // --- AMERİKA BORSASI (NASDAQ / NYSE) ---
            Company("AAPL", "Apple Inc.", "NASDAQ", "https://logo.clearbit.com/apple.com", "AAP", "Teknoloji"),
            Company("TSLA", "Tesla Inc.", "NASDAQ", "https://logo.clearbit.com/tesla.com", "TSL", "Otomotiv"),
            Company("MSFT", "Microsoft Corp.", "NASDAQ", "https://logo.clearbit.com/microsoft.com", "MSF", "Teknoloji"),
            Company("NVDA", "NVIDIA Corporation", "NASDAQ", "https://logo.clearbit.com/nvidia.com", "NVD", "Yapay Zeka"),
            Company("AMZN", "Amazon.com Inc.", "NASDAQ", "https://logo.clearbit.com/amazon.com", "AMZ", "Perakende"),
            Company("GOOGL", "Alphabet Inc.", "NASDAQ", "https://logo.clearbit.com/google.com", "GOG", "Teknoloji"),
            Company("COIN", "Coinbase Global", "NASDAQ", "https://logo.clearbit.com/coinbase.com", "COI", "Finans"),
            
            // --- AVRUPA BORSASI (FRA / EURONEXT) ---
            Company("SAP", "SAP SE", "FRA", "https://logo.clearbit.com/sap.com", "SAP", "Teknoloji"),
            Company("MC.PA", "LVMH", "EURONEXT", "https://logo.clearbit.com/lvmh.com", "LVM", "Lüks Giyim")
        )
    }
}
