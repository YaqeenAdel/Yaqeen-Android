package com.cancer.yaqeen.presentation.util

import android.content.Context
import android.util.DisplayMetrics
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


fun dpToPx(dp: Float, context: Context): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun languageIsEnglish() =
    Locale.getDefault().language.equals("en")


fun String.formatTime12(): String {

    // Create a SimpleDateFormat object with the desired format
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h.mm a", Locale.getDefault())

    // Parse the input time string into a Date object
    val date = inputFormat.parse(this)

    // Format the date using the output format
    return outputFormat.format(date)

}