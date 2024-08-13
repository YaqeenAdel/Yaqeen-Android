package com.cancer.yaqeen.data.features.home.schedule.routine_test.responses

import com.google.gson.annotations.SerializedName

data class EditRoutineTestResponse (
    @SerializedName("update_Schedules_by_pk")
    val response: InsertRoutineTestResponse?
)

