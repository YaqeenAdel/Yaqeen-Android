package com.cancer.yaqeen.data.features.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReminderTime(
    val hour: String,
    val minute: String,
    val timing: String,
    val text: String,
): Parcelable
