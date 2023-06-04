package com.vamsi3.android.screentranslator.gradle.plugin

plugins {
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.kapt")
}

kapt {
    correctErrorTypes = true
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    "implementation"(libs.findLibrary("hilt.android").get())
    "testImplementation"(libs.findLibrary("hilt.android.testing").get())
    "kapt"(libs.findLibrary("hilt.compiler").get())
    "kaptAndroidTest"(libs.findLibrary("hilt.compiler").get())
}
