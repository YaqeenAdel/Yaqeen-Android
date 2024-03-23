package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class DeleteSymptomResponse (
    @SerializedName("delete_Symptoms_by_pk")
    val response: DeleteScheduleSymptomResponse?
)
data class DeleteScheduleSymptomResponse (
    @SerializedName("SymptomId")
    val symptomID: Long?,

    @SerializedName("SymptomLookupId")
    val symptomLookupID: Long?
)


