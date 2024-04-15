package com.cancer.yaqeen.data.features.home.schedule.medication.responses

import com.google.gson.annotations.SerializedName

data class AddMedicationResponse (
    @SerializedName("insert_Schedules_one")
    val response: InsertScheduleMedicationResponse?
)

data class InsertScheduleMedicationResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: EntityMedicationResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?
)

data class EntityMedicationResponse (
    @SerializedName("Name")
    val name: String?,

    @SerializedName("Type")
    val type: String?,

    @SerializedName("Unit")
    val unit: String?,

    @SerializedName("DosageTimes100")
    val dosageTimes100: Int?,

    @SerializedName("StrengthTimes100")
    val strengthTimes100: Int?,

    @SerializedName("Notes")
    val notes: String?
)

