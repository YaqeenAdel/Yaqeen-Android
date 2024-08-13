package com.cancer.yaqeen.data.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val DATE_PATTERN_API = "yyyy-MM-dd'T'HH:mm:ss"
const val DATE_PATTERN_API_2 = "yyyy-MM-dd"
const val DATE_PATTERN_UI = "dd MMM yyyy"
const val DATE_PATTERN_UI_2 = "dd/MM/yyyy"
const val TIME_PATTERN_UI = "hh:mm a"
const val TIME_PATTERN_UI_2 = "HH:mm"
const val HOUR_24_FORMAT_PATTERN = "HH"
val localDateRemote: Locale = Locale.ENGLISH
val localDateUI: Locale = Locale.getDefault()


fun formatDateTime(dateTime: String?, inputPattern: String, outputPattern: String, inputLocale: Locale, outputLocale: Locale): String{
    return try {
        if (dateTime.isNullOrEmpty())
            ""
        val inputDateFormat = SimpleDateFormat(inputPattern, inputLocale)
        val outputDateFormat = SimpleDateFormat(outputPattern, outputLocale)

        val inputDate = inputDateFormat.parse(dateTime)
        outputDateFormat.format(inputDate)
    }
    catch (_: Exception) {
        ""
    }
}

fun formatDateTimeAPIToDateUI(dateTime: String?, inputPattern: String = DATE_PATTERN_API, outputPattern: String = DATE_PATTERN_UI_2, locale: Locale = localDateUI): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = localDateRemote,
        outputLocale = locale
    )
}

fun formatDateTimeAPIToTimeUI(dateTime: String?, inputPattern: String = DATE_PATTERN_API, outputPattern: String = TIME_PATTERN_UI, locale: Locale = localDateUI): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = localDateRemote,
        outputLocale = locale
    )
}

fun formatDateUIToDateAPI(dateTime: String?, inputPattern: String = DATE_PATTERN_UI_2, outputPattern: String = DATE_PATTERN_API_2, locale: Locale = localDateUI): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = locale,
        outputLocale = localDateRemote
    )
}

fun formatEnglishTimeUIToTimeUI(dateTime: String, inputPattern: String = TIME_PATTERN_UI, outputPattern: String = TIME_PATTERN_UI): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = localDateRemote,
        outputLocale = localDateUI,
    )
}

fun convertEnglishTimeToHour24UI(dateTime: String, inputPattern: String = TIME_PATTERN_UI, outputPattern: String = HOUR_24_FORMAT_PATTERN): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = localDateRemote,
        outputLocale = localDateUI
    )
}

fun convertDateTimeAPIToHour24UI(dateTime: String?, inputPattern: String = DATE_PATTERN_API, outputPattern: String = HOUR_24_FORMAT_PATTERN): String {
    return formatDateTime(
        dateTime = dateTime,
        inputPattern = inputPattern,
        outputPattern = outputPattern,
        inputLocale = localDateRemote,
        outputLocale = localDateUI
    )
}

fun formatEnglishDateToDateAPI(dateTime: String?): String {
    return formatDateUIToDateAPI(dateTime = dateTime, locale = localDateRemote)
}

fun convertDateTimeToMillis(dateTime: String?, inputPattern: String = DATE_PATTERN_API): Long {
    return try {
        if (dateTime.isNullOrEmpty())
            0L
        val inputDateFormat = SimpleDateFormat(inputPattern, localDateUI)

        val inputDate = inputDateFormat.parse(dateTime)
        inputDate.time
    } catch (_: Exception) {
        0L
    }
}

fun convertMillisecondsToTime(milliseconds: Long, pattern: String = TIME_PATTERN_UI): String {
    return try {
        val timestamp = Date(milliseconds)

        val dateFormat = SimpleDateFormat(pattern, localDateUI)
        dateFormat.format(timestamp)
    }catch (e: Exception){
        ""
    }

}

fun Long.toDate(): Date {
    return Date(this)
}

fun isCurrentTodayAndAfterTimeNow(dateTime: String?, inputPattern: String = DATE_PATTERN_API): Boolean {
    return try {
        if (dateTime.isNullOrEmpty())
            false
        val currentTimestamp = System.currentTimeMillis()
        val currentDate = currentTimestamp.toDate()

        val inputDateFormat = SimpleDateFormat(inputPattern, localDateUI)
        val inputDate = inputDateFormat.parse(dateTime)

        if (isSameDay(currentDate, inputDate)) {
            val date = Calendar.getInstance()
            date.time = inputDate

            date.timeInMillis >= currentTimestamp
        } else {
            false
        }
    }catch (e: Exception){
        false
    }

}

fun isCurrentToday(dateTime: String?, inputPattern: String = DATE_PATTERN_API): Boolean {
    return try {
        if (dateTime.isNullOrEmpty())
            false
        val currentTimestamp = System.currentTimeMillis()
        val currentDate = currentTimestamp.toDate()

        val inputDateFormat = SimpleDateFormat(inputPattern, localDateUI)
        val inputDate = inputDateFormat.parse(dateTime)

        isSameDay(currentDate, inputDate)
    }catch (e: Exception){
        false
    }
}

fun isSameDay(dateFirst: Date, dateSecond: Date): Boolean {
    return try {
        val calFirst = Calendar.getInstance()
        calFirst.time = dateFirst
        val calSecond = Calendar.getInstance()
        calSecond.time = dateSecond

        calFirst.get(Calendar.YEAR) == calSecond.get(Calendar.YEAR) &&
                calFirst.get(Calendar.DAY_OF_YEAR) == calSecond.get(Calendar.DAY_OF_YEAR)
    }catch (e: Exception){
        false
    }
}

fun isAfterNow(dateTime: String?, inputPattern: String = DATE_PATTERN_API): Boolean {
    return try {
        if (dateTime.isNullOrEmpty())
            false
        val currentTimestamp = System.currentTimeMillis()

        val inputDateFormat = SimpleDateFormat(inputPattern, localDateUI)
        val inputDate = inputDateFormat.parse(dateTime)

        val date = Calendar.getInstance()
        date.time = inputDate

        currentTimestamp >= date.timeInMillis
    }catch (e: Exception){
        false
    }
}


fun formatDate(dateTime: Long, pattern: String = DATE_PATTERN_UI, locale: Locale = localDateUI): String {
    return try {
        val inputDate = dateTime.toDate()
        val outputDateFormat = SimpleDateFormat(pattern, locale)
        outputDateFormat.format(inputDate)
    }
    catch (_: Exception) {
        ""
    }
}

fun getTodayDate(): String {
    return try {
        val currentTimestamp = System.currentTimeMillis()

        formatDate(currentTimestamp)
    }catch (e: Exception){
        ""
    }
}

