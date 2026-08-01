plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "com.nexus.porsuk.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}