package com.example.casttotv.utils

import android.R
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
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