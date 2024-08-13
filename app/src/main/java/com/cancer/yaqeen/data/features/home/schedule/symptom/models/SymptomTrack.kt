package com.cancer.yaqeen.data.features.home.schedule.symptom.models

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime2

data class SymptomTrack(
    var symptomTypes: List<SymptomType>? = null,
    var details: String? = null,
    var photosList: MutableList<Photo>? = arrayListOf(),
    var reminderTime2: ReminderTime2? = null,
    var startDateUI: String? = null,
    var startDateEn: String? = null,
    var doctorName: String? = null,
    val editable: Boolean = false,
    val symptomId: Int? = null,
    var destinationId: Int? = null,
)
