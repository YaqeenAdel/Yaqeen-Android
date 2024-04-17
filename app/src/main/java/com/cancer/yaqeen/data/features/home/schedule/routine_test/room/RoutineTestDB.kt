package com.cancer.yaqeen.data.features.home.schedule.routine_test.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "RoutineTest")
data class RoutineTestDB(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val routineTestId: Int? = null,
    val routineTestName: String,
    val notes: String,
    val scheduleType: String,
    val cronExpression: String? = null,
    val startDate: Long,
    val hour24: Int,
    val minute: Int,
    val isAM: Boolean,
    val time: String,
    val periodTimeId: Int?,
    val reminderBeforeInMinutes: Int,
    var workID: UUID? = null,
    var workBeforeID: UUID? = null
): Parcelable
