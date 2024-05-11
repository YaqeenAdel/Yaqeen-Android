package com.cancer.yaqeen.presentation.service

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import java.util.UUID
import java.util.concurrent.TimeUnit

class WorkerReminder(private val context: Context): ReminderManager() {

    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    override fun setReminder(medication: MedicationDB, oneTime: Boolean): String {

        val periodicWorkRequest = with(medication){
//            val timeDelayInMilliSeconds = calculateInitialDelay(startDate, hour24, minute)
            WorkerRequest.Builder()
                .setStartDateTime(startDateTime)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
//                .setObjectKey(MEDICATION)
                .setObject(medication)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setReminderDays(medication: MedicationDB, oneTime: Boolean): List<String> {
        val workIds = arrayListOf<String>()
        medication.specificDaysIds?.forEach {  id ->
            val String = setPeriodReminderSpecificDay(medication, DayEnum.getDay(id).dayId)

            workIds.add(String)
        }

        return workIds
    }


    private fun setPeriodReminderSpecificDay(medication: MedicationDB, dayId: Int): String {

        val periodicWorkRequest = with(medication){
            val timeDelayInMilliSeconds =
                calculateInitialDelayForSpecificDay(startDateTime, dayId)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setObject(medication)
                .build()
        }
        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setReminder(routineTest: RoutineTestDB, oneTime: Boolean): Pair<String, String?> {
        val workBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminder(routineTest.apply { reminderBeforeIsAvailable = true })
        }else { null }

        val periodicWorkRequest = with(routineTest){
            WorkerRequest.Builder()
                .setStartDateTime(startDateTime)
                .setPeriodTime(periodTimeId)
                .setActionName(Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString() to workBeforeID
    }

    override fun scheduleReminder(routineTest: RoutineTestDB): String {
        val periodicWorkRequest = with(routineTest){
            val timeDelayInMilliSeconds =
                startDateTime - TimeUnit.MINUTES.toMillis(routineTest.reminderBeforeInMinutes.toLong())
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setReminderDays(routineTest: RoutineTestDB, oneTime: Boolean): Pair<List<String>, String?>{
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
            val timeDelayInMilliSeconds =
                calculateInitialDelayForSpecificDay(startDateTime, dayId)
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInMilliSeconds)
                .setPeriodTime(periodTimeId)
                .setActionName(OPEN_MEDICATION_WINDOW_ACTION)
                .setObject(routineTest)
                .build()
        }
        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun setReminder(medicalAppointment: MedicalAppointmentDB, oneTime: Boolean): Pair<String, String?> {
        val workBeforeID = if (medicalAppointment.reminderBeforeInMinutes > 0) {
            scheduleReminder(medicalAppointment.apply { reminderBeforeIsAvailable = true })
        }else{ null }

        val workRequest = with(medicalAppointment){
            val timeDelayInMilliSeconds =
                calculateInitialDelay(
                    startDate,
                    hour24,
                    minute
                )
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInMilliSeconds)
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
            val timeDelayInMilliSeconds =
                calculateInitialDelay(
                    startDate,
                    hour24,
                    minute
                )
            WorkerRequest.Builder()
                .setStartDateTime(timeDelayInMilliSeconds)
                .setActionName(Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION)
                .setObject(medicalAppointment)
                .buildOneTimeWork()
        }

        enqueueWork(workRequest)

        return workRequest.id.toString()
    }

    override fun setPeriodReminder(timeDelayInMilliSeconds: Long, periodTimeId: Int, actionName: String): String {
        val periodicWorkRequest = WorkerRequest.Builder()
            .setStartDateTime(timeDelayInMilliSeconds)
            .setPeriodTime(periodTimeId)
            .setActionName(actionName)
            .build()

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
    }

    override fun<T> setReminder(timeDelayInMilliSeconds: Long, obj: T, actionName: String): String {
        val periodicWorkRequest = WorkerRequest.Builder()
            .setStartDateTime(timeDelayInMilliSeconds)
            .setActionName(actionName)
            .setObject(obj)
            .buildOneTimeWork()

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id.toString()
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