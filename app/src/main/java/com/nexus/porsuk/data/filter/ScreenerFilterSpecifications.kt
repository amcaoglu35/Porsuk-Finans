package com.nexus.porsuk.data.filter

import com.nexus.porsuk.domain.model.ScreenerResultItem

/**
 * Porsuk Screener Pro Ultimate — Specification Pattern Arayüzü (FilterSpecification)
 *
 * Open/Closed Principle: Yeni filtre kriterleri var olan kodları değiştirmeden bu arayüz uygulanarak eklenir.
 */
interface FilterSpecification<T> {
    fun isSatisfiedBy(item: T): Boolean
}

/**
 * Değerleme F/K Filtre Specification (PeFilterSpecification)
 */
class PeFilterSpecification(private val maxPe: Double) : FilterSpecification<ScreenerResultItem> {
    override fun isSatisfiedBy(item: ScreenerResultItem): Boolean {
        return item.peRatio <= maxPe
    }
}

/**
 * Bilanço Altman Z-Score Specification (AltmanZFilterSpecification)
 */
class AltmanZFilterSpecification(private val minZScore: Double) : FilterSpecification<ScreenerResultItem> {
    override fun isSatisfiedBy(item: ScreenerResultItem): Boolean {
        return item.altmanZScore >= minZScore
    }
}

/**
 * Master Score Specification (MasterScoreFilterSpecification)
 */
class MasterScoreFilterSpecification(private val minScore: Int) : FilterSpecification<ScreenerResultItem> {
    override fun isSatisfiedBy(item: ScreenerResultItem): Boolean {
        return item.masterScore >= minScore
    }
}

/**
 * Birleşik Specification Mantığı (AndSpecification)
 */
class AndSpecification<T>(
    private val specs: List<FilterSpecification<T>>
) : FilterSpecification<T> {
    override fun isSatisfiedBy(item: T): Boolean {
        return specs.all { it.isSatisfiedBy(item) }
    }
}
