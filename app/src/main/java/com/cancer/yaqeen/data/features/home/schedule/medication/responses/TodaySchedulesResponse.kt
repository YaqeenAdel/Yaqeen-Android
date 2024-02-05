package com.cancer.yaqeen.data.features.home.schedule.medication.responses

import com.google.gson.annotations.SerializedName

data class TodaySchedulesResponse(
    @SerializedName("Schedules")
    val schedules: List<TodayScheduleResponse>?
)
data class TodayScheduleResponse (
    @SerializedName("ScheduledEvents")
    val scheduledEvents: ScheduledEventsResponse,

    @SerializedName("Entity")
    val entity: EntityResponse?,

    @SerializedName("ScheduleId")
    val scheduleID: Int?
)

data class ScheduledEventsResponse(
    @SerializedName("events")
    val events: List<ScheduledEventResponse>?
)

data class ScheduledEventResponse(
    @SerializedName("scheduled_time")
    val scheduledTime: String
)

