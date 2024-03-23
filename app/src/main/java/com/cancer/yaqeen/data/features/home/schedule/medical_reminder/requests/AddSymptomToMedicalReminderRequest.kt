package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests

import com.google.gson.annotations.SerializedName

data class AddSymptomToMedicalReminderRequest(
    @SerializedName("object")
    val scheduleSymptom: ScheduleSymptomRequest
)
data class ScheduleSymptomRequest (
    @SerializedName("SchedulesScheduleId")
    val medicalReminderID: Int,

    @SerializedName("SymptomId")
    val symptomID: Int
)


data class AddSymptomToMedicalReminderRequestBuilder(
    val medicalReminderID: Int,
    val symptomID: Int
) {
    fun buildRequestBody(): AddSymptomToMedicalReminderRequest =
        AddSymptomToMedicalReminderRequest(
            scheduleSymptom = ScheduleSymptomRequest(
                medicalReminderID = medicalReminderID,
                symptomID = symptomID
            )
        )
}
