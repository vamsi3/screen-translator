package com.vamsi3.android.screentranslator.core.data.model

enum class TranslateApp(
    val appName: String,
    val packageName: String,
    val imageTranslateActivity: String
) {
    GOOGLE_LENS("Google Lens", "com.google.ar.lens", "com.google.vr.apps.ornament.app.lens.LensLauncherActivity"),
    NAVER_PAPAGO("Naver Papago", "com.naver.labs.translator", "com.naver.labs.translator.ui.main.DeepLinkActivity"),
    DEEPL_TRANSLATE("DeepL Translate", "com.deepl.mobiletranslator", "com.deepl.mobiletranslator.MainActivity");

    companion object {
        val default = GOOGLE_LENS
    }
}
