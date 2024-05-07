package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "MedicalAppointment")
data class MedicalAppointmentDB(
    @PrimaryKey
    val medicalAppointmentId: Int = 0,
    var doctorName: String,
    val location: String,
    val phoneNumber: String,
    val notes: String,
    val scheduleType: String,
    val startDate: Long,
    val hour24: Int,
    val minute: Int,
    val isAM: Boolean,
    val time: String,
    val reminderBeforeInMinutes: Int,
    var reminderBeforeIsAvailable: Boolean = false,
    var workID: String? = null,
    var workBeforeID: String? = null,
    var json: String? = null
): Parcelable{

    fun createNotificationMessage(): String{
        val date = convertMilliSecondsToDate(startDate)
        return "Hello! Just a friendly reminder to you have an appointment at $date $time with $doctorName in $location, note for you phone number: $phoneNumber. Your health is important, so let's stay on track together. \uD83D\uDE0A"
    }

}
