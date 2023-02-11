plugins {
    id("screentranslator.build.android.application")
    id("screentranslator.build.android.application.compose")
    id("screentranslator.build.android.hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator"

    defaultConfig {
        applicationId = "com.vamsi3.android.screentranslator"
        versionCode = 4
        versionName = "0.0.4"
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
    implementation(project(":core:ui"))
    implementation(project(":core:resource"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:translate"))
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.core.splashscreen)
}
