package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import android.net.Uri
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime

data class SymptomTrack(
    var symptomTypes: List<SymptomType>? = null,
    var details: String? = null,
    var imageUrl: String? = null,
    var imageDownloadUrl: String? = null,
    var imageUri: Uri? = null,
    var imageName: String? = null,
    var reminderTime: String? = null,
    var startDate: String? = null,
    var doctorName: String? = null,
    val editable: Boolean = false,
    val symptomId: Int? = null
)
