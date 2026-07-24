package com.nexus.porsuk.domain.repository

import com.nexus.porsuk.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 1. Derleme ve Ortam Yöneticisi Deposu Sözleşmesi (BuildRepository)
 */
interface BuildRepository {
    fun getActiveEnvironmentInfo(): Flow<BuildEnvironmentInfo>
    fun getBuildIntegrityStatus(): Flow<BuildIntegrityStatus>
    fun getPerformanceReport(): Flow<PerformanceMetricsReport>
}

/**
 * 2. Sürüm ve Dağıtım Deposu Sözleşmesi (ReleaseRepository)
 */
interface ReleaseRepository {
    fun getLatestReleaseNotes(): Flow<ReleaseNotes>
    fun getAvailableReleaseTracks(): List<ReleaseTrack>
    suspend fun promoteReleaseToTrack(track: ReleaseTrack)
}

/**
 * 3. Semantik Sürüm Yöneticisi Deposu Sözleşmesi (VersionRepository)
 */
interface VersionRepository {
    fun getCurrentVersion(): Flow<SemanticVersion>
    fun generateNextVersionName(incrementType: String): String
}

/**
 * 4. Statik Analiz ve Kalite Kapısı Deposu Sözleşmesi (QualityRepository)
 */
interface QualityRepository {
    fun getQualityGateMetrics(): Flow<QualityGateMetrics>
    suspend fun runQualityChecks(): QualityGateMetrics
}

/**
 * 5. CI/CD Otomasyon Pipeline Deposu Sözleşmesi (PipelineRepository)
 */
interface PipelineRepository {
    fun getLatestPipelineRun(): Flow<PipelineRunResult>
    suspend fun triggerPipelineRun(variant: BuildVariantType): Flow<PipelineRunResult>
}
