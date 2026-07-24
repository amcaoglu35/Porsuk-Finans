package com.nexus.porsuk.feature.devops

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.porsuk.domain.model.*

/**
 * Porsuk Production Hardening, DevOps & Release Platform — Ana Ekran (DevOpsReleaseScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOpsReleaseScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DevOpsReleaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DevOps & Release Platform", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.currentVersion.versionString} (${uiState.environmentInfo.variant.displayName})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Sekme Listesi (Tab Row)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(DevOpsTab.entries) { tab ->
                        FilterChip(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            label = { Text("${tab.iconEmoji} ${tab.displayName}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (uiState.selectedTab) {
                        DevOpsTab.BUILD_VARIANTS -> item {
                            BuildVariantsCard(
                                env = uiState.environmentInfo,
                                onRunPipeline = { viewModel.triggerPipelineBuild(it) }
                            )
                        }

                        DevOpsTab.CI_CD_PIPELINE -> item {
                            CiCdPipelineCard(
                                runResult = uiState.pipelineRun,
                                isRunning = uiState.isRunningPipeline,
                                onTrigger = { viewModel.triggerPipelineBuild(BuildVariantType.PRODUCTION) }
                            )
                        }

                        DevOpsTab.QUALITY_GATES -> item {
                            QualityGatesCard(
                                metrics = uiState.qualityMetrics,
                                isRunning = uiState.isRunningQualityCheck,
                                onRunCheck = { viewModel.runQualityChecks() }
                            )
                        }

                        DevOpsTab.RELEASE_TRACKS -> item {
                            ReleaseTracksCard(
                                releaseNotes = uiState.releaseNotes,
                                availableTracks = uiState.availableTracks,
                                onPromote = { viewModel.promoteReleaseToTrack(it) }
                            )
                        }

                        DevOpsTab.SECURITY_HARDENING -> item {
                            SecurityHardeningCard(integrity = uiState.integrityStatus)
                        }

                        DevOpsTab.PERFORMANCE -> item {
                            PerformanceProfilesCard(perf = uiState.performanceReport)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildVariantsCard(
    env: BuildEnvironmentInfo,
    onRunPipeline: (BuildVariantType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🛠️ Aktif Build Varyantı: ${env.variant.displayName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("API Endpoint: ${env.apiBaseUrl}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Debuggable: ${if (env.isDebuggable) "Evet (Debug)" else "Hayır (Release)"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Aktif Feature Flag Sayısı: ${env.featureFlagsCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            Text("Hızlı Derleme Başlat:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onRunPipeline(BuildVariantType.DEVELOPMENT) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Dev Build", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { onRunPipeline(BuildVariantType.PRODUCTION) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Prod AAB", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CiCdPipelineCard(
    runResult: PipelineRunResult,
    isRunning: Boolean,
    onTrigger: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚀 CI/CD Pipeline Otomasyonu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(runResult.status.colorHex).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = runResult.status.displayName,
                        color = Color(runResult.status.colorHex),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Aktif Aşama: ${runResult.currentStage.displayName}", style = MaterialTheme.typography.bodySmall)
            Text("Çıktı Artifact: ${runResult.generatedArtifactName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Test Başarı Oranı: %${runResult.passPercentage}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onTrigger,
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pipeline Çalışıyor...")
                } else {
                    Text("CI/CD Pipeline Çalıştır")
                }
            }
        }
    }
}

@Composable
private fun QualityGatesCard(
    metrics: QualityGateMetrics,
    isRunning: Boolean,
    onRunCheck: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎯 Statik Analiz & Kalite Kapıları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Detekt Hataları: ${metrics.detektIssuesCount}", style = MaterialTheme.typography.bodySmall)
            Text("Ktlint İhlalleri: ${metrics.ktlintViolationsCount}", style = MaterialTheme.typography.bodySmall)
            Text("Android Lint Uyarıları: ${metrics.androidLintWarningsCount}", style = MaterialTheme.typography.bodySmall)
            Text("Unit Test Kapsamı: %${metrics.unitTestCoveragePct} (Hedef: %90+)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onRunCheck,
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Kalite Kapısı Kontrolünü Yeniden Çalıştır")
            }
        }
    }
}

@Composable
private fun ReleaseTracksCard(
    releaseNotes: ReleaseNotes,
    availableTracks: List<ReleaseTrack>,
    onPromote: (ReleaseTrack) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📦 Sürüm ve Dağıtım Yönetimi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Aktif Kanal: ${releaseNotes.track.displayName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Sürüm Tarihi: ${releaseNotes.releaseDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Değişiklik Notları:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            releaseNotes.changelogHighlights.forEach { note ->
                Text("• $note", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Sürümü Başka Kanala Yükselt (Promote):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTracks) { track ->
                    AssistChip(
                        onClick = { onPromote(track) },
                        label = { Text(track.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityHardeningCard(integrity: BuildIntegrityStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🛡️ Derleme Güvenliği & OWASP MASVS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Release Imza Doğrulaması: ${if (integrity.isSignedWithReleaseKey) "Geçerli 🟢" else "Eksik 🔴"}", style = MaterialTheme.typography.bodySmall)
            Text("R8 Code Shrinking / Obfuscation: ${if (integrity.isR8ShrinkingEnabled) "Aktif 🟢" else "Devre Dışı"}", style = MaterialTheme.typography.bodySmall)
            Text("Resource Optimization: ${if (integrity.isResourceOptimized) "Aktif 🟢" else "Devre Dışı"}", style = MaterialTheme.typography.bodySmall)
            Text("SHA-256 Fingerprint: ${integrity.sha256Fingerprint.take(24)}...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PerformanceProfilesCard(perf: PerformanceMetricsReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("⚡ Baseline Profiles & Performans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Soğuk Başlatma (Cold Start): ${perf.appStartupTimeMs} ms", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("APK/AAB Boyutu: ${perf.apkSizeMb} MB", style = MaterialTheme.typography.bodySmall)
            Text("Ortalama Bellek Kullanımı: ${perf.memoryFootprintMb} MB", style = MaterialTheme.typography.bodySmall)
            Text("Baseline Profiles Durumu: ${if (perf.isBaselineProfileActive) "Aktif (Optimize) 🚀" else "Pasif"}", style = MaterialTheme.typography.bodySmall)
            Text("Macrobenchmark Skoru: ${perf.macrobenchmarkScore} / 100", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
