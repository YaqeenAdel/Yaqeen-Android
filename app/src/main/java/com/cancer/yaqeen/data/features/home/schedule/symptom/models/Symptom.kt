package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import android.net.Uri
import android.os.Parcelable
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Symptom(
    val id: Int,
    var symptomTypes: List<SymptomType>? = null,
    var imageUrl: String? = null,
    var imageDownloadUrl: String? = null,
    var details: String? = null,
    var reminderTime: String? = null,
    var startDate: String? = null,
    var doctorName: String? = null
): Parcelable
