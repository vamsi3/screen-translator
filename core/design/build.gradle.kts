plugins {
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-library")
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-library-compose")
}

android {
    namespace = "com.vamsi3.android.screentranslator.core.design"
}


dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material3)

    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)

    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.runtime)
}
