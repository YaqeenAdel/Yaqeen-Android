package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class EditSymptomResponse (
    @SerializedName("update_Symptoms_by_pk")
    val response: InsertScheduleSymptomResponse?
)


