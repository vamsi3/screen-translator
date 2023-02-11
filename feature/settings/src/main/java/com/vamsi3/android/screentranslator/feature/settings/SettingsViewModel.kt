package com.vamsi3.android.screentranslator.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    settingsData = SettingsData(
                        themeMode = userData.themeMode,
                        translateApp = userData.translateApp,
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
}

data class SettingsData(
    val themeMode: ThemeMode,
    val translateApp: TranslateApp,
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settingsData: SettingsData) : SettingsUiState
}
