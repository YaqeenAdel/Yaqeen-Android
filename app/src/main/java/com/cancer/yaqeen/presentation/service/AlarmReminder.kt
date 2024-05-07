package com.cancer.yaqeen.presentation.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION


class AlarmReminder(private val context: Context): ReminderManager() {

    override fun setPeriodReminder(medication: MedicationDB): String {
        val uuid = with(medication) {
            val timeDelayInSeconds = calculateStartDateTime(startDate, hour24, minute)
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun setPeriodReminderDays(medication: MedicationDB): List<String> {
        val workIds = arrayListOf<String>()
        medication.specificDaysIds?.forEach {  id ->
            val uuid = setPeriodReminderSpecificDay(medication, DayEnum.getDay(id).dayId)

            workIds.add(uuid)
        }

        return workIds
    }

    private fun setPeriodReminderSpecificDay(medication: MedicationDB, dayId: Int): String {
        val uuid = with(medication) {
            val timeDelayInSeconds = calculateStartDateTimeForSpecificDay(startDate, hour24, minute, dayId)
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun setPeriodReminder(routineTest: RoutineTestDB): Pair<String, String?> {
        val uuid = with(routineTest){
            val timeDelayInSeconds = calculateStartDateTime(startDate, hour24, minute)
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.routine_test_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.ROUTINE_TEST)
                .setJson(json)
                .build()
        }

        val reminderBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        return uuid to reminderBeforeID
    }

    override fun scheduleReminder(routineTest: RoutineTestDB): String {
        val (hour24, minute) = if (routineTest.minute >= routineTest.reminderBeforeInMinutes)
            (routineTest.hour24) to (routineTest.minute - routineTest.reminderBeforeInMinutes)
        else (routineTest.hour24 - 1) to (routineTest.minute - routineTest.reminderBeforeInMinutes + 60)


        val uuid = with(routineTest){
            val timeDelayInSeconds =
                calculateStartDateTime(
                    startDate,
                    hour24,
                    minute
                )
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.routine_test_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.ROUTINE_TEST)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun setPeriodReminderDays(routineTest: RoutineTestDB): Pair<List<String>, String?> {
        val reminderBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        val workIds = arrayListOf<String>()
        routineTest.specificDaysIds?.forEach {  id ->
            val uuid = setPeriodReminderSpecificDay(routineTest, DayEnum.getDay(id).dayId)

            workIds.add(uuid)
        }

        return workIds to reminderBeforeID
    }

    private fun setPeriodReminderSpecificDay(routineTest: RoutineTestDB, dayId: Int): String {
        val uuid = with(routineTest) {
            val timeDelayInSeconds = calculateStartDateTimeForSpecificDay(startDate, hour24, minute, dayId)
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICATION)
                .setJson(json)
                .build()
        }

        return uuid
    }

    override fun setPeriodReminder(medicalAppointment: MedicalAppointmentDB): Pair<String, String?> {
        val uuid = with(medicalAppointment){
            val timeDelayInSeconds =
                calculateStartDateTime(
                    startDate,
                    hour24,
                    minute
                )
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
//                .setTitle(context.getString(R.string.medical_appointment_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICAL_APPOINTMENT)
                .setJson(json)
                .buildOneTime()
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
            val timeDelayInSeconds =
                calculateStartDateTime(
                    startDate,
                    hour24,
                    minute
                )
            ReminderRequest.Builder(context)
                .setStartDateTime(timeDelayInSeconds)
//                .setTitle(context.getString(R.string.medical_appointment_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setRequestCode(System.currentTimeMillis().toInt())
                .setObjectKey(Constants.MEDICAL_APPOINTMENT)
                .setJson(json)
                .buildOneTime()
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

