package com.cancer.yaqeen.data.features.home.schedule.medication.responses

import com.google.gson.annotations.SerializedName

data class EditMedicationResponse (
    @SerializedName("update_Schedules_by_pk")
    val response: InsertScheduleMedicationResponse?
)


