package com.example.casttotv.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.casttotv.models.FileModel
import java.io.File


var folder_path = ""
const val default = "default"
const val VIDEO = "VIDEO"
const val AUDIO = "AUDIO"
const val IMAGE = "IMAGE"
var playingFileModel = FileModel("", 0, "")
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

    fun Context.toastLong(messages: String) {
        Toast.makeText(this, messages, Toast.LENGTH_LONG).show()
    }

    fun Context.toastShort(messages: String) {
        Toast.makeText(this, messages, Toast.LENGTH_SHORT).show()
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


}