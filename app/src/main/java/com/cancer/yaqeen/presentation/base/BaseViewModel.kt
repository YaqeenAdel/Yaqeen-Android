package com.cancer.yaqeen.presentation.base

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.LOGGED_IN
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SELECTED_LANGUAGE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.USER_ID
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.USER_NAME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


open class BaseViewModel(
    val context: Context,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
) : ViewModel() {

    //adb shell setprop debug.firebase.analytics.app PACKAGE_NAME
    //adb shell setprop debug.firebase.analytics.app com.cancer.yaqeen

    fun logEvent(googleAnalyticsEvent: GoogleAnalyticsEvent) {
        with(googleAnalyticsEvent) {
            val user = prefEncryptionUtil.getModelData(PREF_USER, User::class.java)
            val params = getAllEventParams(prefEncryptionUtil.isLogged, user?.id, user?.name)
            logEvent(eventName, params)
        }

    }

    private fun logEvent(eventName: String, eventParams: Bundle) {
        viewModelScope.launch {
            FirebaseAnalytics.getInstance(context)
                .logEvent(
                    eventName,
                    eventParams
                )
        }
    }
}