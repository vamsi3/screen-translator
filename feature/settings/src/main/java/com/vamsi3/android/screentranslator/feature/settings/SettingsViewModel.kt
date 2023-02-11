package com.vamsi3.android.screentranslator.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.milliseconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .filter { it != null }
            .map { userData ->
                SettingsUiState.Success(
                    settingsData = SettingsData(
                        userData!!.themeMode,
                        userData.translateApp,
                        userData.notificationShadeCollapseDelayDuration.inWholeMilliseconds,
                    )
                )
            }
            .onEmpty {
                SettingsUiState.Success(
                    SettingsData(
                        ThemeMode.default,
                        TranslateApp.default,
                        0L,
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState.Loading
            )

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userDataRepository.setThemeMode(themeMode)
        }
    }

    fun setTranslateApp(translateApp: TranslateApp) {
        viewModelScope.launch {
            userDataRepository.setTranslateApp(translateApp)
        }
    }

    fun setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration: Long) {
        viewModelScope.launch {
            userDataRepository.setNotificationShadeCollapseDelayDuration(notificationShadeCollapseDelayDuration.milliseconds)
        }
    }
}

data class SettingsData(
    val themeMode: ThemeMode,
    val translateApp: TranslateApp,
    val notificationShadeCollapseDelayDuration: Long,
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settingsData: SettingsData) : SettingsUiState
}
