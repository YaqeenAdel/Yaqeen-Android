package com.cancer.yaqeen.presentation.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.Constants.TITLE_KEY
import com.cancer.yaqeen.presentation.util.convertMillisecondsToDateComponents
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit

class WorkerManager(
    private val context: Context
) : IWorkerManager {

    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    override fun setScheduleMedication(medication: MedicationDB): UUID {
        val timeDelayInSeconds =
            calculateInitialDelay(medication.startDate, medication.hour24, medication.minute)

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.medication_reminder),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    MEDICATION to medication.toJson()
                )
            )
            .build()

        enqueueWork(workRequest)

        return workRequest.id
    }

    override fun setPeriodScheduleForMedication(medication: MedicationDB): UUID {
        val timeDelayInSeconds =
            calculateInitialDelay(medication.startDate, medication.hour24, medication.minute)

        val periodicWorkRequest = buildPeriodicWorkRequestBuilder(medication.periodTimeId)
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.medication_reminder),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    MEDICATION to medication.toJson()
                )
            ).build()

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id
    }

    override fun setPeriodScheduleForRoutineTest(routineTest: RoutineTestDB): Pair<UUID, UUID?> {
        val workBeforeID = if (routineTest.reminderBeforeInMinutes > 0) {
            scheduleReminderForRoutineTest(routineTest)
        }else{ null }

        val timeDelayInSeconds =
            calculateInitialDelay(routineTest.startDate, routineTest.hour24, routineTest.minute)

        val periodicWorkRequest = buildPeriodicWorkRequestBuilder(routineTest.periodTimeId)
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.routine_test_reminder),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    ROUTINE_TEST to routineTest.toJson()
                )
            ).build()

        enqueueWork(periodicWorkRequest)

        return periodicWorkRequest.id to workBeforeID
    }

    override fun setPeriodScheduleForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Pair<UUID, UUID?> {
        val workBeforeID = if (medicalAppointment.reminderBeforeInMinutes > 0) {
            scheduleReminderForMedicalAppointment(medicalAppointment)
        }else{ null }

        val timeDelayInSeconds =
            calculateInitialDelay(
                medicalAppointment.startDate,
                medicalAppointment.hour24,
                medicalAppointment.minute
            )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.medical_appointment),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    MEDICAL_APPOINTMENT to medicalAppointment.toJson()
                )
            )
            .build()

        enqueueWork(workRequest)

        return workRequest.id to workBeforeID
    }

    override fun scheduleReminderForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): UUID {
        val (hour24, minute) = if (medicalAppointment.minute >= medicalAppointment.reminderBeforeInMinutes)
            (medicalAppointment.hour24) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes)
        else (medicalAppointment.hour24 - 1) to (medicalAppointment.minute - medicalAppointment.reminderBeforeInMinutes + 60)
        val timeDelayInSeconds =
            calculateInitialDelay(
                medicalAppointment.startDate,
                hour24,
                minute
            )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.medical_appointment_reminder),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    MEDICAL_APPOINTMENT to medicalAppointment.toJson()
                )
            )
            .build()

        enqueueWork(workRequest)

        return workRequest.id
    }

    override fun scheduleReminderForRoutineTest(routineTest: RoutineTestDB): UUID {
        val (hour24, minute) = if (routineTest.minute >= routineTest.reminderBeforeInMinutes)
            (routineTest.hour24) to (routineTest.minute - routineTest.reminderBeforeInMinutes)
        else (routineTest.hour24 - 1) to (routineTest.minute - routineTest.reminderBeforeInMinutes + 60)
        val timeDelayInSeconds =
            calculateInitialDelay(
                routineTest.startDate,
                hour24,
                minute
            )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    TITLE_KEY to context.getString(R.string.routine_test_reminder),
                    BODY_KEY to context.getString(R.string.reminder_text_message),
                    ROUTINE_TEST to routineTest.toJson()
                )
            )
            .build()

        enqueueWork(workRequest)

        return workRequest.id
    }

    override fun cancelWork(workRequestId: UUID) {
        workManager.cancelWorkById(workRequestId)
    }

    private fun buildPeriodicWorkRequestBuilder(periodTimeId: Int?): PeriodicWorkRequest.Builder =
        when (periodTimeId) {
            PeriodTimeEnum.EVERY_DAY.id -> {
                initPeriodicWorkEveryXDays(1)
            }

            PeriodTimeEnum.EVERY_8_HOURS.id -> {
                initPeriodicWorkEveryXHours(8)
            }

            PeriodTimeEnum.EVERY_12_HOURS.id -> {
                initPeriodicWorkEveryXHours(12)
            }

            PeriodTimeEnum.DAY_AFTER_DAY.id -> {
                initPeriodicWorkEveryXDays(2)
            }

            PeriodTimeEnum.EVERY_WEEK.id -> {
                initPeriodicWorkEveryXDays(7)
            }

            PeriodTimeEnum.EVERY_MONTH.id -> {
                initPeriodicWorkEveryXDays(30)
            }

            else -> {
                initPeriodicWorkEveryXDays(1)
            }
        }

    private fun initPeriodicWorkEveryXDays(repeatInterval: Long): PeriodicWorkRequest.Builder =
        PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval,
            TimeUnit.DAYS
        )

    private fun initPeriodicWorkEveryXHours(repeatInterval: Long): PeriodicWorkRequest.Builder =
        PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval,
            TimeUnit.HOURS
        )


    private fun enqueueWork(workRequest: WorkRequest) {
        workManager.enqueue(workRequest)
    }

    private fun calculateInitialDelay(startDate: Long, hour24: Int, minute: Int): Long {
        Log.d("TAG", "calculateInitialDelay: $startDate / $hour24 / $minute")
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

            Log.d("TAG", "calculateInitialDelay: $e")
            0L
        }

    }
}