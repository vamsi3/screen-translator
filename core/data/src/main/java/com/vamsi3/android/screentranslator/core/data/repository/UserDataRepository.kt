package com.vamsi3.android.screentranslator.core.data.repository

import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.data.model.UserData
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface UserDataRepository {
    val userData: StateFlow<UserData?>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setTranslateApp(translateApp: TranslateApp)

    suspend fun setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration: Duration)
}
