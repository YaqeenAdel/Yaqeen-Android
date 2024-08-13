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

    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 4 until 12 -> resources.getString(R.string.good_morning)
        in 12 until 18 -> resources.getString(R.string.good_afternoon)
        in 18 until 22 -> resources.getString(R.string.good_evening)
        else -> resources.getString(R.string.good_night)
    }
}