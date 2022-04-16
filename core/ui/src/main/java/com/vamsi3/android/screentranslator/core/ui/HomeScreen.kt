package com.vamsi3.android.screentranslator.core.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews
import com.vamsi3.android.screentranslator.feature.settings.SettingsScreen
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState
import com.vamsi3.android.screentranslator.feature.settings.SettingsViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    when (settingsUiState) {
        SettingsUiState.Loading -> {
            Text(
                text = "Loading...",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        is SettingsUiState.Success -> {
            val darkTheme = when ((settingsUiState as SettingsUiState.Success).settingsData.themeMode) {
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
