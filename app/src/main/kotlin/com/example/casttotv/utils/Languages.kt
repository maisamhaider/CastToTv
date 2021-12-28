package com.example.casttotv.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import java.util.*


class Languages {

    fun changeLocale(context: Context, language: String) {
        if (language == "") {
            return
        }
        val locale = Locale(language)
        Locale.setDefault(locale)

        val overrideConfiguration: Configuration = context.resources.configuration
        overrideConfiguration.setLocale(locale)

        val context: Context = context.createConfigurationContext(overrideConfiguration)

    }



    fun saveLocale(context: Context, language: String) {
        context.putPrefs(LOCALE_LANGUAGE, language)
    }

    fun saveLocale(context: Context) = context.getPrefs(LOCALE_LANGUAGE, "en")

}