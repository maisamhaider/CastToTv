package com.example.casttotv.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.example.casttotv.BuildConfig
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.dataclasses.FolderModel
import com.example.casttotv.dataclasses.Lang
import com.example.casttotv.dataclasses.Tabs
import com.example.casttotv.utils.Pref.getPrefs
import java.io.File
import java.util.*

var folder_path = ""
const val default = "default"
const val VIDEO = "VIDEO"
const val AUDIO = "AUDIO"
const val IMAGE = "IMAGE"
const val SLIDE = "SLIDE"
var playingFileModel = FileModel("", 0, "")
var singletonFolderModel = FolderModel("", "", "")
var playingFileName = ""
var playingFileCurrentPos = 0

const val LOCATION_PERMISSION_REQUEST_CODE = 1000
const val LATITUDE = "LATITUDE"
const val LONGITUDE = "LONGITUDE"


object MySingleton {

    var historyBookFavClose = ""
    var localeLanguage = "en"

    fun Context.changeTheme() {
        val dark = getPrefs(THEME_DARK, false)
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun Context.shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun Context.shareWithText(image: String) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "image/*"

        val photoURI = FileProvider.getUriForFile(this, "$packageName.provider",
            File(image)
        )
        share.putExtra(Intent.EXTRA_STREAM, photoURI)
        startActivity(Intent.createChooser(share, "share with"))
    }

    fun Context.funCopy(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun <T> Context.toastLong(messages: T) {
        Toast.makeText(this, messages.toString(), Toast.LENGTH_LONG).show()
    }

    fun <T> Context.toastShort(messages: T) {
        Toast.makeText(this, messages.toString(), Toast.LENGTH_SHORT).show()
    }

    fun Context.enablingWiFiDisplay() {
        try {
            startActivity(Intent("android.settings.WIFI_DISPLAY_SETTINGS"))
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            startActivity(Intent("android.settings.CAST_SETTINGS"))
            return
        } catch (exception1: Exception) {
            Toast.makeText(
                this,
                "Device not supported",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @ColorInt
    fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes =
            if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(this, colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

    val LANGUAGES: List<Lang> = listOf(
        (Lang("af", "Afrikaans")),
        (Lang("sq", "Albanian")),
        (Lang("am", "Amharic")),
        (Lang("ar", "Arabic")),
        (Lang("hy", "Armenian")),
        (Lang("az", "Azerbaijani")),
        (Lang("eu", "Basque")),
        (Lang("be", "Belarusian")),
        (Lang("bs", "Bosnian")),
        (Lang("bg", "bulgarian")),
        (Lang("ca", "Catalan")),
        (Lang("ceb", "Cebuano")),
        (Lang("zh", "Chinese")),
        (Lang("co", "Corsican")),
        (Lang("hr", "Croatian")),
        (Lang("cs", "Czech")),
        (Lang("da", "Danish")),
        (Lang("nl", "Dutch")),
        (Lang("en", "English")),
        (Lang("eo", "Esperanto")),
        (Lang("et", "Estonian")),
        (Lang("fi", "Finnish")),
        (Lang("fr", "French")),
        (Lang("gl", "Galician")),
        (Lang("ka", "Georgian")),
        (Lang("de", "German")),
        (Lang("el", "Greek")),
        (Lang("gu", "Gujarati")),
        (Lang("ht", "Haitian Creole")),
        (Lang("ha", "Hausa")),
        (Lang("haw", "Hawaiian")),
        (Lang("he", "Cebuano")),
        (Lang("hi", "Hindi")),
        (Lang("hmn", "Hmong")),
        (Lang("hu", "Hungarian")),
        (Lang("is", "Icelandic")),
        (Lang("ig", "igbo")),
        (Lang("in", "Indonesian")),
        (Lang("ga", "Irish")),
        (Lang("it", "Italian")),
        (Lang("ja", "Japanese")),
        (Lang("jv", "Javanese")),
        (Lang("kn", "Kannada")),
        (Lang("kk", "Kazakh")),
        (Lang("km", "Khmer")),
        (Lang("rw", "Kinyarwanda")),
        (Lang("ko", "Korean")),
        (Lang("ku", "Kurdish")),
        (Lang("ky", "Kyrgyz")),
        (Lang("lo", "Laothian")),
        (Lang("lv", "Latvian")),
        (Lang("lt", "Lithuanian")),
        (Lang("lb", "Luxembourgish")),
        (Lang("mk", "Macedonian")),
        (Lang("mg", "Malagasy")),
        (Lang("ms", "Malay")),
        (Lang("ml", "Malayalam")),
        (Lang("mt", "Maltese")),
        (Lang("mr", "Marathi")),
        (Lang("mn", "Mongolian")),
        (Lang("my", "Burmese")),
        (Lang("ne", "Nepali")),
        (Lang("no", "Norwegian")),
        (Lang("ny", "Nyanja (Chichewa)")),
        (Lang("or", "Odia (Oriya)")),
        (Lang("ps", "Pashto")),
        (Lang("fa", "Persian")),
        (Lang("pl", "Polish")),
        (Lang("pt", "Portuguese")),
        (Lang("pa", "Punjabi")),
        (Lang("ro", "Romanian")),
        (Lang("ru", "Russian")),
        (Lang("sm", "Samoan")),
        (Lang("gd", "Scottish Gaelic")),
        (Lang("sr", "Serbian")),
        (Lang("sn", "Shona")),
        (Lang("sd", "Sindhi")),
        (Lang("si", "Sinhala (Sinhalese)")),
        (Lang("sk", "Slovak")),
        (Lang("sl", "Slovenian")),
        (Lang("so", "Somali")),
        (Lang("es", "Spanish")),
        (Lang("su", "Sundanese")),
        (Lang("sw", "Swahili")),
        (Lang("sv", "Swedish")),
        (Lang("tl", "Filipino")),
        (Lang("tg", "Tajik")),
        (Lang("ta", "Tamil")),
        (Lang("tt", "Tatar")),
        (Lang("te", "Telugu")),
        (Lang("th", "Thai")),
        (Lang("tr", "Turkish")),
        (Lang("tk", "Turkmen")),
        (Lang("uk", "Ukrainian")),
        (Lang("ur", "Urdu")),
        (Lang("uz", "Uzbek")),
        (Lang("vi", "Vietnamese")),
        (Lang("cy", "Welsh")),
        (Lang("xh", "Xhosa")),
        (Lang("yo", "Yoruba")),
        (Lang("zu", "Zulu"))
    )


    fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        localeLanguage = language
        return createConfigurationContext(config)
    }

}