package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReminderTime2(
    val timeEN: String,
    val timeUI: String,
    val hour24: String
): Parcelable
