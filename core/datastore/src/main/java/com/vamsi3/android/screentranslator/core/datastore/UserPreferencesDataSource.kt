package com.vamsi3.android.screentranslator.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
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
                notificationShadeCollapseDelayDuration = it.notificationShadeCollapseDelayMilliseconds.milliseconds,
                themeMode = when(it.userThemePreference) {
                    null,
                    UserThemePreferenceProto.UNRECOGNIZED,
                    UserThemePreferenceProto.THEME_UNSPECIFIED,
                    UserThemePreferenceProto.THEME_SYSTEM -> ThemeMode.SYSTEM
                    UserThemePreferenceProto.THEME_DARK -> ThemeMode.DARK
                    UserThemePreferenceProto.THEME_LIGHT -> ThemeMode.LIGHT
                },
                translateApp = when(it.userTranslateAppPreference) {
                    null,
                    UserTranslateAppPreferenceProto.UNRECOGNIZED,
                    UserTranslateAppPreferenceProto.TRANSLATE_APP_UNSPECIFIED,
                    UserTranslateAppPreferenceProto.TRANSLATE_APP_GOOGLE -> TranslateApp.GOOGLE

                    UserTranslateAppPreferenceProto.TRANSLATE_APP_DEEPL_TRANSLATE -> TranslateApp.DEEPL_TRANSLATE
                    UserTranslateAppPreferenceProto.TRANSLATE_APP_NAVER_PAPAGO -> TranslateApp.NAVER_PAPAGO
                },
            )
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    suspend fun setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration: Duration) {
        userPreferencesProto.updateData {
            it.copy {
                Log.i("ScreenTranslator", "updating to $notificationShadeCollapseDelayDuration")
                this.notificationShadeCollapseDelayMilliseconds = notificationShadeCollapseDelayDuration.inWholeMilliseconds.toInt()
            }
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        userPreferencesProto.updateData {
            it.copy {
                this.userThemePreference = when (themeMode) {
                    ThemeMode.SYSTEM -> UserThemePreferenceProto.THEME_SYSTEM
                    ThemeMode.LIGHT -> UserThemePreferenceProto.THEME_LIGHT
                    ThemeMode.DARK -> UserThemePreferenceProto.THEME_DARK
                }
            }
        }
    }

    suspend fun setTranslateApp(translateApp: TranslateApp) {
        userPreferencesProto.updateData {
            it.copy {
                this.userTranslateAppPreference = when (translateApp) {
                    TranslateApp.DEEPL_TRANSLATE -> UserTranslateAppPreferenceProto.TRANSLATE_APP_DEEPL_TRANSLATE
                    TranslateApp.GOOGLE -> UserTranslateAppPreferenceProto.TRANSLATE_APP_GOOGLE
                    TranslateApp.NAVER_PAPAGO -> UserTranslateAppPreferenceProto.TRANSLATE_APP_NAVER_PAPAGO
                }
            }
        }
    }
}
