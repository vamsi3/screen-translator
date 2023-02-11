package com.vamsi3.android.screentranslator

import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vamsi3.android.screentranslator.core.data.model.ThemeMode
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.ui.HomeScreen
import com.vamsi3.android.screentranslator.feature.translate.ScreenTranslatorTileService
import com.vamsi3.android.screentranslator.core.resource.R
import com.vamsi3.android.screentranslator.feature.settings.SettingsData
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState.Loading
import com.vamsi3.android.screentranslator.feature.settings.SettingsUiState.Success
import com.vamsi3.android.screentranslator.feature.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        var uiState: SettingsUiState by mutableStateOf(Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .collectLatest {
                        uiState = it
                    }
            }
        }

        splashScreen.setKeepOnScreenCondition { uiState == Loading }

        setContent {
            HomeScreen()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val statusBarManager = getSystemService(StatusBarManager::class.java)

            statusBarManager.requestAddTileService(
                ComponentName(
                    this,
                    ScreenTranslatorTileService::class.java,
                ),
                getString(R.string.translate_tile_label),
                Icon.createWithResource(this, R.drawable.ic_translate_tile),
                {},
                {}
            )
        }
    }
}
