package com.vamsi3.android.screentranslator.core.data.model

import kotlin.time.Duration

/**
 * Class summarizing user interest data
 */
data class UserData(
    val notificationShadeCollapseDelayDuration: Duration,
    val themeMode: ThemeMode,
    val translateApp: TranslateApp,
)
