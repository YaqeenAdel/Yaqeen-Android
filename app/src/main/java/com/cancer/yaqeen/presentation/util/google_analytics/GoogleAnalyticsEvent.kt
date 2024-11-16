package com.cancer.yaqeen.presentation.util.google_analytics

import android.os.Build
import android.os.Bundle
import androidx.core.os.bundleOf
import com.cancer.yaqeen.BuildConfig
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate

data class GoogleAnalyticsEvent(
    val eventName: String,
    val eventParams: Array<Pair<String, Any>> = arrayOf()
){

    fun getAllEventParams(isLoggedIn: Boolean, userId: String?, userName: String?): Bundle {
        val bundle = if (isLoggedIn){
            bundleOf(
                GoogleAnalyticsAttributes.APP_VERSION to BuildConfig.VERSION_CODE,
                GoogleAnalyticsAttributes.DEVICE_MODEL to Build.MODEL,
                GoogleAnalyticsAttributes.ACTION_DATE_TIME to convertMilliSecondsToDate(System.currentTimeMillis()),
                GoogleAnalyticsAttributes.LOGGED_IN to "True",
                GoogleAnalyticsAttributes.USER_ID to userId.toString(),
                GoogleAnalyticsAttributes.USER_NAME to userName.toString(),
                *eventParams
            )
        }else {
            bundleOf(
                GoogleAnalyticsAttributes.APP_VERSION to BuildConfig.VERSION_CODE,
                GoogleAnalyticsAttributes.DEVICE_MODEL to Build.MODEL,
                GoogleAnalyticsAttributes.ACTION_DATE_TIME to convertMilliSecondsToDate(System.currentTimeMillis()),
                GoogleAnalyticsAttributes.LOGGED_IN to "False",
                *eventParams
            )
        }
        return bundle
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GoogleAnalyticsEvent

        if (eventName != other.eventName) return false
        if (!eventParams.contentEquals(other.eventParams)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventName.hashCode()
        result = 31 * result + eventParams.contentHashCode()
        return result
    }
}
