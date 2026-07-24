package com.nexus.porsuk.data.repository

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildRepositoryImpl @Inject constructor() : BuildRepository {
    private val environmentState = MutableStateFlow(BuildEnvironmentInfo())
    private val integrityState = MutableStateFlow(BuildIntegrityStatus())
    private val performanceState = MutableStateFlow(PerformanceMetricsReport())

    override fun getActiveEnvironmentInfo(): Flow<BuildEnvironmentInfo> = environmentState.asStateFlow()

    override fun getBuildIntegrityStatus(): Flow<BuildIntegrityStatus> = integrityState.asStateFlow()

    override fun getPerformanceReport(): Flow<PerformanceMetricsReport> = performanceState.asStateFlow()
}

@Singleton
class ReleaseRepositoryImpl @Inject constructor() : ReleaseRepository {
    private val releaseNotesState = MutableStateFlow(ReleaseNotes())

    override fun getLatestReleaseNotes(): Flow<ReleaseNotes> = releaseNotesState.asStateFlow()

    override fun getAvailableReleaseTracks(): List<ReleaseTrack> = ReleaseTrack.entries.toList()

    override suspend fun promoteReleaseToTrack(track: ReleaseTrack) {
        releaseNotesState.value = releaseNotesState.value.copy(track = track)
    }
}

@Singleton
class VersionRepositoryImpl @Inject constructor() : VersionRepository {
    private val versionState = MutableStateFlow(SemanticVersion())

    override fun getCurrentVersion(): Flow<SemanticVersion> = versionState.asStateFlow()

    override fun generateNextVersionName(incrementType: String): String {
        val current = versionState.value
        return when (incrementType.uppercase()) {
            "MAJOR" -> "${current.major + 1}.0.0"
            "MINOR" -> "${current.major}.${current.minor + 1}.0"
            else -> "${current.major}.${current.minor}.${current.patch + 1}"
        }
    }
}

@Singleton
class QualityRepositoryImpl @Inject constructor() : QualityRepository {
    private val qualityMetricsState = MutableStateFlow(QualityGateMetrics())

    override fun getQualityGateMetrics(): Flow<QualityGateMetrics> = qualityMetricsState.asStateFlow()

    override suspend fun runQualityChecks(): QualityGateMetrics {
        delay(500) // Simüle edilmiş statik analiz kontrolü
        val updated = QualityGateMetrics(
            detektIssuesCount = 0,
            ktlintViolationsCount = 0,
            androidLintWarningsCount = 1,
            unitTestCoveragePct = 96.2,
            vulnerableDependenciesCount = 0,
            isQualityGatePassed = true
        )
        qualityMetricsState.value = updated
        return updated
    }
}

@Singleton
class PipelineRepositoryImpl @Inject constructor() : PipelineRepository {
    private val pipelineState = MutableStateFlow(PipelineRunResult())

    override fun getLatestPipelineRun(): Flow<PipelineRunResult> = pipelineState.asStateFlow()

    override suspend fun triggerPipelineRun(variant: BuildVariantType): Flow<PipelineRunResult> = flow {
        emit(pipelineState.value.copy(status = PipelineStatus.IN_PROGRESS, currentStage = PipelineStage.BUILD))
        delay(400)
        emit(pipelineState.value.copy(status = PipelineStatus.IN_PROGRESS, currentStage = PipelineStage.UNIT_TEST))
        delay(400)
        emit(pipelineState.value.copy(status = PipelineStatus.IN_PROGRESS, currentStage = PipelineStage.STATIC_ANALYSIS))
        delay(400)
        emit(pipelineState.value.copy(status = PipelineStatus.IN_PROGRESS, currentStage = PipelineStage.SECURITY_SCAN))
        delay(400)
        val finalResult = PipelineRunResult(
            variant = variant,
            status = PipelineStatus.SUCCESS,
            currentStage = PipelineStage.DEPLOYMENT,
            passPercentage = 100.0,
            timestamp = System.currentTimeMillis()
        )
        pipelineState.value = finalResult
        emit(finalResult)
    }
}
