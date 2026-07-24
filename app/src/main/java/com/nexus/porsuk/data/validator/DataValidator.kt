package com.nexus.porsuk.data.validator

import com.nexus.porsuk.data.local.entity.CompanyEntity
import com.nexus.porsuk.data.local.entity.FundEntity
import com.nexus.porsuk.data.local.entity.MarketQuoteEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Data Center — Veri Doğrulama ve Bütünlük Kontrolü (Data Validator)
 *
 * Uzak API veya yerel kaynaklardan gelen verilerin eksik, bozuk veya mükerrer (duplicate)
 * olup olmadığını kontrol eder.
 */
@Singleton
class DataValidator @Inject constructor() {

    /**
     * Şirket (Hisse) verisinin geçerliliğini doğrular.
     */
    fun isValidCompany(company: CompanyEntity): Boolean {
        if (company.symbol.isBlank()) return false
        if (company.companyName.isBlank()) return false
        return true
    }

    /**
     * TEFAS Fon verisinin geçerliliğini doğrular.
     */
    fun isValidFund(fund: FundEntity): Boolean {
        if (fund.code.isBlank()) return false
        if (fund.name.isBlank()) return false
        if (fund.price < 0.0) return false
        return true
    }

    /**
     * Anlık piyasa fiyat verisinin (MarketQuote) doğruluğunu kontrol eder.
     */
    fun isValidMarketQuote(quote: MarketQuoteEntity): Boolean {
        if (quote.symbol.isBlank()) return false
        if (quote.currentPrice < 0.0) return false
        return true
    }

    /**
     * Liste içerisindeki mükerrer (duplicate) Şirket kayıtlarını sembole göre temizler.
     */
    fun deduplicateCompanies(list: List<CompanyEntity>): List<CompanyEntity> {
        return list.distinctBy { it.symbol.uppercase().trim() }
            .filter { isValidCompany(it) }
    }

    /**
     * Liste içerisindeki mükerrer (duplicate) Fon kayıtlarını fon koduna göre temizler.
     */
    fun deduplicateFunds(list: List<FundEntity>): List<FundEntity> {
        return list.distinctBy { it.code.uppercase().trim() }
            .filter { isValidFund(it) }
    }
}
