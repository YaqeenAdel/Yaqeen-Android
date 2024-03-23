package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models

import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom

data class MedicalReminder(
    val id: Int,
    var doctorName: String,
    val location: String,
    val phoneNumber: String,
    var startDate: String,
    var reminderTime: String,
    var reminderBeforeTime: String,
    val notes: String,
    var symptom: Symptom? = null,
)
