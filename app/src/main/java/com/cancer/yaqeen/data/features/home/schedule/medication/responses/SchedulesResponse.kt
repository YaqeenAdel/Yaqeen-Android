package com.cancer.yaqeen.data.features.home.schedule.medication.responses

import com.google.gson.annotations.SerializedName

data class SchedulesResponse(
    @SerializedName("Schedules")
    val schedules: List<ScheduleResponse>?
)
data class ScheduleResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: EntityResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?
)

data class EntityResponse (
    @SerializedName("Name")
    val name: String?,

    @SerializedName("Type")
    val type: String?,

    @SerializedName("Unit")
    val unit: String?,

    @SerializedName("DosageTimes100")
    val dosageTimes: Int?,

    @SerializedName("StrengthTimes100")
    val strengthTimes: Int?
)

