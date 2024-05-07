package com.cancer.yaqeen.presentation.service

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.ROUTINE_TEST
import java.util.UUID

class WorkerReminder(private val context: Context): ReminderManager() {

    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    override fun setPeriodReminder(medication: MedicationDB): String {

        val periodicWorkRequest = with(medication){
            val timeDelayInSeconds = calculateInitialDelay(startDate, hour24, minute)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
//                .setObjectKey(MEDICATION)
                .setObject(medication)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString().toString()
    }

    override fun setPeriodReminderDays(medication: MedicationDB): List<String> {
        val workIds = arrayListOf<String>()
        medication.specificDaysIds?.forEach {  id ->
            val String = setPeriodReminderSpecificDay(medication, DayEnum.getDay(id).dayId)

            workIds.add(String)
        }

        return workIds
    }


    private fun setPeriodReminderSpecificDay(medication: MedicationDB, dayId: Int): String {

        val periodicWorkRequest = with(medication){
            val timeDelayInSeconds =
                calculateInitialDelayForSpecificDay(startDate, hour24, minute, dayId)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(MEDICATION)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setObject(medication)
                .build()
        }
        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setPeriodReminder(routineTest: RoutineTestDB): Pair<String, String?> {
        val workBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        val periodicWorkRequest = with(routineTest){
            val timeDelayInSeconds = calculateInitialDelay(startDate, hour24, minute)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.routine_test_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(ROUTINE_TEST)
                .setActionName(Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString() to workBeforeID
    }

    override fun scheduleReminder(routineTest: RoutineTestDB): String {
        val (hour24, minute) = if (routineTest.minute >= routineTest.reminderBeforeInMinutes)
            (routineTest.hour24) to (routineTest.minute - routineTest.reminderBeforeInMinutes)
        else (routineTest.hour24 - 1) to (routineTest.minute - routineTest.reminderBeforeInMinutes + 60)


        val periodicWorkRequest = with(routineTest){
            val timeDelayInSeconds =
                calculateInitialDelay(
                    startDate,
                    hour24,
                    minute
                )
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.routine_test_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(ROUTINE_TEST)
                .setActionName(Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setPeriodReminderDays(routineTest: RoutineTestDB): Pair<List<String>, String?>{
        val workBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        val workIds = arrayListOf<String>()
        routineTest.specificDaysIds?.forEach {  id ->
            val String = setPeriodReminderSpecificDay(routineTest, DayEnum.getDay(id).dayId)

            workIds.add(String)
        }

        return workIds to workBeforeID
    }


    private fun setPeriodReminderSpecificDay(routineTest: RoutineTestDB, dayId: Int): String {

        val periodicWorkRequest = with(routineTest){
            val timeDelayInSeconds =
                calculateInitialDelayForSpecificDay(startDate, hour24, minute, dayId)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
                .setPeriodTime(periodTimeId)
//                .setTitle(context.getString(R.string.medication_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(MEDICATION)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }
        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setPeriodReminder(medicalAppointment: MedicalAppointmentDB): Pair<String, String?> {
        val workBeforeID = if (medicalAppointment.reminderBeforeInMinutes > 0) {
            scheduleReminder(medicalAppointment.apply { reminderBeforeIsAvailable = true })
        }else{ null }

        val workRequest = with(medicalAppointment){
            val timeDelayInSeconds =
                calculateInitialDelay(
                    startDate,
                    hour24,
                    minute
                )
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
//                .setTitle(context.getString(R.string.medical_appointment_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(MEDICAL_APPOINTMENT)
                .setActionName(Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setObject(medicalAppointment)
                .buildOneTimeWork()
        }

        enqueueWork(workRequest)

        return workRequest.id.toString() to workBeforeID
    }

    override fun scheduleReminder(medicalAppointment: MedicalAppointmentDB): String {
        val (hour24, minute) = if (medicalAppointment.minute >= medicalAppointment.reminderBeforeInMinutes)
            (medicalAppointment.hour24) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes)
        else (medicalAppointment.hour24 - 1) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes + 60)

        val workRequest = with(medicalAppointment){
            val timeDelayInSeconds =
                calculateInitialDelay(
                    startDate,
                    hour24,
                    minute
                )
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInSeconds)
//                .setTitle(context.getString(R.string.medical_appointment_reminder))
//                .setBody(context.getString(R.string.reminder_text_message))
//                .setObjectKey(MEDICAL_APPOINTMENT)
                .setActionName(Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setObject(medicalAppointment)
                .buildOneTimeWork()
        }

        enqueueWork(workRequest)

        return workRequest.id.toString()
    }

    override fun cancelReminder(workRequestId: String) {
        workManager.cancelWorkById(
            UUID.fromString(workRequestId)
        )
    }

    override fun cancelAllReminders() {
        workManager.cancelAllWork()
    }

    private fun enqueueWork(workRequest: WorkRequest) {
        workManager.enqueue(workRequest)
    }
}