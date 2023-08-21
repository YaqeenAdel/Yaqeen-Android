package com.cancer.yaqeen.presentation.base

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YaqeenApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(applicationContext)
    }
}