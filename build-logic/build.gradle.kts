plugins {
    `kotlin-dsl`
}

group = "com.vamsi3.android.screentranslator.gradle.plugin"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
//    implementation(libs.ksp.gradle.plugin)
}
