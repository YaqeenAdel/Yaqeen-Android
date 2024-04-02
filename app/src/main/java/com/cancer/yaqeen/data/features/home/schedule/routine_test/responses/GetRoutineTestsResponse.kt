package com.cancer.yaqeen.data.features.home.schedule.routine_test.responses

import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DownloadPhotoLinksResponse
import com.google.gson.annotations.SerializedName

data class GetRoutineTestsResponse(
    @SerializedName("Schedules")
    val schedules: List<RoutineTestResponse>?
)
data class RoutineTestResponse (
    @SerializedName("CronExpression")
    val cronExpression: String?,

    @SerializedName("Entity")
    val entity: RoutineTestEntityResponse?,

    @SerializedName("EntityType")
    val entityType: String?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?,

    @SerializedName("DownloadPhotoLinks")
    val downloadPhotoLinks: DownloadPhotoLinksResponse?
)

data class RoutineTestEntityResponse (
    @SerializedName("Name")
    val name: String?,

    @SerializedName("Notes")
    val notes: String?,

    @SerializedName("NotifyBeforeMinutes")
    val notifyBeforeMinutes: Int?
)