package com.vamsi3.android.screentranslator.gradle.plugin

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.vamsi3.android.screentranslator.gradle.plugin.kotlin-android")
}

kotlin {
    jvmToolchain(17)
}

android {
    defaultConfig {
        targetSdk = 33
    }
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        // Treat all Kotlin warnings as errors (disabled by default)
        // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
        val warningsAsErrors: String? by project
        allWarningsAsErrors = warningsAsErrors.toBoolean()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            // Enable experimental coroutines APIs, including Flow
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlin.Experimental",
            "-opt-in=kotlin.time.ExperimentalTime",
        )
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
}
