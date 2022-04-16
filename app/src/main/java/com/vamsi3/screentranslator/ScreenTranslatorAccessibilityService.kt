package com.vamsi3.screentranslator

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.provider.Settings
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

const val FILE_PROVIDER_AUTHORITY = "com.vamsi3.screentranslator.fileProvider"
const val MIME_TYPE_JPEG = "image/jpeg"
const val PAPAGO_APP_PACKAGE_NAME = "com.naver.labs.translator"
const val PAPAGO_APP_INTENT_RECEIVER = "$PAPAGO_APP_PACKAGE_NAME.ui.main.DeepLinkActivity"
const val SCREENSHOT_FILE_NAME = "screenshot.jpg"

class ScreenTranslatorAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        swipeUpAndTakeScreenshot()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun swipeUpAndTakeScreenshot() {
        if (!isScreenTranslatorAccessibilityServiceEnabled()) {
            redirectToAccessibilitySettings()
            return
        }

        val path = Path().apply {
            val midX: Float = resources.displayMetrics.widthPixels * .5F
            val bottomY: Float = resources.displayMetrics.heightPixels * .95F
            val topY: Float = resources.displayMetrics.heightPixels * .05F

            moveTo(midX, bottomY)
            lineTo(midX, topY)
        }

        val gestureDescription =
            GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
                .build()

        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                takeScreenshotAndRedirectToPapago()
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                showToast(R.string.translation_failed)
            }
        }, null)
    }

    private fun isScreenTranslatorAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val screenTranslatorComponentName = ComponentName(application, this.javaClass)

        return enabledServices.split(':').stream()
            .map { componentNameString -> ComponentName.unflattenFromString(componentNameString) }
            .anyMatch { componentName -> componentName == screenTranslatorComponentName }
    }

    private fun redirectToAccessibilitySettings() {
        val intent =
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        showToast(R.string.enable_screen_translator)
    }

    private fun takeScreenshotAndRedirectToPapago() {
        takeScreenshot(
            Display.DEFAULT_DISPLAY,
            application.mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshotResult: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshotResult.hardwareBuffer,
                        screenshotResult.colorSpace
                    )
                    if (bitmap == null) {
                        showToast(R.string.translation_failed)
                        return
                    }

                    val file = writeBitmapToFile(bitmap)
                    redirectToPapago(file)
                }

                override fun onFailure(i: Int) {
                    showToast(R.string.translation_failed)
                }
            }
        )
    }

    private fun writeBitmapToFile(bitmap: Bitmap): File {
        val file = File(application.filesDir, SCREENSHOT_FILE_NAME)
        if (!file.exists()) file.createNewFile()

        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
        fileOutputStream.close()

        return file
    }

    private fun redirectToPapago(file: File) {
        val uri = FileProvider.getUriForFile(
            application,
            FILE_PROVIDER_AUTHORITY,
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage(PAPAGO_APP_PACKAGE_NAME)
            .setClassName(PAPAGO_APP_PACKAGE_NAME, PAPAGO_APP_INTENT_RECEIVER)
            .setDataAndTypeAndNormalize(uri, MIME_TYPE_JPEG)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.papago_app_not_found)
        }
    }

    private fun showToast(resourceId: Int) {
        Toast.makeText(application, resourceId, Toast.LENGTH_SHORT).show()
    }

}