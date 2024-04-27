package com.cancer.yaqeen.presentation.service

import android.os.Build
import android.util.Log
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.isSameDay
import com.cancer.yaqeen.presentation.util.convertMillisecondsToDateComponents
import com.cancer.yaqeen.presentation.util.getDateTime
import com.cancer.yaqeen.presentation.util.getDateWithSpecificDay
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar
import java.util.TimeZone

abstract class ReminderManager {
    abstract fun setPeriodReminder(medication: MedicationDB): String
    abstract fun setPeriodReminderDays(medication: MedicationDB): List<String>
    abstract fun setPeriodReminder(routineTest: RoutineTestDB): Pair<String, String?>
    abstract fun scheduleReminder(routineTest: RoutineTestDB): String
    abstract fun setPeriodReminder(medicalAppointment: MedicalAppointmentDB): Pair<String, String?>
    abstract fun scheduleReminder(medicalAppointment: MedicalAppointmentDB): String
    open fun cancelReminder(workRequestId: String) {}
    open fun cancelReminder(reminderId: String, actionName: String, objectJsonKey: String, objectJsonValue: String){}
    abstract fun cancelAllReminders()


    protected fun calculateStartDateTime(startDate: Long, hour24: Int, minute: Int): Long {
        val (year, month, day) = startDate.convertMillisecondsToDateComponents()
        return getDateTime(year, month, day, hour24, minute).timeInMillis
    }


    protected fun calculateStartDateTimeForSpecificDay(startDate: Long, hour24: Int, minute: Int, dayId: Int): Long {
        return try {

            var date = getDateWithSpecificDay(startDate, dayId)

            val calendar = Calendar.getInstance(TimeZone.getDefault())
            val hourOfDay24 = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            calendar.timeInMillis = startDate

            if (isSameDay(date.time, calendar.time)) {
                if (hourOfDay24 >= hour24 && minutes >= minute) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    date = getDateWithSpecificDay(calendar.timeInMillis, dayId)
                }
            }

            val (year, month, day) = date.timeInMillis.convertMillisecondsToDateComponents()

            getDateTime(year, month, day, hour24, minute).timeInMillis

        } catch (e: Exception) {
            0L
        }
    }

    protected fun calculateInitialDelay(startDate: Long, hour24: Int, minute: Int): Long {
        return try {
            val (year, month, day) = startDate.convertMillisecondsToDateComponents()

            val milliSecondsBetween = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val scheduleDateTime = LocalDateTime.of(year, month, day, hour24, minute, 0)
                val currentDateTime = LocalDateTime.now()
                val duration = Duration.between(currentDateTime, scheduleDateTime)

                duration.toMillis()
            } else {
                val currentDateTime = Calendar.getInstance(TimeZone.getDefault())
                val currentTimeInMillis = currentDateTime.time.time
                currentDateTime.set(year, month - 1, day, hour24, minute, 0)
                val difference = currentDateTime.time.time - currentTimeInMillis

                difference
            }

            milliSecondsBetween + 500L
        } catch (e: Exception) {
            0L
        }

    }

    protected fun calculateInitialDelayForSpecificDay(startDate: Long, hour24: Int, minute: Int, dayId: Int): Long {
        return try {

            var date = getDateWithSpecificDay(startDate, dayId)

            val calendar = Calendar.getInstance(TimeZone.getDefault())
            val hourOfDay24 = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            calendar.timeInMillis = startDate

            if (isSameDay(date.time, calendar.time)){
                if(hourOfDay24 >= hour24 && minutes >= minute){
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    date = getDateWithSpecificDay(calendar.timeInMillis, dayId)
                }
            }

            val (year, month, day) = date.timeInMillis.convertMillisecondsToDateComponents()

            Log.d("TAG", "calculateInitialDelayForSpecificDay: $year / $month / $day")

            val milliSecondsBetween = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val scheduleDateTime = LocalDateTime.of(year, month, day, hour24, minute, 0)
                val currentDateTime = LocalDateTime.now()
                val duration = Duration.between(currentDateTime, scheduleDateTime)

                duration.toMillis()
            } else {
                val currentDateTime = Calendar.getInstance(TimeZone.getDefault())
                val currentTimeInMillis = currentDateTime.time.time
                currentDateTime.set(year, month - 1, day, hour24, minute, 0)
                val difference = currentDateTime.time.time - currentTimeInMillis

                difference
            }

            milliSecondsBetween + 500L
        } catch (e: Exception) {
            0L
        }

    }
}