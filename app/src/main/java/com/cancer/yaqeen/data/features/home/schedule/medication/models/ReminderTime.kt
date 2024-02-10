package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReminderTime(
    val hour12: String,
    val hour24: String,
    val minute: String,
    val timing: String,
    val isAM: Boolean,
    val text: String,
): Parcelable
