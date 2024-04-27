package com.cancer.yaqeen.presentation.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.OBJECT_JSON
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

    override fun doWork(): Result {

        val medication = inputData.getString(MEDICATION).toString()
        val routineTest = inputData.getString(ROUTINE_TEST).toString()
        val medicalAppointment = inputData.getString(MEDICAL_APPOINTMENT).toString()


        val (actionName, objectJsonValue) = if (medication.isNotEmpty()) OPEN_MEDICATION_WINDOW_ACTION to medication
        else if (routineTest.isNotEmpty()) OPEN_ROUTINE_TEST_WINDOW_ACTION to routineTest
        else if (medicalAppointment.isNotEmpty()) OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION to medicalAppointment
        else null to null

        actionName?.let {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = actionName
                data = Uri.parse(objectJsonValue)
            }

            applicationContext.sendBroadcast(intent)
        }


        return Result.success()
    }
}