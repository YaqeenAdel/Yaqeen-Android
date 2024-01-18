package com.cancer.yaqeen.data.features.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Interest(
    val interestID: Int,
    val interestName: String,
    val backgroundColor: String,
    val textColor: String,
): Parcelable
