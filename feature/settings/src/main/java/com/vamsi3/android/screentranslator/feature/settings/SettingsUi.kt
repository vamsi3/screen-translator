package com.vamsi3.android.screentranslator.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState.Success

@ExperimentalLifecycleComposeApi
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (settingsUiState is Success) {
        SettingsContent(
            settingsData = (settingsUiState as Success).settingsData,
            onChangeThemeMode = viewModel::setThemeMode,
            onChangeTranslateApp = viewModel::setTranslateApp,
            onChangeNotificationShadeCollapseDelayDuration = viewModel::setNotificationShadeCollapseDelayDuration,
        )
    }
}

@Composable
fun SettingsContent(
    settingsData: SettingsData,
    onChangeThemeMode: (ThemeMode) -> Unit,
    onChangeTranslateApp: (TranslateApp) -> Unit,
    onChangeNotificationShadeCollapseDelayDuration: (Long) -> Unit,
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
        TranslateAppSetting(
            translateApp = settingsData.translateApp,
            onChangeTranslateApp = onChangeTranslateApp
        )
        NotificationShadeCollapseSetting(
            settingsData.notificationShadeCollapseDelayDuration,
            onChangeNotificationShadeCollapseDelayDuration,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSetting(
    themeMode: ThemeMode,
    onChangeThemeMode: (ThemeMode) -> Unit,
) {
    val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }

    ThemeModeDialog(
        openDialog = openDialog,
        setOpenDialog = setOpenDialog,
        themeMode = themeMode,
        onChangeThemeMode = onChangeThemeMode
    )

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = {
                    setOpenDialog(true)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(48.dp),
            ) {
                Text(themeMode.modeName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateAppSetting(
    translateApp: TranslateApp,
    onChangeTranslateApp: (TranslateApp) -> Unit,
) {
    val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }

    TranslateAppDialog(
        openDialog = openDialog,
        setOpenDialog = setOpenDialog,
        translateApp = translateApp,
        onChangeTranslateApp = onChangeTranslateApp
    )

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Translate App", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = {
                    setOpenDialog(true)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(48.dp),
            ) {
                Text(translateApp.appName)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ThemeModeDialog(
    openDialog: Boolean,
    setOpenDialog: (Boolean) -> Unit,
    themeMode: ThemeMode,
    onChangeThemeMode: (ThemeMode) -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = {
                setOpenDialog(false)
            },
            title = {
                Text(text = "Theme")
            },
            text = {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    ThemeMode.values().forEach { themeModeOption ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (themeModeOption == themeMode),
                                    onClick = { onChangeThemeMode(themeModeOption) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RadioButton(
                                selected = (themeModeOption == themeMode),
                                onClick = null
                            )
                            Text(
                                text = themeModeOption.modeName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun TranslateAppDialog(
    openDialog: Boolean,
    setOpenDialog: (Boolean) -> Unit,
    translateApp: TranslateApp,
    onChangeTranslateApp: (TranslateApp) -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = {
                setOpenDialog(false)
            },
            title = {
                Text(text = "Translate App")
            },
            text = {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    TranslateApp.values().forEach { translateAppOption ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (translateAppOption == translateApp),
                                    onClick = { onChangeTranslateApp(translateAppOption) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RadioButton(
                                selected = (translateAppOption == translateApp),
                                onClick = null
                            )
                            Text(
                                text = translateAppOption.appName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun NotificationShadeCollapseSetting(
    notificationShadeCollapseDelayDuration: Long,
    onChangeNotificationShadeCollapseDelayDuration: (Long) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        var notificationShadeCollapseDelayDurationState by remember { mutableStateOf(notificationShadeCollapseDelayDuration) }
        var isNotificationShadeCollapseEnabled by remember { mutableStateOf(notificationShadeCollapseDelayDurationState != 0L) }

        Column {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Notification Shade Delay", style = MaterialTheme.typography.titleMedium)
                Switch(
                    modifier = Modifier.padding(0.dp),
                    checked = isNotificationShadeCollapseEnabled,
                    onCheckedChange = {
                        isNotificationShadeCollapseEnabled = !isNotificationShadeCollapseEnabled
                        onChangeNotificationShadeCollapseDelayDuration(
                            if (isNotificationShadeCollapseEnabled) notificationShadeCollapseDelayDurationState else 0L
                        )
                    },
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Slider(
                    value = notificationShadeCollapseDelayDurationState.toFloat(),
                    onValueChange = { notificationShadeCollapseDelayDurationState = it.toLong() },
                    onValueChangeFinished = {
                        isNotificationShadeCollapseEnabled = notificationShadeCollapseDelayDurationState != 0L
                        onChangeNotificationShadeCollapseDelayDuration(
                            notificationShadeCollapseDelayDurationState
                        )
                    },
                    valueRange = 0f..900f,
                    steps = 8,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.6f),
                    enabled = isNotificationShadeCollapseEnabled
                )
                if (isNotificationShadeCollapseEnabled) {
                    Button(
                        enabled = false,
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .height(48.dp),
                    ) {
                        Text("$notificationShadeCollapseDelayDurationState ms")
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
                    translateApp = TranslateApp.GOOGLE_LENS,
                    notificationShadeCollapseDelayDuration = 300L,
                ),
                onChangeThemeMode = { },
                onChangeTranslateApp = { },
                onChangeNotificationShadeCollapseDelayDuration = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun ThemeModeDialogPreview() {
    ScreenTranslatorTheme {
        Surface {
            ThemeModeDialog(
                openDialog = true,
                setOpenDialog = {},
                themeMode = ThemeMode.SYSTEM,
                onChangeThemeMode = {}
            )
        }
    }
}
