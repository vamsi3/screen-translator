package com.vamsi3.android.screentranslator.core.ui

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews
import com.vamsi3.android.screentranslator.core.resource.R
import com.vamsi3.android.screentranslator.feature.settings.SettingsData
import com.vamsi3.android.screentranslator.feature.settings.SettingsScreen
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState.Loading
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState.Success
import com.vamsi3.android.screentranslator.feature.settings.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by viewModel.uiState.collectAsStateWithLifecycle()


    when (settingsUiState) {
        Loading -> {
            Text(
                text = "Loading...",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        is Success -> {
            val darkTheme = when ((settingsUiState as Success).settingsData.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            ScreenTranslatorTheme(
                darkTheme = darkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val uriHandler = LocalUriHandler.current
                        val gitHubRepoUrl = stringResource(R.string.top_bar_favorite_url)

                        ScreenTranslatorTopAppBar(
                            titleRes = R.string.app_name,
                            actionIcon = Icons.Filled.Favorite,
                            actionIconContentDescription = stringResource(R.string.top_bar_favorite_description),
                            onActionClick = {
                                uriHandler.openUri(gitHubRepoUrl)
                            }
                        )

                        SettingsScreen()
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppPreview() {
    HomeScreen()
}
