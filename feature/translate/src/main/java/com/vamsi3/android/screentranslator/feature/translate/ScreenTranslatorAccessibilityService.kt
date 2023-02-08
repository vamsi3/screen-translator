package com.vamsi3.android.screentranslator.feature.translate

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.provider.Settings
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

const val FILE_PROVIDER_AUTHORITY = "com.vamsi3.android.screentranslator.fileProvider"
const val MIME_TYPE_JPEG = "image/jpeg"
const val SCREENSHOT_FILE_NAME = "screenshot.jpg"

@AndroidEntryPoint
class ScreenTranslatorAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        swipeUpAndTakeScreenshot()
        stopSelf(startId)
        return START_NOT_STICKY
    }

    private fun swipeUpAndTakeScreenshot() {
        if (!isScreenTranslatorAccessibilityServiceEnabled(application)) {
            redirectToAccessibilitySettings()
            return
        }

        val userData = userDataRepository.userData.value
        val useNotificationPanelSwipeUp = userData.useNotificationPanelSwipeUp
        val notificationPanelSwipeUpDuration = userData.notificationPanelSwipeUpDuration
            .inWholeMilliseconds.let { if (it <= 0L) 300L else it }

        if (!useNotificationPanelSwipeUp) {
            takeScreenshotAndRedirectToTranslateApp()
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
                .addStroke(GestureDescription.StrokeDescription(path, 0, notificationPanelSwipeUpDuration))
                .build()

        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                takeScreenshotAndRedirectToTranslateApp()
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                showToast(R.string.translation_failed)
            }
        }, null)
    }

    private fun redirectToAccessibilitySettings() {
        val intent =
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        showToast(R.string.enable_screen_translator)
    }

    private fun takeScreenshotAndRedirectToTranslateApp() {
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
                    redirectToTranslateApp(file)
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

    private fun redirectToTranslateApp(file: File) {
        val translateApp = userDataRepository.userData.value.translateApp

        val uri = FileProvider.getUriForFile(
            application,
            FILE_PROVIDER_AUTHORITY,
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
            .setType(MIME_TYPE_JPEG)
            .setPackage(translateApp.packageName)
            .setClassName(translateApp.packageName, translateApp.imageTranslateActivity)
//            .setDataAndTypeAndNormalize(uri, MIME_TYPE_JPEG)
            .setFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                        or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                        or Intent.FLAG_ACTIVITY_FORWARD_RESULT
                        or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            .putExtra(Intent.EXTRA_STREAM, uri)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
//            try {
//                val intent = Intent(
//                    this,
//                    Class.forName("com.vamsi3.android.screentranslator.MainActivity")
//                )
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            } catch (e: ClassNotFoundException) {
//                e.printStackTrace()
//            }
            showToast("${translateApp.appName} app not found")
        }
    }

    private fun showToast(resourceId: Int) {
        Toast.makeText(application, resourceId, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
    }
}
