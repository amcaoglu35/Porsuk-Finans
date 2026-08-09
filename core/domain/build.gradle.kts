plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.nexus.porsuk.core.domain"
}

dependencies {
    implementation(libs.java.inject)
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.ta4j:ta4j-core:0.18")

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
