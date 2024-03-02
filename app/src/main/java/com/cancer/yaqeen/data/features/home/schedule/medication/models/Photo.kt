package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val id: Long,
    val uri: Uri? = null,
    val url: String? = null,
    var pathURL: String? = null,
    val imageName: String? = null
): Parcelable
