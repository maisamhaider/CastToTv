package com.example.casttotv.utils

import android.content.Context
import android.content.SharedPreferences

const val NO_COUNTRY = "NO_COUNTRY"
const val COUNTRY = "COUNTRY"
const val SHOW_SPLASH_LAYOUT = "SHOW_SPLASH_LAYOUT"
const val SHOW_PERMISSION_LAYOUT = "SHOW_PERMISSION_LAYOUT"
private const val My_PREF = "My_PREF"

//browser settings keys
const val SELECTED_ENGINE = "SELECTED_ENGINE"
const val FAVORITE_DEFAULT_SITE = "FAVORITE_DEFAULT_SITE"
const val CUSTOM_SEARCH_ENGINE = "CUSTOM_SEARCH_ENGINE"
const val CUSTOM_USER_AGENT = "CUSTOM_USER_AGENT"


const val DATA_CLEAR_CACHE = "DATA_CLEAR_CACHE"
const val DATA_CLEAR_HISTORY = "DATA_CLEAR_HISTORY"
const val DATA_CLEAR_INDEXED_LOCAL_DATABASE = "DATA_CLEAR_INDEXED_LOCAL_DATABASE"
const val DATA_CLEAR_COOKIES = "DATA_CLEAR_COOKIES"
const val DATA_CLEAR_APPLICATION_EXIT = "DATA_CLEAR_APPLICATION_EXIT"


const val START_CONTROL_IMAGE = "START_CONTROL_IMAGE"
const val START_CONTROL_SAVE_DATA = "START_CONTROL_SAVE_DATA"
const val START_CONTROL_HISTORY = "START_CONTROL_HISTORY"
const val START_CONTROL_LOCATION = "START_CONTROL_LOCATION"
const val START_CONTROL_AD_BLOCK = "START_CONTROL_AD_BLOCK"
const val START_CONTROL_JAVASCRIPT = "START_CONTROL_JAVASCRIPT"
const val START_CONTROL_COOKIES = "START_CONTROL_COOKIES"

const val BEHAVIOR_UI_CONFIRM_BROWSER_EXIT = "BEHAVIOR_UI_CONFIRM_BROWSER_EXIT"
const val BEHAVIOR_UI_CONFIRM_TAB_CLOSE = "BEHAVIOR_UI_CONFIRM_TAB_CLOSE"
const val BEHAVIOR_UI_REOPEN_LAST_TAB = "BEHAVIOR_UI_REOPEN_LAST_TAB"
const val BEHAVIOR_UI_HIDE_TOOLBAR = "BEHAVIOR_UI_HIDE_TOOLBAR"
const val BEHAVIOR_UI_FIRST_TOOLBAR = "BEHAVIOR_UI_FIRST_TOOLBAR"
const val BEHAVIOR_UI_POSITION = "BEHAVIOR_UI_POSITION"
const val BEHAVIOR_UI_SHOW_ON_START = "BEHAVIOR_UI_SHOW_ON_START"

const val LOCALE_LANGUAGE = "LOCALE_LANGUAGE"



object Pref {

    private lateinit var sharePreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
      fun Context.initPref() {
        sharePreferences = getSharedPreferences(My_PREF, Context.MODE_PRIVATE)
        editor = sharePreferences.edit()
    }

    fun Context.putPrefs(key: String, value: String): Boolean {
        initPref()
        return editor.putString(key, value).commit()
    }

    fun Context.getPrefs(key: String, defValue: String): String {
        initPref()
        return sharePreferences.getString(key, defValue).toString()
    }

    fun Context.putPrefs(key: String, value: Boolean): Boolean {
        initPref()
        return editor.putBoolean(key, value).commit()
    }

    fun Context.getPrefs(key: String, defValue: Boolean): Boolean {
        initPref()
        return sharePreferences.getBoolean(key, defValue)
    }

}