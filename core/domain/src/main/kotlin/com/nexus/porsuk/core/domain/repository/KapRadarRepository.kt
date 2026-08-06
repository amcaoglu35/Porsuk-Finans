package com.nexus.porsuk.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class KapCategory {
    BILANCO, OZEL_DURUM, PAY_ALIM_SATIM, SERMAYE_ARTRIMI, TEMETTU
}

data class KapNotice(
    val id: String,
    val symbol: String,
    val companyName: String,
    val title: String,
    val category: KapCategory,
    val publishTime: String,
    val summary: String,
    val isImportant: Boolean = true
)

interface KapRadarRepository {
    fun getKapNotices(): Flow<List<KapNotice>>
    fun getFilteredKapNotices(categoryFilter: String, portfolioSymbols: Set<String>): Flow<List<KapNotice>>
}

@Singleton
class KapRadarRepositoryImpl @Inject constructor(
    private val kapScraperService: KapScraperService
) : KapRadarRepository {

    override fun getKapNotices(): Flow<List<KapNotice>> = flow {
        val result = kapScraperService.fetchLatestKapNotices()
        if (result.isSuccess) {
            emit(result.getOrDefault(emptyList()))
        } else {
            throw result.exceptionOrNull() ?: IllegalStateException("KAP verisi çekilemedi.")
        }
    }

    override fun getFilteredKapNotices(
        categoryFilter: String,
        portfolioSymbols: Set<String>
    ): Flow<List<KapNotice>> = flow {
        val result = kapScraperService.fetchLatestKapNotices()
        val list = if (result.isSuccess) {
            result.getOrDefault(emptyList())
        } else {
            throw result.exceptionOrNull() ?: IllegalStateException("KAP verisi çekilemedi.")
        }

        val filtered = list.filter { item ->
            when (categoryFilter) {
                "Portföyüm" -> portfolioSymbols.contains(item.symbol)
                "Temettü" -> item.category == KapCategory.TEMETTU
                "Bilanço" -> item.category == KapCategory.BILANCO
                "Sermaye" -> item.category == KapCategory.SERMAYE_ARTRIMI
                else -> true
            }
        }
        emit(filtered)
    }
}
