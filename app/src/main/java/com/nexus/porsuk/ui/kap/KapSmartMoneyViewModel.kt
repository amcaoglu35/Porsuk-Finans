package com.nexus.porsuk.ui.kap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.core.domain.repository.KapNotice
import com.nexus.porsuk.core.domain.repository.KapScraperService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KapSmartMoneyUiState(
    val isLoading: Boolean = false,
    val notices: List<KapNotice> = emptyList(),
    val errorMessage: String? = null,
    val selectedCategory: String = "Tümü"
)

@HiltViewModel
class KapSmartMoneyViewModel @Inject constructor(
    private val scraperService: KapScraperService
) : ViewModel() {

    private val _uiState = MutableStateFlow(KapSmartMoneyUiState())
    val uiState: StateFlow<KapSmartMoneyUiState> = _uiState.asStateFlow()

    init {
        fetchNotices()
    }

    fun fetchNotices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = scraperService.fetchLatestKapNotices()
            if (result.isSuccess) {
                _uiState.update { it.copy(
                    notices = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessage = null
                ) }
            } else {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "KAP duyuruları çekilemedi. kap.org.tr HTML yapısı değişmiş veya erişim kısıtlanmış olabilir."
                ) }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
