package com.cancer.yaqeen.presentation.util

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.util.Locale
import android.content.res.Configuration

class MyContextWrapper(val base: Context): ContextWrapper(base) {

    @Suppress("deprecation")
    fun wrap(language: String): ContextWrapper {
        var context = base
        val config: Configuration = context.resources.configuration
        val sysLocale: Locale? = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            getSystemLocale(config)
        } else {
            getSystemLocaleLegacy(config)
        }
        if (language != "" && sysLocale!!.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale)
            } else {
                setSystemLocaleLegacy(config, locale)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context = context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
        return MyContextWrapper(context)
    }

    @Suppress("deprecation")
    private fun getSystemLocaleLegacy(config: Configuration): Locale? {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getSystemLocale(config: Configuration): Locale? {
        return config.locales.get(0)
    }

    @Suppress("deprecation")
    private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun setSystemLocale(config: Configuration, locale: Locale?) {
        config.setLocale(locale)
    }
}