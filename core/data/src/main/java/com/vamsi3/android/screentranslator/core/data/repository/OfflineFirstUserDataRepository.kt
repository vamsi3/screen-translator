package com.vamsi3.android.screentranslator.core.data.repository

import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.datastore.UserPreferencesDataSource
import javax.inject.Inject
import kotlin.time.Duration

class OfflineFirstUserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserDataRepository {

    override val userData = userPreferencesDataSource.userData

    override suspend fun setUseNotificationPanelSwipeUp(useNotificationPanelSwipeUp: Boolean) =
        userPreferencesDataSource.setUseNotificationPanelSwipeUp(useNotificationPanelSwipeUp)

    override suspend fun setNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDuration: Duration) =
        userPreferencesDataSource.setNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDuration)

    override suspend fun setThemeMode(themeMode: ThemeMode) =
        userPreferencesDataSource.setThemeMode(themeMode)
}
