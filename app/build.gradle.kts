import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun getRequiredProperty(key: String): String {
    val value = localProperties.getProperty(key)?.trim()
    if (value.isNullOrBlank()) {
        throw GradleException("Missing $key in local.properties")
    }
    return value
}

plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.android.application.compose)
    alias(libs.plugins.app.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.app.kotlin.serialization)
}

android {
    namespace = "com.nexus.porsuk"

    defaultConfig {
        applicationId = "com.nexus.porsuk"
        versionCode = 1
        versionName = "1.0.0"

        val finnhubKey = getRequiredProperty("FINNHUB_API_KEY")
        val fmpKey = getRequiredProperty("FMP_API_KEY")
        val newsApiKey = getRequiredProperty("NEWS_API_KEY")
        val exchangeRateKey = getRequiredProperty("EXCHANGE_RATE_API_KEY")
        val fredKey = getRequiredProperty("FRED_API_KEY")
        val yahooKey = getRequiredProperty("YAHOO_RAPIDAPI_KEY")

        buildConfigField("String", "FINNHUB_API_KEY", "\"$finnhubKey\"")
        buildConfigField("String", "FMP_API_KEY", "\"$fmpKey\"")
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "EXCHANGE_RATE_API_KEY", "\"$exchangeRateKey\"")
        buildConfigField("String", "FRED_API_KEY", "\"$fredKey\"")
        buildConfigField("String", "YAHOO_RAPIDAPI_KEY", "\"$yahooKey\"")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/ui-tooling_release.kotlin_module",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            )
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.feature.sample)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.java.inject)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.activity.compose)

    implementation(libs.coil.compose)
    implementation(libs.google.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.jsoup)
    implementation(libs.google.ai.generativeai)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.compose.markdown)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.leakcanary)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
}