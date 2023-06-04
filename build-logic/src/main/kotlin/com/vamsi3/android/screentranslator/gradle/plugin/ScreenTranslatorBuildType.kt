package com.vamsi3.android.screentranslator.gradle.plugin

enum class ScreenTranslatorBuildType(
    val applicationIdSuffix: String? = null,
) {
    DEBUG(".debug"),
    RELEASE,
}
