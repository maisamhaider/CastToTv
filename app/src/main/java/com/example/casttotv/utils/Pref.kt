package com.example.casttotv.utils

import android.content.Context
import android.content.SharedPreferences

const val NO_COUNTRY = "NO_COUNTRY"
const val COUNTRY = "COUNTRY"
const val SHOW_SPLASH_LAYOUT = "SHOW_SPLASH_LAYOUT"
const val SHOW_PERMISSION_LAYOUT = "SHOW_PERMISSION_LAYOUT"

object Pref {

    private const val My_PREF = "My_PREF"
    private lateinit var sharePreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private fun Context.initPref() {
        sharePreferences = getSharedPreferences(My_PREF, Context.MODE_PRIVATE)
        editor = sharePreferences.edit()
    }

    fun Context.putString(key: String, value: String): Boolean {
        initPref()
        return editor.putString(key, value).commit()
    }

    fun Context.getString(key: String, defValue: String): String {
        initPref()
        return sharePreferences.getString(key, defValue).toString()
    }
    fun Context.putBoolean(key: String, value: Boolean): Boolean {
        initPref()
        return editor.putBoolean(key, value).commit()
    }

    fun Context.getBoolean(key: String, defValue: Boolean): Boolean {
        initPref()
        return sharePreferences.getBoolean(key, defValue)
    }

}