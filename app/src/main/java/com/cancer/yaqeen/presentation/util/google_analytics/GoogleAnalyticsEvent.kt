package com.cancer.yaqeen.presentation.util.google_analytics

import android.os.Build
import android.os.Bundle
import androidx.core.os.bundleOf
import com.cancer.yaqeen.BuildConfig

data class GoogleAnalyticsEvent(
    val eventName: String,
    val eventParams: Array<Pair<String, Any>> = arrayOf()
){

    fun getAllEventParams(): Bundle {
        val bundle = bundleOf(
            GoogleAnalyticsAttributes.APP_VERSION to BuildConfig.VERSION_CODE,
            GoogleAnalyticsAttributes.DEVICE_MODEL to Build.MODEL,
            GoogleAnalyticsAttributes.ACTION_DATE_TIME to System.currentTimeMillis(),
            *eventParams
        )
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
