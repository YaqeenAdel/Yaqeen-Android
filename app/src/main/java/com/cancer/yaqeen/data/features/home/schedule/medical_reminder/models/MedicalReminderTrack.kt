package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models

import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom

data class MedicalReminderTrack(
    var doctorName: String? = null,
    var location: String? = null,
    var phoneNumber: String? = null,
    var startDate: String? = null,
    var reminderTime: String? = null,
    var reminderBefore: ReminderBefore = ReminderBefore.MINUTES_0,
    var notes: String? = null,
    var symptom: Symptom? = null,
    val editable: Boolean = false,
    val medicalReminderId: Int? = null
)
