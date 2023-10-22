package com.cancer.yaqeen.presentation.base

import android.app.Application
import android.content.Context
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.presentation.util.MyContextWrapper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YaqeenApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseAnalytics.getInstance(applicationContext)
    }
}