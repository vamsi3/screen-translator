package com.vamsi3.android.screentranslator.core.data.model

enum class ThemeMode(
    val modeName: String,
) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark");

    companion object {
        val default = SYSTEM
    }
}
