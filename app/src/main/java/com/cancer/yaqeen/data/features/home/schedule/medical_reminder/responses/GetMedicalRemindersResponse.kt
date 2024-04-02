package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses

import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DownloadPhotoLinksResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomResponse
import com.google.gson.annotations.SerializedName

data class GetMedicalRemindersResponse(
    @SerializedName("Schedules")
    val schedules: List<MedicalReminderResponse>?
)
data class MedicalReminderResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: MedicalReminderEntityResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?,

    @SerializedName("StartDate")
    val startDate: String?,

    @SerializedName("DownloadPhotoLinks")
    val downloadPhotoLinks: DownloadPhotoLinksResponse?,

    @SerializedName("ScheduleSymptoms")
    val scheduleSymptoms: List<ScheduleSymptomResponse>?
)

data class MedicalReminderEntityResponse (
    @SerializedName("Notes")
    val notes: String?,

    @SerializedName("Location")
    val location: String?,

    @SerializedName("Physician")
    val physician: String?,

    @SerializedName("PhoneNumber")
    val phoneNumber: String?,

    @SerializedName("NotifyBeforeMinutes")
    val notifyBeforeMinutes: Int?
)
data class ScheduleSymptomResponse (
    @SerializedName("SymptomId")
    val symptomID: Int?,

    @SerializedName("Symptom")
    val symptom: SymptomResponse?
)