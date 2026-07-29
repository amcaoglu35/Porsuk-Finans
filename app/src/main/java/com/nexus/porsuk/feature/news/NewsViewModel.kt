package com.nexus.porsuk.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.NewsCategory
import com.nexus.porsuk.domain.repository.NewsIntelligenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsIntelligenceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(NewsCategory.LATEST)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<NewsUiState> = _selectedCategory.flatMapLatest { category ->
        combine(
            newsRepository.getNewsByCategory(category),
            _searchQuery,
            _isLoading
        ) { news, query, loading ->
            NewsUiState(
                allNews = news.filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) },
                searchQuery = query,
                selectedCategory = category,
                isLoading = loading
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewsUiState())

    init {
        refreshNews()
    }

    fun selectCategory(category: NewsCategory) {
        _selectedCategory.value = category
        refreshNews()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            newsRepository.refreshNewsByCategory(_selectedCategory.value)
            _isLoading.value = false
        }
    }

    fun toggleBookmark(articleId: String, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            newsRepository.setBookmark(articleId, !isCurrentlyBookmarked)
        }
    }

    fun markAsRead(articleId: String) {
        viewModelScope.launch {
            newsRepository.markAsRead(articleId)
        }
    }
}
