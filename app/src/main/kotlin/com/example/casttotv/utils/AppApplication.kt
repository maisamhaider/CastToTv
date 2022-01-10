package com.example.casttotv.utils

import android.app.Application
import com.example.casttotv.database.AppDatabase
import com.example.casttotv.utils.MySingleton.changeTheme
import com.example.casttotv.utils.MySingleton.languageSetUp
import com.example.casttotv.utils.MySingleton.localeLanguage
import com.example.casttotv.utils.MySingleton.setAppLocale
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs

class AppApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        changeTheme()
        languageSetUp()
        localeLanguage = getPrefs(LOCALE_LANGUAGE, "en")
        val firstRun = getPrefs(FIRST_RUN, true)
        if (firstRun) {
            putPrefs(SELECTED_ENGINE, "google")
            putPrefs(FIRST_RUN, false)
        }
        setAppLocale(localeLanguage)
    }


}