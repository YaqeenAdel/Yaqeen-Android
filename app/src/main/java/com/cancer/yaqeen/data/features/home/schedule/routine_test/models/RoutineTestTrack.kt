package com.cancer.yaqeen.data.features.home.schedule.routine_test.models

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time

data class RoutineTestTrack(
    var routineTestName: String? = null,
    var photo: Photo? = null,
    var periodTime: Time? = null,
    var specificDays: List<Day>? = null,
    var startDate: Long? = null,
    var reminderTime: ReminderTime? = null,
    var reminderBeforeTime: String? = null,
    var notes: String? = null,
    val editable: Boolean = false,
    val medicationId: Int? = null
)
