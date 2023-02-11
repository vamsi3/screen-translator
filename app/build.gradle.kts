plugins {
    id("screentranslator.build.android.application")
    id("screentranslator.build.android.application.compose")
    id("screentranslator.build.android.hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator"

    defaultConfig {
        applicationId = "com.vamsi3.android.screentranslator"
        versionCode = 5
        versionName = "0.0.5"
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:resource"))
    implementation(project(":core:ui"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:translate"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
}
