package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models

import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom

data class MedicalReminderTrack(
    var doctorName: String? = null,
    var location: String? = null,
    var phoneNumber: String? = null,
    var startDate: String? = null,
    var reminderTime: String? = null,
    var reminderBeforeTime: String? = null,
    var notes: String? = null,
    var symptom: Symptom? = null,
    val editable: Boolean = false,
    val symptomId: Int? = null
)
