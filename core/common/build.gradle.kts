plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.nexus.porsuk.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.java.inject)
    implementation(libs.kotlinx.coroutines.android)
}
