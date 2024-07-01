package com.cancer.yaqeen.presentation.util

import android.content.res.Resources
import com.cancer.yaqeen.R
import java.util.Calendar

fun calculateStartDateTime(startDate: Long, hour24: Int, minute: Int): Long {
    val (year, month, day) = startDate.convertMillisecondsToDateComponents()
    return getDateTime(year, month, day, hour24, minute).timeInMillis
}

fun getWelcomeMessage(resources: Resources): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when {
        hour < 12 -> resources.getString(R.string.good_morning)
        hour < 18 -> resources.getString(R.string.good_afternoon)
        else -> resources.getString(R.string.good_evening)
    }
}