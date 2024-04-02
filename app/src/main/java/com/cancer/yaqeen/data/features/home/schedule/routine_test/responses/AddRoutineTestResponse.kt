package com.cancer.yaqeen.data.features.home.schedule.routine_test.responses

import com.google.gson.annotations.SerializedName

data class AddRoutineTestResponse(
    @SerializedName("insert_Schedules_one")
    val response: InsertRoutineTestResponse?
)

data class InsertRoutineTestResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: EntityRoutineTestResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?
)

data class EntityRoutineTestResponse (
    @SerializedName("Name")
    val name: String?,

    @SerializedName("Notes")
    val notes: String?,

    @SerializedName("NotifyBeforeMinutes")
    val notifyBeforeMinutes: Int?
)
