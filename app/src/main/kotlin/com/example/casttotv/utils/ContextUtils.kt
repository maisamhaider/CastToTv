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
        val localeToSwitchTo = Locale(context.getPrefs(LOCALE_LANGUAGE, "en"))
        var cxt = context
        val resources: Resources = cxt.resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else {
            configuration.locale = localeToSwitchTo
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            cxt = context.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return ContextUtils(cxt)
    }
}