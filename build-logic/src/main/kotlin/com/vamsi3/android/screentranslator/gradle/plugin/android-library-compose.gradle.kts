package com.vamsi3.android.screentranslator.gradle.plugin

plugins {
    id("com.android.library")
//    id("com.vamsi3.android.screentranslator.gradle.plugin.android-compose")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.findVersion("androidxComposeCompiler").get().toString()
    }
}

dependencies {
    val composeBom = libs.findLibrary("androidx-compose-bom").get()
    add("implementation", platform(composeBom))
    add("androidTestImplementation", platform(composeBom))
}
