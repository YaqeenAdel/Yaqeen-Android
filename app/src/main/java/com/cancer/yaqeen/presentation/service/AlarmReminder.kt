package com.cancer.yaqeen.presentation.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
import java.util.concurrent.TimeUnit


class AlarmReminder(private val context: Context): ReminderManager() {
    override fun setReminder(medication: MedicationDB, oneTime: Boolean): String {
        val uuid = with(medication) {
            ReminderRequest.Builder(context)
                .setStartDateTime(startDateTime)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .setReminderType(oneTime)
                .build()
        }

        return uuid
    }

    override fun setReminderDays(medication: MedicationDB, oneTime: Boolean): List<String> {
        val workIds = arrayListOf<String>()
        medication.specificDaysIds?.forEach {  id ->
            val uuid = setReminderSpecificDay(medication, DayEnum.getDay(id).dayId, oneTime)

            workIds.add(uuid)
        }

        return workIds
    }

    private fun setReminderSpecificDay(medication: MedicationDB, dayId: Int, oneTime: Boolean): String {
        val uuid = with(medication) {
            val timeDelayInMilliSeconds = calculateStartDateTimeForSpecificDay(startDateTime, dayId)
            Log.d("AlarmSpecificDay", "setReminderSpecificDay: $timeDelayInMilliSeconds")
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .setReminderType(oneTime)
                .build()
        }

        return uuid
    }

    override fun setReminder(routineTest: RoutineTestDB, oneTime: Boolean): Pair<String, String?> {
        val uuid = with(routineTest){
            ReminderRequest.Builder(context)
                .setStartDateTime(startDateTime)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.ROUTINE_TEST)
                .setJson(json)
                .setReminderType(oneTime)
                .build()
        }
        Log.d("reminderBefore", "setReminder: $routineTest")

        val reminderBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        return uuid to reminderBeforeID
    }

    override fun scheduleReminder(routineTest: RoutineTestDB): String {
        val uuid = with(routineTest){
            val timeDelayInMilliSeconds =
                startDateTime - TimeUnit.MINUTES.toMillis(routineTest.reminderBeforeInMinutes.toLong())
            Log.d("reminderBefore", "scheduleReminder: $routineTest")
            Log.d("reminderBefore", "scheduleReminder: $timeDelayInMilliSeconds")

            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.ROUTINE_TEST)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun setReminderDays(routineTest: RoutineTestDB, oneTime: Boolean): Pair<List<String>, String?> {
        val reminderBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        val workIds = arrayListOf<String>()
        routineTest.specificDaysIds?.forEach {  id ->
            val uuid = setReminderSpecificDay(routineTest, DayEnum.getDay(id).dayId, oneTime)

            workIds.add(uuid)
        }

        return workIds to reminderBeforeID
    }

    private fun setReminderSpecificDay(routineTest: RoutineTestDB, dayId: Int, oneTime: Boolean): String {
        val uuid = with(routineTest) {
            val timeDelayInMilliSeconds = calculateStartDateTimeForSpecificDay(startDateTime, dayId)
            Log.d("AlarmSpecificDay", "setReminderSpecificDay, routineTest: $timeDelayInMilliSeconds")
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .setReminderType(oneTime)
                .build()
        }

        return uuid
    }

    override fun setReminder(medicalAppointment: MedicalAppointmentDB, oneTime: Boolean): Pair<String, String?> {
        val uuid = with(medicalAppointment){
            val timeDelayInMilliSeconds =
                calculateStartDateTime(
                    startDate,
                    hour24,
                    minute
                )
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInMilliSeconds)
                .setActionName(OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICAL_APPOINTMENT)
                .setJson(json)
                .setReminderType(oneTime)
                .build()
        }

        val reminderBeforeID = if (medicalAppointment.reminderBeforeInMinutes > 0) {
            scheduleReminder(medicalAppointment.apply { reminderBeforeIsAvailable = true })
        }else{ null }

        return uuid to reminderBeforeID
    }

    override fun scheduleReminder(medicalAppointment: MedicalAppointmentDB): String {
        val (hour24, minute) = if (medicalAppointment.minute >= medicalAppointment.reminderBeforeInMinutes)
            (medicalAppointment.hour24) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes)
        else (medicalAppointment.hour24 - 1) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes + 60)

        val uuid = with(medicalAppointment){
            val timeDelayInMilliSeconds =
                calculateStartDateTime(
                    startDate,
                    hour24,
                    minute
                )
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInMilliSeconds)
//                .setTitle(context.getString(R.string.medical_appointment_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICAL_APPOINTMENT)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun cancelReminder(reminderId: String, actionName: String, objectJsonValue: String) {

        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val requestCode = reminderId.toIntOrNull() ?: 0
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val data = Uri.parse(objectJsonValue)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = actionName
//            this.flags = Intent.FLAG_RECEIVER_FOREGROUND
            this.data = data
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)


        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun cancelAllReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= 34) {
            alarmManager.cancelAll()
        }
    }
}

