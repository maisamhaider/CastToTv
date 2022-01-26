package com.example.casttotv.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build

import android.os.LocaleList
import com.example.casttotv.utils.Pref.getPrefs
import java.util.*


class ContextUtils(base: Context) : ContextWrapper(base) {

    fun updateLocale(context: Context): ContextWrapper {
       val  localeToSwitchTo = Locale(context.getPrefs(LOCALE_LANGUAGE, "en"))
        var context = context
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(localeToSwitchTo) // 2
            LocaleList.setDefault(localeList) // 3
            configuration.setLocales(localeList) // 4
        } else {
            configuration.locale = localeToSwitchTo // 5
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context = context.createConfigurationContext(configuration) // 6
        } else {
            resources.updateConfiguration(configuration, resources.displayMetrics) // 7
        }
        return ContextUtils(context) // 8
    }
}