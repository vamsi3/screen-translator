package com.vamsi3.android.screentranslator.feature.translate

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ScreenTranslatorTileService : TileService() {
    override fun onClick() {
        super.onClick()

        val accessibilityPermissionGranted = isScreenTranslatorAccessibilityServiceEnabled(application)
        val previousTileState = qsTile.state
        qsTile.state = if (accessibilityPermissionGranted) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        if (qsTile.state != previousTileState) {
            qsTile.updateTile()
        }

        val intent = Intent(application, ScreenTranslatorAccessibilityService::class.java)
        startService(intent)
    }
}
