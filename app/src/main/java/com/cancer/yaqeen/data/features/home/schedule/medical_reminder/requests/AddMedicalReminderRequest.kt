package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests

import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.utils.formatDateAPI
import com.cancer.yaqeen.data.utils.formatTimeAPI
import com.google.gson.annotations.SerializedName

data class AddMedicalReminderRequest(
    val schedule: ScheduleAppointmentRequest
)
data class ScheduleAppointmentRequest (
    @SerializedName("EntityType")
    val entityType: String,

    @SerializedName("Entity")
    val entity: EntityAppointmentRequest,

    @SerializedName("StartDate")
    val startDate: String
)

data class EntityAppointmentRequest (
    @SerializedName("Physician")
    val physician: String,

    @SerializedName("Location")
    val location: String,

    @SerializedName("PhoneNumber")
    val phoneNumber: String,

    @SerializedName("Notes")
    val notes: String,

    @SerializedName("NotifyBeforeMinutes")
    val notifyBeforeMinutes: Int
)


data class AddMedicalReminderRequestBuilder(
    val doctorName: String,
    val location: String,
    val phoneNumber: String,
    val startDate: String?,
    val time: String?,
    val notifyBeforeMinutes: Int,
    val notes: String
) {
    fun buildRequestBody(): AddMedicalReminderRequest =
        AddMedicalReminderRequest(
            schedule = ScheduleAppointmentRequest(
                entityType = ScheduleType.MEDICAL_REMINDER.scheduleType,
                entity = EntityAppointmentRequest(
                    physician = doctorName,
                    location = location,
                    phoneNumber = phoneNumber,
                    notes = notes,
                    notifyBeforeMinutes = notifyBeforeMinutes
                ),
                startDate = "${startDate?.formatDateAPI() ?: ""} ${time ?: ""}"
            )
        )
}
