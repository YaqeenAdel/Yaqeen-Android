package com.cancer.yaqeen.data.features.home.schedule.routine_test.models

import android.os.Parcelable
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoutineTest(
    val id: Int,
    val routineTestName: String,
//    val notifyBeforeMinutes: Int,
    val reminderBefore: ReminderBefore,
    val notes: String,
    val photo: Photo?,
    val scheduleType: String,
    val cronExpression: String,
    val reminderTime: ReminderTime?,
    val startDate: Long,
): Parcelable
