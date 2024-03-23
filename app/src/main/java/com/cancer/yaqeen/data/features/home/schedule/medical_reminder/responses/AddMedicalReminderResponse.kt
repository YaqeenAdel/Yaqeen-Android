package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses

import com.google.gson.annotations.SerializedName

data class AddMedicalReminderResponse (
    @SerializedName("insert_Schedules_one")
    val insertSchedulesOne: InsertMedicalReminderResponse?
)
data class InsertMedicalReminderResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: MedicalReminderEntityResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?
)
