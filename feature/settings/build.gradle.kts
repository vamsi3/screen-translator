plugins {
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-library")
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-library-compose")
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator.feature.settings"
}

dependencies {
    implementation(project(":core:design"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
}
