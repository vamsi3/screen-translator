package com.vamsi3.android.screentranslator.feature.translate

import android.app.Application
import android.content.ComponentName
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

internal fun isScreenTranslatorAccessibilityServiceEnabled(
    application: Application,
): Boolean {
    val enabledServices = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val screenTranslatorComponentName = ComponentName(application, ScreenTranslatorAccessibilityService::class.java)

    return enabledServices.split(':').stream()
        .map { componentNameString -> ComponentName.unflattenFromString(componentNameString) }
        .anyMatch { componentName -> componentName == screenTranslatorComponentName }
}
