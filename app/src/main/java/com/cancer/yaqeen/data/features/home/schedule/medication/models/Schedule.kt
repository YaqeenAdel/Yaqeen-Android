package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.os.Parcelable
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    val id: Int,
    val medication: ScheduleMedication? = null,
    val appointment: ScheduleAppointment? = null,
    val routineTest: ScheduleRoutineTest? = null,
    val notes: String,
    val scheduleType: String,
    val scheduledTimes: List<String>,
    val scheduledTodayTime: String,
    val cronExpression: String,
//    val reminderTime: ReminderTime? = null,
): Parcelable

@Parcelize
data class ScheduleMedication(
    val medicationName: String,
    val medicationType: String,
    val strengthAmount: Int,
    val unitType: String,
    val dosageAmount: Int,
): Parcelable

@Parcelize
data class ScheduleRoutineTest(
    var routineTestName: String,
    var reminderBefore: ReminderBefore,
): Parcelable

@Parcelize
data class ScheduleAppointment(
    var doctorName: String,
    val location: String,
    val phoneNumber: String,
    var reminderBefore: ReminderBefore,
): Parcelable