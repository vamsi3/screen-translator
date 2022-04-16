package com.vamsi3.screentranslator

import android.content.Intent
import android.service.quicksettings.TileService

class ScreenTranslatorTileService : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(application, ScreenTranslatorAccessibilityService::class.java)
        startService(intent)
    }

}