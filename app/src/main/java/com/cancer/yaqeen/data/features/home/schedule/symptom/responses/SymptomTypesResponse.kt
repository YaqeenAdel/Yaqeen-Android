package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class SymptomTypesResponse (
    @SerializedName("SymptomLookups")
    val symptomLookups: List<SymptomLookupResponse>?
)

data class SymptomLookupResponse (
    @SerializedName("SymptomLookupId")
    val symptomLookupID: Int?,

    @SerializedName("Translations")
    val translations: List<SymptomLookupTranslationResponse>?
)

data class SymptomLookupTranslationResponse (
    @SerializedName("Language")
    val language: String?,

    @SerializedName("Translation")
    val translation: SymptomTranslationResponse?
)

data class SymptomTranslationResponse (
    @SerializedName("Name")
    val name: String?
)