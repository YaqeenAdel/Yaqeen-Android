package com.cancer.yaqeen.presentation.util

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.cancer.yaqeen.R
import com.google.android.material.textfield.TextInputLayout

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

fun View.enable(color: Int = R.color.purple_700){
    isEnabled = true
    backgroundTintList =
        ContextCompat.getColorStateList(context, color)
}

fun View.disable(color: Int = R.color.silver_medal){
    isEnabled = false
    backgroundTintList =
        ContextCompat.getColorStateList(context, color)
}

fun NavController.tryNavigate(directions: NavDirections) =
    try {
        navigate(directions)
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