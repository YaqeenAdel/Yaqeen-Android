package com.cancer.yaqeen.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.BUNDLE_DATA
import com.cancer.yaqeen.presentation.util.Constants.IGNORE_NOTIFICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.NOTIFICATION_ID
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.Constants.TITLE_KEY
import com.cancer.yaqeen.presentation.util.NotificationUtils
import com.cancer.yaqeen.presentation.util.windows.MedicalAppointmentWindow
import com.cancer.yaqeen.presentation.util.windows.MedicationWindow
import com.cancer.yaqeen.presentation.util.windows.RoutineTestWindow
import com.cancer.yaqeen.presentation.util.windows.Window


class NotificationReceiver : BroadcastReceiver() {

    private lateinit var notificationUtils: NotificationUtils
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the custom action here
        when (intent.action) {
            IGNORE_NOTIFICATION_ACTION -> {
                val notificationId = intent.getIntExtra(NOTIFICATION_ID, 1)
                notificationUtils = NotificationUtils(context)
                notificationUtils.cancelNotification(notificationId)
            }
            OPEN_MEDICATION_WINDOW_ACTION -> {
                val title = intent.getStringExtra(TITLE_KEY).toString()
                val text = intent.getStringExtra(BODY_KEY).toString()
                val bundle: Bundle? = intent.getBundleExtra(BUNDLE_DATA)
                val medication: MedicationDB? = bundle?.getParcelable(MEDICATION)

                val window = MedicationWindow(context)
                window.setMedication(medication)
                window.open()
            }
            OPEN_ROUTINE_TEST_WINDOW_ACTION -> {
                val title = intent.getStringExtra(TITLE_KEY).toString()
                val text = intent.getStringExtra(BODY_KEY).toString()
                val bundle: Bundle? = intent.getBundleExtra(BUNDLE_DATA)
                val routineTest: RoutineTestDB? = bundle?.getParcelable(ROUTINE_TEST)

                val window = RoutineTestWindow(context)
                window.setRoutineTest(routineTest)
                window.open()
            }
            OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION -> {
                val title = intent.getStringExtra(TITLE_KEY).toString()
                val text = intent.getStringExtra(BODY_KEY).toString()
                val bundle: Bundle? = intent.getBundleExtra(BUNDLE_DATA)
                val medicalAppointment: MedicalAppointmentDB? = bundle?.getParcelable(MEDICAL_APPOINTMENT)

                val window = MedicalAppointmentWindow(context)
                window.setMedicalAppointment(medicalAppointment)
                window.open()
            }
        }
    }
}