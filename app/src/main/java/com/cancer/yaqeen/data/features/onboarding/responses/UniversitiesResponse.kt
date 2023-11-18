package com.cancer.yaqeen.data.features.onboarding.responses

import com.google.gson.annotations.SerializedName

data class UniversitiesResponse(
    @SerializedName("Universities")
    val universities: List<UniversityResponse>?
)

data class UniversityResponse (
    @SerializedName("CountryName")
    val countryName: String?,

    @SerializedName("StateCode")
    val stateCode: String?,

    @SerializedName("StateName")
    val stateName: String?,

    @SerializedName("UniversityName")
    val universityName: String?,

    @SerializedName("UniversityId")
    val universityID: Long?
)
