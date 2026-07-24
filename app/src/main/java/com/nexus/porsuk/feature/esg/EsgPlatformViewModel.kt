package com.nexus.porsuk.feature.esg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk ESG & Sustainability Intelligence Platform — ViewModel
 *
 * Çevresel (E), Sosyal (S) ve Kurumsal Yönetişim (G) derecelendirmelerini, karbon yoğunluğunu ve ESG uyarısını yönetir.
 */
@HiltViewModel
class EsgPlatformViewModel @Inject constructor(
    private val esgRepository: ESGRepository,
    private val sustainabilityRepository: SustainabilityRepository,
    private val climateRepository: ClimateRepository,
    private val governanceRepository: GovernanceRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EsgPlatformUiState())
    val uiState: StateFlow<EsgPlatformUiState> = _uiState.asStateFlow()

    init {
        loadEsgPlatformData("THYAO.IS")
    }

    fun selectProvider(provider: EsgProviderType) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    private fun loadEsgPlatformData(symbol: String) {
        viewModelScope.launch {
            launch {
                esgRepository.getEsgScoreData(symbol).collect { score ->
                    _uiState.update { it.copy(esgScore = score, isLoading = false) }
                }
            }

            launch {
                climateRepository.getEnvironmentalPillar(symbol).collect { env ->
                    _uiState.update { it.copy(environmentalPillar = env) }
                }
            }

            launch {
                socialRepository.getSocialPillar(symbol).collect { soc ->
                    _uiState.update { it.copy(socialPillar = soc) }
                }
            }

            launch {
                governanceRepository.getGovernancePillar(symbol).collect { gov ->
                    _uiState.update { it.copy(governancePillar = gov) }
                }
            }

            launch {
                sustainabilityRepository.getControversyAlerts(symbol).collect { alerts ->
                    _uiState.update { it.copy(controversyAlerts = alerts) }
                }
            }
        }
    }
}
