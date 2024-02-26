package com.cancer.yaqeen.data.features.home.schedule.symptom.requests

import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.utils.tryToLong
import com.google.gson.annotations.SerializedName

data class AddSymptomRequest(
    val symptom: ScheduleSymptomRequest
)
data class ScheduleSymptomRequest (
    @SerializedName("Details")
    val details: String,

    @SerializedName("SymptomLookupId")
    val symptomLookupId: Int,

    @SerializedName("Notes")
    val notes: String,

    @SerializedName("PhotoLink")
    var photoLink: String,

    @SerializedName("Time")
    var time: String
)


data class AddSymptomRequestBuilder(
    val details: String,
    val symptomLookupIds: List<Int>,
    val doctorName: String,
    val dateTime: String,
    val photoLinks: List<String>,
) {
    fun buildRequestBody(): AddSymptomRequest =
        AddSymptomRequest(
            symptom = ScheduleSymptomRequest(
                details = details,
                symptomLookupId = symptomLookupIds.first(),
                notes = doctorName,
                photoLink = photoLinks.first(),
                time = dateTime
            )
        )
}
