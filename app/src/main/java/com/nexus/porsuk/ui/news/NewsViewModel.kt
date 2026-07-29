package com.nexus.porsuk.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NewsUiState(
    val categories: List<String> = listOf(
        "Son Haberler", "Şirket Haberleri", "Sektör Haberleri", 
        "Ekonomi Haberleri", "Dünya Piyasaları", "Kripto", "Teknoloji", "Yapay Zeka"
    ),
    val selectedCategory: String = "Son Haberler",
    val searchQuery: String = "",
    val allNews: List<NewsItemEntity> = emptyList(),
    val filteredNews: List<NewsItemEntity> = emptyList(),
    val favoriteNewsIds: Set<Int> = emptySet(),
    val readNewsIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NewsViewModel(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews("BIST")
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun toggleFavorite(newsId: Int) {
        _uiState.update { state ->
            val favs = state.favoriteNewsIds.toMutableSet()
            if (favs.contains(newsId)) favs.remove(newsId) else favs.add(newsId)
            state.copy(favoriteNewsIds = favs)
        }
    }

    fun markAsRead(newsId: Int) {
        _uiState.update { state ->
            state.copy(readNewsIds = state.readNewsIds + newsId)
        }
    }

    fun loadNews(symbolOrKeyword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val flow = repository.getNews(symbolOrKeyword)
                flow.collect { newsList ->
                    val categorized = newsList.map { news ->
                        val text = news.title + " " + (news.summary ?: "")
                        val sentiment = when {
                            text.contains("artış", ignoreCase = true) || text.contains("rekor", ignoreCase = true) || text.contains("büyüme", ignoreCase = true) -> "POZİTİF"
                            text.contains("düşüş", ignoreCase = true) || text.contains("zarar", ignoreCase = true) || text.contains("kriz", ignoreCase = true) -> "NEGATİF"
                            else -> "NÖTR"
                        }
                        news.copy(sentiment = sentiment)
                    }
                    _uiState.update { it.copy(allNews = categorized, isLoading = false) }
                    applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var result = state.allNews

        if (state.searchQuery.isNotBlank()) {
            result = result.filter { 
                it.title.contains(state.searchQuery, ignoreCase = true) || 
                (it.summary ?: "").contains(state.searchQuery, ignoreCase = true) 
            }
        }

        if (state.selectedCategory != "Son Haberler") {
            result = result.filter { news ->
                val summaryText = news.summary ?: ""
                when (state.selectedCategory) {
                    "Şirket Haberleri" -> news.symbol.isNotBlank()
                    "Ekonomi Haberleri" -> news.title.contains("Ekonomi", ignoreCase = true) || news.title.contains("Faiz", ignoreCase = true)
                    "Dünya Piyasaları" -> news.title.contains("Fed", ignoreCase = true) || news.title.contains("Borsa", ignoreCase = true)
                    "Kripto" -> news.title.contains("Bitcoin", ignoreCase = true) || news.title.contains("Crypto", ignoreCase = true)
                    "Teknoloji" -> news.title.contains("Teknoloji", ignoreCase = true) || news.title.contains("Chip", ignoreCase = true)
                    "Yapay Zeka" -> news.title.contains("AI", ignoreCase = true) || news.title.contains("Yapay Zeka", ignoreCase = true)
                    else -> true
                }
            }
        }

        _uiState.update { it.copy(filteredNews = result) }
    }
}
