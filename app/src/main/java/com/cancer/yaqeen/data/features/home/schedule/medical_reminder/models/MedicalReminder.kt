package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models

import android.os.Parcelable
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalReminder(
    val id: Int,
    var doctorName: String,
    val location: String,
    val phoneNumber: String,
    var startDate: Long,
    var reminderTime: ReminderTime? = null,
    var reminderBefore: ReminderBefore,
    val notes: String,
    var symptom: Symptom? = null,
): Parcelable
