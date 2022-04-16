plugins {
    id("screentranslator.build.android.application")
    id("screentranslator.build.android.application.compose")
    id("screentranslator.build.android.hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator"

    defaultConfig {
        applicationId = "com.vamsi3.android.screentranslator"
        versionCode = 1
        versionName = "0.0.1"
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
    implementation(project(":core:design"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:translate"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.appcompat)
}
