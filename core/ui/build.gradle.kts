plugins {
    id("screentranslator.build.android.library")
    id("screentranslator.build.android.library.compose")
}

android {
    namespace = "com.vamsi3.android.screentranslator.core.ui"
}

dependencies {
    implementation(project(":core:design"))
    implementation(project(":core:model"))
    implementation(project(":feature:settings"))

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)

    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)

    api(libs.androidx.compose.ui.util)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}
