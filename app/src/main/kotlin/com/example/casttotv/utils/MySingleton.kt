package com.example.casttotv.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.TypedValue
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.casttotv.dataclasses.FileModel
import com.example.casttotv.dataclasses.FolderModel
import com.example.casttotv.dataclasses.Lang
import com.example.casttotv.dataclasses.Tabs
import com.example.casttotv.utils.Pref.getPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


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

const val WHITE_LIST_TYPE = "WHITE_LIST_TYPE"
const val WHITE_LIST_ADBLOCK = "WHITE_LIST_ADBLOCK"
const val WHITE_LIST_JAVASCRIPT = "WHITE_LIST_JAVASCRIPT"
const val WHITE_LIST_COOKIES = "WHITE_LIST_COOKIES"

const val BAIDU_ENGINE_URL = "https://www.baidu.com/s?wd="
const val BING_ENGINE_URL = "https://www.bing.com/search?q="
const val DUCKDUCKGO_ENGINE_URL = "https://duckduckgo.com/?q="
const val GOOGLE_ENGINE_URL = "https://www.google.com/search?q="
const val SEARX_ENGINE_URL = "https://searx.github.io/searx/search.html?q="
const val QWANT_ENGINE_URL = "https://www.qwant.com/?q="
const val ECOSIA_ENGINE_URL = "https://www.ecosia.org/search?q="

object MySingleton {
    var LANGUAGE_DIALOG_SHOWING = false
    var localeLanguage = "en"
    var tabs: MutableList<Tabs> = ArrayList<Tabs>()

//    fun Context.themeDark(): Boolean = getPrefs(THEME_DARK, false)

    fun Context.themeDark(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false

        }
    }


    fun Context.changeTheme() {
        val dark = getPrefs(THEME_DARK, false)
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun Context.shareWithText(image: String) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "image/*"

        val photoURI = FileProvider.getUriForFile(this, "$packageName.provider",
            File(image)
        )
        share.putExtra(Intent.EXTRA_STREAM, photoURI)
//        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(share, "share with"))

    }

    fun Context.funCopy(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun Context.createWebPrintJob(webView: WebView) {
        val printManager = this
            .getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter: PrintDocumentAdapter?
        printAdapter = webView.createPrintDocumentAdapter("MyDocument")
        val jobName = " Print Test"
        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
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

    fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

    val listOfLanguages: ArrayList<Lang> = ArrayList()

    private val LANGUAGES_CODES_ONLINE = listOf(
        "af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "zh-CN",
        "zh-TW", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "fi", "fr", "fy", "gl", "ka",
        "de", "el", "gu", "ht", "ha", "haw", "he", "hi", "hmn", "hu", "is", "ig", "id", "ga",
        "it", "ja", "jv", "kn", "kk", "km", "rw", "ko", "ku", "ky", "lo", "lv", "lt", "lb",
        "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "ny", "or", "ps",
        "fa", "pl", "pt", "pa", "ro", "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk",
        "sl", "so", "es", "su", "sw", "sv", "tl", "tg", "ta", "tt", "te", "th", "tr", "tk",
        "uk", "ur", "ug", "uz", "vi", "cy", "xh", "yi", "yo", "zu"
    )
    private val LANGUAGES_NAMES_ONLINE = listOf(
        "Afrikaans",
        "Albanian",
        "Amharic",
        "Arabic",
        "Armenian",
        "Azerbaijani",
        "Basque",
        "Belarusian",
        "Bengali",
        "Bosnian",
        "Bulgarian",
        "Catalan",
        "Cebuano",
        "Chinese (Simplified)",
        "Chinese (Traditional)",
        "Corsican",
        "Croatian",
        "Czech",
        "Danish",
        "Dutch",
        "English",
        "Esperanto",
        "Estonian",
        "Finnish",
        "French",
        "Frisian",
        "Galician",
        "Georgian",
        "German",
        "Greek",
        "Gujarati",
        "Haitian Creole",
        "Hausa",
        "Hawaiian",
        "Hebrew",
        "Hindi",
        "Hmong",
        "Hungarian",
        "Icelandic",
        "Igbo",
        "Indonesian",
        "Irish",
        "Italian",
        "Japanese",
        "Javanese",
        "Kannada",
        "Kazakh",
        "Khmer",
        "Kinyarwanda",
        "Korean",
        "Kurdish",
        "Kyrgyz",
        "Lao",
        "Latvian",
        "Lithuanian",
        "Luxembourgish",
        "Macedonian",
        "Malagasy",
        "Malay",
        "Malayalam",
        "Maltese",
        "Maori",
        "Marathi",
        "Mongolian",
        "Myanmar (Burmese)",
        "Nepali",
        "Norwegian",
        "Nyanja (Chichewa)",
        "Odia (Oriya)",
        "Pashto",
        "Persian",
        "Polish",
        "Portuguese (Portugal, Brazil)",
        "Punjabi",
        "Romanian",
        "Russian",
        "Samoan",
        "Scots Gaelic",
        "Serbian",
        "Sesotho",
        "Shona",
        "Sindhi",
        "Sinhala (Sinhalese)",
        "Slovak",
        "Slovenian",
        "Somali",
        "Spanish",
        "Sundanese",
        "Swahili",
        "Swedish",
        "Tagalog (Filipino)",
        "Tajik",
        "Tamil",
        "Tatar",
        "Telugu",
        "Thai",
        "Turkish",
        "Turkmen",
        "Ukrainian",
        "Urdu",
        "Uyghur",
        "Uzbek",
        "Vietnamese",
        "Welsh",
        "Xhosa",
        "Yiddish",
        "Yoruba",
        "Zulu"
    )

    fun languageSetUp() {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in LANGUAGES_CODES_ONLINE.indices) {
                listOfLanguages.add(Lang(LANGUAGES_CODES_ONLINE[i], LANGUAGES_NAMES_ONLINE[i]))
            }
        }
    }

    fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

}