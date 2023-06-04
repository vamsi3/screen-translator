package com.vamsi3.android.screentranslator.feature.translate

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.vamsi3.android.screentranslator.core.data.model.TranslateApp
import com.vamsi3.android.screentranslator.core.data.repository.UserDataRepository
import com.vamsi3.android.screentranslator.core.resource.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration

const val PACKAGE_ANDROID_SYSTEM_UI = "com.android.systemui"
const val MIME_TYPE_JPEG = "image/jpeg"

@AndroidEntryPoint
class ScreenTranslatorAccessibilityService : AccessibilityService(){

    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var userDataRepository: UserDataRepository

//    private var mAccessibilityButtonController: AccessibilityButtonController? = null
//    private var accessibilityButtonCallback: AccessibilityButtonController.AccessibilityButtonCallback? = null
//    private var mIsAccessibilityButtonAvailable: Boolean = false

    override fun onServiceConnected() {
//        mAccessibilityButtonController = accessibilityButtonController
////        mIsAccessibilityButtonAvailable = mAccessibilityButtonController?.isAccessibilityButtonAvailable ?: false
////
////        if (!mIsAccessibilityButtonAvailable) return
//
//        serviceInfo = serviceInfo.apply {
//            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
//        }
//
//        accessibilityButtonCallback =
//            object : AccessibilityButtonController.AccessibilityButtonCallback() {
//                override fun onClicked(controller: AccessibilityButtonController) {
//                    Log.i("ScreenTranslator", "Accessibility button pressed!")
//
//                    // Add custom logic for a service to react to the
//                    // accessibility button being pressed.
//                }
//
//                override fun onAvailabilityChanged(
//                    controller: AccessibilityButtonController,
//                    available: Boolean
//                ) {
//                    Log.i("ScreenTranslator", "Accessibility AvailabilityChanged!")
//                    if (controller == mAccessibilityButtonController) {
//                        mIsAccessibilityButtonAvailable = available
//                    }
//                }
//            }
//
//        accessibilityButtonCallback?.also {
//            mAccessibilityButtonController?.registerAccessibilityButtonCallback(it)
//        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        translateScreen()
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onInterrupt() {
        stopSelf()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

//        Log.i("ScreenTranslator", "event: $event")

        disableWindowStateChangedEvents()

        if (event.packageName.equals(PACKAGE_ANDROID_SYSTEM_UI) &&
            event.text.any { it.contains("Notification shade") }
        ) {
            takeScreenshotAndTranslate()
        }
    }

    private fun translateScreen() {
        when {
            !isScreenTranslatorAccessibilityServiceEnabled(application) -> {
                dismissNotificationShade()
                startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                showToast(R.string.enable_screen_translator)
            }
            else -> {
                enableWindowStateChangedEvents()
                dismissNotificationShade()
            }
        }
    }

    private fun dismissNotificationShade() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
            }
            else -> {
                val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                applicationContext.sendBroadcast(intent)
            }
        }

        Log.i("ScreenTranslator", "dismissNotificationShade: ${userDataRepository.userData.value?.notificationShadeCollapseDelayDuration}")

        val delay = userDataRepository.userData.value?.notificationShadeCollapseDelayDuration ?: Duration.ZERO
        Thread.sleep(delay.inWholeMilliseconds)
    }

    private fun enableWindowStateChangedEvents() {
        serviceInfo = serviceInfo.apply {
            eventTypes = eventTypes or AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = packageNames?.plusElement(PACKAGE_ANDROID_SYSTEM_UI) ?: arrayOf(PACKAGE_ANDROID_SYSTEM_UI)
            flags = flags or AccessibilityServiceInfo.DEFAULT
        }
    }

    private fun disableWindowStateChangedEvents() {
        serviceInfo = serviceInfo.apply {
            eventTypes = eventTypes and AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED.inv()
            packageNames = packageNames.copyOf(packageNames.lastIndex).ifEmpty { null }
            flags = flags and AccessibilityServiceInfo.DEFAULT.inv()
        }
    }

    private fun takeScreenshotAndTranslate() {
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
                    forwardScreenshotToTranslateApp(file)
                }

                override fun onFailure(i: Int) {
                    showToast(R.string.translation_failed)
                }
            }
        )
    }

    private fun writeBitmapToFile(bitmap: Bitmap): File {
        application
            .cacheDir
            .listFiles { _, filename -> filename.endsWith(".jpg") }
            ?.forEach { file -> file.delete() }

        val file = File(application.cacheDir, UUID.randomUUID().toString() + ".jpg")
                .apply { if (!exists()) createNewFile() }

        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
        fileOutputStream.close()

        return file
    }

    private fun forwardScreenshotToTranslateApp(file: File) {
        val translateApp = userDataRepository.userData.value?.translateApp ?: TranslateApp.default

        val uri = FileProvider.getUriForFile(
            application,
            application.applicationContext.packageName + ".fileProvider",
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
            suggestTranslateAppInstallation(translateApp)
        }
    }

    private fun suggestTranslateAppInstallation(translateApp: TranslateApp) {
        showToast("Please install ${translateApp.appName}")
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            startActivity(intent.apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${translateApp.packageName}")
                `package` = "com.android.vending"
            })
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(intent.apply {
                    data = Uri.parse("market://details?id=${translateApp.packageName}")
                    `package` = null
                })
            } catch (e: ActivityNotFoundException) {
                startActivity(intent.apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=${translateApp.packageName}")
                    `package` = null
                })
            }
        }
    }

    private fun showToast(resourceId: Int) {
        Toast.makeText(application, resourceId, Toast.LENGTH_LONG).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }
}
