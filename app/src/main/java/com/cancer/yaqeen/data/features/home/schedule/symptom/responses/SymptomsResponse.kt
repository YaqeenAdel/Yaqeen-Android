package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class SymptomsResponse(
    @SerializedName("Symptoms")
    val symptoms: List<SymptomResponse>?
)

data class SymptomResponse (
    @SerializedName("SymptomId")
    val symptomId: Int?,

    @SerializedName("Details")
    val details: String?,

    @SerializedName("Notes")
    val notes: String?,

    @SerializedName("Time")
    val time: String?,

    @SerializedName("SymptomLookupId")
    val symptomLookupID: Int?,

    @SerializedName("UpdatedAt")
    val updatedAt: String?,

    @SerializedName("SymptomLookup")
    val symptomLookup: SymptomLookupResponse,

    @SerializedName("PhotoLink")
    val photoLink: String?,

    @SerializedName("DownloadPhotoLink")
    val downloadPhotoLink: DownloadPhotoLinkResponse?
)

data class DownloadPhotoLinkResponse (
    val url: String?
)

