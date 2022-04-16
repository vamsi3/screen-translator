package com.vamsi3.android.screentranslator.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    settingsData = SettingsData(
                        themeMode = userData.themeMode,
                        useNotificationPanelSwipeUp = userData.useNotificationPanelSwipeUp,
                        notificationPanelSwipeUpDuration = userData.notificationPanelSwipeUpDuration.inWholeMilliseconds,
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SettingsUiState.Loading
            )

    fun setUseNotificationPanelSwipeUp(useNotificationPanelSwipeUp: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseNotificationPanelSwipeUp(useNotificationPanelSwipeUp)
        }
    }

    fun setNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDuration: Long) {
        viewModelScope.launch {
            userDataRepository.setNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDuration.milliseconds)
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userDataRepository.setThemeMode(themeMode)
        }
    }
}

data class SettingsData(
    val themeMode: ThemeMode,
    val useNotificationPanelSwipeUp: Boolean,
    val notificationPanelSwipeUpDuration: Long,
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settingsData: SettingsData) : SettingsUiState
}
