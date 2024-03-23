package com.cancer.yaqeen.data.features.home.schedule.medication.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    val id: Int,
    val medicationName: String,
    val medicationType: String,
    val strengthAmount: Int,
    val unitType: String,
    val dosageAmount: Int,
    val notes: String,
    val scheduleType: String,
    val scheduledTimes: List<String>,
    val scheduledTodayTime: String,
    val cronExpression: String,
//    val reminderTime: ReminderTime? = null,
): Parcelable