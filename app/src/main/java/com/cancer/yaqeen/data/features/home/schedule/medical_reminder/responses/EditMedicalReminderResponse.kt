package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses

import com.google.gson.annotations.SerializedName

data class EditMedicalReminderResponse (
    @SerializedName("update_Schedules_by_pk")
    val updateSchedulesOne: InsertMedicalReminderResponse?
)

