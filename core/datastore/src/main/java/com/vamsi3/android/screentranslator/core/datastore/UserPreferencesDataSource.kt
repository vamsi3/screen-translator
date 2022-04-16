package com.vamsi3.android.screentranslator.core.datastore

import androidx.datastore.core.DataStore
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class UserPreferencesDataSource @Inject constructor(
    private val userPreferencesProto: DataStore<UserPreferencesProto>,
) {
    val userData = userPreferencesProto.data
        .map {
            UserData(
                useNotificationPanelSwipeUp = it.useNotificationPanelSwipeUp,
                notificationPanelSwipeUpDuration = it.notificationPanelSwipeUpDurationMilliseconds.milliseconds,
                themeMode = when(it.userThemePreference) {
                    null,
                    UserThemePreferenceProto.UNRECOGNIZED,
                    UserThemePreferenceProto.UNSPECIFIED,
                    UserThemePreferenceProto.SYSTEM -> ThemeMode.SYSTEM
                    UserThemePreferenceProto.DARK -> ThemeMode.DARK
                    UserThemePreferenceProto.LIGHT -> ThemeMode.LIGHT
                }
            )
        }
        .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, UserData(
            true,
            250.milliseconds,
            ThemeMode.SYSTEM
        ))

    suspend fun setUseNotificationPanelSwipeUp(useNotificationPanelSwipeUp: Boolean) {
        userPreferencesProto.updateData {
            it.copy {
                this.useNotificationPanelSwipeUp = useNotificationPanelSwipeUp
            }
        }
    }

    suspend fun setNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDuration: Duration) {
        userPreferencesProto.updateData {
            it.copy {
                this.notificationPanelSwipeUpDurationMilliseconds = notificationPanelSwipeUpDuration.inWholeMilliseconds.toInt()
            }
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        userPreferencesProto.updateData {
            it.copy {
                this.userThemePreference = when (themeMode) {
                    ThemeMode.SYSTEM -> UserThemePreferenceProto.SYSTEM
                    ThemeMode.LIGHT -> UserThemePreferenceProto.LIGHT
                    ThemeMode.DARK -> UserThemePreferenceProto.DARK
                }
            }
        }
    }
}
