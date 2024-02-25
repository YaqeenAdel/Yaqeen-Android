package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.net.Uri

data class Photo(
    val id: Int,
    val uri: Uri? = null,
    val url: String? = null,
    val imageName: String
)
