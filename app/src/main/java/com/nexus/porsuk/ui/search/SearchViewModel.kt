package com.nexus.porsuk.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.model.StockDetails
import com.nexus.porsuk.domain.usecase.search.SearchStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchStockUseCase: SearchStockUseCase
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<StockDetails>>(emptyList())
    val searchResults: StateFlow<List<StockDetails>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchStock(symbol: String, market: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val results = searchStockUseCase(symbol, market)
            _searchResults.value = results
            _isLoading.value = false
        }
    }
}
