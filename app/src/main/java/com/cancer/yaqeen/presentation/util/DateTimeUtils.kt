package com.cancer.yaqeen.presentation.util

fun calculateStartDateTime(startDate: Long, hour24: Int, minute: Int): Long {
    val (year, month, day) = startDate.convertMillisecondsToDateComponents()
    return getDateTime(year, month, day, hour24, minute).timeInMillis
}