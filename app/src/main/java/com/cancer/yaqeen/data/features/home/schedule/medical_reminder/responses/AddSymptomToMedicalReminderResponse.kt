package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses

import com.google.gson.annotations.SerializedName

data class AddSymptomToMedicalReminderResponse(
    @SerializedName("insert_ScheduleSymptom_one")
    val insertScheduleSymptomOne: SymptomToMedicalReminderResponse?
)

data class SymptomToMedicalReminderResponse (
    @SerializedName("SchedulesScheduleId")
    val schedulesScheduleID: Int?,

    @SerializedName("SymptomId")
    val symptomID: Int?
)