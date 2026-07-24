package com.nexus.porsuk.feature.devops

import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Production Hardening & DevOps Platform — UI Ekran Durumu (DevOpsReleaseUiState)
 */
data class DevOpsReleaseUiState(
    val selectedTab: DevOpsTab = DevOpsTab.BUILD_VARIANTS,
    val environmentInfo: BuildEnvironmentInfo = BuildEnvironmentInfo(),
    val integrityStatus: BuildIntegrityStatus = BuildIntegrityStatus(),
    val performanceReport: PerformanceMetricsReport = PerformanceMetricsReport(),
    val releaseNotes: ReleaseNotes = ReleaseNotes(),
    val currentVersion: SemanticVersion = SemanticVersion(),
    val qualityMetrics: QualityGateMetrics = QualityGateMetrics(),
    val pipelineRun: PipelineRunResult = PipelineRunResult(),
    val availableTracks: List<ReleaseTrack> = ReleaseTrack.entries.toList(),
    val futureStubs: DevOpsFutureStubs = DevOpsFutureStubs(),
    val isRunningPipeline: Boolean = false,
    val isRunningQualityCheck: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

enum class DevOpsTab(val displayName: String, val iconEmoji: String) {
    BUILD_VARIANTS("Build Variants", "🛠️"),
    CI_CD_PIPELINE("CI/CD Pipeline", "🚀"),
    QUALITY_GATES("Quality Gates", "🎯"),
    RELEASE_TRACKS("Release Tracks", "📦"),
    SECURITY_HARDENING("Security & MASVS", "🛡️"),
    PERFORMANCE("Baseline & Macro", "⚡");
}
