package com.cancer.yaqeen.data.features.home.schedule.symptom.requests

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.utils.formatDateUIToDateAPI
import com.cancer.yaqeen.data.utils.formatEnglishDateToDateAPI
import com.google.gson.annotations.SerializedName

data class AddSymptomRequest(
    val symptom: ScheduleSymptomRequest
)
data class ScheduleSymptomRequest (
    @SerializedName("Details")
    val details: String,

    @SerializedName("SymptomLookupIds")
    val symptomLookupIds: List<Int>,

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
    val startDate: String?,
    val time: String?,
    var photos: List<Photo>,
) {
    fun buildRequestBody(): AddSymptomRequest =
        AddSymptomRequest(
            symptom = ScheduleSymptomRequest(
                details = details,
                symptomLookupIds = symptomLookupIds,
                notes = doctorName,
                photoLink = photos.map { it.pathURL }.joinToString(","),
                time = "${formatEnglishDateToDateAPI(startDate)} $time"
            )
        )
}
