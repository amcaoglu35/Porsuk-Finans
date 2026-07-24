package com.nexus.porsuk.feature.devops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Porsuk Production Hardening, DevOps & Release Platform — ViewModel
 *
 * CI/CD otomasyonu, sürüm yönetimi, kalite kapıları ve derleme güvenlik kontrollerini yönetir.
 */
@HiltViewModel
class DevOpsReleaseViewModel @Inject constructor(
    private val buildRepository: BuildRepository,
    private val releaseRepository: ReleaseRepository,
    private val versionRepository: VersionRepository,
    private val qualityRepository: QualityRepository,
    private val pipelineRepository: PipelineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevOpsReleaseUiState())
    val uiState: StateFlow<DevOpsReleaseUiState> = _uiState.asStateFlow()

    init {
        loadDevOpsData()
    }

    fun selectTab(tab: DevOpsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun triggerPipelineBuild(variant: BuildVariantType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningPipeline = true) }
            pipelineRepository.triggerPipelineRun(variant).collect { runResult ->
                _uiState.update {
                    it.copy(
                        pipelineRun = runResult,
                        isRunningPipeline = runResult.status == PipelineStatus.IN_PROGRESS
                    )
                }
            }
        }
    }

    fun runQualityChecks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningQualityCheck = true) }
            val updated = qualityRepository.runQualityChecks()
            _uiState.update { it.copy(qualityMetrics = updated, isRunningQualityCheck = false) }
        }
    }

    fun promoteReleaseToTrack(track: ReleaseTrack) {
        viewModelScope.launch {
            releaseRepository.promoteReleaseToTrack(track)
        }
    }

    private fun loadDevOpsData() {
        viewModelScope.launch {
            launch {
                buildRepository.getActiveEnvironmentInfo().collect { env ->
                    _uiState.update { it.copy(environmentInfo = env, isLoading = false) }
                }
            }

            launch {
                buildRepository.getBuildIntegrityStatus().collect { integrity ->
                    _uiState.update { it.copy(integrityStatus = integrity) }
                }
            }

            launch {
                buildRepository.getPerformanceReport().collect { perf ->
                    _uiState.update { it.copy(performanceReport = perf) }
                }
            }

            launch {
                releaseRepository.getLatestReleaseNotes().collect { rel ->
                    _uiState.update { it.copy(releaseNotes = rel) }
                }
            }

            launch {
                versionRepository.getCurrentVersion().collect { ver ->
                    _uiState.update { it.copy(currentVersion = ver) }
                }
            }

            launch {
                qualityRepository.getQualityGateMetrics().collect { qg ->
                    _uiState.update { it.copy(qualityMetrics = qg) }
                }
            }

            launch {
                pipelineRepository.getLatestPipelineRun().collect { pipe ->
                    _uiState.update { it.copy(pipelineRun = pipe) }
                }
            }
        }
    }
}
