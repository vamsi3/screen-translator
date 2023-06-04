import com.vamsi3.android.screentranslator.gradle.plugin.ScreenTranslatorBuildType

plugins {
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-application")
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-application-compose")
    id("com.vamsi3.android.screentranslator.gradle.plugin.android-hilt")
}

android {
    namespace = "com.vamsi3.android.screentranslator"

    defaultConfig {
        applicationId = "com.vamsi3.android.screentranslator"
        versionCode = 6
        versionName = "0.0.6"
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file(System.getenv("RELEASE_STORE_FILE"))
//            storePassword = System.getenv("RELEASE_STORE_PASSWORD")
//            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
//            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
//        }
//    }

    buildTypes {
        debug {
            applicationIdSuffix = ScreenTranslatorBuildType.DEBUG.applicationIdSuffix
        }

        val release by getting {
            applicationIdSuffix = ScreenTranslatorBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

//            signingConfig = signingConfigs.getByName("release")
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
