package com.cancer.yaqeen.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.fromJson
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.service.WorkerReminder
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.IGNORE_NOTIFICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.NOTIFICATION_ID
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_MEDICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_ROUTINE_TEST_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.NotificationUtils
import com.cancer.yaqeen.presentation.util.scheduleJobServicePeriodically
import com.cancer.yaqeen.presentation.util.windows.MedicalAppointmentWindow
import com.cancer.yaqeen.presentation.util.windows.MedicationWindow
import com.cancer.yaqeen.presentation.util.windows.RoutineTestWindow
import java.util.concurrent.TimeUnit


class NotificationReceiver : BroadcastReceiver() {

    private lateinit var notificationUtils: NotificationUtils
    private lateinit var reminder: ReminderManager

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "onReceive: ${intent.action}")
        notificationUtils = NotificationUtils(context)
        // Handle the custom action here
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                scheduleJobServicePeriodically(context, TimeUnit.MINUTES.toMillis(15), PersistableBundle().apply {
                    putString(Constants.ACTION_KEY, UPDATE_LOCAL_SCHEDULES_ACTION)
                })
            }
            IGNORE_NOTIFICATION_ACTION -> {
                val notificationId = intent.getIntExtra(NOTIFICATION_ID, 1)
                notificationUtils.cancelNotification(notificationId)
            }
            OPEN_MEDICATION_WINDOW_ACTION -> {
                reminder = WorkerReminder(context)
                val title = context.getString(R.string.medication_reminder)
                val medication: MedicationDB? = intent.data.toString().fromJson(MedicationDB::class.java)

                val detailsMedication: String = medication?.createNotificationMessage(context).toString()
                val medicationId = medication?.medicationId ?: 1


                reminder.setReminder(
                    TimeUnit.MINUTES.toMillis(1),
                    medication,
                    UPDATE_LOCAL_MEDICATION_ACTION
                )

                notificationUtils.notify(title, detailsMedication, medicationId)

                val window = MedicationWindow(context)
                window.setMedication(medication)
                window.open()
            }
            OPEN_ROUTINE_TEST_WINDOW_ACTION -> {
                reminder = WorkerReminder(context)
                val title = context.getString(R.string.routine_test_reminder)
                val routineTest: RoutineTestDB? = intent.data.toString().fromJson(RoutineTestDB::class.java)

                val detailsRoutineTest: String = routineTest?.createNotificationMessage(context).toString()
                val medicationId = routineTest?.routineTestId ?: 1

                Log.d("reminderBefore", "NotificationReceiver: $routineTest")

                reminder.setReminder(
                    TimeUnit.MINUTES.toMillis(1),
                    routineTest,
                    UPDATE_LOCAL_ROUTINE_TEST_ACTION
                )

                notificationUtils.notify(title, detailsRoutineTest, medicationId)

                val window = RoutineTestWindow(context)
                window.setRoutineTest(routineTest)
                window.open()
            }
            OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION -> {
                reminder = WorkerReminder(context)
                val title = context.getString(R.string.routine_test_reminder)
                val routineTest: RoutineTestDB? = intent.data.toString().fromJson(RoutineTestDB::class.java)

                val detailsRoutineTest: String = routineTest?.createNotificationMessage(context).toString()
                val medicationId = routineTest?.routineTestId ?: 1

                Log.d("reminderBefore", "OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION: $routineTest")

                notificationUtils.notify(title, detailsRoutineTest, medicationId)

                val window = RoutineTestWindow(context)
                window.setRoutineTest(routineTest)
                window.open()
            }
            OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION -> {
                val title = context.getString(R.string.medical_appointment_reminder)
                val medicalAppointment: MedicalAppointmentDB? = intent.data.toString().fromJson(MedicalAppointmentDB::class.java)

                val detailsMedication: String = medicalAppointment?.createNotificationMessage(context).toString()
                val medicationId = medicalAppointment?.medicalAppointmentId ?: 1

                notificationUtils.notify(title, detailsMedication, medicationId)

                val window = MedicalAppointmentWindow(context)
                window.setMedicalAppointment(medicalAppointment)
                window.open()
            }
        }
    }


}