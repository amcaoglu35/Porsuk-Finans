package com.nexus.porsuk.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.NewsArticle
import com.nexus.porsuk.domain.model.NewsCategory
import com.nexus.porsuk.domain.model.NewsSentiment
import com.nexus.porsuk.domain.repository.NewsIntelligenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk News Intelligence Center — ViewModel (NewsViewModel)
 *
 * 13 Haber kategorisini, Son Dakika Manşet Akışını, aramayı, okundu/bookmark durumlarını yönetir.
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsIntelligenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadBreakingNews()
        loadNews()
    }

    private fun loadBreakingNews() {
        viewModelScope.launch {
            newsRepository.getBreakingNews().collect { list ->
                if (list.isEmpty()) {
                    val sampleBreaking = listOf(
                        NewsArticle(
                            articleId = "b1",
                            title = "FED Faiz Kararını Açıkladı: Politika Faizi Sabit",
                            summary = "ABD Merkez Bankası faiz kararını %5.50 seviyesinde sabit tuttu.",
                            content = "Detaylı açıklamalarda enflasyonla mücadele vurgusu yapıldı.",
                            source = "Bloomberg HT",
                            category = NewsCategory.FED,
                            isBreaking = true,
                            sentiment = NewsSentiment.NEUTRAL
                        ),
                        NewsArticle(
                            articleId = "b2",
                            title = "BIST 100 Rekor Hacimle Günü Tamamladı",
                            summary = "Borsa İstanbul binek endeksi rekor seviyeden kapanış gerçekleştirdi.",
                            content = "Yabancı yatırımcı girişleri devam ediyor.",
                            source = "Reuters",
                            category = NewsCategory.BIST,
                            isBreaking = true,
                            sentiment = NewsSentiment.POSITIVE
                        )
                    )
                    _uiState.update { it.copy(breakingNews = sampleBreaking) }
                } else {
                    _uiState.update { it.copy(breakingNews = list) }
                }
            }
        }
    }

    private fun loadNews() {
        viewModelScope.launch {
            newsRepository.getAllNews().collect { list ->
                if (list.isEmpty()) {
                    val sampleList = listOf(
                        NewsArticle(
                            articleId = "n1",
                            title = "THY Geniş Gövdeli Uçak Siparişini KAP'a Bildirdi",
                            summary = "Türk Hava Yolları 15 yeni uçak siparişi anlaşması imzaladığını duyurdu.",
                            content = "Detaylar KAP açıklaması ile paylaşıldı.",
                            source = "KAP",
                            category = NewsCategory.KAP,
                            symbol = "THYAO.IS",
                            sentiment = NewsSentiment.POSITIVE
                        ),
                        NewsArticle(
                            articleId = "n2",
                            title = "TCMB Haftalık Repo İhalesi Sonuçlandı",
                            summary = "Merkez Bankası likidite adımlarını sürdürüyor.",
                            content = "Detaylar TCMB resmi sitesinde yayınlandı.",
                            source = "TCMB",
                            category = NewsCategory.TCMB,
                            sentiment = NewsSentiment.NEUTRAL
                        )
                    )
                    _uiState.update { it.copy(allNews = sampleList, isLoading = false) }
                } else {
                    _uiState.update { it.copy(allNews = list, isLoading = false) }
                }
                applySearchAndFilter()
            }
        }
    }

    fun selectCategory(category: NewsCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        applySearchAndFilter()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applySearchAndFilter()
    }

    fun toggleBookmark(articleId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            newsRepository.setBookmark(articleId, !currentStatus)
        }
    }

    fun selectArticleForDetail(article: NewsArticle?) {
        _uiState.update { it.copy(selectedArticleForDetail = article) }
        if (article != null) {
            viewModelScope.launch {
                newsRepository.markAsRead(article.articleId)
            }
        }
    }

    private fun applySearchAndFilter() {
        val state = _uiState.value
        var list = state.allNews

        if (state.selectedCategory != NewsCategory.ALL) {
            list = list.filter { it.category == state.selectedCategory }
        }

        if (state.searchQuery.isNotBlank()) {
            val query = state.searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(query) || it.summary.lowercase().contains(query)
            }
        }

        _uiState.update { it.copy(filteredNews = list) }
    }
}
