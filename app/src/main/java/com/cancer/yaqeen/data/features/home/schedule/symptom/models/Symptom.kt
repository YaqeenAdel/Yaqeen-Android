package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import android.os.Parcelable
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Symptom(
    val id: Int,
    var symptomTypes: List<SymptomType>? = null,
//    var pathURL: List<String>? = null,
//    var imageUrl: List<String>? = null,
    var photosList: List<Photo>? = null,
    var details: String? = null,
    var reminderTime: String? = null,
    var startDate: String? = null,
    var doctorName: String? = null
): Parcelable
