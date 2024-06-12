package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses

import com.google.gson.annotations.SerializedName

data class DeleteSymptomFromScheduleResponse(
    @SerializedName("delete_ScheduleSymptom_by_pk")
    val response: DeleteScheduleSymptomResponse?
)
data class DeleteScheduleSymptomResponse (
    @SerializedName("SchedulesScheduleId")
    val schedulesScheduleID: Long?,

    @SerializedName("SymptomId")
    val symptomID: Long?
)
