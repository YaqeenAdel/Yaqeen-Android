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

    @SerializedName("SymptomLookups")
    val symptomLookups: List<SymptomLookupElementResponse>?,

    @SerializedName("PhotoLink")
    val photoLink: String?,

    @SerializedName("DownloadPhotoLink")
    val downloadPhotoLink: DownloadPhotoLinkResponse?,

    @SerializedName("DownloadPhotoLinks")
    val downloadPhotoLinks: DownloadPhotoLinksResponse?
)

data class DownloadPhotoLinkResponse (
    val url: String?
)

data class DownloadPhotoLinksResponse (
    val urls: List<SignedUrlResponse>?
)

data class SignedUrlResponse (
    val path: String?,

    @SerializedName("signed_url")
    val url: String?
)
data class SymptomLookupElementResponse (
    @SerializedName("Details")
    val details: SymptomLookupDetailsResponse?
)
data class SymptomLookupDetailsResponse (
    @SerializedName("Translations")
    val translations: List<SymptomLookupTranslationResponse>?,

    @SerializedName("SymptomLookupId")
    val symptomLookupID: Int?
)

