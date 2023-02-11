plugins {
    id("screentranslator.build.android.library")
    id("screentranslator.build.android.library.compose")
    id("screentranslator.build.android.hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator.feature.translate"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:resource"))
    implementation(project(":feature:settings"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
}
