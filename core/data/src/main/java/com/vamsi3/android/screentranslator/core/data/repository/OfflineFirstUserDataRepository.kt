package com.vamsi3.android.screentranslator.core.data.repository

import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.datastore.UserPreferencesDataSource
import javax.inject.Inject
import kotlin.time.Duration

class OfflineFirstUserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserDataRepository {

    override val userData = userPreferencesDataSource.userData

    override suspend fun setThemeMode(themeMode: ThemeMode) =
        userPreferencesDataSource.setThemeMode(themeMode)

    override suspend fun setTranslateApp(translateApp: TranslateApp) =
        userPreferencesDataSource.setTranslateApp(translateApp)

    override suspend fun setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration: Duration) =
        userPreferencesDataSource.setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration)

}
