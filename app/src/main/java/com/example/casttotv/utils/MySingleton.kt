package com.example.casttotv.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.casttotv.models.FileModel

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