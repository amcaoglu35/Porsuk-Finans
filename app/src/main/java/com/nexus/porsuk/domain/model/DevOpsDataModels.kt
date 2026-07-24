package com.nexus.porsuk.domain.model

/**
 * 5 Derleme Varyant Türü (Build Variant Types)
 */
enum class BuildVariantType(val displayName: String, val isDebuggable: Boolean) {
    DEVELOPMENT("Development (Dev)", true),
    QA("Quality Assurance (QA)", true),
    STAGING("Staging / Pre-Prod", true),
    BETA("Beta Release", false),
    PRODUCTION("Production (Release)", false);
}

/**
 * Derleme Ortam Bilgileri (BuildEnvironmentInfo)
 */
data class BuildEnvironmentInfo(
    val variant: BuildVariantType = BuildVariantType.PRODUCTION,
    val apiBaseUrl: String = "https://api.porsuk.app/v1/",
    val isDebuggable: Boolean = false,
    val logLevel: String = "INFO",
    val featureFlagsCount: Int = 18,
    val isObfuscated: Boolean = true
)

/**
 * CI/CD Pipeline Aşamaları (PipelineStage)
 */
enum class PipelineStage(val displayName: String) {
    BUILD("Android Build & Compilation"),
    UNIT_TEST("Unit & ViewModel Tests"),
    STATIC_ANALYSIS("Static Analysis (Detekt & Ktlint)"),
    INTEGRATION_TEST("Integration & UI Tests"),
    SECURITY_SCAN("OWASP Security & Vulnerability Scan"),
    SIGNING("AAB/APK Release Signing"),
    DEPLOYMENT("Store & Internal Deployment");
}

/**
 * CI/CD Pipeline Durumu (PipelineStatus)
 */
enum class PipelineStatus(val displayName: String, val colorHex: Long) {
    IDLE("Hazır (Idle)", 0xFF757575),
    IN_PROGRESS("Çalışıyor ⚙️", 0xFFFFB300),
    SUCCESS("Başarılı 🟢", 0xFF00C853),
    FAILED("Başarısız 🔴", 0xFFD50000),
    CANCELLED("İptal Edildi ⚪", 0xFF9E9E9E);
}

/**
 * CI/CD Pipeline Çalıştırma Sonucu (PipelineRunResult)
 */
data class PipelineRunResult(
    val runId: String = "run_${System.currentTimeMillis()}",
    val variant: BuildVariantType = BuildVariantType.PRODUCTION,
    val status: PipelineStatus = PipelineStatus.SUCCESS,
    val currentStage: PipelineStage = PipelineStage.DEPLOYMENT,
    val durationMs: Long = 185000L,
    val passPercentage: Double = 98.5,
    val generatedArtifactName: String = "app-production-release.aab",
    val artifactSizeBytes: Long = 18450000L,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Semantik Sürüm Modeli (SemanticVersion)
 */
data class SemanticVersion(
    val major: Int = 3,
    val minor: Int = 9,
    val patch: Int = 0,
    val preReleaseSuffix: String? = null,
    val buildNumber: Int = 1042
) {
    val versionString: String
        get() = if (preReleaseSuffix.isNull_or_blank()) "$major.$minor.$patch" else "$major.$minor.$patch-$preReleaseSuffix"
}

private fun String?.isNull_or_blank() = this == null || this.trim().isEmpty()

/**
 * Sürüm Dağıtım Kanalı (ReleaseTrack)
 */
enum class ReleaseTrack(val displayName: String) {
    INTERNAL("Dahili Test (Internal)"),
    ALPHA("Kapalı Alpha (Alpha)"),
    BETA("Açık Beta (Beta)"),
    PRODUCTION("Üretim (Production)");
}

/**
 * Sürüm Notları ve Yayın Modeli (ReleaseNotes)
 */
data class ReleaseNotes(
    val version: SemanticVersion = SemanticVersion(),
    val track: ReleaseTrack = ReleaseTrack.PRODUCTION,
    val releaseDate: String = "24 Temmuz 2026",
    val changelogHighlights: List<String> = listOf(
        "Yeni Settings & Personalization Center eklendi.",
        "Production Hardening & Baseline Profiles optimizasyonu yapıldı.",
        "Güvenlik denetim günlüğü ve KeyStore koruması artırıldı."
    ),
    val rollbackVersionTarget: String = "v3.8.4",
    val isApprovedForRelease: Boolean = true
)

/**
 * Kalite Kapısı Metrikleri (QualityGateMetrics)
 */
data class QualityGateMetrics(
    val detektIssuesCount: Int = 0,
    val ktlintViolationsCount: Int = 0,
    val androidLintWarningsCount: Int = 2,
    val unitTestCoveragePct: Double = 94.5,
    val vulnerableDependenciesCount: Int = 0,
    val isQualityGatePassed: Boolean = true
)

/**
 * Performans ve Baseline Profiles Raporu (PerformanceMetricsReport)
 */
data class PerformanceMetricsReport(
    val appStartupTimeMs: Long = 210L, // Cold Start
    val apkSizeMb: Double = 17.6,
    val memoryFootprintMb: Double = 42.5,
    val isBaselineProfileActive: Boolean = true,
    val macrobenchmarkScore: Int = 96
)

/**
 * Derleme Bütünlüğü ve Güvenlik Sertifika Modeli (BuildIntegrityStatus)
 */
data class BuildIntegrityStatus(
    val isSignedWithReleaseKey: Boolean = true,
    val sha256Fingerprint: String = "A1:B2:C3:D4:E5:F6:78:90:12:34:56:78:90:AB:CD:EF:12:34:56:78",
    val isR8ShrinkingEnabled: Boolean = true,
    val isResourceOptimized: Boolean = true,
    val isSecretsObfuscated: Boolean = true
)

/**
 * Geleceğe Hazır DevOps Multi-Platform & Infrastructure Stub Modeli
 */
data class DevOpsFutureStubs(
    val isDesktopBuildReady: Boolean = true,
    val isWebBuildReady: Boolean = true,
    val isAutomatedStoreSubmissionActive: Boolean = false,
    val isCanaryReleaseEnabled: Boolean = false,
    val infrastructureAsCodeVersion: String = "v1.4.0 (Terraform/Docker)"
)
