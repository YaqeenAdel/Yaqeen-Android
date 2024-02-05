package com.cancer.yaqeen.presentation.util

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun View.changeVisibility(show: Boolean, isGone: Boolean = false) =
    if (show) {
        visibility = View.VISIBLE
    } else {
        visibility = if(isGone) View.GONE else View.INVISIBLE
    }
fun TextInputLayout.disableErrorInputLayout(){
    error = null
    isErrorEnabled = false
}

fun Fragment.enableTouch() {
    this.requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}
fun Fragment.disableTouch() {
    this.requireActivity().window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun View.enable(color: Int = R.color.primary_color){
    isEnabled = true
    backgroundTintList =
        ContextCompat.getColorStateList(context, color)
}

fun View.disable(color: Int = R.color.gray){
    isEnabled = false
    backgroundTintList =
        ContextCompat.getColorStateList(context, color)
}

fun NavController.tryNavigate(directions: NavDirections) =
    try {
        navigate(directions)
    } catch (_: Exception) {}

fun NavController.tryNavigateUp() =
    try {
        navigateUp()
    } catch (_: Exception) {}


fun NavController.tryNavigate(
    resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null
) =
    try {
        navigate(resId, args, navOptions)
    } catch (_: Exception) {}


fun NavController.tryPopBackStack(
    destinationId: Int,
    inclusive: Boolean
) =
    try {
        popBackStack(destinationId, inclusive)
    } catch (_: Exception) {}


fun NavController.tryPopBackStack() =
    try {
        popBackStack()
    } catch (_: Exception) {}

fun ViewPager2.autoScroll(interval: Long) {

    val handler = Handler()
    var scrollPosition = 0

    val runnable = object : Runnable {

        override fun run() {

            /**
             * Calculate "scroll position" with
             * adapter pages count and current
             * value of scrollPosition.
             */
            val count = adapter?.itemCount ?: 0
            setCurrentItem(scrollPosition++ % count, true)

            handler.postDelayed(this, interval)
        }
    }

    registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // Updating "scroll position" when user scrolls manually
            scrollPosition = position + 1
        }

        override fun onPageScrollStateChanged(state: Int) {
            // Not necessary
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            // Not necessary
        }
    })

    this.removeCallbacks(runnable)

    handler.post(runnable)
}

fun AutoCompleteTextView.selectItem(position: Int = 0, text: String = "") {
    this.setText(text)
    this.showDropDown()
    this.listSelection = position
    this.performCompletion()
}
fun Long.formatMilliseconds(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:$seconds"
}

fun String.detectLanguage(): String {
    // Regular expression to match Arabic characters
    val arabicRegex = "\\p{InArabic}"

    // Regular expression to match English characters
    val englishRegex = "\\p{InBasicLatin}"

    // Check if the text contains Arabic or English characters
    val containsArabic = contains(Regex(arabicRegex))
    val containsEnglish = contains(Regex(englishRegex))

    // Determine the language based on the presence of Arabic or English characters
    return when {
        containsArabic && !containsEnglish -> "ar"
        containsEnglish && !containsArabic -> "en"
        else -> "unknown"
    }
}

fun Long.timestampToHour(): String {
    val dateFormat = SimpleDateFormat("h", Locale.ENGLISH)
    val date = Date(this)

    return dateFormat.format(date)
}

fun Long.timestampToDay(): String {
    val dateFormat = SimpleDateFormat("d", Locale.ENGLISH)
    val date = Date(this)

    return dateFormat.format(date)
}

fun Long.timestampToMonth(): String {
    val dateFormat = SimpleDateFormat("M", Locale.ENGLISH)
    val date = Date(this)

    return dateFormat.format(date)
}

fun Long.timestampToYear(): String {
    val dateFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)
    val date = Date(this)

    return dateFormat.format(date)
}
