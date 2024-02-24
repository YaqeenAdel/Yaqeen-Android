package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SymptomType(
    val id: Int,
    val name: String,
    var selected: Boolean = false
): Parcelable
