package com.vamsi3.android.screentranslator.feature.translate

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
import android.view.accessibility.AccessibilityNodeInfo
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
import kotlin.time.DurationUnit
import kotlin.time.toDuration


const val PACKAGE_ANDROID_SYSTEM_UI = "com.android.systemui"
const val MIME_TYPE_JPEG = "image/jpeg"

@AndroidEntryPoint
class ScreenTranslatorAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var userDataRepository: UserDataRepository

//    private var mAccessibilityButtonController: AccessibilityButtonController? = null
//    private var accessibilityButtonCallback: AccessibilityButtonController.AccessibilityButtonCallback? = null
//    private var mIsAccessibilityButtonAvailable: Boolean = false

    override fun onServiceConnected() {
        Log.i(
            "ScreenTranslatorAccessibilityService",
            "canRetrieveWindowContent: ${serviceInfo.canRetrieveWindowContent}"
        )

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.DEFAULT
        }

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

        Log.i("ScreenTranslatorAccessibilityService", "event: $event")

        val translateApp = userDataRepository.userData.value?.translateApp ?: TranslateApp.default

        if (event.packageName.equals(PACKAGE_ANDROID_SYSTEM_UI) &&
            event.text.any { it.contains("Notification shade") }
        ) {
            disableEvents()
            if (translateApp == TranslateApp.GOOGLE) {
                enableEventsForPackage(translateApp.packageName)
            }
            takeScreenshotAndTranslate()
        }

        if (event.packageName.equals(translateApp.packageName)) {
            if (translateApp == TranslateApp.GOOGLE) {
                val translateNode = rootInActiveWindow?.findAccessibilityNodeInfosByText("Translate")
                    ?.find { it.contentDescription == "Switch to Translate mode" }
                translateNode?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            disableEvents()
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
                enableEventsForPackage(PACKAGE_ANDROID_SYSTEM_UI)
                dismissNotificationShade()
            }
        }
    }

    private fun dismissNotificationShade() {
        performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)

        val delay = userDataRepository.userData.value?.notificationShadeCollapseDelayDuration
            ?: Duration.ZERO
        Thread.sleep(delay.inWholeMilliseconds)
    }

    private fun enableEventsForPackage(packageName: String) {
        Log.i(
            "ScreenTranslatorAccessibilityService",
            "before enableEventsForPackage | serviceInfo: $serviceInfo"
        )
        serviceInfo = serviceInfo.apply {
            eventTypes = eventTypes or AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = packageNames?.plusElement(packageName) ?: arrayOf(packageName)
        }
        Log.i(
            "ScreenTranslatorAccessibilityService",
            "after enableEventsForPackage | serviceInfo: $serviceInfo"
        )
    }

    private fun disableEvents() {
        Log.i(
            "ScreenTranslatorAccessibilityService",
            "before disableEvents | serviceInfo: $serviceInfo"
        )
        serviceInfo = serviceInfo.apply {
            eventTypes = eventTypes and AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED.inv()
            packageNames = null
        }
        Log.i(
            "ScreenTranslatorAccessibilityService",
            "after disableEvents | serviceInfo: $serviceInfo"
        )
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
            applicationContext,
            application.applicationContext.packageName + ".fileProvider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
            .setType(MIME_TYPE_JPEG)
            .setPackage(translateApp.packageName)
            .setClassName(translateApp.packageName, translateApp.imageTranslateActivity)
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
                data =
                    Uri.parse("https://play.google.com/store/apps/details?id=${translateApp.packageName}")
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
                    data =
                        Uri.parse("https://play.google.com/store/apps/details?id=${translateApp.packageName}")
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
