package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class AddSymptomResponse (
    @SerializedName("insert_Symptoms_one")
    val response: InsertScheduleSymptomResponse?
)

data class InsertScheduleSymptomResponse (
    @SerializedName("SymptomId")
    val symptomId: Int?,

    @SerializedName("Details")
    val details: String?,

    @SerializedName("Notes")
    val notes: String?,

    @SerializedName("Time")
    val time: String?,

    @SerializedName("PhotoDownloadLink")
    val photoDownloadLink: PhotoDownloadLinkResponse
)
data class PhotoDownloadLinkResponse (
    val url: String
)


