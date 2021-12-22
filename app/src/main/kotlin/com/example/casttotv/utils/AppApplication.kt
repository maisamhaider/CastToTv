package com.example.casttotv.utils

import android.app.Application
import com.example.casttotv.database.AppDatabase
import com.example.casttotv.utils.MySingleton.languageSetUp
import com.example.casttotv.utils.MySingleton.localeLanguage
import com.example.casttotv.utils.MySingleton.setAppLocale
import com.example.casttotv.utils.Pref.getPrefs

class AppApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        languageSetUp()
        localeLanguage = getPrefs(LOCALE_LANGUAGE, "en")
        setAppLocale(localeLanguage)
    }


}