package com.vamsi3.android.screentranslator.core.design.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vamsi3.android.screentranslator.core.design.theme.LocalBackgroundTheme
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews

@Composable
fun ScreenTranslatorBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation
    Surface(
        color = if (color == Color.Unspecified) Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}

@ThemePreviews
@Composable
private fun ScreenTranslatorBackgroundPreview() {
    ScreenTranslatorTheme(useDynamicTheme = false) {
        ScreenTranslatorBackground(Modifier.size(100.dp), content = {})
    }
}
