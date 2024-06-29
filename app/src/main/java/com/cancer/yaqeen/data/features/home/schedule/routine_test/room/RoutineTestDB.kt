package com.cancer.yaqeen.data.features.home.schedule.routine_test.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cancer.yaqeen.data.features.home.schedule.medication.room.ReminderStatus
import com.cancer.yaqeen.data.utils.convertMillisecondsToTime
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
@Entity(tableName = "RoutineTest")
data class RoutineTestDB(
    @PrimaryKey
    val routineTestId: Int = 0,
    val routineTestName: String,
    val notes: String,
    val scheduleType: String,
    val cronExpression: String? = null,
    var startDateTime: Long,
//    val startDate: Long,
//    val hour24: Int,
//    val minute: Int,
//    val isAM: Boolean,
//    var time: String,
    val periodTimeId: Int?,
    val reminderBeforeInMinutes: Int,
    var reminderBeforeIsAvailable: Boolean = false,
    val specificDaysIds: List<Int> = listOf(),
    var workID: String? = null,
    var workBeforeID: String? = null,
    var workSpecificDaysIDs: List<String> = listOf(),
    var json: String? = null,
    var isReminded: Boolean = false,
    var statusReminder: ReminderStatus = ReminderStatus.NEW
): Parcelable{

    fun createNotificationMessage(): String{
        val date = convertMilliSecondsToDate(startDateTime)
        var timeMillis = getCurrentTimeMillis()
        if (reminderBeforeIsAvailable){
            timeMillis += TimeUnit.MINUTES.toMillis(reminderBeforeInMinutes.toLong())
        }
        val time = convertMillisecondsToTime(timeMillis)
        return "Hello! Just a friendly reminder to test: $routineTestName at $date $time as prescribed today. Your health is important, so let's stay on track together. \uD83D\uDE0A"
    }

}
