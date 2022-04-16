package com.vamsi3.android.screentranslator.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews

@ExperimentalLifecycleComposeApi
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    when (settingsUiState) {
        SettingsUiState.Loading -> {
            Text(
                text = stringResource(R.string.loading),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        is SettingsUiState.Success -> {
            SettingsContent(
                settingsData = (settingsUiState as SettingsUiState.Success).settingsData,
                onChangeThemeMode = viewModel::setThemeMode,
                onChangeUseNotificationPanelSwipeUp = viewModel::setUseNotificationPanelSwipeUp,
                onChangeNotificationPanelSwipeUpDuration = viewModel::setNotificationPanelSwipeUpDuration,
            )
        }
    }
}

@Composable
fun SettingsContent(
    settingsData: SettingsData,
    onChangeThemeMode: (themeMode: ThemeMode) -> Unit,
    onChangeUseNotificationPanelSwipeUp: (useNotificationPanelSwipeUp: Boolean) -> Unit,
    onChangeNotificationPanelSwipeUpDuration: (notificationPanelSwipeUpDuration: Long) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        ThemeSetting(
            themeMode = settingsData.themeMode,
            onChangeThemeMode = onChangeThemeMode
        )
        NotificationPanelSwipeUpSetting(
            settingsData.useNotificationPanelSwipeUp,
            onChangeUseNotificationPanelSwipeUp,
            settingsData.notificationPanelSwipeUpDuration,
            onChangeNotificationPanelSwipeUpDuration
        )
    }
}

@Composable
fun ThemeSetting(
    themeMode: ThemeMode,
    onChangeThemeMode: (themeMode: ThemeMode) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(

        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Theme", style = MaterialTheme.typography.titleMedium)
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                if (themeMode == ThemeMode.SYSTEM) {
                    Button(onClick = { onChangeThemeMode(ThemeMode.SYSTEM) }) { Text(stringResource(R.string.theme_mode_system_default), fontWeight = FontWeight.SemiBold) }
                } else {
                    FilledTonalButton(onClick = { onChangeThemeMode(ThemeMode.SYSTEM) }) { Text(stringResource(R.string.theme_mode_system_default), fontWeight = FontWeight.SemiBold) }
                }
                if (themeMode == ThemeMode.LIGHT) {
                    Button(onClick = { onChangeThemeMode(ThemeMode.LIGHT) }) { Text(stringResource(R.string.theme_mode_light), fontWeight = FontWeight.SemiBold) }
                } else {
                    FilledTonalButton(onClick = { onChangeThemeMode(ThemeMode.LIGHT) }) { Text(stringResource(R.string.theme_mode_light), fontWeight = FontWeight.SemiBold) }
                }
                if (themeMode == ThemeMode.DARK) {
                    Button(onClick = { onChangeThemeMode(ThemeMode.DARK) }) { Text(stringResource(R.string.theme_mode_dark), fontWeight = FontWeight.SemiBold) }
                } else {
                    FilledTonalButton(onClick = { onChangeThemeMode(ThemeMode.DARK) }) { Text(stringResource(R.string.theme_mode_dark), fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable
fun NotificationPanelSwipeUpSetting(
    useNotificationPanelSwipeUp: Boolean,
    onChangeUseNotificationPanelSwipeUp: (useNotificationPanelSwipeUp: Boolean) -> Unit,
    notificationPanelSwipeUpDuration: Long,
    onChangeNotificationPanelSwipeUpDuration: (notificationPanelSwipeUpDuration: Long) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(

        ) {
            var notificationPanelSwipeUpDurationState by remember { mutableStateOf(notificationPanelSwipeUpDuration) }

            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Notification Panel Swipe", style = MaterialTheme.typography.titleMedium)
                Switch(
                    modifier = Modifier.padding(0.dp),
                    checked = useNotificationPanelSwipeUp,
                    onCheckedChange = onChangeUseNotificationPanelSwipeUp,
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Slider(
                    value = notificationPanelSwipeUpDurationState.toFloat(),
                    onValueChange = { notificationPanelSwipeUpDurationState = it.toLong() },
                    onValueChangeFinished = { onChangeNotificationPanelSwipeUpDuration(notificationPanelSwipeUpDurationState) },
                    valueRange = 100f..1000f,
                    steps = 8,
                    modifier = Modifier.padding(vertical=16.dp).fillMaxWidth(0.6f),
                    enabled = useNotificationPanelSwipeUp
                )
                Surface(
                    tonalElevation = 12.dp,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .width(96.dp)
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (useNotificationPanelSwipeUp) {
                            Text("$notificationPanelSwipeUpDurationState ms")
                        }
                    }
                }
            }
        }

    }
}

@ThemePreviews
@Composable
private fun SettingsPreview() {
    ScreenTranslatorTheme {
        Surface {
            SettingsContent(
                settingsData = SettingsData(
                    themeMode = ThemeMode.SYSTEM,
                    useNotificationPanelSwipeUp = true,
                    notificationPanelSwipeUpDuration = 250,
                ),
                onChangeThemeMode = { },
                onChangeUseNotificationPanelSwipeUp = { },
                onChangeNotificationPanelSwipeUpDuration = { },
            )
        }
    }
}
