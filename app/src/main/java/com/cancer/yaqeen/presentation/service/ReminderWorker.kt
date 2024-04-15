package com.cancer.yaqeen.presentation.service

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.fromJson
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.BUNDLE_DATA
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.Constants.TITLE_KEY
import com.cancer.yaqeen.presentation.util.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : Worker(context, params) {

    private val notificationUtils: NotificationUtils = NotificationUtils(context)

    override fun doWork(): Result {
        notificationUtils.notify(inputData)

        val title = inputData.getString(TITLE_KEY).toString()
        val text = inputData.getString(BODY_KEY).toString()
        val medication =
            inputData.getString(MEDICATION).toString().fromJson(MedicationDB::class.java)
        val routineTest =
            inputData.getString(ROUTINE_TEST).toString().fromJson(RoutineTestDB::class.java)
        val medicalAppointment = inputData.getString(MEDICAL_APPOINTMENT).toString()
            .fromJson(MedicalAppointmentDB::class.java)

        val (actionName, bundle) = if (medication != null) OPEN_MEDICATION_WINDOW_ACTION to bundleOf(MEDICATION to medication)
        else if (routineTest != null) OPEN_ROUTINE_TEST_WINDOW_ACTION to bundleOf(ROUTINE_TEST to routineTest)
        else if (medicalAppointment != null) OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION to bundleOf(MEDICAL_APPOINTMENT to medicalAppointment)
        else null to null

        actionName?.let {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = actionName
                putExtra(TITLE_KEY, title)
                putExtra(BODY_KEY, text)
                putExtra(BUNDLE_DATA, bundle)

            }

            applicationContext.sendBroadcast(intent)
        }


        return Result.success()
    }
}