package com.nexus.porsuk.ui.macro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.data.local.SettingsManager
import com.nexus.porsuk.data.local.entity.MacroDataEntity
import com.nexus.porsuk.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MacroIndicatorItem(
    val title: String,
    val seriesId: String,
    val currentValue: String,
    val unit: String,
    val description: String,
    val historyPoints: List<Float> = emptyList()
)

data class MacroUiState(
    val indicators: List<MacroIndicatorItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MacroViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MacroUiState())
    val uiState: StateFlow<MacroUiState> = _uiState.asStateFlow()

    init {
        loadMacroData()
    }

    fun loadMacroData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val macroMap = repository.getMacroIndicators()
                
                val defaultIndicators = listOf(
                    MacroIndicatorItem("FED Politika Faizi", "FEDFUNDS", macroMap["FED_FAIZ"] ?: "5.33", "%", "ABD Merkez Bankası Gösterge Faizi", listOf(5.25f, 5.33f, 5.33f, 5.33f, 5.25f)),
                    MacroIndicatorItem("Tüketici Enflasyonu (CPI)", "CPIAUCSL", "3.0", "% Yıllık", "Tüketici Fiyat Endeksi", listOf(3.4f, 3.3f, 3.1f, 3.0f)),
                    MacroIndicatorItem("Üretici Enflasyonu (PPI)", "PPIACO", "2.6", "% Yıllık", "Üretici Fiyat Endeksi", listOf(2.2f, 2.4f, 2.6f)),
                    MacroIndicatorItem("Gayri Safi Yurt İçi Hasıla (GDP)", "GDP", "28.2T", "$", "ABD Toplam Ekonomik Büyüklük", listOf(27.9f, 28.1f, 28.2f)),
                    MacroIndicatorItem("İşsizlik Oranı (Unemployment)", "UNRATE", "4.1", "%", "İş Gücü İşsizlik Oranı", listOf(3.8f, 3.9f, 4.0f, 4.1f)),
                    MacroIndicatorItem("10 Yıllık ABD Tahvil Getirisi", "DGS10", "4.18", "%", "10-Year Treasury Yield Benchmark", listOf(4.45f, 4.30f, 4.22f, 4.18f)),
                    MacroIndicatorItem("2 Yıllık ABD Tahvil Getirisi", "DGS2", "4.42", "%", "2-Year Yield Benchmark", listOf(4.70f, 4.55f, 4.42f)),
                    MacroIndicatorItem("VIX Korku Endeksi", "VIXCLS", "15.4", "Puan", "Piyasa Oynaklık Endeksi", listOf(12.8f, 14.2f, 16.5f, 15.4f)),
                    MacroIndicatorItem("Dolar Endeksi (DXY)", "DTWEXBGS", "104.2", "Puan", "US Dollar Broad Index", listOf(105.5f, 104.8f, 104.2f)),
                    MacroIndicatorItem("M2 Para Arzı", "WM2NS", "21.0T", "$", "Piyasadaki Toplam Likidite", listOf(20.8f, 20.9f, 21.0f)),
                    MacroIndicatorItem("İmalat PMI Endeksi", "MANEMP", "48.5", "Puan", "Purchasing Managers Index", listOf(49.2f, 48.7f, 48.5f)),
                    MacroIndicatorItem("Tüketici Güven Endeksi", "UMCSENT", "68.2", "Puan", "University of Michigan Consumer Sentiment", listOf(65.6f, 66.4f, 68.2f))
                )

                _uiState.update { it.copy(indicators = defaultIndicators, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }
}
