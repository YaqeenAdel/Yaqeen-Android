package com.cancer.yaqeen.presentation.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import com.cancer.yaqeen.data.features.onboarding.models.Language
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt


fun dpToPx(dp: Float, context: Context): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun languageIsEnglish() =
    Locale.getDefault().language.equals(Language.ENGLISH.lang)


fun String.formatTime12(): String {

    // Create a SimpleDateFormat object with the desired format
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h.mm a", Locale.getDefault())

    // Parse the input time string into a Date object
    val date = inputFormat.parse(this)

    // Format the date using the output format
    return outputFormat.format(date)

}


fun updateConfiguration(localeCode: String? = null, resources: Resources, context: Context){
    val contextWrapper: Context =
        MyContextWrapper(context).wrap(localeCode ?: Locale.getDefault().language)
    resources.updateConfiguration(
        contextWrapper.resources.configuration,
        contextWrapper.resources.displayMetrics
    )
}
fun overrideLocale(localeCode: String? = null, resources: Resources) {
    val local = Locale(
        localeCode ?: Locale.getDefault().language
    )
    Locale.setDefault(local)
    val newConfig = Configuration()
    newConfig.setLocale(local)
    resources.updateConfiguration(newConfig, resources.displayMetrics)
}
fun convertMilliSecondsToDate(milliseconds: Long, pattern: String = "dd/MM/yyyy"): String {
    val timestamp = Date(milliseconds)

    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(timestamp)
}
