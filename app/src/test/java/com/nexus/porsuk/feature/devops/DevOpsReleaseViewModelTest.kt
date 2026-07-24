package com.nexus.porsuk.feature.devops

import com.nexus.porsuk.domain.model.*
import com.nexus.porsuk.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Porsuk Production Hardening & DevOps Platform — ViewModel Unit Testleri
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DevOpsReleaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeBuildRepository = object : BuildRepository {
        override fun getActiveEnvironmentInfo() = flowOf(BuildEnvironmentInfo(variant = BuildVariantType.PRODUCTION))
        override fun getBuildIntegrityStatus() = flowOf(BuildIntegrityStatus())
        override fun getPerformanceReport() = flowOf(PerformanceMetricsReport())
    }

    private val fakeReleaseRepository = object : ReleaseRepository {
        override fun getLatestReleaseNotes() = flowOf(ReleaseNotes())
        override fun getAvailableReleaseTracks() = ReleaseTrack.entries.toList()
        override suspend fun promoteReleaseToTrack(track: ReleaseTrack) {}
    }

    private val fakeVersionRepository = object : VersionRepository {
        override fun getCurrentVersion() = flowOf(SemanticVersion())
        override fun generateNextVersionName(incrementType: String) = "3.10.0"
    }

    private val fakeQualityRepository = object : QualityRepository {
        override fun getQualityGateMetrics() = flowOf(QualityGateMetrics(unitTestCoveragePct = 96.2))
        override suspend fun runQualityChecks() = QualityGateMetrics(unitTestCoveragePct = 96.2)
    }

    private val fakePipelineRepository = object : PipelineRepository {
        override fun getLatestPipelineRun() = flowOf(PipelineRunResult())
        override suspend fun triggerPipelineRun(variant: BuildVariantType) = flowOf(PipelineRunResult(status = PipelineStatus.SUCCESS))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDevOpsData updates uiState with environment and quality metrics`() = runTest {
        val viewModel = DevOpsReleaseViewModel(
            buildRepository = fakeBuildRepository,
            releaseRepository = fakeReleaseRepository,
            versionRepository = fakeVersionRepository,
            qualityRepository = fakeQualityRepository,
            pipelineRepository = fakePipelineRepository
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(BuildVariantType.PRODUCTION, state.environmentInfo.variant)
        assertEquals(96.2, state.qualityMetrics.unitTestCoveragePct, 0.1)
        assertEquals(false, state.isLoading)
        assertNotNull(state.currentVersion)
    }
}
