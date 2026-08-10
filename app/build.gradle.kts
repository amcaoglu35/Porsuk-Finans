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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.navigation.compose)
    implementation("org.ta4j:ta4j-core:0.18")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.java.inject)
    compileOnly(libs.javax.annotation.api)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.compose)
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
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.truth)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.espresso.core)
}