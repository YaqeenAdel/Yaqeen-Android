package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import android.net.Uri
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo

data class SymptomTrack(
    var symptomTypes: List<SymptomType>? = null,
    var details: String? = null,
//    var imageUrlList: MutableList<String>? = null,
//    var imageDownloadUrlList: MutableList<String>? = null,
    var photosList: MutableList<Photo>? = arrayListOf(),
    var reminderTime: String? = null,
    var startDate: String? = null,
    var doctorName: String? = null,
    val editable: Boolean = false,
    val symptomId: Int? = null
)
