package com.cancer.yaqeen.data.features.onboarding.responses

import com.google.gson.annotations.SerializedName

data class ResourcesResponse (
    @SerializedName("CancerStages")
    val cancerStages: List<CancerStageResponse>,

    @SerializedName("CancerTypes")
    val cancerTypes: List<CancerTypeResponse>,

    @SerializedName("PatientInterests")
    val patientInterests: List<InterestResponse>,

    @SerializedName("DoctorInterests")
    val doctorInterests: List<InterestResponse>,

    @SerializedName("Photos")
    val photos: List<PhotoResponse>
)

data class CancerStageResponse (
    @SerializedName("StageId")
    val stageID: Long?,

    @SerializedName("LogoURL")
    val logoURL: String?,

    @SerializedName("Translations")
    val translations: List<CancerStageTranslationResponse>?
)

data class CancerStageTranslationResponse (
    @SerializedName("Translation")
    val translation: PurpleTranslationResponse?
)


data class PurpleTranslationResponse (
    @SerializedName("Info")
    val info: String,

    @SerializedName("StageName")
    val stageName: String?
)

data class CancerTypeResponse (
    @SerializedName("CancerId")
    val cancerID: Long?,

    @SerializedName("LogoURL")
    val logoURL: String?,

    @SerializedName("Translations")
    val translations: List<CancerTypeTranslationResponse>?
)
data class CancerTypeTranslationResponse (
    @SerializedName("Translation")
    val translation: FluffyTranslationResponse?
)


data class FluffyTranslationResponse (
    @SerializedName("Info")
    val info: String,

    @SerializedName("Name")
    val cancerTypeName: String?
)


data class InterestResponse (
    @SerializedName("InterestId")
    val interestID: Int?,

    @SerializedName("LogoURL")
    val logoURL: String?,

    @SerializedName("Translations")
    val translations: List<DoctorInterestTranslationResponse>?
)


data class DoctorInterestTranslationResponse (
    @SerializedName("Translation")
    val translation: TentacledTranslationResponse?
)


data class TentacledTranslationResponse (
    @SerializedName("Name")
    val name: String?
)
data class PhotoResponse (
    @SerializedName("PhotoURL")
    val photoURL: String?,

    @SerializedName("Usage")
    val usage: Long?
)
