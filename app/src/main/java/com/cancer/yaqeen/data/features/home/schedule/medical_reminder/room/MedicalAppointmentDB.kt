package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "MedicalAppointment")
data class MedicalAppointmentDB(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val medicalAppointmentId: Int? = null,
    var doctorName: String,
    val location: String,
    val phoneNumber: String,
    val notes: String,
    val scheduleType: String,
    val startDate: Long,
    val hour24: Int,
    val minute: Int,
    val reminderBeforeInMinutes: Int,
    var workID: UUID? = null,
    var workBeforeID: UUID? = null
): Parcelable
