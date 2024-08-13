package com.cancer.yaqeen.data.features.home.schedule.routine_test.requests

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.google.gson.annotations.SerializedName

data class AddRoutineTestRequest(
    val schedule: ScheduleRoutineTestRequest
)
data class ScheduleRoutineTestRequest (
    @SerializedName("EntityType")
    val entityType: String,

    @SerializedName("Entity")
    val entity: EntityRoutineTestRequest,

    @SerializedName("PhotoLink")
    val photoLink: String,

    @SerializedName("CronExpression")
    val cronExpression: String
)

data class EntityRoutineTestRequest (
    @SerializedName("Name")
    val name: String,

    @SerializedName("Notes")
    val notes: String,

    @SerializedName("NotifyBeforeMinutes")
    val notifyBeforeMinutes: Int
)

data class AddRoutineTestRequestBuilder(
    val routineTestName: String,
    val notifyBeforeMinutes: Int,
    var photo: Photo?= null,
    val cronExpression: String,
    val notes: String
) {
    fun buildRequestBody(): AddRoutineTestRequest =
        AddRoutineTestRequest(
            schedule = ScheduleRoutineTestRequest(
                entityType = ScheduleType.ROUTINE_TESTS.scheduleType,
                entity = EntityRoutineTestRequest(
                    name = routineTestName,
                    notifyBeforeMinutes = notifyBeforeMinutes,
                    notes = notes
                ),
                photoLink = photo?.pathURL ?: "",
                cronExpression = cronExpression
            )
        )
}