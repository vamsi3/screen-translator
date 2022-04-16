package com.vamsi3.android.screentranslator.core.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.vamsi3.android.screentranslator.core.design.theme.ScreenTranslatorTheme
import com.vamsi3.android.screentranslator.core.design.util.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTranslatorTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector,
    actionIconContentDescription: String?,
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = titleRes),
                fontWeight = FontWeight.Bold,
            )
        },
        modifier = modifier,
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    )
}

@ThemePreviews
@Composable
private fun ScreenTranslatorTopAppBarPreview() {
    ScreenTranslatorTheme {
        Surface {
            ScreenTranslatorTopAppBar(
                titleRes = android.R.string.unknownName,
                actionIcon = Icons.Filled.Favorite,
                actionIconContentDescription = "Favorite",
            )
        }
    }
}
